package com.curicfordoc.app;

import static android.graphics.Color.argb;

import static com.curicfordoc.app.R.drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class clinic_details extends AppCompatActivity implements LocationListener {

    EditText hospital_name, owner_name, contact_no, email_address;
    TextView location_details_but, upi_error, banking_error;
    EditText location_city, location_state, location_country, location_address;
    TextInputEditText upi_id, ac_holder_name, account_number, ifsc_code;
    Button submit_button;
    Float ratings = 0.0F;
    public static boolean isDetailsExists;
    String hospID, hospitalName, ownerName, contactNum, email, contactNo, city, state, country, address, feeRs, upiId, holder_name, account_no, ifsc;
    ProgressBar progressBar;
    ScrollView scrollv;
    LocationManager locationManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_details);

        hook();
        grantPermission();
        fillDetails();
        location_details_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantPermission();
                checkLocationIsEnabledOrNot();
                getLocation();
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isDetailsExists) {
                    hospID = String.valueOf((int) (Math.random() * (654546456 - 1000 + 1)) + 1000);
                }

                if (check_error() && hospID != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    scrollv.fullScroll(View.FOCUS_UP);

                    if (new userDetails().initialize(clinic_details.this))
                        new userDetails().onCreate();

                    Map<String, Object> note = new HashMap<>();
                    note.put("hospital_name", hospitalName);
                    note.put("owner_name", ownerName);
                    note.put("contact_no", contactNo);
                    note.put("country", country);
                    note.put("email", email);
                    note.put("state", state);
                    note.put("city", city);
                    note.put("ratings", ratings);
                    note.put("hospId", hospID);
                    note.put("address", address);
                    note.put("doctor_login_id", userDetails.userId);
                    note.put("details_verified", false);
                    note.put("doctor_login_method", userDetails.login_method);
                    // Storing hospital details && timings
                    ProgressDialog pd = new ProgressDialog(clinic_details.this);
                    pd.setMessage("Uploading Details");
                    pd.show();
                    db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("hospital_details").set(note)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.hide();
                                    SharedPreferences HospDetails = getSharedPreferences("HospDetails", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = HospDetails.edit();
                                    editor.putString("hospName", hospitalName);
                                    editor.putString("ownerName", ownerName);
                                    editor.putBoolean("isVerified", false);
                                    editor.putString("city", city);
                                    editor.putString("state", state);
                                    editor.putString("country", country);
                                    editor.putString("ratingValue", ratings.toString());
                                    editor.apply();
                                    Toast.makeText(clinic_details.this, "Details Submitted", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.hide();
                            Toast.makeText(clinic_details.this, "Failed to upload details", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Map<String, Object> note2 = new HashMap<>();
                    note2.put("upi_id", upiId);
                    note2.put("bank_account_holder_name", holder_name);
                    note2.put("bank_account_number", account_no);
                    note2.put("bank_ifsc_code", ifsc);
                    // Storing payment details
                    db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("payment_details").set(note2)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.hide();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.hide();
                        }
                    });
                    // storing hospital details for given city
                    db.collection("registered_hospitals").document(country).collection(state).document(city).collection("hosp").document(hospID).set(note)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    verification(userDetails.login_method, userDetails.userId);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(clinic_details.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(clinic_details.this, "Submission failed, please try again later", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean verification(String login_method, String userID) {
        Map<String, Object> note = new HashMap<>();
        note.put("userId", userID);
        note.put("city", userID);
        note.put("state", userID);
        note.put("country", userID);
        db.collection("unverified_doctors").document(login_method).collection(userID).document("verification").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // On success code
                        Intent intent = new Intent(clinic_details.this, verification.class);
                        startActivity(intent);
                        finish();
                    }
                });
        return true;
    }

    private void fillDetails() {
        progressBar.setVisibility(View.VISIBLE);
        submit_button.setEnabled(false);

        if (new userDetails().initialize(clinic_details.this))
            new userDetails().onCreate();

        db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("hospital_details").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // Filling payment details
                        db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("payment_details").get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            upi_id.setText(documentSnapshot.getString("upi_id"));
                                            ac_holder_name.setText(documentSnapshot.getString("bank_account_holder_name"));
                                            account_number.setText(documentSnapshot.getString("bank_account_number"));
                                            ifsc_code.setText(documentSnapshot.getString("bank_ifsc_code"));
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        if (documentSnapshot.exists()) {
                            hospital_name.setText(documentSnapshot.getString("hospital_name"));
                            owner_name.setText(documentSnapshot.getString("owner_name"));
                            contact_no.setText(documentSnapshot.getString("contact_no"));
                            email_address.setText(documentSnapshot.getString("email"));
                            location_city.setText(documentSnapshot.getString("city"));
                            location_state.setText(documentSnapshot.getString("state"));
                            location_country.setText(documentSnapshot.getString("country"));
                            location_address.setText(documentSnapshot.getString("address"));
                            upi_id.setText(documentSnapshot.getString("upi_id"));
                            ac_holder_name.setText(documentSnapshot.getString("bank_account_holder_name"));
                            account_number.setText(documentSnapshot.getString("bank_account_number"));
                            ifsc_code.setText(documentSnapshot.getString("bank_ifsc_code"));
                            hospID = documentSnapshot.getString("hospId");
                            try {ratings = Float.parseFloat(documentSnapshot.getString("ratings"));}catch (Exception e){ }
                            submit_button.setText("UPDATE DETAILS");
                            isDetailsExists = true;
                            progressBar.setVisibility(View.INVISIBLE);
                            submit_button.setEnabled(true);

                        } else {
                            isDetailsExists = false;
                            progressBar.setVisibility(View.INVISIBLE);
                            submit_button.setEnabled(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(clinic_details.this, "Failed to load details, please try again", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationIsEnabledOrNot() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!gpsEnabled && !networkEnabled) {
            new AlertDialog.Builder(clinic_details.this).setTitle("Enable GPS Service").setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent((Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
                        }
                    }).setNegativeButton("Cancle", null).show();
        }
    }

    private void grantPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    private void hook() {

        scrollv = findViewById(R.id.ScrollView);
        progressBar = findViewById(R.id.loading_bar2);

        hospital_name = findViewById(R.id.hospital_name);
        owner_name = findViewById(R.id.owner_name);
        contact_no = findViewById(R.id.contact_no);
        email_address = findViewById(R.id.email_address);

        location_details_but = findViewById(R.id.location_details_butt);

        location_city = findViewById(R.id.location_city);
        location_state = findViewById(R.id.location_state);
        location_country = findViewById(R.id.location_country);
        location_address = findViewById(R.id.location_address);

        upi_id = findViewById(R.id.upi_id);
        upi_error = findViewById(R.id.upi_error);
        banking_error = findViewById(R.id.banking_error);
        ac_holder_name = findViewById(R.id.account_holder_name);
        account_number = findViewById(R.id.account_number);
        ifsc_code = findViewById(R.id.ifsc_code);

        submit_button = findViewById(R.id.submit_button);

    }

    public boolean check_error() {

        // Setting default drawable
        hospital_name.setBackgroundResource(drawable.custom_edit_text);
        owner_name.setBackgroundResource(drawable.custom_edit_text);
        location_state.setBackgroundResource(drawable.custom_edit_text);
        location_city.setBackgroundResource(drawable.custom_edit_text);
        location_country.setBackgroundResource(drawable.custom_edit_text);
        location_address.setBackgroundResource(drawable.custom_edit_text);
        contact_no.setBackgroundResource(drawable.custom_edit_text);
        // Giving padding of 40 px to left and right
        hospital_name.setPadding(40, 0, 40, 0);
        owner_name.setPadding(40, 0, 40, 0);
        location_state.setPadding(40, 0, 40, 0);
        location_city.setPadding(40, 0, 40, 0);
        location_country.setPadding(40, 0, 40, 0);
        location_address.setPadding(40, 20, 40, 20);
        contact_no.setPadding(40, 0, 40, 0);

        // Removing errors
        upi_error.setVisibility(View.INVISIBLE);
        banking_error.setVisibility(View.INVISIBLE);

        hospitalName = hospital_name.getText().toString().trim();
        ownerName = owner_name.getText().toString().trim();
        contactNum = contact_no.getText().toString().trim();

        contactNo = contact_no.getText().toString().trim();
        email = email_address.getText().toString().trim();
        city = location_city.getText().toString().trim().toLowerCase();
        state = location_state.getText().toString().trim().toLowerCase();
        country = location_country.getText().toString().trim().toLowerCase();
        address = location_address.getText().toString().trim();
        upiId = upi_id.getText().toString().trim();
        holder_name = ac_holder_name.getText().toString().trim();
        account_no = account_number.getText().toString().trim();
        ifsc = ifsc_code.getText().toString().trim();

        if (TextUtils.isEmpty(hospitalName)) {
            //scrollv.smoothScrollBy(0, -1000);
            scrollv.fullScroll(View.FOCUS_UP);
            hospital_name.setBackgroundResource(drawable.custom_edit_text_error);
            hospital_name.setPadding(40, 0, 40, 0);
            return false;
        }
        if (TextUtils.isEmpty(ownerName)) {
            scrollv.fullScroll(View.FOCUS_UP);
            owner_name.setBackgroundResource(drawable.custom_edit_text_error);
            owner_name.setPadding(40, 0, 40, 0);
            return false;
        }
        if (TextUtils.isEmpty(contactNo)) {
            contact_no.setBackgroundResource(drawable.custom_edit_text_error);
            contact_no.setPadding(40, 0, 40, 0);
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            email_address.setBackgroundResource(drawable.custom_edit_text_error);
            email_address.setPadding(40, 0, 40, 0);
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            location_city.setBackgroundResource(drawable.custom_edit_text_error);
            location_city.setPadding(40, 0, 40, 0);
            return false;
        }
        if (TextUtils.isEmpty(state)) {
            location_state.setBackgroundResource(drawable.custom_edit_text_error);
            location_state.setPadding(40, 0, 40, 0);
            return false;
        }
        if (TextUtils.isEmpty(country)) {
            location_country.setBackgroundResource(drawable.custom_edit_text_error);
            location_country.setPadding(40, 0, 40, 0);
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            location_address.setBackgroundResource(drawable.custom_edit_text_error);
            location_address.setPadding(40, 20, 40, 20);
            return false;
        }
        if (TextUtils.isEmpty(upiId)) {
            upi_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(holder_name)) {
            banking_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(account_no)) {
            banking_error.setVisibility(View.VISIBLE);
            return false;
        }
        if (TextUtils.isEmpty(ifsc)) {
            banking_error.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            location_country.setText(addresses.get(0).getCountryName());
            location_state.setText(addresses.get(0).getAdminArea());
            location_city.setText(addresses.get(0).getLocality());
            location_address.setText(addresses.get(0).getAddressLine(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public class autoScroll1 extends Thread {
        public void run() {
            scrollv.setSmoothScrollingEnabled(true);
            int t = 1;
            while (true) {
                t++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scrollv.smoothScrollBy(0, 1000);
                if (t == 5)
                    break;
            }

        }
    }

    public class autoScroll extends Thread {
        public void run() {
            scrollv.setSmoothScrollingEnabled(true);
            int t = 1;
            while (true) {
                t++;
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scrollv.smoothScrollBy(0, -1000);
                if (t == 5)
                    break;
            }

        }
    }

}