package com.archer.matching_card_game.three_cards;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ACCOMPLISHMENTS;
import static com.archer.matching_card_game.three_cards.HelperClass.ANDROBOT;
import static com.archer.matching_card_game.three_cards.HelperClass.ARCADE;
import static com.archer.matching_card_game.three_cards.HelperClass.BOTH;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_1;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_2;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_3;
import static com.archer.matching_card_game.three_cards.HelperClass.HORIZONTAL;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_COINS_ONE_GAME;
import static com.archer.matching_card_game.three_cards.HelperClass.NO_SCROLL;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL;
import static com.archer.matching_card_game.three_cards.HelperClass.VERTICAL;

public class GameSummary implements View.OnClickListener {


    Game CurrentGame;
    SummaryMatrix objSummaryMatrix;
    int CardHeight,CardWidth;
    int CurrentWinningStreak;

    int weight[]= new int[]{10,10,9,8,7,6,5,4,3,2,1};
    View ViewsToAnimate_RightAligned[] = new View[6];
    View ViewsToAnimate_HorizontalOnes[] = new View[6];
    int top_ViewsToAnimate_RightAligned;
    int top_ViewsToAnimate_HorizontalOnes;

    String playerOneMoves;
    int playerOne_Hits,totalHits;
    float average = 0;
    int timeReward;
    int winningStreak;
    String totalMoves;
    boolean animFlag = true;

    public long Score;
    int CoinsEarned;


    public GameSummary(WeakReference<Game> currentGame,int card_height,int card_width)
    {
        CurrentGame = currentGame.get();
        CardHeight=card_height;
        CardWidth=card_width;
        CurrentWinningStreak = -1;

    }


    public void loadSummaryScreen()
    {
        top_ViewsToAnimate_HorizontalOnes=0;
        top_ViewsToAnimate_RightAligned=0;
        inflateSummaryScreen();
        addFlingListenerToSummaryScreen(CurrentGame.mContext.CurrentView);
        InitiateAndAnimateViews(CurrentGame.mContext.CurrentView);
    }


    public void loadSummaryScreen_StoryMode()
    {
        top_ViewsToAnimate_HorizontalOnes=0;
        top_ViewsToAnimate_RightAligned=0;
        inflateSummaryScreen();
        InitiateAndAnimateViews(CurrentGame.mContext.CurrentView);

        View btn_prev =CurrentGame.mContext.CurrentView.findViewById(R.id.btn_prev_page);
        View btn_next =CurrentGame.mContext.CurrentView.findViewById(R.id.btn_next_page);
        btn_next.setVisibility(View.GONE);
        btn_prev.setVisibility(View.GONE);
    }

    private void inflateSummaryScreen()
    {

        if(CurrentGame.mContext.CurrentView != null)
            CurrentGame.mContext.CurrentView.startAnimation(AnimationUtils.loadAnimation(CurrentGame.mContext,
                    android.R.anim.fade_out));

        LayoutInflater inflater=CurrentGame.mContext.getLayoutInflater();
        int layout_id = R.layout.screen_game_summary;
        View summary_screen=inflater.inflate(layout_id, null, false);
        CurrentGame.mContext.CURRENT_SCREEN = layout_id;
        summary_screen.startAnimation(AnimationUtils.loadAnimation(CurrentGame.mContext, android.R.anim.fade_in));
        CurrentGame.mContext.setContentView(summary_screen);
        CurrentGame.mContext.CurrentView = summary_screen;

        Typeface font = Typeface.createFromAsset(CurrentGame.mContext.getAssets(), "fonts/hurry up.ttf");
        HelperClass.SetFontToControls(font, (ViewGroup) summary_screen);



        final AdView mAdView = (AdView) summary_screen.findViewById(R.id.adView);
        mAdView.loadAd(CurrentGame.mContext.AdRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }
    private void InitiateAndAnimateViews(View summary_screen)
    {
        TextView tvMoves = (TextView)summary_screen.findViewById(R.id.tvMoves);
        TextView tvHits = (TextView)CurrentGame.mContext.CurrentView.findViewById(R.id.tvHits);
        final TextView tvAverage = (TextView)summary_screen.findViewById(R.id.tvAverage);
        TextView tvNearMisses = (TextView)summary_screen.findViewById(R.id.tvNearMisses);
        TextView tvHitChain = (TextView)summary_screen.findViewById(R.id.tvHitChain);
        TextView tvTime = (TextView)summary_screen.findViewById(R.id.tvTime);
        TextView tvWinningStreak = (TextView)summary_screen.findViewById(R.id.tvWinningStreak);
        final TextView tvScore = (TextView)summary_screen.findViewById(R.id.tvTotalScore);
        TextView animHit = (TextView)CurrentGame.mContext.CurrentView.findViewById(R.id.animHit);
        TextView animAverage = (TextView)summary_screen.findViewById(R.id.animAverage);
        TextView animNearMisses = (TextView)summary_screen.findViewById(R.id.animNearMiss);
        TextView animHitChain = (TextView)summary_screen.findViewById(R.id.animMaxHitStreak);
        TextView animTime = (TextView)summary_screen.findViewById(R.id.animTime);
        TextView animWinningStreak = (TextView)summary_screen.findViewById(R.id.animWinningStreak);
        Button btnClickMatrix = (Button)summary_screen.findViewById(R.id.btnClickCountMatrix);
        Button btnMoveMatrix = (Button)summary_screen.findViewById(R.id.btnMoveTraceMatrix);
        Button btnRetainMatrix = (Button) summary_screen.findViewById(R.id.btnRetainingPower);
        Button btnPrevious = (Button)summary_screen.findViewById(R.id.btn_prev_page);
        Button btnNext = (Button)summary_screen.findViewById(R.id.btn_next_page);
        View btnExit = summary_screen.findViewById(R.id.btnExit);
        View btn_exit = summary_screen.findViewById(R.id.btn_exit);
        View btnStore =  summary_screen.findViewById(R.id.btnStore);
        View btn_store =  summary_screen.findViewById(R.id.btn_store);

        tvMoves.setText(playerOneMoves+" / "+totalMoves);
        tvHits.setText(String.valueOf(playerOne_Hits) + " / " + String.valueOf(totalHits));
        if(playerOne_Hits>0)
            addViewToAnimate(animHit, "10x" + String.valueOf(playerOne_Hits));


        if(playerOne_Hits == 0)
        {
            tvAverage.setText("-");
        }
        else {
            tvAverage.setText(String.valueOf(average) + " Moves / Hit");
            if((int)average<11 && average>0) {
                float x = (weight[Math.round(average)] + (average-(int)average));
                addViewToAnimate(animAverage, String.valueOf(x) + "x" + String.valueOf(playerOne_Hits));
            }
        }

        tvNearMisses.setText(String.valueOf(CurrentGame.NearMisses));
        if(CurrentGame.NearMisses>0)
        {
            addViewToAnimate(animNearMisses, "-2x" + String.valueOf(CurrentGame.NearMisses));
            animNearMisses.setTextColor(Color.RED);
        }
        tvHitChain.setText(String.valueOf(CurrentGame.maxHitStreak));
        if(CurrentGame.maxHitStreak>0)
            addViewToAnimate(animHitChain, "10x" + String.valueOf(CurrentGame.maxHitStreak));

        tvTime.setText(String.valueOf(CurrentGame.GameRunningTime) + " seconds");
        if(timeReward>0)
            addViewToAnimate(animTime,"+"+String.valueOf(timeReward));

        //set current winning streak on text view
        tvWinningStreak.setText(String.valueOf(winningStreak));
        if(winningStreak>0)
            addViewToAnimate(animWinningStreak, "10x"+String.valueOf(winningStreak));

        btnClickMatrix.setOnClickListener(this);
        btnMoveMatrix.setOnClickListener(this);
        btnRetainMatrix.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btnStore.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        loadAttemptHitRatio();

        tvScore.setText(String.valueOf(Score));

        startScoreAnimation();
        startCoinsAnimation();

        animFlag = false;
    }
    private void addScoreFromAttemptHitRatio() {
        SparseArray<Integer> attemptHitMap = new SparseArray<>();
        for(int i = 0; i < CurrentGame.CardAttempt_Map.size(); i++) {
            int key = CurrentGame.CardAttempt_Map.keyAt(i);
            int count = CurrentGame.CardAttempt_Map.get(key);

            if(CurrentGame.Matches.indexOfKey(key)>=0)
            {
                if (attemptHitMap.indexOfKey(count) < 0)
                    attemptHitMap.put(count, 1);
                else {
                    int value = attemptHitMap.get(count);
                    attemptHitMap.put(count, value + 1);
                }
            }
        }

         for(int i=0;i<attemptHitMap.size();i++)
        {
            int attempt_number = attemptHitMap.keyAt(i);
            int hits = attemptHitMap.get(attempt_number);
            if(attempt_number<=10)
            {
                int x;
                if(attempt_number==-1)
                    x= 10;
                else
                    x = weight[attempt_number];
                Score+=x*hits;
            }
        }

    }

    private void startCoinsAnimation() {
        if(animFlag) {
            final View animCoin1 =  CurrentGame.mContext.CurrentView.findViewById(R.id.animCoins1);
            final View animCoin2 =  CurrentGame.mContext.CurrentView.findViewById(R.id.animCoins2);
            final View animCoin3 =  CurrentGame.mContext.CurrentView.findViewById(R.id.animCoins3);
            View regionCoin = CurrentGame.mContext.CurrentView.findViewById(R.id.region_coins);
            regionCoin.setVisibility(View.VISIBLE);

            final AnimationSet flip = HelperClass.FlipAnimation(100, 17);
            animCoin1.startAnimation(flip);
            animCoin2.startAnimation(flip);
            animCoin3.startAnimation(flip);

            CoinsEarned = getCoinsEarned();
            flip.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    {
                        View regionCoin = CurrentGame.mContext.CurrentView.findViewById(R.id.region_coins);
                        TextView tvCoins = (TextView) CurrentGame.mContext.CurrentView.findViewById(R.id.tvCoins);
                        if (regionCoin != null && tvCoins != null) {
                            regionCoin.setVisibility(View.INVISIBLE);
                            tvCoins.setTextColor(Color.YELLOW);
                            tvCoins.setText(String.valueOf(CoinsEarned));
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        else
        {
            View regionCoin = CurrentGame.mContext.CurrentView.findViewById(R.id.region_coins);
            TextView tvCoins = (TextView) CurrentGame.mContext.CurrentView.findViewById(R.id.tvCoins);
            regionCoin.setVisibility(View.INVISIBLE);
            tvCoins.setTextColor(Color.YELLOW);
            tvCoins.setText(String.valueOf(CoinsEarned));

        }
    }

    private void loadAttemptHitRatio() {
        MainActivity mContext = CurrentGame.mContext;
        LinearLayout attempt = (LinearLayout)mContext.CurrentView.findViewById(R.id.ll_Attempt);
        LinearLayout match   = (LinearLayout)mContext.CurrentView.findViewById(R.id.ll_Match);

        LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(HelperClass.ConvertToPx(mContext, 54),
                HelperClass.ConvertToPx(mContext, 33));
        lParam.gravity = Gravity.CENTER;



        SparseArray<Integer> attemptHitMap = new SparseArray<>();
        for(int i = 0; i < CurrentGame.CardAttempt_Map.size(); i++) {
            int key = CurrentGame.CardAttempt_Map.keyAt(i);
            int count = CurrentGame.CardAttempt_Map.get(key);

            if(CurrentGame.Matches.indexOfKey(key)>=0)
            {
                if (attemptHitMap.indexOfKey(count) < 0)
                    attemptHitMap.put(count, 1);
                else {
                    int value = attemptHitMap.get(count);
                    attemptHitMap.put(count, value + 1);
                }
            }
        }

        int background_color[] =new int[] { R.color.white_transparent_20,
                R.color.white_transparent_60, R.color.white_transparent_20};
        for(int i=0;i<attemptHitMap.size();i++)
        {
            int attempt_number = attemptHitMap.keyAt(i);
            int hits = attemptHitMap.get(attempt_number);

            TextView tv = new TextView(mContext);
            TextView anim_view = new TextView(mContext);
            if(attempt_number==-1)
                tv.setText("*");
            else
                tv.setText(String.valueOf(attempt_number));
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lParam);
            tv.setTextColor(Color.BLACK);

            anim_view.setGravity(Gravity.CENTER);
            anim_view.setLayoutParams(lParam);
            anim_view.setTextColor(Color.YELLOW);
            anim_view.setVisibility(View.INVISIBLE);

            RelativeLayout cell = new RelativeLayout(mContext);
            cell.setLayoutParams(lParam);
            cell.setBackgroundResource(background_color[i % 2]);
            cell.addView(tv);
            cell.addView(anim_view);

            attempt.addView(cell);

            if(attempt_number<=10)
            {
                int x;
                if(attempt_number==-1)
                    x= 10;
                else
                    x = weight[attempt_number];

                if(i<5)
                {
                    anim_view.setText(String.valueOf(x)+"x"+String.valueOf(hits));
                    ViewsToAnimate_HorizontalOnes[top_ViewsToAnimate_HorizontalOnes++]=anim_view;
                    anim_view.setTextSize(20);
                    Typeface font = Typeface.createFromAsset(CurrentGame.mContext.getAssets(), "fonts/hurry up.ttf");
                    anim_view.setTypeface(font, Typeface.BOLD);
                }
            }


            tv = new TextView(mContext);
            tv.setText(String.valueOf(hits));
            tv.setBackgroundResource(background_color[i%2+1]);
            tv.setLayoutParams(lParam);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.BLACK);
            match.addView(tv);
        }
        RelativeLayout.LayoutParams horizontalDivider_params= new RelativeLayout.LayoutParams(1,
                ViewGroup.LayoutParams.MATCH_PARENT);
        match.addView(getDivider(horizontalDivider_params));
        attempt.addView(getDivider(horizontalDivider_params));
    }

    private View getDivider(RelativeLayout.LayoutParams rl_params)
    {
        RelativeLayout divider = new RelativeLayout(CurrentGame.mContext);
        divider.setLayoutParams(rl_params);
        divider.setBackgroundColor(Color.BLACK);
        return divider;
    }

    private void addViewToAnimate(TextView anim_target,String text)
    {
        Typeface font = Typeface.createFromAsset(CurrentGame.mContext.getAssets(), "fonts/hurry up.ttf");
        ViewsToAnimate_RightAligned[top_ViewsToAnimate_RightAligned++] = anim_target;
        anim_target.setTextColor(Color.YELLOW);
        anim_target.setTextSize(13);
        anim_target.setText(text);
        anim_target.setTypeface(font, Typeface.BOLD);

    }

    private AnimationSet getAnimationFotHorizontalViews()
    {
        AlphaAnimation fade_out = new AlphaAnimation(1f,0.0f);
        fade_out.setDuration(400);
        fade_out.setStartOffset(1800);
        fade_out.setFillAfter(true);
        fade_out.setInterpolator(new AccelerateInterpolator(1.5f));

        ScaleAnimation expand = new ScaleAnimation(.8f,1.2f,.8f,1.2f, Animation.RELATIVE_TO_SELF,.5f, Animation.RELATIVE_TO_SELF  , .5f);
        expand.setDuration(1800);
        expand.setStartOffset(0);
        expand.setFillAfter(true);
        expand.setInterpolator(new DecelerateInterpolator(1.2f));

        int deltaY = HelperClass.ConvertToPx(CurrentGame.mContext, 33);
        TranslateAnimation shake_1 = new TranslateAnimation(0, 0, 0, deltaY);//delta x,y
        shake_1.setDuration(1700);
        shake_1.setStartOffset(0);
        shake_1.setFillAfter(true);


        AnimationSet Reload = new AnimationSet(true);
        Reload.addAnimation(fade_out);
        Reload.addAnimation(expand);
        Reload.addAnimation(shake_1);
        Reload.setFillAfter(true);
        return Reload;
    }

    private AnimationSet getAnimation()
    {
        AlphaAnimation fade_out = new AlphaAnimation(1f,0.0f);
        fade_out.setDuration(400);
        fade_out.setStartOffset(1800);
        fade_out.setFillAfter(true);
        fade_out.setInterpolator(new AccelerateInterpolator(1.5f));

        ScaleAnimation expand = new ScaleAnimation(.8f,1.3f,.8f,1.3f, Animation.RELATIVE_TO_SELF,.5f, Animation.RELATIVE_TO_SELF  , .5f);
        expand.setDuration(1800);
        expand.setStartOffset(0);
        expand.setFillAfter(true);
        expand.setInterpolator(new DecelerateInterpolator(1.2f));

        float deltaX = HelperClass.ConvertToPx(CurrentGame.mContext, 70);
        float displacement = HelperClass.ConvertToPx(CurrentGame.mContext, 15);
        TranslateAnimation shake_1 = new TranslateAnimation(-deltaX,-deltaX+(2*displacement) , 0, -displacement);
        shake_1.setDuration(1700);
        shake_1.setStartOffset(0);
        shake_1.setFillAfter(true);
        shake_1.setInterpolator(new DecelerateInterpolator(1.3f));


        AnimationSet Reload = new AnimationSet(true);
        Reload.addAnimation(fade_out);
        Reload.addAnimation(expand);
        Reload.addAnimation(shake_1);
        Reload.setFillAfter(true);
        return Reload;
    }

    private void startScoreAnimation()
    {
        if(animFlag) {
            new CountDownTimer(300, 300) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {

                    AnimationSet anim = getAnimation();
                    AnimationSet anim_set2 = getAnimationFotHorizontalViews();
                    for (int i = 0; i < top_ViewsToAnimate_RightAligned; i++) {
                        ViewsToAnimate_RightAligned[i].startAnimation(anim);
                    }

                    for (int i = 0; i < top_ViewsToAnimate_HorizontalOnes; i++) {
                        ViewsToAnimate_HorizontalOnes[i].startAnimation(anim_set2);
                    }
                }
            }.start();
        }
    }

    public void CalculateScore()
    {
        Score=0;

        totalMoves =String.valueOf(CurrentGame.TotalMoves);
//        if(CurrentGame.PlayerMode == ONE_PLAYER)
//            playerOneMoves = totalMoves;
//        else
            playerOneMoves = String.valueOf(CurrentGame.Player1_Moves);


        if(CurrentGame.PlayerMode == HelperClass.ONE_PLAYER)
            playerOne_Hits = CurrentGame.PlayerOne_Score + CurrentGame.PlayerTwo_Score;
        else
            playerOne_Hits = CurrentGame.PlayerOne_Score;
        totalHits = CurrentGame.PlayerOne_Score+CurrentGame.PlayerTwo_Score;

        Score+=playerOne_Hits*10;
        switch (CurrentGame.CardSet)
        {
            case CARD_SET_2:
            {
                Score*=1.2;
                break;
            }
            case CARD_SET_3:
            {
                Score*=1.4;
                break;
            }
        }

        if(CurrentGame.GameMode == HelperClass.TIME_TRIAL && CurrentGame.TimeTrialTimerValue/1000<CurrentGame.TotalCardsOnBoard)
        {
            Score = Score +  (long)(Score*(((CurrentGame.TotalCardsOnBoard)-(CurrentGame.TimeTrialTimerValue/1000))/50f) );
        }


        if(playerOne_Hits >0)
        {
//            if (CurrentGame.PlayerMode == ONE_PLAYER)
//                average = (float) CurrentGame.ActualClickCount / (2 * playerOne_Hits);
//            else
            average = ((float) CurrentGame.Player1_Moves) / playerOne_Hits;
            average*=100;
            average = Math.round(average);
            average/=100;

            if((int)average<11 && average>0) {
                float x = (weight[Math.round(average)] + (average-(int)average));
                Score+=average* x;
            }
        }

        if(CurrentGame.NearMisses>0)
        {
            Score-=(2*CurrentGame.NearMisses);
        }
        Score+= CurrentGame.maxHitStreak*10;


        float boardSize = CurrentGame.TotalCardsOnBoard;

        timeReward = (int) ((boardSize*4.86f) - getGameTime()); //3.24, 6.48
        if(timeReward<=0 || playerOne_Hits<(totalHits/2) || CurrentGame.powUsed)
            timeReward = 0;
        Score+=timeReward;

        //set current winning streak
        winningStreak = getCurrentWinningStreak(average,playerOne_Hits,totalHits);
        int maxBonusForManualPlayerTwo;
        if(CurrentGame.PlayerTwoType == HelperClass.MANUAL ||
                CurrentGame.TotalCardsOnBoard<10)
            maxBonusForManualPlayerTwo = Math.min(winningStreak*10,45);
        else
            maxBonusForManualPlayerTwo = Math.min(winningStreak*10,100);
        Score+=maxBonusForManualPlayerTwo;

        addScoreFromAttemptHitRatio();
    }

    private int getGameTime()
    {
        int time = CurrentGame.GameRunningTime;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CurrentGame.mContext);
        int x = preferences.getInt(String.valueOf(HelperClass.FLIP_ANIMATION_TIME), 9);
        int totalMoves = CurrentGame.ActualClickCount/3;
        if(x<20)
        {
            time += Math.round(totalMoves*333f/1000f);
        }
        return time;
    }

    public int getCoinsEarned()
    {
        float coins;
        float coins_playerTwoType = 0;
        float coins_playerTwoMemory = 0;
        float coins_boardType;
        float coins_timeTrail = 0;
        float cardSetWeight = 0;

        if(CurrentGame.PlayerMode == HelperClass.TWO_PLAYER && CurrentGame.PlayerTwoType != HelperClass.MANUAL)
        {
            coins_playerTwoType = 0.04f;
            coins_playerTwoMemory = CurrentGame.RobotMemoryLevel/100;
        }


        if(CurrentGame.BoardType == HelperClass.ONE_BOARD)
            coins_boardType = 0.09f;
        else
            coins_boardType = 0.065f;

        switch (CurrentGame.CardSet)
        {
            case HelperClass.CARD_SET_1:
                cardSetWeight = 0.01f;
                break;
            case HelperClass.CARD_SET_2:
                cardSetWeight = 0.035f;
                break;
            case HelperClass.CARD_SET_3:
                cardSetWeight = 0.06f;
                break;
        }

        if(CurrentGame.GameMode == HelperClass.TIME_TRIAL && CurrentGame.TimeTrialTimerValue/1000<CurrentGame.TotalCardsOnBoard)
            coins_timeTrail = .0065f * (31 - CurrentGame.TimeTrialTimerValue/1000);

        float multiplier = coins_playerTwoType + coins_playerTwoMemory + coins_boardType +
                cardSetWeight + coins_timeTrail;
        if(CurrentGame.TotalCardsOnBoard<8)
        {
            multiplier*= (.13 * CurrentGame.TotalCardsOnBoard);
        }
        coins = multiplier*Score;
        coins += getScrollBonus();
        return (int)coins;
    }

    public float getScrollBonus()
    {
        float bonus;
        int displaySize;
        int rowSize = CurrentGame.RowSize;
        int colSize = CurrentGame.ColumnSize;
        int x,y;
        switch (CurrentGame.BoardIdentifier)
        {
            case HelperClass.OneBoard_VerticalScroll:
                displaySize = Math.min((colSize*(colSize+2)),(colSize*rowSize));
                break;
            case HelperClass.ThreeBoard_VerticalScroll:
                x = colSize+2;
                x = x - (x%2); //reduce one if x is odd
                displaySize = x*colSize;
                break;
            case HelperClass.OneBoard_HorizontalScroll:
            case HelperClass.ThreeBoard_HorizontalScroll:
                x = Math.min(4,(colSize-1));
                displaySize = x*rowSize;
                break;
            case HelperClass.OneBoard_BothScroll:
                x = Math.min(colSize,4);
                y = Math.min(rowSize,5);
                displaySize = x*y;
                break;
            case HelperClass.ThreeBoard_BothScroll:
                x = Math.min(colSize,4);
                y = Math.min(rowSize,3);
                displaySize = x*y;
                break;
            default:
                displaySize = rowSize*colSize;

        }
        float numberOfNotVisibleCards = (rowSize*colSize - displaySize);
        float factor = Math.min(numberOfNotVisibleCards,playerOne_Hits);
        bonus = factor * 1.8f;
        return bonus;
    }

    public int getCurrentWinningStreak(float currentAverage,int currentHits,int currentTotalHits) {

        if(CurrentWinningStreak != -1)
            return CurrentWinningStreak;

        float previousAverage;
        int previousPlayerType;
        int previousWinningStreak;

        //Load previous game data
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CurrentGame.mContext);
        previousAverage = preferences.getFloat(String.valueOf(HelperClass.PREVIOUS_AVERAGE),0);
        previousPlayerType = preferences.getInt(String.valueOf(HelperClass.PREVIOUS_PLAYER_MODE), 0);
        previousWinningStreak = preferences.getInt(String.valueOf(HelperClass.PREVIOUS_WINNING_STREAK),0);

        //Compute current winning streak
        int currentPlayerMode = CurrentGame.PlayerMode;
        if(currentPlayerMode == HelperClass.ONE_PLAYER)
        {
            if(previousPlayerType != currentPlayerMode)
                CurrentWinningStreak = 1;
            else
            {
                if(currentAverage<=previousAverage )
                    CurrentWinningStreak = previousWinningStreak+1;
                else
                    CurrentWinningStreak = 0;
            }
        }
        else
        {
            if(currentTotalHits-currentHits >= currentHits)
                CurrentWinningStreak = 0;
            else
            {
                if(previousPlayerType != currentPlayerMode)
                    CurrentWinningStreak = 1;
                else
                    CurrentWinningStreak = previousWinningStreak + 1;
            }
        }

        //Store current game data
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CurrentGame.mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(String.valueOf(HelperClass.PREVIOUS_PLAYER_MODE), currentPlayerMode);
        editor.putFloat(String.valueOf(HelperClass.PREVIOUS_AVERAGE), currentAverage);
        editor.putInt(String.valueOf(HelperClass.PREVIOUS_WINNING_STREAK), CurrentWinningStreak);
        // Commit the edits!
        editor.apply();

        return CurrentWinningStreak;
    }

    public Long getHighestScore()
    {
        String identifier;
        int boardSize = CurrentGame.ColumnSize*CurrentGame.RowSize;
        identifier = String.valueOf(CurrentGame.GameMode) + HelperClass.DELIMITER_2 +
                String.valueOf(CurrentGame.PlayerMode) + HelperClass.DELIMITER_2 +
                String.valueOf(CurrentGame.BoardType) + HelperClass.DELIMITER_2 +
                String.valueOf(CurrentGame.CardSet) + HelperClass.DELIMITER_2 +
                String.valueOf(CurrentGame.ScrollType) + HelperClass.DELIMITER_2 +
                String.valueOf(boardSize);

        if(CurrentGame.GameMode == HelperClass.TIME_TRIAL)
            identifier+= HelperClass.DELIMITER_2 + String.valueOf(CurrentGame.TimeTrialTimerValue);

        if(CurrentGame.PlayerMode != HelperClass.ONE_PLAYER)
        {
            identifier+= String.valueOf(CurrentGame.PlayerTwoType);

            if(CurrentGame.PlayerMode == HelperClass.ROBOT_PLAYER)
                identifier+= HelperClass.DELIMITER_2 + String.valueOf(CurrentGame.RobotMemoryLevel);
        }


        String scoring_data;

        //Load previous scores
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CurrentGame.mContext);
        String defTopScore = "0~0~0~0~0";
        String temp = "99999999999";
        String defWorstTimes = "";
        for(int i = 0;i<5;i++)
            defWorstTimes+=temp+ HelperClass.DELIMITER_2;

        String defMaxMoves = defWorstTimes;
        scoring_data = preferences.getString(identifier, defTopScore+ HelperClass.DELIMITER+defWorstTimes+ HelperClass.DELIMITER+defMaxMoves);


        String allHighScores = scoring_data.split(HelperClass.DELIMITER)[0];
        String allBestTime = scoring_data.split(HelperClass.DELIMITER)[1];
        String allLeastMoves = scoring_data.split(HelperClass.DELIMITER)[2];

        String _highScore[] = allHighScores.split(HelperClass.DELIMITER_2);
        String _bestTime[] = allBestTime.split(HelperClass.DELIMITER_2);
        String _leastMove[] = allLeastMoves.split(HelperClass.DELIMITER_2);

        long prev_highScore = Long.parseLong(_highScore[0]);

        scoring_data = "";

        scoring_data = refreshScoreData(Score,_highScore,1,scoring_data,allHighScores);
        int p2Hits = totalHits-playerOne_Hits;
        if(playerOne_Hits>p2Hits)
        {
            scoring_data = refreshScoreData(CurrentGame.GameRunningTime, _bestTime, -1, scoring_data, allBestTime);
            scoring_data = refreshScoreData(CurrentGame.Player1_Moves, _leastMove, -1, scoring_data, allLeastMoves);
        }
        else
        {
            scoring_data+=allBestTime+ HelperClass.DELIMITER+allLeastMoves;
        }


        //Store current game data
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(identifier, scoring_data);
        // Commit the edits!
        editor.apply();

        if(Score>=prev_highScore)
            checkAndUpdateTopScoreForStandardBoard();

        return prev_highScore;
    }

    public void checkAndUpdateTopScoreForStandardBoard()
    {

        //region logic to store 48 top scores for standard board in 1P mode
        if(CurrentGame.PlayerMode==ONE_PLAYER)
        {
            int id[][][][] = new int[2][2][4][3];
            //region id values
            id[0][0][0][0] =  HelperClass.TOP_SCORE_ARC_1P_1B_NO_SCROLL_CS1;
            id[0][0][0][1] =  HelperClass.TOP_SCORE_ARC_1P_1B_NO_SCROLL_CS2;
            id[0][0][0][2] =  HelperClass.TOP_SCORE_ARC_1P_1B_NO_SCROLL_CS3;
            id[0][0][1][0] =  HelperClass.TOP_SCORE_ARC_1P_1B_H_SCROLL_CS1;
            id[0][0][1][1] =  HelperClass.TOP_SCORE_ARC_1P_1B_H_SCROLL_CS2;
            id[0][0][1][2] =  HelperClass.TOP_SCORE_ARC_1P_1B_H_SCROLL_CS3;
            id[0][0][2][0] =  HelperClass.TOP_SCORE_ARC_1P_1B_V_SCROLL_CS1;
            id[0][0][2][1] =  HelperClass.TOP_SCORE_ARC_1P_1B_V_SCROLL_CS2;
            id[0][0][2][2] =  HelperClass.TOP_SCORE_ARC_1P_1B_V_SCROLL_CS3;
            id[0][0][3][0] =  HelperClass.TOP_SCORE_ARC_1P_1B_BOTH_SCROLL_CS1;
            id[0][0][3][1] =  HelperClass.TOP_SCORE_ARC_1P_1B_BOTH_SCROLL_CS2;
            id[0][0][3][2] =  HelperClass.TOP_SCORE_ARC_1P_1B_BOTH_SCROLL_CS3;

            id[0][1][0][0] =  HelperClass.TOP_SCORE_ARC_1P_3B_NO_SCROLL_CS1;
            id[0][1][0][1] =  HelperClass.TOP_SCORE_ARC_1P_3B_NO_SCROLL_CS2;
            id[0][1][0][2] =  HelperClass.TOP_SCORE_ARC_1P_3B_NO_SCROLL_CS3;
            id[0][1][1][0] =  HelperClass.TOP_SCORE_ARC_1P_3B_H_SCROLL_CS1;
            id[0][1][1][1] =  HelperClass.TOP_SCORE_ARC_1P_3B_H_SCROLL_CS2;
            id[0][1][1][2] =  HelperClass.TOP_SCORE_ARC_1P_3B_H_SCROLL_CS3;
            id[0][1][2][0] =  HelperClass.TOP_SCORE_ARC_1P_3B_V_SCROLL_CS1;
            id[0][1][2][1] =  HelperClass.TOP_SCORE_ARC_1P_3B_V_SCROLL_CS2;
            id[0][1][2][2] =  HelperClass.TOP_SCORE_ARC_1P_3B_V_SCROLL_CS3;
            id[0][1][3][0] =  HelperClass.TOP_SCORE_ARC_1P_3B_BOTH_SCROLL_CS1;
            id[0][1][3][1] =  HelperClass.TOP_SCORE_ARC_1P_3B_BOTH_SCROLL_CS2;
            id[0][1][3][2] =  HelperClass.TOP_SCORE_ARC_1P_3B_BOTH_SCROLL_CS3;

            id[1][0][0][0] =  HelperClass.TOP_SCORE_TT_1P_1B_NO_SCROLL_CS1;
            id[1][0][0][1] =  HelperClass.TOP_SCORE_TT_1P_1B_NO_SCROLL_CS2;
            id[1][0][0][2] =  HelperClass.TOP_SCORE_TT_1P_1B_NO_SCROLL_CS3;
            id[1][0][1][0] =  HelperClass.TOP_SCORE_TT_1P_1B_H_SCROLL_CS1;
            id[1][0][1][1] =  HelperClass.TOP_SCORE_TT_1P_1B_H_SCROLL_CS2;
            id[1][0][1][2] =  HelperClass.TOP_SCORE_TT_1P_1B_H_SCROLL_CS3;
            id[1][0][2][0] =  HelperClass.TOP_SCORE_TT_1P_1B_V_SCROLL_CS1;
            id[1][0][2][1] =  HelperClass.TOP_SCORE_TT_1P_1B_V_SCROLL_CS2;
            id[1][0][2][2] =  HelperClass.TOP_SCORE_TT_1P_1B_V_SCROLL_CS3;
            id[1][0][3][0] =  HelperClass.TOP_SCORE_TT_1P_1B_BOTH_SCROLL_CS1;
            id[1][0][3][1] =  HelperClass.TOP_SCORE_TT_1P_1B_BOTH_SCROLL_CS2;
            id[1][0][3][2] =  HelperClass.TOP_SCORE_TT_1P_1B_BOTH_SCROLL_CS3;

            id[1][1][0][0] =  HelperClass.TOP_SCORE_TT_1P_3B_NO_SCROLL_CS1;
            id[1][1][0][1] =  HelperClass.TOP_SCORE_TT_1P_3B_NO_SCROLL_CS2;
            id[1][1][0][2] =  HelperClass.TOP_SCORE_TT_1P_3B_NO_SCROLL_CS3;
            id[1][1][1][0] =  HelperClass.TOP_SCORE_TT_1P_3B_H_SCROLL_CS1;
            id[1][1][1][1] =  HelperClass.TOP_SCORE_TT_1P_3B_H_SCROLL_CS2;
            id[1][1][1][2] =  HelperClass.TOP_SCORE_TT_1P_3B_H_SCROLL_CS3;
            id[1][1][2][0] =  HelperClass.TOP_SCORE_TT_1P_3B_V_SCROLL_CS1;
            id[1][1][2][1] =  HelperClass.TOP_SCORE_TT_1P_3B_V_SCROLL_CS2;
            id[1][1][2][2] =  HelperClass.TOP_SCORE_TT_1P_3B_V_SCROLL_CS3;
            id[1][1][3][0] =  HelperClass.TOP_SCORE_TT_1P_3B_BOTH_SCROLL_CS1;
            id[1][1][3][1] =  HelperClass.TOP_SCORE_TT_1P_3B_BOTH_SCROLL_CS2;
            id[1][1][3][2] =  HelperClass.TOP_SCORE_TT_1P_3B_BOTH_SCROLL_CS3;
            //endregion
            int game_mode_index,board_type_index,scroll_index,card_set_index;

            if(CurrentGame.GameMode==ARCADE)
                game_mode_index=0;
            else if(CurrentGame.GameMode==TIME_TRIAL)
            {
                if(CurrentGame.TimeTrialTimerValue>5000)
                    return;
                game_mode_index=1;
            } else return;
            if(CurrentGame.BoardType==ONE_BOARD)
                board_type_index=0;
            else
                board_type_index=1;
            switch (CurrentGame.ScrollType)
            {
                case NO_SCROLL:
                    scroll_index=0;
                    if(CurrentGame.RowSize!=6 || CurrentGame.ColumnSize!=4)
                        return;
                    break;
                case HORIZONTAL:
                    if(CurrentGame.RowSize!=6 || CurrentGame.ColumnSize!=4)
                        return;
                    scroll_index=1;
                    break;
                case VERTICAL:
                    if(CurrentGame.RowSize!=7 || CurrentGame.ColumnSize!=4)
                        return;
                    scroll_index=2;
                    break;
                case BOTH:
                    if(CurrentGame.RowSize!=7 || CurrentGame.ColumnSize!=5)
                        return;
                    scroll_index=3;
                    break;
                default:
                    return;
            }
            switch (CurrentGame.CardSet)
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
                    return;
            }
            SharedPreferences prefs = CurrentGame.mContext.getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =prefs.edit();
            long prev_value=prefs.getLong(String.valueOf(id[game_mode_index][board_type_index][scroll_index][card_set_index]), 0);
            if(Score>prev_value)
            {
                editor.putLong(String.valueOf(id[game_mode_index][board_type_index][scroll_index][card_set_index]), Score);
                editor.apply();
            }
        }
        //endregion
        //region logic to store 8 top scores for standard board in 2P mode versus androbot
        else if(CurrentGame.PlayerTwoType == ANDROBOT)
        {
            int id[][] = new int[2][4];
            //region id-values for 2p game against androbot
            id[0][0] =  HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_NO_SCROLL;
            id[0][1] =  HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_H_SCROLL;
            id[0][2] =  HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_V_SCROLL;
            id[0][3] =  HelperClass.TOP_SCORE_ARC_2P_ANDROBOT_1B_BOTH_SCROLL;
            id[1][0] =  HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_NO_SCROLL;
            id[1][1] =  HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_H_SCROLL;
            id[1][2] =  HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_V_SCROLL;
            id[1][3] =  HelperClass.TOP_SCORE_TT_2P_ANDROBOT_1B_BOTH_SCROLL;
            //endregion

            int game_mode_index,scroll_index;
            if(CurrentGame.GameMode==ARCADE)
                game_mode_index=0;
            else if(CurrentGame.GameMode==TIME_TRIAL)
            {
                game_mode_index=1;
            } else return;
            if(CurrentGame.BoardType!=ONE_BOARD)
                return;
            switch (CurrentGame.ScrollType)
            {
                case NO_SCROLL:
                    scroll_index=0;
                    if(CurrentGame.RowSize!=6 || CurrentGame.ColumnSize!=4)
                        return;
                    break;
                case HORIZONTAL:
                    if(CurrentGame.RowSize!=6 || CurrentGame.ColumnSize!=4)
                        return;
                    scroll_index=1;
                    break;
                case VERTICAL:
                    if(CurrentGame.RowSize!=7 || CurrentGame.ColumnSize!=4)
                        return;
                    scroll_index=2;
                    break;
                case BOTH:
                    if(CurrentGame.RowSize!=7 || CurrentGame.ColumnSize!=5)
                        return;
                    scroll_index=3;
                    break;
                default:
                    return;
            }
            SharedPreferences prefs = CurrentGame.mContext.getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =prefs.edit();
            long prev_value=prefs.getLong(String.valueOf(id[game_mode_index][scroll_index]), 0);
            if(Score>prev_value)
            {
                editor.putLong(String.valueOf(id[game_mode_index][scroll_index]), Score);
                editor.apply();
            }
        }
        //endregion

    }

    public String refreshScoreData(long value,String allValues[],int sortLogic,
                                 String scoring_data,String prevValue)
    {
        if(value*sortLogic > Long.parseLong(allValues[4])*sortLogic)
        {
            insertScore(value, allValues,sortLogic);
            for (int i=0;i<5;i++)
                scoring_data += allValues[i] + HelperClass.DELIMITER_2;
        }
        else
        {
            scoring_data += prevValue;
        }
        scoring_data+= HelperClass.DELIMITER;

        return  scoring_data;
    }

    public void insertScore(long score,String scores[],int sortLogic)
    {
        int pos=0;
        for (int i =0;i<5;i++)
        {
            if(Long.parseLong(scores[i])*sortLogic < score*sortLogic)
            {
                pos = i;
                break;
            }
        }
        //shift value to make room for score
        System.arraycopy(scores, pos, scores, pos + 1, 4 - pos);
        scores[pos] = String.valueOf(score);
    }

    public void writeCoinsToPreferences(int challengeBonus)
    {
        CoinsEarned = getCoinsEarned();
        CoinsEarned += challengeBonus;
        CurrentGame.mContext.updateCoins(CoinsEarned);

        SharedPreferences prefs = CurrentGame.mContext.getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE);
        long x=prefs.getLong(String.valueOf(MAX_COINS_ONE_GAME), 0);
        if(CoinsEarned>x)
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(String.valueOf(MAX_COINS_ONE_GAME), CoinsEarned);
            editor.apply();
        }
    }

    private void addFlingListenerToSummaryScreen(View v)
    {
        final GestureDetector gdt = new GestureDetector(CurrentGame.mContext,new GestureListener());
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
            case R.id.btnClickCountMatrix:
                if(objSummaryMatrix == null)
                    objSummaryMatrix = new SummaryMatrix(new WeakReference<>(CurrentGame),CardHeight,CardWidth);
                objSummaryMatrix.SetMatrixType(objSummaryMatrix.CLICK_COUNT);
                objSummaryMatrix.CreateMatrix();
                break;
            case R.id.btnMoveTraceMatrix:
                if(objSummaryMatrix == null)
                    objSummaryMatrix = new SummaryMatrix(new WeakReference<>(CurrentGame),CardHeight,CardWidth);
                objSummaryMatrix.SetMatrixType(objSummaryMatrix.MOVE_TRACE);
                objSummaryMatrix.CreateMatrix();
                break;
            case R.id.btnRetainingPower:
                if(objSummaryMatrix == null)
                    objSummaryMatrix = new SummaryMatrix(new WeakReference<>(CurrentGame),CardHeight,CardWidth);
                objSummaryMatrix.SetMatrixType(objSummaryMatrix.RETAINING_POWER);
                objSummaryMatrix.CreateMatrix();
                break;
            case R.id.btn_prev_page:
            {
                TopScores objTopScore = new TopScores(new WeakReference<>(CurrentGame), true);
                objTopScore.InitializeBoardDetails(CurrentGame.GameMode, CurrentGame.PlayerMode,
                        CurrentGame.PlayerTwoType, CurrentGame.RobotMemoryLevel, CurrentGame.BoardType,
                        CurrentGame.ScrollType, CurrentGame.CardSet, CurrentGame.RowSize,
                        CurrentGame.ColumnSize, CurrentGame.TimeTrialTimerValue);
                objTopScore.current_screen_index = objTopScore.LEAST_MOVES;
                objTopScore.Show();
            }
                break;
            case R.id.btn_next_page:
            {
                TopScores objTopScore = new TopScores(new WeakReference<>(CurrentGame), true);
                objTopScore.InitializeBoardDetails(CurrentGame.GameMode, CurrentGame.PlayerMode,
                        CurrentGame.PlayerTwoType, CurrentGame.RobotMemoryLevel, CurrentGame.BoardType,
                        CurrentGame.ScrollType, CurrentGame.CardSet, CurrentGame.RowSize,
                        CurrentGame.ColumnSize, CurrentGame.TimeTrialTimerValue);

                objTopScore.Show();
            }
                break;
            case R.id.btnExit:
            case R.id.btn_exit:
                CurrentGame.mContext.loadView(R.layout.screen_home);
                CurrentGame.CleanUp();
                break;
            case R.id.btnStore:
            case R.id.btn_store:
                CurrentGame.mContext.loadStoreScreen();
                CurrentGame.CleanUp();
                break;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final int SWIPE_MIN_DISTANCE = 40;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                TopScores objTopScore = new TopScores(new WeakReference<>(CurrentGame),true);
                objTopScore.InitializeBoardDetails(CurrentGame.GameMode,CurrentGame.PlayerMode,
                        CurrentGame.PlayerTwoType,CurrentGame.RobotMemoryLevel,CurrentGame.BoardType,
                        CurrentGame.ScrollType,CurrentGame.CardSet,CurrentGame.RowSize,
                        CurrentGame.ColumnSize,CurrentGame.TimeTrialTimerValue);
                objTopScore.Show();
                return false; // Right to left
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                TopScores objTopScore = new TopScores(new WeakReference<>(CurrentGame),true);
                objTopScore.InitializeBoardDetails(CurrentGame.GameMode,CurrentGame.PlayerMode,
                        CurrentGame.PlayerTwoType,CurrentGame.RobotMemoryLevel,CurrentGame.BoardType,
                        CurrentGame.ScrollType,CurrentGame.CardSet,CurrentGame.RowSize,
                        CurrentGame.ColumnSize,CurrentGame.TimeTrialTimerValue);
                objTopScore.current_screen_index = objTopScore.LEAST_MOVES;
                objTopScore.Show();
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


}
