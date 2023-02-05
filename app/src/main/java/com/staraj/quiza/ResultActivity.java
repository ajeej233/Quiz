 package com.staraj.quiza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.staraj.quiza.databinding.ActivityResultBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultActivity extends AppCompatActivity {
    ActivityResultBinding binding;
    double POINTS = 5;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.banner.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);




        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);
        double points = correctAnswers * POINTS   ;
        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"share demo");
                String shareMessage="https://play.google.com/store/apops/details?="+BuildConfig.APPLICATION_ID+"\n\n";
                startActivity(Intent.createChooser(intent,"Share By"));
            }
        });

        binding.score.setText(String.format("%d/%d",correctAnswers,totalQuestions));
        binding.earnedCoins.setText(String.valueOf(points));

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("coins", FieldValue.increment(points));

        setAds();


        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
                if (mInterstitialAd != null){
                    mInterstitialAd.show(ResultActivity.this);
                    setAds();

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            setAds();

                        }
                    });
                }else{
                    startActivity(new Intent(ResultActivity.this, MainActivity.class));

                }


            }
        });



    }
    public void onBackPressed(){

        Intent intent = new Intent(ResultActivity.this,MainActivity.class);
        startActivity(intent);
       finishAffinity();
        if (mInterstitialAd != null){
            mInterstitialAd.show(ResultActivity.this);
            setAds();

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    setAds();

                }
            });
        }else{
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    public void setAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.Inter_ads), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("TAG", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });
    }




}