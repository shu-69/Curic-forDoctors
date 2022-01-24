package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class verification extends AppCompatActivity {

    CardView done_butt;
    SharedPreferences HospDetails;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        done_butt = findViewById(R.id.doneButt);

        HospDetails = getSharedPreferences("HospDetails", MODE_PRIVATE);
        editor = HospDetails.edit();

        editor.putBoolean("isVerified", false);
        editor.apply();

        done_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(verification.this, homepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }
}