package com.staraj.quiza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.staraj.quiza.databinding.ActivityResultBinding;
import com.staraj.quiza.databinding.ActivityVideoBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoActivity extends AppCompatActivity {
    ActivityVideoBinding binding;

    private Button clicks;
    private TextView coins;
    private RewardedAd mRewardedAd;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        clicks = findViewById(R.id.ShowAds);
        coins = findViewById(R.id.points);
        rewads();
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.Banner3.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);


        clicks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    Activity activityContext = VideoActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.

                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                            String rew =rewardType+ " Sccussfully Added to Your Wallet ";
                            coins.setText(rew);
                            database.collection("users")
                                    .document(FirebaseAuth.getInstance().getUid())
                                    .update("coins", FieldValue.increment(rewardAmount));
                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    rewads();
                                    mRewardedAd = null;
                                    super.onAdDismissedFullScreenContent();
                                }
                            });
                        }
                    });
                } else {

                    Toast.makeText(VideoActivity.this, "Ads not loaded \n Try Again", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }
    public void rewads(){
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, getString(R.string.rewarded_ads),
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("TAG", loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                    }
                });
    }
}