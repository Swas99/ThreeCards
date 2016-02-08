package com.archer.matching_card_game.three_cards;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import static com.archer.matching_card_game.three_cards.HelperClass.ANDROBOT;
import static com.archer.matching_card_game.three_cards.HelperClass.ARCADE;
import static com.archer.matching_card_game.three_cards.HelperClass.BOTH;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_1;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_2;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET_3;
import static com.archer.matching_card_game.three_cards.HelperClass.ConvertToPx;
import static com.archer.matching_card_game.three_cards.HelperClass.FLIP_ANIMATION_TIME;
import static com.archer.matching_card_game.three_cards.HelperClass.HORIZONTAL;
import static com.archer.matching_card_game.three_cards.HelperClass.HURRICANE;
import static com.archer.matching_card_game.three_cards.HelperClass.LOCKING_TIME;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_COL_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_ROW_SIZE_1B;
import static com.archer.matching_card_game.three_cards.HelperClass.MAX_ROW_SIZE_3B;
import static com.archer.matching_card_game.three_cards.HelperClass.NO_SCROLL;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.ONE_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.RANDOM_BOT;
import static com.archer.matching_card_game.three_cards.HelperClass.ROCK;
import static com.archer.matching_card_game.three_cards.HelperClass.SCREEN_GAME;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL;
import static com.archer.matching_card_game.three_cards.HelperClass.THREE_BOARD;
import static com.archer.matching_card_game.three_cards.HelperClass.TWO_PLAYER;
import static com.archer.matching_card_game.three_cards.HelperClass.VERTICAL;

public class HomePageTitleBar {

    MainActivity mContext;
    long count;
    int fact_index;
    boolean refreshFirstRow;

    public HomePageTitleBar(WeakReference<MainActivity> mainActivityWeakReference)
    {
        mContext = mainActivityWeakReference.get();
        fact_index = (int)(Math.random()*1000) % 27;
    }

    private void Initialize() {
        final View secondRow = mContext.findViewById(R.id.title_secondRow);
        final View thirdRow = mContext.findViewById(R.id.title_thirdRow);


        final String  msg2;

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if (hour > 2)
        {
            if(hour < 12)
                msg2 = "Good Morning";
            else if (hour < 17)
                msg2 = "Good Afternoon";
            else if (hour < 21)
                msg2 = "Good Evening";
            else
                msg2 = "";
        }
        else
            msg2 = "";


        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setFirstRow();

                secondRow.setVisibility(View.GONE);
                thirdRow.setVisibility(View.GONE);

                if (!msg2.equals("")) {
                    secondRow.setVisibility(View.VISIBLE);
                    ((TextView) mContext.findViewById(R.id.tvHeader_2)).setText(msg2);
                    ((TextView) mContext.findViewById(R.id.tvHeader_2)).setTypeface(Typeface.SANS_SERIF);
                    mContext.findViewById(R.id.firstContainer_SecondRow).setVisibility(View.GONE);
                    mContext.findViewById(R.id.secondContainer_SecondRow).setVisibility(View.GONE);
                }
            }
        });

    }


    public void requestUpdate(boolean checkRequired)
    {
        count++;

        refreshFirstRow = !checkRequired;
        Runnable myRunnable = new Runnable(){
            public void run(){
                DoComputationsInThread();
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }


    public void DoComputationsInThread()
    {
        if(count==1)
        {
            Initialize();
            return;
        }


        if(count%3 == 0)
        {
            setChallenge();
            return;
        }

        setFactMsg();
    }


    private void setChallenge()
    {
        //region create challenge

        int []AllPlayerModes = {ONE_PLAYER, TWO_PLAYER,TWO_PLAYER,TWO_PLAYER};
        int []possiblePlayerTwoType = {HURRICANE,ROCK,ANDROBOT,RANDOM_BOT};
        int []possibleRobotMemory = {1,2,3,4,5,6,7,8,9,10,3,4,5,6,7,8,9,4,5,6,4,5,6,7};
        int []possibleGameMode = {ARCADE,TIME_TRIAL,ARCADE,ARCADE};
        int []possibleTimeTrialTimer = {5,5,6,7,8,9,10,11,12,13,10,11,10,10,5,4,3,4,5,6,7};
        int []possibleBoardType = {ONE_BOARD,ONE_BOARD,ONE_BOARD,THREE_BOARD};
        int []possibleScrollType = {NO_SCROLL,NO_SCROLL,NO_SCROLL,NO_SCROLL,VERTICAL,HORIZONTAL,BOTH};
        int []possibleCardSet = {CARD_SET_1,CARD_SET_2,CARD_SET_3};
        int []possibleRowSize_1B = {3,4,5,6,7,8,9,10,4,5,6,7,8,5,6,7,6,3,4,5,6,7,8,4,5,6};
        int []possibleRowSize_2B = {1,2,3,1,2,3,1,2,3,4,1,2,3};

        final int PlayerMode = AllPlayerModes[(int)(Math.random()*1000)%AllPlayerModes.length];
        final int PlayerTwoType = possiblePlayerTwoType[(int)(Math.random()*1000)%possiblePlayerTwoType.length];
        final int RobotMemoryLevel = possibleRobotMemory[(int)(Math.random()*1000)%possibleRobotMemory.length];
        final int GameMode = possibleGameMode[(int)(Math.random()*1000)%possibleGameMode.length];
        final int TimeTrialTimer = possibleTimeTrialTimer[(int)(Math.random()*1000)%possibleTimeTrialTimer.length] * 1000;
        final int BoardType = possibleBoardType[(int)(Math.random()*1000)%possibleBoardType.length];
        final int ScrollType = possibleScrollType[(int)(Math.random()*1000)%possibleScrollType.length];
        final int CardSet = possibleCardSet[(int)(Math.random()*1000)%possibleCardSet.length];
        final int RowSize,ColSize;

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
            default:
                RowSize=6;
                ColSize=4;
        }

        //endregion

        mContext.runOnUiThread(new Runnable() {

            int Reward;

            @Override
            public void run() {
                setFirstRow();
                setSecondRow();
                setThirdRow();

                View btnAccept = mContext.findViewById(R.id.btnAccept);
                View btnDecline = mContext.findViewById(R.id.btnDecline);

                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartGame(PlayerMode,PlayerTwoType,RobotMemoryLevel,GameMode,
                                TimeTrialTimer,BoardType,RowSize,ColSize,ScrollType,CardSet);
                    }
                });

                btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestUpdate(false);
                    }
                });
            }

            private void setSecondRow()
            {
                final View secondRow = mContext.findViewById(R.id.title_secondRow);
                secondRow.setVisibility(View.VISIBLE);

                mContext.findViewById(R.id.firstContainer_SecondRow).setVisibility(View.VISIBLE);
                mContext.findViewById(R.id.secondContainer_SecondRow).setVisibility(View.VISIBLE);



                View ivRobot = mContext.findViewById(R.id.firstContainer_SecondRow);
                TextView secondContainer = (TextView)mContext.findViewById(R.id.secondContainer_SecondRow);

                TextView tvSecondRow = ((TextView)mContext.findViewById(R.id.tvHeader_2));
                tvSecondRow.setTypeface(Typeface.MONOSPACE);

                if(PlayerMode == TWO_PLAYER)
                {
                    ivRobot.setVisibility(View.VISIBLE);
                    setImageFromArray(PlayerTwoType, ivRobot);

                    secondContainer.setVisibility(View.VISIBLE);
                    secondContainer.setText(": ");

                    tvSecondRow.setText(getChallengeMsg_2P());
                    computeReward_2P();
                }
                else
                {
                    ivRobot.setVisibility(View.GONE);
                    secondContainer.setVisibility(View.GONE);
                    tvSecondRow.setText(getChallengeMsg_1P());
                    computeReward_1P();
                }
            }

            private void computeReward_1P() {
                int boardSize = RowSize*ColSize;
                if(BoardType == THREE_BOARD)
                    boardSize*=3;
                float x = (boardSize / 5);


                if(ScrollType != NO_SCROLL)
                    x += getNumberOfHiddenCards_ScrollGame()/2;

                if(GameMode == TIME_TRIAL)
                {
                    float flipAnimTime;

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    int overhead = preferences.getInt(String.valueOf(FLIP_ANIMATION_TIME), 9);
                    int lockingTime = preferences.getInt(String.valueOf(LOCKING_TIME), 600);
                    if(overhead<20)
                    {
                        flipAnimTime = 2;
                    }
                    else
                    {
                        flipAnimTime = 120*3;
                    }
                    int minTimeReqdToFlipAllCards = (int) (flipAnimTime*boardSize + ((boardSize/2)*lockingTime));
                    float qualifyingTime = minTimeReqdToFlipAllCards*2.5f;

                    int timeTrialReward = (int) (qualifyingTime/TimeTrialTimer);
                    x+=timeTrialReward;
                }
                switch (CardSet)
                {
                    case CARD_SET_1:
                        x*=1;
                        break;
                    case CARD_SET_2:
                        x*=1.2f;
                        break;
                    case CARD_SET_3:
                        x*=1.3f;
                        break;
                }
                Reward = Math.round(x);
            }

            private void computeReward_2P() {
                int boardSize = RowSize*ColSize;
                if(BoardType == THREE_BOARD)
                    boardSize*=3;
                float x = (boardSize / 4);


                if(ScrollType != NO_SCROLL)
                    x += getNumberOfHiddenCards_ScrollGame()/2;

                if(GameMode == TIME_TRIAL)
                {
                    float flipAnimTime;

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    int overhead = preferences.getInt(String.valueOf(FLIP_ANIMATION_TIME), 9);
                    int lockingTime = preferences.getInt(String.valueOf(LOCKING_TIME), 600);
                    if(overhead<20)
                    {
                        flipAnimTime = 2;
                    }
                    else
                    {
                        flipAnimTime = 120*3;
                    }
                    int minTimeReqdToFlipAllCards = (int) (flipAnimTime*boardSize + ((boardSize/2)*lockingTime));
                    float qualifyingTime = minTimeReqdToFlipAllCards*3f;

                    int timeTrialReward = (int) (qualifyingTime/TimeTrialTimer);
                    x+=timeTrialReward;
                }

                float robotMemoryFactor = 1 + ((RobotMemoryLevel-1) * 0.11f);
                x *= robotMemoryFactor;

                switch (CardSet)
                {
                    case CARD_SET_1:
                        x*=1;
                        break;
                    case CARD_SET_2:
                        x*=1.2f;
                        break;
                    case CARD_SET_3:
                        x*=1.3f;
                        break;
                }

                Reward = Math.round(x);
            }

            public int getNumberOfHiddenCards_ScrollGame()
            {
                int displaySize;
                int rowSize = RowSize;
                if(BoardType==THREE_BOARD)
                    rowSize*=3;
                int x,y;
                switch (ScrollType)
                {
                    case VERTICAL:
                        if(BoardType==ONE_BOARD)
                            displaySize = Math.min((ColSize *(ColSize +2)),(ColSize *rowSize));
                        else
                        {
                            x = ColSize +2;
                            x = x - (x%2); //reduce one if x is odd
                            displaySize = x* ColSize;
                        }
                        break;
                    case HORIZONTAL:
                        x = Math.min(4,(ColSize -1));
                        displaySize = x*rowSize;
                        break;
                    case BOTH:
                        if(BoardType==ONE_BOARD)
                        {
                            x = Math.min(ColSize, 4);
                            y = Math.min(rowSize, 6);
                        }else
                        {
                            x = Math.min(ColSize,4);
                            y = Math.min(rowSize,3);
                        }
                        displaySize = x*y;
                        break;
                    default:
                        displaySize = rowSize* ColSize;

                }
                return (rowSize* ColSize - displaySize);
            }

            private void setThirdRow()
            {
                View thirdRow = mContext.findViewById(R.id.title_thirdRow);
                thirdRow.setVisibility(View.VISIBLE);
                TextView tvThirdRow = (TextView)mContext.findViewById(R.id.tvHeader_3);
                tvThirdRow.setTypeface(Typeface.MONOSPACE);

                String text = "Reward = +" + String.valueOf(Reward) + " Coins";
                tvThirdRow.setText(text);
            }

            private void setImageFromArray(int value,View iv)
            {
                int robots[] = {HURRICANE,ROCK,ANDROBOT,RANDOM_BOT};
                int resID [] = {R.drawable.robot_hurricane,R.drawable.robot_rock,
                        R.drawable.robot_androbot, R.drawable.robot_random};
                int length = robots.length;
                for(int i = 0 ;i<length;i++)
                {
                    if(value == robots[i])
                    {
                        iv.setBackgroundResource(resID[i]);
                        break;
                    }
                }
            }

            private String getChallengeMsg_1P()
            {
                String boardSize = "["+String.valueOf(RowSize)+"x"+String.valueOf(ColSize) +"] ";
                String boardType;
                if(BoardType == ONE_BOARD)
                {
                    boardType = "[1B] ";
                }
                else
                {
                    boardType = "[2B] ";
                }


                String scroll;
                switch (ScrollType)
                {
                    case VERTICAL:
                        scroll= "[V scroll] ";
                        break;
                    case HORIZONTAL:
                        scroll= "[H scroll] ";
                        break;
                    case BOTH:
                        scroll= "[V+H scroll] ";
                        break;
                    default:
                        scroll = "";
                }

                String TTT="";
                if(GameMode == TIME_TRIAL)
                {
                    TTT = "[Time Trial-" +String.valueOf(TimeTrialTimer/1000) + "s] ";
                }

                int board_size = (int) (RowSize*ColSize*1.2f);
                if(BoardType == THREE_BOARD)
                    board_size*=1.6f;

                int max_moves = board_size;
                String text ="Challenge : ";
                String objective = "\nObjective : Complete within " + String.valueOf(max_moves) + " moves";

                return text + boardSize + boardType +scroll + TTT + objective;
            }

            private String getChallengeMsg_2P()
            {
                String boardSize = "[" + String.valueOf(RowSize)+"x"+String.valueOf(ColSize) + "] ";
                String boardType;
                if(BoardType == ONE_BOARD)
                {
                    boardType = "[1B] ";
                }
                else
                {
                    boardType = "[2B] ";
                }


                String scroll;
                switch (ScrollType)
                {
                    case VERTICAL:
                        scroll= "[V scroll] ";
                        break;
                    case HORIZONTAL:
                        scroll= "[H scroll] ";
                        break;
                    case BOTH:
                        scroll= "[V+H scroll] ";
                        break;
                    default:
                        scroll = "";
                }

                String TTT="";
                if(GameMode == TIME_TRIAL)
                {
                    TTT = "[Time Trial-" +String.valueOf(TimeTrialTimer/1000) + "s] ";
                }


                String text;
                if(RobotMemoryLevel>=7 && (RowSize*ColSize>22))
                    text = "I dare you to defeat me at ";
                else
                    text = "Defeat me at ";

                return text + boardSize + boardType +scroll + TTT;
            }


            private void StartGame(int PlayerMode, int PlayerTwoType, int RobotMemoryLevel, int GameMode, int TimeTrialTimer,
                                   int BoardType, int RowSize, int ColSize, int ScrollType, int CardSet)
            {

                if(mContext.objCardGame == null)
                    mContext.objCardGame = new Game(mContext);
                else
                {
                    mContext.objCardGame.Clear();
                }
                mContext.objCardGame.LockingTime = mContext.getLockingTime();
                mContext.objCardGame.GameBackground = 0;
                mContext.objCardGame.PlayerOne_Turn = true; //PlayerOne_FirstMove;
                mContext.objCardGame.isChallengeGame = true;
                mContext.objCardGame.ChallengeReward =Reward;
                mContext.objCardGame.setGameConfiguration(PlayerMode, PlayerTwoType, RobotMemoryLevel,
                        GameMode, TimeTrialTimer, BoardType, RowSize, ColSize, ScrollType, CardSet);
                mContext.objCardGame.StartGame();
                mContext.CURRENT_SCREEN = SCREEN_GAME;
            }
        });

    }

    private void setFactMsg() {

        final View secondRow = mContext.findViewById(R.id.title_secondRow);
        final View thirdRow = mContext.findViewById(R.id.title_thirdRow);


        final String msg;
        msg = getFact();
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setFirstRow();
                secondRow.setVisibility(View.VISIBLE);
                thirdRow.setVisibility(View.GONE);

                mContext.findViewById(R.id.firstContainer_SecondRow).setVisibility(View.GONE);

                mContext.findViewById(R.id.secondContainer_SecondRow).setVisibility(View.VISIBLE);
                ((TextView)mContext.findViewById(R.id.secondContainer_SecondRow)).setText("Tip : ");
                ((TextView)mContext.findViewById(R.id.secondContainer_SecondRow)).setTypeface(Typeface.DEFAULT_BOLD);
                mContext.findViewById(R.id.secondContainer_SecondRow).setPadding(ConvertToPx(mContext,4),0,0,0);

                ((TextView)mContext.findViewById(R.id.tvHeader_2)).setText(msg);
                ((TextView) mContext.findViewById(R.id.tvHeader_2)).setTypeface(Typeface.SANS_SERIF);
                //
            }
        });

    }

    private String getFact()
    {
        String[] facts = {
                "Accept challenges to earn extra coins",
                "Locking-Time is the time after which a card pair is locked for a mis-matched move",
                "Turn ON one touch flip for faster game play",
                "You can earn more coins by playing against a robot",
                "Playing against a robot with high memory will fetch higher coin bonus",
                "Matching cards are put in separate boards in 3 board game",
                "Win consecutive games to score more points & earn more coins",
                "Scoring better average than previous game will add to winning streak when playing in 1P mode",
                "Defeating your opponent successively will add to winning streak when playing in 2P mode",
                "Switching player modes will reset wining streak",
                "Set row size > (column size+2) when playing 1B, vertical scroll game to experience vertical scroll effect",
                "Set row size > [(column size+2)/3] when playing 3B, vertical scroll game to experience vertical scroll effect",
                "Card Set 3 will fetch higher coin bonus than Card set 2",
                "Card Set 2 will fetch higher coin bonus than Card set 1",
                "Accept challenges to earn extra coins",
                "Set column size > 4 when playing V+H scroll game to experience horizontal scroll effect",
                "Set row size > 6 when playing 1B, V+H scroll game to experience vertical scroll effect",
                "Set row size > 1 when playing 3B, V+H scroll game to experience vertical scroll effect",
                "Use Click-Count matrix to check number of times a card was clicked during game play",
                "Retaining-Power matrix : Difference of values for a card pair signifies how long you remembered the pair before the hit",
                "Use Move-Trace to check the order of hits",
                "Scroll mode: Higher the number of card not in display, Higher the coin bonus",
                "Set Row size >= (Column size/3) when playing 3B, no scroll game for better experience",
                "Set Row size >= Column size when playing 1B, no scroll game for better experience",
                "Time-Trial : Find a card pair before timer goes out to reset the timer",
                "Set column size > (row size/2) when playing 1B, horizontal scroll game for better experience",
                "Set column size > row size when playing 3B, horizontal scroll game for better experience",
        };

        if(fact_index>=facts.length)
            fact_index=0;

        return facts[fact_index++];
    }


    private void setFirstRow() {
        if(!refreshFirstRow)
            return;

        String greetings[] = {"Hello ","Hi "};
        int i = (int)(Math.random()*1000) %2;
        String msg = greetings[i] + mContext.playerOneName;

        TextView firstRow = (TextView) mContext.findViewById(R.id.tvHeader_1);
        firstRow.setVisibility(View.VISIBLE);
        if(!mContext.playerOneName.toLowerCase().equals("player 1"))
            firstRow.setText(msg);
        firstRow.setTypeface(Typeface.SANS_SERIF);
    }


}
