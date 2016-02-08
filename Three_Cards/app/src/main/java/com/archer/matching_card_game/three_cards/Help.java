package com.archer.matching_card_game.three_cards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;


public class Help implements View.OnClickListener {

    int Help_Index = 0;
    final int OBJECTIVE = 0;
    final int GAME_MODE = 1;
    final int POWER = 2;
    final int PLAYER_MODE = 3;
    final int BOARD_DETAILS = 4;

    MainActivity mContext;

    public Help(WeakReference<MainActivity> m_context)
    {
        mContext = m_context.get();
    }


    public void Show()
    {
        View help_screen = mContext.loadView(R.layout.screen_help);
        addFlingListenerToHelpScreen(help_screen);
        ((TextView)mContext.findViewById(R.id.tvHeader_1)).setTypeface(Typeface.SANS_SERIF);

        //region Check if Ad-Free version
        if(!mContext.adFreeVersion) {
            final AdView mAdView = (AdView) help_screen.findViewById(R.id.adView);
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
        ObjectiveClick();
        addListenerToControls();
    }

    private void addListenerToControls()
    {
        View btnObjective = mContext.findViewById(R.id.btnObjective);
        View btnPlayerMode = mContext.findViewById(R.id.btnPlayerMode);
        View btnBoardDetails = mContext.findViewById(R.id.btnBoardDetails);
        View btnPower = mContext.findViewById(R.id.btnPower);
        View btnGameMode = mContext.findViewById(R.id.btnGameMode);
        View btnBack = mContext.findViewById(R.id.btnBack);
        View btn_back = mContext.findViewById(R.id.btn_back);

        btnObjective.setOnClickListener(this);
        btnPlayerMode.setOnClickListener(this);
        btnBoardDetails.setOnClickListener(this);
        btnPower.setOnClickListener(this);
        btnGameMode.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void ObjectiveClick()
    {
        Help_Index=OBJECTIVE;
        deSelectedAllButtons();
        View btnObjective = mContext.findViewById(R.id.btnObjective);
        btnObjective.setBackgroundResource(R.drawable.btn_white_reverse);

        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_help_objective, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));

        ((TextView)mContext.findViewById(R.id.tvHeader_1)).setText("Objective");
    }

    private void PlayerModeClick()
    {
        Help_Index=PLAYER_MODE;
        deSelectedAllButtons();
        View btnPlayerMode = mContext.findViewById(R.id.btnPlayerMode);
        btnPlayerMode.setBackgroundResource(R.drawable.btn_white_reverse);

        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_help_player_mode, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));

        ((TextView)mContext.findViewById(R.id.tvHeader_1)).setText("Player Mode");
    }
    private void BoardDetailsClick()
    {
        Help_Index=BOARD_DETAILS;
        deSelectedAllButtons();
        View btnBoardDetails = mContext.findViewById(R.id.btnBoardDetails);
        btnBoardDetails.setBackgroundResource(R.drawable.btn_white_reverse);

        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_help_board_details, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));

        ((TextView)mContext.findViewById(R.id.tvHeader_1)).setText("Board Details");

        view.findViewById(R.id.btnOneBoard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new AlertDialog.Builder(mContext).show();
                LayoutInflater inflater = mContext.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_help_1board, null, true);
                view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view);
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                View v2 =  mContext.CurrentView;
                lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        view.findViewById(R.id.btnThreeBoard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new AlertDialog.Builder(mContext).show();
                LayoutInflater inflater = mContext.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_help_3board, null, true);
                view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view);
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                View v2 =  mContext.CurrentView;
                lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        view.findViewById(R.id.btnHorizontalScroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new AlertDialog.Builder(mContext).show();
                LayoutInflater inflater = mContext.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_help_horizontal_scroll, null, true);
                view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view);
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                View v2 =  mContext.CurrentView;
                lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        view.findViewById(R.id.btnVerticalScroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new AlertDialog.Builder(mContext).show();
                LayoutInflater inflater = mContext.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_help_vertical_scroll, null, true);
                view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view);
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                View v2 =  mContext.CurrentView;
                lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        view.findViewById(R.id.btnBothScroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new AlertDialog.Builder(mContext).show();
                LayoutInflater inflater = mContext.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_help_both_scroll, null, true);
                view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view);
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                View v2 =  mContext.CurrentView;
                lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        view.findViewById(R.id.btnNoScroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new AlertDialog.Builder(mContext).show();
                LayoutInflater inflater = mContext.getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_help_no_scroll, null, true);
                view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(view);
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
                View v2 =  mContext.CurrentView;
                lp.width = v2.getMeasuredWidth() - HelperClass.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });


    }
    private void GameModeClick()
    {
        Help_Index=GAME_MODE;
        deSelectedAllButtons();
        View btnGameMode = mContext.findViewById(R.id.btnGameMode);
        btnGameMode.setBackgroundResource(R.drawable.btn_white_reverse);

        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_help_game_mode, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));

        ((TextView)mContext.findViewById(R.id.tvHeader_1)).setText("Game Mode");
    }
    private void PowerClick()
    {
        Help_Index=POWER;
        deSelectedAllButtons();
        View btnPow = mContext.findViewById(R.id.btnPower);
        btnPow.setBackgroundResource(R.drawable.btn_white_reverse);

        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_help_powers, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));

        ((TextView)mContext.findViewById(R.id.tvHeader_1)).setText("Power");
    }

    private void deSelectedAllButtons()
    {
        View btnObjective = mContext.findViewById(R.id.btnObjective);
        View btnPlayerMode = mContext.findViewById(R.id.btnPlayerMode);
        View btnBoardDetails = mContext.findViewById(R.id.btnBoardDetails);
        View btnPower = mContext.findViewById(R.id.btnPower);
        View btnGameMode = mContext.findViewById(R.id.btnGameMode);

        View allButtons[] = { btnObjective,btnPlayerMode,btnBoardDetails,btnPower,btnGameMode};

        for (View allButton : allButtons) {
            allButton.setBackgroundResource(R.drawable.btn_white_transparency_20);
        }
    }


    private void addFlingListenerToHelpScreen(View v)
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

    private void loadHelp()
    {
        if(Help_Index>BOARD_DETAILS)
            Help_Index = OBJECTIVE;
        if(Help_Index<OBJECTIVE)
            Help_Index = BOARD_DETAILS;

        switch (Help_Index)
        {
            case OBJECTIVE:
                ObjectiveClick();
                break;
            case GAME_MODE:
                GameModeClick();
                break;
            case POWER:
                PowerClick();
                break;
            case PLAYER_MODE:
                PlayerModeClick();
                break;
            case BOARD_DETAILS:
                BoardDetailsClick();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnObjective:
                if(Help_Index==OBJECTIVE)
                    return;
                ObjectiveClick();
                break;
            case R.id.btnPlayerMode:
                if(Help_Index==PLAYER_MODE)
                    return;
                PlayerModeClick();
                break;
            case R.id.btnBoardDetails:
                if(Help_Index==BOARD_DETAILS)
                    return;
                BoardDetailsClick();
                break;
            case R.id.btnPower:
                if(Help_Index==POWER)
                    return;
                PowerClick();
                break;
            case R.id.btnGameMode:
                if(Help_Index==GAME_MODE)
                    return;
                GameModeClick();
                break;
            case R.id.btnBack:
            case R.id.btn_back:
                mContext.onBackPress();
                break;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final int SWIPE_MIN_DISTANCE = 40;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                Help_Index++;
                loadHelp();
                return false; // Right to left
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE ) //&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                Help_Index--;
                loadHelp();
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
