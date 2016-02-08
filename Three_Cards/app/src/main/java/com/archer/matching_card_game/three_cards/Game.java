package com.archer.matching_card_game.three_cards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.archer.matching_card_game.three_cards.StoryMode.GameValues;
import com.archer.matching_card_game.three_cards.StoryMode.PostGame;
import com.google.android.gms.ads.AdListener;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.ROBOT_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.SetEnableControls;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL;


public class Game {

    //region Game Data

    public int BoardType;
    public int RowSize, ColumnSize;
    public int CardSet;
    public boolean PlayerOne_Turn;
    public int Player1_Moves;
    public int PlayerOne_Score;
    public int PlayerTwo_Score;
    public int GameBackground;
    public int LockingTime;
    public ImageView CurrentCard;
    public GameSummary objGameSummary;
    public int CurrentModule;
    public int CurrentLevel;
    public int CurrentStage;
    public int CurrentChallenge;
    public boolean StoryMode;
    int PlayerMode;
    int GameMode;
    int ScrollType;
    int BoardIdentifier;
    int PlayerTwoType;
    int RobotMemoryLevel;
    int TimeTrialTimerValue;
    int TotalCardsOnBoard;
    int EffectiveClickCount;
    int ActualClickCount;
    int TotalMoves;
    int currentHitStreak;
    int maxHitStreak;
    boolean IsConsecutiveHit;
    int GameRunningTime;
    int[][] Cards_ImageResID;
    int Card_Clicks[][];
    int CardLastClicked[][];
    int CardRetainingPower[][];
    int NearMisses;
    SparseArray<String> MatchingCard_Map;
    SparseArray<Integer> CardAttempt_Map;
    SparseArray<Integer> Matches;
    int ReplacementCards[];
    String DestroyedCards[];
    int DestroyedCards_Top;
    int clickAdjustment_destroyedCards;
    CountDownTimer GameTimer;
    ImageView IV_AllCards[][];
    ViewGroup GameBoard;
    boolean powUsed;
    boolean isChallengeGame;
    int ChallengeReward;
    TextView TV_GameDataInfo;
    TextView tvPlayerTurn;
    TextView tvScore;
    TextView tvTime;
    TextView tvTimeTrialTimer;
    TextView tvConsecutiveWins;
    TextView tvHitStreak;
    TextView tvMoves;
    AnimationSet Flip_anim;
    View.OnClickListener CardClick_Listener;
    Animation.AnimationListener DefaultFlipListener;
    SoundPool sp;
    int CARD_MATCH_SOUND;
    int CARD_MISMATCH_SOUND;
    boolean powFind_flag;
    boolean isAnimating;

    //    endregion
    Button Btn_Power;
    Robot robotPlayer;
    TimeTrail objTimeTrail;
    Power objPower;
    MainActivity mContext;
    private Semaphore WAIT_LOCK;


    public Game(final MainActivity context) {
        mContext = context;
    }

    public void setGameConfiguration(int playerMode, int playerTwoType, int robotMemoryLevel, int gameMode,
                                     int timeTrialTimer, int boardType, int rowSize, int colSize, int scrollType,
                                     int cardSet) {
        PlayerMode = playerMode;
        if (playerMode == HelperClass.TWO_PLAYER && playerTwoType != HelperClass.MANUAL)
            PlayerMode = HelperClass.ROBOT_PLAYER;

        PlayerTwoType = playerTwoType;
        RobotMemoryLevel = robotMemoryLevel;
        GameMode = gameMode;
        TimeTrialTimerValue = timeTrialTimer;
        BoardType = boardType;
        RowSize = rowSize;
        ColumnSize = colSize;
        ScrollType = scrollType;
        CardSet = cardSet;
        TotalCardsOnBoard = rowSize * colSize;
        if (boardType == HelperClass.THREE_BOARD)
            TotalCardsOnBoard *= 3;
        TotalCardsOnBoard = TotalCardsOnBoard / 3 * 3; //Handling odd-board size

        InitializeGameTimer();
        LockingTime = getLockingTime();

        if (PlayerMode == HelperClass.ROBOT_PLAYER && robotPlayer == null) {
            robotPlayer = new Robot(new WeakReference<>(this), robotMemoryLevel, PlayerTwoType);
        }

        if (GameMode == HelperClass.TIME_TRIAL && objTimeTrail == null) {
            objTimeTrail = new TimeTrail(new WeakReference<>(this), TimeTrialTimerValue);
        }

        if (objPower == null)
            objPower = new Power(new WeakReference<>(this));

    }

    public int getFlipTimeDuration() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getInt(String.valueOf(HelperClass.FLIP_ANIMATION_TIME), 9);
    }

    public int getLockingTime() {
        if (!StoryMode) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            LockingTime = preferences.getInt(String.valueOf(HelperClass.LOCKING_TIME), 600);
        }
        return LockingTime;
    }

    public void StartGame() {
        MatchingCard_Map = new SparseArray<>();
        CardAttempt_Map = new SparseArray<>();
        Matches = new SparseArray<>();
        Flip_anim = HelperClass.FlipAnimation(getFlipTimeDuration(), 2);
        generateBoardIdentifier();
        createGame();
        InitializeSoundPool();

        WAIT_LOCK = new Semaphore(1, true);
        IsConsecutiveHit = true;
        Card_Clicks = new int[RowSize][ColumnSize];
        CardLastClicked = new int[RowSize][ColumnSize];
        CardRetainingPower = new int[RowSize][ColumnSize];
        DestroyedCards = new String[RowSize * ColumnSize];
        if (PlayerMode == HelperClass.ROBOT_PLAYER) {
            robotPlayer.resetHitProbability();
        }
    }

    private void generateBoardIdentifier() {
        BoardIdentifier = 0;
        if (BoardType == HelperClass.ONE_BOARD) {
            switch (ScrollType) {
                case HelperClass.NO_SCROLL:
                    BoardIdentifier = HelperClass.OneBoard_WithoutScroll;
                    break;
                case HelperClass.VERTICAL:
                    BoardIdentifier = HelperClass.OneBoard_VerticalScroll;
                    break;
                case HelperClass.HORIZONTAL:
                    BoardIdentifier = HelperClass.OneBoard_HorizontalScroll;
                    break;
                case HelperClass.BOTH:
                    BoardIdentifier = HelperClass.OneBoard_BothScroll;
                    break;
            }
        } else if (BoardType == HelperClass.THREE_BOARD) {
            switch (ScrollType) {
                case HelperClass.NO_SCROLL:
                    BoardIdentifier = HelperClass.ThreeBoard_WithoutScroll;
                    break;
                case HelperClass.VERTICAL:
                    BoardIdentifier = HelperClass.ThreeBoard_VerticalScroll;
                    break;
                case HelperClass.HORIZONTAL:
                    BoardIdentifier = HelperClass.ThreeBoard_HorizontalScroll;
                    break;
                case HelperClass.BOTH:
                    BoardIdentifier = HelperClass.ThreeBoard_BothScroll;
                    break;
            }
        }

    }

    public void createGame() {
        mContext.CURRENT_SCREEN = HelperClass.SCREEN_GAME;

        switch (BoardIdentifier) {
            case HelperClass.OneBoard_WithoutScroll:
                CreateOneBoardWithoutScroll_Game();
                break;
            case HelperClass.ThreeBoard_WithoutScroll:
                CreateThreeBoardWithoutScroll_Game();
                break;
            case HelperClass.OneBoard_HorizontalScroll:
                CreateOneBoardHorizontalScroll_Game();
                break;
            case HelperClass.OneBoard_VerticalScroll:
                CreateOneBoardVerticalScroll_Game();
                break;
            case HelperClass.OneBoard_BothScroll:
                CreateOneBoardBothScroll_Game();
                break;
            case HelperClass.ThreeBoard_HorizontalScroll:
                CreateThreeBoardHorizontalScroll_Game();
                break;
            case HelperClass.ThreeBoard_VerticalScroll:
                CreateThreeBoardVerticalScroll_Game();
                break;
            case HelperClass.ThreeBoard_BothScroll:
                CreateThreeBoardBothScroll_Game();
                break;
        }
        if (GameMode == HelperClass.TIME_TRIAL)
            objTimeTrail.TimeTrialTimer.start();


        mContext.displayAdsCounter++;
        if (TotalCardsOnBoard > 20 && mContext.displayAdsCounter != 2) {
            mContext.displayAdsCounter++;
            if (TotalCardsOnBoard > 30)
                mContext.displayAdsCounter = 2;
        }
        if (mContext.displayAdsCounter > 2)
            mContext.displayAdsCounter = 2;

    }

    public void resetGameData() {
        DestroyedCards_Top = ActualClickCount = EffectiveClickCount = PlayerOne_Score = PlayerTwo_Score = 0;
        maxHitStreak = currentHitStreak = 0;
        clickAdjustment_destroyedCards = GameRunningTime = 0;
        NearMisses = Player1_Moves = TotalMoves=0;
        PlayerOne_Turn = true;
        IsConsecutiveHit = true;
        powUsed = false;
        isChallengeGame = false;
        ChallengeReward = 0;

        HelperClass.clearArray(Card_Clicks, RowSize, ColumnSize);
        HelperClass.clearArray(CardLastClicked, RowSize, ColumnSize);
        HelperClass.clearArray(CardRetainingPower, RowSize, ColumnSize);
        DestroyedCards = new String[RowSize * ColumnSize];
        MatchingCard_Map = new SparseArray<>();
        CardAttempt_Map = new SparseArray<>();
        WAIT_LOCK.release();
        WAIT_LOCK = new Semaphore(1, true);

        boolean changePlayer = (PlayerTwoType == HelperClass.RANDOM_BOT);
        if (PlayerMode == HelperClass.ROBOT_PLAYER)
            robotPlayer.Clear(changePlayer);
    }

    private void InitializeGameTimer() {
        try {
            if (GameTimer == null)
                GameTimer = new CountDownTimer(10000000, 999) {
                    public void onTick(long millisUntilFinished) {
                        //Sync threads !!
                        AcquireLOCK();

                        GameRunningTime++;
                        SetGameInfoText();

                        //Sync threads !!
                        ReleaseLOCK();
                    }

                    public void onFinish() {
                    }
                };
            else
                GameTimer.cancel();
        } catch (Exception ex) {
            /* do nothing */
        }
    }

    private void CreateImageMap() {
        Cards_ImageResID = getCardSet();

        int totalCardsOnBoard = RowSize*ColumnSize;
        int count_unique_cards = totalCardsOnBoard/3;
        int temp[] = new int[totalCardsOnBoard];
        int index=0;

        for (int r = 0; r < RowSize; r++) {
            for (int c = 0; c < ColumnSize; c++) {
                temp[index++]=Cards_ImageResID[r][c];
            }
        }
        index=0;
        for (int r = 0; r < RowSize; r++) {
            for (int c = 0; c < ColumnSize; c++) {
                Cards_ImageResID[r][c] = temp[index++];
                if(index==count_unique_cards)
                    index=0;
            }
        }

        //region handle uneven board
        switch (totalCardsOnBoard%3)
        {
            case 2:
                Cards_ImageResID[RowSize-1][ColumnSize-2]=temp[count_unique_cards+1];
            case 1:
                Cards_ImageResID[RowSize-1][ColumnSize-1]=temp[count_unique_cards];
        }
        //endregion

        RandomizeImagesMatrix(Cards_ImageResID);
    }

    private void CreateImageMapForThreeBoard() {
        int[][] cards_B1 = getCardSet();
        int cards_B2[][] = new int[RowSize][ColumnSize];
        int cards_B3[][] = new int[RowSize][ColumnSize];
        for (int i = 0; i < RowSize; i++) {
            System.arraycopy(cards_B1[i], 0, cards_B2[i], 0, ColumnSize);
            System.arraycopy(cards_B1[i], 0, cards_B3[i], 0, ColumnSize);
        }
        RandomizeImagesMatrix(cards_B1);
        RandomizeImagesMatrix(cards_B2);
        RandomizeImagesMatrix(cards_B3);
        Cards_ImageResID = new int[3 * RowSize][ColumnSize];
        for (int i = 0; i < RowSize; i++) {
            for (int j = 0; j < ColumnSize; j++) {
                Cards_ImageResID[i][j] = cards_B1[i][j];
                Cards_ImageResID[i + RowSize][j] = cards_B2[i][j];
                Cards_ImageResID[i + RowSize + RowSize][j] = cards_B3[i][j];
            }
        }
    }

    private LinearLayout CreateBoardSet(int row_adjustment, int col_adjustment, LinearLayout.LayoutParams decidingLayoutParam) {
        final LinearLayout simple_game = new LinearLayout(mContext);
        int pad_px = HelperClass.ConvertToPx(mContext, 2);
        simple_game.setPadding(pad_px, pad_px, pad_px, pad_px);
        simple_game.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        simple_game.setLayoutParams(lParam);
        for (int i = 0; i < RowSize; i++) {
            LinearLayout l_col = new LinearLayout(mContext);
            simple_game.addView(l_col);
            l_col.setOrientation(LinearLayout.HORIZONTAL);
            decidingLayoutParam.gravity = Gravity.CENTER;
            l_col.setLayoutParams(decidingLayoutParam);

            for (int j = 0; j < ColumnSize; j++) {
                ImageView iv = new ImageView(mContext);
                IV_AllCards[i + row_adjustment][j + col_adjustment] = iv;
                l_col.addView(iv);
                iv.setImageResource(R.drawable.lock);
//                iv.setImageResource(Cards_ImageResID[i+row_adjustment][j+col_adjustment]);/
                pad_px = HelperClass.ConvertToPx(mContext, 1);
                iv.setPadding(pad_px, pad_px, pad_px, pad_px);

                iv.setTag(String.valueOf(i + row_adjustment) + HelperClass.DELIMITER + String.valueOf(j + col_adjustment));

                LinearLayout.LayoutParams cl_Param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                cl_Param.weight = 1.0f;
                cl_Param.gravity = Gravity.CENTER;

                iv.setLayoutParams(cl_Param);
                iv.setOnClickListener(CardClick_Listener);
            }

        }
        return simple_game;
    }

    protected void CreateOneBoardWithoutScroll_Game() {
        CreateImageMap();
        InitializeCardClickListener();
        IV_AllCards = new ImageView[RowSize][ColumnSize];

        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        LinearLayout simple_game = CreateBoardSet(0, 0, lParam1);
        LoadGame(simple_game);
    }

    protected void CreateOneBoardVerticalScroll_Game() {
        CreateImageMap();
        InitializeCardClickListener();
        IV_AllCards = new ImageView[RowSize][ColumnSize];

        int row_height = getRowHeightForOneBoardVerticalScroll(ColumnSize);
        LinearLayout.LayoutParams decidingLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                row_height, 1f);
        LinearLayout simple_game1 = CreateBoardSet(0, 0, decidingLayoutParam);

        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        ScrollView s1 = new ScrollView(mContext);
        s1.setLayoutParams(lParam1);
        s1.addView(simple_game1);

        final LinearLayout board = new LinearLayout(mContext);
        board.setOrientation(LinearLayout.HORIZONTAL);
        board.addView(s1);

        LoadGame(board);
    }

    protected void CreateOneBoardHorizontalScroll_Game() {
        CreateImageMap();
        InitializeCardClickListener();
        IV_AllCards = new ImageView[RowSize][ColumnSize];

        int col_width = getFullRowWidthForOneBoardHorizontalScroll();
        LinearLayout.LayoutParams decidingLayoutParam = new LinearLayout.LayoutParams(col_width,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        LinearLayout simple_game1 = CreateBoardSet(0, 0, decidingLayoutParam);

        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        HorizontalScrollView s1 = new HorizontalScrollView(mContext);
        s1.setLayoutParams(lParam1);
        s1.addView(simple_game1);

        final LinearLayout board = new LinearLayout(mContext);
        board.setOrientation(LinearLayout.VERTICAL);
        board.addView(s1);

        LoadGame(board);
    }

    protected void CreateOneBoardBothScroll_Game() {
        CreateImageMap();
        InitializeCardClickListener();
        IV_AllCards = new ImageView[RowSize][ColumnSize];

        Point card_size = getDisplaySizeForOneBoardScrollGame();
        int row_height = card_size.y;
        int col_width = card_size.x;
        LinearLayout.LayoutParams decidingLayoutParam = new LinearLayout.LayoutParams(col_width, row_height, 1f);
        LinearLayout simple_game1 = CreateBoardSet(0, 0, decidingLayoutParam);


        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        ScrollView s_v1 = new ScrollView(mContext);
        s_v1.setLayoutParams(lParam1);
        s_v1.addView(simple_game1);

        lParam1.weight = 1f;
        HorizontalScrollView s_h1 = new HorizontalScrollView(mContext);
        s_h1.setLayoutParams(lParam1);
        s_h1.addView(s_v1);

        final LinearLayout board = new LinearLayout(mContext);
        board.setOrientation(LinearLayout.HORIZONTAL);
        board.addView(s_h1);

        LoadGame(board);
    }

    private void CreateThreeBoardWithoutScroll_Game() {
        CreateImageMapForThreeBoard();
        IV_AllCards = new ImageView[3 * RowSize][ColumnSize];
        InitializeCardClickListener();

        LinearLayout.LayoutParams decidingLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        LinearLayout simple_game1 = CreateBoardSet(0, 0, decidingLayoutParam);
        LinearLayout simple_game2 = CreateBoardSet(RowSize, 0, decidingLayoutParam);
        LinearLayout simple_game3 = CreateBoardSet(RowSize * 2, 0, decidingLayoutParam);

        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        simple_game1.setLayoutParams(lParam1);
        simple_game2.setLayoutParams(lParam1);
        simple_game3.setLayoutParams(lParam1);

        RowSize *= 3;
        final LinearLayout threeBoard = new LinearLayout(mContext);
        threeBoard.setOrientation(LinearLayout.VERTICAL);
        threeBoard.addView(simple_game1);
        threeBoard.addView(getDivider());
        threeBoard.addView(simple_game2);
        threeBoard.addView(getDivider());
        threeBoard.addView(simple_game3);

        LoadGame(threeBoard);
    }

    protected void CreateThreeBoardVerticalScroll_Game() {
        IV_AllCards = new ImageView[3 * RowSize][ColumnSize];
        CreateImageMapForThreeBoard();
        InitializeCardClickListener();

        int row_height = getRowHeightForThreeBoardVerticalScroll(ColumnSize);
        LinearLayout.LayoutParams decidingLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                row_height, 1f);
        LinearLayout simple_game1 = CreateBoardSet(0, 0, decidingLayoutParam);
        LinearLayout simple_game2 = CreateBoardSet(RowSize, 0, decidingLayoutParam);
        LinearLayout simple_game3 = CreateBoardSet(RowSize * 2, 0, decidingLayoutParam);


        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        ScrollView s1 = new ScrollView(mContext);
        ScrollView s2 = new ScrollView(mContext);
        ScrollView s3 = new ScrollView(mContext);
        s1.setLayoutParams(lParam1);
        s2.setLayoutParams(lParam1);
        s3.setLayoutParams(lParam1);
        s1.addView(simple_game1);
        s2.addView(simple_game2);
        s3.addView(simple_game3);

        RowSize *= 3;
        final LinearLayout threeBoard = new LinearLayout(mContext);
        threeBoard.setOrientation(LinearLayout.VERTICAL);
        threeBoard.addView(s1);
        threeBoard.addView(getDivider());
        threeBoard.addView(s2);
        threeBoard.addView(getDivider());
        threeBoard.addView(s3);

        LoadGame(threeBoard);
    }

    protected void CreateThreeBoardHorizontalScroll_Game() {
        IV_AllCards = new ImageView[3 * RowSize][ColumnSize];
        CreateImageMapForThreeBoard();
        InitializeCardClickListener();

        int col_width = getFullRowWidthForThreeBoardHorizontalScroll();
        LinearLayout.LayoutParams decidingLayoutParam = new LinearLayout.LayoutParams(col_width, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        LinearLayout simple_game1 = CreateBoardSet(0, 0, decidingLayoutParam);
        LinearLayout simple_game2 = CreateBoardSet(RowSize, 0, decidingLayoutParam);
        LinearLayout simple_game3 = CreateBoardSet(RowSize * 2, 0, decidingLayoutParam);


        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        HorizontalScrollView s1 = new HorizontalScrollView(mContext);
        HorizontalScrollView s2 = new HorizontalScrollView(mContext);
        HorizontalScrollView s3 = new HorizontalScrollView(mContext);
        s1.setLayoutParams(lParam1);
        s2.setLayoutParams(lParam1);
        s3.setLayoutParams(lParam1);
        s1.addView(simple_game1);
        s2.addView(simple_game2);
        s3.addView(simple_game3);

        RowSize *= 3;
        final LinearLayout threeBoard = new LinearLayout(mContext);
        threeBoard.setOrientation(LinearLayout.VERTICAL);
        threeBoard.addView(s1);
        threeBoard.addView(getDivider());
        threeBoard.addView(s2);
        threeBoard.addView(getDivider());
        threeBoard.addView(s3);

        LoadGame(threeBoard);
    }

    protected void CreateThreeBoardBothScroll_Game() {
        IV_AllCards = new ImageView[3 * RowSize][ColumnSize];
        CreateImageMapForThreeBoard();
        InitializeCardClickListener();

        Point card_size = getDisplaySizeForThreeBoardScrollGame();
        int row_height = card_size.y;
        int col_width = card_size.x;
        LinearLayout.LayoutParams decidingLayoutParam = new LinearLayout.LayoutParams(col_width, row_height, 1f);
        LinearLayout simple_game1 = CreateBoardSet(0, 0, decidingLayoutParam);
        LinearLayout simple_game2 = CreateBoardSet(RowSize, 0, decidingLayoutParam);
        LinearLayout simple_game3 = CreateBoardSet(RowSize * 2, 0, decidingLayoutParam);


        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        ScrollView s_v1 = new ScrollView(mContext);
        ScrollView s_v2 = new ScrollView(mContext);
        ScrollView s_v3 = new ScrollView(mContext);
        s_v1.setLayoutParams(lParam1);
        s_v2.setLayoutParams(lParam1);
        s_v3.setLayoutParams(lParam1);
        s_v1.addView(simple_game1);
        s_v2.addView(simple_game2);
        s_v3.addView(simple_game3);

        lParam1.weight = 1f;
        HorizontalScrollView s_h1 = new HorizontalScrollView(mContext);
        HorizontalScrollView s_h2 = new HorizontalScrollView(mContext);
        HorizontalScrollView s_h3 = new HorizontalScrollView(mContext);
        s_h1.setLayoutParams(lParam1);
        s_h2.setLayoutParams(lParam1);
        s_h3.setLayoutParams(lParam1);
        s_h1.addView(s_v1);
        s_h2.addView(s_v2);
        s_h3.addView(s_v3);

        RowSize *= 3;
        final LinearLayout threeBoard = new LinearLayout(mContext);
        threeBoard.setOrientation(LinearLayout.VERTICAL);
        threeBoard.addView(s_h1);
        threeBoard.addView(getDivider());
        threeBoard.addView(s_h2);
        threeBoard.addView(getDivider());
        threeBoard.addView(s_h3);

        LoadGame(threeBoard);
    }

    protected View getDivider() {
        int TwoBoardAdjustment = 2;
        RelativeLayout v = new RelativeLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                HelperClass.ConvertToPx(mContext, TwoBoardAdjustment));
        v.setLayoutParams(layoutParams);
        v.setBackgroundColor(Color.argb(63, 0, 0, 0));
        return v;
    }


    private void LoadGame(View game) {
        LinearLayout main_layout = new LinearLayout(mContext);

        LinearLayout.LayoutParams lParam1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        game.setLayoutParams(lParam1);

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
        LayoutInflater inflater = mContext.getLayoutInflater();
        View gameInfoSection = inflater.inflate(R.layout.view_game_title, main_layout, false);
        tvPlayerTurn = (TextView) gameInfoSection.findViewById(R.id.tvPlayerTurn);
        tvScore = (TextView) gameInfoSection.findViewById(R.id.tvScore);
        tvTime = (TextView) gameInfoSection.findViewById(R.id.tvTime);
        tvTimeTrialTimer = (TextView) gameInfoSection.findViewById(R.id.tvTimer);
        tvConsecutiveWins = (TextView) gameInfoSection.findViewById(R.id.tvWinningStreak);
        tvHitStreak = (TextView) gameInfoSection.findViewById(R.id.tvHitStreak);
        tvMoves = (TextView) gameInfoSection.findViewById(R.id.tvMoves);
        Btn_Power = (Button) gameInfoSection.findViewById(R.id.btnPower);

        if (mContext.playerOneName.length() > 8 || mContext.playerTwoName.length() > 8)
            tvPlayerTurn.setTextSize(HelperClass.ConvertToPx(mContext, 8));
        HelperClass.applyBorderDrawableToView(tvTimeTrialTimer, Color.TRANSPARENT, Color.WHITE, HelperClass.ConvertToPx(mContext, 10), 1);
        HelperClass.applyBorderDrawableToView(tvScore, Color.TRANSPARENT, Color.BLACK, HelperClass.ConvertToPx(mContext, 4), 1);
        HelperClass.applyBorderDrawableToView(tvTime, Color.TRANSPARENT, Color.BLACK, HelperClass.ConvertToPx(mContext, 4), 1);

        HelperClass.SetFontToControls(font, (ViewGroup) gameInfoSection);
        tvMoves.setTypeface(Typeface.DEFAULT_BOLD);

        if (GameMode != HelperClass.TIME_TRIAL)
            tvTimeTrialTimer.setVisibility(View.GONE);

        SetGameInfoText();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int winningStreak = preferences.getInt(String.valueOf(HelperClass.PREVIOUS_WINNING_STREAK), 0);
        int previousPlayerMode = preferences.getInt(String.valueOf(HelperClass.PREVIOUS_PLAYER_MODE), 0);
        if (winningStreak > 0 && previousPlayerMode == PlayerMode) {
            tvConsecutiveWins.setVisibility(View.VISIBLE);
            tvConsecutiveWins.setText("Winning Streak : " + String.valueOf(winningStreak));
        }


        main_layout.setOrientation(LinearLayout.VERTICAL);
        main_layout.setBackgroundResource(getBackground());
        main_layout.addView(gameInfoSection);
        main_layout.addView(game);

        mContext.CurrentView.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out));
        main_layout.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
        mContext.setContentView(main_layout);
        mContext.CurrentView = main_layout;

        setGameBoard(IV_AllCards[0][0]);
        objPower.AssignClickListener(Btn_Power);
        CreateCardPairMap();

    }


    //Height for vertical scroll
    private int getRowHeightForOneBoardVerticalScroll(int ColumnSize) {
        int height;
        int screen_height =
                HelperClass.getWindowSize(mContext.getWindowManager().getDefaultDisplay()).y - HelperClass.ConvertToPx(mContext, 60);
        int numberOfRows = ColumnSize + 2;
        if (numberOfRows > RowSize)
            numberOfRows = RowSize;

        height = screen_height / numberOfRows;
        return height;
    }

    //Height for vertical scroll
    private int getRowHeightForThreeBoardVerticalScroll(int ColumnSize) {
        int height;
        int screen_height = HelperClass.getWindowSize(mContext.getWindowManager().getDefaultDisplay()).y
                - HelperClass.ConvertToPx(mContext, 64);
        int numberOfRows = ColumnSize + 2;
        if (numberOfRows > RowSize * 3)
            numberOfRows = RowSize * 3;

        if (numberOfRows % 3 == 1)
            numberOfRows--;
        else if (numberOfRows % 3 == 2)
            numberOfRows-=2;

        if (numberOfRows<6 && RowSize*3>6)
            numberOfRows+=3;

        height = screen_height / numberOfRows;
        return height;
    }

    //ThreeBoardScroll_H
    public int getFullRowWidthForThreeBoardHorizontalScroll() {
        return getFullRowWidthForOneBoardHorizontalScroll();
    }

    //OneBoardScroll_H
    public int getFullRowWidthForOneBoardHorizontalScroll() {
        int card_width;

        int screen_width = HelperClass.getWindowSize(mContext.getWindowManager().getDefaultDisplay()).x;
        switch (ColumnSize) {
            case 1:
            case 2:
            case 3:
            case 4:
                card_width = screen_width / (ColumnSize - 1);
                break;
            default:
                card_width = screen_width / 4;
        }

        return card_width * ColumnSize;
    }

    //ThreeBoardScroll_B
    public Point getDisplaySizeForThreeBoardScrollGame() {
        Point windowSize = HelperClass.getWindowSize(mContext.getWindowManager().getDefaultDisplay());

        int width, height;
        switch (ColumnSize) {
            case 1:
            case 2:
            case 3:
                width = windowSize.x;
                break;
            default:
                width = (windowSize.x / 4) * ColumnSize;
                break;
        }
        switch (RowSize * 3) {
            case 3:
            case 6:
                height = (windowSize.y - HelperClass.ConvertToPx(mContext, 64)) / (3);
                break;
            default:
                height = (windowSize.y - HelperClass.ConvertToPx(mContext, 64)) / 6;
                break;
        }

        return new Point(width, height);
    }

    //OneBoardScroll_B
    public Point getDisplaySizeForOneBoardScrollGame() {
        Point windowSize = HelperClass.getWindowSize(mContext.getWindowManager().getDefaultDisplay());

        int width, height;
        switch (ColumnSize) {
            case 1:
            case 2:
            case 3:
                width = windowSize.x;
                break;
            default:
                width = (windowSize.x / 4) * ColumnSize;
                break;
        }
        switch (RowSize) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                height = (windowSize.y - HelperClass.ConvertToPx(mContext, 60)) / RowSize;
                break;
            default:
                height = (windowSize.y - HelperClass.ConvertToPx(mContext, 60)) / 5;
                break;
        }

        return new Point(width, height);
    }


    //
    private void setGameBoard(View card) {
        switch (BoardIdentifier) {
            case HelperClass.OneBoard_WithoutScroll:
                GameBoard = (ViewGroup) (card.getParent().getParent());
                break;
            case HelperClass.ThreeBoard_WithoutScroll:
                GameBoard = (ViewGroup) (card.getParent().getParent().getParent());
                break;
            case HelperClass.OneBoard_HorizontalScroll:
            case HelperClass.OneBoard_VerticalScroll:
                GameBoard = (ViewGroup) (card.getParent().getParent().getParent());
                break;
            case HelperClass.OneBoard_BothScroll:
                GameBoard = (ViewGroup) (card.getParent().getParent().getParent().getParent());
                break;
            case HelperClass.ThreeBoard_HorizontalScroll:
            case HelperClass.ThreeBoard_VerticalScroll:
                GameBoard = (ViewGroup) (card.getParent().getParent().getParent().getParent());
            case HelperClass.ThreeBoard_BothScroll:
                GameBoard = (ViewGroup) (card.getParent().getParent().getParent().getParent().getParent());
                break;
        }
    }

    private void CreateCardPairMap() {
        for (int i = 0; i < RowSize; i++) {
            for (int j = 0; j < ColumnSize; j++) {
                if (MatchingCard_Map.indexOfKey(Cards_ImageResID[i][j]) < 0) {
                    String item = String.valueOf(i) + HelperClass.DELIMITER + String.valueOf(j);
                    MatchingCard_Map.put(Cards_ImageResID[i][j], item);
                } else {
                    String item = MatchingCard_Map.get(Cards_ImageResID[i][j]);
                    item += HelperClass.DELIMITER_2 + String.valueOf(i) + HelperClass.DELIMITER + String.valueOf(j);
                    MatchingCard_Map.delete(Cards_ImageResID[i][j]);
                    MatchingCard_Map.put(Cards_ImageResID[i][j], item);
                }
            }
        }
    }

    private void RandomizeImagesMatrix(int M[][]) {
        int x, y, temp_id;
        for (int i = 0; i < RowSize; i++) {
            for (int j = 0; j < ColumnSize; j++) {
                x = (int) (Math.random() * 100) % RowSize;
                y = (int) (Math.random() * 100) % ColumnSize;
                temp_id = M[x][y];
                M[x][y] = M[i][j];
                M[i][j] = temp_id;
            }
        }
    }

    private void shuffleArray(int[] ar, int length) {
        // If running on Java 6 or older, use `new Random()` on RHS
        Random rnd;
        if (Build.VERSION.SDK_INT >= 21)
            rnd = ThreadLocalRandom.current();
        else
            rnd = new Random();

        for (int i = length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public void SoundEffect(boolean IsMatch) {
        if (IsMatch)
            sp.play(CARD_MATCH_SOUND, 1, 1, 1, 0, 1);
        else
            sp.play(CARD_MISMATCH_SOUND, 1, 1, 1, 0, 2);
    }

    private void InitializeSoundPool() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sp = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }
        CARD_MATCH_SOUND = sp.load(mContext, R.raw.card_match, 1);
        CARD_MISMATCH_SOUND = sp.load(mContext, R.raw.card_mismatch, 1);
    }

    @Nullable
    private int[][] getCardSet() {
        switch (CardSet) {
            case HelperClass.CARD_SET_1:
                return getCardSetOne();
            case HelperClass.CARD_SET_2:
                return getCardSetTwo();
            case HelperClass.CARD_SET_3:
                return getCardSetThree();
            case HelperClass.STORY_MODE_CARD_SET:
                return getCardForStoryMode();
        }
        return null;
    }

    private int[][] getCardSetOne() {
        int AllCards[] = GetCardsFromResources(R.array.card_set_one);
        int ImageMap[][] = new int[RowSize][ColumnSize];
        int AllImages_length = AllCards.length;

        for (int i = 0; i < RowSize; i++) {
            for (int j = 0; j < ColumnSize; j++) {
                int index = (int) (Math.random() * 1000 % AllImages_length);
                ImageMap[i][j] = AllCards[index];
                //Remove from array
                System.arraycopy(AllCards, index + 1, AllCards, index, AllImages_length - 1 - index);
                AllImages_length--;
                if (AllImages_length < 70)
                    break;
            }
            if (AllImages_length < 70)
                break;
        }
        createReplacementCards(AllCards, AllImages_length);

        return ImageMap;
    }

    private int[][] getCardSetTwo() {
        int AllSet1_Cards[] = GetCardsFromResources(R.array.card_set_one);
        int AllSet2_Cards[] = GetCardsFromResources(R.array.card_set_three_type1);

        int ImageMap[][] = new int[RowSize][ColumnSize];
        int set1_count = TotalCardsOnBoard / 4;
        int count = 1;

        int set1Cards_length = AllSet1_Cards.length;
        int set2Cards_length = AllSet2_Cards.length;

        for (int r = 0; r < RowSize; r++) {
            for (int c = 0; c < ColumnSize; c++) {
                if (count++ < set1_count) {
                    int index = (int) (Math.random() * 1000 % set1Cards_length);
                    ImageMap[r][c] = AllSet1_Cards[index];
                    //Remove this card
                    System.arraycopy(AllSet1_Cards, index + 1, AllSet1_Cards, index, set1Cards_length - 1 - index);
                    set1Cards_length--;
                } else {
                    int index = (int) (Math.random() * 1000 % set2Cards_length);
                    index = index / 2 * 2;
                    ImageMap[r][c++] = AllSet2_Cards[index];
                    if (c == ColumnSize) {
                        c = 0;
                        r++;
                    }
                    if (r == RowSize) break;
                    ImageMap[r][c] = AllSet2_Cards[index + 1];
                    //Remove these two card
                    System.arraycopy(AllSet2_Cards, index + 2, AllSet2_Cards, index, set2Cards_length - 2 - index);
                    set2Cards_length -= 2;
                }
            }
        }
        int[] replacementCards = GetCardsFromResources(R.array.card_set_three_type2);
        createReplacementCards(replacementCards, replacementCards.length);

        return ImageMap;
    }

    private int[][] getCardSetThree() {
        int AllType1Cards[] = GetCardsFromResources(R.array.card_set_three_type1);
        int AllType2Cards[] = GetCardsFromResources(R.array.card_set_three_type2);

        int ImageMap[][] = new int[RowSize][ColumnSize];
        int count = 1;

        int type1Cards_length = AllType1Cards.length;
        int type2Cards_length = AllType2Cards.length;

        for (int r = 0; r < RowSize; r++) {
            for (int c = 0; c < ColumnSize; c++) {
                if (count % 5 != 0) {
                    int index = (int) (Math.random() * 1000 % type1Cards_length);
                    index = index / 2 * 2;
                    ImageMap[r][c++] = AllType1Cards[index];
                    if (c == ColumnSize) {
                        c = 0;
                        r++;
                    }
                    if (r == RowSize) {
                        type1Cards_length--;
                        break;
                    }
                    ImageMap[r][c] = AllType1Cards[index + 1];
                    //Remove these two card
                    System.arraycopy(AllType1Cards, index + 2, AllType1Cards, index, type1Cards_length - 2 - index);
                    type1Cards_length -= 2;
                } else {
                    int index = (int) (Math.random() * 1000 % type2Cards_length);
                    index = index / 2 * 2;
                    ImageMap[r][c++] = AllType2Cards[index];
                    if (c == ColumnSize) {
                        c = 0;
                        r++;
                    }
                    if (r == RowSize) break;
                    ImageMap[r][c] = AllType2Cards[index + 1];
                    //Remove these two card
                    System.arraycopy(AllType2Cards, index + 2, AllType2Cards, index, type2Cards_length - 2 - index);
                    type2Cards_length -= 2;
                }
                count++;
                if (type1Cards_length < 70)
                    break;
            }
            if (type1Cards_length < 70)
                break;
        }
        createReplacementCards(AllType1Cards, type1Cards_length);


        return ImageMap;
    }

    private int[][] getCardForStoryMode() {
        GameValues objGameValues = new GameValues(CurrentModule, CurrentLevel, CurrentStage, CurrentChallenge);
        int AllCards[] = objGameValues.getCardSet();
        shuffleArray(AllCards, AllCards.length);
        CardSet = objGameValues.getCardSetValue();
        int ImageMap[][] = new int[RowSize][ColumnSize];
        int index = 0;
        int AllCardsLength = AllCards.length;
        for (int i = 0; i < RowSize && index < AllCardsLength; i++) {
            for (int j = 0; j < ColumnSize && index < AllCardsLength; j++) {
                ImageMap[i][j] = AllCards[index++];
            }
        }
        ReplacementCards = objGameValues.getReplacementCards();
        objGameValues = null;
        mContext.CollectGarbage();
        return ImageMap;
    }

    private void createReplacementCards(int[] possibleReplacements, int length) {
        //create replacement array
        int sizeOfReplacementArray = (TotalCardsOnBoard + 1) / 2;
        ReplacementCards = new int[sizeOfReplacementArray * 2];
        for (int i = 0; i < sizeOfReplacementArray; i++) {
            int index = (int) (Math.random() * 1000 % length);
            ReplacementCards[i] = possibleReplacements[index];
            //Remove from array
            System.arraycopy(possibleReplacements, index + 1, possibleReplacements, index, length - 1 - index);
            length--;
        }
        int j = sizeOfReplacementArray - 1;
        for (int i = sizeOfReplacementArray * 2 - 1; i >= sizeOfReplacementArray; i--) {
            ReplacementCards[i] = ReplacementCards[j--];
        }
    }

    public int[] GetCardsFromResources(int id) {
        int AllImages[];
        TypedArray images;
        images = mContext.getResources().obtainTypedArray(id);

        int length = images.length();
        AllImages = new int[length];
        for (int i = 0; i < length; i++) {
            AllImages[i] = images.getResourceId(i, -1);
        }
        images.recycle();
        return AllImages;
    }

    private int getBackground() {
        TypedArray backgrounds = mContext.getResources().obtainTypedArray(R.array.game_backgrounds);
        if (GameBackground <= backgrounds.length()) {
            if (GameBackground != 0) {
                GameBackground = backgrounds.getResourceId(GameBackground - 1, -1);
            } else {
                GameBackground = backgrounds.getResourceId((int) (Math.random() * 100) % backgrounds.length(), -1);
            }
        }
        backgrounds.recycle();
        return GameBackground;
    }
    //


    ImageView SelectedCards[]=new ImageView[3];
    //Contain definition of default card click listener
    private void InitializeCardClickListener()
    {
        DefaultFlipListener = new Animation.AnimationListener() {

            int CurrentCardCount=0;
            public CountDownTimer cWaitAfterMove =
                    new CountDownTimer(LockingTime, LockingTime) {
                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish()  {
                            //Sync threads!
                            AcquireLOCK();
                            for(int i=0;i<CurrentCardCount;i++)
                            {
                                if(SelectedCards[i]!=null)
                                {
                                    SelectedCards[i].setImageResource(R.drawable.lock);
                                    SelectedCards[i].setOnClickListener(CardClick_Listener);
                                }
                            }
                            CurrentCardCount=0;
                            //Sync Threads
                            ReleaseLOCK();

                            if(PlayerMode != HelperClass.ROBOT_PLAYER || PlayerOne_Turn)
                                HelperClass.SetEnableControls(true, GameBoard);
                        }
                    };

            public CountDownTimer cWaitAndSimulateRobotMove =
                    new CountDownTimer(LockingTime+100, LockingTime+100) {
                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish()  {
                            SimulateRobotMove();
                        }
                    };

            @Override
            public void onAnimationStart(Animation animation) {

                //Sync threads !!
                AcquireLOCK();
                isAnimating=true;
                HelperClass.SetEnableControls(false, GameBoard);
                if( ActualClickCount==0 )
                {
                    GameTimer.start();
                }

                if((PlayerMode== HelperClass.ONE_PLAYER || PlayerOne_Turn) && ActualClickCount%3==0)
                    ComputeCardAttemptMap(CurrentCard.getTag().toString());


                int i = Integer.parseInt(CurrentCard.getTag().toString().split(HelperClass.DELIMITER)[0]);
                int j = Integer.parseInt(CurrentCard.getTag().toString().split(HelperClass.DELIMITER)[1]);

                IV_AllCards[i][j] = null;
                //Sync threads !!
                ReleaseLOCK();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Sync threads !!
                AcquireLOCK();

                int i = Integer.parseInt(CurrentCard.getTag().toString().split(HelperClass.DELIMITER)[0]);
                int j = Integer.parseInt(CurrentCard.getTag().toString().split(HelperClass.DELIMITER)[1]);
                Card_Clicks[i][j]++;
                EffectiveClickCount++;
                ActualClickCount++;
                if(PlayerMode==ROBOT_PLAYER)
                    robotPlayer.updateCardClick(i,j);
                CardLastClicked[i][j] = ActualClickCount;

                switch (EffectiveClickCount%3)
                {
                    //region first card
                    case 1:
                    {
                        SelectedCards[CurrentCardCount]=CurrentCard;
                        SelectedCards[CurrentCardCount].setOnClickListener(null);
                        SelectedCards[CurrentCardCount++].setImageResource(Cards_ImageResID[i][j]);
                        HelperClass.SetEnableControls(true, GameBoard);
                        CardRetainingPower[i][j] = (ActualClickCount+1)/3;
                        break;
                    }
                    //endregion

                    //region second card
                    case 2:
                    {
                        SelectedCards[CurrentCardCount]=CurrentCard;
                        SelectedCards[CurrentCardCount].setOnClickListener(null);
                        SelectedCards[CurrentCardCount++].setImageResource(Cards_ImageResID[i][j]);
                        CardRetainingPower[i][j] = (ActualClickCount+1)/3;

                        int i1 = Integer.parseInt(SelectedCards[0].getTag().toString().split(HelperClass.DELIMITER)[0]);
                        int j1 = Integer.parseInt(SelectedCards[0].getTag().toString().split(HelperClass.DELIMITER)[1]);

                        //didn't match with first card
                        if(Cards_ImageResID[i1][j1] != Cards_ImageResID[i][j])
                        {
                            SoundEffect(false);

                            IV_AllCards[i][j] = SelectedCards[1];
                            IV_AllCards[i1][j1] = SelectedCards[0];
                            EffectiveClickCount-=2;

                            IsConsecutiveHit=false;
                            tvHitStreak.setText("");
                            PlayerOne_Turn=!PlayerOne_Turn;
                            if(PlayerOne_Turn || PlayerMode == HelperClass.ONE_PLAYER)
                                Player1_Moves++;
                            TotalMoves++;

                            cWaitAfterMove.cancel();
                            cWaitAfterMove.start();
                        }
                        else
                        {
                            HelperClass.SetEnableControls(true, GameBoard);
                        }
                        break;
                    }
                    //endregion

                    //region third card
                    case 0:
                    {
                        SelectedCards[CurrentCardCount]=CurrentCard;
                        SelectedCards[CurrentCardCount].setOnClickListener(null);
                        SelectedCards[CurrentCardCount++].setImageResource(Cards_ImageResID[i][j]);
                        int i0 = Integer.parseInt(SelectedCards[0].getTag().toString().split(HelperClass.DELIMITER)[0]);
                        int j0 = Integer.parseInt(SelectedCards[0].getTag().toString().split(HelperClass.DELIMITER)[1]);
                        int i1 = Integer.parseInt(SelectedCards[1].getTag().toString().split(HelperClass.DELIMITER)[0]);
                        int j1 = Integer.parseInt(SelectedCards[1].getTag().toString().split(HelperClass.DELIMITER)[1]);

                        //matched previous cards
                        if(Cards_ImageResID[i1][j1] == Cards_ImageResID[i][j])
                        {
                            SoundEffect(true);
                            if(PlayerOne_Turn)
                                PlayerOne_Score++;
                            else
                                PlayerTwo_Score++;

                            if(PlayerOne_Turn || PlayerMode == HelperClass.ONE_PLAYER)
                            {
                                Player1_Moves++;
                                Matches.put(Cards_ImageResID[i][j],1);
                                if(IsConsecutiveHit)
                                {
                                    currentHitStreak++;
                                }else
                                {
                                    currentHitStreak=1;
                                    IsConsecutiveHit=true;
                                }
                                if(currentHitStreak>maxHitStreak)
                                    maxHitStreak=currentHitStreak;

                                tvHitStreak.setText("Hit Streak : "+String.valueOf(currentHitStreak));
                            }
                            TotalMoves++;

                            if(GameMode == HelperClass.TIME_TRIAL)
                            {
                                objTimeTrail.TimeTrialTimer.cancel();
                                objTimeTrail.TimeTrialTimer.start();
                            }

                            RemoveCardFromReplacementList(Cards_ImageResID[i][j]);
                            SelectedCards[2].setOnClickListener(null);
                            HelperClass.SetEnableControls(true, GameBoard);
                            CurrentCardCount=0;
                        }
                        else
                        {
                            SoundEffect(false);
                            IV_AllCards[i0][j0] = SelectedCards[0];
                            IV_AllCards[i1][j1] = SelectedCards[1];
                            IV_AllCards[i][j] = SelectedCards[2];
                            EffectiveClickCount-=3;
                            CardRetainingPower[i][j] = (ActualClickCount+1)/3;

                            if(PlayerOne_Turn || PlayerMode == HelperClass.ONE_PLAYER)
                                ComputeNearMisses(SelectedCards[0].getTag().toString(),
                                        SelectedCards[1].getTag().toString(),
                                        SelectedCards[2].getTag().toString());

                            IsConsecutiveHit=false;
                            tvHitStreak.setText("");
                            PlayerOne_Turn=!PlayerOne_Turn;
                            if(PlayerOne_Turn || PlayerMode == HelperClass.ONE_PLAYER)
                                Player1_Moves++;
                            TotalMoves++;

                            cWaitAfterMove.cancel();
                            cWaitAfterMove.start();
                        }
                        SetGameInfoText();
                        break;
                    }
                    //endregion
                }

                if(EffectiveClickCount+clickAdjustment_destroyedCards >= TotalCardsOnBoard )
                {
                    GameTimer.cancel();
                    if(GameMode == HelperClass.TIME_TRIAL)
                        objTimeTrail.TimeTrialTimer.cancel();
                    postGameLogic();
                }
                else
                {
                    Btn_Power.setEnabled(true);
                    if(PlayerMode == HelperClass.ROBOT_PLAYER && !PlayerOne_Turn)
                    {
                        HelperClass.SetEnableControls(false, GameBoard);
                        Btn_Power.setEnabled(false);
                        cWaitAndSimulateRobotMove.cancel();
                        cWaitAndSimulateRobotMove.start();
                    }
                }

                isAnimating=false;
                //Sync threads !!
                ReleaseLOCK();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        };
        Flip_anim.setAnimationListener(DefaultFlipListener);
        CardClick_Listener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                CurrentCard = (ImageView)view;
                view.startAnimation(Flip_anim);
            }
        };
    }

    Runnable myRunnable;
    public void SimulateRobotMove()
    {
        if(myRunnable==null)
            myRunnable = new Runnable(){

                public void run(){

                //wait till TT anim is complete
                    if(GameMode == HelperClass.TIME_TRIAL)
                        while (objTimeTrail.isAnimating);

                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Sync threads !!
                            AcquireLOCK();

                            robotPlayer.SimulateMove();

                            //Sync threads !!
                            ReleaseLOCK();
                        }
                    });
                }
            };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }
    public void RemoveCardFromReplacementList(int resID) {
        int array_length = ReplacementCards.length;
        int count =0;
        for(int i = 0;i<array_length-1 && count<2 ;i++)
        {
            if(ReplacementCards[i]==resID)
            {
                while (ReplacementCards[i]==resID && count<2)
                {
                    System.arraycopy(ReplacementCards, i + 1, ReplacementCards, i, array_length - 1 - i);
                    count++;
                }
            }
        }
    }

    private void ComputeNearMisses(String FirstCard,String SecondCard,String ThirdCard) {
        int r = Integer.parseInt(FirstCard.split(HelperClass.DELIMITER)[0]);
        int c = Integer.parseInt(FirstCard.split(HelperClass.DELIMITER)[1]);
        String cards_encrypted = MatchingCard_Map.get(Cards_ImageResID[r][c]);

        String matchingCard;
        String[] card_location = cards_encrypted.split(HelperClass.DELIMITER_2);
        if(card_location.length<3)
            return;

        if((FirstCard.equals(card_location[0]) && SecondCard.equals(card_location[1])) ||
                (FirstCard.equals(card_location[1]) && SecondCard.equals(card_location[0])) )
        {
            matchingCard = card_location[2];
        }
        else if((FirstCard.equals(card_location[1]) && SecondCard.equals(card_location[2])) ||
                (FirstCard.equals(card_location[2]) && SecondCard.equals(card_location[1])) )
        {
            matchingCard = card_location[0];
        }
        else
        {
            matchingCard = card_location[1];
        }
        int matchingCard_r = Integer.parseInt(matchingCard.split(HelperClass.DELIMITER)[0]);
        int matchingCard_c = Integer.parseInt(matchingCard.split(HelperClass.DELIMITER)[1]);

        if(CardLastClicked[matchingCard_r][matchingCard_c]>0 )
        {
            r = Integer.parseInt(ThirdCard.split(HelperClass.DELIMITER)[0]);
            c = Integer.parseInt(ThirdCard.split(HelperClass.DELIMITER)[1]);
            int dx = matchingCard_r - r;
            int dy = matchingCard_c - c;
            if( (dx>=-1 && dx<=1) && (dy>=-1 && dy<=1) )
            {
                NearMisses++;
            }
        }
    }

    private void ComputeCardAttemptMap(String tag) {

        int r = Integer.parseInt(tag.split(HelperClass.DELIMITER)[0]);
        int c = Integer.parseInt(tag.split(HelperClass.DELIMITER)[1]);

        String cards_encrypted = MatchingCard_Map.get(Cards_ImageResID[r][c]);
        String []cards_location = cards_encrypted.split(HelperClass.DELIMITER_2);
        if(cards_location.length<3)
            return;


        String matching_card1,matching_card2;
        if(tag.equals(cards_location[0]))
        {
            matching_card1 = cards_location[1];
            matching_card2 = cards_location[2];
        }
        else if(tag.equals(cards_location[1]))
        {
            matching_card1 = cards_location[2];
            matching_card2 = cards_location[0];
        }
        else
        {
            matching_card1 = cards_location[0];
            matching_card2 = cards_location[1];
        }
        int matchingCard1_r = Integer.parseInt(matching_card1.split(HelperClass.DELIMITER)[0]);
        int matchingCard1_c = Integer.parseInt(matching_card1.split(HelperClass.DELIMITER)[1]);

        int matchingCard2_r = Integer.parseInt(matching_card2.split(HelperClass.DELIMITER)[0]);
        int matchingCard2_c = Integer.parseInt(matching_card2.split(HelperClass.DELIMITER)[1]);

        if(CardLastClicked[matchingCard1_r][matchingCard1_c]>0 && CardLastClicked[matchingCard2_r][matchingCard2_c]>0)
        {
            if(Cards_ImageResID[r][c]>0) {
                if (CardAttempt_Map.indexOfKey(Cards_ImageResID[r][c]) >= 0) {
                    int attempt = CardAttempt_Map.get(Cards_ImageResID[r][c]);
                    if (attempt == -1)
                        attempt++;

                    CardAttempt_Map.put(Cards_ImageResID[r][c], attempt + 1);

                } else {
                    CardAttempt_Map.put(Cards_ImageResID[r][c], 1);
                }
            }
        }
        else
        {
            CardAttempt_Map.put(Cards_ImageResID[r][c], -1);
        }
    }

    //Sets the Game details
    public void SetGameInfoText()
    {
        String turn, time, score, timeTrialMsg;
        String playerOne = mContext.playerOneName;
        String playerTwo = getPlayer2_Name();

        if (PlayerOne_Turn )
        {
            turn = playerOne + "' turn" ;
        } else
        {
            turn = playerTwo + "' turn" ;
        }
        score = "P1-" + String.valueOf(PlayerOne_Score) + " P2-" + String.valueOf(PlayerTwo_Score);

        if(PlayerMode == HelperClass.ONE_PLAYER)
        {
            turn = playerOne + "' turn" ;
            score = "P1-" + String.valueOf(PlayerOne_Score+PlayerTwo_Score);
            if(Player1_Moves>0)
                tvMoves.setText("#"+String.valueOf(Player1_Moves));
        }

        time = "Time : " + String.valueOf(GameRunningTime);

        tvPlayerTurn.setText(turn);
        tvScore.setText(score);
        tvTime.setText(time);

        if(GameMode == HelperClass.TIME_TRIAL)
        {
            timeTrialMsg = String.valueOf(objTimeTrail.SecondsLeft_TimeTrial);
            tvTimeTrialTimer.setText(timeTrialMsg);
        }

    }

    public String getPlayer2_Name()
    {
        if(PlayerMode == HelperClass.ROBOT_PLAYER)
            return mContext.get_text(PlayerTwoType);

        return mContext.playerTwoName;
    }

    //region Post Game Dialog
    public void postGameLogic()
    {
        mContext.CURRENT_SCREEN = HelperClass.SCREEN_POST_GAME;
        //region Display Remove Ads dialog Periodically
        if(mContext.displayAdsCounter>=1 && !mContext.adFreeVersion)
        {
            LayoutInflater inflater = mContext.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_remove_ads, null, true);

            final Dialog dialog = new AlertDialog.Builder(mContext).show();
            dialog.setCancelable(false);
            dialog.setContentView(view);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            View v2 =  mContext.CurrentView;
            lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            mContext.objInAppBilling.setRemoveAdsPrice(view.findViewById(R.id.tvRemoveAdsPrice));

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK))
                    {
                        DisplayInterstitialAd();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            //region Click Listener for remove ads dialog
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId())
                    {
                        case R.id.btnShowAd:
                            DisplayInterstitialAd();
                            break;
                        case R.id.btnRemoveAds:
                            ShowBoardCompleteDialog();
                            String SKU_REMOVE_ADS = "sku_remove_ads";
                            mContext.objInAppBilling.LaunchPurchaseFlow(SKU_REMOVE_ADS);
//                            mContext.objCardGame.CleanUp();
                            break;
                    }
                    dialog.dismiss();
                }
            };
            //endregion

            view.findViewById(R.id.btnShowAd).setOnClickListener(listener);
            view.findViewById(R.id.btnRemoveAds).setOnClickListener(listener);
            dialog.show();
        }
        //endregion
        else
            ShowBoardCompleteDialog();
    }

    public void DisplayInterstitialAd()
    {
        //if(Math.random()>0.5)// adbuddiz
        {
            if (mContext.mInterstitialAd!=null && mContext.mInterstitialAd.isLoaded())
            {
                mContext.mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        mContext.requestNewInterstitial();
                        ShowBoardCompleteDialog();
                        mContext.displayAdsCounter = 0;
                        mContext.CollectGarbage();
                    }
                });
                mContext.mInterstitialAd.show();
            }
            else
            {
                mContext.requestNewInterstitial();
                ShowBoardCompleteDialog();
                mContext.displayAdsCounter=1; //Setting value to one less than trigger value
            }
        }
//        else adbuddiz
//        {
//            AdBuddiz.showAd(mContext);
//            ShowBoardCompleteDialog();
//            mContext.displayAdsCounter = 0;
//            mContext.CollectGarbage();
//        }
    }

    public void ShowBoardCompleteDialog()
    {
        if(objGameSummary==null)
            objGameSummary = new GameSummary(new WeakReference<>(this),
                CurrentCard.getMeasuredHeight(), CurrentCard.getMeasuredWidth());
        else
        {
            objGameSummary.CardHeight=CurrentCard.getMeasuredHeight();
            objGameSummary.CardWidth=CurrentCard.getMeasuredWidth();
        }
        objGameSummary.CalculateScore();
        if (StoryMode)
        {
            objGameSummary.writeCoinsToPreferences(0);
            if (!powUsed)
                objGameSummary.getHighestScore();

            PostGame objPostGame = new PostGame(new WeakReference<>(mContext));
            objPostGame.ShowLevelCompletedDialog();
        }
        else
            ShowLevelCompletedDialogForNonStoryMode();
    }
    public void ShowLevelCompletedDialogForNonStoryMode()
    {
        mContext.CURRENT_SCREEN = HelperClass.SCREEN_POST_GAME_DIALOG_NORMAL;
        LayoutInflater inflater = mContext.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_normal_mode_summary, null, true);

        createSummaryMessage(view);
        final Dialog dialog = new AlertDialog.Builder(mContext).show();
        dialog.setCancelable(false);
        dialog.setContentView(view);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK))
                {
                    switch (mContext.CURRENT_SCREEN)
                    {
                        case HelperClass.SCREEN_POST_GAME_DIALOG_NORMAL:
                            objGameSummary.loadSummaryScreen();
                            dialog.dismiss();
                            mContext.CollectGarbage();
                            break;
                    }
                }
                return false;
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        View v2 =  mContext.CurrentView;
        lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        view.findViewById(R.id.btnRestart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGameData();
                dialog.dismiss();
                if (BoardType == HelperClass.THREE_BOARD) {
                    RowSize /= 3;
                }
                createGame();
                mContext.CollectGarbage();
            }
        });
        view.findViewById(R.id.btnGameSummary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objGameSummary.loadSummaryScreen();
                dialog.dismiss();
                mContext.CollectGarbage();
            }
        });

        dialog.show();
    }
    private void createSummaryMessage(View v)
    {
        String data[]=new String[12];
        View region_stars = v.findViewById(R.id.region_stars);

        //region Section1 : general game msg
        if(PlayerMode == HelperClass.ONE_PLAYER)
        {
            computeStarsFor1PlayerGame(region_stars);
            data[0] = "Time = " + String.valueOf(GameRunningTime + " Seconds");
            data[1] = "Total moves = "+ String.valueOf(Player1_Moves);

            ((TextView)v.findViewById(R.id.tvHeader_1)).setText("Level Complete!");
        }
        else
        {
            computeStarsFor2PlayerGame(region_stars);
            if(PlayerOne_Score>PlayerTwo_Score)
            {
                data[0] = mContext.playerOneName+" wins";
                data[1]= "Score = " + String.valueOf(PlayerOne_Score) + " : " + String.valueOf(PlayerTwo_Score);
                ((TextView)v.findViewById(R.id.tvHeader_1)).setText("Yay!");
            }
            else if(PlayerOne_Score<PlayerTwo_Score)
            {
                data[0] = getPlayer2_Name() +" wins";
                data[1] = "Score = " + String.valueOf(PlayerTwo_Score) + " : " + String.valueOf(PlayerOne_Score);
                ((TextView)v.findViewById(R.id.tvHeader_1)).setText("Oh No!");
            }
            else
            {
                data[0] = "It's a tie!";
                data[1] = "Score = " + String.valueOf(PlayerOne_Score) + " : " + String.valueOf(PlayerTwo_Score);
                ((TextView)v.findViewById(R.id.tvHeader_1)).setText("Ah!");
            }

        }

        ((TextView)v.findViewById(R.id.tv1)).setText(data[0]);
        ((TextView)v.findViewById(R.id.tv2)).setText(data[1]);
        //endregion

        //region Section 2 : High Score related
        long prev_userScore;
        if(powUsed)
            prev_userScore=9999999999l;
        else
            prev_userScore= objGameSummary.getHighestScore();
        long score = objGameSummary.Score;
        long defaultMaxHighScore = TotalCardsOnBoard * 5 * 3;
        long defaultMinHighScore = TotalCardsOnBoard * 4;
        if(score>prev_userScore && score>defaultMaxHighScore)
        {
            data[2]= "**New High Score**";
            data[3]="Nailed it!";

            v.findViewById(R.id.region_row3).setVisibility(View.VISIBLE);
            v.findViewById(R.id.region_row4).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.tv3)).setText(data[2]);
            ((TextView)v.findViewById(R.id.tv4)).setText(data[3]);
        }
        else if(score>prev_userScore && score>defaultMinHighScore)
        {
            data[2]="**Personal Best Score**";
            data[3]="Great job!";

            v.findViewById(R.id.region_row3).setVisibility(View.VISIBLE);
            v.findViewById(R.id.region_row4).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.tv3)).setText(data[2]);
            ((TextView)v.findViewById(R.id.tv4)).setText(data[3]);
        }
        //endregion

        //region Section 3 : Challenge related
        int extraCoins = 0;
        if(isChallengeGame)
        {

            v.findViewById(R.id.region_row5).setVisibility(View.VISIBLE);
            boolean challengeWon = false;
            if(PlayerMode == HelperClass.ONE_PLAYER)
            {
                int board_size = (int)(RowSize*ColumnSize*1.2f);
                if(BoardType == HelperClass.THREE_BOARD)
                {
                    board_size/=3;
                    board_size*=1.6f;
                }
                int max_moves = board_size;
                int totalMoves = Player1_Moves;
                if(totalMoves<=max_moves)
                {
                    challengeWon=true;
                    data[5]= "Congrats!";
                    data[6]= "Moves made : " + String.valueOf(totalMoves);
                    data[7]= "Challenge max limit : " +String.valueOf(max_moves);

                    v.findViewById(R.id.region_row6).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.region_row7).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.region_row8).setVisibility(View.VISIBLE);
                    ((TextView)v.findViewById(R.id.tv6)).setText(data[5]);
                    ((TextView)v.findViewById(R.id.tv7)).setText(data[6]);
                    ((TextView)v.findViewById(R.id.tv8)).setText(data[7]);
                }
                else
                {
                    data[5]= "You failed to finish within " + String.valueOf(max_moves) +" moves.";

                    v.findViewById(R.id.region_row6).setVisibility(View.VISIBLE);
                    ((TextView)v.findViewById(R.id.tv6)).setText(data[5]);
                }
            }
            else
            {
                if(PlayerOne_Score>PlayerTwo_Score)
                {
                    challengeWon=true;
                    data[5]= "Congrats!";
                    data[6]="You have defeated " + mContext.get_text(PlayerTwoType);

                    v.findViewById(R.id.region_row6).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.region_row7).setVisibility(View.VISIBLE);
                    ((TextView)v.findViewById(R.id.tv6)).setText(data[5]);
                    ((TextView)v.findViewById(R.id.tv7)).setText(data[6]);
                }
                else if (PlayerOne_Score ==PlayerTwo_Score)
                {
                    data[5]= "It was a good game. Better luck next time.";

                    v.findViewById(R.id.region_row6).setVisibility(View.VISIBLE);
                    ((TextView)v.findViewById(R.id.tv6)).setText(data[5]);
                }
                else
                {
                    data[5]= "You need more practice.";

                    v.findViewById(R.id.region_row6).setVisibility(View.VISIBLE);
                    ((TextView)v.findViewById(R.id.tv6)).setText(data[5]);
                }
            }
            if(challengeWon)
            {
                data[8]="Reward = "+String.valueOf(ChallengeReward)+" extra coins!";

                v.findViewById(R.id.region_row9).setVisibility(View.VISIBLE);
                ((TextView)v.findViewById(R.id.tv9)).setText(data[8]);

                extraCoins = ChallengeReward;
            }
        }
        //endregion

        objGameSummary.writeCoinsToPreferences(extraCoins);
        isChallengeGame = false;
        ChallengeReward = 0;
    }
    private void computeStarsFor1PlayerGame(View regionStars)
    {
        int moves = mContext.objCardGame.Player1_Moves;
        int minLimit = mContext.objCardGame.RowSize*mContext.objCardGame.ColumnSize;
        minLimit = (minLimit/2) + (minLimit/3);
        int maxLimit = minLimit*4;
        int totalMiniStars = Math.round((float) 14 * (maxLimit - moves) / (maxLimit - minLimit) + 1);
        totalMiniStars+=1;
        createStarts(totalMiniStars, regionStars);
    }
    private void computeStarsFor2PlayerGame(View regionStars)
    {
        int maxHits = mContext.objCardGame.RowSize*mContext.objCardGame.ColumnSize/3;
        int minHits = 0;
        int playerOneHits = mContext.objCardGame.PlayerOne_Score;
        int totalMiniStars = Math.round((float) 14 * (playerOneHits - minHits) / (maxHits - minHits) + 1);
        createStarts(totalMiniStars, regionStars);
    }
    private void createStarts(int totalMiniStars,View regionStars)
    {
        if(totalMiniStars==0)
        {
            regionStars.setVisibility(View.GONE);
            return;
        }
        if(totalMiniStars>15)
            totalMiniStars=15;
        while (totalMiniStars>2)
        {
            ((LinearLayout)regionStars).addView(getStar(R.drawable.img_star_full));
            totalMiniStars-=3;
        }
        if(totalMiniStars==2)
            ((LinearLayout)regionStars).addView(getStar(R.drawable.img_star_two_third));
        else if (totalMiniStars==1)
            ((LinearLayout)regionStars).addView(getStar(R.drawable.img_star_one_third));
    }
    private View getStar(int drawableID)
    {
        RelativeLayout rl = new RelativeLayout(mContext);
        LinearLayout.LayoutParams l_params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1f);
        rl.setLayoutParams(l_params);

        TextView tv = new TextView(mContext);
        RelativeLayout.LayoutParams r_params = new RelativeLayout.LayoutParams(
                HelperClass.ConvertToPx(mContext,40),
                HelperClass.ConvertToPx(mContext,40)
        );
        r_params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        tv.setLayoutParams(r_params);
        tv.setBackgroundResource(drawableID);
        rl.addView(tv);

        return rl;
    }
    //endregion

    //Semaphores! To synchronize threads
    public void AcquireLOCK()
    {
        try {
            while(WAIT_LOCK.availablePermits() == 0);//WAIT
            WAIT_LOCK.acquire();
        }catch (Exception e)
        {
            Toast.makeText(mContext,"Oops. I am semaphore error.",Toast.LENGTH_SHORT).show();
        }

    }
    public void ReleaseLOCK()
    {
        try {
        WAIT_LOCK.release();
        }catch (Exception e)
        { Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show(); }
    }

    //Clears all variables & calls gc()
    public void Clear()
    {
        objGameSummary = null;
        Cards_ImageResID = null;
        GameTimer = null;
        IV_AllCards = null;
        TV_GameDataInfo = null;
        GameBoard = null;
        CardClick_Listener = null;
        Flip_anim = null;
        CurrentCard = null;
        DefaultFlipListener = null;
        DestroyedCards = null;
        Card_Clicks = null;
        CardLastClicked = null;
        CardRetainingPower = null;
        //mContext = null;
        Btn_Power = null;
        robotPlayer = null;
        objTimeTrail = null;
        objPower = null;
        MatchingCard_Map = null;
        CardAttempt_Map = null;

//        WAIT_LOCK.release();
        WAIT_LOCK=null;
        sp= null;

        mContext.CollectGarbage();
        IsConsecutiveHit = false;
        PlayerOne_Turn = false;
        Player1_Moves = 0;
        TotalMoves=0;
        maxHitStreak = 0;
        currentHitStreak = 0;
        DestroyedCards_Top = 0;
        EffectiveClickCount = 0;
        ActualClickCount = 0;
        PlayerOne_Score = 0;
        PlayerTwo_Score = 0;
        GameRunningTime = 0;
        PlayerMode = 0;
        GameMode = 0;
        BoardType = 0;
        ScrollType = 0;
        BoardIdentifier = 0;
        RowSize = 0;
        ColumnSize = 0;
        NearMisses = 0;
        clickAdjustment_destroyedCards = 0;
    }

    //Clears all variables & calls gc() : Used after scope of screen is over
    public void CleanUp()
    {
        if(GameTimer!=null)
            GameTimer.cancel();
        if(objTimeTrail!=null)
            if(objTimeTrail.TimeTrialTimer!=null)
            {
                objTimeTrail.TimeTrialTimer.cancel();
                objTimeTrail.TimeTrialTimer=null;
            }
        GameTimer=null;
        if(Flip_anim!=null)
            Flip_anim.setAnimationListener(null);
        Flip_anim=null;
        objGameSummary=null;
        Cards_ImageResID = null;
        GameTimer = null;
        IV_AllCards = null;
        TV_GameDataInfo = null;
        GameBoard = null;
        CardClick_Listener = null;
        Flip_anim = null;
        CurrentCard = null;
        DefaultFlipListener = null;
        DestroyedCards = null;
        Card_Clicks = null;
        CardLastClicked = null;
        Matches=null;
        CardRetainingPower = null;
        Btn_Power = null;
        robotPlayer = null;
        objTimeTrail = null;
        MatchingCard_Map = null;
        CardAttempt_Map = null;
        objPower = null;

        WAIT_LOCK.release();
        WAIT_LOCK=null;
        sp.release();
        sp= null;

        mContext.CollectGarbage();
        IsConsecutiveHit = false;
        PlayerOne_Turn = false;
        Player1_Moves = 0;
        maxHitStreak = 0;
        currentHitStreak = 0;
        DestroyedCards_Top = 0;
        EffectiveClickCount = 0;
        ActualClickCount = 0;
        PlayerOne_Score = 0;
        PlayerTwo_Score = 0;
        GameRunningTime = 0;
        PlayerMode = 0;
        GameMode = 0;
        BoardType = 0;
        ScrollType = 0;
        BoardIdentifier = 0;
        RowSize = 0;
        ColumnSize = 0;
        clickAdjustment_destroyedCards = 0;
    }

}
