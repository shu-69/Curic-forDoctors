package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class helpAndSupport extends AppCompatActivity {

    CardView backButton, chatCard, emailCard, requestCallCard, callUsCard;
    ImageView cfordBanner;

    private String SPFileName = "HelpCenter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        hook();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        emailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(helpAndSupport.this, emailSupport.class);
                startActivity(intent);
            }
        });
        requestCallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(helpAndSupport.this, callBackSupport.class);
                startActivity(intent);
            }
        });
        callUsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(SPFileName, MODE_PRIVATE);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + sharedPreferences.getString("phone", "+917050901295")));
                startActivity(intent);
            }
        });

    }

    private void hook() {
        backButton = (CardView) findViewById(R.id.backCardV);
        chatCard = (CardView) findViewById(R.id.chatCard);
        emailCard = (CardView) findViewById(R.id.emailCard);
        requestCallCard = (CardView) findViewById(R.id.requestcallCard);
        callUsCard = (CardView) findViewById(R.id.callusCard);
        cfordBanner = (ImageView) findViewById(R.id.cfordBanner);
    }
}