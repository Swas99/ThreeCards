package com.archer.matching_card_game.three_cards;


import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ConfigureOutOfParentAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.CreateTransitionDrawable;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER_2;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_REPLACE;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_SHUFFLE;
import static com.archer.matching_card_game.three_cards.HelperClass.RotateAndFadeInAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.RotateAndFadeOutAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.SetEnableControls;
import static com.archer.matching_card_game.three_cards.HelperClass.SwapAnimation;

public class TimeTrail {

    public CountDownTimer TimeTrialTimer;
    Game CurrentGame;
    int SecondsLeft_TimeTrial;
    int TimeTrial_TimerValue;
    boolean isAnimating;

    public TimeTrail(final WeakReference<Game> mGame,int timeTrial_TimerValue)
    {
        CurrentGame = mGame.get();
        TimeTrial_TimerValue = timeTrial_TimerValue;
        SecondsLeft_TimeTrial = timeTrial_TimerValue / 1000;
        InitializeCountDownTimer();
        isAnimating=false;
    }


    private void InitializeCountDownTimer() {
        TimeTrialTimer = new CountDownTimer(TimeTrial_TimerValue,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerOnTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Runnable myRunnable = new Runnable(){

                    public void run(){
                    //Wait till cards are animating
                    while (CurrentGame.isAnimating)
                    {
                        ;//WAIT !!
                    }

                    CurrentGame.mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TimerOnFinish();
                        }
                        });
                    }
                };
                Thread thread = new Thread(myRunnable);
                thread.start();
            }
        };
    }


    private void TimerOnTick(long millisUntilFinished)
    {
        //Sync threads !!
        CurrentGame.AcquireLOCK();

        SecondsLeft_TimeTrial = (int)millisUntilFinished/1000;
        if(CurrentGame.PlayerMode != ONE_PLAYER || CurrentGame.ActualClickCount==0)
            CurrentGame.SetGameInfoText();

        //Sync threads !!
        CurrentGame.ReleaseLOCK();
    }

    private void TimerOnFinish()
    {
        //Sync threads !!
        CurrentGame.AcquireLOCK();

        isAnimating = true;
        SecondsLeft_TimeTrial = 0;
        CurrentGame.SetGameInfoText();

        CurrentGame.Btn_Power.setEnabled(false);
        SetEnableControls(false, CurrentGame.GameBoard);


        ImageView []availableCards = new ImageView[CurrentGame.RowSize*CurrentGame.ColumnSize];
        int length = GetAvailableCards(availableCards);
        if(length == 1)
        {
            TransitionDrawable cross_fade = CreateTransitionDrawable(R.drawable.lock,R.drawable.card_destroyed,CurrentGame.mContext);
            ImageView card = availableCards[0];
            card.getParent().requestChildFocus(card,card);
            card.setOnClickListener(null);
            card.setImageDrawable(cross_fade);
            cross_fade.startTransition(800);
            new CountDownTimer(800,800){
                @Override
                public void onTick(long milliSecondsToFinish){}

                @Override
                public void onFinish()
                {
                    //Sync threads !!
                    CurrentGame.AcquireLOCK();

                    CurrentGame.GameTimer.cancel();
                    CurrentGame.postGameLogic();
                    SetEnableControls(true, CurrentGame.GameBoard);
                    CurrentGame.Btn_Power.setEnabled(true);

                    //Sync threads !!
                    CurrentGame.ReleaseLOCK();
                    isAnimating=false;
                }
            }.start();

        }
        else if(length == 2)
        {
            ShuffleCards(availableCards, length);
        }
        else if(length>2)
        {
            if(CurrentGame.EffectiveClickCount%2 == 0 && Math.random() <= .75) //Replace card 75% time for first card
            {
                ImageView[] selected_cards = SelectCardsRandomly(availableCards,length,POW_REPLACE);
                ReplaceCards(selected_cards,selected_cards.length);
            }
            else
            {
                ImageView[] selected_cards = SelectCardsRandomly(availableCards,length,POW_SHUFFLE);
                ShuffleCards(selected_cards,selected_cards.length);
            }
        }

        //Sync threads !!
        CurrentGame.ReleaseLOCK();

    }

    private void ReplaceImageRes(int oldResId,int newResId)
    {
        for(int i = 0;i<CurrentGame.RowSize;i++)
        {
            for(int j=0;j<CurrentGame.ColumnSize;j++)
            {
                if(CurrentGame.Cards_ImageResID[i][j] == oldResId)
                {
                    CurrentGame.Cards_ImageResID[i][j] = newResId;
                    if(CurrentGame.MatchingCard_Map.indexOfKey(newResId)<0)
                    {
                        String item = String.valueOf(i)+DELIMITER+String.valueOf(j);
                        CurrentGame.MatchingCard_Map.put(newResId,item);
                    }
                    else
                    {
                        String item = CurrentGame.MatchingCard_Map.get(newResId);
                        item += DELIMITER_2+String.valueOf(i)+DELIMITER+String.valueOf(j);
                        CurrentGame.MatchingCard_Map.delete(newResId);
                        CurrentGame.MatchingCard_Map.put(newResId,item);
                    }
                }
            }
        }

    }
    private int GetAvailableCards(ImageView []availableCards)
    {
        int length = 0;
        for(int i=0;i<CurrentGame.RowSize;i++)
        {
            for(int j=0;j<CurrentGame.ColumnSize;j++)
            {
                if( CurrentGame.IV_AllCards[i][j]!=null) {
                    availableCards[length++] = CurrentGame.IV_AllCards[i][j];
                }
            }
        }
        return length;
    }

    private ImageView[] SelectCardsRandomly(ImageView []availableCards,int length,int typeIdentifier)
    {
        int numberOfCardsToReplaceOrShuffle = 1;
        if(Math.random() > .8)
            numberOfCardsToReplaceOrShuffle++;
        if(Math.random() > .9)
            numberOfCardsToReplaceOrShuffle++;
        if(Math.random() > .95)
            numberOfCardsToReplaceOrShuffle++;

        if(typeIdentifier == POW_SHUFFLE)
            numberOfCardsToReplaceOrShuffle++;

        if(numberOfCardsToReplaceOrShuffle>length)
            numberOfCardsToReplaceOrShuffle=length;

        ImageView[] selected_cards = new ImageView[numberOfCardsToReplaceOrShuffle];

        for(int i=0;i<numberOfCardsToReplaceOrShuffle;i++)
        {
            int index = ((int)(Math.random()*100))%length;
            selected_cards[i] = availableCards[index];
            while (index<length-1)
                availableCards[index] = availableCards[++index];
            length--;
        }
        return selected_cards;
    }

    private void ShuffleCards(final ImageView[] cardsToShuffle, final int length)
    {
        {
            int x[] = new int[length];
            int y[] = new int[length];
            AnimationSet swap_anim[] = new AnimationSet[length];

            for(int i=0;i<length;i++)
            {
                View v = cardsToShuffle[i];
                int [] position = new int[2];
                v.getLocationOnScreen(position);
                x[i] = position[0];
                y[i] = position[1];
                ConfigureOutOfParentAnimation(v,true);
            }
            for(int i = 0;i<length-1;i++)
            {
                int deltaX = x[i+1] - x[i];
                int deltaY = y[i+1] - y[i];
                swap_anim[i] = SwapAnimation(deltaX,deltaY);
            }
            swap_anim[length-1] =  SwapAnimation(x[0]-x[length-1],y[0]-y[length-1]);

            swap_anim[length-1].setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //Sync threads !!
                    CurrentGame.AcquireLOCK();

                    int []image_resource = new int[length];
                    int lengthOfImageResource = length;
                    int r,c;
                    for(int i=0;i<length;i++)
                    {
                        r = Integer.parseInt(cardsToShuffle[i].getTag().toString().split(DELIMITER)[0]);
                        c = Integer.parseInt(cardsToShuffle[i].getTag().toString().split(DELIMITER)[1]);
                        image_resource[i] = CurrentGame.Cards_ImageResID[r][c];
                    }
                    for (int i = 0 ;i<length;i++)
                    {
                        int random_index = ((int)(Math.random()*1000))%lengthOfImageResource;
                        r = Integer.parseInt(cardsToShuffle[i].getTag().toString().split(DELIMITER)[0]);
                        c = Integer.parseInt(cardsToShuffle[i].getTag().toString().split(DELIMITER)[1]);
                        CurrentGame.Cards_ImageResID[r][c] = image_resource[random_index];
                        lengthOfImageResource--;
                        while (random_index<lengthOfImageResource)
                            image_resource[random_index]=image_resource[++random_index];
                    }

                    //Sync threads !!
                    CurrentGame.ReleaseLOCK();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //Sync threads !!
                    CurrentGame.AcquireLOCK();

                    SetEnableControls(true, CurrentGame.GameBoard);
                    CurrentGame.Btn_Power.setEnabled(true);

                    if(!CurrentGame.powFind_flag)//To sy
                        TimeTrialTimer.start();

                    //Sync threads !!
                    CurrentGame.ReleaseLOCK();
                    isAnimating=false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            for(int i = 0;i<length;i++)
            {
                ImageView card = cardsToShuffle[i];
                card.getParent().requestChildFocus(card,card);
                card.startAnimation(swap_anim[i]);
            }
        }
    }

    private void ReplaceCards(final ImageView[] cardsToReplace,final int length)
    {
        AnimationSet fade_out =  RotateAndFadeOutAnimation();
        final AnimationSet fade_in = RotateAndFadeInAnimation();

        fade_out.setAnimationListener(new Animation.AnimationListener() {

            boolean flag;
            @Override
            public void onAnimationStart(Animation animation) {
                //Sync threads !!
                CurrentGame.AcquireLOCK();

                flag = true;
                int r,c;
                int imgResToReplace;
                int indexOfReplacement=0;
                for(int i=0;i<length;i++)
                {
                    r = Integer.parseInt(cardsToReplace[i].getTag().toString().split(DELIMITER)[0]);
                    c = Integer.parseInt(cardsToReplace[i].getTag().toString().split(DELIMITER)[1]);
                    imgResToReplace = CurrentGame.Cards_ImageResID[r][c];

                    int new_imageRes = CurrentGame.ReplacementCards[indexOfReplacement];
                    CurrentGame.ReplacementCards[indexOfReplacement++] = imgResToReplace;
                    ReplaceImageRes(imgResToReplace,new_imageRes);
                }

                //Sync threads !!
                CurrentGame.ReleaseLOCK();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Sync threads !!
                CurrentGame.AcquireLOCK();

                if(flag)
                {
                    for(int i = 0;i<length;i++)
                        cardsToReplace[i].startAnimation(fade_in);
                }
                flag = false;

                //Sync threads !!
                CurrentGame.ReleaseLOCK();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Sync threads !!
                CurrentGame.AcquireLOCK();

                for(int i = 0;i<length;i++)
                {
                    cardsToReplace[i].setImageResource(R.drawable.lock);
                }
                SetEnableControls(true, CurrentGame.GameBoard);
                CurrentGame.Btn_Power.setEnabled(true);

                if(!CurrentGame.powFind_flag)
                    TimeTrialTimer.start();

                //Sync threads !!
                CurrentGame.ReleaseLOCK();
                isAnimating=false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        for(int i = 0;i<length;i++)
        {
            ImageView card = cardsToReplace[i];
            card.getParent().requestChildFocus(card,card);
            card.startAnimation(fade_out);
        }
    }


}
