package com.archer.matching_card_game.three_cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import static com.archer.matching_card_game.three_cards.HelperClass.ConvertToPx;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER;
import static com.archer.matching_card_game.three_cards.HelperClass.DELIMITER_2;
import static com.archer.matching_card_game.three_cards.HelperClass.POWER_COUNT;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_DESTROY;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_EXTRA_MOVES;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_FIND;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_PEEK;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_REPLACE;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_SHUFFLE;
import static com.archer.matching_card_game.three_cards.HelperClass.POW_SWAP;
import static com.archer.matching_card_game.three_cards.HelperClass.applyBorderDrawableToView;

public class PowerCardAdapter  extends BaseAdapter {
    private Context mContext;
    private int[] CardsResID;
    private int[] CardName;
    private int[] CardIdentifier;
    private int[] CardCount;
    private int lengthOfArray;


    public Object getItem(int position) {
        return String.valueOf(CardName[position])+DELIMITER+String.valueOf(CardIdentifier[position])
                +DELIMITER+String.valueOf(CardCount[position]);
    }


    public PowerCardAdapter(Context c,int cardsLeft) {

        mContext = c;
        getPowersFromPreferences();
        Initialize(cardsLeft);
    }

    private void Initialize(int cardsLeft){

        for(int i=0; i<lengthOfArray;i++)
        {
            switch (CardName[i])
            {
                case POW_SWAP:
                    if(CardIdentifier[i]>cardsLeft || cardsLeft<2)
                    {
                        removePowerCard(i);
                        i--;
                    }
                    break;
                case POW_SHUFFLE:
                    if(cardsLeft<2)
                    {
                        removePowerCard(i);
                        i--;
                    }
                    break;
                case POW_REPLACE:
                    if(CardIdentifier[i]>cardsLeft || cardsLeft<2)
                    {
                        removePowerCard(i);
                        i--;
                    }
                    break;
                case POW_DESTROY:
                    if(CardIdentifier[i]> (cardsLeft-1))
                    {
                        removePowerCard(i);
                        i--;
                    }
                    break;
                case POW_PEEK:
                    if(CardIdentifier[i]>cardsLeft || cardsLeft<2)
                    {
                        removePowerCard(i);
                        i--;
                    }
                    break;
                case POW_EXTRA_MOVES:
                    if(CardIdentifier[i]>cardsLeft || cardsLeft<2)
                    {
                        removePowerCard(i);
                        i--;
                    }
                    break;
                case POW_FIND:
                    if(CardIdentifier[i]>cardsLeft || cardsLeft<2)
                    {
                        removePowerCard(i);
                        i--;
                    }
                    break;
            }
        }
    }

    public int getCount() {
        return lengthOfArray;
    }


    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View powerCard;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            powerCard=inflater.inflate(R.layout.power_card, null, false);

            int size = ConvertToPx(mContext, 97);
            powerCard.setLayoutParams(new GridView.LayoutParams(size, size));
            powerCard.setPadding(8, 8, 8, 8);
        } else {
            powerCard = convertView;
        }


        ImageView iv1 = (ImageView)powerCard.findViewById(R.id.ivCard);
        TextView tvCount = (TextView)powerCard.findViewById(R.id.tvCount);
        TextView tvIdentifier = (TextView) powerCard.findViewById(R.id.tvIdentifier);
        iv1.setBackgroundResource(CardsResID[position]);

        {
            final AnimationDrawable frameAnimation = (AnimationDrawable) iv1.getBackground();
            new CountDownTimer(200,200){
                @Override
                public void onTick(long i){}
                @Override
                public void onFinish()
                {
                    if(frameAnimation!=null)
                    frameAnimation.start();
                }
            }.start();
        }
        tvCount.setText(String.valueOf(CardCount[position]));
        tvIdentifier.setText(String.valueOf(CardIdentifier[position]));
        setIdentifierVisibility(tvIdentifier,position);

        applyBorderDrawableToView(tvCount, Color.RED, Color.RED, ConvertToPx(mContext, 7), 0);
        applyBorderDrawableToView(tvIdentifier, Color.argb(90, 255, 255, 255),
                Color.BLACK,ConvertToPx(mContext,7),0);
        return powerCard;
    }

    public void setIdentifierVisibility(View v,int pos)
    {
        switch (CardName[pos])
        {
            case POW_SHUFFLE:
            case POW_FIND:
                v.setVisibility(View.INVISIBLE);
                break;
            default:
                v.setVisibility(View.VISIBLE);
        }
    }

    public void removePowerCard(int pos)
    {
        RemoveFromArray(CardIdentifier,pos,lengthOfArray);
        RemoveFromArray(CardCount, pos, lengthOfArray);
        RemoveFromArray(CardsResID, pos, lengthOfArray);
        RemoveFromArray(CardName, pos, lengthOfArray);
        lengthOfArray--;
    }
    public void RemoveFromArray(int Array[],int index,int length) {
        System.arraycopy(Array, index + 1, Array, index, length - 1 - index);
    }


    private void getPowersFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        lengthOfArray = preferences.getInt(String.valueOf(POWER_COUNT), 0);
        if(lengthOfArray==0)
            return;

        int allPowers[] = {POW_EXTRA_MOVES,POW_FIND,POW_SHUFFLE,POW_SWAP,POW_REPLACE,POW_DESTROY,POW_PEEK};
        int allPowerResId[] = {R.drawable.pow_extra_moves,R.drawable.pow_find,R.drawable.pow_shuffle,
                R.drawable.pow_swap,R.drawable.pow_replace,R.drawable.pow_destroy,R.drawable.pow_peek};

        CardName = new int[lengthOfArray];
        CardsResID = new int[lengthOfArray];
        CardIdentifier = new int[lengthOfArray];
        CardCount = new int[lengthOfArray];
        int index = 0;

        for (int i=0;i<allPowers.length; i++)
        {
           String power_data;
            power_data = preferences.getString(String.valueOf(allPowers[i]), "");
            if(power_data.equals(""))
                continue;

            int card_identifier,card_count;
            String array_powerData[] = power_data.split(DELIMITER_2);
            for (String val : array_powerData)
            {
                String identifier_count[] = val.split(DELIMITER);
                card_identifier = Integer.parseInt(identifier_count[0]);
                card_count = Integer.parseInt(identifier_count[1]);
                CardName[index] = allPowers[i];
                CardsResID[index] = allPowerResId[i];
                CardIdentifier[index] = card_identifier;
                CardCount[index] = card_count;
                index++;
            }
        }
    }


    public void writePowersToPreferences(int power,int range,int count)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String power_data = preferences.getString(String.valueOf(power), "");
        int length = preferences.getInt(String.valueOf(POWER_COUNT),0);
        SharedPreferences.Editor editor = preferences.edit();
        String array_powerData[] = power_data.split(DELIMITER_2);
        String newPowerData = "";
        for (String val : array_powerData)
        {
            String identifier_count[] = val.split(DELIMITER);
            int card_identifier = Integer.parseInt(identifier_count[0]);
            if(card_identifier == range)
            {
                int card_count = Integer.parseInt(identifier_count[1]) - 1;
                if(card_count>0)
                {
                    newPowerData+=  String.valueOf(card_identifier)+ DELIMITER+String.valueOf(card_count) + DELIMITER_2;
                }
                else
                {
                    length--;
                }
            }
            else
            {
                newPowerData+=val+DELIMITER_2;
            }
        }
        editor.putString(String.valueOf(power), newPowerData);
        editor.putInt(String.valueOf(POWER_COUNT), length);
        editor.apply();
    }
}