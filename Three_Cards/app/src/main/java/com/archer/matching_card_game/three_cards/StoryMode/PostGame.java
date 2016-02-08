package com.archer.matching_card_game.three_cards.StoryMode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.archer.matching_card_game.three_cards.HelperClass;
import com.archer.matching_card_game.three_cards.MainActivity;
import com.archer.matching_card_game.three_cards.R;
import com.google.android.gms.games.Games;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ACCOMPLISHMENTS;
import static com.archer.matching_card_game.three_cards.HelperClass.CURRENT_GAME_ID;
import static com.archer.matching_card_game.three_cards.HelperClass.ConvertToPx;
import static com.archer.matching_card_game.three_cards.HelperClass.HIT_OR_MISS_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.JET_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.LOVE_AFFAIR_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.OVERALL_PROGRESS_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_POST_GAME_DIALOG_NORMAL;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_POST_GAME_DIALOG_OH_NO;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_POST_GAME_DIALOG_YAY;
import static com.archer.matching_card_game.three_cards.HelperClass.SHELTER_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.STARS_1_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.STARS_2_STEPS;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_CARD_SET;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_DATA;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_OVERALL_TOP_SCORE;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_SCORES;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_STARS;
import static com.archer.matching_card_game.three_cards.HelperClass.THREE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.THUNDER_BOLT_STEPS;


public class PostGame implements View.OnClickListener, DialogInterface.OnKeyListener  {

    final int STATUS_COMPLETED = 102;
    final int STATUS_IN_PROGRESS = 104;
    final int STATUS_NEW = 103;
    final int STATUS_LOCKED = 101;
    MainActivity mContext;
    int M,L,S,C;//Module-Level-Stage-Challenge

    public PostGame(WeakReference<MainActivity> m_context)
    {
        mContext = m_context.get();
        M=mContext.objCardGame.CurrentModule;
        L=mContext.objCardGame.CurrentLevel;
        S=mContext.objCardGame.CurrentStage;
        C=mContext.objCardGame.CurrentChallenge;
    }

    private void updateGameStatus(boolean result)
    {
        int ModuleLevelCount[] = {18,8,4,8,15,17,11};
        SharedPreferences.Editor editor = mContext.getSharedPreferences(String.valueOf(STORY_MODE_DATA),
                Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(STORY_MODE_DATA),
                Context.MODE_PRIVATE);

        String id = String.valueOf(M)+ "_" +
                String.valueOf(L) + "_" +
                String.valueOf(S) + "_" +
                String.valueOf(C);
        if(result)
        {
            if(prefs.getInt(id,STATUS_NEW)!=STATUS_COMPLETED)
            {
                editor.putInt(id, STATUS_COMPLETED);
                updateAccomplishment();
                //region check_and_unlock_next_level
                int countOfCompleteGamesForCurrentLevel=0;
                String prefix = String.valueOf(M)+"_"+String.valueOf(L)+"_";
                String temp_id;
                for(int _S=0;_S<6;_S++)
                {
                    for (int _C=0;_C<5;_C++)
                    {
                        temp_id = prefix +String.valueOf(_S)+"_"+String.valueOf(_C);
                        if(prefs.getInt(temp_id,-1) == STATUS_COMPLETED)
                            countOfCompleteGamesForCurrentLevel++;
                    }
                }
                if(countOfCompleteGamesForCurrentLevel>24)//24 = 80% of maxGamesInOneLevel(30)
                {
                    if(ModuleLevelCount[M]>L+1)
                    {
                        temp_id = String.valueOf(M)+"_"+String.valueOf(L+1)+"_0_0";
                        if(prefs.getInt(temp_id,STATUS_LOCKED)==STATUS_LOCKED)
                            editor.putInt(temp_id, STATUS_NEW);
                    }
                }
                //endregion
                //region update game_number
                int p,q,r,s;//next_id
                p=M; q=L; r=S; s=C;
                boolean moduleComplete=false;
                s++;
                if(s>4) //4 is max index of challenges
                {
                    s=0;
                    r++;
                    if(r>5) //5 is max index of stages
                    {
                        r=0;
                        q++;
                        if(q>=ModuleLevelCount[p])//check for max index of level in current module
                        {
                            q=0;
                            p++;
                            if(p>=ModuleLevelCount.length)
                            {
                                moduleComplete=true;
                                p=0;
                            }
                        }
                    }
                }
                String next_id = String.valueOf(p)+ "_" +
                        String.valueOf(q) + "_" +
                        String.valueOf(r) + "_" +
                        String.valueOf(s);

                if(!moduleComplete && prefs.getInt(next_id,STATUS_LOCKED)==STATUS_LOCKED)
                {
                    editor.putInt(next_id, STATUS_NEW);
                }

                String currentGameIndex = prefs.getString(String.valueOf(CURRENT_GAME_ID),"0_0_0_0");
                if(currentGameIndex.equals(id))
                {
                    editor.putString(String.valueOf(CURRENT_GAME_ID), next_id);
                }
                //endregion
            }
        }
        else if(prefs.getInt(id,STATUS_LOCKED)!=STATUS_COMPLETED)
            editor.putInt(id, STATUS_IN_PROGRESS);

        editor.apply();
    }

    private void updateAccomplishment()
    {
        int steps_id;
        switch (M)
        {
            case 0:
                steps_id= LOVE_AFFAIR_STEPS;
                break;
            case 1:
                steps_id= THUNDER_BOLT_STEPS;
                break;
            case 2:
                steps_id= JET_STEPS;
                break;
            case 3:
                steps_id= HIT_OR_MISS_STEPS;
                break;
            case 4:
                steps_id= STARS_1_STEPS;
                break;
            case 5:
                steps_id= SHELTER_STEPS;
                break;
            case 6:
                steps_id= STARS_2_STEPS;
                break;
            default: steps_id=-1;
        }
        SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mContext.getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE).edit();
        int prev_value=prefs.getInt(String.valueOf(steps_id), 0);
        editor.putInt(String.valueOf(steps_id),prev_value+1);

        prev_value=prefs.getInt(String.valueOf(OVERALL_PROGRESS_STEPS),0);
        editor.putInt(String.valueOf(OVERALL_PROGRESS_STEPS), prev_value + 1);

        editor.apply();
    }

    private void updateScore(long currentScore)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(String.valueOf(STORY_MODE_SCORES),
            Context.MODE_PRIVATE).edit();
        editor.putLong(String.valueOf(getGameNumber()), currentScore);
        editor.apply();
    }
    private void updateStoryModeOverallScore()
    {
        //Calculate value
        long cumulativeScore[]=new long[8];
        SharedPreferences pref1 = mContext.getSharedPreferences(String.valueOf(STORY_MODE_SCORES),
                Context.MODE_PRIVATE);
        int i,lim;
        lim=540;
        for(i=1;i<=lim;i++)//Love Affair
            cumulativeScore[0]+=pref1.getLong(String.valueOf(i),0);
        lim+=240;
        for(;i<=lim;i++)//Thunder-Bolt
            cumulativeScore[1]+=pref1.getLong(String.valueOf(i),0);
        lim+=120;
        for(;i<=lim;i++)//Jet
            cumulativeScore[2]+=pref1.getLong(String.valueOf(i),0);
        lim+=240;
        for(;i<=lim;i++)//Hit or Miss
            cumulativeScore[3]+=pref1.getLong(String.valueOf(i),0);
        lim+=450;
        for(;i<=lim;i++)//Stars 1.0
            cumulativeScore[4]+=pref1.getLong(String.valueOf(i),0);
        lim+=510;
        for(;i<=lim;i++)//Shelter
            cumulativeScore[5]+=pref1.getLong(String.valueOf(i),0);
        lim+=330;
        for(;i<=lim;i++)//Stars 2.0
            cumulativeScore[6]+=pref1.getLong(String.valueOf(i),0);

        for(int j=0;j<7;j++)
            cumulativeScore[7]+=cumulativeScore[j];

        switch (M)
        {
            case 0:
            {
                updateModule_OverallScore(HelperClass.LOVE_AFFAIR_TOP_SCORE,cumulativeScore[M]);
                break;
            }
            case 1:
            {
                updateModule_OverallScore(HelperClass.THUNDER_BOLT_TOP_SCORE,cumulativeScore[M]);
                break;
            }
            case 2:
            {
                updateModule_OverallScore(HelperClass.JET_TOP_SCORE,cumulativeScore[M]);
                break;
            }
            case 3:
            {
                updateModule_OverallScore(HelperClass.HIT_OR_MISS_TOP_SCORE,cumulativeScore[M]);
                break;
            }
            case 4:
            {
                updateModule_OverallScore(HelperClass.STARS_1_TOP_SCORE,cumulativeScore[M]);
                break;
            }
            case 5:
            {
                updateModule_OverallScore(HelperClass.SHELTER_TOP_SCORE,cumulativeScore[M]);
                break;
            }
            case 6:
            {
                updateModule_OverallScore(HelperClass.STARS_2_TOP_SCORE,cumulativeScore[M]);
                break;
            }
        }

        //Update Overall Story Mode Score
        SharedPreferences pref2 = mContext.getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE);
        long x=pref2.getLong(String.valueOf(STORY_MODE_OVERALL_TOP_SCORE), 0);
        SharedPreferences.Editor editor = pref2.edit();
        editor.putLong(String.valueOf(STORY_MODE_OVERALL_TOP_SCORE), cumulativeScore[7]);
        editor.apply();
    }
    private void updateModule_OverallScore(int id,long score)
    {
        //Update value
        SharedPreferences pref2 = mContext.getSharedPreferences(String.valueOf(ACCOMPLISHMENTS),
                Context.MODE_PRIVATE);
        long x=pref2.getLong(String.valueOf(id), 0);
        SharedPreferences.Editor editor = pref2.edit();
        if(score>x)
        {
            editor.putLong(String.valueOf(id), score);
            editor.apply();
        }
    }

    private long getHighScore()
    {
        SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(STORY_MODE_SCORES),
                Context.MODE_PRIVATE);
        return prefs.getLong(String.valueOf(getGameNumber()), 0l);
    }

    private int getGameNumber()
    {
        int ModuleLevelCount[] = {18,8,4,8,15,17,11};
        int maxStages=6;
        int maxChallenges=5;
        int maxGamesInOneLevel = maxStages*maxChallenges;

        int gameNumber=0;
        for(int i=0;i<M;i++)
            gameNumber+=ModuleLevelCount[i]*maxGamesInOneLevel;

        gameNumber+= L*maxGamesInOneLevel;
        gameNumber+= S*maxChallenges;
        gameNumber+= C+1;
        return gameNumber;
    }

    private int getOnePlayerMovesLimit() {
        int boardSize =  mContext.objCardGame.RowSize*mContext.objCardGame.ColumnSize;
        int card_sets =  boardSize/3;
        int minMoves= (boardSize/2) + card_sets;
        int moves_limit = minMoves + (boardSize /(S+1));
        if(C==0)
            moves_limit+=card_sets;
        return moves_limit;
    }

    private boolean getResult()
    {
        if(C<2)
        {
            if(mContext.objCardGame.Player1_Moves<=getOnePlayerMovesLimit())
                return true;
        }
        else
        {
            if(mContext.objCardGame.PlayerOne_Score>mContext.objCardGame.PlayerTwo_Score)
                return true;
        }
        return false;
    }

    public void ShowLevelCompletedDialog()
    {
        boolean result = getResult();
        updateGameStatus(result);
        long currentScore = mContext.objCardGame.objGameSummary.Score;
        long topScore = getHighScore();
        if(currentScore>topScore)
        {
            updateScore(currentScore);
            updateStoryModeOverallScore();
            topScore=currentScore;
        }

        LayoutInflater inflater = mContext.getLayoutInflater();
        View view;
        //region Game Won Dialog
        if(result)
        {
            mContext.CURRENT_SCREEN = HelperClass.SCREEN_POST_GAME_DIALOG_YAY;
            view = inflater.inflate(R.layout.dialog_story_mode_summary_yay, null, true);

            View region_stars = view.findViewById(R.id.region_stars);
            if(C<2)
            {
                computeStarsFor1PlayerGame(region_stars);
                ((TextView) view.findViewById(R.id.tvChallengeLimit)).setText(String.valueOf(getOnePlayerMovesLimit()));
                ((TextView)view.findViewById(R.id.tvMoves)).setText(String.valueOf(mContext.objCardGame.Player1_Moves));
            }
            else
            {
                computeStarsFor2PlayerGame(region_stars);
                view.findViewById(R.id.region_moves_limit).setVisibility(View.GONE);
                view.findViewById(R.id.region_moves_made).setVisibility(View.GONE);
                view.findViewById(R.id.region_two_player_result_row_1).setVisibility(View.VISIBLE);
            }

            ((TextView)view.findViewById(R.id.tvScore)).setText(String.valueOf(currentScore));
            ((TextView)view.findViewById(R.id.tvTopScore)).setText(String.valueOf(topScore));
            String gameNumber = "#Game "+String.valueOf(getGameNumber()) ;
            ((TextView) view.findViewById(R.id.tvGameNumber)).setText(gameNumber);
            view.findViewById(R.id.btnGameSummary).setOnClickListener(this);
            TextView tvNext = (TextView)view.findViewById(R.id.btnNext);
            tvNext.setOnClickListener(this);
            Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
            tvNext.setTypeface(font);
            view.findViewById(R.id.btnRestart).setOnClickListener(this);
            view.findViewById(R.id.btn_store).setOnClickListener(this);
            view.findViewById(R.id.btnStore).setOnClickListener(this);
            view.findViewById(R.id.tv_share).setOnClickListener(this);

            View tvShare = view.findViewById(R.id.tvShare);
            AnimationSet Beats_InOut = Beats_InOut();
            tvShare.startAnimation(Beats_InOut);
            tvShare.setOnClickListener(this);
        }
        //endregion
        //region Game Lost
        else
        {
            mContext.CURRENT_SCREEN = HelperClass.SCREEN_POST_GAME_DIALOG_OH_NO;
            view = inflater.inflate(R.layout.dialog_story_mode_summary_oh_no, null, true);

            if(C<2)
            {
                ((TextView) view.findViewById(R.id.tvChallengeLimit)).setText(String.valueOf(getOnePlayerMovesLimit()));
                ((TextView)view.findViewById(R.id.tvMoves)).setText(String.valueOf(mContext.objCardGame.Player1_Moves));
            }
            else
            {
                view.findViewById(R.id.region_moves_limit).setVisibility(View.GONE);
                view.findViewById(R.id.region_moves_made).setVisibility(View.GONE);
                view.findViewById(R.id.region_two_player_result_row_1).setVisibility(View.VISIBLE);

                TextView tvRow3 =  (TextView)view.findViewById(R.id.tvRow3);
                tvRow3.setText("Try Again");

                if(mContext.objCardGame.PlayerOne_Score==mContext.objCardGame.PlayerTwo_Score)
                {
                    TextView tvHeader = (TextView)view.findViewById(R.id.tvHeader);
                    tvHeader.setText("It's a tie!");
                }

            }

            view.findViewById(R.id.btnGameSummary).setOnClickListener(this);
            view.findViewById(R.id.btnRestart).setOnClickListener(this);

            TextView btnNext = (TextView)view.findViewById(R.id.btnNext);
            int []nextGame = getNextGame();
            int nxt_m = nextGame[0];
            int nxt_l = nextGame[1];
            int nxt_s = nextGame[2];
            int nxt_c = nextGame[3];
            if(getCompletionStatus(nxt_m,nxt_l,nxt_s,nxt_c)==STATUS_LOCKED)
            {
                btnNext.setClickable(false);
                btnNext.setTextColor(Color.argb(153,170,170,170));
            }
            else
            {
                btnNext.setTextColor(Color.WHITE);
                btnNext.setClickable(true);
                btnNext.setOnClickListener(this);
            }

            View btn_restart = view.findViewById(R.id.btn_restart);
            btn_restart.setOnClickListener(this);
            Animation rotateAnim = RotateAnim();
            btn_restart.startAnimation(rotateAnim);

            View btnBuy = view.findViewById(R.id.btnBuy);
            Animation zoomInOut = ZoomInOut_Slow();
            btnBuy.startAnimation(zoomInOut);
            btnBuy.setOnClickListener(this);

        }
        //endregion


        if(mContext.CommonDialog==null)
            mContext.CommonDialog = new AlertDialog.Builder(mContext).show();
        else
            mContext.CommonDialog.show();
        mContext.CommonDialog.setCancelable(false);
        mContext.CommonDialog.setContentView(view);
        mContext.CommonDialog.setOnKeyListener(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mContext.CommonDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        View v2 =  mContext.CurrentView;
        lp.width = v2.getMeasuredWidth() - ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private void computeStarsFor1PlayerGame(View regionStars)
    {
        int moves = mContext.objCardGame.Player1_Moves;
        int maxLimit = getOnePlayerMovesLimit();
        int minLimit = mContext.objCardGame.RowSize*mContext.objCardGame.ColumnSize;
        minLimit = (minLimit/2) + (minLimit/3);
        int totalMiniStars = Math.round((float) 11 * (maxLimit - moves) / (maxLimit - minLimit) + 1);
        totalMiniStars+=3;
        createStarts(totalMiniStars,regionStars);
    }
    private void computeStarsFor2PlayerGame(View regionStars)
    {
        int maxHits = mContext.objCardGame.RowSize*mContext.objCardGame.ColumnSize/3;
        int minHits = (maxHits+1)/2;
        int playerOneHits = mContext.objCardGame.PlayerOne_Score;
        int totalMiniStars = Math.round((float) 7 * (playerOneHits - minHits) / (maxHits - minHits) + 1);
        totalMiniStars+=7;
        createStarts(totalMiniStars,regionStars);
    }
    private void createStarts(int totalMiniStars,View regionStars)
    {
        if(totalMiniStars>15)
            totalMiniStars=15;
        writeStars(totalMiniStars);
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
    private void writeStars(int totalMiniStars)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(String.valueOf(STORY_MODE_STARS),
                Context.MODE_PRIVATE).edit();
        editor.putInt(String.valueOf(getGameNumber()), totalMiniStars);
        editor.apply();
    }

    private void NextGame()
    {
        //region create next_game values
        int []nextGame = getNextGame();
        int nxt_m = nextGame[0];
        int nxt_l = nextGame[1];
        int nxt_s = nextGame[2];
        int nxt_c = nextGame[3];
        //endregion

        StartGame objStartGame = new StartGame(new WeakReference<>(mContext));
        objStartGame.setStoryModeData(nxt_m, nxt_l, nxt_s, nxt_c);
        objStartGame.showObjective();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mContext.CommonDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        mContext.CommonDialog.findViewById(R.id.btnClose).setBackgroundColor(Color.BLACK);
    }

    private int getCompletionStatus(int M,int L,int S,int C)
    {
        String id = String.valueOf(M)+"_"+String.valueOf(L)+
                "_"+String.valueOf(S)+"_"+String.valueOf(C);

        SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(STORY_MODE_DATA),
                Context.MODE_PRIVATE);
        //get data from preferences
        return prefs.getInt(id,getDefaultCompletionStatus(M,L,S,C));
    }
    private int getDefaultCompletionStatus(int M,int L,int S,int C)
    {
        int ModuleLevelCount[] = {18,8,4,8,15,17,11};
        if(ModuleLevelCount[M]-1==L)
            return STATUS_NEW;

        if(L<1 && S<1 && C<1)
            return STATUS_NEW;
        else
            return STATUS_LOCKED;
    }

    private int[] getNextGame()
    {
        int ModuleLevelCount[] = {18,8,4,8,15,17,11};
        int nxt_m = M;
        int nxt_l = L;
        int nxt_s = S;
        int nxt_c = C+1;

        if (nxt_c>4)
        {
            nxt_c=0;
            nxt_s++;
            if(nxt_s>5)
            {
                nxt_s=0;
                nxt_l++;
                if(nxt_l>=ModuleLevelCount[nxt_m])
                {
                    nxt_l=0;
                    nxt_m++;
                    if(nxt_m>=ModuleLevelCount.length)
                    {
                        nxt_m=0;
                    }
                }
            }
        }

        int nextGame[]= {nxt_m,nxt_l,nxt_s,nxt_c};
        return nextGame;
    }
    public AnimationSet Beats_InOut()
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

    public Animation RotateAnim()
    {

        RotateAnimation rotate = new RotateAnimation(360f,0f,Animation.RELATIVE_TO_SELF,.5f,Animation.RELATIVE_TO_SELF,.5f);
        rotate.setDuration(800);
        rotate.setStartOffset(500);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new AccelerateInterpolator(1.1f));

        AnimationSet Reload = new AnimationSet(true);
        Reload.addAnimation(rotate);
        Reload.setInterpolator(new AccelerateInterpolator(1.1f));
        return rotate;
    }
    public Animation ZoomInOut_Slow()
    {
        ScaleAnimation zoom = new ScaleAnimation(.9f,1f,.9f,1f,
                Animation.RELATIVE_TO_SELF,.5f,
                Animation.RELATIVE_TO_SELF, .5f);
        zoom.setDuration(2700);
        zoom.setStartOffset(100);
        zoom.setFillAfter(true);
        zoom.setRepeatMode(Animation.REVERSE);
        zoom.setRepeatCount(Animation.INFINITE);
        return zoom;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnGameSummary:
                mContext.CommonDialog.dismiss();
                mContext.objCardGame.StoryMode=false;
                mContext.objCardGame.objGameSummary.loadSummaryScreen_StoryMode();
                break;
            case R.id.btnNext:
                NextGame();
                break;
            case R.id.btnRestart:
            case R.id.btn_restart:
                mContext.objCardGame.resetGameData();
                mContext.objCardGame.CardSet = STORY_MODE_CARD_SET;
                if(mContext.objCardGame.BoardType == THREE_BOARD)
                    mContext.objCardGame.RowSize/=3;
                mContext.objCardGame.createGame();
                mContext.CommonDialog.dismiss();
                break;
            case R.id.btnStore:
            case R.id.btn_store:
            case R.id.btnBuy:
            case R.id.btn_buy:
                mContext.CommonDialog.dismiss();
                mContext.loadStoreScreen();
                mContext.objCardGame.CleanUp();
                break;
            case R.id.btnShare:
            case R.id.btn_share:
            case R.id.tvShare:
            case R.id.tv_share:
            {
                String msg =
                        "Check out my score" + "\n" +
                                "https://play.google.com/store/apps/details?id=com.archer.matching_card_game.three_cards";
                takeScreenShotAndShare(msg);
            }
                break;
        }
    }

    private void takeScreenShotAndShare(String msg)
    {
        //region create screenshot
        View mainView = mContext.getWindow().getDecorView().getRootView();
        View dialogView = mContext.CommonDialog.getWindow().getDecorView().getRootView();

        mainView.setDrawingCacheEnabled(true);
        Bitmap bitmap = mainView.getDrawingCache(); //screenshot for background view

        dialogView.setDrawingCacheEnabled(true);
        Bitmap bitmap2 = dialogView.getDrawingCache(); //screenshot for dialog

        int location[] = new int[2];
        int location2[] = new int[2];
        mainView.getLocationOnScreen(location);
        dialogView.getLocationOnScreen(location2);

        //Create a transparent dark layer to add to background
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(0xFF999999, 0x00000000);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap,location[0],location[1],p);//Add dark layer to background
        canvas.drawBitmap(bitmap2, location2[0] - location[0], location2[1] - location[1],
                new Paint()); //draw dialog over background

        File imageFile = new File(mContext.getFilesDir(),"TwoCards.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            switch (mContext.CURRENT_SCREEN)
            {
                case SCREEN_POST_GAME_DIALOG_NORMAL:
                case SCREEN_POST_GAME_DIALOG_YAY:
                case SCREEN_POST_GAME_DIALOG_OH_NO:
                    mContext.CommonDialog.findViewById(R.id.btnGameSummary).performClick();
                    break;
            }
        }
        return false;
    }

}
