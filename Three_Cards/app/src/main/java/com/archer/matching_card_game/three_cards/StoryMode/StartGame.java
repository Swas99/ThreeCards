package com.archer.matching_card_game.three_cards.StoryMode;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archer.matching_card_game.three_cards.Game;
import com.archer.matching_card_game.three_cards.HelperClass;
import com.archer.matching_card_game.three_cards.MainActivity;
import com.archer.matching_card_game.three_cards.R;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ARCADE;
import static com.archer.matching_card_game.three_cards.HelperClass.BOTH;
import static com.archer.matching_card_game.three_cards.HelperClass.HORIZONTAL;
import static com.archer.matching_card_game.three_cards.HelperClass.NO_SCROLL;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_POST_GAME_DIALOG_NORMAL;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_POST_GAME_DIALOG_OH_NO;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_POST_GAME_DIALOG_YAY;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_SCORES;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_STARS;
import static com.archer.matching_card_game.three_cards.HelperClass.VERTICAL;

public class StartGame implements View.OnClickListener {

    MainActivity mContext;
    ScreenCreation objScreenCreation;

    int CurrentModule;
    int CurrentLevel;
    int CurrentStage;
    int CurrentChallenge;
    int BoardSize;

    int ScrollType;
    int BoardType;
    int GameMode;
    int PlayerMode;
    int PlayerTwoType;
    int RobotMemoryLevel;
    int TTT_Value;
    int RowSize;
    int ColSize;
    int GameBackGroundIndex;
    int LockingTime;

    Dialog DialogWindow;
    View ContentView;


    public StartGame(WeakReference<MainActivity> m_context,WeakReference<ScreenCreation> obj_screenCreation)
    {
        mContext = m_context.get();
        objScreenCreation = obj_screenCreation.get();
        DialogWindow = mContext.CommonDialog;
    }
    public StartGame(WeakReference<MainActivity> m_context)
    {
        mContext = m_context.get();
        DialogWindow = mContext.CommonDialog;
    }
    public void setStoryModeData(int currentModule,int currentLevel,int currentStage,int currentChallenge)
    {
        CurrentModule=currentModule;
        CurrentLevel=currentLevel;
        CurrentStage=currentStage;
        CurrentChallenge=currentChallenge;
        configureGame();
    }
    private void configureGame()
    {
        GameValues objGameValues = new GameValues(CurrentModule,CurrentLevel,CurrentStage,CurrentChallenge);
        ScrollType = objGameValues.getScrollType();
        BoardType = objGameValues.getBoardType();
        GameMode = objGameValues.getGameMode();
        PlayerMode = objGameValues.getPlayerMode();
        PlayerTwoType = objGameValues.getPlayerTwoType();
        RobotMemoryLevel = objGameValues.getRobotMemoryLevel();
        TTT_Value = objGameValues.getTimeTrialTimer();
        RowSize = objGameValues.getRowSize();
        ColSize = objGameValues.getColSize();
        GameBackGroundIndex = objGameValues.getBackGroundIndex();
        LockingTime = objGameValues.getLockingTime();

        BoardSize=objGameValues.getBoardSize();

        objGameValues = null;
        mContext.CollectGarbage();
    }

    public void showObjective()
    {
        LayoutInflater inflater = mContext.getLayoutInflater();
        ContentView = inflater.inflate(R.layout.dialog_story_mode_start_game, null);
        switch (mContext.CURRENT_SCREEN)
        {
            case SCREEN_POST_GAME_DIALOG_NORMAL:
            case SCREEN_POST_GAME_DIALOG_YAY:
            case SCREEN_POST_GAME_DIALOG_OH_NO:
                break;
            default:
            ContentView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogWindow.dismiss();
                }
            });
        }
        DialogWindow.setContentView(ContentView);

        TextView tvGameNumber = (TextView)ContentView.findViewById(R.id.tvGameNumber);
        TextView tvObjective = (TextView)ContentView.findViewById(R.id.tvObjective);
        TextView tvScore = (TextView)ContentView.findViewById(R.id.tvScore);
        TextView btnGameMode = (TextView)ContentView.findViewById(R.id.btnGameMode);
        TextView btnBoardType = (TextView)ContentView.findViewById(R.id.btnBoardType);
        TextView btnScrollType = (TextView)ContentView.findViewById(R.id.btnScrollType);
        TextView btnStart = (TextView)ContentView.findViewById(R.id.btnStart);


        long topScore = getScore();
        tvGameNumber.setText("#Game "+String.valueOf(getGameNumber()));
        tvObjective.setText(getObjectiveInfo());
        tvScore.setText(String.valueOf(topScore));
        btnGameMode.setText(mContext.get_text(GameMode));
        btnBoardType.setText(mContext.get_text(BoardType));
        String scrollType = mContext.get_text(ScrollType);
        if(!scrollType.equals("No Scroll"))
            scrollType+= " Scroll";
        btnScrollType.setText(scrollType);
        btnGameMode.setOnClickListener(this);
        btnBoardType.setOnClickListener(this);
        btnScrollType.setOnClickListener(this);
        btnStart.setOnClickListener(this);

        if(topScore>0)
        {
         createStarts(ContentView.findViewById(R.id.region_stars));
        }
    }
    private void createStarts(View regionStars)
    {
        int totalMiniStars = readStars();
        if(totalMiniStars==0)
            return;

        View tvShare = ContentView.findViewById(R.id.tvShare);
        tvShare.setVisibility(View.VISIBLE);
        AnimationSet zoomInOut = ZoomInOut();
        tvShare.startAnimation(zoomInOut);
        tvShare.setOnClickListener(this);

        regionStars.setVisibility(View.VISIBLE);
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

    private int getGameNumber()
    {
        int ModuleLevelCount[] = {18,8,4,8,15,17,11};
        int maxStages=6;
        int maxChallenges=5;
        int maxGamesInOneLevel = maxStages*maxChallenges;

        int gameNumber=0;
        for(int i=0;i<CurrentModule;i++)
        {
            gameNumber+=ModuleLevelCount[i]*maxGamesInOneLevel;
        }
        gameNumber+= CurrentLevel*maxGamesInOneLevel;
        gameNumber+= CurrentStage*maxChallenges;
        gameNumber+= CurrentChallenge+1;
        return gameNumber;
    }
    private String getObjectiveInfo()
    {
        String objective="Defeat ";

        switch (CurrentChallenge)
        {
            case 0:
                objective = "Complete within " + getOnePlayerMovesLimit() + " moves";
                break;
            case 1:
                objective = "Complete within " + getOnePlayerMovesLimit() + " moves";
                break;
            case 2:
                objective+= "HURRICANE";
                break;
            case 3:
                objective+= "ROCK";
                break;
            case 4:
                objective+= "ANDROBOT";
                break;
        }

        return objective;
    }

    private String getOnePlayerMovesLimit() {
        int card_sets = BoardSize/3;
        int minMoves= (BoardSize/2) + card_sets;
        int moves_limit = minMoves + (BoardSize/(CurrentStage+1));
        if(CurrentChallenge==0)
            moves_limit+=card_sets;
        return String.valueOf(moves_limit);
    }
    private long getScore()
    {
        SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(STORY_MODE_SCORES),
                Context.MODE_PRIVATE);
        return prefs.getLong(String.valueOf(getGameNumber()), 0l);
    }

    private void loadContentToDialog(int layoutResId)
    {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(layoutResId, null);
        DialogWindow.setContentView(view);
        DialogWindow.setCancelable(false);
        View.OnClickListener go_back = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogWindow.setContentView(ContentView);
                DialogWindow.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = DialogWindow.getWindow();
                lp.copyFrom(window.getAttributes());
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
            }
        };
        view.findViewById(R.id.btnClose).setOnClickListener(go_back);
        view.findViewById(R.id.btnOk).setOnClickListener(go_back);
    }

    private void Start_Game()
    {
        if(mContext.objCardGame == null)
            mContext.objCardGame = new Game(mContext);
        else
        {
            mContext.objCardGame.Clear();
        }
        mContext.objCardGame.StoryMode=true;
        mContext.objCardGame.CurrentModule=CurrentModule;
        mContext.objCardGame.CurrentLevel=CurrentLevel;
        mContext.objCardGame.CurrentStage=CurrentStage;
        mContext.objCardGame.CurrentChallenge=CurrentChallenge;

        mContext.objCardGame.GameBackground = GameBackGroundIndex;
        mContext.objCardGame.LockingTime = LockingTime;
        mContext.objCardGame.PlayerOne_Turn = true;
        mContext.objCardGame.setGameConfiguration(
                PlayerMode,
                PlayerTwoType,
                RobotMemoryLevel,
                GameMode,
                TTT_Value,
                BoardType,
                RowSize,
                ColSize,
                ScrollType,
                HelperClass.STORY_MODE_CARD_SET
        );

        mContext.objCardGame.StartGame();
        mContext.CURRENT_SCREEN = HelperClass.SCREEN_GAME;

        DialogWindow.dismiss();
    }

    private int readStars()
    {
        SharedPreferences pref = mContext.getSharedPreferences(String.valueOf(STORY_MODE_STARS),
                Context.MODE_PRIVATE);
        return pref.getInt(String.valueOf(getGameNumber()), 0);
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

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnGameMode:
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = DialogWindow.getWindow();
                lp.copyFrom(window.getAttributes());
                lp.height =  WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                if(GameMode == ARCADE)
                    loadContentToDialog(R.layout.dialog_story_mode_arcade);
                else
                    loadContentToDialog(R.layout.dialog_story_mode_time_trial);
                break;
            case R.id.btnBoardType:
                if(BoardType == ONE_BOARD)
                    loadContentToDialog(R.layout.dialog_help_1board);
                else
                    loadContentToDialog(R.layout.dialog_help_3board);
                break;
            case R.id.btnScrollType:
                switch (ScrollType)
                {
                    case NO_SCROLL:
                        loadContentToDialog(R.layout.dialog_help_no_scroll);
                        break;
                    case VERTICAL:
                        loadContentToDialog(R.layout.dialog_help_vertical_scroll);
                        break;
                    case HORIZONTAL:
                        loadContentToDialog(R.layout.dialog_help_horizontal_scroll);
                        break;
                    case BOTH:
                        loadContentToDialog(R.layout.dialog_help_both_scroll);
                        break;
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
            case R.id.btnStart:
                Start_Game();
                DialogWindow.dismiss();
                CleanUp();
                break;
        }
    }

    public void takeScreenShotAndShare(String msg)
    {
        //region create screenshot
        View mainView = mContext.getWindow().getDecorView().getRootView();
        View dialogView = DialogWindow.getWindow().getDecorView().getRootView();

        mainView.setDrawingCacheEnabled(true);
        android.graphics.Bitmap bitmap = mainView.getDrawingCache(); //screenshot for background view

        dialogView.setDrawingCacheEnabled(true);
        android.graphics.Bitmap bitmap2 = dialogView.getDrawingCache(); //screenshot for dialog

        int location[] = new int[2];
        int location2[] = new int[2];
        mainView.getLocationOnScreen(location);
        dialogView.getLocationOnScreen(location2);

        //Create a transparent dark layer to add to background
        Paint p = new android.graphics.Paint();
        ColorFilter filter = new LightingColorFilter(0xFF999999, 0x00000000);
        p.setColorFilter(filter);

        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        canvas.drawBitmap(bitmap,location[0],location[1],p);//Add dark layer to background
        canvas.drawBitmap(bitmap2, location2[0] - location[0], location2[1] - location[1],
                new android.graphics.Paint()); //draw dialog over background

        File imageFile = new File(mContext.getFilesDir(),"TwoCards.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            mainView.setDrawingCacheEnabled(false);
            dialogView.setDrawingCacheEnabled(false);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //endregion
        //region Share with apps
        Uri screenshotUri = FileProvider.getUriForFile(
                mContext,
                "com.archer.matching_card_game.three_cards",
                imageFile);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share using.."));
        //endregion
    }

    private void CleanUp()
    {
        Runnable myRunnable = new Runnable(){
            public void run(){
                if(objScreenCreation!=null)
                {
                    objScreenCreation.cleanUp();
                    objScreenCreation=null;
                }
                mContext=null;
                DialogWindow=null;
                ContentView=null;
                System.gc();
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

}
