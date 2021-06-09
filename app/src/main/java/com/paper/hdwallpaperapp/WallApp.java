package com.paper.hdwallpaperapp;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

public class WallApp  extends Application {
    private BillingClient billingClient;

    @Override
    public void onCreate() {
        super.onCreate();


        // Logging set to help debug issues, remove before releasing your app.


        inAppPurchases();
    }
    public void inAppPurchases() {
        // To be implemented in a later section.

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WallApp.this.getBaseContext());
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(WallApp.this.getString(R.string.adsubscribed), Boolean.TRUE);
                    edit.apply();
                }
            }
        };

        ///create client billing
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        //make connect to google play store
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    boolean value = checkUserSubscription();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(getString(R.string.adsubscribed), value);
                    edit.apply();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }
    private boolean checkUserSubscription() {
        assert billingClient != null;
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        List<Purchase> purchase = purchasesResult.getPurchasesList();
        if (purchase != null) {
            return  true;
        }
        return false;
    }
}
