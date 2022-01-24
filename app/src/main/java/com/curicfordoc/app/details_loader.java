package com.curicfordoc.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class details_loader extends AppCompatActivity {

    String userName, userId, login_method;
    TextView greetingtext;

    SharedPreferences HospDetailsSP;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_loader);

        greetingtext = (TextView) findViewById(R.id.greetingtext);
        HospDetailsSP = getSharedPreferences("HospDetails", MODE_PRIVATE);
        SharedPreferences.Editor HospDetailsSPEditor = HospDetailsSP.edit();

        Intent intent = getIntent();
        userName = intent.getExtras().getString("userName");
        String newUserName = "";
        for (int i = userName.length() - 1; i >= 0; i--) {
            if (userName.charAt(i) == ' ' && i != 0) {
                for (int j = 0; j < i; j++) {
                    newUserName += userName.charAt(j);
                }
                break;
            }
        }
        if (newUserName.length() == 0) {
            newUserName = userName;
        }
        greetingtext.setText("Hii" + "\n" + newUserName);

        login_method = intent.getExtras().getString("login_method");
        userId = intent.getExtras().getString("userId");


        db.collection("registered_doctors").document(login_method).collection(userId).document("hospital_details").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            HospDetailsSPEditor.putString("hospName", documentSnapshot.getString("hospital_name"));
                            HospDetailsSPEditor.putString("docName", documentSnapshot.getString("doctor_name"));
                            HospDetailsSPEditor.putString("docCategory", documentSnapshot.getString("category"));
                            HospDetailsSPEditor.putBoolean("isVerified", documentSnapshot.getBoolean("details_verified"));

                            Intent intent = new Intent(details_loader.this, homepage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(details_loader.this, clinic_details.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(details_loader.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });


        HospDetailsSPEditor.apply();

    }
}














