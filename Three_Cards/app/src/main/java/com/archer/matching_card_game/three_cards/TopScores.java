package com.archer.matching_card_game.three_cards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ANDROBOT;
import static com.archer.matching_card_game.three_cards.HelperClass.ARCADE;
import static com.archer.matching_card_game.three_cards.HelperClass.BOARD_TYPE;
import static com.archer.matching_card_game.three_cards.HelperClass.BOTH;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_1;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_2;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_3;
import static com.archer.matching_card_game.three_cards.HelperClass.COLUMN_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.ConvertToPx;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER_2;
import static com.archer.matching_card_game.three_cards.HelperClass.GAME_MODE;
import static com.archer.matching_card_game.three_cards.HelperClass.HORIZONTAL;
import static com.archer.matching_card_game.three_cards.HelperClass.HURRICANE;
import static com.archer.matching_card_game.three_cards.HelperClass.MANUAL;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_COL_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_ROW_SIZE_1B;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_ROW_SIZE_3B;
import static com.archer.matching_card_game.three_cards.HelperClass.NO_SCROLL;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_MODE;
import static com.archer.matching_card_game.three_cards.HelperClass.RANDOM_BOT;
import static com.archer.matching_card_game.three_cards.HelperClass.ROBOT_MEMORY;
import static com.archer.matching_card_game.three_cards.HelperClass.ROBOT_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.ROCK;
import static com.archer.matching_card_game.three_cards.HelperClass.ROW_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.SCROLL_TYPE;
import static com.archer.matching_card_game.three_cards.HelperClass.SetFontToControls;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL_TIMER;
import static com.archer.matching_card_game.three_cards.HelperClass.THREE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.TWO_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.VERTICAL;

public class TopScores implements View.OnClickListener {

    final int SUMMARY_SCREEN = 3;
    final int TOP_SCORES = 0;
    final int BEST_TIME = 1;
    final int LEAST_MOVES = 2;
    Game CurrentGame;
    MainActivity mContext;
    boolean isFromGameScreen;
    View.OnClickListener Process_Input;
    String defaultPlayerNames[] = { "Andro-Bot","Hurricane","Rock","Rock","Hurricane" };
    int defaultScores[] = new int[5];
    TextView PlayerNames[] = new TextView[5];
    TextView PlayerScore[] = new TextView[5];
    //Selected Values
    int GameMode;
    int TimeTrialTimerValue;
    int PlayerMode;
    int PlayerType;
    int RobotMemory;
    int BoardType;
    int ScrollType;
    int CardSet;
    int RowSize;
    int ColumnSize;
    int [] SCREENS = {TOP_SCORES,BEST_TIME,LEAST_MOVES,SUMMARY_SCREEN};
    int current_screen_index = TOP_SCORES;

    public TopScores(WeakReference<Game> currentGame,boolean is_fromGameScreen)
    {
        isFromGameScreen = is_fromGameScreen;
        CurrentGame = currentGame.get();
        mContext = CurrentGame.mContext;
        InitializeDialogInputListener();
    }
    public TopScores(WeakReference<MainActivity> m_context)
    {
        isFromGameScreen = false;
        mContext = m_context.get();
        InitializeDialogInputListener();
    }
    public void InitializeBoardDetails(int gameMode,int playerMode,int playerType,
                                       int robotMemory, int boardType,int scrollType,
                                       int cardSet,int rowSize,int columnSize,
                                       int timeTrialTimerValue)
    {
        if(isFromGameScreen && boardType==THREE_BOARD)
            rowSize/=3;

        GameMode = gameMode;
        PlayerMode = playerMode;
        PlayerType = playerType;
        RobotMemory = robotMemory;
        BoardType = boardType;
        ScrollType = scrollType;
        CardSet = cardSet;
        RowSize = rowSize;
        ColumnSize = columnSize;
        TimeTrialTimerValue = timeTrialTimerValue;
    }

    public void Show( )
    {
        View this_view = mContext.loadView(R.layout.screen_top_score);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
        SetFontToControls(font, (ViewGroup) this_view);

        addListenerToControls();
        InitializeRobotMemoryListener();

        setBoardDetailsText();
        if(!isFromGameScreen)
            initializeSpecificControls_Set2();

        addFlingListenerToTopScoresScreen(this_view);
        loadPage();

        //region Check if Ad-Free version
        if(!mContext.adFreeVersion)
        {
            final AdView mAdView = (AdView) this_view.findViewById(R.id.adView);
            mAdView.loadAd(mContext.AdRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }
        //endregion
    }


    //region Listeners

    private void initializeSpecificControls_Set2()
    {
        View btnBack = (mContext.findViewById(R.id.btnBack));

        mContext.findViewById(R.id.btnExit).setVisibility(View.INVISIBLE);
        mContext.findViewById(R.id.btnStore).setVisibility(View.INVISIBLE);

        btnBack.setVisibility(View.VISIBLE);
    }

    private void addListenerToControls()
    {

        View btn_prev_page = mContext.findViewById(R.id.btn_prev_page);
        View btn_next_page = mContext.findViewById(R.id.btn_next_page);

        //buttons
        View GameMode = mContext.findViewById(R.id.GameMode);
        View PlayerMode = mContext.findViewById(R.id.PlayerMode);
        View BoardType = mContext.findViewById(R.id.BoardType);
        View ScrollType = mContext.findViewById(R.id.ScrollType);
        View CardSet = mContext.findViewById(R.id.CardSet);
        TextView RowSize = (TextView)mContext.findViewById(R.id.RowSize);
        TextView ColSize = (TextView)mContext.findViewById(R.id.ColSize);

        //Edit buttons
        View btnGameMode = mContext.findViewById(R.id.btnGameMode);
        View btnPlayerMode = mContext.findViewById(R.id.btnPlayerMode);
        View btnBoardType = mContext.findViewById(R.id.btnBoardType);
        View btnScrollType = mContext.findViewById(R.id.btnScrollType);
        View btnCardSet = mContext.findViewById(R.id.btnCardSet);
        View btnBoardSize = mContext.findViewById(R.id.btnBoardSize);

        View btnShareScore = mContext.findViewById(R.id.btnShareScore);
        View btnResetScores = mContext.findViewById(R.id.btnResetScores);

        View btnStore = mContext.findViewById(R.id.btnStore);
        View btn_store = mContext.findViewById(R.id.btn_store);
        View btnExit = mContext.findViewById(R.id.btnExit);
        View btn_Exit = mContext.findViewById(R.id.btn_exit);

        btnShareScore.setOnClickListener(this);
        btnResetScores.setOnClickListener(this);
        btn_next_page.setOnClickListener(this);
        btn_prev_page.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btn_Exit.setOnClickListener(this);
        btnStore.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        //buttons
        GameMode.setOnClickListener(this);
        PlayerMode.setOnClickListener(this);
        BoardType.setOnClickListener(this);
        CardSet.setOnClickListener(this);
        ScrollType.setOnClickListener(this);
        RowSize.setOnClickListener(this);
        ColSize.setOnClickListener(this);

        //Edit buttons
        btnGameMode.setOnClickListener(this);
        btnPlayerMode.setOnClickListener(this);
        btnBoardType.setOnClickListener(this);
        btnCardSet.setOnClickListener(this);
        btnScrollType.setOnClickListener(this);
        btnBoardSize.setOnClickListener(this);
    }
    private void InitializeRobotMemoryListener()
    {
        final SeekBar robotMemory = (SeekBar)mContext.findViewById(R.id.RobotMemory);
        robotMemory.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                RobotMemory = seekBar.getProgress() + 1;
                loadPage();
            }
        });
    }

    private void addFlingListenerToTopScoresScreen(View v)
    {
        final GestureDetector gdt = new GestureDetector(mContext,new GestureListener());
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnExit:
            case R.id.btn_exit:
                mContext.loadView(R.layout.screen_home);
                CurrentGame.CleanUp();
                break;
            case R.id.btn_prev_page:
                decrementScreenIndex();
                break;
            case R.id.btn_next_page:
                incrementScreenIndex();
                break;
            case R.id.btnStore:
            case R.id.btn_store:
                mContext.loadStoreScreen();
                CurrentGame.CleanUp();
                break;
            case R.id.btnGameMode:
            case R.id.GameMode:
                displayDialog(getGameModes(), true);
                break;
            case R.id.btnPlayerMode:
            case R.id.PlayerMode:
                displayDialog(getPlayerModes(),true);
                break;
            case R.id.btnBoardType:
            case R.id.BoardType:
                displayDialog(getBoardType(),true);
                break;
            case R.id.btnBoardSize:
            case R.id.RowSize:
            case R.id.ColSize:
                displayDialog(getRowSize(),true);
                break;
            case R.id.btnScrollType:
            case R.id.ScrollType:
                displayDialog(getScrollType(),true);
                break;
            case R.id.btnCardSet:
            case R.id.CardSet:
                displayDialog(getCardSet(),true);
                break;
            case R.id.btnShareScore:
            {
                String msg = "Check out my scores." + "\n" +
                        "https://play.google.com/store/apps/details?id=com.archer.matching_card_game.three_cards";
                mContext.takeScreenShotAndShare(msg);
            }
                break;
            case R.id.btnResetScores:
                resetScores();
                break;
            case R.id.tv_leader_board_custom_game:
            case R.id.btn_leader_board_custom_game:
            {
                int id[] = getGameIdentifierForLeaderBoard();
                mContext.onShowLeaderboardsRequested(id[0], id[1]);
            }
            break;
        }
    }

    private void InitializeDialogInputListener() {
        Process_Input = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyValue = v.getTag().toString();
                int key = Integer.parseInt(keyValue.split(DELIMITER_2)[0]);
                int value = Integer.parseInt(keyValue.split(DELIMITER_2)[1]);
                switch (key)
                {
                    case GAME_MODE:
                        GameMode = value;
                        if(GameMode == TIME_TRIAL)
                        {
                            mContext.CommonDialog.dismiss();
                            displayDialog(getTimeTrialTime(),false);
                            return;
                        }
                        break;

                    case TIME_TRIAL_TIMER:
                        TimeTrialTimerValue = value;
                        break;
                    case CARD_SET:
                        CardSet = value;
                        break;

                    case PLAYER_MODE:
                        switch (value)
                        {
                            case 0:
                                PlayerMode = ONE_PLAYER;
                                PlayerType = 0;
                                break;
                            case 1:
                                PlayerMode = TWO_PLAYER;
                                PlayerType = MANUAL;
                                break;
                            case 2:
                                PlayerMode = ROBOT_PLAYER;
                                PlayerType = ANDROBOT;
                                break;
                            case 3:
                                PlayerMode = ROBOT_PLAYER;
                                PlayerType = HURRICANE;
                                break;
                            case 4:
                                PlayerMode = ROBOT_PLAYER;
                                PlayerType = ROCK;
                                break;
                            case 5:
                                PlayerMode = ROBOT_PLAYER;
                                PlayerType = RANDOM_BOT;
                                break;
                        }
                        if(PlayerMode!=ONE_PLAYER)
                            mContext.findViewById(R.id.tvMsg).setVisibility(View.VISIBLE);
                        else
                            mContext.findViewById(R.id.tvMsg).setVisibility(View.GONE);
                        break;
                    case BOARD_TYPE:
                        BoardType = value;
                        switch (BoardType)
                        {
                            case ONE_BOARD:
                                RowSize = Math.max(ColumnSize, RowSize);
                                break;
                            case THREE_BOARD:
                                RowSize = Math.min(RowSize,MAX_ROW_SIZE_3B);
                                break;
                        }
                        break;

                    case ROW_SIZE:
                        RowSize = value;
                        mContext.CommonDialog.dismiss();
                        displayDialog(getColSize(),false);
                        return;
                    case COLUMN_SIZE:
                        ColumnSize = value;
                        break;
                    case SCROLL_TYPE:
                        ScrollType = value;
                        break;

                    default:
                        Toast.makeText(mContext,"Oops. I am unknown error 3",Toast.LENGTH_SHORT).show();
                }
                mContext.CommonDialog.dismiss();
                setBoardDetailsText();
                loadPage();
            }
        };

    }

    //endregion


    //region dialog logic

    private void displayDialog(View v,boolean isCancelable)
    {
        if(mContext.CommonDialog==null)
            mContext.CommonDialog  = new AlertDialog.Builder(mContext).show();
        else
            mContext.CommonDialog.show();
        mContext.CommonDialog.setContentView(v);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
        SetFontToControls(font, (ViewGroup) v);
        mContext.CommonDialog.setCancelable(isCancelable);
    }

    private View getGameModes()
    {
        String titleText = "Select Game Mode";
        String text[] =  {"Arcade","Time-Trial"};
        int tag [] =  {ARCADE, TIME_TRIAL};
        return addToMainContainer(tag,text,String.valueOf(GAME_MODE),tag.length, titleText);
    }

    private View getPlayerModes()
    {
        String text[] =  {"1P", "2P - Manual","2P - AndroBot","2P - Hurricane","2P - Rocky","2P - Random-Bot"};
        int tag [] =  {0, 1,2,3,4,5};
        return addToMainContainer(tag,text,String.valueOf(PLAYER_MODE),tag.length,"Select Player Mode");
    }

    private View getRobotMemory()
    {
        String text[] =  {"1","2","3","4","5","6","7","8","9","10"};
        int tag [] =  {1,2,3,4,5,6,7,8,9,10};
        return addToMainContainer(tag, text, String.valueOf(ROBOT_MEMORY), tag.length, "Select Robot Memory");
    }

    private View getBoardType()
    {
        String text[] =  {"One-Board","Two-Board"};
        int tag [] =  {ONE_BOARD, THREE_BOARD};
        return addToMainContainer(tag, text, String.valueOf(BOARD_TYPE), tag.length, "Select Board Type");
    }

    private View getTimeTrialTime()
    {
        String text[] =  new String[27];
        int tag [] =  new int[27];
        for(int i = 4;i<=30;i++)
        {
            text[i-4] = String.valueOf(i) + " S";
            tag [i-4] = i*1000;
        }

        return addToBoardSizeContainer(tag, text, String.valueOf(TIME_TRIAL_TIMER), tag.length, "Time Trail Time");
    }

    private View getColSize()
    {
        int size;
        if(BoardType == ONE_BOARD)
            size = Math.min(RowSize, MAX_COL_SIZE);
        else
            size = Math.min(2*RowSize,MAX_COL_SIZE);

        int i =2;
        String text[] =  new String [size];
        int tag [] =  new int[size];
        while (i<=size)
        {
            tag[i-2] = i;
            text[i-2] = String.valueOf(i);
            i++;
        }
        return addToBoardSizeContainer(tag, text, String.valueOf(COLUMN_SIZE), i - 2, "Select Column Size");
    }

    private View getRowSize()
    {
        int size;
        if(BoardType == ONE_BOARD)
            size = MAX_ROW_SIZE_1B;
        else
            size = MAX_ROW_SIZE_3B;

        String text[] =  new String[size];
        int tag [] =  new int[size];
        int i = 2;
        while (i<=size)
        {
            tag[i-2]=i;
            text[i-2]=String.valueOf(i);
            i++;
        }
        return addToBoardSizeContainer(tag, text, String.valueOf(ROW_SIZE), i - 2, "Select Row Size");
    }

    private View getBoardSize()
    {
        return getRowSize();
    }

    private View getScrollType()
    {
        String text[] =  {"No Scroll","Vertical Scroll","Horizontal Scroll","Both Scroll"};
        int tag [] =  {NO_SCROLL, VERTICAL,HORIZONTAL,BOTH};
        return addToMainContainer(tag, text, String.valueOf(SCROLL_TYPE), tag.length, "Select Scroll Type");
    }

    private View getCardSet()
    {
        String text[] =  {"I","II","III"};
        int tag [] =  {CARD_SET_1,CARD_SET_2,CARD_SET_3};
        return addToMainContainer(tag, text, String.valueOf(CARD_SET), tag.length, "Select Card Set");
    }

    private View addToMainContainer(int[] tag, String[] text,String identifier,int length,String titleText) {
        LinearLayout mainContainer = new LinearLayout(mContext);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.addView(getTitleTextView(titleText));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConvertToPx(mContext,190),
                ConvertToPx(mContext, 45));
        RelativeLayout.LayoutParams rl_params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                1);
        for (int i=0;i<length;i++)
        {
            TextView tv = new Button(mContext);
            tv.setText(text[i]);
            String tvTag = identifier + DELIMITER_2 + String.valueOf(tag[i]);
            tv.setTag(tvTag);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.argb(180, 255, 255, 255));
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0,ConvertToPx(mContext,3),0,0);
            tv.setLayoutParams(layoutParams);
            mainContainer.addView(tv);
            tv.setOnClickListener(Process_Input);

            mainContainer.addView(getDivider(rl_params));
        }
        return mainContainer;
    }

    private View addToBoardSizeContainer(int[] tag, String[] text,String identifier,int length,String titleText) {
        LinearLayout row = null;
        LinearLayout mainContainer = new LinearLayout(mContext);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.addView(getTitleTextView(titleText));
        int i=0;
        LinearLayout.LayoutParams row_Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams box_Params = new LinearLayout.LayoutParams(ConvertToPx(mContext,72),
                ConvertToPx(mContext,40));
        RelativeLayout.LayoutParams verticalDivider_params= new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        RelativeLayout.LayoutParams horizontalDivider_params= new RelativeLayout.LayoutParams(1,
                ViewGroup.LayoutParams.MATCH_PARENT);
        while(i<length)
        {
            row = new LinearLayout(mContext);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(row_Params);
            mainContainer.addView(row);
            mainContainer.addView(getDivider(verticalDivider_params));
            for(int col=0;col<3 && i<length;col++,i++)
            {
                Button tv = new Button(mContext);
                tv.setText(text[i]);
                String tvTag = identifier + DELIMITER_2 + String.valueOf(tag[i]);
                tv.setTag(tvTag);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(0, ConvertToPx(mContext, 3), 0, 0);
                tv.setBackgroundColor(Color.argb(180, 255, 255, 255));
                tv.setGravity(Gravity.CENTER);
                tv.setLayoutParams(box_Params);
                tv.setOnClickListener(Process_Input);
                row.addView(tv);
                row.addView(getDivider(horizontalDivider_params));
            }
        }
        while (i%3 != 0)
        {
            Button tv = new Button(mContext);
            tv.setLayoutParams(box_Params);
            tv.setPadding(0, ConvertToPx(mContext, 3), 0, 0);
            tv.setBackgroundColor(Color.argb(180, 255, 255, 255));
            row.addView(tv);
            row.addView(getDivider(horizontalDivider_params));
            i++;
        }

        return mainContainer;
    }

    private View getDivider(RelativeLayout.LayoutParams rl_params)
    {
        RelativeLayout divider = new RelativeLayout(mContext);
        divider.setLayoutParams(rl_params);
        divider.setBackgroundColor(Color.BLACK);
        return divider;
    }

    private View getTitleTextView(String titleText)
    {
        int five_dip = ConvertToPx(mContext, 5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tvTitle = new TextView(mContext);
        tvTitle.setText(titleText);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setBackgroundColor(Color.BLACK);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setPadding(4 * five_dip, 2 * five_dip, 4 * five_dip, 2 * five_dip);
        return tvTitle;
    }

    public void loadSummaryScreen()
    {
        if(CurrentGame.objGameSummary == null)
            CurrentGame.objGameSummary = new GameSummary(new WeakReference<>(CurrentGame),
                    CurrentGame.CurrentCard.getMeasuredHeight(),CurrentGame.CurrentCard.getMeasuredWidth());
        CurrentGame.objGameSummary.loadSummaryScreen();
    }

//endregion area

    private void loadTopScoresScreen() {
        ((TextView)mContext.findViewById(R.id.tvTitle)).setText("Top Scores");
        ((TextView)mContext.findViewById(R.id.tvSubTitle)).setText("Top Scores");
        String text = LoadScores();
        ((TextView) mContext.findViewById(R.id.tvUserScore)).setText("Your top score   =   " + text);

        mContext.findViewById(R.id.tvMsg).setVisibility(View.GONE);
    }

    private void loadBestTimeScreen() {
        ((TextView)mContext.findViewById(R.id.tvTitle)).setText("Best Time");
        ((TextView)mContext.findViewById(R.id.tvSubTitle)).setText("Best Time");
        String text = LoadScores();
        if(text.equals("99999999999"))
            ((TextView) mContext.findViewById(R.id.tvUserScore)).setText(" ");
        else
            ((TextView) mContext.findViewById(R.id.tvUserScore)).setText("Your best time   =   " + text);

        if(PlayerMode!=ONE_PLAYER)
            mContext.findViewById(R.id.tvMsg).setVisibility(View.VISIBLE);
        else
            mContext.findViewById(R.id.tvMsg).setVisibility(View.GONE);
    }

    private void loadLeastMovesScreen() {
        if(mContext.findViewById(R.id.tvTitle)== null)
            return;
        ((TextView)mContext.findViewById(R.id.tvTitle)).setText("Least Moves");

        if(mContext.findViewById(R.id.tvSubTitle)== null)
            return;
        ((TextView)mContext.findViewById(R.id.tvSubTitle)).setText("Least Moves");
        String text = LoadScores();

        if(mContext.findViewById(R.id.tvUserScore)== null)
            return;
        if(text.equals("99999999999"))
            ((TextView) mContext.findViewById(R.id.tvUserScore)).setText(" ");
        else
            ((TextView) mContext.findViewById(R.id.tvUserScore)).setText("Your Least moves   =   " + text);


        if(mContext.findViewById(R.id.tvMsg)== null)
            return;
        if(PlayerMode!=ONE_PLAYER)
            mContext.findViewById(R.id.tvMsg).setVisibility(View.VISIBLE);
        else
            mContext.findViewById(R.id.tvMsg).setVisibility(View.GONE);
    }

    private void incrementScreenIndex()
    {

        current_screen_index++;
        if(current_screen_index>=SCREENS.length)
        {
            current_screen_index=0;
        }
        else if(SCREENS[current_screen_index] == SUMMARY_SCREEN && !isFromGameScreen)
        {
            current_screen_index = 0;
        }

        loadPage();
    }

    private void decrementScreenIndex()
    {

        current_screen_index--;
        if(current_screen_index<0)
        {
            current_screen_index=SCREENS.length-1;
            if(!isFromGameScreen)
                current_screen_index--;
        }

        loadPage();
    }

    private void loadPage()
    {
        switch (SCREENS[current_screen_index])
        {
            case SUMMARY_SCREEN:
                loadSummaryScreen();
                break;
            case TOP_SCORES:
                loadTopScoresScreen();
                break;
            case BEST_TIME:
                loadBestTimeScreen();
                break;
            case LEAST_MOVES:
                loadLeastMovesScreen();
                break;
        }


        if(mContext.isSignedIn() && SCREENS[current_screen_index]==TOP_SCORES)
        {
            int id[] = getGameIdentifierForLeaderBoard();
            if(id[0] > -1)
            {
                if(mContext.findViewById(R.id.region_leader_board_custom_game)==null)
                    return;

                mContext.findViewById(R.id.region_leader_board_custom_game).setVisibility(View.VISIBLE);
                mContext.findViewById(R.id.tv_leader_board_custom_game).setOnClickListener(this);
                mContext.findViewById(R.id.btn_leader_board_custom_game).setOnClickListener(this);
            }
            else
            {
//                mContext.findViewById(R.id.region_leader_board_custom_game).setVisibility(View.GONE);
                if(mContext.findViewById(R.id.region_leader_board_custom_game) == null)
                    return;

                View v = mContext.findViewById(R.id.region_leader_board_custom_game);
                ((ViewGroup)(v.getParent())).removeView(v);
            }
        }
        else
        {
            if(mContext.findViewById(R.id.region_leader_board_custom_game)!=null)
                mContext.findViewById(R.id.region_leader_board_custom_game).setVisibility(View.GONE);
        }

    }

    public void mergeScores(String targetNameList[],long targetScoreList[],long userScores[])
    {
        int sortLogic = -1;
        if(SCREENS[current_screen_index] == TOP_SCORES)
            sortLogic*=-1;

        int defList_counter=0;
        int userList_counter=0;
        for(int i=0;i<5;i++)
        {
            if(defaultScores[defList_counter] * sortLogic >= userScores[userList_counter] * sortLogic)
            {
                targetNameList[i] = defaultPlayerNames[defList_counter];
                targetScoreList[i] = defaultScores[defList_counter++];
            }
            else
            {
                targetNameList[i] = "YOU";
                targetScoreList[i] = userScores[userList_counter++];
            }
        }
    }

    public String LoadScores()
    {
        createDefaultScores();
        PlayerNames[0] = (TextView) mContext.findViewById(R.id.tvPlayerName_1);
        PlayerNames[1] = (TextView) mContext.findViewById(R.id.tvPlayerName_2);
        PlayerNames[2] = (TextView) mContext.findViewById(R.id.tvPlayerName_3);
        PlayerNames[3] = (TextView) mContext.findViewById(R.id.tvPlayerName_4);
        PlayerNames[4] = (TextView) mContext.findViewById(R.id.tvPlayerName_5);

        PlayerScore[0] = (TextView) mContext.findViewById(R.id.tvPlayerScore_1);
        PlayerScore[1] = (TextView) mContext.findViewById(R.id.tvPlayerScore_2);
        PlayerScore[2] = (TextView) mContext.findViewById(R.id.tvPlayerScore_3);
        PlayerScore[3] = (TextView) mContext.findViewById(R.id.tvPlayerScore_4);
        PlayerScore[4] = (TextView) mContext.findViewById(R.id.tvPlayerScore_5);



        String playerNames[] = new String[5];

        long highScores [] = new long[5];
        long userHighScores[] = getHighScoresFromPreferences( );
        mergeScores(playerNames, highScores, userHighScores);
        String userHighScore = String.valueOf(userHighScores[0]);
        for (int i=0;i<5;i++)
        {
            PlayerNames[i].setText(String.valueOf(playerNames[i]));
            PlayerScore[i].setText(String.valueOf(highScores[i]));
        }
        return userHighScore;
    }

    public void createDefaultScores()
    {
        int totalCards = RowSize*ColumnSize;
        if(BoardType == THREE_BOARD)
            totalCards*=3;
        totalCards=totalCards/3*3;

        int maxScore,minScore,interval;
        switch (SCREENS[current_screen_index])
        {
            case TOP_SCORES:
                maxScore = totalCards * 5 * 3 ;
                minScore = totalCards * 4;
                interval = (maxScore-minScore)/4;
                break;
            case BEST_TIME:
                maxScore = (int)(totalCards * 2.7);
                minScore = maxScore * 6;
                interval = (maxScore-minScore)/4;
                break;
            case LEAST_MOVES:
                maxScore = (int)(totalCards * 0.7);
                minScore = (int)(maxScore * 2.7);
                interval = (maxScore-minScore)/4;
                break;
            default:
                maxScore = totalCards * 7 * 3 ;
                minScore = totalCards * 5;
                interval = (maxScore-minScore)/4;
        }


        defaultScores[0] = maxScore;
        for(int i = 1 ;i<5;i++)
        {
            defaultScores[i]=defaultScores[i-1]-interval;
        }
    }

    public long[] getHighScoresFromPreferences( )
    {
        String identifier = getBoardIdentifier();
        long []high_scores = new long[5];

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String defTopScore = "0~0~0~0~0";
        String temp = "99999999999";
        String defWorstTimes = "";
        for(int i = 0;i<5;i++)
            defWorstTimes+=temp+DELIMITER_2;
        String defMaxMoves = defWorstTimes;
        String scoring_data = preferences.getString(identifier, defTopScore+DELIMITER+defWorstTimes+DELIMITER+defMaxMoves);

        String highScores_arr[] = scoring_data.split(DELIMITER)[SCREENS[current_screen_index]].split(DELIMITER_2);
        for(int i=0;i<5;i++)
        {
            high_scores[i] = Long.parseLong(highScores_arr[i]);
        }
        return high_scores;
    }

    private void resetScores() {
        String msg = "Are you sure?\nSelect 'Yes' to continue.\nSelect 'No' to cancel.";
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Reset ?");
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(true);
        // alertDialog.setIcon(R.drawable.delete);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String identifier_scoreData = getBoardIdentifier();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                String defTopScore = "0~0~0~0~0";
                String temp = "99999999999";
                String defWorstTimes = "";
                for(int i = 0;i<5;i++)
                    defWorstTimes+=temp+DELIMITER_2;
                String defMaxMoves = defWorstTimes;
                String scoreData = preferences.getString(identifier_scoreData, defTopScore +
                        DELIMITER + defWorstTimes + DELIMITER + defMaxMoves);

                String allTopScores = scoreData.split(DELIMITER)[TOP_SCORES];
                String allBestTime = scoreData.split(DELIMITER)[BEST_TIME];
                String allLeastMove = scoreData.split(DELIMITER)[LEAST_MOVES];
                switch (SCREENS[current_screen_index])
                {
                    case TOP_SCORES:
                        allTopScores = "0~0~0~0~0";
                        break;
                    case BEST_TIME:
                        allBestTime = defWorstTimes;
                        break;
                    case LEAST_MOVES:
                        allLeastMove = defMaxMoves;
                        break;
                }

                scoreData = allTopScores+DELIMITER+allBestTime+DELIMITER+allLeastMove;
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(identifier_scoreData);
                editor.putString(identifier_scoreData, scoreData);
                editor.apply();

                ((TextView) mContext.findViewById(R.id.tvUserScore)).setText("");
                LoadScores();
                setBoardDetailsText();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void setBoardDetailsText()
    {
        String gameMode = getText(GameMode);
        if(GameMode == TIME_TRIAL)
        {
            gameMode+= " (" + String.valueOf(TimeTrialTimerValue/1000) + ")";
        }
        String playerType =getText(PlayerMode);
        if(PlayerMode != ONE_PLAYER)
        {
            playerType += " : " + getText(PlayerType);
        }
        String boardType = getText(BoardType);
        String scrollType = getText(ScrollType);
        String cardSet = getText(CardSet);
        String rowSize = String.valueOf(RowSize);
        String colSize = String.valueOf(ColumnSize);

        ((TextView) mContext.findViewById(R.id.GameMode)).setText(gameMode);
        ((TextView) mContext.findViewById(R.id.PlayerMode)).setText(playerType);
        ((TextView) mContext.findViewById(R.id.BoardType)).setText(boardType);
        ((TextView) mContext.findViewById(R.id.ScrollType)).setText(scrollType);
        ((TextView) mContext.findViewById(R.id.CardSet)).setText(cardSet);
        ((TextView) mContext.findViewById(R.id.RowSize)).setText(rowSize);
        ((TextView) mContext.findViewById(R.id.ColSize)).setText(colSize);
        SeekBar roboMemory = ((SeekBar) mContext.findViewById(R.id.RobotMemory));

        if(PlayerMode == TWO_PLAYER && PlayerType!= MANUAL)
            PlayerMode = ROBOT_PLAYER;

        if(PlayerMode != ROBOT_PLAYER)
            roboMemory.setEnabled(false);
        else
        {
            roboMemory.setEnabled(true);
            roboMemory.setProgress(RobotMemory - 1);
        }


    }

    public String getBoardIdentifier()
    {
        String identifier;
        int boardSize = ColumnSize*RowSize;
        if(BoardType == THREE_BOARD)
            boardSize*=3;
        identifier = String.valueOf(GameMode) + DELIMITER_2 +
                String.valueOf(PlayerMode) + DELIMITER_2 +
                String.valueOf(BoardType) + DELIMITER_2 +
                String.valueOf(CardSet) + DELIMITER_2 +
                String.valueOf(ScrollType) + DELIMITER_2 +
                String.valueOf(boardSize);

        if(GameMode == TIME_TRIAL)
            identifier+= DELIMITER_2 + String.valueOf(TimeTrialTimerValue);

        if(PlayerMode != ONE_PLAYER)
        {
            identifier+= String.valueOf(PlayerType);

            if(PlayerMode == ROBOT_PLAYER)
                identifier+= DELIMITER_2 + String.valueOf(RobotMemory);
        }

        return identifier;
    }

    public String getText(int identifier)
    {
        String text = "";
        switch (identifier)
        {
            case ARCADE:
                text = "ARCADE";
                break;
            case TIME_TRIAL:
                text = "Time-Trial";
                break;
            case ONE_PLAYER:
                text = "1P";
                break;
            case TWO_PLAYER:
                text = "2P";
                break;
            case ROBOT_PLAYER:
                text = "2P";
                break;
            case MANUAL:
                text = "MANUAL";
                break;
            case ANDROBOT:
                text = "ANDROBOT";
                break;
            case HURRICANE:
                text = "Hurricane";
                break;
            case ROCK:
                text = "Rock";
                break;
            case RANDOM_BOT:
                text = "Random Bot";
                break;
            case ONE_BOARD:
                text = "1 Board";
                break;
            case THREE_BOARD:
                text = "3 Board";
                break;
            case NO_SCROLL:
                text = "No Scroll";
                break;
            case VERTICAL:
                text = "Vertical";
                break;
            case HORIZONTAL:
                text = "Horizontal";
                break;
            case BOTH:
                text = "Both";
                break;
            case CARD_SET_1:
                text = "Card Set - I";
                break;
            case CARD_SET_2:
                text = "Card Set - II";
                break;
            case CARD_SET_3:
                text = "Card Set - III";
                break;
        }

        return text;
    }

    public int[] getGameIdentifierForLeaderBoard()
    {
        //region logic to store 48 top scores for standard board in 1P mode
        if(PlayerMode==ONE_PLAYER)
        {
            int id[][][][][] = new int[2][2][4][3][];
            //region id values
            id[0][0][0][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_NO_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p1b6x4no_scrollcard_set1};
            id[0][0][0][1] = new int[] {HelperClass.TOP_SCORE_ARC_1P_1B_NO_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p1b6x4no_scrollcard_set2};
            id[0][0][0][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_NO_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p1b6x4no_scrollcard_set3};
            id[0][0][1][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_H_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p1b6x4h_scrollcard_set1};
            id[0][0][1][1] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_H_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p1b6x4h_scrollcard_set2};
            id[0][0][1][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_H_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p1b6x4h_scrollcard_set3};
            id[0][0][2][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_V_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p1b7x4v_scrollcard_set1};
            id[0][0][2][1] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_V_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p1b7x4v_scrollcard_set2};
            id[0][0][2][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_V_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p1b7x4v_scrollcard_set3};
            id[0][0][3][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_BOTH_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p1b7x5hv_scrollcard_set1};
            id[0][0][3][1] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_BOTH_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p1b7x5hv_scrollcard_set2};
            id[0][0][3][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_1B_BOTH_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p1b7x5hv_scrollcard_set3};

            id[0][1][0][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_NO_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p3b3x4no_scrollcard_set1};
            id[0][1][0][1] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_NO_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p3b3x4no_scrollcard_set2};
            id[0][1][0][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_NO_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p3b3x4no_scrollcard_set3};
            id[0][1][1][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_H_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p3b3x5h_scrollcard_set1};
            id[0][1][1][1] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_H_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p3b3x5h_scrollcard_set2};
            id[0][1][1][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_H_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p3b3x5h_scrollcard_set3};
            id[0][1][2][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_V_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p3b3x4v_scrollcard_set1};
            id[0][1][2][1] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_V_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p3b3x4v_scrollcard_set2};
            id[0][1][2][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_V_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p3b3x4v_scrollcard_set3};
            id[0][1][3][0] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_BOTH_SCROLL_CS1,
                    R.string.leaderboard_top_score__arcade1p3b3x5hv_scrollcard_set1};
            id[0][1][3][1] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_BOTH_SCROLL_CS2,
                    R.string.leaderboard_top_score__arcade1p3b3x5hv_scrollcard_set2};
            id[0][1][3][2] = new int[] { HelperClass.TOP_SCORE_ARC_1P_3B_BOTH_SCROLL_CS3,
                    R.string.leaderboard_top_score__arcade1p3b3x5hv_scrollcard_set3};

            id[1][0][0][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_NO_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b6x4no_scrollcard_set1};
            id[1][0][0][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_NO_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b6x4no_scrollcard_set2};
            id[1][0][0][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_NO_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b6x4no_scrollcard_set3};
            id[1][0][1][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_H_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b6x4h_scrollcard_set1};
            id[1][0][1][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_H_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b6x4h_scrollcard_set2};
            id[1][0][1][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_H_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b6x4h_scrollcard_set3};
            id[1][0][2][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_V_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b7x4v_scrollcard_set1};
            id[1][0][2][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_V_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b7x4v_scrollcard_set2};
            id[1][0][2][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_V_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b7x4v_scrollcard_set3};
            id[1][0][3][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_BOTH_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b7x5hv_scrollcard_set1};
            id[1][0][3][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_BOTH_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b7x5hv_scrollcard_set2};
            id[1][0][3][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_1B_BOTH_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p1b7x5hv_scrollcard_set3};

            id[1][1][0][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_NO_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x4no_scrollcard_set1};
            id[1][1][0][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_NO_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x4no_scrollcard_set2};
            id[1][1][0][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_NO_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x4no_scrollcard_set3};
            id[1][1][1][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_H_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x5h_scrollcard_set1};
            id[1][1][1][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_H_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x5h_scrollcard_set2};
            id[1][1][1][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_H_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x5h_scrollcard_set3};
            id[1][1][2][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_V_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x4v_scrollcard_set1};
            id[1][1][2][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_V_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x4v_scrollcard_set2};
            id[1][1][2][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_V_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x4v_scrollcard_set3};
            id[1][1][3][0] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_BOTH_SCROLL_CS1,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x5hv_scrollcard_set1};
            id[1][1][3][1] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_BOTH_SCROLL_CS2,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x5hv_scrollcard_set2};
            id[1][1][3][2] = new int[] { HelperClass.TOP_SCORE_TT_1P_3B_BOTH_SCROLL_CS3,
                    R.string.leaderboard_top_score__timetrial5_secs1p3b3x5hv_scrollcard_set3};
            //endregion
            int game_mode_index,board_type_index,scroll_index,card_set_index;
            if(GameMode==ARCADE)
                game_mode_index=0;
            else if(GameMode==TIME_TRIAL)
            {
                if(TimeTrialTimerValue>5000)
                    return new int[]{-1,-1};
                game_mode_index=1;
            } else return new int[] {-1,-1};
            if(BoardType==ONE_BOARD)
            {
                board_type_index=0;
                switch (ScrollType)
                {
                    case NO_SCROLL:
                        scroll_index=0;
                        if(RowSize!=6 || ColumnSize!=4)
                            return new int[] {-1,-1};
                        break;
                    case HORIZONTAL:
                        if(RowSize!=6 || ColumnSize!=4)
                            return new int[] {-1,-1};
                        scroll_index=1;
                        break;
                    case VERTICAL:
                        if(RowSize!=7 || ColumnSize!=4)
                            return new int[] {-1,-1};
                        scroll_index=2;
                        break;
                    case BOTH:
                        if(RowSize!=7 || ColumnSize!=5)
                            return new int[] {-1,-1};
                        scroll_index=3;
                        break;
                    default:
                        return new int[] {-1,-1};
                }
            }
            else
            {
                board_type_index=1;
                switch (ScrollType)
                {
                    case NO_SCROLL:
                        scroll_index=0;
                        if(RowSize!=3 || ColumnSize!=4)
                            return new int[] {-1,-1};
                        break;
                    case HORIZONTAL:
                        if(RowSize!=3 || ColumnSize!=5)
                            return new int[] {-1,-1};
                        scroll_index=1;
                        break;
                    case VERTICAL:
                        if(RowSize!=3 || ColumnSize!=4)
                            return new int[] {-1,-1};
                        scroll_index=2;
                        break;
                    case BOTH:
                        if(RowSize!=3 || ColumnSize!=5)
                            return new int[] {-1,-1};
                        scroll_index=3;
                        break;
                    default:
                        return new int[] {-1,-1};
                }
            }
            switch (CardSet)
            {
                case CARD_SET_1:
                    card_set_index=0;
                    break;
                case CARD_SET_2:
                    card_set_index=1;
                    break;
                case CARD_SET_3:
                    card_set_index=2;
                    break;
                default:
                    return new int[] {-1,-1};
            }
            return id[game_mode_index][board_type_index][scroll_index][card_set_index];
        }
        //endregion
        //region logic to store 8 top scores for standard board in 2P mode versus androbot
        else if(PlayerType == ANDROBOT)
        {
            int id[][][] = new int[2][4][];
            //region id-values for 2p game against androbot
            id[0][0] = new int[] { HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_NO_SCROLL,
                    R.string.leaderboard_top_scores__arcade2pandrobot1b6x4no_scroll};
            id[0][1] = new int[] { HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_H_SCROLL,
                    R.string.leaderboard_top_scores__arcade2pandrobot1b6x4h_scroll};
            id[0][2] = new int[] { HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_V_SCROLL,
                    R.string.leaderboard_top_scores__arcade2pandrobot1b7x4v_scroll};
            id[0][3] = new int[] { HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_BOTH_SCROLL,
                    R.string.leaderboard_top_scores__arcade2pandrobot1b7x5hv_scroll};
            id[1][0] = new int[] { HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_NO_SCROLL,
                    R.string.leaderboard_top_scores__timetrial5_secs2pandrobot1b6x4no_scroll};
            id[1][1] = new int[] { HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_H_SCROLL,
                    R.string.leaderboard_top_scores__timetrial5_secs2pandrobot1b6x4h_scroll};
            id[1][2] = new int[] { HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_V_SCROLL,
                    R.string.leaderboard_top_scores__timetrial5_secs2pandrobot1b7x4v_scroll};
            id[1][3] = new int[] { HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_BOTH_SCROLL,
                    R.string.leaderboard_top_scores__timetrial5_secs2pandrobot1b7x5hv_scroll};
            //endregion

            int game_mode_index,scroll_index;
            if(GameMode==ARCADE)
                game_mode_index=0;
            else if(GameMode==TIME_TRIAL)
            {
                game_mode_index=1;
            } else return new int[] {-1,-1};
            if(BoardType!=ONE_BOARD)
                return new int[] {-1,-1};
            switch (ScrollType)
            {
                case NO_SCROLL:
                    scroll_index=0;
                    if(RowSize!=6 || ColumnSize!=4)
                        return new int[] {-1,-1};
                    break;
                case HORIZONTAL:
                    if(RowSize!=6 || ColumnSize!=4)
                        return new int[] {-1,-1};
                    scroll_index=1;
                    break;
                case VERTICAL:
                    if(RowSize!=7 || ColumnSize!=4)
                        return new int[] {-1,-1};
                    scroll_index=2;
                    break;
                case BOTH:
                    if(RowSize!=7 || ColumnSize!=5)
                        return new int[] {-1,-1};
                    scroll_index=3;
                    break;
                default:
                    return new int[] {-1,-1};
            }
            return id[game_mode_index][scroll_index];
        }
        //endregion

        return new int[] {-1,-1};
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final int SWIPE_MIN_DISTANCE = 40;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                incrementScreenIndex();
                return false; //Right to left
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                decrementScreenIndex();
                return false; // Left to right
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE)// && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false; // Bottom to top
            }
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE)// && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                return false; // Top to bottom
            }
            return true;
        }
    }

}
