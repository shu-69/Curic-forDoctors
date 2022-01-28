package com.curicfordoc.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curicfordoc.app.database.DoctorsDatabaseHandler;
import com.curicfordoc.app.database.DocDetail;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class registration_complete extends AppCompatActivity {

    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_DATE = "userCreatedOn";
    private static final String KEY_USER_AVATAR = "userAvtar";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressBar progress;
    String login_method, userName, userId;
    Uri userAvtar;
    String userAv;
    String id;
    boolean check_details = false;

    SharedPreferences HospDetailsSP;
    SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_complete);

        progress = findViewById(R.id.progress);

        HospDetailsSP = getSharedPreferences("HospDetails", MODE_PRIVATE);
        editor = HospDetailsSP.edit();

        Intent intent = getIntent();

        login_method = intent.getExtras().getString("login_method");
        if (login_method.equals("phone")) {
            //userName = intent.getExtras().getString("userName");
            userId = intent.getExtras().getString("userPhone");

            editor.putString("userAvatar", "https://isobarscience.com/wp-content/uploads/2020/09/default-profile-picture1.jpg");

            db.collection("registered_doctors").document("phone_users").collection(userId).document("hospital_details").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot2) {
                            if (documentSnapshot2.exists()) {
                                // Checking if the hospital details is available
                                check_details = true;
                                editor.putString("hospName", documentSnapshot2.getString("hospital_name"));
                                editor.putString("ownerName", documentSnapshot2.getString("owner_name"));
                                editor.putBoolean("isVerified", documentSnapshot2.getBoolean("details_verified"));
                                editor.putString("city", documentSnapshot2.getString("city"));
                                editor.putString("state", documentSnapshot2.getString("state"));
                                editor.putString("country", documentSnapshot2.getString("country"));
                                editor.putString("ratingValue", documentSnapshot2.getString("ratings"));
                                timer t = new timer();
                                t.start();
                            } else {
                                // Open clinic_details activity;
                                check_details = false;
                                timer t = new timer();
                                t.start();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(registration_complete.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (login_method.equals("google")) {
            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (signInAccount != null) {
                userName = signInAccount.getDisplayName();
                userId = signInAccount.getEmail();
                userAvtar = signInAccount.getPhotoUrl();
                userAv = String.valueOf(userAvtar);
                id = signInAccount.getId();

                editor.putString("userAvatar", userAv);

                // TODO ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

                String finalUserName = userName;
                db.collection("registered_doctors").document("gmail_users").collection(userId).document("login_details").get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    db.collection("registered_doctors").document("gmail_users").collection(userId).document("hospital_details").get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                                    if (documentSnapshot2.exists()) {
                                                        check_details = true;
                                                        editor.putString("hospName", documentSnapshot2.getString("hospital_name"));
                                                        editor.putString("ownerName", documentSnapshot2.getString("owner_name"));
                                                        editor.putBoolean("isVerified", documentSnapshot2.getBoolean("details_verified"));
                                                        editor.putString("city", documentSnapshot2.getString("city"));
                                                        editor.putString("state", documentSnapshot2.getString("state"));
                                                        editor.putString("country", documentSnapshot2.getString("country"));
                                                        editor.putString("ratingValue", documentSnapshot2.getString("ratings"));

                                                        timer t = new timer();
                                                        t.start();
                                                    }else{
                                                        check_details = false;
                                                        timer t = new timer();
                                                        t.start();
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(registration_complete.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {

                                    SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                                    Date todayDate = new Date();
                                    String thisDate = currentDate.format(todayDate);

                                    Map<String, Object> note = new HashMap<>();
                                    note.put(KEY_USER_NAME, finalUserName);
                                    note.put(KEY_USER_EMAIL, userId);
                                    note.put(KEY_USER_ID, id);
                                    note.put(KEY_DATE, thisDate);
                                    note.put(KEY_USER_AVATAR, userAv);
                                    db.collection("registered_doctors").document("gmail_users").collection(userId).document("login_details").set(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    check_details = false;
                                                    timer t = new timer();
                                                    t.start();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(registration_complete.this, "Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
            }
        } else if (login_method.equals("facebook")) {
//
//            String userId = intent.getExtras().getString("userId");
//            userName = intent.getExtras().getString("userName");
//            userPhone = intent.getExtras().getString("userPhone");
//            String userAvtar = intent.getExtras().getString("userAvtar");
//            String userEmail = intent.getExtras().getString("userEmail");
//
//            Query checkUserExists = FirebaseDatabase.getInstance().getReference("facebook_users").orderByChild("userId").equalTo(userId);
//            String finalid = userId;
//            String finalUserName = userName;
//            String finalUserPhone = userPhone;
//            checkUserExists.addListenerForSingleValueEvent(new ValueEventListener() {
//                @SuppressLint("SetTextI18n")
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    final String Id = finalid;
//                    if ( snapshot.exists()){
//                        // User registered earlier, do nothing
//                        lottie_succes_check.setVisibility(View.INVISIBLE);
//                        done_txt.setVisibility(View.INVISIBLE);
//                        welcome_txt.setVisibility(View.VISIBLE);
//                    }else{
//                        // user not registered earlier
//                        rootNode = FirebaseDatabase.getInstance();
//                        reference = rootNode.getReference("facebook_users");
//
//                        String name = finalUserName;
//                        String age = null;
//                        String gender = null;
//                        String phoneNo = finalUserPhone;
//
//                        String email = userEmail;
//                        String password = null;
//                        String id = userId;
//                        userHelperClass helperClass = new userHelperClass(name, age, gender, phoneNo, email, password, id);
//                        try {
//                            reference.child(name).setValue(helperClass);
//                        }catch (Exception e) {
//                            Toast.makeText(getApplicationContext(), "Try another facebook account", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });

        }

    }

    public void saveTxt(String LoginMethod, String UserName, String UserId) {

            final String FILE_NAME = "data";
            try {
                editor.putString("userName", userName);
                editor.putString("userId", userId);
                editor.apply();
                FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fileOutputStream.write((LoginMethod + "\n").getBytes());
                fileOutputStream.write((UserName + "\n").getBytes());
                fileOutputStream.write((UserId + "\n").getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(HospDetailsSP.contains("hospName")){
                new userDetails(registration_complete.this);
                saveDoctorsDetails(userDetails.login_method, userDetails.userId);
            }

    }

    private void goclinic_details(View view) {
        Intent intent = new Intent(this, clinic_details.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// display the activity without animation   getWindow().setWindowAnimations(0)
        startActivity(intent);
    }

    private void saveDoctorsDetails(String login_method, String userId){
        new userDetails(registration_complete.this);
        db.collection("registered_doctors").document(login_method).collection(userId).document("doctors").collection("ids")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DoctorsDatabaseHandler DBHandler = new DoctorsDatabaseHandler(registration_complete.this);
                for (QueryDocumentSnapshot document : task.getResult()){
                    DocDetail DocDetail = new DocDetail();
                    DocDetail.setId(document.getString("docId"));
                    DocDetail.setName(document.getString("doctorName"));
                    DocDetail.setExperience(document.getString("experience"));
                    DocDetail.setFee(document.getString("fee"));
                    DocDetail.setImage(document.getString("profileImage"));
                    DocDetail.setSpecialization(document.getString("specialization"));

                    DBHandler.addDoctor(DocDetail);
                }
            }
        });
    }

    private void gohomescreen(View view) {
        Intent intent = new Intent(this, homepage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// display the activity without animation   getWindow().setWindowAnimations(0)
        startActivity(intent);
    }

    class timer extends Thread {
        public void run() {
            saveTxt(login_method, userName, userId);
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            if (check_details) {
                gohomescreen(null);
            } else {
                goclinic_details(null);
            }
            finish();
        }
    }
}