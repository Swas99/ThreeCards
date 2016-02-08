package com.archer.matching_card_game.three_cards;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;

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

public class Store implements View.OnClickListener{

    MainActivity mContext;
    BuyCoins objBuyCoins;
    BuyPowers objBuyPowers;
    RemoveAds objRemoveAds;
    int CURRENT_SCREEN=1;
    final int VIEW_BUY_POWERS = 1;
    final int VIEW_BUY_COINS = 5;
    final int VIEW_REMOVE_ADS = 10;

    public Store(WeakReference<MainActivity> m_context)
    {
        mContext = m_context.get();
    }

    public void Show()
    {
        mContext.loadView(R.layout.screen_store);
        addListenerToControls();
        powerClick();

        //region Check if Ad-Free version
        if(!mContext.adFreeVersion) {
            final AdView mAdView = (AdView) mContext.findViewById(R.id.adView);
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


    private void addListenerToControls()
    {
        View btnPow = mContext.findViewById(R.id.btnPower);
        View btnCoin = mContext.findViewById(R.id.btnCoin);
        View btnRemoveAds = mContext.findViewById(R.id.btnRemoveAds);
        View btn_Pow = mContext.findViewById(R.id.btn_power);
        View btn_Coin = mContext.findViewById(R.id.btn_coin);
        View btn_RemoveAds = mContext.findViewById(R.id.btn_removeAds);
        View btnBack = mContext.findViewById(R.id.btnBack);
        View btn_back = mContext.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(this);
        btnPow.setOnClickListener(this);
        btnCoin.setOnClickListener(this);
        btnRemoveAds.setOnClickListener(this);
        btn_Pow.setOnClickListener(this);
        btn_Coin.setOnClickListener(this);
        btn_RemoveAds.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void powerClick()
    {
        CURRENT_SCREEN=VIEW_BUY_POWERS;

        View btnPow = mContext.findViewById(R.id.btnPower);
        View btnCoin = mContext.findViewById(R.id.btnCoin);
        View btnRemoveAds = mContext.findViewById(R.id.btnRemoveAds);

        //Select
        btnPow.setBackgroundResource(R.drawable.btn_white_reverse);
        //Un-Select
        btnCoin.setBackgroundResource(R.drawable.btn_white_transparency_20);
        btnRemoveAds.setBackgroundResource(R.drawable.btn_white_transparency_20);

        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_buy_powers, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));

        if(objBuyPowers==null)
            objBuyPowers = new BuyPowers();
        objBuyPowers.load();
    }

    private void removeAdsClick()
    {
        CURRENT_SCREEN=VIEW_REMOVE_ADS;

        View btnPow = mContext.findViewById(R.id.btnPower);
        View btnCoin = mContext.findViewById(R.id.btnCoin);
        View btnRemoveAds = mContext.findViewById(R.id.btnRemoveAds);

        //Select
        btnRemoveAds.setBackgroundResource(R.drawable.btn_white_reverse);
        //Un-Select
        btnCoin.setBackgroundResource(R.drawable.btn_white_transparency_20);
        btnPow.setBackgroundResource(R.drawable.btn_white_transparency_20);


        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_remove_ads, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));

        if(objRemoveAds==null)
            objRemoveAds = new RemoveAds();
        objRemoveAds.InitializeRemoveAdsView();


    }

    private void coinsClick()
    {
        CURRENT_SCREEN=VIEW_BUY_COINS;

        View btnPow = mContext.findViewById(R.id.btnPower);
        View btnCoin = mContext.findViewById(R.id.btnCoin);
        View btnRemoveAds = mContext.findViewById(R.id.btnRemoveAds);

        //Select
        btnCoin.setBackgroundResource(R.drawable.btn_white_reverse);
        //Un-Select
        btnPow.setBackgroundResource(R.drawable.btn_white_transparency_20);
        btnRemoveAds.setBackgroundResource(R.drawable.btn_white_transparency_20);


        RelativeLayout frame = (RelativeLayout)mContext.findViewById(R.id.frame);
        frame.removeAllViews();
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.view_buy_coins, frame, true);
        view.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
        if(objBuyCoins==null)
            objBuyCoins = new BuyCoins();
        objBuyCoins.load();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnPower:
            case R.id.btn_power:
                if(CURRENT_SCREEN==VIEW_BUY_POWERS)
                    return;
                powerClick();
                break;
            case R.id.btnCoin:
            case R.id.btn_coin:
                if(CURRENT_SCREEN==VIEW_BUY_COINS)
                    return;
                coinsClick();
                break;
            case R.id.btnRemoveAds:
            case R.id.btn_removeAds:
                if(CURRENT_SCREEN==VIEW_REMOVE_ADS)
                    return;
                removeAdsClick();
                break;
            case R.id.btn_back:
            case R.id.btnBack:
                mContext.onBackPress();
                break;
        }
    }

    class BuyCoins implements View.OnClickListener//, AdBuddizRewardedVideoDelegate
    {
        public void load()
        {
            InitializeBuyCoinsView();
            mContext.objInAppBilling.setCoinPrice();
        }
        public void InitializeBuyCoinsView()
        {
            View btnBuyPouchOfCoins = mContext.findViewById(R.id.btnBuyPouchOfCoins);
            View btnBuyBagOfCoins = mContext.findViewById(R.id.btnBuyBagOfCoins);
            View btnBuyTrunkOfCoins = mContext.findViewById(R.id.btnBuyTrunkOfCoins);
           // View btn_get_free_coin = mContext.findViewById(R.id.btn_get_free_coin);


            btnBuyPouchOfCoins.setOnClickListener(this);
            btnBuyBagOfCoins.setOnClickListener(this);
            btnBuyTrunkOfCoins.setOnClickListener(this);
           // btn_get_free_coin.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnBuyPouchOfCoins:
                {
                    String SKU_POUCH_OF_COINS = "sku_pouch_of_coins";
                    mContext.objInAppBilling.LaunchPurchaseFlow(SKU_POUCH_OF_COINS);
                }
                    break;
                case R.id.btnBuyBagOfCoins:
                {
                    String SKU_BAG_OF_COINS = "sku_bag_of_coins";
                    mContext.objInAppBilling.LaunchPurchaseFlow(SKU_BAG_OF_COINS);
                }
                    break;
                case R.id.btnBuyTrunkOfCoins:
                {
                    String SKU_TRUNK_OF_COINS = "sku_trunk_of_coins";
                    mContext.objInAppBilling.LaunchPurchaseFlow(SKU_TRUNK_OF_COINS);
                }
                    break;
                case R.id.btn_get_free_coin:
                {
                   // AdBuddiz.RewardedVideo.show(mContext);
                }
                break;
            }
        }

//        @Override
//        public void didFetch() {
//
//        }
//
//        @Override
//        public void didFail(AdBuddizRewardedVideoError adBuddizRewardedVideoError) {
//
//        }
//
//        @Override
//        public void didComplete() {
//            long free_coin_value = 90;
//            mContext.updateCoins(free_coin_value);
//        }
//
//        @Override
//        public void didNotComplete() {
//
//        }
    }

    class RemoveAds implements View.OnClickListener
    {
        public void InitializeRemoveAdsView()
        {
            View btnRemoveAdNow = mContext.findViewById(R.id.btnRemoveAdNow);
            btnRemoveAdNow.setOnClickListener(this);
            mContext.objInAppBilling.setRemoveAdsPrice(mContext.findViewById(R.id.tvRemoveAdsPrice));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnRemoveAdNow:
                    String SKU_REMOVE_ADS = "sku_remove_ads";
                    mContext.objInAppBilling.LaunchPurchaseFlow(SKU_REMOVE_ADS);
                    break;
            }
        }
    }

    class BuyPowers implements View.OnClickListener
    {
        int allPowers[] = {POW_EXTRA_MOVES,POW_FIND,POW_SHUFFLE,POW_SWAP,POW_REPLACE,POW_DESTROY,POW_PEEK};
        int allPowerResId[] = {R.drawable.pow_extra_moves,R.drawable.pow_find,R.drawable.pow_shuffle,
                R.drawable.pow_swap,R.drawable.pow_replace,R.drawable.pow_destroy,R.drawable.pow_peek};
        String powerNames[] = {"Extra Moves","Find Card","Shuffle","Swap","Replace","Destroyer","Peek"};
        int minRange[] = {4,-1,-1,2,1,1,1};


        int currentIndex = 0;
        int count = 1;
        int identifier = minRange[currentIndex];

        TextView ivPower;
        TextView tvCount;
        TextView tvIdentifier;
        TextView tvPowerName;
        TextView tvRange;
        TextView tvQuantity;
        TextView tvPrice;

        private void InitializeScreenComponents() {
            ivPower = (TextView)mContext.findViewById(R.id.ivCard);
            tvCount = (TextView)mContext.findViewById(R.id.tvCount);
            tvIdentifier = (TextView) mContext.findViewById(R.id.tvIdentifier);
            tvPowerName = (TextView) mContext.findViewById(R.id.tvPowerName);
            applyBorderDrawableToView(tvCount, Color.RED, Color.RED, ConvertToPx(mContext, 7), 0);
            applyBorderDrawableToView(tvIdentifier, Color.argb(90, 255, 255, 255),
                    Color.BLACK, ConvertToPx(mContext, 7), 0);
            tvQuantity = (TextView) mContext.findViewById(R.id.tvQuantity);
            tvRange = (TextView) mContext.findViewById(R.id.tvRange);
            tvPrice = (TextView) mContext.findViewById(R.id.tvPrice);
        }

        public void InitializeBuyCoinsView()
        {
            View btnMyPower = mContext.findViewById(R.id.btnMyPower);
            View btn_prev_power = mContext.findViewById(R.id.btn_prev_power);
            View btn_next_power = mContext.findViewById(R.id.btn_next_power);
            View btnIncreaseRange = mContext.findViewById(R.id.btnIncreaseRange);
            View btnDecreaseRange = mContext.findViewById(R.id.btnDecreaseRange);
            View btnIncreaseQuantity = mContext.findViewById(R.id.btnIncreaseQuantity);
            View btnDecreaseQuantity = mContext.findViewById(R.id.btnDecreaseQuantity);
            View btnBuy = mContext.findViewById(R.id.btnBuy);


            btnMyPower.setOnClickListener(this);
            btn_prev_power.setOnClickListener(this);
            btn_next_power.setOnClickListener(this);
            btnIncreaseRange.setOnClickListener(this);
            btnDecreaseRange.setOnClickListener(this);
            btnIncreaseQuantity.setOnClickListener(this);
            btnDecreaseQuantity.setOnClickListener(this);
            btnBuy.setOnClickListener(this);


            TextView tvCoins = (TextView)mContext.findViewById(R.id.tvCoins);
            tvCoins.setText(String.valueOf(mContext.coins));
        }

        public void load()
        {
            InitializeBuyCoinsView();
            InitializeScreenComponents();
            setPowerCard();
        }

        public void setPowerCard()
        {
            ivPower.setBackgroundResource(allPowerResId[currentIndex]);
            {
                final AnimationDrawable frameAnimation = (AnimationDrawable) ivPower.getBackground();
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
            tvCount.setText(String.valueOf(count));
            tvQuantity.setText(String.valueOf(count));
            tvIdentifier.setText(String.valueOf(identifier));
            tvRange.setText(String.valueOf(identifier));
            setIdentifierVisibility(tvIdentifier, allPowers[currentIndex]);
            tvPowerName.setText(powerNames[currentIndex]);

            tvPrice.setText(String.valueOf(getPrice()));
        }

        public void MyPowerClick()
        {
            CreatePowerDialog();
        }
        public void previousPowerClick()
        {
            currentIndex--;
            if(currentIndex==-1)
                currentIndex=allPowers.length-1;
            if(identifier<minRange[currentIndex])
                identifier=minRange[currentIndex];
            setPowerCard();

        }
        public void nextPowerClick()
        {
            currentIndex++;
            if(currentIndex==allPowers.length)
                currentIndex=0;
//            if(identifier<minRange[currentIndex])
                identifier=minRange[currentIndex];
            setPowerCard();
        }
        public void IncreaseRangeClick()
        {
            identifier++;
            if(identifier>20)
                identifier=20;
            else
                setPowerCard();
        }
        public void DecreaseRangeClick()
        {
            identifier--;
            if(identifier<minRange[currentIndex])
                identifier=minRange[currentIndex];
            else
            setPowerCard();
        }
        public void IncreaseQuantityClick()
        {
            count++;
            if (count>20)
                count=20;
            else
             setPowerCard();
        }
        public void DecreaseQuantityClick()
        {
            count--;
            if (count==0)
                count=1;
            else
                setPowerCard();
        }
        public void BuyPower()
        {
            long price = getPrice();
            mContext.updateCoins(0);
            if(mContext.coins<price)
            {
                ShowInsufficientCoinsDialog();
            }
            else
            {
                writePowersToPreferences(allPowers[currentIndex],identifier,count);
                mContext.updateCoins(-price);
                TextView tvCoins = (TextView)mContext.findViewById(R.id.tvCoins);
                tvCoins.setText(String.valueOf(mContext.coins));
                ShowPurchaseSuccessDialog();
            }
        }

        public void CreatePowerDialog()
        {
            final AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            View v =  mContext.CurrentView;
            lp.width = v.getMeasuredWidth() - ConvertToPx(mContext, 50); //WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = v.getMeasuredHeight() - ConvertToPx(mContext, 100);//WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(lp);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View pwd=inflater.inflate(R.layout.dialog_pow_find, null, false);
            pwd.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            final GridView gridview = (GridView) pwd.findViewById(R.id.gdCards);
            int INFINITE_EQUIVALENT = 100;
            final PowerCardAdapter powers = new PowerCardAdapter(mContext,INFINITE_EQUIVALENT);
            gridview.setAdapter(powers);
            TextView tvTitle = (TextView)pwd.findViewById(R.id.tvTitle);
            tvTitle.setText("My Powers");

            dialog.setContentView(pwd);
            dialog.show();
        }

        public void setIdentifierVisibility(View v,int card)
        {
            switch (card)
            {
                case POW_SHUFFLE:
                case POW_FIND:
                    v.setVisibility(View.INVISIBLE);
                    tvRange.setText("∞");
                    break;
                default:
                    v.setVisibility(View.VISIBLE);
            }
        }

        public long getPrice()
        {
            long price;
            long basePrice;
            switch (allPowers[currentIndex])
            {
                case POW_EXTRA_MOVES:
                    basePrice = 140;
                    price = (long)Math.pow(2, identifier - minRange[currentIndex] )*basePrice*count;
                    break;
                case POW_FIND:
                    basePrice = 180;
                    price = basePrice*count;
                    break;
                case POW_SHUFFLE:
                    basePrice = 120;
                    price = basePrice*count;
                    break;
                case POW_SWAP:
                    basePrice = 90;
                    price = (long)Math.pow(2, identifier - minRange[currentIndex] )*basePrice*count;
                    break;
                case POW_REPLACE:
                    basePrice = 90;
                    price = (long)Math.pow(2,identifier - minRange[currentIndex])*basePrice*count;
                    break;
                case POW_DESTROY:
                    basePrice = 120;
                    price = (long)Math.pow(2,identifier - minRange[currentIndex])*basePrice*count;
                    break;
                case POW_PEEK:
                    basePrice = 200;
                    price = (long)Math.pow(2,identifier - minRange[currentIndex])*basePrice*count;
                    break;
                default:
                    basePrice=999999;
                    price = (long)Math.pow(2,identifier - minRange[currentIndex])*basePrice*count;
            }
            return (price);
        }

        public void ShowPurchaseSuccessDialog()
        {
            String msg = "'" + powerNames[currentIndex] + "' bought successfully.";
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("SUCCESS");
            alertDialog.setMessage(msg);
            alertDialog.setCancelable(true);
            alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }

        private void ShowInsufficientCoinsDialog()
        {
            String msg = "You do not have sufficient coins to buy this item.";
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Insufficient Coin Balance");
            alertDialog.setMessage(msg);
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("Get Coins", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    coinsClick();

                }
            });

            alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }

        public void writePowersToPreferences(int power,int range,int count)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String power_data = preferences.getString(String.valueOf(power), "");
            int length = preferences.getInt(String.valueOf(POWER_COUNT), 0);
            SharedPreferences.Editor editor = preferences.edit();
            String array_powerData[] = power_data.split(DELIMITER_2);
            String newPowerData = "";
            boolean updated_flag = false;

            for (String val : array_powerData)
            {
                if(val.equals(""))
                    break;

                String identifier_count[] = val.split(DELIMITER);
                int card_identifier = Integer.parseInt(identifier_count[0]);
                if(card_identifier == range)
                {
                    int card_count = Integer.parseInt(identifier_count[1]) + count;
                    newPowerData+=  String.valueOf(card_identifier)+ DELIMITER+String.valueOf(card_count) + DELIMITER_2;
                    updated_flag = true;
                }
                else
                {
                    newPowerData+=val+DELIMITER_2;
                }
            }
            if(!updated_flag)
            {
                newPowerData+=  String.valueOf(range)+ DELIMITER+String.valueOf(count) + DELIMITER_2;
                length++;
            }
            editor.putString(String.valueOf(power), newPowerData);
            editor.putInt(String.valueOf(POWER_COUNT), length);
            editor.apply();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnMyPower:
                    MyPowerClick();
                    break;
                case R.id.btn_prev_power:
                    previousPowerClick();
                    break;
                case R.id.btn_next_power:
                    nextPowerClick();
                    break;
                case R.id.btnIncreaseRange:
                    IncreaseRangeClick();
                    break;
                case R.id.btnDecreaseRange:
                    DecreaseRangeClick();
                    break;
                case R.id.btnIncreaseQuantity:
                    IncreaseQuantityClick();
                    break;
                case R.id.btnDecreaseQuantity:
                    DecreaseQuantityClick();
                    break;
                case R.id.btnBuy:
                    BuyPower();
                    break;
            }
        }
    }
}
