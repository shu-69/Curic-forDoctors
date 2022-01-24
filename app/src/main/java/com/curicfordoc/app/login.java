package com.curicfordoc.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {

    EditText username, userpass;
    TextView loginText;
    ImageView loginbut, regbut, google , fb;
    ProgressBar loading;
    String login_method, usrPhone;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initilization();

        createRequest();

        createLoginText();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        buttonsListners();

    }

    private void initilization() {
        loginbut = findViewById(R.id.loginbutt);
        regbut = findViewById(R.id.regbutt);
        google = findViewById(R.id.google);
        fb = findViewById(R.id.fb);
        username = findViewById(R.id.usernametxt);
        userpass = findViewById(R.id.userpasstxt);
        loading = findViewById(R.id.progressbar);
        loginText = findViewById(R.id.loginText);
    }

    private void buttonsListners() {

        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = username.getText().toString().trim();
                String userPass = userpass.getText().toString().trim();
                Boolean validPhone = true;

                if (TextUtils.isEmpty(userName)){
                    validPhone = false;
                }
                try {
                    long tempPhone = Long.parseLong(username.getText().toString());
                    if ( userName.charAt(0) == '0' ){
                        userName = userName.substring(1);
                    }else if ( userName.charAt(0) == '+' && userName.charAt(1) == '9' && userName.charAt(2) == '1' ){
                        userName = userName.substring(3);
                    }
                    userName = "+91" + userName;
                } catch (NumberFormatException nfe) {
                    validPhone = false;
                }

                if (validPhone){
                    loading.setVisibility(View.VISIBLE);
                    String finalUserName = userName;
                    db.collection("registered_doctors").document("phone_users").collection(userName).document("login_details").get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        String tempusername = documentSnapshot.getString("userPhone");
                                        String tempuserpass = documentSnapshot.getString("userPassword");
                                        if ( finalUserName.equals(tempusername) ){
                                            if ( userPass.equals(tempuserpass) ){
                                                login_method = "phone";
                                                usrPhone = tempusername;
                                                loading.setVisibility(View.INVISIBLE);
                                                goregCompScreen(null);
                                            }else{
                                                Toast.makeText(login.this, "Password doesn't matched", Toast.LENGTH_SHORT).show();
                                                loading.setVisibility(View.INVISIBLE);
                                            }
                                        }else{
                                            Toast.makeText(login.this, "User doesn't exists", Toast.LENGTH_SHORT).show();
                                            loading.setVisibility(View.INVISIBLE);
                                        }
                                    }else {
                                        Toast.makeText(login.this, "User doesn't exists", Toast.LENGTH_SHORT).show();
                                        loading.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }
        });

        regbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                signIn();
            }
        });
    }

    private void createRequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_forGmailLogin))
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
                loading.setVisibility(View.INVISIBLE);
                //Errors received here
                //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText( login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
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

    private void createLoginText() {
        String text = getString(R.string.LoginText);

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
                Intent intent = new Intent(login.this, WebView.class);
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
                Intent intent = new Intent(login.this, WebView.class);
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

        ss.setSpan(clickableSpan1, 71, 83, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 86, 100, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginText.setText(ss);
        loginText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}


















