package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class callBackSupport extends AppCompatActivity {

    Spinner languageSpinner;
    EditText phoneEditText;
    CardView requestCard, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_back_support);

        languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        phoneEditText = (EditText) findViewById(R.id.editTextPhone);
        requestCard = (CardView) findViewById(R.id.requestButton);
        back = (CardView) findViewById(R.id.backCardV);

        String[] langArray = new String[]{
                "English", "Hindi"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, langArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        requestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    SharedPreferences preferences = getSharedPreferences("HelpCenter", MODE_PRIVATE);
                    String number = preferences.getString("phone", "+917050901295");
                    String userId = "";
                    if (userDetails.login_method.equals("phone"))
                        userId = userDetails.userId;
                    else if (userDetails.login_method.equals("google"))
                        userId = userDetails.userId;
                    else if (userDetails.login_method.equals("facebook")) {
                        // TODO : put facebook id
                    }
                    if (phoneEditText.getText().toString().trim().length() == 10 || phoneEditText.getText().toString().trim().length() == 13) {
                        String msg = userDetails.userName + " is requesting for a Call back. \n" + "userId : " + userId + "\n" +
                                "Language : " + languageSpinner.getSelectedItem().toString() + "\n"
                                + "Contact No. : " + phoneEditText.getText().toString().trim();
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null, msg, null, null);
                            Toast.makeText(getApplicationContext(), "Our Customer support executive will reach you soon.", Toast.LENGTH_LONG).show();
                            requestCard.setEnabled(false);
                        } catch (Exception e) {
                            System.out.println(e.getLocalizedMessage());
                            Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(callBackSupport.this, "Please Login to use this service", Toast.LENGTH_SHORT).show();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}