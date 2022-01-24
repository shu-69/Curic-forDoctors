package com.curicfordoc.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottom_nav = findViewById(R.id.bottomnav);
        bottom_nav.setOnNavigationItemSelectedListener(navListner);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new first_frag()).commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.first_frag:
                    selectedFragment = new first_frag();
                    break;

                case R.id.sec_frag:_frag:
                selectedFragment = new sec_frag();
                    break;

                case R.id.third_frag:t_frag:
                selectedFragment = new third_frag();
                    break;
//
//                case R.id.fourth_frag:_frag:
//                selectedFragment = new fourth_frag();
//                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();

            return true;
        }
    };
}