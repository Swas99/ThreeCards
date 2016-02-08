package com.archer.matching_card_game.three_cards;

import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import static com.archer.matching_card_game.three_cards.HelperClass.*;


public class Robot {

    Game CurrentGame;
    int RobotMemoryLevel;
    float hitProbability;
    private int MemoryLogicID;
    int Card_Clicks[][];
    int ThresholdClickCount;

    public void resetHitProbability()
    {
        int r=CurrentGame.RowSize;
        int c=CurrentGame.ColumnSize;
        Card_Clicks = new int[r][c];
        switch(MemoryLogicID)
        {
            case HURRICANE:
                hitProbability = .08f + ((.72f/9f)*(RobotMemoryLevel-1));
//                ThresholdClickCount= 5 - (3*(RobotMemoryLevel-1)/9);
                break;
            case ROCK:
                hitProbability = .09f + ((.73f/9f)*(RobotMemoryLevel-1));
//                ThresholdClickCount= 4 - (3*(RobotMemoryLevel-1)/9);
                break;
            case ANDROBOT:
                hitProbability = .1f + ((.8f/9f)*(RobotMemoryLevel-1));
//                ThresholdClickCount= 3 - (2*(RobotMemoryLevel-1)/9);
                break;
        }
        ThresholdClickCount=20+RobotMemoryLevel;
    }

    public void Clear(boolean changeMemoryLogic)
    {
        resetHitProbability();
    }

    public void updateCardClick(int r,int c)
    {
        Card_Clicks[r][c]=CurrentGame.ActualClickCount;
    }
    public void resetCardClick(int r,int c)
    {
        Card_Clicks[r][c]=0;
    }
    public Robot(final WeakReference<Game> parentGame, int robotMemoryLevel,int memoryLogicID)
    {
        CurrentGame=parentGame.get();

        if(memoryLogicID == RANDOM_BOT)
        {
            int robotType[] = new int[]{ANDROBOT,HURRICANE,ROCK};
            int i = (int)(Math.random()*1000)%3;
            memoryLogicID = robotType[i];
            int randomValue = (int)(Math.random()*1000)%5 + 1;
            if(robotMemoryLevel>5)
                robotMemoryLevel = randomValue + 5;
            else
                robotMemoryLevel = randomValue;
        }
        MemoryLogicID = memoryLogicID;
        RobotMemoryLevel = robotMemoryLevel;
    }

    public void SimulateMove()
    {
        SimulateCardClick();
        switch (MemoryLogicID)
        {
            case HURRICANE:
                hitProbability+=0.0001;
                break;
            case ROCK:
                hitProbability+=0.0004;
                break;
            case ANDROBOT:
                hitProbability+=0.001;
                break;
        }
        if(hitProbability>1)
            hitProbability-=0.09;
    }

    private void FreshInMemoryCards(String[] probableCards,String[] OtherCards)
    {
        int length_probableCards =0;
        int length_otherCards = 0;
        for(int i=0;i<CurrentGame.RowSize;i++)
        {
            for(int j=0;j<CurrentGame.ColumnSize;j++)
            {
                if( CurrentGame.IV_AllCards[i][j]!=null) {
                    if ( CurrentGame.ActualClickCount - Card_Clicks[i][j] <= ThresholdClickCount)
                        probableCards[length_probableCards++] = i + DELIMITER + j;
                    else
                        OtherCards[length_otherCards++] = i+DELIMITER+j;
                }
            }
        }
    }

    private boolean doFirstClick( String[]probableCards)
    {
        int probableCards_length =getLengthOfDynamicArray( probableCards);
        int [] resId_array = new int[probableCards_length];
        int resIdArray_length = 0;
        for(int i=0;i<probableCards_length;i++)
        {
            String item=probableCards[i];
            int indexOfDelimiter=item.indexOf('_');
            int row = Integer.parseInt (item.substring(0,indexOfDelimiter));
            int col = Integer.parseInt(item.substring(indexOfDelimiter+1));
            resId_array[resIdArray_length++] = CurrentGame.Cards_ImageResID[row][col];
        }
        Arrays.sort(resId_array);
        int indexOfMatch = -1;
        for(int i=0;i<probableCards_length-2;i++)
        {
            if(resId_array[i] == resId_array[i+1] && resId_array[i+1] == resId_array[i+2] )
            {
                indexOfMatch=i;
                break;
            }
        }
        if(indexOfMatch!=-1)
        {
            int resId = resId_array[indexOfMatch];
            for(int i=0;i<probableCards_length;i++)
            {
                String item = probableCards[i];
                int indexOfDelimiter=item.indexOf('_');
                int row = Integer.parseInt (item.substring(0,indexOfDelimiter));
                int col = Integer.parseInt(item.substring(indexOfDelimiter+1));
                if(Math.random()<=hitProbability)
                {
                    if( CurrentGame.Cards_ImageResID[row][col] == resId)
                    {
                        ClickAndFocusOnView(CurrentGame.IV_AllCards[row][col]);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void FindBestMatch()
    {
        String [] probableCards = new String[CurrentGame.ColumnSize*CurrentGame.RowSize];
        String[] availableCards = new String[CurrentGame.ColumnSize*CurrentGame.RowSize];
        FreshInMemoryCards(probableCards,availableCards);
        int indexOfDelimiter,row,col;
        String item;

        //Find a match (*hopefully*)
        indexOfDelimiter = CurrentGame.SelectedCards[0].getTag().toString().indexOf(('_'));
        row = Integer.parseInt(CurrentGame.SelectedCards[0].getTag().toString().substring(0, indexOfDelimiter));
        col = Integer.parseInt(CurrentGame.SelectedCards[0].getTag().toString().substring(indexOfDelimiter + 1));
        int required_resID = CurrentGame.Cards_ImageResID[row][col];

        if(Math.random()<=hitProbability)
        {
            for(String Item : probableCards)
            {
                if(Item == null)
                    break;

                indexOfDelimiter = Item.indexOf('_');
                row = Integer.parseInt(Item.substring(0, indexOfDelimiter));
                col = Integer.parseInt(Item.substring(indexOfDelimiter + 1));
                if(CurrentGame.Cards_ImageResID[row][col] == required_resID)
                {
                    ClickAndFocusOnView(CurrentGame.IV_AllCards[row][col]);
                    return;
                }
            }
        }

        //
        if(getLengthOfDynamicArray( availableCards)==0)
        {
            int length = getLengthOfDynamicArray( probableCards);
            int randomIndex = ((int) (Math.random() * 1000)) % length;
            item = probableCards[randomIndex];
            indexOfDelimiter = item.indexOf('_');
            row = Integer.parseInt(item.substring(0, indexOfDelimiter));
            col = Integer.parseInt(item.substring(indexOfDelimiter + 1));
            ClickAndFocusOnView(CurrentGame.IV_AllCards[row][col]);
            return;
        }

        //Case 3 : No luck. Click random card
        int length = getLengthOfDynamicArray( availableCards);
        int randomIndex = ((int) (Math.random() * 1000)) % length;
        item = availableCards[randomIndex];
        indexOfDelimiter = item.indexOf('_');
        row = Integer.parseInt(item.substring(0, indexOfDelimiter));
        col = Integer.parseInt(item.substring(indexOfDelimiter + 1));
        ClickAndFocusOnView(CurrentGame.IV_AllCards[row][col]);
    }

    private void SimulateCardClick()
    {
        switch (CurrentGame.EffectiveClickCount%3)
        {
            //region Second/Third Card of the move
            case 1:
            case 2:
            {
                FindBestMatch();
                break;
            }
            //endregion
            //region First card of the move
            case 0:
            {
                int boardSize = CurrentGame.ColumnSize*CurrentGame.RowSize;
                String [] probableCards = new String[boardSize];
                String[] availableCards = new String[boardSize];
                FreshInMemoryCards(probableCards, availableCards);
                int card_number = CurrentGame.EffectiveClickCount%3;
                if(!doFirstClick(probableCards))
                {//Do a random click

                    int length = getLengthOfDynamicArray( availableCards);
                    String item;
                    if(length == 0)//Base case When number of cards on board is odd
                    {
                        item = probableCards[0];
                    }
                    else
                    {
                        int randomIndex = ((int) (Math.random() * 1000)) % length;
                        item = availableCards[randomIndex];
                    }
                    int indexOfDelimiter = item.indexOf('_');
                    int row = Integer.parseInt(item.substring(0, indexOfDelimiter));
                    int col = Integer.parseInt(item.substring(indexOfDelimiter + 1));
                    ClickAndFocusOnView(CurrentGame.IV_AllCards[row][col]);
                }
                break;
            }
            //endregion
        }
    }

}
