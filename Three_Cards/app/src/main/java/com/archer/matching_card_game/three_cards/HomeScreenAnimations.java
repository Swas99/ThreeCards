package com.archer.matching_card_game.three_cards;


import android.graphics.Point;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import java.lang.ref.WeakReference;

public class HomeScreenAnimations {

    public void StartAnimation(WeakReference<MainActivity> m_context)
    {
        MainActivity mContext = m_context.get();

        AnimateGameModes(mContext);
        AnimateLogo(mContext);
        AnimateOtherButtons(mContext);

    }

    private void AnimateGameModes(MainActivity mContext)
    {
        View leftV1 = mContext.findViewById(R.id.btnQuickGame);
        View leftV2 = mContext.findViewById(R.id.btnArcade);
        View rightV1 = mContext.findViewById(R.id.btnStoryMode);
        View rightV2 = mContext.findViewById(R.id.btnTimeTrial);


        View [] allViews = { leftV1,leftV2, rightV1, rightV2};
        ConfigureOutOfParentAnimation(allViews);

        Point screenSize = HelperClass.getWindowSize(mContext.getWindowManager().getDefaultDisplay());
        int screenWidth = screenSize.x;

        AnimationSet slideRight = Slide((int)(screenWidth/3.3),0);
        AnimationSet slideLeft = Slide((int)(-screenWidth/3.3),0);

        leftV1.startAnimation(slideRight);
        leftV2.startAnimation(slideRight);
        rightV1.startAnimation(slideLeft);
        rightV2.startAnimation(slideLeft);
    }

    private void AnimateLogo(MainActivity mContext)
    {
        View twoCards = mContext.findViewById(R.id.btnGameLogo);
        View [] allViews = { twoCards};
        ConfigureOutOfParentAnimation(allViews);

        AnimationSet ZoomIn = ZoomIn();
        twoCards.startAnimation(ZoomIn);
    }

    private void AnimateOtherButtons(MainActivity mContext)
    {
        View v1 = mContext.findViewById(R.id.btnHelp);
        View v2 = mContext.findViewById(R.id.btnStore);
        View v3 = mContext.findViewById(R.id.btnTopScores);
        View v4 = mContext.findViewById(R.id.btnSettings);
        View v5 = mContext.findViewById(R.id.btnRating);
        View v6 = mContext.findViewById(R.id.btnFb);
        View v7 = mContext.findViewById(R.id.btnShare);


        View [] allViews = { v1,v2,v3,v4,v5,v6,v7};
        ConfigureOutOfParentAnimation(allViews);
        AnimationSet zoomInOut = ZoomInOut();

        for (View v : allViews)
        {
            v.startAnimation(zoomInOut);
        }
    }

    private void ConfigureOutOfParentAnimation(View[] v_arr)
    {
        for (View v : v_arr)
        {
            HelperClass.ConfigureOutOfParentAnimation(v,true);
        }
    }

    public AnimationSet Slide(int deltaX,int deltaY)
    {

        TranslateAnimation shake_1 = new TranslateAnimation(-deltaX, 0, 0, deltaY);
        shake_1.setDuration(800);
        shake_1.setStartOffset(400);
        shake_1.setFillAfter(true);

        AnimationSet ShakeIt = new AnimationSet(true);
        ShakeIt.addAnimation(shake_1);
        ShakeIt.setInterpolator(new OvershootInterpolator());

        return ShakeIt;
    }

    public AnimationSet ZoomIn()
    {
        ScaleAnimation zoom = new ScaleAnimation(.97f,1.1f,.97f,1.1f,
                Animation.RELATIVE_TO_SELF,.5f,
                Animation.RELATIVE_TO_SELF, .5f);
        zoom.setDuration(3600);
        zoom.setStartOffset(400);
        zoom.setFillAfter(true);
        zoom.setRepeatMode(Animation.REVERSE);
        zoom.setRepeatCount(Animation.INFINITE);

        AnimationSet ZoomIn = new AnimationSet(true);
//        ZoomIn.addAnimation(fade_in);
        ZoomIn.addAnimation(zoom);

        return ZoomIn;
    }

    public AnimationSet ZoomInOut()
    {
        ScaleAnimation zoom = new ScaleAnimation(.9f,1f,.9f,1f,
                Animation.RELATIVE_TO_SELF,.5f,
                Animation.RELATIVE_TO_SELF, .5f);
        zoom.setDuration(3000);
        zoom.setStartOffset(400);
        zoom.setFillAfter(true);
        zoom.setRepeatMode(Animation.REVERSE);
        zoom.setRepeatCount(Animation.INFINITE);

        AnimationSet ZoomIn = new AnimationSet(true);
        ZoomIn.addAnimation(zoom);

        return ZoomIn;
    }

}
