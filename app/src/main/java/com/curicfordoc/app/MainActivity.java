package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public static String login_method;
    public static String userName;
    public static String userId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final static String FILE_NAME = "data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

           try {
                FileInputStream inputStream = openFileInput(FILE_NAME);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputStreamReader);
                login_method = br.readLine();
                if (login_method.equals("phone")){
                    userName= br.readLine();
                    userId = br.readLine();
                    login_method = "phone_users";
                    timer t = new timer();
                    t.start();

                }else if (login_method.equals("google")){
                    userName = br.readLine();
                    userId = br.readLine();
                    login_method = "gmail_users";
                    timer t = new timer();
                    t.start();

                }else if (login_method.equals("facebook")){
                    userName = br.readLine();
                    userId = br.readLine();
                    login_method = "fb_users";
                    timer t = new timer();
                    t.start();

                }else if (TextUtils.isEmpty(login_method)){
                    Intent intent = new Intent(MainActivity.this, login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// display the activity without animation   getWindow().setWindowAnimations(0)
                    startActivity(intent);
                    finish();
                }

            } catch (FileNotFoundException e) {
               timer_for_login timerForLogin = new timer_for_login();
               timerForLogin.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        new userDetails(MainActivity.this);

    }

    public void details_loader(View view){
        Intent intent = new Intent(MainActivity.this, homepage.class); // TODO : Remove - details_loader.class
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// display the activity without animation   getWindow().setWindowAnimations(0)
        intent.putExtra("login_method", login_method);
        intent.putExtra("userName", userName);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    class timer extends Thread {
        public void run(){
            int i = 0;
            for(i=0; i<2; i++){
                try {
                    Thread.sleep(1000);
                }catch (Exception e){}
            }
            details_loader(null);
//            Intent intent = new Intent(MainActivity.this, homepage.class);
//            startActivity(intent);
        }
    }
    class timer_for_login extends Thread {
        public void run(){
            int i = 0;
            for(i=0; i<4; i++){
                try {
                    Thread.sleep(1000);
                }catch (Exception e){}
            }
            Intent intent = new Intent(MainActivity.this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// display the activity without animation   getWindow().setWindowAnimations(0)
            startActivity(intent);
            finish();
        }
    }
}