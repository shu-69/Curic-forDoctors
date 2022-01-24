package com.curicfordoc.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.Inflater;

public class ManageDoctors extends AppCompatActivity {

    LinearLayout parentLayout;
    TabLayout tabLayout;
    LottieAnimationView profileLoader;

    SharedPreferences HospDetails;
    ImageView TempImageView;
    int t1Hour, t1Minute;

    Vector NameVC = new Vector();
    Vector GenderVC = new Vector();
    Vector ProfileImageVC = new Vector();
    Vector SpecializationVC = new Vector();
    Vector ExperienceVC = new Vector();
    Vector FeeVC = new Vector();
    Vector DocIDVC = new Vector();

    Vector CheckMonVC = new Vector();
    Vector CheckTueVC = new Vector();
    Vector CheckWedVC = new Vector();
    Vector CheckThurVC = new Vector();
    Vector CheckFriVC = new Vector();
    Vector CheckSatVC = new Vector();
    Vector CheckSunVC = new Vector();

    Vector MonTimeVC = new Vector();
    Vector TueTimeVC = new Vector();
    Vector WedTimeVC = new Vector();
    Vector ThurTimeVC = new Vector();
    Vector FriTimeVC = new Vector();
    Vector SatTimeVC = new Vector();
    Vector SunTimeVC = new Vector();


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_doctors);

        hook();

        HospDetails = getSharedPreferences("HospDetails", MODE_PRIVATE);

        if (new userDetails().initialize(ManageDoctors.this))
            new userDetails().onCreate();


        loadDoctorsProfile(userDetails.login_method, userDetails.userId);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getText().equals("ADD PROFILE +")) {
                    createNewView(false, 0);
                } else if (NameVC.size() - 1 >= tab.getPosition()) {
                    createNewView(true, tab.getPosition());
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void hook() {
        parentLayout = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tab_layout);
        profileLoader = findViewById(R.id.data_loading_anim);

    }

    private void loadDoctorsProfile(String LoginMethod, String UserID) {

        profileLoader.setVisibility(View.VISIBLE);

        db.collection("registered_doctors").document(LoginMethod).collection(UserID).document("doctors").collection("ids")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    tabLayout.removeAllTabs();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NameVC.add(document.getString("doctorName"));
                        GenderVC.add(document.getString("gender"));
                        ExperienceVC.add(document.getString("experience"));
                        FeeVC.add(document.getString("fee"));
                        ProfileImageVC.add(document.getString("profileImage"));
                        SpecializationVC.add(document.getString("specialization"));
                        DocIDVC.add(document.getString("docId"));

                        MonTimeVC.add(document.getString("monday_timing"));
                        TueTimeVC.add(document.getString("tuesday_timing"));
                        WedTimeVC.add(document.getString("wednesday_timing"));
                        ThurTimeVC.add(document.getString("thursday_timing"));
                        FriTimeVC.add(document.getString("friday_timing"));
                        SatTimeVC.add(document.getString("saturday_timing"));
                        SunTimeVC.add(document.getString("sunday_timing"));
                    }
                    profileLoader.setVisibility(View.GONE);
                    if (NameVC.size() == 0) {
                        createNewTabItem("ADD PROFILE +");
                        createNewView(false, 0);
                    } else {
                        for (int i = 0; i < NameVC.size(); i++) {
                            createNewTabItem(NameVC.get(i).toString());
                        }
                        createNewTabItem("ADD PROFILE +");
                        createNewView(true, 0);
                    }
                }
            }
        });

    }

    private void createNewTabItem(String TabItemName) {
        tabLayout.addTab(tabLayout.newTab().setText(TabItemName));

        parentLayout.removeAllViews();


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createNewView(boolean isUserExists, int dataPosition) {
        View view = getLayoutInflater().inflate(R.layout.new_doctor_details_card, null);

        LinearLayout UpdateDeleteLayout = view.findViewById(R.id.update_delete_buttons_layout);
        CardView UpdateCard = view.findViewById(R.id.updateDetails_card);
        CardView DeleteCard = view.findViewById(R.id.deleteDetails_card);
        TextView DocNameTextV = view.findViewById(R.id.docNameEditText);
        Spinner DocGenderSpinner = view.findViewById(R.id.docGender);
        ImageView DocProfileImage = view.findViewById(R.id.docProfileImage);
        TextView DocSpecializationTextV = view.findViewById(R.id.doctor_specialization);
        ChipGroup ChipGroup = view.findViewById(R.id.chip_group);
        TextView ExperienceTextV = view.findViewById(R.id.experience);
        CheckBox check_mon, check_tue, check_wed, check_thur, check_fri, check_sat, check_sun;
        TextView mon_1st_from_time, mon_1st_to_time, mon_2nd_from_time, mon_2nd_to_time,
                tue_1st_from_time, tue_1st_to_time, tue_2nd_from_time, tue_2nd_to_time,
                wed_1st_from_time, wed_1st_to_time, wed_2nd_from_time, wed_2nd_to_time,
                thur_1st_from_time, thur_1st_to_time, thur_2nd_from_time, thur_2nd_to_time,
                fri_1st_from_time, fri_1st_to_time, fri_2nd_from_time, fri_2nd_to_time,
                sat_1st_from_time, sat_1st_to_time, sat_2nd_from_time, sat_2nd_to_time,
                sun_1st_from_time, sun_1st_to_time, sun_2nd_from_time, sun_2nd_to_time,
                location_details_but, upi_error, banking_error;
        //<editor-fold>

        check_mon = view.findViewById(R.id.checkbox_mon);
        check_tue = view.findViewById(R.id.checkbox_tue);
        check_wed = view.findViewById(R.id.checkbox_wed);
        check_thur = view.findViewById(R.id.checkbox_thru);
        check_fri = view.findViewById(R.id.checkbox_fri);
        check_sat = view.findViewById(R.id.checkbox_sat);
        check_sun = view.findViewById(R.id.checkbox_sun);

        mon_1st_from_time = view.findViewById(R.id.mon_1st_from_time);
        mon_1st_to_time = view.findViewById(R.id.mon_1st_to_time);
        mon_2nd_from_time = view.findViewById(R.id.mon_2nd_from_time);
        mon_2nd_to_time = view.findViewById(R.id.mon_2nd_to_time);

        tue_1st_from_time = view.findViewById(R.id.tue_1st_from_time);
        tue_1st_to_time = view.findViewById(R.id.tue_1st_to_time);
        tue_2nd_from_time = view.findViewById(R.id.tue_2nd_from_time);
        tue_2nd_to_time = view.findViewById(R.id.tue_2nd_to_time);

        wed_1st_from_time = view.findViewById(R.id.wed_1st_from_time);
        wed_1st_to_time = view.findViewById(R.id.wed_1st_to_time);
        wed_2nd_from_time = view.findViewById(R.id.wed_2nd_from_time);
        wed_2nd_to_time = view.findViewById(R.id.wed_2nd_to_time);

        thur_1st_from_time = view.findViewById(R.id.thru_1st_from_time);
        thur_1st_to_time = view.findViewById(R.id.thru_1st_to_time);
        thur_2nd_from_time = view.findViewById(R.id.thru_2nd_from_time);
        thur_2nd_to_time = view.findViewById(R.id.thru_2nd_to_time);

        fri_1st_from_time = view.findViewById(R.id.fri_1st_from_time);
        fri_1st_to_time = view.findViewById(R.id.fri_1st_to_time);
        fri_2nd_from_time = view.findViewById(R.id.fri_2nd_from_time);
        fri_2nd_to_time = view.findViewById(R.id.fri_2nd_to_time);

        sat_1st_from_time = view.findViewById(R.id.sat_1st_from_time);
        sat_1st_to_time = view.findViewById(R.id.sat_1st_to_time);
        sat_2nd_from_time = view.findViewById(R.id.sat_2nd_from_time);
        sat_2nd_to_time = view.findViewById(R.id.sat_2nd_to_time);

        sun_1st_from_time = view.findViewById(R.id.sun_1st_from_time);
        sun_1st_to_time = view.findViewById(R.id.sun_1st_to_time);
        sun_2nd_from_time = view.findViewById(R.id.sun_2nd_from_time);
        sun_2nd_to_time = view.findViewById(R.id.sun_2nd_to_time);

        //</editor-fold>
        TextView FeeTextV = view.findViewById(R.id.fee);
        Button SubmitButton = view.findViewById(R.id.submitButton);

        // Setting Adapter in Gender Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DocGenderSpinner.setAdapter(adapter);


        if (isUserExists)
            SubmitButton.setVisibility(View.GONE);
        else
            UpdateDeleteLayout.setVisibility(View.GONE);

        if (isUserExists) {
            DocNameTextV.setText(NameVC.get(dataPosition).toString());
            if (GenderVC.get(dataPosition).toString().equals("Male"))
                DocGenderSpinner.setSelection(1);
            else
                DocGenderSpinner.setSelection(2);
            DocProfileImage.setPadding(0, 0, 0, 0);
            DocProfileImage.setImageBitmap(StringToBitMap(ProfileImageVC.get(dataPosition).toString()));
            DocSpecializationTextV.setText(SpecializationVC.get(dataPosition).toString());
            ExperienceTextV.setText(ExperienceVC.get(dataPosition).toString());
            FeeTextV.setText(FeeVC.get(dataPosition).toString());
            String mon_timings = "";
            if (MonTimeVC.get(dataPosition) != null)
                mon_timings = MonTimeVC.get(dataPosition).toString();
            String tue_timings = "";
            if (TueTimeVC.get(dataPosition) != null)
                tue_timings = TueTimeVC.get(dataPosition).toString();
            String wed_timings = "";
            if (WedTimeVC.get(dataPosition) != null)
                wed_timings = WedTimeVC.get(dataPosition).toString();
            String thur_timings = "";
            if (ThurTimeVC.get(dataPosition) != null)
                thur_timings = ThurTimeVC.get(dataPosition).toString();
            String fri_timings = "";
            if (FriTimeVC.get(dataPosition) != null)
                fri_timings = FriTimeVC.get(dataPosition).toString();
            String sat_timings = "";
            if (SatTimeVC.get(dataPosition) != null)
                sat_timings = SatTimeVC.get(dataPosition).toString();
            String sun_timings = "";
            if (SunTimeVC.get(dataPosition) != null)
                sun_timings = SunTimeVC.get(dataPosition).toString();


            if (!TextUtils.isEmpty(mon_timings)) {
                check_mon.setChecked(true);
                String temptime = "";
                for (int i = 0; i < mon_timings.length(); i++) {
                    if (mon_timings.charAt(i) == '&') {
                        mon_1st_to_time.setText(temptime);
                        temptime = "";
                        for (int j = i + 1; j < mon_timings.length(); j++) {
                            if (mon_timings.charAt(j) == '-') {
                                mon_2nd_from_time.setText(temptime);
                                temptime = "";
                            } else
                                temptime += mon_timings.charAt(j);
                            if (j == mon_timings.length() - 1) {
                                mon_2nd_to_time.setText(temptime);
                                i = mon_timings.length();
                            }
                        }
                    } else if (mon_timings.charAt(i) == '-') {
                        mon_1st_from_time.setText(temptime);
                        temptime = "";
                    } else
                        temptime += mon_timings.charAt(i);
                }
            }
            if (!TextUtils.isEmpty(tue_timings)) {
                check_tue.setChecked(true);
                String temptime = "";
                for (int i = 0; i < tue_timings.length(); i++) {
                    if (tue_timings.charAt(i) == '&') {
                        tue_1st_to_time.setText(temptime);
                        temptime = "";
                        for (int j = i + 1; j < tue_timings.length(); j++) {
                            if (tue_timings.charAt(j) == '-') {
                                tue_2nd_from_time.setText(temptime);
                                temptime = "";
                            } else
                                temptime += tue_timings.charAt(j);
                            if (j == tue_timings.length() - 1) {
                                tue_2nd_to_time.setText(temptime);
                                i = tue_timings.length();
                            }
                        }
                    } else if (tue_timings.charAt(i) == '-') {
                        tue_1st_from_time.setText(temptime);
                        temptime = "";
                    } else
                        temptime += tue_timings.charAt(i);
                }
            }
            if (!TextUtils.isEmpty(wed_timings)) {
                check_wed.setChecked(true);
                String temptime = "";
                for (int i = 0; i < wed_timings.length(); i++) {

                    if (wed_timings.charAt(i) == '&') {
                        wed_1st_to_time.setText(temptime);
                        temptime = "";
                        for (int j = i + 1; j < wed_timings.length(); j++) {
                            if (wed_timings.charAt(j) == '-') {
                                wed_2nd_from_time.setText(temptime);
                                temptime = "";
                            } else
                                temptime += wed_timings.charAt(j);
                            if (j == wed_timings.length() - 1) {
                                wed_2nd_to_time.setText(temptime);
                                i = wed_timings.length();
                            }
                        }
                    } else if (wed_timings.charAt(i) == '-') {
                        wed_1st_from_time.setText(temptime);
                        temptime = "";
                    } else
                        temptime += wed_timings.charAt(i);
                }
            }
            if (!TextUtils.isEmpty(thur_timings)) {
                check_thur.setChecked(true);
                String temptime = "";
                for (int i = 0; i < thur_timings.length(); i++) {
                    if (thur_timings.charAt(i) == '&') {
                        thur_1st_to_time.setText(temptime);
                        temptime = "";
                        for (int j = i + 1; j < thur_timings.length(); j++) {
                            if (thur_timings.charAt(j) == '-') {
                                thur_2nd_from_time.setText(temptime);
                                temptime = "";
                            } else
                                temptime += thur_timings.charAt(j);
                            if (j == thur_timings.length() - 1) {
                                thur_2nd_to_time.setText(temptime);
                                i = thur_timings.length();
                            }
                        }
                    } else if (thur_timings.charAt(i) == '-') {
                        thur_1st_from_time.setText(temptime);
                        temptime = "";
                    } else
                        temptime += thur_timings.charAt(i);
                }
            }
            if (!TextUtils.isEmpty(fri_timings)) {
                check_fri.setChecked(true);
                String temptime = "";
                for (int i = 0; i < fri_timings.length(); i++) {
                    if (fri_timings.charAt(i) == '&') {
                        fri_1st_to_time.setText(temptime);
                        temptime = "";
                        for (int j = i + 1; j < fri_timings.length(); j++) {
                            if (fri_timings.charAt(j) == '-') {
                                fri_2nd_from_time.setText(temptime);
                                temptime = "";
                            } else
                                temptime += fri_timings.charAt(j);
                            if (j == fri_timings.length() - 1) {
                                fri_2nd_to_time.setText(temptime);
                                i = fri_timings.length();
                            }
                        }
                    } else if (fri_timings.charAt(i) == '-') {
                        fri_1st_from_time.setText(temptime);
                        temptime = "";
                    } else
                        temptime += fri_timings.charAt(i);
                }
            }
            if (!TextUtils.isEmpty(sat_timings)) {
                check_sat.setChecked(true);
                String temptime = "";
                for (int i = 0; i < sat_timings.length(); i++) {
                    if (sat_timings.charAt(i) == '&') {
                        sat_1st_to_time.setText(temptime);
                        temptime = "";
                        for (int j = i + 1; j < sat_timings.length(); j++) {
                            if (sat_timings.charAt(j) == '-') {
                                sat_2nd_from_time.setText(temptime);
                                temptime = "";
                            } else
                                temptime += sat_timings.charAt(j);
                            if (j == sat_timings.length() - 1) {
                                sat_2nd_to_time.setText(temptime);
                                i = sat_timings.length();
                            }
                        }
                    } else if (sat_timings.charAt(i) == '-') {
                        sat_1st_from_time.setText(temptime);
                        temptime = "";
                    } else
                        temptime += sat_timings.charAt(i);
                }
            }
            if (!TextUtils.isEmpty(sun_timings)) {
                check_sun.setChecked(true);
                String temptime = "";
                for (int i = 0; i < sun_timings.length(); i++) {
                    if (sun_timings.charAt(i) == '&') {
                        sun_1st_to_time.setText(temptime);
                        temptime = "";
                        for (int j = i + 1; j < sun_timings.length(); j++) {
                            if (sun_timings.charAt(j) == '-') {
                                sun_2nd_from_time.setText(temptime);
                                temptime = "";
                            } else
                                temptime += sun_timings.charAt(j);
                            if (j == sun_timings.length() - 1) {
                                sun_2nd_to_time.setText(temptime);
                                i = sun_timings.length();
                            }
                        }
                    } else if (sun_timings.charAt(i) == '-') {
                        sun_1st_from_time.setText(temptime);
                        temptime = "";
                    } else
                        temptime += sun_timings.charAt(i);
                }
            }

        }

        DocProfileImage.setOnClickListener(v -> {
            TempImageView = DocProfileImage;
            ImagePicker.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        // <editor-fold> // Chips Listeners
        view.findViewById(R.id.chip1).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip1);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip2).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip2);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip3).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip3);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip4).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip4);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip5).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip5);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip6).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip6);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip7).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip7);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip8).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip8);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip9).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip9);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip10).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip10);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip11).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip11);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip12).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip12);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip13).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip13);
            DocSpecializationTextV.setText(chip.getText());
        });
        view.findViewById(R.id.chip14).setOnClickListener(v -> {
            Chip chip = view.findViewById(R.id.chip14);
            DocSpecializationTextV.setText(chip.getText());
        });
        // </editor-fold>
        // <editor-fold> // Time Choosers
        mon_1st_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            mon_1st_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        mon_1st_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            mon_1st_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        mon_2nd_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            mon_2nd_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        mon_2nd_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            mon_2nd_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        tue_1st_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            tue_1st_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        tue_1st_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            tue_1st_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        tue_2nd_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            tue_2nd_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        tue_2nd_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            tue_2nd_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        wed_1st_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            wed_1st_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        wed_1st_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            wed_1st_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        wed_2nd_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            wed_2nd_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        wed_2nd_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            wed_2nd_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        thur_1st_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            thur_1st_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        thur_1st_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            thur_1st_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        thur_2nd_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            thur_2nd_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        thur_2nd_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            thur_2nd_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        fri_1st_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            fri_1st_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        fri_1st_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            fri_1st_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        fri_2nd_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            fri_2nd_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        fri_2nd_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            fri_2nd_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sat_1st_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sat_1st_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sat_1st_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sat_1st_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sat_2nd_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sat_2nd_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sat_2nd_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sat_2nd_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sun_1st_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sun_1st_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sun_1st_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sun_1st_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sun_2nd_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sun_2nd_from_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });
        sun_2nd_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManageDoctors.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int minute) {   // remove the theme for analog clock design
                        t1Hour = hourofDay;
                        t1Minute = minute;
                        String time = t1Hour + ":" + t1Minute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);

                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            sun_2nd_to_time.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
/**
 Calendar calendar = Calendar.getInstance();
 calendar.set(0,0,0,t1Hour,t1Minute);
 mon_1st_from_time.setText(DateFormat.format("hh:mm aa",calendar));*/
                    }
                }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // remove for analog clock design
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });

        // </editor-fold> //
        SubmitButton.setOnClickListener(v -> {

            if (new userDetails().initialize(ManageDoctors.this))
                new userDetails().onCreate();

            String Name = DocNameTextV.getText().toString().trim();
            String Gender = DocGenderSpinner.getSelectedItem().toString();
            String Specialization = DocSpecializationTextV.getText().toString().trim();
            String Experience = ExperienceTextV.getText().toString().trim();
            String Fee = FeeTextV.getText().toString().trim();
            String DocID = String.valueOf((int) (Math.random() * (654546456 - 1000 + 1)) + 1000);

            String mon1from = mon_1st_from_time.getText().toString();
            String mon1to = mon_1st_to_time.getText().toString();
            String mon2from = mon_2nd_from_time.getText().toString();
            String mon2to = mon_2nd_to_time.getText().toString();

            String tue1from = tue_1st_from_time.getText().toString();
            String tue1to = tue_1st_to_time.getText().toString();
            String tue2from = tue_2nd_from_time.getText().toString();
            String tue2to = tue_2nd_to_time.getText().toString();

            String wed1from = wed_1st_from_time.getText().toString();
            String wed1to = wed_1st_to_time.getText().toString();
            String wed2from = wed_2nd_from_time.getText().toString();
            String wed2to = wed_2nd_to_time.getText().toString();

            String thur1from = thur_1st_from_time.getText().toString();
            String thur1to = thur_1st_to_time.getText().toString();
            String thur2from = thur_2nd_from_time.getText().toString();
            String thur2to = thur_2nd_to_time.getText().toString();

            String fri1from = fri_1st_from_time.getText().toString();
            String fri1to = fri_1st_to_time.getText().toString();
            String fri2from = fri_2nd_from_time.getText().toString();
            String fri2to = fri_2nd_to_time.getText().toString();

            String sat1from = sat_1st_from_time.getText().toString();
            String sat1to = sat_1st_to_time.getText().toString();
            String sat2from = sat_2nd_from_time.getText().toString();
            String sat2to = sat_2nd_to_time.getText().toString();

            String sun1from = sun_1st_from_time.getText().toString();
            String sun1to = sun_1st_to_time.getText().toString();
            String sun2from = sun_2nd_from_time.getText().toString();
            String sun2to = sun_2nd_to_time.getText().toString();

            // Checking if the profile image is uploaded or not.
            boolean isProfileImageUploaded = true;

            String imageType = DocProfileImage.getDrawable().toString();
            String tempImageType = "";

            for (int i = 0; i < imageType.length(); i++) {
                if (imageType.charAt(i) == '@')
                    break;
                else
                    tempImageType += imageType.charAt(i);
            }


            if (tempImageType.equals("android.graphics.drawable.VectorDrawable"))
                isProfileImageUploaded = false;

            if (Name.equals("Dr. ") || TextUtils.isEmpty(Name) || Gender.equals("Select Gender") || TextUtils.isEmpty(Specialization) || TextUtils.isEmpty(Experience) || TextUtils.isEmpty(Fee)
                    || !isProfileImageUploaded) {
                Toast.makeText(ManageDoctors.this, "All fields required", Toast.LENGTH_SHORT).show();
            } else {
                ProgressDialog pd = new ProgressDialog(ManageDoctors.this);
                pd.setMessage("Adding Profile");
                pd.show();

                Map<String, Object> note = new HashMap<>();
                note.put("doctorName", Name);
                note.put("gender", Gender);
                note.put("docId", DocID);
                note.put("profileImage", BitMapToString(drawableToBitmap(DocProfileImage.getDrawable())));
                note.put("specialization", Specialization);
                note.put("experience", Experience);
                note.put("fee", Fee);
                if (check_mon.isChecked()) {
                    if (!TextUtils.isEmpty(mon1from) || !TextUtils.isEmpty(mon1to))
                        if (TextUtils.isEmpty(mon2from) || TextUtils.isEmpty(mon2to)) {
                            note.put("monday_timing", mon1from + "-" + mon1to + "&");
                        } else {
                            note.put("monday_timing", mon1from + "-" + mon1to + "&" + mon2from + "-" + mon2to);
                        }
                }
                if (check_tue.isChecked()) {
                    if (!TextUtils.isEmpty(tue1from) || !TextUtils.isEmpty(tue1to))
                        if (TextUtils.isEmpty(tue2from) || TextUtils.isEmpty(tue2to)) {
                            note.put("tuesday_timing", tue1from + "-" + tue1to + "&");
                        } else {
                            note.put("tuesday_timing", tue1from + "-" + tue1to + "&" + tue2from + "-" + tue2to);
                        }
                }
                if (check_wed.isChecked()) {
                    if (!TextUtils.isEmpty(wed1from) || !TextUtils.isEmpty(wed1to))
                        if (TextUtils.isEmpty(wed2from) || TextUtils.isEmpty(wed2to)) {
                            note.put("wednesday_timing", wed1from + "-" + wed1to + "&");
                        } else {
                            note.put("wednesday_timing", wed1from + "-" + wed1to + "&" + wed2from + "-" + wed2to);
                        }
                }
                if (check_thur.isChecked()) {
                    if (!TextUtils.isEmpty(thur1from) || !TextUtils.isEmpty(thur1to))
                        if (TextUtils.isEmpty(thur2from) || TextUtils.isEmpty(thur2to)) {
                            note.put("thursday_timing", thur1from + "-" + thur1to + "&");
                        } else {
                            note.put("thursday_timing", thur1from + "-" + thur1to + "&" + thur2from + "-" + thur2to);
                        }
                }
                if (check_fri.isChecked()) {
                    if (!TextUtils.isEmpty(fri1from) || !TextUtils.isEmpty(fri1to))
                        if (TextUtils.isEmpty(fri2from) || TextUtils.isEmpty(fri2to)) {
                            note.put("friday_timing", fri1from + "-" + fri1to + "&");
                        } else {
                            note.put("friday_timing", fri1from + "-" + fri1to + "&" + fri2from + "-" + fri2to);
                        }
                }
                if (check_sat.isChecked()) {
                    if (!TextUtils.isEmpty(sat1from) || !TextUtils.isEmpty(sat1to))
                        if (TextUtils.isEmpty(sat2from) || TextUtils.isEmpty(sat2to)) {
                            note.put("saturday_timing", sat1from + "-" + sat1to + "&");
                        } else {
                            note.put("saturday_timing", sat1from + "-" + sat1to + "&" + sat2from + "-" + sat2to);
                        }
                }
                if (check_sun.isChecked()) {
                    if (!TextUtils.isEmpty(sun1from) || !TextUtils.isEmpty(sun1to))
                        if (TextUtils.isEmpty(sun2from) || TextUtils.isEmpty(sun2to)) {
                            note.put("sunday_timing", sun1from + "-" + sun1to + "&");
                        } else {
                            note.put("sunday_timing", sun1from + "-" + sun1to + "&" + sun2from + "-" + sun2to);
                        }
                }

                db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("doctors")
                        .collection("ids").document(DocID).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        pd.hide();
                        Toast.makeText(ManageDoctors.this, "Profile Created Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ManageDoctors.this, ManageDoctors.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.hide();
                        Toast.makeText(ManageDoctors.this, "Something went wrong [Try again later]", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });
        DeleteCard.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManageDoctors.this);
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Are you sure you want to Delete this Profile?");
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                    ProgressDialog pd = new ProgressDialog(ManageDoctors.this);
                    pd.setMessage("Deleting Profile");
                    pd.show();
                    db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("doctors")
                            .collection("ids").document(DocIDVC.get(dataPosition).toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            pd.hide();
                            Toast.makeText(ManageDoctors.this, "Profile Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ManageDoctors.this, ManageDoctors.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.hide();
                            Toast.makeText(ManageDoctors.this, "Something went wrong [Try again later]", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).show();
        });
        UpdateCard.setOnClickListener(v -> {
            if (new userDetails().initialize(ManageDoctors.this))
                new userDetails().onCreate();

            String Name = DocNameTextV.getText().toString().trim();
            String Gender = DocGenderSpinner.getSelectedItem().toString();
            String Specialization = DocSpecializationTextV.getText().toString().trim();
            String Experience = ExperienceTextV.getText().toString().trim();
            String Fee = FeeTextV.getText().toString().trim();
            String DocID = DocIDVC.get(dataPosition).toString();

            String mon1from = mon_1st_from_time.getText().toString();
            String mon1to = mon_1st_to_time.getText().toString();
            String mon2from = mon_2nd_from_time.getText().toString();
            String mon2to = mon_2nd_to_time.getText().toString();

            String tue1from = tue_1st_from_time.getText().toString();
            String tue1to = tue_1st_to_time.getText().toString();
            String tue2from = tue_2nd_from_time.getText().toString();
            String tue2to = tue_2nd_to_time.getText().toString();

            String wed1from = wed_1st_from_time.getText().toString();
            String wed1to = wed_1st_to_time.getText().toString();
            String wed2from = wed_2nd_from_time.getText().toString();
            String wed2to = wed_2nd_to_time.getText().toString();

            String thur1from = thur_1st_from_time.getText().toString();
            String thur1to = thur_1st_to_time.getText().toString();
            String thur2from = thur_2nd_from_time.getText().toString();
            String thur2to = thur_2nd_to_time.getText().toString();

            String fri1from = fri_1st_from_time.getText().toString();
            String fri1to = fri_1st_to_time.getText().toString();
            String fri2from = fri_2nd_from_time.getText().toString();
            String fri2to = fri_2nd_to_time.getText().toString();

            String sat1from = sat_1st_from_time.getText().toString();
            String sat1to = sat_1st_to_time.getText().toString();
            String sat2from = sat_2nd_from_time.getText().toString();
            String sat2to = sat_2nd_to_time.getText().toString();

            String sun1from = sun_1st_from_time.getText().toString();
            String sun1to = sun_1st_to_time.getText().toString();
            String sun2from = sun_2nd_from_time.getText().toString();
            String sun2to = sun_2nd_to_time.getText().toString();

            // Checking if the profile image is uploaded or not.
            boolean isProfileImageUploaded = true;

            String imageType = DocProfileImage.getDrawable().toString();
            String tempImageType = "";

            for (int i = 0; i < imageType.length(); i++) {
                if (imageType.charAt(i) == '@')
                    break;
                else
                    tempImageType += imageType.charAt(i);
            }


            if (tempImageType.equals("android.graphics.drawable.VectorDrawable"))
                isProfileImageUploaded = false;

            if (Name.equals("Dr. ") || TextUtils.isEmpty(Name) || Gender.equals("Select Gender") || TextUtils.isEmpty(Specialization) || TextUtils.isEmpty(Experience) || TextUtils.isEmpty(Fee)
                    || !isProfileImageUploaded)
                Toast.makeText(ManageDoctors.this, "All fields required", Toast.LENGTH_SHORT).show();
            else {
                ProgressDialog pd = new ProgressDialog(ManageDoctors.this);
                pd.setMessage("Updating Profile");
                pd.show();

                Map<String, Object> note = new HashMap<>();
                note.put("doctorName", Name);
                note.put("gender", Gender);
                note.put("docId", DocID);
                note.put("profileImage", BitMapToString(drawableToBitmap(DocProfileImage.getDrawable())));
                note.put("specialization", Specialization);
                note.put("experience", Experience);
                note.put("fee", Fee);
                if (check_mon.isChecked()) {
                    if (!TextUtils.isEmpty(mon1from) || !TextUtils.isEmpty(mon1to))
                        if (TextUtils.isEmpty(mon2from) || TextUtils.isEmpty(mon2to)) {
                            note.put("monday_timing", mon1from + "-" + mon1to + "&");
                        } else {
                            note.put("monday_timing", mon1from + "-" + mon1to + "&" + mon2from + "-" + mon2to);
                        }
                }
                if (check_tue.isChecked()) {
                    if (!TextUtils.isEmpty(tue1from) || !TextUtils.isEmpty(tue1to))
                        if (TextUtils.isEmpty(tue2from) || TextUtils.isEmpty(tue2to)) {
                            note.put("tuesday_timing", tue1from + "-" + tue1to + "&");
                        } else {
                            note.put("tuesday_timing", tue1from + "-" + tue1to + "&" + tue2from + "-" + tue2to);
                        }
                }
                if (check_wed.isChecked()) {
                    if (!TextUtils.isEmpty(wed1from) || !TextUtils.isEmpty(wed1to))
                        if (TextUtils.isEmpty(wed2from) || TextUtils.isEmpty(wed2to)) {
                            note.put("wednesday_timing", wed1from + "-" + wed1to + "&");
                        } else {
                            note.put("wednesday_timing", wed1from + "-" + wed1to + "&" + wed2from + "-" + wed2to);
                        }
                }
                if (check_thur.isChecked()) {
                    if (!TextUtils.isEmpty(thur1from) || !TextUtils.isEmpty(thur1to))
                        if (TextUtils.isEmpty(thur2from) || TextUtils.isEmpty(thur2to)) {
                            note.put("thursday_timing", thur1from + "-" + thur1to + "&");
                        } else {
                            note.put("thursday_timing", thur1from + "-" + thur1to + "&" + thur2from + "-" + thur2to);
                        }
                }
                if (check_fri.isChecked()) {
                    if (!TextUtils.isEmpty(fri1from) || !TextUtils.isEmpty(fri1to))
                        if (TextUtils.isEmpty(fri2from) || TextUtils.isEmpty(fri2to)) {
                            note.put("friday_timing", fri1from + "-" + fri1to + "&");
                        } else {
                            note.put("friday_timing", fri1from + "-" + fri1to + "&" + fri2from + "-" + fri2to);
                        }
                }
                if (check_sat.isChecked()) {
                    if (!TextUtils.isEmpty(sat1from) || !TextUtils.isEmpty(sat1to))
                        if (TextUtils.isEmpty(sat2from) || TextUtils.isEmpty(sat2to)) {
                            note.put("saturday_timing", sat1from + "-" + sat1to + "&");
                        } else {
                            note.put("saturday_timing", sat1from + "-" + sat1to + "&" + sat2from + "-" + sat2to);
                        }
                }
                if (check_sun.isChecked()) {
                    if (!TextUtils.isEmpty(sun1from) || !TextUtils.isEmpty(sun1to))
                        if (TextUtils.isEmpty(sun2from) || TextUtils.isEmpty(sun2to)) {
                            note.put("sunday_timing", sun1from + "-" + sun1to + "&");
                        } else {
                            note.put("sunday_timing", sun1from + "-" + sun1to + "&" + sun2from + "-" + sun2to);
                        }
                }

                db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("doctors")
                        .collection("ids").document(DocID).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        pd.hide();
                        Toast.makeText(ManageDoctors.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ManageDoctors.this, ManageDoctors.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.hide();
                        Toast.makeText(ManageDoctors.this, "Profile Updating Failed [Try again later]", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


        parentLayout.removeAllViews();
        parentLayout.addView(view);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        TempImageView.setPadding(0, 0, 0, 0);
        TempImageView.setImageURI(uri);

    }
}





























