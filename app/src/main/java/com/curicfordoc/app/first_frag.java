package com.curicfordoc.app;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link first_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class first_frag extends Fragment {

    View view;
    LabeledSwitch toogleOpenclose;
    TextView textOpenClose;
    LottieAnimationView refresh;

    SharedPreferences HospDetailsSP;
    SharedPreferences.Editor editor;

    private final static String FILE_NAME = "data";
    public static String login_method = "";
    public static String userPhone = "";
    public static String userEmail = "";
    public static String userName = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public first_frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment first_frag.
     */
    // TODO: Rename and change types and number of parameters
    public static first_frag newInstance(String param1, String param2) {
        first_frag fragment = new first_frag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first_frag, container, false);

        hook();
        toogler();

        HospDetailsSP = getContext().getSharedPreferences("HospDetails", Context.MODE_PRIVATE);
        editor = HospDetailsSP.edit();

        if (new userDetails().initialize(getContext())) {
            new userDetails().onCreate();
        }

        new LoadImage().execute(HospDetailsSP.getString("userAvatar", "https://isobarscience.com/wp-content/uploads/2020/09/default-profile-picture1.jpg"));

        toogleOpenclose.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                toogler();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh.playAnimation();
            }
        });


        return view;
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                System.out.print(e.getMessage());
            }
            return logo;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            editor.putString("profileImageBitmap", BitMapToString(result));
            editor.apply();
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    void loadUserProfileImage(String ImageURL) {
        try {
            URL url = new URL(ImageURL);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            editor.putString("profileImageBitmap", image.toString());
            editor.apply();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void toogler() {
        if (toogleOpenclose.isOn()) {
            textOpenClose.setText("OPENED");
            textOpenClose.setTextColor(Color.parseColor("#10631E"));
            textOpenClose.setBackgroundResource(R.drawable.custom_edit_text_appborder);
            textOpenClose.setPadding(14, 5, 14, 0);
            textOpenClose.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textOpenClose.setText("CLOSED");
            textOpenClose.setTextColor(Color.parseColor("#FF0000"));
            textOpenClose.setBackgroundResource(R.drawable.custom_edit_text_redborder);
            textOpenClose.setPadding(14, 5, 14, 0);
            textOpenClose.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    private void hook() {
        toogleOpenclose = (LabeledSwitch) view.findViewById(R.id.toogleOpenClose);
        textOpenClose = (TextView) view.findViewById(R.id.textOpenClose);
        refresh = (LottieAnimationView) view.findViewById(R.id.refresh);
    }

    public void refresher() {
        refresh.playAnimation();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        refresh.pauseAnimation();
    }
}