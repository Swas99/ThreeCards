package com.archer.matching_card_game.three_cards;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.archer.matching_card_game.three_cards.GlobalScores.BaseGameUtils;
import com.archer.matching_card_game.three_cards.StoryMode.ScreenCreation;
import com.archer.matching_card_game.three_cards.util.InAppBilling;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.event.Events;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestBuffer;
import com.google.android.gms.games.quest.QuestUpdateListener;
import com.google.android.gms.games.quest.Quests;
import com.google.android.gms.plus.Plus;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ACCOMPLISHMENTS;
import static com.archer.matching_card_game.three_cards.HelperClass.AD_FREE_VERSION_HASH_MAP;
import static com.archer.matching_card_game.three_cards.HelperClass.AD_FREE_VERSION_MAP_KEY;
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
import static com.archer.matching_card_game.three_cards.HelperClass.FLIP_ANIMATION_TIME;
import static com.archer.matching_card_game.three_cards.HelperClass.GAME_BACKGROUND;
import static com.archer.matching_card_game.three_cards.HelperClass.GAME_MODE;
import static com.archer.matching_card_game.three_cards.HelperClass.HIT_OR_MISS_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.HORIZONTAL;
import static com.archer.matching_card_game.three_cards.HelperClass.HURRICANE;
import static com.archer.matching_card_game.three_cards.HelperClass.JET_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.LOCKING_TIME;
import static com.archer.matching_card_game.three_cards.HelperClass.LOVE_AFFAIR_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.MANUAL;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_COINS_ONE_GAME;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_COL_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_ROW_SIZE_1B;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_ROW_SIZE_3B;
import static com.archer.matching_card_game.three_cards.HelperClass.NO_SCROLL;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.OVERALL_PROGRESS_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_MODE;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_ONE_NAME;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_TWO_NAME;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_TWO_TYPE;
import static com.archer.matching_card_game.three_cards.HelperClass.QUICK_GAME;
import static com.archer.matching_card_game.three_cards.HelperClass.RANDOM_BOT;
import static com.archer.matching_card_game.three_cards.HelperClass.RICHIE_RICH;
import static com.archer.matching_card_game.three_cards.HelperClass.ROBOT_MEMORY;
import static com.archer.matching_card_game.three_cards.HelperClass.ROBOT_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.ROCK;
import static com.archer.matching_card_game.three_cards.HelperClass.ROW_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_GAME;
import static com.archer.matching_card_game.three_cards.HelperClass.SCROLL_TYPE;
import static com.archer.matching_card_game.three_cards.HelperClass.SHELTER_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.STARS_1_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.STARS_2_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.SetEnableControls;
import static com.archer.matching_card_game.three_cards.HelperClass.SetFontToControls;
import static com.archer.matching_card_game.three_cards.HelperClass.THREE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.THUNDER_BOLT_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL_TIMER;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL_VALUE_1;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL_VALUE_2;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL_VALUE_3;
import static com.archer.matching_card_game.three_cards.HelperClass.TOTAL_COINS;
import static com.archer.matching_card_game.three_cards.HelperClass.TWO_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.VERTICAL;

//implements GestureDetector.OnGestureListener
public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        QuestUpdateListener
{

    final MainActivity thisContext = this;
    public AdRequest AdRequest;
    public InterstitialAd mInterstitialAd;
    InAppBilling objInAppBilling;

    public View CurrentView;
    public int CURRENT_SCREEN;
    public Game objCardGame;
    public int LockingTime;
    HomePageTitleBar objHomePageTitleBar;
    public AlertDialog CommonDialog;
    View.OnClickListener Process_Input;
    //user data
    public boolean adFreeVersion;
    public long coins;
    String playerOneName;
    String playerTwoName;
    //region Game Related
    int PlayerMode;
    int PlayerTwoType;
    int RobotMemoryLevel;
    int GameMode;
    int TimeTrialTimer;
    int BoardType;
    int RowSize;
    int ColSize;
    int ScrollType;
    int CardSet;
    int GameBackground;
//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
////            boolean isPlayInProgress = savedInstanceState.getBoolean(":");
//        }

        initializeOnCreateControls();
        initializeGlobalScores();
    }


    public void initializeOnCreateControls()
    {
        if (objCardGame != null) {
            objCardGame.Clear();
            objCardGame.mContext=this;
        }

        if (objHomePageTitleBar == null)
            objHomePageTitleBar = new HomePageTitleBar(new WeakReference<>(this));
        loadView(R.layout.screen_home);
        CURRENT_SCREEN = R.layout.screen_home;

        initializeAds();
        //region Initialize In App Billing
        objInAppBilling = new InAppBilling(this);
        objInAppBilling.initializeInAppBilling();
        //endregion

        //region Set Coins
        ((TextView)findViewById(R.id.tvCoins)).setTypeface(Typeface.SANS_SERIF);
        updateCoins(0);
        setCoins();
        //endregion

        setPlayerNames();
        InitializeDialogInputListener();
        //region check if adFreeVersion
        SharedPreferences prefs = getSharedPreferences(String.valueOf(AD_FREE_VERSION_HASH_MAP),
                Context.MODE_PRIVATE);
        adFreeVersion =  prefs.getBoolean(String.valueOf(AD_FREE_VERSION_MAP_KEY), false);
        //endregion
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (objInAppBilling.mHelper != null)
                objInAppBilling.mHelper.dispose();
            objInAppBilling.mHelper = null;
        }
        catch (Exception e) {/*do nothing*/}

        CollectGarbage();
    }

    public void setCoins() {
        ((TextView) findViewById(R.id.tvCoins)).setText(String.valueOf(coins));
    }

    public View loadView(int layout_id) {
        if (CurrentView != null)
            CurrentView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        View view;
        LayoutInflater inflater = getLayoutInflater();
        view = inflater.inflate(layout_id, null, false);
        Typeface font = Typeface.createFromAsset(thisContext.getAssets(), "fonts/hurry up.ttf");
        SetFontToControls(font, (ViewGroup) view);

        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        setContentView(view);
        CurrentView = view;
        CURRENT_SCREEN = layout_id;
        screenSpecificControls();
        clearUnusedScreenReferences();
        return view;
    }
    public void screenSpecificControls() {
        switch (CURRENT_SCREEN) {
            case R.layout.screen_home:
                setCoins();
                objHomePageTitleBar.requestUpdate(false);
                AnimateViews();
                break;
            case R.layout.screen_board_details:
                LockingTime = getLockingTime();
                GameBackground = getGameBackground();
                addFlingListenerForBoardBackgroundSelection();
                InitializeScreenControls_BoardDetails();
                if(GameMode == ARCADE)
                {
                    int COLOR_DISABLED_GRAY = Color.argb(45, 45, 45, 45);
                    View temp_view = findViewById(R.id.region_TimeTrial);
                    SetEnableControls(false, (ViewGroup) temp_view);
                    temp_view.setBackgroundColor(COLOR_DISABLED_GRAY);
                    findViewById(R.id.TimeTrialTime).setBackgroundResource(R.drawable.background_white45_white196_disabled);
                }
                break;
            case R.layout.screen_quick_game:
                InitializeScreenControls_QuickGame();
                break;
        }
    }

    private void AnimateViews() {
        HomeScreenAnimations objAnim = new HomeScreenAnimations();
        objAnim.StartAnimation(new WeakReference<>(thisContext));
    }

    //region load static screens from Home screen

    SettingsScreen objSettings;
    TopScores objTopScoresScreen;
    Store objStoreScreen;
    Help objHelpScreen;
    public void loadSettingsScreen() {
        if(objSettings==null)
          objSettings = new SettingsScreen(new WeakReference<>(thisContext));
        objSettings.loadScreen();
    }
    public void loadTopScoresScreen()
    {
        if(objTopScoresScreen==null)
         objTopScoresScreen = new TopScores(new WeakReference<>(thisContext));
        LoadDefaultValues("");
        objTopScoresScreen.InitializeBoardDetails(GameMode,PlayerMode,PlayerTwoType,RobotMemoryLevel,
                BoardType,ScrollType,CardSet,RowSize,ColSize,TimeTrialTimer);
        objTopScoresScreen.Show();
    }
    public void loadStoreScreen()
    {
        if(objStoreScreen==null)
            objStoreScreen = new Store(new WeakReference<>(thisContext));
        objStoreScreen.Show();
    }
    public void loadHelpScreen()
    {
        if(objHelpScreen==null)
            objHelpScreen = new Help(new WeakReference<>(thisContext));
        objHelpScreen.Show();
    }
    //endregion

    public void loadAdsToAdView()
    {
        if(!adFreeVersion)
        {
            final AdView mAdView = (AdView) findViewById(R.id.adView);
            mAdView.loadAd(AdRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void setPlayerNames() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        playerOneName = preferences.getString(String.valueOf(PLAYER_ONE_NAME), "Player 1");
        playerTwoName = preferences.getString(String.valueOf(PLAYER_TWO_NAME), "Player 2");
    }

    //region Home Screen Click Handler
    public void myClickHandler(View v) {
        switch (v.getId()) {
            case R.id.btnGameLogo:
                objHomePageTitleBar.requestUpdate(true);
                break;
            case R.id.btnQuickGame:
                LoadDefaultValues(String.valueOf(QUICK_GAME));
                loadView(R.layout.screen_quick_game);
                InitializeRobotMemoryListener();

                loadAdsToAdView();
                break;
            case R.id.btnArcade:
                loadView(R.layout.screen_player_mode);
                LoadDefaultValues("");
                InitializeScreenControls_PlayerMode();
                CURRENT_SCREEN = R.layout.screen_player_mode;
                GameMode = ARCADE;

                loadAdsToAdView();
                break;
            case R.id.btnTimeTrial:
                loadView(R.layout.screen_player_mode);
                LoadDefaultValues("");
                InitializeScreenControls_PlayerMode();
                CURRENT_SCREEN = R.layout.screen_player_mode;
                GameMode = TIME_TRIAL;

                loadAdsToAdView();
                break;
            case R.id.btnStoryMode:
                ScreenCreation obj = new ScreenCreation(new WeakReference<>(this));
                obj.show();
                break;
            case R.id.btnHelp:
                loadHelpScreen();
                break;
            case R.id.btnStore:
            case R.id.btn_store:
                loadStoreScreen();
                break;
            case R.id.btnTopScores:
            case R.id.btn_topScores:
                loadTopScoresScreen();
                break;
            case R.id.btnSettings:
            case R.id.btn_settings:
                loadSettingsScreen();
                break;
            case R.id.btnRating:
            case R.id.btn_rating:
                openPlayStorePage();
                break;
            case R.id.btn_fb:
            case R.id.btnFb:

                try
                {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/627541484052447"));
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/The.Archer.Apps"));
                    startActivity(intent);
                }
                break;
            case R.id.btnShare:
            case R.id.btn_share:
                String msg = "Classic game to keep your brain cells engaged.\n" +
                        "https://play.google.com/store/apps/details?id=com.archer.matching_card_game.three_cards";
                takeScreenShotAndShare(msg);
                break;
            case R.id.btnBack:
            case R.id.btn_back:
            case R.id.btnExit:
            case R.id.btn_exit:
                onBackPress();
                break;
        }
    }
    //endregion

    //region Quick Game screen UI related controls
    public void myClickHandler_QuickGame(View v)
    {
        switch (v.getId()) {
            case R.id.value_1P:
                PlayerMode = ONE_PLAYER;
                break;
            case R.id.value_2P:
                PlayerMode = TWO_PLAYER;
                break;
            case R.id.PlayerTwoType:
            case R.id.btnPlayerTwoType:
                displayDialog(getPlayerType(), true);
                return;
            case R.id.value_Arcade:
                GameMode = ARCADE;
                break;
            case R.id.value_TimeTrial:
                GameMode = TIME_TRIAL;
                break;
            case R.id.value_TTT_v1:
                TimeTrialTimer = TIME_TRIAL_VALUE_1;
                break;
            case R.id.value_TTT_v2:
                TimeTrialTimer = TIME_TRIAL_VALUE_2;
                break;
            case R.id.value_TTT_v3:
                TimeTrialTimer = TIME_TRIAL_VALUE_3;
                break;
            case R.id.btnTimeTrialTime:
            case R.id.TimeTrialTime:
                displayDialog(getTimeTrialTime(), true);
                return;
            case R.id.value_OneBoard:
                BoardType = ONE_BOARD;
                RowSize = 6;
                ColSize = 4;
                if(ScrollType==VERTICAL)
                    RowSize=7;
                else if(ScrollType==BOTH)
                {
                    RowSize=7;
                    ColSize=5;
                }
                break;
            case R.id.value_ThreeBoard:
                BoardType = THREE_BOARD;
                RowSize = 3;
                ColSize = 4;
                if(ScrollType==BOTH || ScrollType==HORIZONTAL)
                    ColSize++;
                break;
            case R.id.RowSize:
            case R.id.ColSize:
            case R.id.btnBoardSize:
                displayDialog(getBoardSize(), true);
                return;
            case R.id.ScrollType:
            case R.id.btnScrollType:
                displayDialog(getScrollType(), true);
                return;
            case R.id.value_CardSetOne:
                CardSet = CARD_SET_1;
                break;
            case R.id.value_CardSetTwo:
                CardSet = CARD_SET_2;
                break;
            case R.id.value_CardSetThree:
                CardSet = CARD_SET_3;
                break;
            case R.id.btnCardSet:
                displayDialog(getCardSet(), true);
                return;
            case R.id.btnRandomizeConfiguration:
                RandomizeValues();
                break;
            case R.id.btnBack:
            case R.id.btn_back:
                onBackPress();
                return;
            case R.id.btnStart:
                showObjective();
//                StartGame();
                return;
        }
        InitializeScreenControls_QuickGame();
    }

    private void InitializeScreenControls_QuickGame()
    {
        View temp_view;

        int boardType_views[] = {R.id.value_OneBoard, R.id.value_ThreeBoard};
        int boardType_id[] = {ONE_BOARD, THREE_BOARD};

        int timeTrialTime_views[] = {R.id.value_TTT_v1,R.id.value_TTT_v2,R.id.value_TTT_v3};
        int timeTrialTime_values[] = { 5000, 10000, 15000};


        int cardSet_views[] = {R.id.value_CardSetOne, R.id.value_CardSetTwo, R.id.value_CardSetThree};
        int cardSet_id[] = {CARD_SET_1, CARD_SET_2, CARD_SET_3};

        int COLOR_DISABLED_GRAY = Color.argb(45, 45, 45, 45);


        if (PlayerMode == ONE_PLAYER) {
            temp_view = findViewById(R.id.region_PlayerTwo);
            SetEnableControls(false, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(COLOR_DISABLED_GRAY);
            temp_view = findViewById(R.id.region_memory);
            SetEnableControls(false, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(COLOR_DISABLED_GRAY);

            findViewById(R.id.PlayerTwoType).setBackgroundResource(R.drawable.background_white45_white196_disabled);
            findViewById(R.id.value_1P).setBackgroundResource(R.drawable.btn_white_pressed_thin_border);
            findViewById(R.id.value_2P).setBackgroundResource(R.drawable.btn_white_transparency_20);
        }
        else
        {

            temp_view = findViewById(R.id.region_PlayerTwo);
            SetEnableControls(true, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(Color.argb(0, 0, 0, 0));
            temp_view = findViewById(R.id.region_memory);
            temp_view.setBackgroundColor(Color.argb(0, 0, 0, 0));
            SetEnableControls(true, (ViewGroup) temp_view);

            findViewById(R.id.value_2P).setBackgroundResource(R.drawable.btn_white_pressed_thin_border);
            findViewById(R.id.value_1P).setBackgroundResource(R.drawable.btn_white_transparency_20);
            findViewById(R.id.PlayerTwoType).setBackgroundResource(R.drawable.btn_white_pressed_thin_border);


            if (PlayerTwoType == MANUAL)
            {
                temp_view = findViewById(R.id.region_memory);
                SetEnableControls(false, (ViewGroup) temp_view);
            }
        }
        setContentToView(R.id.PlayerTwoType, get_text(PlayerTwoType));
        ((SeekBar) findViewById(R.id.RobotMemory)).setProgress(RobotMemoryLevel - 1);

        if(GameMode == ARCADE)
        {
            findViewById(R.id.value_Arcade).setBackgroundResource(R.drawable.btn_white_pressed_thin_border);
            findViewById(R.id.value_TimeTrial).setBackgroundResource(R.drawable.btn_white_transparency_20);

            temp_view = findViewById(R.id.region_TimeTrial);
            SetEnableControls(false, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(COLOR_DISABLED_GRAY);
            setBackgroundToViews(timeTrialTime_views, R.drawable.background_white45_white196_disabled);

            findViewById(R.id.region_TimeTrialTime_value_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.TimeTrialTime).setVisibility(View.GONE);
        }
        else
        {
            findViewById(R.id.value_TimeTrial).setBackgroundResource(R.drawable.btn_white_pressed_thin_border);
            findViewById(R.id.value_Arcade).setBackgroundResource(R.drawable.btn_white_transparency_20);

            temp_view = findViewById(R.id.region_TimeTrial);
            SetEnableControls(true, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(Color.TRANSPARENT);
            if(TimeTrialTimer == 5000 || TimeTrialTimer == 10000 || TimeTrialTimer == 15000)
            {
                findViewById(R.id.region_TimeTrialTime_value_buttons).setVisibility(View.VISIBLE);
                findViewById(R.id.TimeTrialTime).setVisibility(View.GONE);

                setBackgroundToViews(timeTrialTime_views, R.drawable.btn_white_transparency_20);
                SetBackgroundToViewFromArray(timeTrialTime_values, timeTrialTime_views, TimeTrialTimer, R.drawable.btn_white_pressed_thin_border);
            }
            else
            {
                temp_view = findViewById(R.id.TimeTrialTime);
                String timeTrialText = String.valueOf(TimeTrialTimer/1000) + " sec";

                temp_view.setVisibility(View.VISIBLE);
                findViewById(R.id.region_TimeTrialTime_value_buttons).setVisibility(View.GONE);

                ((TextView)temp_view).setText(timeTrialText);
                temp_view.setBackgroundResource(R.drawable.btn_white_pressed_thin_border);
            }
        }

        setBackgroundToViews(boardType_views, R.drawable.btn_white_transparency_20);
        SetBackgroundToViewFromArray(boardType_id, boardType_views, BoardType, R.drawable.btn_white_pressed_thin_border);

        setContentToView(R.id.ScrollType,get_text(ScrollType));

        setContentToView(R.id.RowSize, String.valueOf(RowSize));
        setContentToView(R.id.ColSize, String.valueOf(ColSize));

        setBackgroundToViews(cardSet_views, R.drawable.btn_white_transparency_20);
        SetBackgroundToViewFromArray(cardSet_id, cardSet_views, CardSet, R.drawable.btn_white_pressed_thin_border);
    }
    //endregion

    //region Player Mode screen UI related controls
    public void myClickHandler_PlayerMode(View v) {
        switch (v.getId()) {
            case R.id.value_1P:
                PlayerMode = ONE_PLAYER;
                break;
            case R.id.value_2P:
                PlayerMode = TWO_PLAYER;
                break;
            case R.id.value_androbot:
                PlayerTwoType = ANDROBOT;
                break;
            case R.id.value_hurricane:
                PlayerTwoType = HURRICANE;
                break;
            case R.id.value_rock:
                PlayerTwoType = ROCK;
                break;
            case R.id.value_random:
                PlayerTwoType = RANDOM_BOT;
                break;
            case R.id.value_manual:
                PlayerTwoType = MANUAL;
                break;
            case R.id.value_robotMemory_1:
                RobotMemoryLevel = 1;
                break;
            case R.id.value_robotMemory_2:
                RobotMemoryLevel = 2;
                break;
            case R.id.value_robotMemory_3:
                RobotMemoryLevel = 3;
                break;
            case R.id.value_robotMemory_4:
                RobotMemoryLevel = 4;
                break;
            case R.id.value_robotMemory_5:
                RobotMemoryLevel = 5;
                break;
            case R.id.value_robotMemory_6:
                RobotMemoryLevel = 6;
                break;
            case R.id.value_robotMemory_7:
                RobotMemoryLevel = 7;
                break;
            case R.id.value_robotMemory_8:
                RobotMemoryLevel = 8;
                break;
            case R.id.value_robotMemory_9:
                RobotMemoryLevel = 9;
                break;
            case R.id.value_robotMemory_10:
                RobotMemoryLevel = 10;
                break;
            case R.id.btnRandomize:
                int temp = GameMode;
                RandomizeValues();
                GameMode = temp;
                break;
            case R.id.btnBack:
            case R.id.btn_back:
                loadView(R.layout.screen_home);
                CURRENT_SCREEN = R.layout.screen_home;
                return;
            case R.id.btnNext:
                loadView(R.layout.screen_board_details);
                return;
        }
        InitializeScreenControls_PlayerMode();
    }

    private void InitializeScreenControls_PlayerMode() {
        View temp_view;
        int playerMode_views[] = {R.id.value_1P, R.id.value_2P};
        //int playerMode_id[] = {ONE_PLAYER, TWO_PLAYER};
        int playerType_views[] = {R.id.value_hurricane, R.id.value_androbot, R.id.value_rock, R.id.value_random, R.id.value_manual};
        int playerType_id[] = {HURRICANE, ANDROBOT, ROCK, RANDOM_BOT, MANUAL};
        int robotMemory_views[] = {R.id.value_robotMemory_1, R.id.value_robotMemory_2, R.id.value_robotMemory_3,
                R.id.value_robotMemory_4, R.id.value_robotMemory_5, R.id.value_robotMemory_6, R.id.value_robotMemory_7,
                R.id.value_robotMemory_8, R.id.value_robotMemory_9, R.id.value_robotMemory_10};
        int robotMemory_id[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int COLOR_DISABLED_GRAY = Color.argb(45, 45, 45, 45);

        setBackgroundToViews(playerMode_views, R.drawable.background_white45_white196);

        if (PlayerMode == ONE_PLAYER) {
            temp_view = findViewById(R.id.region_PlayerTwo);
            SetEnableControls(false, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(COLOR_DISABLED_GRAY);
            temp_view = findViewById(R.id.region_memory);
            SetEnableControls(false, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(COLOR_DISABLED_GRAY);


            setBackgroundToViews(playerType_views, R.drawable.background_white45_white196_disabled);
            setBackgroundToViews(robotMemory_views, R.drawable.background_white45_white196_disabled);

            findViewById(R.id.value_1P).setBackgroundResource(R.drawable.background_white_orange);
            findViewById(R.id.value_2P).setBackgroundResource(R.drawable.background_white45_white196);
            return;
        }

        //ELSE
        {
            setBackgroundToViews(playerType_views, R.drawable.background_white45_white196);

            temp_view = findViewById(R.id.region_PlayerTwo);
            SetEnableControls(true, (ViewGroup) temp_view);
            temp_view.setBackgroundColor(Color.argb(0, 0, 0, 0));
            temp_view = findViewById(R.id.region_memory);
            temp_view.setBackgroundColor(Color.argb(0, 0, 0, 0));
            SetEnableControls(true, (ViewGroup) temp_view);

            findViewById(R.id.value_2P).setBackgroundResource(R.drawable.background_white_orange);
            findViewById(R.id.value_1P).setBackgroundResource(R.drawable.background_white45_white196);
            SetBackgroundToViewFromArray(playerType_id, playerType_views, PlayerTwoType, R.drawable.background_white_orange);

            if (PlayerTwoType == MANUAL) {
                setBackgroundToViews(robotMemory_views, R.drawable.background_white45_white196_disabled);
                findViewById(R.id.region_memory).setBackgroundColor(COLOR_DISABLED_GRAY);
                return;
            }
            //ELSE
            setBackgroundToViews(robotMemory_views, R.drawable.background_white45_white196);
            SetBackgroundToViewFromArray(robotMemory_id, robotMemory_views, RobotMemoryLevel, R.drawable.background_white_orange);
        }
    }
    //endregion

    //region Board Details screen UI related controls
    public void myClickHandler_BoardDetails(View v) {
        switch (v.getId()) {
            case R.id.value_OneBoard:
                BoardType = ONE_BOARD;
                RowSize = 6;
                ColSize = 4;
                if(ScrollType==VERTICAL)
                    RowSize=7;
                else if(ScrollType==BOTH)
                {
                    RowSize=7;
                    ColSize=5;
                }
                break;
            case R.id.value_ThreeBoard:
                BoardType = THREE_BOARD;
                RowSize = 3;
                ColSize = 4;
                if(ScrollType==BOTH || ScrollType==HORIZONTAL)
                    ColSize++;
                break;
            case R.id.value_noScroll:
                ScrollType = NO_SCROLL;
                break;
            case R.id.value_verticalScroll:
                ScrollType = VERTICAL;
                break;
            case R.id.value_horizontalScroll:
                ScrollType = HORIZONTAL;
                break;
            case R.id.value_bothScroll:
                ScrollType = BOTH;
                break;
            case R.id.RowSize:
            case R.id.ColSize:
            case R.id.btnBoardSize:
                displayDialog(getBoardSize(), true);
                return;
            case R.id.value_CardSetOne:
                CardSet = CARD_SET_1;
                break;
            case R.id.value_CardSetTwo:
                CardSet = CARD_SET_2;
                break;
            case R.id.value_CardSetThree:
                CardSet = CARD_SET_3;
                break;
            case R.id.CardSet:
            case R.id.btnCardSet:
                displayDialog(getCardSet(),true);
                return;
            case R.id.TimeTrialTime:
            case R.id.btnTimeTrialTime:
                displayDialog(getTimeTrialTime(), true);
                return;
            case R.id.OneTouchFlip:
                reverseOneTouchFlip();
                break;
            case R.id.LockingTime:
            case R.id.btnLockingTime:
                displayDialog(getLockingTimeValue(),true);
                return;
            case R.id.previousBoardBackground:
                GameBackground--;
                break;
            case R.id.ivBoardBackground:
            case R.id.nextBoardBackground:
                GameBackground++;
                break;

            case R.id.btnBack:
            case R.id.btn_back:
                onBackPress();
                return;
            case R.id.btnStart:
                showObjective();
//                StartGame();
                return;
        }
        InitializeScreenControls_BoardDetails();
    }

    public void InitializeScreenControls_BoardDetails() {

        int boardType_views[] = {R.id.value_OneBoard, R.id.value_ThreeBoard};
        int boardType_id[] = {ONE_BOARD, THREE_BOARD};

        int scrollType_views[] = {R.id.value_noScroll, R.id.value_verticalScroll, R.id.value_horizontalScroll, R.id.value_bothScroll};
        int scrollType_id[] = {NO_SCROLL, VERTICAL, HORIZONTAL, BOTH};

        int cardSet_views[] = {R.id.value_CardSetOne, R.id.value_CardSetTwo, R.id.value_CardSetThree};
        int cardSet_id[] = {CARD_SET_1, CARD_SET_2, CARD_SET_3};


        setBackgroundToViews(boardType_views, R.drawable.btn_white_transparency_20);
        SetBackgroundToViewFromArray(boardType_id, boardType_views, BoardType, R.drawable.btn_white_pressed_thin_border);

        setBackgroundToViews(scrollType_views, R.drawable.btn_white_transparency_20);
        SetBackgroundToViewFromArray(scrollType_id, scrollType_views, ScrollType, R.drawable.btn_white_pressed_thin_border);

        setBackgroundToViews(cardSet_views, R.drawable.btn_white_transparency_20);
        SetBackgroundToViewFromArray(cardSet_id, cardSet_views, CardSet, R.drawable.btn_white_pressed_thin_border);

        setContentToView(R.id.RowSize, String.valueOf(RowSize));
        setContentToView(R.id.ColSize, String.valueOf(ColSize));
        setContentToView(R.id.TimeTrialTime, String.valueOf(TimeTrialTimer / 1000) + "sec");

        setContentToView(R.id.OneTouchFlip, getOneTouchFlipText());
        setContentToView(R.id.LockingTime, String.valueOf(LockingTime) + " ms");

        setGameBackgroundPreview();
    }
    //endregion

    //region Screen Creation Specific controls

    private void addFlingListenerForBoardBackgroundSelection() {
        final View v  = findViewById(R.id.region_boardBackground);
        final GestureDetector gdt = new GestureDetector(thisContext,new GestureListener());
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }
        });
    }
    public void setContentToView(int viewResId,String value)
    {
        View v = findViewById(viewResId);
        ((TextView)v).setText(value);
    }

    public void setBackgroundToViews(int view_id[],int drawableId)
    {
        View v;
        for (int id:view_id)
        {
            v = findViewById(id);
            v.setBackgroundResource(drawableId);
        }
    }

    public void SetBackgroundToViewFromArray(int identifier[],int view_id[],int id,int drawableId)
    {
        View v;
        for(int i=0;i<identifier.length;i++)
        {
            if(identifier[i]==id)
            {
                v = findViewById(view_id[i]);
                v.setBackgroundResource(drawableId);
                break;
            }
        }
    }

    //endregion

    public int getLockingTime()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        return preferences.getInt(String.valueOf(LOCKING_TIME), 600);
    }
    public void reverseOneTouchFlip()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        int x = preferences.getInt(String.valueOf(FLIP_ANIMATION_TIME), 9);
        x = (x<20)? 120 : 9;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(String.valueOf(FLIP_ANIMATION_TIME), x);
        editor.apply();
    }

    public String getOneTouchFlipText()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        int x = preferences.getInt(String.valueOf(FLIP_ANIMATION_TIME), 9);
        if(x>20)
            return "OFF";
        else
            return ("ON");
    }
    public int getGameBackground()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        return preferences.getInt(String.valueOf(GAME_BACKGROUND), 0);
    }

    public void setGameBackgroundPreview()
    {
        TypedArray backgrounds = thisContext.getResources().obtainTypedArray(R.array.game_backgrounds);
        int length = backgrounds.length();
        length++;
        if(GameBackground<0)
            GameBackground+=length;
        else
            GameBackground = GameBackground % length;

        int background_res;
        if(GameBackground != 0)
        {
            background_res = backgrounds.getResourceId(GameBackground - 1, -1);
        }
        else
        {
            background_res = R.drawable.background_random;
        }
        backgrounds.recycle();
        findViewById(R.id.ivBoardBackground).setBackgroundResource(background_res);
    }

    private void InitializeRobotMemoryListener()
    {
        final SeekBar robotMemory = (SeekBar)findViewById(R.id.RobotMemory);
        robotMemory.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                RobotMemoryLevel = seekBar.getProgress() + 1;
            }
        });
    }

    private void SaveGameConfiguration()
    {
        String id="";
        if(CURRENT_SCREEN==R.layout.screen_quick_game)
        {
            CheckBox cb = (CheckBox) findViewById(R.id.cbSaveConfiguration);
            if(!cb.isChecked())
                return;
            id=String.valueOf(QUICK_GAME);
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(thisContext);
        SharedPreferences.Editor editor = settings.edit();

         editor.putInt(String.valueOf(PLAYER_MODE) + id, PlayerMode);
         editor.putInt(String.valueOf(PLAYER_TWO_TYPE) + id, PlayerTwoType);
         editor.putInt(String.valueOf(ROBOT_MEMORY) + id, RobotMemoryLevel);
         editor.putInt(String.valueOf(GAME_MODE) + id, GameMode);
         editor.putInt(String.valueOf(TIME_TRIAL_TIMER) + id, TimeTrialTimer);
         editor.putInt(String.valueOf(BOARD_TYPE) + id, BoardType);
         editor.putInt(String.valueOf(ROW_SIZE) + id, RowSize);
         editor.putInt(String.valueOf(COLUMN_SIZE) + id, ColSize);
         editor.putInt(String.valueOf(SCROLL_TYPE) + id, ScrollType);
         editor.putInt(String.valueOf(CARD_SET) + id, CardSet);

        if(CURRENT_SCREEN == R.layout.screen_board_details)
        {
            editor.putInt(String.valueOf(LOCKING_TIME), LockingTime);
            editor.putInt(String.valueOf(GAME_BACKGROUND), GameBackground);
        }
        // Commit the edits!
        editor.apply();
    }

    private void StartGame()
    {
        SaveGameConfiguration();
        objCardGame = new Game(this);
        objCardGame.GameBackground = GameBackground;
        objCardGame.PlayerOne_Turn = true; //PlayerOne_FirstMove;
        objCardGame.setGameConfiguration(PlayerMode, PlayerTwoType, RobotMemoryLevel, GameMode, TimeTrialTimer,
                BoardType, RowSize, ColSize, ScrollType, CardSet);
        objCardGame.StartGame();
        CURRENT_SCREEN = SCREEN_GAME;
    }

    //region Start Game Dialog for custom Game

    private void loadContentToGameStartDialog(int layoutResId,final AlertDialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = CurrentView.getMeasuredWidth() - ConvertToPx(thisContext, 40);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(layoutResId, null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        View.OnClickListener go_back = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showObjective();
            }
        };
        view.findViewById(R.id.btnClose).setOnClickListener(go_back);
        view.findViewById(R.id.btnOk).setOnClickListener(go_back);
    }

    public void showObjective()
    {
        final AlertDialog dialog = new AlertDialog.Builder(thisContext).show();
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = CurrentView.getMeasuredWidth() - ConvertToPx(thisContext, 40);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom_mode_start_game, null);
        dialog.setContentView(view);

        //region Objective Dialog Click Handler
        View.OnClickListener click_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnStart:
                        StartGame();
                        dialog.dismiss();
                        break;
                    case R.id.btnClose:
                        dialog.dismiss();
                        break;
                    case R.id.btnGameMode:
                        if (GameMode == ARCADE)
                            loadContentToGameStartDialog(R.layout.dialog_story_mode_arcade,dialog);
                        else
                            loadContentToGameStartDialog(R.layout.dialog_story_mode_time_trial,dialog);
                        break;
                    case R.id.btnBoardType:
                        if (BoardType == ONE_BOARD)
                            loadContentToGameStartDialog(R.layout.dialog_help_1board,dialog);
                        else
                            loadContentToGameStartDialog(R.layout.dialog_help_3board,dialog);
                        break;
                    case R.id.btnScrollType:
                    {
                        switch (ScrollType) {
                            case NO_SCROLL:
                                loadContentToGameStartDialog(R.layout.dialog_help_no_scroll, dialog);
                                break;
                            case VERTICAL:
                                loadContentToGameStartDialog(R.layout.dialog_help_vertical_scroll, dialog);
                                break;
                            case HORIZONTAL:
                                loadContentToGameStartDialog(R.layout.dialog_help_horizontal_scroll, dialog);
                                break;
                            case BOTH:
                                loadContentToGameStartDialog(R.layout.dialog_help_both_scroll, dialog);
                                break;
                        }
                    }
                    break;
                    case R.id.tv_leader_board_custom_game:
                    case R.id.btn_leader_board_custom_game:
                    {
                        int id[] = getGameIdentifierForLeaderBoard();
                        onShowLeaderboardsRequested(id[0],id[1]);
                    }
                    break;
                    case R.id.tvShare:
                    {
                        String msg =
                                "Check out my score" + "\n" +
                                        "https://play.google.com/store/apps/details?id=com.archer.matching_card_game.three_cards";
                        takeScreenShotAndShare(msg);
                    }
                    break;
                }
            }
        };
        //endregion

        view.findViewById(R.id.btnGameMode).setOnClickListener(click_listener);
        view.findViewById(R.id.btnBoardType).setOnClickListener(click_listener);
        view.findViewById(R.id.btnScrollType).setOnClickListener(click_listener);
        view.findViewById(R.id.tvShare).setOnClickListener(click_listener);
        view.findViewById(R.id.btnStart).setOnClickListener(click_listener);
        view.findViewById(R.id.btnClose).setOnClickListener(click_listener);

        TextView tvObjective = (TextView)view.findViewById(R.id.tvObjective);
        TextView tvBoardSize = (TextView)view.findViewById(R.id.tvBoardSize);
        TextView tvScore = (TextView)view.findViewById(R.id.tvScore);
        TextView tvTime = (TextView)view.findViewById(R.id.tvTime);
        TextView tvMoves = (TextView)view.findViewById(R.id.tvMoves);
        TextView btnGameMode = (TextView)view.findViewById(R.id.btnGameMode);
        TextView btnBoardType = (TextView)view.findViewById(R.id.btnBoardType);
        TextView btnScrollType = (TextView)view.findViewById(R.id.btnScrollType);

        if(PlayerMode==HelperClass.TWO_PLAYER && PlayerTwoType!=HelperClass.MANUAL)
            tvObjective.setText("Defeat " + get_text(PlayerTwoType) + "(" + String.valueOf(RobotMemoryLevel) + ")");
        String boardSize = String.valueOf(RowSize)+" x "+String.valueOf(ColSize);
        if(BoardType == HelperClass.THREE_BOARD)
            boardSize+=" (three boards)";
        tvBoardSize.setText(boardSize);
        btnGameMode.setText(get_text(GameMode));
        btnBoardType.setText(get_text(BoardType));
        String scrollType = get_text(ScrollType);
        if(!scrollType.equals("No Scroll"))
            scrollType+= " Scroll";
        btnScrollType.setText(scrollType);

        View tvShare = view.findViewById(R.id.tvShare);
        tvShare.setVisibility(View.VISIBLE);
        AnimationSet zoomInOut = ZoomInOut();
        tvShare.startAnimation(zoomInOut);

        String[] scores = getScores();
        tvScore.setText(String.valueOf(scores[0]));
        tvMoves.setText(String.valueOf(scores[1]));
        tvTime.setText(String.valueOf(scores[2]));

        if(isSignedIn())
        {
            int id[] = getGameIdentifierForLeaderBoard();
            if(id[0] > -1)
            {
                view.findViewById(R.id.region_leader_board_custom_game).setVisibility(View.VISIBLE);
                view.findViewById(R.id.tv_leader_board_custom_game).setOnClickListener(click_listener);
                view.findViewById(R.id.btn_leader_board_custom_game).setOnClickListener(click_listener);
            }
        }

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
                if(TimeTrialTimer>5000)
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
                        if(RowSize!=6 || ColSize!=4)
                            return new int[] {-1,-1};
                        break;
                    case HORIZONTAL:
                        if(RowSize!=6 || ColSize!=4)
                            return new int[] {-1,-1};
                        scroll_index=1;
                        break;
                    case VERTICAL:
                        if(RowSize!=7 || ColSize!=4)
                            return new int[] {-1,-1};
                        scroll_index=2;
                        break;
                    case BOTH:
                        if(RowSize!=7 || ColSize!=5)
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
                        if(RowSize!=3 || ColSize!=4)
                            return new int[] {-1,-1};
                        break;
                    case HORIZONTAL:
                        if(RowSize!=3 || ColSize!=5)
                            return new int[] {-1,-1};
                        scroll_index=1;
                        break;
                    case VERTICAL:
                        if(RowSize!=3 || ColSize!=4)
                            return new int[] {-1,-1};
                        scroll_index=2;
                        break;
                    case BOTH:
                        if(RowSize!=3 || ColSize!=5)
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
        else if(PlayerTwoType == ANDROBOT)
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
                    if(RowSize!=6 || ColSize!=4)
                        return new int[] {-1,-1};
                    break;
                case HORIZONTAL:
                    if(RowSize!=6 || ColSize!=4)
                        return new int[] {-1,-1};
                    scroll_index=1;
                    break;
                case VERTICAL:
                    if(RowSize!=7 || ColSize!=4)
                        return new int[] {-1,-1};
                    scroll_index=2;
                    break;
                case BOTH:
                    if(RowSize!=7 || ColSize!=5)
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

    public String[] getScores()
    {
        String identifier;
        int boardSize = ColSize*RowSize;
        if(BoardType==THREE_BOARD)
            boardSize*=3;

        identifier = String.valueOf(GameMode) + HelperClass.DELIMITER_2 +
                String.valueOf(PlayerMode) + HelperClass.DELIMITER_2 +
                String.valueOf(BoardType) + HelperClass.DELIMITER_2 +
                String.valueOf(CardSet) + HelperClass.DELIMITER_2 +
                String.valueOf(ScrollType) + HelperClass.DELIMITER_2 +
                String.valueOf(boardSize);
        if(GameMode == HelperClass.TIME_TRIAL)
            identifier+= HelperClass.DELIMITER_2 + String.valueOf(TimeTrialTimer);
        if(PlayerMode != HelperClass.ONE_PLAYER)
        {
            identifier+= String.valueOf(PlayerTwoType);
            if(PlayerMode == HelperClass.ROBOT_PLAYER)
                identifier+= HelperClass.DELIMITER_2 + String.valueOf(RobotMemoryLevel);
        }
        String scoring_data;
        //Load previous scores
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        String defTopScore = "0~0~0~0~0";
        String defWorstTimes = "-~-~-~-~-";
        String defMaxMoves = "-~-~-~-~-";
        scoring_data = preferences.getString(identifier, defTopScore+ HelperClass.DELIMITER+defWorstTimes+ HelperClass.DELIMITER+defMaxMoves);

        String allHighScores = scoring_data.split(HelperClass.DELIMITER)[0];
        String allBestTime = scoring_data.split(HelperClass.DELIMITER)[1];
        String allLeastMoves = scoring_data.split(HelperClass.DELIMITER)[2];

        String _highScore[] = allHighScores.split(HelperClass.DELIMITER_2);
        String _bestTime[] = allBestTime.split(HelperClass.DELIMITER_2);
        String _leastMove[] = allLeastMoves.split(HelperClass.DELIMITER_2);

        if(_bestTime[0].equals("1"))
            _bestTime[0]+=" second";
        else if(!_bestTime[0].equals("-"))
            _bestTime[0]+=" seconds";

        return new String[]{
                _highScore[0],
                _leastMove[0],
                _bestTime[0]
        };
    }
    public AnimationSet ZoomInOut()
    {
        ScaleAnimation zoom = new ScaleAnimation(1.05f,.96f,1.05f,.96f,
                Animation.RELATIVE_TO_SELF,.5f,
                Animation.RELATIVE_TO_SELF,.5f);
        zoom.setDuration(800);
        zoom.setStartOffset(200);
        zoom.setFillAfter(true);
        zoom.setRepeatCount(Animation.INFINITE);
        zoom.setInterpolator(new OvershootInterpolator(1f));

        AnimationSet ZoomIn = new AnimationSet(true);
        ZoomIn.addAnimation(zoom);
        return ZoomIn;
    }

    //endregion


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            onBackPress();
        }
        return false;
    }

    boolean backPressFlag;
    public void setBackPressFlag()
    {
        try {
            backPressFlag = true;
            Toast.makeText(thisContext, "Tap again to exit", Toast.LENGTH_SHORT).show();
            new CountDownTimer(2700, 2700) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    backPressFlag = false;
                }
            }.start();
        }
        catch (Exception ex){}
    }
    public void onBackPress()
    {
        switch (CURRENT_SCREEN)
        {
            case R.layout.screen_home:
                if(!backPressFlag)
                    setBackPressFlag();
                else
                {
                    thisContext.finish();
                }
                break;
            case R.layout.screen_board_details:
                loadView(R.layout.screen_player_mode);
                InitializeScreenControls_PlayerMode();
                loadAdsToAdView();
            break;
            case SCREEN_GAME:
                if(!backPressFlag)
                    setBackPressFlag();
                else
                {
                    objCardGame.CleanUp();
                    objCardGame=null;
                    loadView(R.layout.screen_home);
                }
                break;
            default:
                loadView(R.layout.screen_home);
        }
        CollectGarbage();
    }

    private void clearUnusedScreenReferences()
    {
        CommonDialog=null;
         objSettings=null;
         objTopScoresScreen=null;
         objStoreScreen=null;
         objHelpScreen=null;
        CollectGarbage();
    }
    public void CollectGarbage() {
        Runnable myRunnable = new Runnable(){
            public void run(){
                System.gc();
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    //region game-detail's dialog logic
    private void displayDialog(View v,boolean isCancelable)
    {
        if(CommonDialog==null)
            CommonDialog  = new AlertDialog.Builder(thisContext).show();
        else
            CommonDialog.show();
        CommonDialog.setContentView(v);
        Typeface font = Typeface.createFromAsset(thisContext.getAssets(), "fonts/hurry up.ttf");
        SetFontToControls(font, (ViewGroup) v);
        CommonDialog.setCancelable(isCancelable);
    }

    private View getPlayerType()
    {
        String titleText = "Select Player";
        String text[] =  {"Manual","Hurricane","Rock","Androbot","Random-Bot"};
        int tag [] =  {MANUAL,HURRICANE,ROCK,ANDROBOT,RANDOM_BOT};
        return addToMainContainer(tag, text, String.valueOf(PLAYER_TWO_TYPE), tag.length, titleText);
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
            size = Math.min(3*RowSize,MAX_COL_SIZE);

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
        int i = 2;
        int base_size=2;
        if(BoardType == ONE_BOARD)
            size = MAX_ROW_SIZE_1B;
        else
        {
            i--; base_size--;
            size = MAX_ROW_SIZE_3B;
        }

        String text[] =  new String[size];
        int tag [] =  new int[size];
        while (i<=size)
        {
            tag[i-base_size]=i;
            text[i-base_size]=String.valueOf(i);
            i++;
        }
        return addToBoardSizeContainer(tag, text, String.valueOf(ROW_SIZE), i - base_size, "Select Row Size");
    }

    private View getBoardSize() {
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
        return addToMainContainer(tag, text, String.valueOf(CARD_SET),tag.length,"Select Card Set");
    }

    private View getLockingTimeValue()
    {
        String text[] =  {"200 ms","400 ms","600 ms","800 ms","1000 ms","1200 ms","1400 ms" };
        int tag [] =  {200,400,600,800,1000,1200,1400};
        return addToMainContainer(tag, text, String.valueOf(LOCKING_TIME), tag.length, "Select Locking Time");
    }

    private View addToMainContainer(int[] tag, String[] text,String identifier,int length,String titleText) {
        LinearLayout mainContainer = new LinearLayout(thisContext);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.addView(getTitleTextView(titleText));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConvertToPx(thisContext,190),
                ConvertToPx(thisContext, 45));
        RelativeLayout.LayoutParams rl_params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                1);
        for (int i=0;i<length;i++)
        {
            TextView tv = new Button(thisContext);
            tv.setText(text[i]);
            String tvTag = identifier + DELIMITER + String.valueOf(tag[i]);
            tv.setTag(tvTag);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.argb(180, 255, 255, 255));
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0,ConvertToPx(thisContext,3),0,0);
            tv.setLayoutParams(layoutParams);
            mainContainer.addView(tv);
            tv.setOnClickListener(Process_Input);

            mainContainer.addView(getDivider(rl_params));
        }

        return mainContainer;
    }

    private View addToBoardSizeContainer(int[] tag, String[] text,String identifier,int length,String titleText) {
        LinearLayout row = null;
        LinearLayout mainContainer = new LinearLayout(thisContext);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.addView(getTitleTextView(titleText));

        int i=0;
        LinearLayout.LayoutParams row_Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams box_Params = new LinearLayout.LayoutParams(ConvertToPx(thisContext,72),
                ConvertToPx(thisContext,40));
        RelativeLayout.LayoutParams verticalDivider_params= new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        RelativeLayout.LayoutParams horizontalDivider_params= new RelativeLayout.LayoutParams(1,
                ViewGroup.LayoutParams.MATCH_PARENT);
        while(i<length)
        {
            row = new LinearLayout(thisContext);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(row_Params);
            mainContainer.addView(row);
            mainContainer.addView(getDivider(verticalDivider_params));
            for(int col=0;col<3 && i<length;col++,i++)
            {
                Button tv = new Button(thisContext);
                tv.setText(text[i]);
                String tvTag = identifier + DELIMITER + String.valueOf(tag[i]);
                tv.setTag(tvTag);
                tv.setTextColor(Color.BLACK);
                tv.setPadding(0, ConvertToPx(thisContext, 3), 0, 0);
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
            Button tv = new Button(thisContext);
            tv.setLayoutParams(box_Params);
            tv.setPadding(0, ConvertToPx(thisContext, 3), 0, 0);
            tv.setBackgroundColor(Color.argb(180, 255, 255, 255));

            row.addView(tv);
            row.addView(getDivider(horizontalDivider_params));
            i++;
        }

        return mainContainer;
    }

    private View getDivider(RelativeLayout.LayoutParams rl_params)
    {
        RelativeLayout divider = new RelativeLayout(thisContext);
        divider.setLayoutParams(rl_params);
        divider.setBackgroundColor(Color.BLACK);
        return divider;
    }

    private View getTitleTextView(String titleText)
    {
        int five_dip = ConvertToPx(thisContext, 5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tvTitle = new TextView(thisContext);
        tvTitle.setText(titleText);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setBackgroundColor(Color.BLACK);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setPadding(4 * five_dip, 2 * five_dip, 4 * five_dip, 2 * five_dip);
        return tvTitle;
    }

    //endregion Dialog logic

    private void InitializeDialogInputListener() {
        Process_Input = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyValue = v.getTag().toString();
                int key = Integer.parseInt(keyValue.split(DELIMITER)[0]);
                int value = Integer.parseInt(keyValue.split(DELIMITER)[1]);
                switch (key)
                {
                    case PLAYER_TWO_TYPE:
                        PlayerTwoType = value;
                        break;
                    case ROW_SIZE:
                        RowSize = value;
                        CommonDialog.dismiss();
                        displayDialog(getColSize(),false);
                        return;
                    case COLUMN_SIZE:
                        ColSize = value;
                        break;
                    case SCROLL_TYPE:
                        ScrollType = value;
                        break;
                    case CARD_SET:
                        CardSet = value;
                        break;
                    case TIME_TRIAL_TIMER:
                        TimeTrialTimer = value;
                        break;
                    case LOCKING_TIME:
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(String.valueOf(LOCKING_TIME), value);
                        editor.apply();
                        LockingTime=value;
                        InitializeScreenControls_BoardDetails();
                        break;

                    default:
                        Toast.makeText(thisContext,"Oops. I am unknown error 2",Toast.LENGTH_SHORT).show();
                }
                CommonDialog.dismiss();

                switch (CURRENT_SCREEN) {
                    case R.layout.screen_board_details:
                        InitializeScreenControls_BoardDetails();
                        break;
                    case R.layout.screen_quick_game:
                        InitializeScreenControls_QuickGame();
                        break;
                }
            }
        };
    }

    //region Load default Values

    public void updateCoins(long value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        coins = preferences.getLong(String.valueOf(TOTAL_COINS), 0);
        coins += value;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(String.valueOf(TOTAL_COINS), coins);

        SharedPreferences prefs = getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE);
        long x=prefs.getLong(String.valueOf(RICHIE_RICH), 0);
        if(coins>x)
        {
            SharedPreferences.Editor editor_2 = getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                    Context.MODE_PRIVATE).edit();
            editor_2.putLong(String.valueOf(RICHIE_RICH), coins);
            editor_2.apply();
        }
        // Commit the edits!
        editor.apply();
    }
    private void LoadDefaultValues(String identifier) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PlayerMode = preferences.getInt(String.valueOf(PLAYER_MODE)+ identifier,ONE_PLAYER);
        PlayerTwoType = preferences.getInt(String.valueOf(PLAYER_TWO_TYPE)+ identifier,RANDOM_BOT);
        RobotMemoryLevel = preferences.getInt(String.valueOf(ROBOT_MEMORY)+ identifier,5);
        GameMode = preferences.getInt(String.valueOf(GAME_MODE)+ identifier,ARCADE);
        TimeTrialTimer = preferences.getInt(String.valueOf(TIME_TRIAL_TIMER)+ identifier,TIME_TRIAL_VALUE_1);
        BoardType = preferences.getInt(String.valueOf(BOARD_TYPE)+ identifier,ONE_BOARD);
        RowSize = preferences.getInt(String.valueOf(ROW_SIZE) + identifier, 6);
        ColSize = preferences.getInt(String.valueOf(COLUMN_SIZE) + identifier, 4);
        ScrollType = preferences.getInt(String.valueOf(SCROLL_TYPE)+ identifier,NO_SCROLL);
        CardSet = preferences.getInt(String.valueOf(CARD_SET)+ identifier,CARD_SET_1);
    }

    //endregion

    private void RandomizeValues() {

        int []AllPlayerModes = {ONE_PLAYER,TWO_PLAYER};
        int []AllPlayerTwoType = {HURRICANE,ROCK,ANDROBOT,RANDOM_BOT};
        int []AllRobotMemory = {1,2,3,4,5,6,7,8,9,10};
        int []AllGameMode = {ARCADE,TIME_TRIAL};
        int []AllTimeTrialTimer = {TIME_TRIAL_VALUE_1,TIME_TRIAL_VALUE_2,TIME_TRIAL_VALUE_3};
        int []AllBoardType = {ONE_BOARD,THREE_BOARD};
        int []AllScrollType = {NO_SCROLL,VERTICAL,HORIZONTAL,BOTH};
        int []AllCardSet = {CARD_SET_1,CARD_SET_2,CARD_SET_3};


        if(PlayerMode==ONE_PLAYER)
            PlayerMode=TWO_PLAYER;
        else
            PlayerMode = AllPlayerModes[(int)(Math.random()*1000)%AllPlayerModes.length];

        PlayerTwoType = AllPlayerTwoType[(int)(Math.random()*1000)%AllPlayerTwoType.length];
        RobotMemoryLevel = AllRobotMemory[(int)(Math.random()*1000)%AllRobotMemory.length];
        GameMode = AllGameMode[(int)(Math.random()*1000)%AllGameMode.length];
        TimeTrialTimer = AllTimeTrialTimer[(int)(Math.random()*1000)%AllTimeTrialTimer.length];
        BoardType = AllBoardType[(int)(Math.random()*1000)%AllBoardType.length];
        ScrollType = AllScrollType[(int)(Math.random()*1000)%AllScrollType.length];
        CardSet = AllCardSet[(int)(Math.random()*1000)%AllCardSet.length];

        //Creating a proportionate board
        int minRowSize,maxRowSize;
        int minColSize,maxColSize;
        switch (ScrollType)
        {
            case NO_SCROLL:
                if(BoardType == ONE_BOARD)
                {
                    minRowSize = 4;
                    maxRowSize = MAX_ROW_SIZE_1B-5;
                    RowSize = (int)(Math.random()*1000)%(maxRowSize-minRowSize+1)+minRowSize;
                    minColSize = RowSize/2;
                }
                else
                {
                    minRowSize = 1;
                    maxRowSize = MAX_ROW_SIZE_3B-2;
                    RowSize = (int)(Math.random()*1000)%(maxRowSize-minRowSize+1)+minRowSize;
                    minColSize = RowSize;
                }
                maxColSize = Math.min(MAX_COL_SIZE-1,RowSize);
                ColSize = ((int)(Math.random()*1000)%(maxColSize-minColSize+1))+minColSize;
                break;
            case HORIZONTAL:
                minColSize = 4;
                if(BoardType == ONE_BOARD)
                {
                    minRowSize =4;
                    maxRowSize = 7;
                }
                else
                {
                    minRowSize = 1;
                    maxRowSize = 3;
                }
                RowSize = ((int)(Math.random()*1000)%(maxRowSize-minRowSize+1))+minRowSize;
                maxColSize = MAX_COL_SIZE;
                ColSize = (int)(Math.random()*1000)%(maxColSize-minColSize+1)+minColSize;
                break;
            case VERTICAL:
                minColSize =2;
                maxColSize = MAX_COL_SIZE-2;
                ColSize = (int)(Math.random()*1000)%(maxColSize-minColSize+1)+minColSize;
                if(BoardType == ONE_BOARD)
                {
                    minRowSize = ColSize+2 + 1;
                    maxRowSize = Math.max(minRowSize,MAX_ROW_SIZE_1B - 4);

                }
                else
                {
                    minRowSize = (ColSize+2)/2 + 1;
                    maxRowSize = Math.max(minRowSize,MAX_ROW_SIZE_3B-2);
                }
                RowSize = ((int)(Math.random()*1000)%(maxRowSize-minRowSize+1))+minRowSize;
                break;
            case BOTH:
                if(BoardType == ONE_BOARD)
                {
                    minRowSize = 7;
                    maxRowSize = MAX_ROW_SIZE_1B-5;
                }
                else
                {
                    minRowSize = 2;
                    maxRowSize = MAX_ROW_SIZE_3B-1;
                }
                minColSize = 5;
                RowSize = ((int)(Math.random()*1000)%(maxRowSize-minRowSize+1))+minRowSize;
                maxColSize = MAX_COL_SIZE;
                ColSize = (int)(Math.random()*1000)%(maxColSize-minColSize+1)+minColSize;
                break;
        }
    }

    public String get_text(int identifier)
    {
        String text = "";
        switch (identifier)
        {
            case ARCADE:
                text = "Arcade";
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
                text = "HURRICANE";
                break;
            case ROCK:
                text = "ROCK";
                break;
            case RANDOM_BOT:
                text = "Random-Bot";
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

    public void takeScreenShotAndShare(String msg)
    {
        //region create screenshot
        View mainView = getWindow().getDecorView().getRootView();

        mainView.setDrawingCacheEnabled(true);
        Bitmap bitmap = mainView.getDrawingCache();//screenshot for background view

        File imageFile = new File(getFilesDir(),"TwoCards.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
        }
        catch (Exception e) {
            e.printStackTrace();
            bitmap.recycle();
            mainView.setDrawingCacheEnabled(false);
            return;
        }
        finally {
            mainView.setDrawingCacheEnabled(false);
        }
        //endregion
        //region Share with apps
        Uri screenshotUri = FileProvider.getUriForFile(
                this,
                "com.archer.matching_card_game.three_cards",
                imageFile);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingIntent, "Share using.."));
        //endregion
    }

    public void openPlayStorePage()
    {
        try
        {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.archer.matching_card_game.three_cards"));
            startActivity(intent);
        }
        catch (Exception e)
        {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.archer.matching_card_game.three_cards"));
                startActivity(intent);
            }
            catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, "I am unknown error x", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //region Ads Logic
    int displayAdsCounter;
    private void initializeAds()
    {
//        AdBuddiz.setPublisherKey(getString(R.string.adbuddiz_ad_id)); // See 3.
//        AdBuddiz.cacheAds(this);
//        AdBuddiz.RewardedVideo.fetch(this);

        //region AdMob
        //Initialize AdRequest for Banner Ads
        AdRequest = new AdRequest.Builder().build();

        //Initialize Interstitial Ads
        mInterstitialAd = new InterstitialAd(getApplication());
        mInterstitialAd.setAdUnitId("ca-app-pub-4622920858510017/6102742885");
        requestNewInterstitial();
        //test = ca-app-pub-3940256099942544/1033173712
        //mine = ca-app-pub-4622920858510017/9784140081
        //My RedMi = 865622026474424
        //endregion
    }
    public void requestNewInterstitial() {
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded())
        {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass on the activity result to the helper for handling
        if (!objInAppBilling.mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
                }
            }
        }
    }
    //region Global Scores


    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;
//    private static EventCallback ec;
//    private static QuestCallback qc;
    private ResultCallback<Quests.ClaimMilestoneResult> mClaimMilestoneResultCallback;

    // tag for debug logging
    final String TAG = "TanC";



    private void initializeGlobalScores() {
        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        // Initialize the callbacks for API data return.
//        ec = new EventCallback(this);
//        qc = new QuestCallback(this);

        // Set the callback for when milestones are claimed.
        mClaimMilestoneResultCallback = new ResultCallback<Quests.ClaimMilestoneResult>() {
            @Override
            public void onResult(Quests.ClaimMilestoneResult result) {
                onMilestoneClaimed(result);
            }
        };
    }

    //region Quest

    //region commented (call backs)
//    /**
//     * Lists all of the events for this app as well as the event counts.
//     */
//    public void loadAndPrintEvents() {
//        // Load up a list of events
//        com.google.android.gms.common.api.PendingResult<Events.LoadEventsResult> pr =
//                Games.Events.load(mGoogleApiClient, true);
//
//        // Set the callback to the EventCallback class.
//        pr.setResultCallback(ec);
//    }

//    /**
//     * Class implementation for handling Event results.
//     */
//    class EventCallback implements com.google.android.gms.common.api.ResultCallback {
//        /**
//         * The activity that creates the callback handler class.
//         */
//        MainActivity m_parent;
//
//        public EventCallback (MainActivity main){
//            m_parent = main;
//        }
//
//        /**
//         * Receives event results.
//         *
//         * @param result The result from the Event.
//         */
//        public void onResult(com.google.android.gms.common.api.Result result) {
//            Events.LoadEventsResult r = (Events.LoadEventsResult)result;
//            com.google.android.gms.games.event.EventBuffer eb = r.getEvents();
//
//            String message = "Current stats: \n";
//
//            Log.i(TAG, "number of events: " + eb.getCount());
//
//            String currentEvent = "";
//            for(int i=0; i < eb.getCount(); i++) {
//                message += "event: " + eb.get(i).getName() + " " + eb.get(i).getEventId() +
//                        " " + eb.get(i).getValue() + "\n";
//            }
//            eb.close();
//
//            Toast.makeText(m_parent, message, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /**
//     * List all of the quests for this app.
//     */
//    public void loadAndListQuests() {
//        int[] selection = {Quests.SELECT_OPEN, Quests.SELECT_COMPLETED_UNCLAIMED,
//                Quests.SELECT_ACCEPTED};
//        com.google.android.gms.common.api.PendingResult<Quests.LoadQuestsResult> pr =
//                Games.Quests.load(mGoogleApiClient, selection,
//                        Quests.SORT_ORDER_ENDING_SOON_FIRST, true);
//
//        // Set the callback to the Quest callback.
//        pr.setResultCallback(qc);
//    }
//
//    /**
//     * Class implementation for handling Quest results.
//     */
//    class QuestCallback implements com.google.android.gms.common.api.ResultCallback {
//        /**
//         * The activity that creates the callback handler class.
//         */
//        MainActivity m_parent;
//
//        public QuestCallback (MainActivity main){
//            m_parent = main;
//        }
//
//        /**
//         * Receives Quest results.
//         *
//         * @param result The result from the Quest.
//         */
//        public void onResult(com.google.android.gms.common.api.Result result) {
//            Quests.LoadQuestsResult r = (Quests.LoadQuestsResult)result;
//            QuestBuffer qb = r.getQuests();
//
//            String message = "Current quest details: \n";
//
//            Log.i(TAG, "Number of quests: " + qb.getCount());
//
//            String currentEvent = "";
//            for(int i=0; i < qb.getCount(); i++) {
//                message += "Quest: " + qb.get(i).getName() + " id: " + qb.get(i).getQuestId();
//            }
//            qb.close();
//
//            Toast.makeText(m_parent, message, Toast.LENGTH_LONG).show();
//        }
//    }
    //endregion

    /**
     * Event handler for when Quests are completed.
     *
     * @param quest The quest that has been completed.
     */
    @Override
    public void onQuestCompleted(Quest quest) {
        // create a message string indicating that the quest was successfully completed
        String message = "You successfully completed quest " + quest.getName();

        // Create a custom toast to indicate the quest was successfully completed.
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Claim the quest reward.
        Games.Quests.claim(
                mGoogleApiClient,
                quest.getQuestId(),
                quest.getCurrentMilestone().getMilestoneId())
                .setResultCallback(mClaimMilestoneResultCallback);
    }

    public void onMilestoneClaimed(Quests.ClaimMilestoneResult result){
        // Process the RewardData binary array to provide a specific reward and present the
        // information to the user.
        try {
            if (result.getStatus().isSuccess()){
                String reward = new String(result.getQuest().getCurrentMilestone().
                        getCompletionRewardData(),
                        "UTF-8");

                updateCoins(Long.valueOf(reward));
                // TOAST to let the player what they were rewarded.
                Toast.makeText(this, "Congratulations, you got a " + reward + " coins.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Reward was not claimed due to error.",
                        Toast.LENGTH_LONG).show();
            }
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }
    /**
     * Shows the current list of available quests.
     */
    public void showQuests() {
        if(isSignedIn())
        {
            int[] selection = {Quests.SELECT_OPEN, Quests.SELECT_COMPLETED_UNCLAIMED,
                    Quests.SELECT_ACCEPTED};
            android.content.Intent questsIntent = Games.Quests.getQuestsIntent(mGoogleApiClient,
                    selection);
            startActivityForResult(questsIntent, 0);
        }
    }

    //endregion

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart(): connecting");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): disconnecting");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    public boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        // Show sign-out button on main menu

        // Show "you are signed in" message on win screen, with no sign in button.

        // Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        if (p != null) {
            playerOneName = p.getDisplayName();
            int indexOfSpace = playerOneName.indexOf(' ');
            playerOneName = playerOneName.substring(0,indexOfSpace);
        }

        if(findViewById(R.id.google_sign_in_section)!=null)
            findViewById(R.id.google_sign_in_section).setVisibility(View.GONE);
        if(findViewById(R.id.region_accomplishments)!=null)
            findViewById(R.id.region_accomplishments).setVisibility(View.VISIBLE);

        if(findViewById(R.id.region_leader_board_love_affair)!=null)
            findViewById(R.id.region_leader_board_love_affair).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_thunder_bolt)!=null)
            findViewById(R.id.region_leader_board_thunder_bolt).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_jet)!=null)
            findViewById(R.id.region_leader_board_jet).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_hit_or_miss)!=null)
            findViewById(R.id.region_leader_board_hit_or_miss).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_stars_1)!=null)
            findViewById(R.id.region_leader_board_stars_1).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_shelter)!=null)
            findViewById(R.id.region_leader_board_shelter).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_stars_2)!=null)
            findViewById(R.id.region_leader_board_stars_2).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_overall_progress)!=null)
            findViewById(R.id.region_leader_board_overall_progress).setVisibility(View.VISIBLE);
        if(findViewById(R.id.region_leader_board_custom_game)!=null)
            findViewById(R.id.region_leader_board_custom_game).setVisibility(View.VISIBLE);

        // Start the quest listener.
        Games.Quests.registerQuestUpdateListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed(): already resolving");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            pushAccomplishments();
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    public void onShowLeaderboardsRequested(int pref_id_for_leader_board,int leader_board_id) {
        if (isSignedIn()) {
            SharedPreferences prefs = getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                    Context.MODE_PRIVATE);
            long x=prefs.getLong(String.valueOf(pref_id_for_leader_board), 0);
            if(x>0)
                Games.Leaderboards.submitScore(mGoogleApiClient, getString(leader_board_id),x);

            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(leader_board_id)),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
        }
    }

    public void onSignInButtonClicked() {
        // start the sign-in flow
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    public void onSignOutButtonClicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    void pushAccomplishments() {
        if (!isSignedIn()) {
            return;
        }
        SharedPreferences prefs = getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE).edit();
        int x;
        x=prefs.getInt(String.valueOf(LOVE_AFFAIR_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_love_affair_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_game_in_love_affair), x);
            editor.remove(String.valueOf(LOVE_AFFAIR_STEPS));
        }
        x=prefs.getInt(String.valueOf(THUNDER_BOLT_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_thunderbolt_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_game_in_thunderbolt), x);
            editor.remove(String.valueOf(THUNDER_BOLT_STEPS));
        }
        x=prefs.getInt(String.valueOf(JET_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_jet_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_game_in_jet), x);
            editor.remove(String.valueOf(JET_STEPS));
        }
        x=prefs.getInt(String.valueOf(HIT_OR_MISS_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_hit_or_miss_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_game_in_hit_or_miss), x);
            editor.remove(String.valueOf(HIT_OR_MISS_STEPS));
        }
        x=prefs.getInt(String.valueOf(STARS_1_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_stars_1_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_game_in_stars_1_0), x);
            editor.remove(String.valueOf(STARS_1_STEPS));
        }
        x=prefs.getInt(String.valueOf(SHELTER_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_shelter_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_game_in_shelter), x);
            editor.remove(String.valueOf(SHELTER_STEPS));
        }
        x=prefs.getInt(String.valueOf(STARS_2_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_stars_2_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_game_in_stars_2_0), x);
            editor.remove(String.valueOf(STARS_2_STEPS));
        }
        x=prefs.getInt(String.valueOf(OVERALL_PROGRESS_STEPS),0);
        if(x>0)
        {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_story_mode_complete), x);
            Games.Events.increment(mGoogleApiClient,  getString(R.string.event_unlocked_new_game), x);
            editor.remove(String.valueOf(OVERALL_PROGRESS_STEPS));
        }
        editor.apply();
    }
    //endregion

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final int SWIPE_MIN_DISTANCE = 20;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                GameBackground++;
                InitializeScreenControls_BoardDetails();
                return false; // Right to left
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                GameBackground--;
                InitializeScreenControls_BoardDetails();
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

//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//                                float distanceY) {
//            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE*10 ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
//            {
//                GameBackground++;
//                InitializeScreenControls_BoardDetails();
//                return false; // Right to left
//            }
//            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE*10 ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
//            {
//                GameBackground--;
//                InitializeScreenControls_BoardDetails();
//                return false; // Left to right
//            }
//            return true;
//        }
    }

//region Continue

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//
//        //savedInstanceState.putInt("Layout", 99);
//        // etc.
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        //data =  savedInstanceState.getInt("Layout");
//    }
//

    //endregion
}

