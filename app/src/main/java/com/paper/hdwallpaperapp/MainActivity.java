package com.paper.hdwallpaperapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterWallpaper adapterWallpaper;
    private BillingClient billingClient;
    private List<String> skuList;
    ArrayList<ModelWallpaper> modelWallpaperArrayList = new ArrayList<>();
    private RecyclerView.LayoutManager parentLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inAppPurchases();
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal1));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal2));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal3));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal4));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal5));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal6));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal7));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal8));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal11));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal14));
        modelWallpaperArrayList.add(new ModelWallpaper(R.drawable.wal15));


        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        parentLayoutManager = new LinearLayoutManager(MainActivity.this);
        adapterWallpaper = new AdapterWallpaper(modelWallpaperArrayList, MainActivity.this);
        recyclerView.setLayoutManager(parentLayoutManager);
        recyclerView.setAdapter(adapterWallpaper);
        adapterWallpaper.notifyDataSetChanged();
    }

    public void inAppPurchases() {
        // To be implemented in a later section.
        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getBaseContext());
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(MainActivity.this.getString(R.string.adsubscribed), Boolean.TRUE);
                    edit.apply();
                    Toast.makeText(MainActivity.this, "Thanks for purchase.", Toast.LENGTH_SHORT).show();
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

            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
        skuProductList();
    }

    public void btSubscribe(View view) {
        if (getPurchaseSharedPreference()) {
            Toast.makeText(MainActivity.this, "Already Subscribed", Toast.LENGTH_SHORT).show();
        }
        else {

            if (billingClient.isReady()) {
                MainActivity.this.skuQueryOnContinue();
            } else {
                Toast.makeText(MainActivity.this, "Something wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void skuQueryOnContinue() {
        try {
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            if (skuList.size() > 0) {

                    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // crashes due to null list of SkuDetails objects //
                            try {
                                assert list != null;
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(list.get(0))
                                        .build();
                                int responseCode = billingClient.launchBillingFlow(MainActivity.this, flowParams).getResponseCode();
                            } catch (Exception ignored) {
                            }
                        }
                    }
                });
            }
        } catch (Exception ignored) {
        }
    }
    public void skuProductList() {
        skuList = new ArrayList<>();
        skuList.add(getResources().getString(R.string.per_6_month_subcription));
    }
    public boolean getPurchaseSharedPreference() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getBoolean(this.getString(R.string.adsubscribed), false);
    }
}