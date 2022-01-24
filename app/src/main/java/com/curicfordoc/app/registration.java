package com.curicfordoc.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class registration extends AppCompatActivity {

    EditText fnametxt, lnametxt, phonetxt, otptxt, passtxt, conpasstxt;
    ImageView google, fb, registerbut, otpright, otpwrong, passright, conpassright;
    TextView timerr, wrngPass, wrngconpass, registrationText;
    ProgressBar loading, sendingProgress;
    Button getotp;
    String recievedotp = null, login_method, usrPhone;
    static Boolean timeOver = true, passCheck = false;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private static final String TAG = "registration";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_PASSWORD = "userPassword";
    private static final String KEY_DATE = "userCreatedOn";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        initilization();

        createRequest();

        createRegisterText();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        passtxt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temppass = passtxt.getText().toString().trim();
                if ( !TextUtils.isEmpty(temppass) ){
                    if (temppass.length() < 5 ) {
                        wrngPass.setText("Password too short");
                        wrngPass.setVisibility(View.VISIBLE);
                        passright.setVisibility(View.INVISIBLE);
                        passCheck = false;
                    } else {
                        passright.setVisibility(View.VISIBLE);
                        wrngPass.setVisibility(View.INVISIBLE);
                        passCheck = true;
                    }
                }

            }
        });
        conpasstxt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempcpass = conpasstxt.getText().toString().trim();
                String temppass = passtxt.getText().toString().trim();
                if ( !TextUtils.isEmpty(tempcpass) && !TextUtils.isEmpty(temppass)){
                    if (temppass.equals(tempcpass)) {
                        conpassright.setVisibility(View.VISIBLE);
                        wrngconpass.setVisibility(View.INVISIBLE);
                        passCheck = true;
                    } else {
                        wrngconpass.setText("Password did't match");
                        wrngconpass.setVisibility(View.VISIBLE);
                        conpassright.setVisibility(View.INVISIBLE);
                        passCheck = false;
                    }
                }

            }
        });
        getotp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (timeOver) {
                    boolean validPhone = true;
                    String ph = phonetxt.getText().toString().trim();
                    try {
                        long tempPhone = Long.parseLong(phonetxt.getText().toString());
                        if ( ph.charAt(0) == '0' ){
                            ph = ph.substring(1);
                        }else if ( ph.charAt(0) == '+' && ph.charAt(1) == '9' && ph.charAt(2) == '1' ){
                            ph = ph.substring(3);
                        }
                        if (ph.length() != 10) {
                            validPhone = false;
                        }
                    } catch (NumberFormatException nfe) {
                        validPhone = false;
                    }

                    if (!ph.isEmpty()) {
                        if (validPhone) {
                            sendingProgress.setVisibility(View.VISIBLE);
                            getotp.setVisibility(View.INVISIBLE);

                            String finalPh = ph;
                            db.collection("registered_doctors").document("phone_users").collection("+91"+ph).document("login_details").get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()){
                                                Toast.makeText(registration.this, "User already exists", Toast.LENGTH_SHORT).show();
                                                sendingProgress.setVisibility(View.INVISIBLE);
                                                getotp.setVisibility(View.VISIBLE);
                                            }else{

                                                //user not registered earlier, so otp will be sent
                                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                        "+91" + finalPh,
                                                        60,
                                                        TimeUnit.SECONDS,
                                                        registration.this,
                                                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                            @Override
                                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                                            }

                                                            @Override
                                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                sendingProgress.setVisibility(View.INVISIBLE);
                                                                getotp.setVisibility(View.VISIBLE);
                                                            }

                                                            @Override
                                                            public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                                recievedotp = backendotp;
                                                                sendingProgress.setVisibility(View.INVISIBLE);
                                                                if (TextUtils.isEmpty(recievedotp)) {
                                                                    Toast.makeText(getApplicationContext(), "Please check your Internet", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    recievedotp = backendotp;

                                                                    timerr.setVisibility(View.VISIBLE);
                                                                    timerOtp t = new timerOtp();
                                                                    t.start();
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(registration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            }}}


            }
        });
        registerbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean check = passCheck;
                String name = fnametxt.getText().toString().trim() + " " + lnametxt.getText().toString().trim();
                String phoneno = phonetxt.getText().toString().trim();
                String opt = otptxt.getText().toString().trim();
                String pass = passtxt.getText().toString().trim();
                String conpass = conpasstxt.getText().toString().trim();

                if (TextUtils.isEmpty(phoneno)){
                    // Phone number empty message
                    check = false;
                }
                if (TextUtils.isEmpty(pass)){
                    // Password empty message
                    check = false;
                }
                if (TextUtils.isEmpty(conpass)){
                    // Confirm Password is empty message
                    check = false;
                }

                if (check){

                        // Now save all the users details in a txt file and save the file in the applications directory.
                        loading.setVisibility(View.VISIBLE);
                        String userotp = otptxt.getText().toString().trim();
                        if (recievedotp != null) {
                            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                    recievedotp, userotp
                            );
                            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        login_method = "phone";
                                        submitDetails(null); // Submit the user details in firebase
                                        //save(null);  // Stores the user data in internal Storage
                                        //  Toast.makeText(getApplicationContext(), "UserSaved", Toast.LENGTH_SHORT).show();
                                    } else {
                                        loading.setVisibility(View.INVISIBLE);
                                        // Otp wrong message
                                        otpwrong.setVisibility(View.VISIBLE);
                                        otpright.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        } else {
                            loading.setVisibility(View.INVISIBLE);
                            Toast.makeText(registration.this, "Please verify your phone", Toast.LENGTH_SHORT).show();
//                            otpwarning.setText("Please verify your Phone number");
//                            otpwarning.setVisibility(View.VISIBLE);
//                            removeWarning rw = new removeWarning();
//                            rw.start();
                            // Display internet error message.
                        }
                        /**

                         folderName = "Curer";
                         if (ContextCompat.checkSelfPermission(registrationScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                         createDirectory(folderName);
                         }else{
                         askPermission();
                         }
                         // If permission is granted.
                         if ( permissionGranted ){

                         submitDetails(null);
                         Toast.makeText(getApplicationContext(), "UserSaved", Toast.LENGTH_SHORT).show();

                         }else{
                         // If permission denied
                         //System.exit(0);
                         }
                         try {
                         BufferedWriter bw = new BufferedWriter(new FileWriter("userData.txt"));
                         // bw.write("userName : " + userName + "\n" + "userAge : " + userAge + "\n" + "userGender : " + userGender + "\n" +
                         //                            "userPhoneNo : " + userPhoneNo + "\n" + "userEmail : " + userEmail + "\n" );
                         bw.close();
                         Toast.makeText(getApplicationContext(), "UserSaved", Toast.LENGTH_SHORT).show();

                         }catch (Exception e){
                         Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                         }
                         */
                    }
                }

        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                signIn();
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registration.this, empty_facebook_login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    private void initilization() {
        fnametxt = (EditText) findViewById(R.id.fnametxt);
        lnametxt = (EditText) findViewById(R.id.lnametxt);
        phonetxt = (EditText) findViewById(R.id.phonetxt);
        otptxt = (EditText) findViewById(R.id.otptxt);
        passtxt = (EditText) findViewById(R.id.passtxt);
        conpasstxt = (EditText) findViewById(R.id.conpasstxt);
        google = (ImageView) findViewById(R.id.google);
        fb = (ImageView) findViewById(R.id.fb);
        registerbut = (ImageView) findViewById(R.id.regbut);
        otpright = (ImageView) findViewById(R.id.otpright);
        otpwrong = (ImageView) findViewById(R.id.otpwrong);
        passright = (ImageView) findViewById(R.id.passright);
        conpassright = (ImageView) findViewById(R.id.conpassright);
        loading = (ProgressBar) findViewById(R.id.loading_bar);
        sendingProgress = (ProgressBar) findViewById(R.id.sendingotp);
        getotp = (Button) findViewById(R.id.getotp);
        timerr = (TextView) findViewById(R.id.timer);
        wrngPass = (TextView) findViewById(R.id.wrngPass);
        wrngconpass = (TextView) findViewById(R.id.wrngConpass);
        registrationText = findViewById(R.id.RegistrationText);
    }

    private void submitDetails(View view){
        String name = fnametxt.getText().toString().trim() + " " + lnametxt.getText().toString().trim();
        String phoneNo = phonetxt.getText().toString().trim();
        if ( phoneNo.charAt(0) == '0' ){
            phoneNo = phoneNo.substring(1);
        }else if ( phoneNo.charAt(0) == '+' && phoneNo.charAt(1) == '9' && phoneNo.charAt(2) == '1' ){
            phoneNo = phoneNo.substring(3);
        }
        phoneNo = "+91" + phoneNo;
        usrPhone = phoneNo;
        String pass = passtxt.getText().toString().trim();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_USER_NAME, name);
        note.put(KEY_USER_PHONE, phoneNo);
        note.put(KEY_USER_PASSWORD, pass);
        note.put(KEY_DATE, thisDate);

        db.collection("registered_doctors").document("phone_users").collection(phoneNo).document("login_details").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        goregCompScreen(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(registration.this, "Please try again", Toast.LENGTH_SHORT).show();
                System.out.println(e.getMessage());
            }
        });

    }

    private void createRequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            login_method = "google";
                            loading.setVisibility(View.INVISIBLE);
                            goregCompScreen(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                            loading.setVisibility(View.INVISIBLE);
                            Toast.makeText( registration.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goregCompScreen(View view) {
        Intent intent = new Intent( this, registration_complete.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// display the activity without animation   getWindow().setWindowAnimations(0)
//        intent.putExtra("userName" , usrName);
        intent.putExtra("userPhone" , usrPhone);
        intent.putExtra("login_method", login_method);
        startActivity(intent);
        finish();

    }


    class timerOtp extends Thread {
        @SuppressLint("SetTextI18n")
        public void run() {
            timeOver = false;
            String ctime = null;
            int min = 1, sec = 00;
            for (; ; ) {
                if (min == 0 && sec == 0) {
                    break;
                }
                if (String.valueOf(sec).length() == 1) {
                    timerr.setText("0" + min + " : " + "0" + sec);
                } else {
                    timerr.setText("0" + min + " : " + sec);
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                if (sec == 0) {
                    min--;
                    sec = 59;
                }
                sec--;
            }
            timerr.setVisibility(View.INVISIBLE);
            timeOver = true;
            try {
                getotp.setVisibility(View.VISIBLE);
            }catch (Exception e){ try{ getotp.setVisibility(View.VISIBLE);} catch (Exception ee) {}}

        }
    }

    private void createRegisterText() {
        String text = getString(R.string.RegisterText);

        SpannableString ss = new SpannableString(text);

//        ForegroundColorSpan appGreen1 = new ForegroundColorSpan(getColor(R.color.appdefault));
//        ForegroundColorSpan appGreen2 = new ForegroundColorSpan(getColor(R.color.appdefault));
//
//        ss.setSpan(appGreen1, 71, 83, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new UnderlineSpan(), 71, 83, 0);
//        ss.setSpan(appGreen2, 86, 100, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new UnderlineSpan(), 86, 100, 0);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(registration.this, WebView.class);
                intent.putExtra("url", getString(R.string.CuricforDocTermsOfUse));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.appdefault));
                ds.setUnderlineText(true);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(registration.this, WebView.class);
                intent.putExtra("url", getString(R.string.CuricforDocPrivacyPolicy));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.appdefault));
                ds.setUnderlineText(true);
            }
        };

        ss.setSpan(clickableSpan1, 74, 86, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 89, 103, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        registrationText.setText(ss);
        registrationText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}



















