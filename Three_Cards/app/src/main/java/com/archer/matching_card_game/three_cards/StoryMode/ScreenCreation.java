package com.archer.matching_card_game.three_cards.StoryMode;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.archer.matching_card_game.three_cards.HelperClass;
import com.archer.matching_card_game.three_cards.MainActivity;
import com.archer.matching_card_game.three_cards.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.CURRENT_GAME_ID;
import static com.archer.matching_card_game.three_cards.HelperClass.ConvertToPx;
import static com.archer.matching_card_game.three_cards.HelperClass.RICHIE_RICH;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_COMPLETION_STATUS;
import static com.archer.matching_card_game.three_cards.HelperClass.STORY_MODE_DATA;
import static com.archer.matching_card_game.three_cards.HelperClass.SetFontToControls;
import static com.archer.matching_card_game.three_cards.HelperClass.getWindowSize;

public class ScreenCreation implements View.OnClickListener {

    final int STATUS_LOCKED = 101;
    final int STATUS_COMPLETED = 102;
    final int STATUS_NEW = 103;
    final int STATUS_IN_PROGRESS = 104;
    MainActivity mContext;
    View.OnClickListener ProcessLevel;
    View.OnClickListener ProcessStage;
    View.OnClickListener ProcessChallenge;
    View.OnClickListener NextClick;
    View.OnClickListener StartContinueClick; 
    View tv_module_header;
    View btn_expand_collapse;
    View module_levels;
    String Modules[] = {"Love Affair","Thunder-bolt","Jet","Hit or Miss","Stars 1.0","Shelter","Stars 2.0"};
    int ModuleLevelCount[] = {18,8,4,8,15,17,11};
    int SelectedModule;
    int SelectedLevel;
    int SelectedStage;
    int SelectedChallenge;
    int CurrentModule;
    int CurrentLevel;
    int CurrentStage;
    int CurrentChallenge;

    int CountOfCompletedGames;
    int TotalGames;

    public ScreenCreation(WeakReference<MainActivity> m_context)
    {
        mContext = m_context.get();
        InitializeClickListeners();
        setCurrentGameAndOverallCompletionStatusData();
    }

    public void show()
    {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.screen_story_mode, null, false);
        if(!mContext.isSignedIn())
        {
            view.findViewById(R.id.google_sign_in_section).setVisibility(View.VISIBLE);
            view.findViewById(R.id.region_accomplishments).setVisibility(View.GONE);
            view.findViewById(R.id.region_leader_board_overall_progress).setVisibility(View.GONE);
        }
        else
        {
            view.findViewById(R.id.google_sign_in_section).setVisibility(View.GONE);
            view.findViewById(R.id.region_accomplishments).setVisibility(View.VISIBLE);
            view.findViewById(R.id.region_leader_board_overall_progress).setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.btnAchievement).setOnClickListener(this);
        view.findViewById(R.id.btnLeaderboard).setOnClickListener(this);
        view.findViewById(R.id.btnQuest).setOnClickListener(this);


        ScrollView mainContainer = (ScrollView)view.findViewById(R.id.mainContainer);
        mainContainer.removeAllViews();

        LinearLayout linear_layout = new LinearLayout(mContext);
        linear_layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linear_layout.setLayoutParams(layoutParams);

        mainContainer.addView(linear_layout);

        int modules_length = Modules.length;
        for(int i=0;i<modules_length;i++)
        {
            linear_layout.addView(getModule(i));
            linear_layout.addView(getDivider(HelperClass.ConvertToPx(mContext,2), Color.BLACK));
            setClickListenerForExpandCollapseButton();
            if(i==CurrentModule)
            {
                module_levels.setVisibility(View.VISIBLE);
                btn_expand_collapse.setBackgroundResource(R.drawable.btn_collapse);
            }
        }

        TextView tvPercentCompleted = (TextView)view.findViewById(R.id.tvPercentCompleted);
        Button btnStartContinue = (Button)view.findViewById(R.id.btnStartContinue);
        int percent_completed = Math.round((float)CountOfCompletedGames/TotalGames*100);
        tvPercentCompleted.setText(String.valueOf(percent_completed));
        tvPercentCompleted.setTextColor(getPercentCompletedTextColor(percent_completed));
        if(CountOfCompletedGames==0)
            btnStartContinue.setText("Start");
        btnStartContinue.setOnClickListener(StartContinueClick);

        if (mContext.CurrentView != null)
            mContext.CurrentView.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out));

        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
        mContext.setContentView(view);
        mContext.CurrentView = view;
        mContext.CURRENT_SCREEN = R.layout.screen_story_mode;

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
        SetFontToControls(font, (ViewGroup) view);
        ((TextView)view.findViewById(R.id.tv_why_sign_in)).setTypeface(Typeface.MONOSPACE);

        view.findViewById(R.id.btn_leader_board_overall_progress).setOnClickListener(this);
        view.findViewById(R.id.tv_leader_board_overall_progress).setOnClickListener(this);


        //region Check if Ad-Free version
        if(!mContext.adFreeVersion)
        {
            final AdView mAdView = (AdView)view.findViewById(R.id.adView);
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

    private void InitializeClickListeners()
    {
        final ScreenCreation thisContext = this;
        ProcessLevel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = String.valueOf(v.getTag());
                createGameDialog(tag);
            }
        };
        ProcessStage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedStage(v.getRootView(), Integer.parseInt(String.valueOf(v.getTag())) - 1);

                setSelectedChallenge(v.getRootView(), 0);
            }
        };
        ProcessChallenge = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedChallenge(v.getRootView(), Integer.parseInt(String.valueOf(v.getTag())) - 1);
            }
        };
        NextClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.archer.matching_card_game.three_cards.StoryMode.StartGame objStartGame =
                        new StartGame(new WeakReference<>(mContext),new WeakReference<>(thisContext));
                objStartGame.setStoryModeData(SelectedModule, SelectedLevel, SelectedStage, SelectedChallenge);
                objStartGame.showObjective();
            }
        };
        StartContinueClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mContext.CommonDialog==null)
                {
                    mContext.CommonDialog  = new AlertDialog.Builder(mContext).show();
                    mContext.CommonDialog.setCancelable(true);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = mContext.CommonDialog.getWindow();
                    lp.copyFrom(window.getAttributes());
                    View v2 =  mContext.CurrentView;
                    lp.width = v2.getMeasuredWidth() - ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height =  WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(lp);
                }
                else
                    mContext.CommonDialog.show();

                com.archer.matching_card_game.three_cards.StoryMode.StartGame objStartGame =
                        new StartGame(new WeakReference<>(mContext),new WeakReference<>(thisContext));
                objStartGame.setStoryModeData(CurrentModule, CurrentLevel, CurrentStage, CurrentChallenge);
                objStartGame.showObjective();
            }
        };
    }

    private void setClickListenerForExpandCollapseButton()
    {
        final View levelsView = module_levels;
        final View btnExpandCollapse = btn_expand_collapse;
        View.OnClickListener expand_collapse = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (levelsView.getVisibility() == View.VISIBLE) {
                    levelsView.setVisibility(View.GONE);
                    btnExpandCollapse.setBackgroundResource(R.drawable.btn_expand);
                } else {
                    levelsView.setVisibility(View.VISIBLE);
                    levelsView.getParent().requestChildFocus(levelsView, levelsView);
                    btnExpandCollapse.setBackgroundResource(R.drawable.btn_collapse);
                }
            }
        };
        btn_expand_collapse.setOnClickListener(expand_collapse);
        tv_module_header.setOnClickListener(expand_collapse);

    }

    private View getModule(int module_index)
    {
        LinearLayout linear_Layout = new LinearLayout(mContext);
        linear_Layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linear_Layout.setLayoutParams(layoutParams);

        linear_Layout.addView(getTitleBar(module_index));
        linear_Layout.addView(getDivider(1, Color.BLACK));
        linear_Layout.addView(getLevels(module_index));

        return linear_Layout;
    }

    private View getTitleBar(int header_index)
    {
        LinearLayout linear_layout = new LinearLayout(mContext);
        linear_layout.setOrientation(LinearLayout.HORIZONTAL);
        linear_layout.setBackgroundColor(Color.argb(17, 250, 250, 250));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linear_layout.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams layout_params1 = new LinearLayout.LayoutParams(0,
                HelperClass.ConvertToPx(mContext,36),1);
        TextView tv = new TextView(mContext);
        tv.setPadding(HelperClass.ConvertToPx(mContext, 9), 0, 0, 0);
        tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        tv.setText(Modules[header_index]);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setTypeface(Typeface.DEFAULT);
        tv.setLayoutParams(layout_params1);
        tv_module_header = tv;
        linear_layout.addView(tv);

        //region Leader-Board option
        addLeaderBoardControls(linear_layout,header_index);
        //endregion

        RelativeLayout cell = new RelativeLayout(mContext);
        LinearLayout.LayoutParams layout_params2 = new LinearLayout.LayoutParams(
                HelperClass.ConvertToPx(mContext,36), HelperClass.ConvertToPx(mContext,36));
        layout_params2.gravity = Gravity.CENTER;
        cell.setLayoutParams(layout_params2);
        int five_dp = HelperClass.ConvertToPx(mContext, 5);
//        int two_dp = HelperClass.ConvertToPx(mContext, 2);
        cell.setPadding(five_dp, five_dp, five_dp, five_dp);
        TextView btn_expand = new TextView(mContext);
        RelativeLayout.LayoutParams layoutParams_r = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams_r.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btn_expand.setLayoutParams(layoutParams_r);
        btn_expand.setGravity(Gravity.CENTER);
        btn_expand.setBackgroundResource(R.drawable.btn_expand);
        btn_expand.setClickable(true);
        btn_expand.setLayoutParams(layoutParams_r);
        btn_expand_collapse = btn_expand;
        cell.addView(btn_expand);

        linear_layout.addView(cell);
        return linear_layout;
    }

    private void addLeaderBoardControls(LinearLayout linear_layout,int header_index) {
        LinearLayout linear_layout_leader_board_region = new LinearLayout(mContext);
        linear_layout_leader_board_region.setOrientation(LinearLayout.HORIZONTAL);
        linear_layout_leader_board_region.setPadding(10, 0, 20, 0);
        if(mContext.isSignedIn())
            linear_layout_leader_board_region.setVisibility(View.VISIBLE);
        else
            linear_layout_leader_board_region.setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParams_2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        linear_layout_leader_board_region.setLayoutParams(layoutParams_2);
        linear_layout_leader_board_region.setId(getId(header_index, 0));

        LinearLayout.LayoutParams layoutParams_3 = new LinearLayout.LayoutParams(HelperClass.ConvertToPx(mContext, 26),
                HelperClass.ConvertToPx(mContext, 26));
        layoutParams_3.gravity = Gravity.CENTER;
        Button btn_leader_board = new Button(mContext);
        btn_leader_board.setGravity(Gravity.CENTER);
        btn_leader_board.setBackgroundResource(R.drawable.btn_gs_top_scores);
        btn_leader_board.setLayoutParams(layoutParams_3);
        btn_leader_board.setId(getId(header_index, 1));
        btn_leader_board.setOnClickListener(this);
        linear_layout_leader_board_region.addView(btn_leader_board);

        LinearLayout.LayoutParams layoutParams_4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams_4.gravity = Gravity.CENTER;
        TextView tv_leader_board = new TextView(mContext);
        tv_leader_board.setGravity(Gravity.CENTER);
        tv_leader_board.setPadding(HelperClass.ConvertToPx(mContext, 4), 0, 0, 0);
        tv_leader_board.setText("Check\nLeader-Board");
        tv_leader_board.setTextColor(Color.BLACK);
        tv_leader_board.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        tv_leader_board.setTypeface(Typeface.MONOSPACE);
        tv_leader_board.setLayoutParams(layoutParams_4);
        tv_leader_board.setOnClickListener(this);
        tv_leader_board.setId(getId(header_index, 2));
        linear_layout_leader_board_region.addView(tv_leader_board);

        linear_layout.addView(linear_layout_leader_board_region);
    }

    private int getId(int header_index,int x) {
        int ids[][]=new int[7][3];
        ids[0][0]=R.id.region_leader_board_love_affair;
        ids[0][1]=R.id.btn_leader_board_love_affair;
        ids[0][2]=R.id.tv_leader_board_love_affair;
        ids[1][0]=R.id.region_leader_board_thunder_bolt;
        ids[1][1]=R.id.btn_leader_board_thunder_bolt;
        ids[1][2]=R.id.tv_leader_board_thunder_bolt;
        ids[2][0]=R.id.region_leader_board_jet;
        ids[2][1]=R.id.btn_leader_board_jet;
        ids[2][2]=R.id.tv_leader_board_jet;
        ids[3][0]=R.id.region_leader_board_hit_or_miss;
        ids[3][1]=R.id.btn_leader_board_hit_or_miss;
        ids[3][2]=R.id.tv_leader_board_hit_or_miss;
        ids[4][0]=R.id.region_leader_board_stars_1;
        ids[4][1]=R.id.btn_leader_board_stars_1;
        ids[4][2]=R.id.tv_leader_board_stars_1;
        ids[5][0]=R.id.region_leader_board_shelter;
        ids[5][1]=R.id.btn_leader_board_shelter;
        ids[5][2]=R.id.tv_leader_board_shelter;
        ids[6][0]=R.id.region_leader_board_stars_2;
        ids[6][1]=R.id.btn_leader_board_stars_2;
        ids[6][2]=R.id.tv_leader_board_stars_2;
        return ids[header_index][x];
    }

    private View getLevels(int module_index)
    {
        LinearLayout linear_layout_1 = new LinearLayout(mContext);
        module_levels = linear_layout_1;

        linear_layout_1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linear_layout_1.setLayoutParams(layoutParams1);
        linear_layout_1.setVisibility(View.GONE);

        int max_levels = ModuleLevelCount[module_index];
        int level_count = 1;

        for(int i=0;i<5 && level_count<=max_levels;i++)
        {
            LinearLayout linear_layout_2 = new LinearLayout(mContext);
            linear_layout_2.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            linear_layout_2.setLayoutParams(layoutParams2);

            int five_dp = HelperClass.ConvertToPx(mContext,5);
            linear_layout_2.setPadding(five_dp, five_dp, five_dp, five_dp);
            linear_layout_1.addView(linear_layout_2);

            for (int j=0;j<5;j++)
            {
                int level_value = (level_count>max_levels)? 0 : level_count++;
                linear_layout_2.addView(getCell(module_index,level_value));
            }
        }
        return linear_layout_1;
    }

    private RelativeLayout getCell(int module_index,int level_value)
    {
        Point windowSize = getWindowSize(mContext.getWindowManager().getDefaultDisplay());
        int screen_width = windowSize.x - HelperClass.ConvertToPx(mContext,150);

        RelativeLayout cell = new RelativeLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT,1f );
        cell.setLayoutParams(layoutParams);
//        int five_dp = HelperClass.ConvertToPx(mContext, 5);
        int two_dp = HelperClass.ConvertToPx(mContext, 2);
        cell.setPadding(two_dp, two_dp, two_dp, two_dp);

        if(level_value>0)
        {
            TextView tv = new TextView(mContext);
            RelativeLayout.LayoutParams layoutParams_r = new RelativeLayout.LayoutParams(
                    screen_width / 5, screen_width / 5);
            layoutParams_r.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            tv.setLayoutParams(layoutParams_r);
            tv.setTextSize(18);
            tv.setGravity(Gravity.CENTER);
            tv.setHeight(screen_width / 5);
            tv.setWidth(screen_width / 5);
            tv.setTextColor(Color.BLACK);
            tv.setText(String.valueOf(level_value));
            tv.setOnClickListener(ProcessLevel);
            tv.setTag(String.valueOf(module_index + 1) + "_" + String.valueOf(level_value));
            cell.addView(tv);

            setLevelBackground(module_index,level_value-1,tv);

        }
        return cell;

    }

    protected View getDivider(int height,int color)
    {
        RelativeLayout v = new RelativeLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        v.setLayoutParams(layoutParams);
        v.setBackgroundColor(color);
        return v;
    }

    private void displayGameDialog(View v,boolean isCancelable)
    {
        if(mContext.CommonDialog==null)
            mContext.CommonDialog  = new AlertDialog.Builder(mContext).show();
        else
            mContext.CommonDialog.show();

        mContext.CommonDialog.setContentView(v);
//        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
//        SetFontToControls(font, (ViewGroup) v);
        mContext.CommonDialog.setCancelable(isCancelable);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mContext.CommonDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        View v2 =  mContext.CurrentView;
        lp.width = v2.getMeasuredWidth() - ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
    
    private void createGameDialog(String tag)
    {
        SelectedModule = Integer.valueOf(String.valueOf(tag).split("_")[0])-1;
        SelectedLevel = Integer.valueOf(String.valueOf(tag).split("_")[1])-1;

        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_story_mode_game_select, null, true);
        view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.CommonDialog.dismiss();
            }
        });
        int result[] = getCurrentStageAndChallenge(SelectedModule, SelectedLevel);
        setSelectedStage(view, result[0]);
        setSelectedChallenge(view, result[1]);

        TextView tvPercentCompleted = (TextView)view.findViewById(R.id.tvPercentCompleted);
        int percent_completed = getPercentCompleted();
        tvPercentCompleted.setText(String.valueOf(percent_completed));
        tvPercentCompleted.setTextColor(getPercentCompletedTextColor(percent_completed));

        TextView btnNext = (TextView)view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(NextClick);

        displayGameDialog(view, true);
    }

    private void setLevelBackground(int p,int q, TextView tv)
    {
        int maxStages = 6;
        int maxChallenges = 5;

        switch (getCompletionStatus(p,q,0,0))
        {
            case STATUS_LOCKED:
                tv.setText("");
                tv.setOnClickListener(null);
                tv.setBackgroundResource(R.drawable.background_level_lock);
                return;
            case STATUS_NEW:
                tv.setBackgroundResource(R.drawable.btn_new_game);
                return;
        }

        for(int s=0;s<maxChallenges;s++)
        {
            if(getCompletionStatus(p,q,maxStages-1,s)!=STATUS_COMPLETED)
            {
                tv.setBackgroundResource(R.drawable.btn_in_progress_game);
                return;
            }
        }
        tv.setBackgroundResource(R.drawable.btn_completed_game);
    }

    private void setStageBackground(int p,int q,int r,int s,TextView tv)
    {
        switch (getCompletionStatus(p,q,r,s))
        {
            case STATUS_LOCKED:
                tv.setText("");
                tv.setOnClickListener(null);
                tv.setBackgroundResource(R.drawable.background_level_lock);
                break;
            case STATUS_NEW:
            case STATUS_IN_PROGRESS:
            case STATUS_COMPLETED:
                tv.setBackgroundResource(R.drawable.btn_white_transparency_20);
                break;
        }
    }

    private void setChallengeBackground(int p,int q,int r,int s,TextView tv)
    {
        int allBackGrounds[] = {R.drawable.btn_circle_green1_no_border,R.drawable.btn_circle_green2_no_border,
                R.drawable.btn_circle_green3_no_border,R.drawable.btn_circle_green4_no_border,
                R.drawable.btn_circle_green5_no_border};
        String allChallengeText[] = {"C1","C2","C3","C4","C5"};
        switch (getCompletionStatus(p,q,r,s))
        {
            case STATUS_LOCKED:
                tv.setText("");
                tv.setOnClickListener(null);
                tv.setBackgroundResource(R.drawable.background_level_lock);
                break;
            case STATUS_NEW:
            case STATUS_COMPLETED:
            case STATUS_IN_PROGRESS:
                tv.setText(allChallengeText[s]);
                tv.setBackgroundResource(allBackGrounds[s]);
                break;
        }
    }

    private void setSelectedStage(View m_context,int selected_index)
    {
        TextView tvStage1 = (TextView)m_context.findViewById(R.id.tvStage1);
        TextView tvStage2 = (TextView)m_context.findViewById(R.id.tvStage2);
        TextView tvStage3 = (TextView)m_context.findViewById(R.id.tvStage3);
        TextView tvStage4 = (TextView)m_context.findViewById(R.id.tvStage4);
        TextView tvStage5 = (TextView)m_context.findViewById(R.id.tvStage5);
        TextView tvStage6 = (TextView)m_context.findViewById(R.id.tvStage6);
        TextView allStages[] = {tvStage1,tvStage2,tvStage3,tvStage4,tvStage5,tvStage6};
        String allStages_Text[] = {"I","II","III","IV","V","VI"};
        int max_stages = allStages.length;
        for(int i=0;i<max_stages;i++)
        {
            TextView tv = allStages[i];
            tv.setText(allStages_Text[i]);
            tv.setOnClickListener(ProcessStage);
            setStageBackground(SelectedModule, SelectedLevel, i, 0, tv);
            if(i==selected_index)
                tv.setBackgroundResource(R.drawable.btn_white_pressed_thin_border);
        }
        SelectedStage = selected_index;
    }

    private void setSelectedChallenge(View m_context,int selected_index)
    {
        TextView tvChallenge1 = (TextView)m_context.findViewById(R.id.tvChallenge1);
        TextView tvChallenge2 = (TextView)m_context.findViewById(R.id.tvChallenge2);
        TextView tvChallenge3 = (TextView)m_context.findViewById(R.id.tvChallenge3);
        TextView tvChallenge4 = (TextView)m_context.findViewById(R.id.tvChallenge4);
        TextView tvChallenge5 = (TextView)m_context.findViewById(R.id.tvChallenge5);
        TextView allChallenges[] = {tvChallenge1,tvChallenge2,tvChallenge3,tvChallenge4,tvChallenge5};
        int selectedBackgrounds[] = { R.drawable.btn_circle_green1_white_border,
                R.drawable.btn_circle_green2_white_border,R.drawable.btn_circle_green3_white_border,
                R.drawable.btn_circle_green4_white_border,R.drawable.btn_circle_green5_white_border};

        int max_challenges = allChallenges.length;
        for(int i=0;i<max_challenges;i++)
        {
            TextView tv = allChallenges[i];
            tv.setOnClickListener(ProcessChallenge);
            setChallengeBackground(SelectedModule, SelectedLevel, SelectedStage, i, tv);
            if(i==selected_index)
                tv.setBackgroundResource(selectedBackgrounds[i]);
        }

        SelectedChallenge = selected_index;
    }

    private int getPercentCompleted()
    {
        int max_stages = 6;
        int max_challenges = 5;
        int completed_count = 0;
        for(int i=0;i<max_stages;i++)
        {
            for(int j=0;j<max_challenges;j++)
            {
                if(getCompletionStatus(SelectedModule,SelectedLevel,i,j)==STATUS_COMPLETED)
                    completed_count++;
            }
        }
        return Math.round((float) completed_count / (max_challenges * max_stages)*100);
    }

    private int getDefaultCompletionStatus(int M,int L,int S,int C)
    {
        if(L<1 && S<1 && C<1)
            return STATUS_NEW;
        else
            return STATUS_LOCKED;
    }

    private void setCurrentGameAndOverallCompletionStatusData( )
    {
        CountOfCompletedGames=0;
        TotalGames=0;
        SharedPreferences prefs = mContext.getSharedPreferences(String.valueOf(STORY_MODE_DATA), Context.MODE_PRIVATE);
        for(int p=0;p<ModuleLevelCount.length;p++)
        {
            int max_levels = ModuleLevelCount[p];
            for (int q=0;q<max_levels;q++)
            {
                int number_of_stages = 6;
                for(int r=0;r<number_of_stages;r++)
                {
                    int number_of_challenges = 5;
                    for (int s=0;s<number_of_challenges;s++)
                    {
                        TotalGames++;
                        String id = String.valueOf(p)+"_"+String.valueOf(q)+
                                "_"+String.valueOf(r)+"_"+String.valueOf(s);
                        //get data from preferences
                        if(prefs.getInt(id,getDefaultCompletionStatus(p,q,r,s))==STATUS_COMPLETED)
                            CountOfCompletedGames++;
                    }
                }
            }
        }

        long percent_completed = CountOfCompletedGames*10000/TotalGames;
        SharedPreferences prefs_2 = mContext.getSharedPreferences(String.valueOf(HelperClass.ACCOMPLISHMENTS),
                Context.MODE_PRIVATE);
        long x=prefs_2.getLong(String.valueOf(STORY_MODE_COMPLETION_STATUS), 0);
        if(x!=percent_completed)
        {
            SharedPreferences.Editor editor = prefs_2.edit();
            editor.putLong(String.valueOf(HelperClass.STORY_MODE_COMPLETION_STATUS), percent_completed);
            editor.apply();
        }

        String currentGame = prefs.getString(String.valueOf(CURRENT_GAME_ID),"0_0_0_0");
        CurrentModule = Integer.parseInt(currentGame.split("_")[0]);
        CurrentLevel = Integer.parseInt(currentGame.split("_")[1]);
        CurrentStage = Integer.parseInt(currentGame.split("_")[2]);
        CurrentChallenge = Integer.parseInt(currentGame.split("_")[3]);
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

    private int getPercentCompletedTextColor(int percent)
    {
        int colors[]={Color.argb(255,255,33,0), Color.argb(255,255,90,0), Color.argb(255,255,121,0),
                Color.argb(255,255,160,0), Color.argb(255,255,195,0), Color.argb(255,255,255,0),
                Color.argb(255,200,255,0), Color.argb(255,150,255,0), Color.argb(255,100,255,0),
                Color.argb(255,50,255,0), Color.argb(255,0,255,0)};
        return colors[percent/10];
    }

    private int[] getCurrentStageAndChallenge(int module_index,int level_index)
    {
        int result[]=new int[2];
        int max_stages = 6;
        int max_challenges = 5;
        for (int i=0;i<max_stages;i++)
        {
            for(int j=0;j<max_challenges;j++)
            {
                if(getCompletionStatus(module_index,level_index,i,j)!=STATUS_COMPLETED)
                {
                    result[0]=i;
                    result[1]=j;
                    return result;
                }
            }
        }
        result[0]=max_stages-1;
        result[1]=max_challenges-1;
        return result;
    }

    public void cleanUp()
    {
        Runnable myRunnable = new Runnable(){
            public void run(){
                ProcessLevel=null;
                ProcessStage=null;
                ProcessChallenge=null;
                NextClick=null;
                StartContinueClick=null;
                tv_module_header=null;
                btn_expand_collapse=null;
                module_levels=null;
                Modules=null;
                ModuleLevelCount=null;
                System.gc();
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnAchievement:
            {
                mContext.onShowAchievementsRequested();
                break;
            }
            case R.id.btnLeaderboard:
            {
                showLeaderBoardDialog();
                break;
            }
            case R.id.btnQuest:
            {
                mContext.showQuests();
                break;
            }
            case R.id.sign_in_button:
            {
                mContext.onSignInButtonClicked();
                break;
            }
            case R.id.btn_leader_board_love_affair:
            case R.id.tv_leader_board_love_affair:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.LOVE_AFFAIR_TOP_SCORE, R.string.leaderboard_love_affair__top_score);
                break;
            }
            case R.id.btn_leader_board_thunder_bolt:
            case R.id.tv_leader_board_thunder_bolt:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.THUNDER_BOLT_TOP_SCORE, R.string.leaderboard_thunderbolt__top_scores);
                break;
            }
            case R.id.btn_leader_board_jet:
            case R.id.tv_leader_board_jet:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.JET_TOP_SCORE, R.string.leaderboard_jet__top_scores);
                break;
            }
            case R.id.btn_leader_board_hit_or_miss:
            case R.id.tv_leader_board_hit_or_miss:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.HIT_OR_MISS_TOP_SCORE, R.string.leaderboard_hit_or_miss__top_scores);
                break;
            }
            case R.id.btn_leader_board_stars_1:
            case R.id.tv_leader_board_stars_1:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.STARS_1_TOP_SCORE, R.string.leaderboard_stars_1_0__top_scores);
                break;
            }
            case R.id.btn_leader_board_shelter:
            case R.id.tv_leader_board_shelter:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.SHELTER_TOP_SCORE, R.string.leaderboard_shelter__top_scores);
                break;
            }
            case R.id.btn_leader_board_stars_2:
            case R.id.tv_leader_board_stars_2:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.STARS_2_TOP_SCORE, R.string.leaderboard_stars_2_0__top_scores);
                break;
            }
            case R.id.tv_leader_board_overall_progress:
            case R.id.btn_leader_board_overall_progress:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.STORY_MODE_COMPLETION_STATUS, R.string.leaderboard_story_mode__completion_status);
                break;
            }

            //Within leader-board dialog
            case R.id.tv_leader_board_richie_rich:
            case R.id.btn_leader_board_richie_rich:
            {
                mContext.onShowLeaderboardsRequested(RICHIE_RICH, R.string.leaderboard_richie_rich__overall);
                break;
            }
            case R.id.btn_leader_board_sprinter:
            case R.id.tv_leader_board_sprinter:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.MAX_COINS_ONE_GAME, R.string.leaderboard_max_coins_earned_in_one_game);
                break;
            }
            case R.id.btn_leader_board_marathon_runner:
            case R.id.tv_leader_board_marathon_runner:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.STORY_MODE_COMPLETION_STATUS, R.string.leaderboard_story_mode__completion_status);
                break;
            }
            case R.id.btn_leader_board_cmp_story_mode:
            case R.id.tv_leader_board_cmp_story_mode:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.STORY_MODE_OVERALL_TOP_SCORE, R.string.leaderboard_story_mode__overall_top_score);
                break;
            }
            case R.id.btn_leader_board_cmp_love_affair:
            case R.id.tv_leader_board_cmp_love_affair:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.LOVE_AFFAIR_TOP_SCORE, R.string.leaderboard_love_affair__top_score);
                break;
            }
            case R.id.btn_leader_board_cmp_thunder_bolt:
            case R.id.tv_leader_board_cmp_thunder_bolt:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.THUNDER_BOLT_TOP_SCORE, R.string.leaderboard_thunderbolt__top_scores);
                break;
            }
            case R.id.btn_leader_board_cmp_jet:
            case R.id.tv_leader_board_cmp_jet:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.JET_TOP_SCORE, R.string.leaderboard_jet__top_scores);
                break;
            }
            case R.id.btn_leader_board_cmp_hit_or_miss:
            case R.id.tv_leader_board_cmp_hit_or_miss:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.HIT_OR_MISS_TOP_SCORE, R.string.leaderboard_hit_or_miss__top_scores);
                break;
            }
            case R.id.btn_leader_board_cmp_stars_1:
            case R.id.tv_leader_board_cmp_stars_1:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.STARS_1_TOP_SCORE, R.string.leaderboard_stars_1_0__top_scores);
                break;
            }
            case R.id.btn_leader_board_cmp_shelter:
            case R.id.tv_leader_board_cmp_shelter:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.SHELTER_TOP_SCORE, R.string.leaderboard_shelter__top_scores);
                break;
            }
            case R.id.btn_leader_board_cmp_stars_2:
            case R.id.tv_leader_board_cmp_stars_2:
            {
                mContext.onShowLeaderboardsRequested(HelperClass.STARS_2_TOP_SCORE, R.string.leaderboard_stars_2_0__top_scores);
                break;
            }
        }
    }

    private void showLeaderBoardDialog()
    {
        final Dialog dialog = new AlertDialog.Builder(mContext).show();
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        View v2 =  mContext.CurrentView;
        lp.width = v2.getMeasuredWidth() - ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = v2.getMeasuredHeight() - ConvertToPx(mContext, 60);
        window.setAttributes(lp);
        LayoutInflater inflater = mContext.getLayoutInflater();
        dialog.setContentView(inflater.inflate(R.layout.dialog_leader_board_story_mode, null, false));

        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_leader_board_richie_rich).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_richie_rich).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_sprinter).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_sprinter).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_marathon_runner).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_marathon_runner).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_story_mode).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_story_mode).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_love_affair).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_love_affair).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_thunder_bolt).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_thunder_bolt).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_jet).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_jet).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_hit_or_miss).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_hit_or_miss).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_stars_1).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_stars_1).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_shelter).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_shelter).setOnClickListener(this);
        dialog.findViewById(R.id.btn_leader_board_cmp_stars_2).setOnClickListener(this);
        dialog.findViewById(R.id.tv_leader_board_cmp_stars_2).setOnClickListener(this);

    }
}
