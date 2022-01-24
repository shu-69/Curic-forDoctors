package com.curicfordoc.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class userDetails extends Application {
    public static String login_method, userName, userId;

    public static Context context;

    public boolean initialize(Context con){
        context = con;

        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            FileInputStream inputStream = context.openFileInput("data");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            login_method = br.readLine();
            if (login_method.equals("phone")){
                userName= br.readLine();
                userId = br.readLine();
                login_method = "phone_users";
            }else if (login_method.equals("google")){
                userName = br.readLine();
                userId = br.readLine();
                login_method = "gmail_users";
            }else if (login_method.equals("facebook")){
                userName = br.readLine();
                userId = br.readLine();
                login_method = "fb_users";
            }
        } catch (Exception e) { }

        System.out.println(login_method);

    }
}
