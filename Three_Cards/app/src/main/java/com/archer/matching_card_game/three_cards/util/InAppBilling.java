package com.archer.matching_card_game.three_cards.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.archer.matching_card_game.three_cards.MainActivity;
import com.archer.matching_card_game.three_cards.R;

import java.util.ArrayList;
import java.util.List;

import static com.archer.matching_card_game.three_cards.HelperClass.AD_FREE_VERSION_HASH_MAP;
import static com.archer.matching_card_game.three_cards.HelperClass.AD_FREE_VERSION_MAP_KEY;

/**
 * Middle layer between main activity & util helper classes for In App Billing Logic
 */
public class InAppBilling {

    MainActivity mContext;
    public IabHelper mHelper;
    String SKU_POUCH_OF_COINS = "sku_pouch_of_coins";
    String SKU_BAG_OF_COINS = "sku_bag_of_coins";
    String SKU_TRUNK_OF_COINS = "sku_trunk_of_coins";
    String SKU_REMOVE_ADS = "sku_remove_ads";
    public long POUCH_OF_COINS_VALUE = 400;
    public long BAG_OF_COINS_VALUE = 900;
    public long TRUNK_OF_COINS_VALUE = 2000;
    String RemoveAdsPrice;
    String CoinPouchPrice;
    String CoinBagPrice;
    String CoinTrunkPrice;
    Purchase PendingConsumption[]={null,null,null};

    boolean connectedToInAppBillingServices;

    public InAppBilling(MainActivity m_context) {
        mContext = m_context;

        RemoveAdsPrice=CoinBagPrice=CoinPouchPrice=CoinTrunkPrice="";
    }

    public void initializeInAppBilling() {
        String base64EncodedPublicKey ;

        // compute your public key and store it in base64EncodedPublicKey
        base64EncodedPublicKey = getPublicKey();

        mHelper = new IabHelper(mContext, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                } else {
                    connectedToInAppBillingServices = true;

                    mHelper.m_Context = mContext;
                    //check for pending consumptions
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            }
        });
    }

    //region Check For pending items to consume
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            try
            {
                if (result.isFailure()) {
                    // handle error here
                }
                else {
                    Purchase purchase;
                    //consume pending purchases
                    purchase = inventory.getPurchase(SKU_BAG_OF_COINS);
                    if (purchase != null && verifyDeveloperPayload(purchase)) {
                        PendingConsumption[0]=purchase;
                    }
                    purchase = inventory.getPurchase(SKU_POUCH_OF_COINS);
                    if (purchase != null && verifyDeveloperPayload(purchase)) {
                        PendingConsumption[1]=purchase;
                    }
                    purchase = inventory.getPurchase(SKU_TRUNK_OF_COINS);
                    if (purchase != null && verifyDeveloperPayload(purchase)) {
                        PendingConsumption[2]=purchase;
                    }
                    if(!mContext.adFreeVersion)
                    {
                        purchase = inventory.getPurchase(SKU_REMOVE_ADS);
                        if (purchase != null && verifyDeveloperPayload(purchase)) {
                            mContext.adFreeVersion = true;
                            SharedPreferences.Editor editor = mContext.getSharedPreferences(String.valueOf(AD_FREE_VERSION_HASH_MAP),
                                    Context.MODE_PRIVATE).edit();
                            editor.putBoolean(String.valueOf(AD_FREE_VERSION_MAP_KEY),true);
                            editor.apply();
                        }
                    }
                    consumePendingItemsOneByOne();
                }
            }
            catch (Exception ex)
            {
                 Toast.makeText(mContext,
                         "Error : Could not consume purchased item." + "\n" +
                         "\n" + "Please : " +
                         "\n" + "1. Restart 3 cards" +
                         "\n" + "2. Check internet connection" +
                         "\n" + "3. Contact us if issue persists",
                         Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion

    private boolean consumePendingItemsOneByOne()
    {
        boolean noItemsToConsume = true;
        if(PendingConsumption[0]!=null)
        {
            mHelper.consumeAsync(PendingConsumption[0],
                    mConsumeFinishedListener);
            PendingConsumption[0]=null;
            noItemsToConsume=false;
        }else if(PendingConsumption[1]!=null)
        {
            mHelper.consumeAsync(PendingConsumption[1],
                    mConsumeFinishedListener);
            PendingConsumption[1]=null;
            noItemsToConsume=false;
        }else if(PendingConsumption[2]!=null)
        {
            mHelper.consumeAsync(PendingConsumption[2],
                    mConsumeFinishedListener);
            PendingConsumption[2]=null;
            noItemsToConsume=false;
        }

        return noItemsToConsume;
    }

    //region update data after consumption
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    try
                    {
                        mHelper.flagEndAsync();
                        if (result.isSuccess())
                        {
                            if (purchase.getSku().equals(SKU_POUCH_OF_COINS)) {
                                mContext.updateCoins(POUCH_OF_COINS_VALUE);
                            }
                            else if (purchase.getSku().equals(SKU_BAG_OF_COINS)) {
                                mContext.updateCoins(BAG_OF_COINS_VALUE);
                            }else if (purchase.getSku().equals(SKU_TRUNK_OF_COINS)) {
                                mContext.updateCoins(TRUNK_OF_COINS_VALUE);
                            }

                            try {
                                View tvCoins = mContext.findViewById(R.id.tvCoins);
                                if(tvCoins!=null)
                                {
                                    ((TextView)tvCoins).setText(String.valueOf(mContext.coins));
                                }
                            } catch (Exception ex) {/* do nothing */}
                        }

                        boolean noAsyncConsumptionInProgress = consumePendingItemsOneByOne();
                        if(noAsyncConsumptionInProgress && CoinTrunkPrice.equals(""))
                            getPriceInLocalCurrency();
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(mContext,"Oops. I am unknown error 1",Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            };
    //endregion

    //region get price in local currency
    public void getPriceInLocalCurrency() {

        if (!connectedToInAppBillingServices)
            return;

        IabHelper.QueryInventoryFinishedListener
                mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    // handle error
                    return;
                }

                try {
                    CoinPouchPrice = inventory.getSkuDetails(SKU_POUCH_OF_COINS).getPrice();
                    CoinBagPrice = inventory.getSkuDetails(SKU_BAG_OF_COINS).getPrice();
                    CoinTrunkPrice = inventory.getSkuDetails(SKU_TRUNK_OF_COINS).getPrice();
                    RemoveAdsPrice = inventory.getSkuDetails(SKU_REMOVE_ADS).getPrice();
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        List<String> additionalSkuList = new ArrayList<>();
        additionalSkuList.add(SKU_BAG_OF_COINS);
        additionalSkuList.add(SKU_POUCH_OF_COINS);
        additionalSkuList.add(SKU_TRUNK_OF_COINS);
        additionalSkuList.add(SKU_REMOVE_ADS);
        mHelper.queryInventoryAsync(true, additionalSkuList,
                mQueryFinishedListener);
    }
    //endregion


    public void LaunchPurchaseFlow(String SKU_ID)
    {
        if(!connectedToInAppBillingServices)
        {
            Toast.makeText(mContext,"Oops. Connection failed.\nPlease try after sometime.",Toast.LENGTH_SHORT).show();
            return;
        }

        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase)
            {
                try {
                    mHelper.flagEndAsync();
                if (result.isFailure()) {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                    Toast.makeText(mContext,"Purchased Failed\nPlease try after sometime",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (purchase.getSku().equals(SKU_REMOVE_ADS))
                {
                    mContext.adFreeVersion = true;
                    SharedPreferences.Editor editor = mContext.getSharedPreferences(String.valueOf(AD_FREE_VERSION_HASH_MAP),
                            Context.MODE_PRIVATE).edit();
                    editor.putBoolean(String.valueOf(AD_FREE_VERSION_MAP_KEY),true);
                    editor.apply();

                    Toast.makeText(mContext,"Application upgraded successfully." +
                    "\nPlease give us a moment to refresh things!",Toast.LENGTH_SHORT).show();
                }
                else //Consume other items
                {
                    mHelper.consumeAsync(purchase,mConsumeFinishedListener);

                    Toast.makeText(mContext,"Purchase successful." +
                            "\nPlease give us a moment to refresh things!",Toast.LENGTH_SHORT).show();
                }
                }
                catch (Exception ex)
                {
                    Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        };

        String code = "2_Cards";
        int transactionID = code.hashCode();
        mHelper.launchPurchaseFlow(mContext, SKU_ID, transactionID,
                mPurchaseFinishedListener, jumble("69"));
    }

    public void setCoinPrice()
    {
        try {
            if(!CoinPouchPrice.equals(""))
                ((TextView)mContext.findViewById(R.id.tvPouchPrice)).setText("● At " + CoinPouchPrice);
            if(!CoinBagPrice.equals(""))
                ((TextView)mContext.findViewById(R.id.tvBagPrice)).setText("● At " + CoinBagPrice);
            if(!CoinTrunkPrice.equals(""))
                ((TextView)mContext.findViewById(R.id.tvTrunkPrice)).setText("● At " + CoinTrunkPrice);
        }
        catch (Exception ex){ /* Do Nothing */ }
    }

    public void setRemoveAdsPrice(View v)
    {
        try {
            if(!RemoveAdsPrice.equals(""))
                ((TextView)v.findViewById(R.id.tvRemoveAdsPrice)).setText("At " + RemoveAdsPrice);
        }
        catch (Exception ex){ /* Do Nothing */ }
    }


    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {

        String payload = p.getDeveloperPayload();
        return payload.trim().equals(jumble("619"));
    }

    public String jumble(String x)
    {
        String a,b,c,d;
        a="SwaS"; b="RiShi_NeVeR"; c="GiVe"; d="Up";
        x= a+"."+b+"."+c+"."+d;
        char y[] = x.toCharArray();
        int []xx = {3,9,5,7,2,11,3,9,5,7,2,11,};
        int l1 = xx.length;
        int l2 = y.length;
        for (int xxx : xx) {
            int index = xxx;
            for (int j = 0; j < l2; j++) {
                char yy = y[j];
                y[j] = y[index % l2];
                y[index % l2] = yy;
                index += xxx;
            }
        }
        x=String.valueOf(y);
        return x.trim();
    }

    public String getPublicKey()
    {
        String Key[]=
                {
                        "vAqmAEQACKgCBIIMA8QACOAAFEQAB0w9GikhqkgBNAjIBIIM",
                        "4VvH9JFbZnltqywo9YEXc/HzFGZRdyOwc3CZbTP0v",
                        "QCDd4yj+qnY3UebsdYQzQXjSLw4pU2nYCyT4sOQ32",
                        "YCy0DlDAsYWkeYcNZiD7AOXxnneeXeIINyQDxu3O",
                        "/A3ZqrbrkvPK65XvlxQazaq07bAjcr1mprtYAfnlX",
                        "QsFJ0eCRC9b72HubFfsUdG8bD3LxPtHbg3GdAlnty",
                        "DxjxYWq7rXL9r0ZD36BdIOlAqZGcaA0yHwOi7jfiz9Bwhi",
                        "1rve69oj5YiJ3qHAZ5YV5GrHqMxxOG7fUgstbW1OiFtiB",
                        "cSo3n0q3x46Jvd5AqYcDH17sfUXDt9IQNbe",
                        "AT5Mh4kwIDAQAB"
                };

        String final_result="";
        int length= Key.length;
        for(int i=0;i<length;i++)
        {
            if(i%2==0)
                final_result+= new StringBuilder(Key[i]).reverse().toString();
            else
                final_result+= Key[i];
        }

        return final_result.trim();
    }

}
