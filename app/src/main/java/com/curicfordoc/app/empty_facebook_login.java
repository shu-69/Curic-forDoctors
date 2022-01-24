package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class empty_facebook_login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_facebook_login);
//
//        private FirebaseAuth mAuth;
//        CallbackManager callbackManager;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_empty_facebook_login);
//
//            mAuth = FirebaseAuth.getInstance();
//
//
//            callbackManager = CallbackManager.Factory.create();
//
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
//            LoginManager.getInstance().registerCallback(callbackManager,
//                    new FacebookCallback<LoginResult>() {
//                        @Override
//                        public void onSuccess(LoginResult loginResult) {
//                            handleFacebookAccessToken(loginResult.getAccessToken());
//                        }
//
//                        @Override
//                        public void onCancel() {
//                            // App code
//                            finish();
//                        }
//
//                        @Override
//                        public void onError(FacebookException exception) {
//                            // App code
//                        }
//                    });
//        }
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//
//            // Pass the activity result back to the Facebook SDK
//            callbackManager.onActivityResult(requestCode, resultCode, data);
//        }
//
//        private void handleFacebookAccessToken(AccessToken token) {
//
//            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//            mAuth.signInWithCredential(credential)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success, update UI with the signed-in user's information
//
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                String usrId = user.getUid();
//                                String usrName = user.getDisplayName();
//                                String usrPhone = user.getPhoneNumber();
//                                String usrEmail = user.getEmail();
//                                Uri userAvtar = user.getPhotoUrl();
//                                String login_method = "facebook";
//
//                                android.content.Intent intent = new Intent( empty_facebook_login.this, registration_complete.class);
//                                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION);// display the activity without animation   getWindow().setWindowAnimations(0)
//                                intent.putExtra("userId" , usrId);
//                                intent.putExtra("userName" , usrName);
//                                intent.putExtra("userPhone" , usrPhone);
//                                intent.putExtra("userEmail", usrEmail);
//                                intent.putExtra("usrAvtar", String.valueOf(userAvtar));
//                                intent.putExtra("login_method", login_method);
//                                startActivity(intent);
//                                finish();
//
//                            } else {
//                                // If sign in fails, display a message to the user.
//
//                                Toast.makeText(empty_facebook_login.this, ""+task.getException(),
//                                        Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        }
//                    });
//    }
    }
}