package com.archer.matching_card_game.three_cards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ConfigureOutOfParentAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.ConvertToPx;
import static com.archer.matching_card_game.three_cards.HelperClass.CreateTransitionDrawable;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER_2;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.POWER_COUNT;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_DESTROY;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_EXTRA_MOVES;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_FIND;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_PEEK;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_REPLACE;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_SHUFFLE;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_SWAP;
import static com.archer.matching_card_game.three_cards.HelperClass.ROBOT_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.RotateAndFadeInAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.RotateAndFadeOutAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.SetEnableControls;
import static com.archer.matching_card_game.three_cards.HelperClass.ShuffleAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.SwapAnimation;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL;

public class Power {

    Game CurrentGame;
    int CurrentCardCount;
    int MaxCardCount;
    ImageView CurrentCard;
    ImageView SelectedCards[];
    private View.OnClickListener lsSwapLogic;
    private View.OnClickListener lsShuffleLogic;
    private View.OnClickListener lsReplaceLogic;
    private View.OnClickListener lsDestroyLogic;
    private View.OnClickListener lsPeekLogic;
    private View.OnClickListener lsExtraMovesLogic;

    public Power(final WeakReference<Game> parentGame)
    {
        CurrentGame = parentGame.get();
    }

    private void AssignClickListenerToAllValidCards(View.OnClickListener listener)
    {
        for(int i=0;i<CurrentGame.RowSize;i++)
        {
            for(int j=0;j<CurrentGame.ColumnSize;j++)
            {
                if(CurrentGame.IV_AllCards[i][j] != null)
                    CurrentGame.IV_AllCards[i][j].setOnClickListener(listener);
            }
        }
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

    private int getClickAdjustment_destroyedCards()
    {
       // Map<Integer, Integer> image_count = new HashMap<Integer, Integer>();
        SparseArray<Integer> image_count = new SparseArray<>();
        for(int i=0;i<CurrentGame.DestroyedCards_Top;i++)
        {
            int r = Integer.parseInt(CurrentGame.DestroyedCards[i].split(DELIMITER)[0]);
            int c = Integer.parseInt(CurrentGame.DestroyedCards[i].split(DELIMITER)[1]);
            if(image_count.indexOfKey(CurrentGame.Cards_ImageResID[r][c])>=0)
            {
                int cnt = image_count.get(CurrentGame.Cards_ImageResID[r][c]);
                cnt++;
                image_count.put(CurrentGame.Cards_ImageResID[r][c],cnt);
            }
            else
            {
                image_count.put(CurrentGame.Cards_ImageResID[r][c],1);
            }
        }

        int adjustment =0;
        for(int i = 0; i < image_count.size(); i++) {
            int key = image_count.keyAt(i);
            int count = image_count.get(key);
            adjustment+=count;
            if(count%2 != 0)
                adjustment++;
        }
        return adjustment;
    }

    private void assignClickListenerToCard(int currentCardCount,int maxCardCount,View.OnClickListener onClick)
    {
        CurrentGame.Btn_Power.setEnabled(false);
        CurrentCardCount = currentCardCount;
        MaxCardCount = maxCardCount;
        SelectedCards = new ImageView[MaxCardCount];
        AssignClickListenerToAllValidCards(onClick);
    }

    public void InitializePowerListeners()
    {

        //region SWAP Logic

          lsSwapLogic = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( CurrentGame.ActualClickCount==0 )
                {
                    CurrentGame.GameTimer.cancel();
                    CurrentGame.GameTimer.start();
                }
                if(CurrentCardCount<MaxCardCount) {
                    ImageView card = (ImageView)view;
                    SelectedCards[CurrentCardCount++] = card;
                    card.setImageResource(R.drawable.selected_card_pow);
                    card.setOnClickListener(null);
                }
                if (CurrentCardCount == MaxCardCount)
                {
                    SetEnableControls(false,CurrentGame.GameBoard);

                    int x[] = new int[MaxCardCount];
                    int y[] = new int[MaxCardCount];
                    AnimationSet swap_anim[] = new AnimationSet[MaxCardCount];

                    for(int i=0;i<MaxCardCount;i++)
                    {
                        View v = SelectedCards[i];
                        int [] position = new int[2];
                        v.getLocationOnScreen(position);
                        x[i] = position[0];
                        y[i] = position[1];
                        ConfigureOutOfParentAnimation(v,true);
                    }
                    for(int i = 0;i<MaxCardCount-1;i++)
                    {
                        int deltaX = x[i+1] - x[i];
                        int deltaY = y[i+1] - y[i];
                        swap_anim[i] = SwapAnimation(deltaX,deltaY);
                    }
                    swap_anim[MaxCardCount-1] =  SwapAnimation(x[0]-x[MaxCardCount-1],y[0]-y[MaxCardCount-1]);

                    swap_anim[MaxCardCount-1].setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //Sync threads !!
                            CurrentGame.AcquireLOCK();
                            CurrentGame.isAnimating = true;
                            int []image_resource = new int[MaxCardCount];
                            int lengthOfImageResource = MaxCardCount;
                            int r,c;
                            for(int i=0;i<MaxCardCount;i++)
                            {
                                r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                                c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
                                image_resource[i] = CurrentGame.Cards_ImageResID[r][c];
                                if(CurrentGame.PlayerMode==ROBOT_PLAYER)
                                    CurrentGame.robotPlayer.resetCardClick(r,c);
                            }
                            for (int i = 0 ;i<MaxCardCount;i++)
                            {
                                int random_index = ((int)(Math.random()*1000))%lengthOfImageResource;
                                r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                                c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
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

                            for(int i=0;i<MaxCardCount;i++)
                            {
                                SelectedCards[i].setImageResource(R.drawable.lock);
                            }
                            AssignClickListenerToAllValidCards(CurrentGame.CardClick_Listener);
                            SetEnableControls(true, CurrentGame.GameBoard);
                            CurrentGame.Btn_Power.setEnabled(true);

                            CurrentGame.isAnimating = false;
                            //Sync threads !!
                            CurrentGame.ReleaseLOCK();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    for(int i = 0;i<MaxCardCount;i++)
                    {
                        SelectedCards[i].startAnimation(swap_anim[i]);
                    }
                }
            }
        };

        //endregion

        //region SHUFFLE Logic
        lsShuffleLogic = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( CurrentGame.ActualClickCount==0 )
                {
                    CurrentGame.GameTimer.cancel();
                    CurrentGame.GameTimer.start();
                }
                SetEnableControls(false,CurrentGame.GameBoard);
                int x[] = new int[CurrentGame.RowSize*CurrentGame.ColumnSize];
                int y[] = new int[CurrentGame.RowSize*CurrentGame.ColumnSize];
                CurrentCardCount = 0;
                AnimationSet shuffle_anim[] = new AnimationSet[CurrentGame.RowSize*CurrentGame.ColumnSize];

                for(int i=0;i<CurrentGame.RowSize;i++)
                {
                    for(int j = 0;j<CurrentGame.ColumnSize;j++) {
                        if(CurrentGame.IV_AllCards[i][j] != null) {
                            View v = CurrentGame.IV_AllCards[i][j];
                            SelectedCards[CurrentCardCount] = (ImageView)v;
                            int[] position = new int[2];
                            v.getLocationOnScreen(position);
                            x[CurrentCardCount] = position[0];
                            y[CurrentCardCount++] = position[1];
                            ConfigureOutOfParentAnimation(v,true);
                        }
                    }
                }

                MaxCardCount = CurrentCardCount;

                int[] position = new int[2];
                view.getLocationOnScreen(position);
                for(int i=0;i<MaxCardCount-1;i++) {
                    int deltaX;// = x[MaxCardCount-i-1] - x[i];
                    int deltaY;// = y[MaxCardCount-i-1] - y[i];
                    deltaX = position[0]-x[i];
                    deltaY = position[1]-y[i];
                    shuffle_anim[i] = ShuffleAnimation(deltaX,deltaY);
                }
                //  shuffle_anim[MaxCardCount-1] =  ShuffleAnimation(x[0] - x[MaxCardCount - 1], y[0] - y[MaxCardCount - 1]);
                shuffle_anim[MaxCardCount-1] =  ShuffleAnimation(position[0] - x[MaxCardCount - 1], position[1] - y[MaxCardCount - 1]);

                shuffle_anim[MaxCardCount-1].setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //Sync threads !!
                        CurrentGame.AcquireLOCK();
                        CurrentGame.isAnimating = true;

                        int []image_resource = new int[MaxCardCount];
                        int lengthOfImageResource = MaxCardCount;
                        int r,c;
                        for(int i=0;i<MaxCardCount;i++)
                        {
                            r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                            c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
                            image_resource[i] = CurrentGame.Cards_ImageResID[r][c];
                        }
                        for (int i = 0 ;i<MaxCardCount;i++)
                        {
                            int random_index = ((int)(Math.random()*1013))%lengthOfImageResource;
                            r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                            c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
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

                        for(int i=0;i<MaxCardCount;i++)
                        {
                            SelectedCards[i].setImageResource(R.drawable.lock);
                        }
                        AssignClickListenerToAllValidCards(CurrentGame.CardClick_Listener);
                        SetEnableControls(true, CurrentGame.GameBoard);
                        CurrentGame.Btn_Power.setEnabled(true);

                        CurrentGame.isAnimating = false;
                        //Sync threads !!
                        CurrentGame.ReleaseLOCK();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                for(int i = 0;i<MaxCardCount;i++)
                {
                    SelectedCards[i].startAnimation(shuffle_anim[i]);
                }

                if(CurrentGame.PlayerMode == ROBOT_PLAYER)
                {
                    CurrentGame.robotPlayer.Clear(false);
                }
            }
        };

        //endregion SHUFFLE

        //region REPLACE Logic

        lsReplaceLogic = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( CurrentGame.ActualClickCount==0 )
                {
                    CurrentGame.GameTimer.cancel();
                    CurrentGame.GameTimer.start();
                }
                if(CurrentCardCount<MaxCardCount) {
                    ImageView card = (ImageView)view;
                    SelectedCards[CurrentCardCount++] = card;
                    card.setImageResource(R.drawable.selected_card_pow);
                    card.setOnClickListener(null);
                }
                if (CurrentCardCount == MaxCardCount)
                {
                    SetEnableControls(false,CurrentGame.GameBoard);

                    AnimationSet fade_out =  RotateAndFadeOutAnimation();
                    final AnimationSet fade_in = RotateAndFadeInAnimation();


                    fade_out.setAnimationListener(new Animation.AnimationListener() {

                        boolean flag;

                        @Override
                        public void onAnimationStart(Animation animation) {
                            //Sync threads !!
                            CurrentGame.AcquireLOCK();
                            CurrentGame.isAnimating = true;
                            flag = true;
                            int r, c;
                            int imgResToReplace;
                            int indexOfReplacement=0;
                            for (int i = 0; i < MaxCardCount; i++) {
                                r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                                c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
                                imgResToReplace = CurrentGame.Cards_ImageResID[r][c];

                                if(CurrentGame.PlayerMode==ROBOT_PLAYER)
                                    CurrentGame.robotPlayer.resetCardClick(r,c);
                                int new_imageRes = CurrentGame.ReplacementCards[indexOfReplacement];
                                CurrentGame.ReplacementCards[indexOfReplacement++] = imgResToReplace;
                                ReplaceImageRes(imgResToReplace, new_imageRes);
                            }

                            //Sync threads !!
                            CurrentGame.ReleaseLOCK();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Sync threads !!
                            CurrentGame.AcquireLOCK();

                            if (flag) {
                                for (int i = 0; i < MaxCardCount; i++)
                                    SelectedCards[i].startAnimation(fade_in);
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

                            AssignClickListenerToAllValidCards(CurrentGame.CardClick_Listener);
                            for (int i = 0; i < MaxCardCount; i++) {
                                SelectedCards[i].setImageResource(R.drawable.lock);
                            }
                            SetEnableControls(true, CurrentGame.GameBoard);
                            CurrentGame.Btn_Power.setEnabled(true);


                            CurrentGame.isAnimating = false;
                            //Sync threads !!
                            CurrentGame.ReleaseLOCK();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                    for(int i = 0;i<MaxCardCount;i++)
                    {
                        SelectedCards[i].startAnimation(fade_out);
                    }
                }
            }
        };

        //endregion

        //region DESTROY Logic

        lsDestroyLogic = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( CurrentGame.ActualClickCount==0 )
                {
                    CurrentGame.GameTimer.cancel();
                    CurrentGame.GameTimer.start();
                }
                TransitionDrawable cross_fade = CreateTransitionDrawable(R.drawable.lock, R.drawable.card_destroyed,CurrentGame.mContext);
                if(CurrentCardCount<MaxCardCount) {
                    ImageView card = (ImageView)view;
                    SelectedCards[CurrentCardCount++] = card;
                    card.setOnClickListener(null);
                    card.setImageDrawable(cross_fade);
                    cross_fade.startTransition(1000);

                    RotateAnimation rotate = new RotateAnimation(0.0f,360f,Animation.RELATIVE_TO_SELF,.5f,Animation.RELATIVE_TO_SELF,.5f);
                    rotate.setStartOffset(100);
                    rotate.setDuration(900);
                    AnimationSet anim = new AnimationSet(true);
                    anim.addAnimation(rotate);
                    anim.setInterpolator(new LinearInterpolator());
                    card.startAnimation(anim);

                    int r = Integer.parseInt(card.getTag().toString().split(DELIMITER)[0]);
                    int c = Integer.parseInt(card.getTag().toString().split(DELIMITER)[1]);
                    CurrentGame.IV_AllCards[r][c] = null;
                    CurrentGame.DestroyedCards[CurrentGame.DestroyedCards_Top++] = card.getTag().toString();
                }
                if (CurrentCardCount == MaxCardCount)
                {
                    SetEnableControls(false,CurrentGame.GameBoard);
                    new CountDownTimer(1000,1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            //Sync threads !!
                            CurrentGame.AcquireLOCK();

//                            for(int i = 0;i<MaxCardCount;i++)
//                            {
//                                int r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
//                                int c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
//                                CurrentGame.IV_AllCards[r][c] = null;
//                                CurrentGame.DestroyedCards[CurrentGame.DestroyedCards_Top++] = SelectedCards[i].getTag().toString();
//                            }
                            CurrentGame.clickAdjustment_destroyedCards = getClickAdjustment_destroyedCards();

                            AssignClickListenerToAllValidCards(CurrentGame.CardClick_Listener);
                            SetEnableControls(true,CurrentGame.GameBoard);
                            CurrentGame.Btn_Power.setEnabled(true);

                            //Sync threads !!
                            CurrentGame.ReleaseLOCK();
                        }
                    }.start();
                }
            }
        };

        //endregion

        //region PEEK Logic

        lsPeekLogic = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final long one_frame_time = 400;
                final AlphaAnimation fade_out = new AlphaAnimation(1.0f,0f);
                fade_out.setDuration(one_frame_time);
                fade_out.setStartOffset(0);
                fade_out.setFillAfter(true);

                final AlphaAnimation fade_in = new AlphaAnimation(0f,1f);
                fade_in.setDuration(one_frame_time);
                fade_in.setStartOffset(0);
                fade_in.setFillAfter(true);

                final ImageView card = (ImageView)view;
                int r = Integer.parseInt(card.getTag().toString().split(DELIMITER)[0]);
                int c = Integer.parseInt(card.getTag().toString().split(DELIMITER)[1]);
                card.setEnabled(false);
                card.setImageResource(CurrentGame.Cards_ImageResID[r][c]);
                card.startAnimation(fade_in);
                if( CurrentGame.ActualClickCount==0 )
                {
                    CurrentGame.GameTimer.cancel();
                    CurrentGame.GameTimer.start();
                }
                CurrentCardCount++;
                CurrentGame.Card_Clicks[r][c]++;
//                CurrentGame.ActualClickCount++;
                  CurrentGame.CardLastClicked[r][c]=CurrentGame.ActualClickCount;
//                if(CurrentGame.PlayerOne_Turn || CurrentGame.PlayerMode == ONE_PLAYER)
//                    CurrentGame.Player1_Moves++;

//                if(CurrentGame.PlayerMode == ROBOT_PLAYER)
//                {
//                    CurrentGame.robotPlayer.AddToMemory(r,c);
//                }

                final long total_animation_time = one_frame_time*2;
                new CountDownTimer(total_animation_time,one_frame_time)
                {
                    @Override
                    public void onTick(long l) {
                        //Sync threads !!
                        CurrentGame.AcquireLOCK();

                        card.startAnimation(fade_in);

                        //Sync threads !!
                        CurrentGame.ReleaseLOCK();
                    }

                    @Override
                    public void onFinish() {
                        //Sync threads !!
                        CurrentGame.AcquireLOCK();

                        card.setImageResource(R.drawable.lock);
                        card.startAnimation(fade_in);
                        card.setEnabled(true);

                        //Sync threads !!
                        CurrentGame.ReleaseLOCK();
                    }

                }.start();


                if (CurrentCardCount == MaxCardCount)
                {
                    SetEnableControls(false, CurrentGame.GameBoard);
                    new CountDownTimer(800,800) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            //Sync threads !!
                            CurrentGame.AcquireLOCK();

                            AssignClickListenerToAllValidCards(CurrentGame.CardClick_Listener);
                            SetEnableControls(true,CurrentGame.GameBoard);
                            CurrentGame.Btn_Power.setEnabled(true);

                            //Sync threads !!
                            CurrentGame.ReleaseLOCK();
                        }
                    }.start();
                }
            }
        };

        //endregion

        //region extra - move

        lsExtraMovesLogic = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentCard = (ImageView)view;
                CurrentCard.startAnimation(CurrentGame.Flip_anim);
            }
        };

        //endregion

    }

    //EXTRA_MOVES Logic
    private void InitializeExtraMovesListener()
    {
        Animation.AnimationListener ExtraMoves_Listener;
        ExtraMoves_Listener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                //Sync threads !!
                CurrentGame.AcquireLOCK();
                CurrentGame.isAnimating = true;

                SetEnableControls(false,CurrentGame.GameBoard);
                if( CurrentGame.ActualClickCount==0 )
                {
                    CurrentGame.GameTimer.cancel();
                    CurrentGame.GameTimer.start();
                }

                SelectedCards[CurrentCardCount++] = CurrentCard;
                int i = Integer.parseInt(CurrentCard.getTag().toString().split(DELIMITER)[0]);
                int j = Integer.parseInt(CurrentCard.getTag().toString().split(DELIMITER)[1]);
                CurrentGame.ActualClickCount++;
                CurrentGame.Card_Clicks[i][j]++;
                CurrentGame.CardLastClicked[i][j]=CurrentGame.ActualClickCount;
                //Effective Click Count is not updated for this power.

                CurrentGame.IV_AllCards[i][j] = null;
                CurrentCard.setOnClickListener(null);

                //Sync threads !!
                CurrentGame.ReleaseLOCK();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Sync threads !!
                CurrentGame.AcquireLOCK();

                int r = Integer.parseInt(CurrentCard.getTag().toString().split(DELIMITER)[0]);
                int c = Integer.parseInt(CurrentCard.getTag().toString().split(DELIMITER)[1]);
                CurrentCard.setImageResource(CurrentGame.Cards_ImageResID[r][c]);
                boolean matchFound = false;
                //HashMap<Integer,ImageView> imgResIds = new HashMap<Integer,ImageView>();
                SparseArray<ImageView> imgResIds = new SparseArray<>();
                for(int i=0; i<CurrentCardCount;i++)
                {
                    r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                    c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);

                    if(imgResIds.indexOfKey(CurrentGame.Cards_ImageResID[r][c]) >= 0)
                    {
                        matchFound = true;
                        CurrentGame.SelectedCards[0] = imgResIds.get(CurrentGame.Cards_ImageResID[r][c]);
                        CurrentGame.SelectedCards[1] = SelectedCards[i];
                        CurrentGame.RemoveCardFromReplacementList(CurrentGame.Cards_ImageResID[r][c]);
                        break;
                    }
                    else
                    {
                        imgResIds.put(CurrentGame.Cards_ImageResID[r][c],SelectedCards[i]);
                    }
                }

                if(!matchFound )
                {
                    if(CurrentCardCount==MaxCardCount)
                    {
                        if(CurrentGame.PlayerOne_Turn || CurrentGame.PlayerMode == ONE_PLAYER)
                            CurrentGame.Player1_Moves++;
                        CurrentGame.TotalMoves++;

                        CurrentGame.Btn_Power.setEnabled(true);
                        new CountDownTimer(CurrentGame.LockingTime, CurrentGame.LockingTime) {
                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                //Sync threads !!
                                CurrentGame.AcquireLOCK();

                                CurrentGame.PlayerOne_Turn = !CurrentGame.PlayerOne_Turn;
                                CurrentGame.SoundEffect(false);
                                for(int i=0;i<MaxCardCount;i++)
                                {
                                    int r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                                    int c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
                                    CurrentGame.IV_AllCards[r][c] = SelectedCards[i];
                                    SelectedCards[i].setImageResource(R.drawable.lock);
                                }
                                AssignClickListenerToAllValidCards(CurrentGame.CardClick_Listener);
                                SetEnableControls(true,(ViewGroup)(CurrentCard.getParent()).getParent().getParent());

                                if (CurrentGame.PlayerMode == ROBOT_PLAYER && !CurrentGame.PlayerOne_Turn) {
                                    SetEnableControls(false, CurrentGame.GameBoard);
                                    CurrentGame.Btn_Power.setEnabled(false);
                                    CurrentGame.robotPlayer.SimulateMove();
                                }

                                //Sync threads !!
                                CurrentGame.ReleaseLOCK();
                            }
                        }.start();
                        CurrentGame.Flip_anim.setAnimationListener(CurrentGame.DefaultFlipListener);
                    } else
                        SetEnableControls(true, CurrentGame.GameBoard);

                }
                else//match found
                {
                    CurrentGame.SoundEffect(true);
                    if(CurrentGame.PlayerOne_Turn || CurrentGame.PlayerMode == ONE_PLAYER)
                        CurrentGame.Player1_Moves++;
                    CurrentGame.TotalMoves++;

                    if (CurrentGame.PlayerOne_Turn)
                    {
                        CurrentGame.PlayerOne_Score++;
                    }
                    else
                        CurrentGame.PlayerTwo_Score++;

                    if(CurrentGame.GameMode == TIME_TRIAL)
                    {
                        CurrentGame.objTimeTrail.TimeTrialTimer.cancel();
                        CurrentGame.objTimeTrail.TimeTrialTimer.start();
                    }

                    CurrentGame.SelectedCards[0].setOnClickListener(null);
                    CurrentGame.SelectedCards[1].setOnClickListener(null);
                    CurrentGame.EffectiveClickCount+=2;
                    CurrentGame.SetGameInfoText();


                    for(int i=0;i<CurrentCardCount;i++)
                    {
                        r = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[0]);
                        c = Integer.parseInt(SelectedCards[i].getTag().toString().split(DELIMITER)[1]);
                        if(SelectedCards[i] != CurrentGame.SelectedCards[0] && SelectedCards[i] != CurrentGame.SelectedCards[1]) {
                            CurrentGame.IV_AllCards[r][c] = SelectedCards[i];
                            SelectedCards[i].setImageResource(R.drawable.lock);
                        }
                    }
                    AssignClickListenerToAllValidCards(CurrentGame.CardClick_Listener);
                    SetEnableControls(true, CurrentGame.GameBoard);
                    CurrentGame.Btn_Power.setEnabled(true);
                    if (CurrentGame.PlayerMode == ROBOT_PLAYER && !CurrentGame.PlayerOne_Turn) {
                        SetEnableControls(false,CurrentGame.GameBoard);
                        CurrentGame.Btn_Power.setEnabled(false);
                        CurrentGame.robotPlayer.SimulateMove();
                    }

                    if (CurrentGame.EffectiveClickCount + CurrentGame.clickAdjustment_destroyedCards
                            == CurrentGame.TotalCardsOnBoard)
                    {
                        if (CurrentGame.GameTimer != null) {
                            CurrentGame.GameTimer.cancel();
                        }
                        if (CurrentGame.GameMode == TIME_TRIAL)
                            CurrentGame.objTimeTrail.TimeTrialTimer.cancel();

                        CurrentGame.postGameLogic();
                    }
                    CurrentGame.Flip_anim.setAnimationListener(CurrentGame.DefaultFlipListener);
                }


                CurrentGame.isAnimating = false;
                //Sync threads !!
                CurrentGame.ReleaseLOCK();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        CurrentGame.Flip_anim.setAnimationListener(ExtraMoves_Listener);
    }

    //FIND Logic
    private void CreateFindDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(CurrentGame.mContext)
                .show();
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        View v =  CurrentGame.mContext.CurrentView;
        lp.width = v.getMeasuredWidth() - ConvertToPx(CurrentGame.mContext, 100); //WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = v.getMeasuredHeight() - ConvertToPx(CurrentGame.mContext, 70);//WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        // final Dialog dialog = new Dialog(CurrentGame.mContext);
        //LayoutInflater inflater= getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) CurrentGame.mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View pwd=inflater.inflate(R.layout.dialog_pow_find, null, false);
        pwd.findViewById(R.id.btnClose).setVisibility(View.GONE);

        final GridView gridview = (GridView) pwd.findViewById(R.id.gdCards);
        final ImageAdapter validCardSet = new ImageAdapter(CurrentGame.mContext);
        validCardSet.createCardsSet(CurrentGame.IV_AllCards, CurrentGame.Cards_ImageResID, CurrentGame.RowSize, CurrentGame.ColumnSize);
        gridview.setAdapter(validCardSet);
        TextView tvTitle = (TextView)pwd.findViewById(R.id.tvTitle);
        tvTitle.setText("Select Card");


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String card_pos = validCardSet.getItem(position).toString();

                if( CurrentGame.ActualClickCount==0 )
                {
                    CurrentGame.GameTimer.cancel();
                    CurrentGame.GameTimer.start();
                }

                int r = Integer.parseInt(card_pos.split(DELIMITER)[0]);
                int c = Integer.parseInt(card_pos.split(DELIMITER)[1]);
                CurrentGame.Card_Clicks[r][c]++;
                CurrentGame.ActualClickCount++;
                CurrentGame.CardLastClicked[r][c]=CurrentGame.ActualClickCount;

                final ImageView target_card = CurrentGame.IV_AllCards[r][c];
                target_card.getParent().requestChildFocus(target_card,target_card);


                final long one_frame_time = 350;
                final AlphaAnimation fade_out = new AlphaAnimation(1.0f,0f);
                fade_out.setDuration(one_frame_time);
                fade_out.setStartOffset(0);
                fade_out.setFillAfter(true);

                final AlphaAnimation fade_in = new AlphaAnimation(0f,1f);
                fade_in.setDuration(one_frame_time);
                fade_in.setStartOffset(0);
                fade_in.setFillAfter(true);


                dialog.dismiss();
                final long total_animation_time = one_frame_time*2;
                new CountDownTimer(total_animation_time,one_frame_time)
                {
                    @Override
                    public void onTick(long l) {
                        //Sync threads !!
                        CurrentGame.AcquireLOCK();

                        target_card.startAnimation(fade_out);

                        //Sync threads !!
                        CurrentGame.ReleaseLOCK();
                    }

                    @Override
                    public void onFinish() {
                        //Sync threads !!
                        CurrentGame.AcquireLOCK();

                        target_card.startAnimation(fade_in);
                        if(CurrentGame.GameMode == TIME_TRIAL)
                            CurrentGame.objTimeTrail.TimeTrialTimer.start();

                        CurrentGame.powFind_flag = false;

                        //Sync threads !!
                        CurrentGame.ReleaseLOCK();
                    }

                }.start();

            }
        });


        if(CurrentGame.GameMode == TIME_TRIAL)
            CurrentGame.objTimeTrail.TimeTrialTimer.cancel();
        dialog.setContentView(pwd);
        dialog.show();
    }


    public void CreatePowerDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(CurrentGame.mContext)
                .show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        View v =  CurrentGame.mContext.CurrentView;
        if(v.getMeasuredHeight()<v.getMeasuredWidth()) {
            lp.width = v.getMeasuredWidth() - ConvertToPx(CurrentGame.mContext, 100); //WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = v.getMeasuredHeight() - ConvertToPx(CurrentGame.mContext, 70);//WindowManager.LayoutParams.WRAP_CONTENT;
        }
        else
        {
            lp.width = v.getMeasuredWidth() - ConvertToPx(CurrentGame.mContext, 50); //WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = v.getMeasuredHeight() - ConvertToPx(CurrentGame.mContext, 100);//WindowManager.LayoutParams.WRAP_CONTENT;
        }
        window.setAttributes(lp);


        LayoutInflater inflater = (LayoutInflater) CurrentGame.mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View pwd=inflater.inflate(R.layout.dialog_pow_find, null, false);
        pwd.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        int cardsLeft = getNumberOfCardLeftOnBoard();

        final GridView gridview = (GridView) pwd.findViewById(R.id.gdCards);
        final PowerCardAdapter powers = new PowerCardAdapter(CurrentGame.mContext,cardsLeft);
        gridview.setAdapter(powers);
        TextView tvTitle = (TextView)pwd.findViewById(R.id.tvTitle);
        tvTitle.setText("Select Power");

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                dialog.dismiss();

                String card = powers.getItem(position).toString();
                int powType = Integer.parseInt(card.split(DELIMITER)[0]);
                int pow_range = Integer.parseInt(card.split(DELIMITER)[1]);
                int powCount = Integer.parseInt(card.split(DELIMITER)[2]);
                powers.writePowersToPreferences(powType,pow_range,powCount);

                switch (powType)
                {

                    case POW_SWAP:
                        assignClickListenerToCard(0,pow_range,lsSwapLogic);
                        break;
                    case POW_SHUFFLE:
                        assignClickListenerToCard(0,CurrentGame.RowSize*CurrentGame.ColumnSize,lsShuffleLogic);
                        break;
                    case POW_REPLACE:
                        assignClickListenerToCard(0,pow_range,lsReplaceLogic);
                        break;
                    case POW_DESTROY:
                        assignClickListenerToCard(0,pow_range,lsDestroyLogic);
                        break;
                    case POW_PEEK:
                        assignClickListenerToCard(0,pow_range,lsPeekLogic);
                        break;
                    case POW_EXTRA_MOVES:
                        assignClickListenerToCard(0, pow_range, lsExtraMovesLogic);
                        InitializeExtraMovesListener();
                        if(CurrentGame.EffectiveClickCount%2 == 1)
                        {
                            CurrentGame.EffectiveClickCount--;
                            SelectedCards[CurrentCardCount++] = CurrentGame.SelectedCards[0];
                        }
                        break;
                    case POW_FIND:

                        CurrentGame.powFind_flag = true;
                        CreateFindDialog();
                        break;
                }
            }
        });

        dialog.setContentView(pwd);
        dialog.show();
    }


    private int getNumberOfCardLeftOnBoard()
    {
        int count = 0;
        for(int i=0;i<CurrentGame.RowSize;i++)
        {
            for(int j=0;j<CurrentGame.ColumnSize;j++)
            {
                if(CurrentGame.IV_AllCards[i][j] != null)
                    count++;
            }
        }
        return count;
    }


    public void AssignClickListener(Button btn_power) {

        if(userHasPowers())
        {
            btn_power.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InitializePowerListeners();
                    CreatePowerDialog();
                }
            });
        }
        else
        {
            btn_power.setVisibility(View.GONE);
        }
    }

    public boolean userHasPowers()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CurrentGame.mContext);
        int powCount = preferences.getInt(String.valueOf(POWER_COUNT), 0);
        return powCount != 0;
    }
}
