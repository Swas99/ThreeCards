package com.archer.matching_card_game.three_cards;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;

import static com.archer.matching_card_game.three_cards.HelperClass.ALPHABET;
import static com.archer.matching_card_game.three_cards.HelperClass.BOARD_TYPE;
import static com.archer.matching_card_game.three_cards.HelperClass.CARD_SET;
import static com.archer.matching_card_game.three_cards.HelperClass.COLUMN_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.ConvertToPx;
import static com.archer.matching_card_game.three_cards.HelperClass.FLIP_ANIMATION_TIME;
import static com.archer.matching_card_game.three_cards.HelperClass.GAME_MODE;
import static com.archer.matching_card_game.three_cards.HelperClass.LOCKING_TIME;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_MODE;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_ONE_NAME;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_TWO_NAME;
import static com.archer.matching_card_game.three_cards.HelperClass.PLAYER_TWO_TYPE;
import static com.archer.matching_card_game.three_cards.HelperClass.QUICK_GAME;
import static com.archer.matching_card_game.three_cards.HelperClass.ROBOT_MEMORY;
import static com.archer.matching_card_game.three_cards.HelperClass.ROW_SIZE;
import static com.archer.matching_card_game.three_cards.HelperClass.SCROLL_TYPE;
import static com.archer.matching_card_game.three_cards.HelperClass.SetFontToControls;
import static com.archer.matching_card_game.three_cards.HelperClass.TIME_TRIAL_TIMER;
import static com.archer.matching_card_game.three_cards.HelperClass.getWindowSize;

public class SettingsScreen implements View.OnClickListener {
    
    final int BUTTON_OK =-1;
    final int BUTTON_CLEAR = -2;
    final int BUTTON_CANCEL = -3;
    final String DELIMITER = HelperClass.DELIMITER + HelperClass.DELIMITER_2 + HelperClass.DELIMITER;
    MainActivity mContext;
    View.OnClickListener Process_Input;
    EditText edit_PlayerName;
    
    public SettingsScreen(WeakReference<MainActivity> context)
    {
        mContext = context.get();
    }

    public void loadScreen()
    {
        mContext.loadView(R.layout.screen_settings);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
        View view = mContext.CurrentView.findViewById(R.id.screenSettings);
        SetFontToControls(font, (ViewGroup) view);
        Initialize();

        //region Check if Ad-Free version
        if(!mContext.adFreeVersion) {
            final AdView mAdView = (AdView) mContext.CurrentView.findViewById(R.id.adView);
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

    public String getOneTouchFlip()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int x = preferences.getInt(String.valueOf(FLIP_ANIMATION_TIME), 9);
        if(x>20)
            return "OFF";
        else
            return "ON";
    }

    public String getLockingTime()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int x= preferences.getInt(String.valueOf(LOCKING_TIME), 600);
         return String.valueOf(x) + " ms";
    }

    private void Initialize()
    {
        TextView PlayerOneName;
        TextView PlayerTwoName;
        TextView btnPlayerOneName;
        TextView btnPlayerTwoName;
        TextView LockingTime;
        TextView btnLockingTime;
        TextView OneTouchFlip;
        TextView btnRestoreDefaults;
        View btnBack;
        //buttons
        PlayerOneName = (TextView)mContext.findViewById(R.id.PlayerOneName);
        PlayerOneName.setText(mContext.playerOneName);

        PlayerTwoName = (TextView)mContext.findViewById(R.id.PlayerTwoName);
        PlayerTwoName.setText(mContext.playerTwoName);

        btnPlayerOneName = (TextView)mContext.findViewById(R.id.btnPlayerOneName);
        btnPlayerTwoName = (TextView)mContext.findViewById(R.id.btnPlayerTwoName);

        OneTouchFlip = (TextView)mContext.findViewById(R.id.OneTouchFlip);
        OneTouchFlip.setText(getOneTouchFlip());

        LockingTime = (TextView)mContext.findViewById(R.id.LockingTime);
        LockingTime.setText(getLockingTime());

        btnLockingTime = (TextView)mContext.findViewById(R.id.btnLockingTime);

        btnRestoreDefaults = (TextView)mContext.findViewById(R.id.btnRestoreDefaults);
        btnBack = mContext.findViewById(R.id. btnBack);
        View btn_back = mContext.findViewById(R.id.btn_back);


        InitializeListeners();

        PlayerOneName.setOnClickListener(this);
        btnPlayerOneName.setOnClickListener(this);
        PlayerTwoName.setOnClickListener(this);
        btnPlayerTwoName.setOnClickListener(this);
        OneTouchFlip.setOnClickListener(this);
        LockingTime.setOnClickListener(this);
        btnLockingTime.setOnClickListener(this);
        btnRestoreDefaults.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void InitializeListeners()
    {
        Process_Input = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                int key = Integer.parseInt(tag.split(DELIMITER)[0]);
                String value = tag.split(DELIMITER)[1];
                switch (key)
                {
                    case ALPHABET:
                        String text = String.valueOf(edit_PlayerName.getText());

                        if (value.equals("<-"))
                        {
                            if(text.length()==0)
                                return;
                            text = text.substring(0,text.length()-1);
                        }
                        else
                        {
                            if(text.length()>10)
                                return;

                            text+=value;
                        }
                        edit_PlayerName.setText(text);
                        break;
                    case BUTTON_CLEAR:
                        edit_PlayerName.setText("");
                        break;
                    case BUTTON_CANCEL:
                        mContext.CommonDialog.dismiss();
                        break;
                    case PLAYER_ONE_NAME:
                    {
                        String playerName = String.valueOf(edit_PlayerName.getText());
                        if(playerName.length()>27)
                            playerName = playerName.substring(0,27);
                        if(playerName.equals(""))
                            playerName="Player A";

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(String.valueOf(PLAYER_ONE_NAME), playerName);
                        editor.apply();
                        ((TextView)mContext.findViewById(R.id.PlayerOneName)).setText(playerName);
                        mContext.playerOneName = playerName;
                        mContext.CommonDialog.dismiss();
                        break;
                    }
                    case PLAYER_TWO_NAME:
                    {
                        String playerName = String.valueOf(edit_PlayerName.getText());
                        if(playerName.length()>27)
                            playerName = playerName.substring(0,27);
                        if(playerName.equals(""))
                            playerName="Player B";

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(String.valueOf(PLAYER_TWO_NAME), playerName);
                        editor.apply();
                        ((TextView)mContext.findViewById(R.id.PlayerTwoName)).setText(playerName);
                        mContext.playerTwoName = playerName;
                        mContext.CommonDialog.dismiss();
                        break;
                    }
                    case LOCKING_TIME:
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(String.valueOf(LOCKING_TIME), Integer.parseInt(value));
                        editor.apply();
                        ((TextView)mContext.findViewById(R.id.LockingTime)).setText(value + " ms");
                        mContext.CommonDialog.dismiss();
                        break;
                }
            }
        };
    }

    private void restoreDefaultValues() {
        String msg = "Are you sure?\n\nSelect 'Yes' to continue.\nSelect 'No' to cancel.";
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Restore Default Values ");
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(true);
        // alertDialog.setIcon(R.drawable.delete);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                reset();
                dialog.dismiss();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void reset() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(String.valueOf(FLIP_ANIMATION_TIME));
        editor.remove(String.valueOf(LOCKING_TIME));

        //Remove defaults values for quick game
        String id = String.valueOf(QUICK_GAME);
        editor.remove(String.valueOf(PLAYER_MODE)+id);
        editor.remove(String.valueOf(PLAYER_TWO_TYPE)+id);
        editor.remove(String.valueOf(ROBOT_MEMORY)+id);
        editor.remove(String.valueOf(GAME_MODE)+id);
        editor.remove(String.valueOf(TIME_TRIAL_TIMER)+id);
        editor.remove(String.valueOf(BOARD_TYPE)+id);
        editor.remove(String.valueOf(ROW_SIZE)+id);
        editor.remove(String.valueOf(COLUMN_SIZE)+id);
        editor.remove(String.valueOf(SCROLL_TYPE)+id);
        editor.remove(String.valueOf(CARD_SET)+id);

        //Remove default values to normal game
        editor.remove(String.valueOf(PLAYER_MODE));
        editor.remove(String.valueOf(PLAYER_TWO_TYPE));
        editor.remove(String.valueOf(ROBOT_MEMORY));
        editor.remove(String.valueOf(GAME_MODE));
        editor.remove(String.valueOf(TIME_TRIAL_TIMER));
        editor.remove(String.valueOf(BOARD_TYPE));
        editor.remove(String.valueOf(ROW_SIZE));
        editor.remove(String.valueOf(COLUMN_SIZE));
        editor.remove(String.valueOf(SCROLL_TYPE));
        editor.remove(String.valueOf(CARD_SET));

        editor.apply();

        ((TextView)mContext.findViewById(R.id.LockingTime)).setText(getLockingTime());
        ((TextView)mContext.findViewById(R.id.OneTouchFlip)).setText(getOneTouchFlip());
    }


    private void displayDialog(View v,boolean isCancelable)
    {
        if(mContext.CommonDialog==null)
            mContext.CommonDialog  = new AlertDialog.Builder(mContext).show();
        else
            mContext.CommonDialog.show();
        mContext.CommonDialog.setContentView(v);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurry up.ttf");
        SetFontToControls(font, (ViewGroup) v);
        mContext.CommonDialog.setCancelable(isCancelable);
    }


    private View createNameDialog(String titleText,String value,int identifier)
    {
        Point windowSize = getWindowSize(mContext.getWindowManager().getDefaultDisplay());
        int five_dip = ConvertToPx(mContext, 5);
        int dialogWidth = windowSize.x - 10*five_dip;

        LinearLayout mainContainer = new LinearLayout(mContext);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.addView(getTitleTextView(titleText));
        mainContainer.setBackgroundColor(Color.argb(180, 255, 255, 255));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams verticalDivider_params= new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layoutParams.gravity = Gravity.CENTER;
        mainContainer.setLayoutParams(layoutParams);

        EditText tv = new EditText(mContext);
        tv.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        tv.setText(value);
        tv.setEnabled(false);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        layoutParams.setMargins(five_dip, five_dip * 3, five_dip, five_dip);
        tv.setLayoutParams(layoutParams);
        mainContainer.addView(tv);
        mainContainer.addView(getDivider(verticalDivider_params));
        addAlphabets(mainContainer);

        addButtons(mainContainer,identifier);
        edit_PlayerName = tv;
        return mainContainer;
    }

    private void addButtons(LinearLayout mainContainer,int id)
    {
        int identifier[] = {BUTTON_CLEAR,BUTTON_OK,BUTTON_CANCEL};
        LinearLayout.LayoutParams row_Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams box_Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ConvertToPx(mContext,45),1f);

        box_Params.setMargins(ConvertToPx(mContext,4),ConvertToPx(mContext,9),
                ConvertToPx(mContext,4),ConvertToPx(mContext,9));
        LinearLayout row = new LinearLayout(mContext);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(row_Params);
        mainContainer.addView(row);
        for(int i=0;i<3;i++)
        {
            Button tv = new Button(mContext);
            tv.setText(getText(identifier[i]));
            String tvTag;
            if(identifier[i] == BUTTON_OK)
                tvTag = id+ DELIMITER + getText(identifier[i]);
            else
                tvTag = identifier[i] + DELIMITER + getText(identifier[i]);
            tv.setTag(tvTag);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundResource(R.drawable.btn_white_transparency_20);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0,ConvertToPx(mContext,2),0,0);
            tv.setLayoutParams(box_Params);
            tv.setOnClickListener(Process_Input);
            row.addView(tv);
        }
    }

    private void addAlphabets(LinearLayout mainContainer)
    {
        String alphabets[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
                "N","O","P","Q","R","S","T","U","V","W","X","Y","Z","_","."," ","<-"};
        int length = alphabets.length;

        LinearLayout.LayoutParams row_Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams box_Params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ConvertToPx(mContext,40),1f);
        RelativeLayout.LayoutParams verticalDivider_params= new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        RelativeLayout.LayoutParams horizontalDivider_params= new RelativeLayout.LayoutParams(1,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int i=0;
        while(i<length)
        {
            LinearLayout row = new LinearLayout(mContext);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(row_Params);
            mainContainer.addView(row);
            mainContainer.addView(getDivider(verticalDivider_params));
            for(int col=0;col<5 && i<length;col++,i++)
            {
                Button tv = new Button(mContext);
                tv.setText(alphabets[i]);
                String tvTag = ALPHABET + DELIMITER + alphabets[i];
                tv.setTag(tvTag);
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.argb(180, 255, 255, 255));
                tv.setGravity(Gravity.CENTER);
                tv.setPadding(0, ConvertToPx(mContext, 3), 0, 0);
                tv.setLayoutParams(box_Params);
                tv.setOnClickListener(Process_Input);
                row.addView(tv);
                row.addView(getDivider(horizontalDivider_params));
            }
        }
    }

    private View getTitleTextView(String titleText)
    {
        int five_dip = ConvertToPx(mContext, 5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tvTitle = new TextView(mContext);
        tvTitle.setText(titleText);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setBackgroundColor(Color.BLACK);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setLayoutParams(layoutParams);
        tvTitle.setPadding(4 * five_dip, 2 * five_dip, 4 * five_dip, 2 * five_dip);
        return tvTitle;
    }


    private View getLockingTimeValue()
    {
        String text[] =  {"200 ms","400 ms","600 ms","800 ms","1000 ms","1200 ms","1400 ms" };
        int tag [] =  {200,400,600,800,1000,1200,1400};
        return addToMainContainer(tag, text, String.valueOf(LOCKING_TIME), tag.length, "Select Locking Time");
    }

    private View addToMainContainer(int[] tag, String[] text,String identifier,int length,String titleText) {
        LinearLayout mainContainer = new LinearLayout(mContext);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.addView(getTitleTextView(titleText));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConvertToPx(mContext,190),
                ConvertToPx(mContext, 45));
        RelativeLayout.LayoutParams rl_params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                1);
        for (int i=0;i<length;i++)
        {
            TextView tv = new Button(mContext);
            tv.setText(text[i]);
            String tvTag = identifier + DELIMITER + String.valueOf(tag[i]);
            tv.setTag(tvTag);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.argb(180, 255, 255, 255));
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0,ConvertToPx(mContext,3),0,0);
            tv.setLayoutParams(layoutParams);
            mainContainer.addView(tv);
            tv.setOnClickListener(Process_Input);

            mainContainer.addView(getDivider(rl_params));
        }

        return mainContainer;
    }



    private View getDivider(RelativeLayout.LayoutParams rl_params)
    {
        RelativeLayout divider = new RelativeLayout(mContext);
        divider.setLayoutParams(rl_params);
        divider.setBackgroundColor(Color.BLACK);
        return divider;
    }

    public String getText(int identifier)
    {
        String text = "";
        switch (identifier)
        {
            case BUTTON_CANCEL:
                text = "Cancel";
                break;
            case BUTTON_CLEAR:
                text = "Clear";
                break;
            case BUTTON_OK:
                text = "OK";
                break;
        }

        return text;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.PlayerOneName:
            case R.id.btnPlayerOneName:
                displayDialog(createNameDialog("Enter Player One Name", mContext.playerOneName, PLAYER_ONE_NAME), true);
                break;
            case R.id.PlayerTwoName:
            case R.id.btnPlayerTwoName:
                displayDialog(createNameDialog("Enter Player Two Name", mContext.playerTwoName, PLAYER_TWO_NAME), true);
                break;
            case R.id.OneTouchFlip:
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                int x = preferences.getInt(String.valueOf(FLIP_ANIMATION_TIME), 9);
                x = (x>20)? 9 : 120;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(String.valueOf(FLIP_ANIMATION_TIME), x);
                editor.apply();

                if(x>20)//Reverse it
                    ((TextView)mContext.findViewById(R.id.OneTouchFlip)).setText("OFF");
                else
                    ((TextView)mContext.findViewById(R.id.OneTouchFlip)).setText("ON");
            }
                break;
            case R.id.LockingTime:
            case R.id.btnLockingTime:
                displayDialog(getLockingTimeValue(),true);
                break;
            case R.id.btnRestoreDefaults:
                restoreDefaultValues();
                break;
            case R.id.btnBack:
            case R.id.btn_back:
                mContext.onBackPress();
                break;

        }
    }
}
