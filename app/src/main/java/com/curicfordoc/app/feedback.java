package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class feedback extends AppCompatActivity {

    LottieAnimationView AngryEmj, SadEmj, ConfusedEmj, HappyEmj, LovedEmj;
    TextView TitleTextV;
    EditText MessageBox;
    CardView submitButton;

    Dialog dialog;

    private ReviewInfo reviewInfo;
    private ReviewManager manager;

    private AdView mAdView;
    private FrameLayout adFrame;

    byte RatingStars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        hook();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = new AdView(this);
        mAdView.setAdUnitId(getString(R.string.ad_unit_id));
        adFrame.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                //mAdView.loadAd(adRequest);

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });

        dialog = new Dialog(this);

        setSmallAllEmojis();

        findViewById(R.id.backCardV).setOnClickListener(v -> finish());
        AngryEmj.setOnClickListener(v -> {
            setSmallAllEmojis();
            AngryEmj.resumeAnimation();
            AngryEmj.setPadding(0,0,0,0);
            TitleTextV.setText("Tell us how we can improve:");
            RatingStars = 1;
        });
        SadEmj.setOnClickListener(v -> {
            setSmallAllEmojis();
            SadEmj.resumeAnimation();
            SadEmj.setPadding(0,0,0,0);
            TitleTextV.setText("Tell us how we can improve:");
            RatingStars = 2;
        });
        ConfusedEmj.setOnClickListener( v -> {
            setSmallAllEmojis();
            ConfusedEmj.resumeAnimation();
            ConfusedEmj.setPadding(0,0,0,0);
            TitleTextV.setText("Tell us how we can improve:");
            RatingStars = 3;
        });
        HappyEmj.setOnClickListener( v -> {
            setSmallAllEmojis();
            HappyEmj.resumeAnimation();
            HappyEmj.setPadding(0,0,0,0);
            TitleTextV.setText("Tell us what you liked:");
            RatingStars = 4;
        });
        LovedEmj.setOnClickListener( v -> {
            setSmallAllEmojis();
            LovedEmj.resumeAnimation();
            LovedEmj.setPadding(0,0,0,0);
            TitleTextV.setText("Tell us what you loved:");
            RatingStars = 5;
        });

        submitButton.setOnClickListener(v -> {
            if (submitRating(RatingStars))
                openRateUsDialog();
        });
    }

    private void hook() {
        AngryEmj = findViewById(R.id.emj_angry);
        SadEmj = findViewById(R.id.emj_sad);
        ConfusedEmj = findViewById(R.id.emj_confused);
        HappyEmj = findViewById(R.id.emj_happy);
        LovedEmj = findViewById(R.id.emj_loved);

        TitleTextV = findViewById(R.id.titleTextV);
        MessageBox = findViewById(R.id.messageBox);
        submitButton = findViewById(R.id.submitButton);

        adFrame = findViewById(R.id.adFrameLayout);

    }

    private void setSmallAllEmojis() {
        AngryEmj.setPadding(0,30,0,30);
        SadEmj.setPadding(0,30,0,30);
        ConfusedEmj.setPadding(0,30,0,30);
        HappyEmj.setPadding(0,30,0,30);
        LovedEmj.setPadding(0,30,0,30);
    }

    private boolean submitRating(byte Rating){
        if (Rating == 0)
            return false;
        String MessageBody;
        if(!MessageBox.getText().toString().trim().isEmpty()){
            MessageBody = "Rating = " + Rating + "/5" + "\n" + "Message = " + MessageBox.getText().toString().trim();
        }else{
            MessageBody = "Rating = " + Rating + "/5" ;
        }

        return true;
    }

    private void startReviewFlow(){
        if(reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener((task -> {
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
            }));
        }
    }

    private void activateReviewInfo(){
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> managerInfoTask = manager.requestReviewFlow();
        managerInfoTask.addOnCompleteListener((task) -> {
            if(task.isSuccessful()){
                reviewInfo = task.getResult();
            }else{
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openRateUsDialog(){
        dialog.setContentView(R.layout.rate_us_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.closeCard).setOnClickListener( v -> dialog.dismiss());


        dialog.findViewById(R.id.rateNowCard).setOnClickListener(v -> {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.playstore_link)));
            startActivity(intent);
        });

        dialog.show();
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}