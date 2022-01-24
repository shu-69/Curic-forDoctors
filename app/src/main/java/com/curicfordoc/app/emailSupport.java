package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class emailSupport extends AppCompatActivity {

    EditText subject, messageBody;
    Button sendButton;
    CardView back;

    private String SPFileName= "HelpCenter";
    private String KEY = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_support);

        subject = (EditText) findViewById(R.id.subjectText);
        messageBody = (EditText) findViewById(R.id.messageBody);
        sendButton = (Button) findViewById(R.id.sendButton);
        back = (CardView) findViewById(R.id.backCardV);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                String[] MailId = {getHelpCenterEmail("HelpCenter", "email")};
                intent.putExtra(Intent.EXTRA_EMAIL, MailId);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString().trim());
                intent.putExtra(Intent.EXTRA_TEXT, messageBody.getText().toString().trim());

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String getHelpCenterEmail(String SPFileName, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(SPFileName, MODE_PRIVATE);

        return sharedPreferences.getString(key, "curicind@gmail.com");
    }
}