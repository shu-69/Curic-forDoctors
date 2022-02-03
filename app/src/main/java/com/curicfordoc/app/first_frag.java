package com.curicfordoc.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.curicfordoc.app.database.AppointmentDetail;
import com.curicfordoc.app.database.AppointmentsDatabaseHandler;
import com.curicfordoc.app.database.DocDetail;
import com.curicfordoc.app.database.DoctorsDatabaseHandler;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import soup.neumorphism.NeumorphCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link first_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class first_frag extends Fragment {

    View view;
    LabeledSwitch toogleOpenclose;
    TextView topTextTodaysAppointmentsTextV, textOpenClose, day_date, day_name, total_appointments;
    ImageView expand;
    LottieAnimationView refresh;
    CardView hospitalRegistrationMessage, noAppointmentsCard, ShimmerCard, ScanAndCureCardV;
    LinearLayout appointmentsContainer;
    NeumorphCardView transactionCardV, updateHospDetailsCardV, manageDoctorsCardV;

    SharedPreferences HospDetailsSP;
    SharedPreferences.Editor editor;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static String login_method = "";

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

        HospDetailsSP = getContext().getSharedPreferences("HospDetails", Context.MODE_PRIVATE);
        editor = HospDetailsSP.edit();

        hook();
        toogler();

        new userDetails(getContext());

        AppointmentsDatabaseHandler ADHandler = new AppointmentsDatabaseHandler(getContext());
        for (int i = 0; i < 1; i++) {
            if (i == 0) {
                AppointmentDetail detail = new AppointmentDetail();
                detail.setOrderId(i + "");
                detail.setPaymentId("this is payment Id");
                detail.setDocId("334909310");
                detail.setAppointmentDate("03/02/2022");
                detail.setAppointmentTime("03:00am to 03:30am");
                detail.setPatientName("Shubham");
                detail.setPatientAge("21");
                detail.setPatientGender("Male");
                detail.setPatientContact("+91 7979958673");
                detail.setPatientEmail("shubhamkumardps10@gmail.com");
                detail.setPatientAddress("AP Colony, Bhatbigha, Near Modern Academy, Gaya, Bihar 823001");
                detail.setDoctorFee("400");
                detail.setPatientLoginMethod("gmail_users");
                detail.setPatientLoginId("shubhamkumardps10@gmail.com");
                // 0 : successful, 1 : cancelled, 2 : pending
                try {
                    detail.setStatus(ADHandler.getAppointmentDetail(i + "").getStatus());
                    detail.setDone(ADHandler.getAppointmentDetail(i + "").getDone());
                    detail.setPinned(ADHandler.getAppointmentDetail(i + "").getPinned());
                } catch (Exception e) {
                    detail.setStatus(2);
                    detail.setDone(false);
                    detail.setPinned(false);
                }
                try {
                    ADHandler.addAppointment(detail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AppointmentDetail detail = new AppointmentDetail();
                detail.setOrderId(i + "");
                detail.setPaymentId("this is payment Id");
                detail.setDocId("463229885");
                detail.setAppointmentDate("03/02/2022");
                detail.setAppointmentTime("03:00am to 03:30am");
                detail.setPatientName("Shubham");
                detail.setPatientAge("21");
                detail.setPatientGender("Male");
                detail.setPatientContact("+91 7979958673");
                detail.setPatientEmail("shubhamkumardps10@gmail.com");
                detail.setPatientAddress("AP Colony, Bhatbigha, Near Modern Academy, Gaya, Bihar 823001");
                detail.setDoctorFee("400");
                detail.setPatientLoginMethod("gmail_users");
                detail.setPatientLoginId("shubhamkumardps10@gmail.com");
                try {
                    detail.setStatus(ADHandler.getAppointmentDetail(i + "").getStatus());// 0 : successful, 1 : cancelled, 2 : pending
                    detail.setDone(ADHandler.getAppointmentDetail(i + "").getDone());
                    detail.setPinned(ADHandler.getAppointmentDetail(i + "").getPinned());
                } catch (Exception e) {
                    detail.setStatus(2);
                    detail.setDone(false);
                    detail.setPinned(false);
                }
                try {
                    ADHandler.addAppointment(detail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        if (checkIfHospitalRegistered()) {
            ToggleShimmer(true);
            loadAllAppointments(userDetails.login_method, userDetails.userId);
        } else
            hospitalRegistrationMessage.setVisibility(View.VISIBLE);


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
        expand.setOnClickListener(v ->{
            if (appointmentsContainer.getVisibility() == View.VISIBLE){
                appointmentsContainer.setVisibility(View.GONE);
                expand.setImageDrawable(getContext().getDrawable(R.drawable.ic_expand_more_30));
            }else{
                appointmentsContainer.setVisibility(View.VISIBLE);
                expand.setImageDrawable(getContext().getDrawable(R.drawable.ic_expand_less_30));
            }
        });
        topTextTodaysAppointmentsTextV.setOnClickListener(v ->{
            if (appointmentsContainer.getVisibility() == View.VISIBLE){
                appointmentsContainer.setVisibility(View.GONE);
                expand.setImageDrawable(getContext().getDrawable(R.drawable.ic_expand_more_30));
            }else{
                appointmentsContainer.setVisibility(View.VISIBLE);
                expand.setImageDrawable(getContext().getDrawable(R.drawable.ic_expand_less_30));
            }
        });
        transactionCardV.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), TransactionHistory.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        updateHospDetailsCardV.setOnClickListener(v ->{
            if (HospDetailsSP.contains("hospName")) {
                Intent intent = new Intent(getActivity(), clinic_details.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        manageDoctorsCardV.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), ManageDoctors.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        ScanAndCureCardV.setOnClickListener(v ->{
            //BottomNavigationView bottom_nav = view.findViewById(R.id.bottomnav);
            homepage.bottom_nav.setSelectedItemId(R.id.third_frag);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new third_frag()).commit();
        });



        return view;
    }

    private void loadAllAppointments(String login_method, String userId) {
        db.collection("registered_doctors").document(login_method).collection(userId).document("appointments_received").collection("approved")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                AppointmentsDatabaseHandler ADHandler = new AppointmentsDatabaseHandler(getContext());
                for (QueryDocumentSnapshot document : task.getResult()) {
                    AppointmentDetail detail = new AppointmentDetail();
                    detail.setOrderId(document.getString("orderId"));
                    detail.setPaymentId(document.getString("paymentId"));
                    detail.setDocId(document.getString("docId"));
                    detail.setAppointmentDate(document.getString("appointmentDate"));
                    detail.setAppointmentTime(document.getString("appointmentTime"));
                    detail.setPatientName(document.getString("patientName"));
                    detail.setPatientAge(document.getString("patientAge"));
                    detail.setPatientGender(document.getString("patientGender"));
                    detail.setPatientContact(document.getString("patientPhone"));
                    detail.setPatientEmail(document.getString("patientEmail"));
                    detail.setPatientAddress(document.getString("patientAddress"));
                    detail.setDoctorFee(document.getString("doctorFee"));
                    detail.setPatientLoginMethod(document.getString("patientLoginMethod"));
                    detail.setPatientLoginId(document.getString("patientLoginId"));
                    detail.setStatus(Integer.parseInt(String.valueOf(document.get("status"))));
                    try {
                        detail.setDone(new AppointmentsDatabaseHandler(getContext()).getAppointmentDetail(document.getString("orderId")).getDone());
                        detail.setPinned(new AppointmentsDatabaseHandler(getContext()).getAppointmentDetail(document.getString("orderId")).getPinned());
                    } catch (Exception e) {
                        detail.setDone(false);
                        detail.setPinned(false);
                    }
                    try {
                        new AppointmentsDatabaseHandler(getContext()).addAppointment(detail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                loadAppointmentsFromDatabase();
            }
        });
    }

    private void loadAppointmentsFromDatabase() {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        int count = 0; // For counting total appointments;
        try {
            boolean checkIsNull = true;

            List<AppointmentDetail> allAppointments = new AppointmentsDatabaseHandler(getContext()).getAllAppointments();

            ToggleShimmer(false);
            for (AppointmentDetail detail : allAppointments) {
                if (detail.getAppointmentDate().equals(currentDate)) {
                    count++;
                    checkIsNull = false;
                    showAppointmentDetails(detail.getDocId(), detail.getOrderId());
                }
            }
            total_appointments.setText(count + "");
            if (checkIsNull) {
                showNoAppointmentsCard();
            }
        } catch (Exception e) {
            ToggleShimmer(false);
        }

    }

    private void showAppointmentDetails(String DocID, String AppointmentOrderID) {

        View view = View.inflate(getContext(), R.layout.appointment_details_card, null);

        MaterialCardView ParentCard = view.findViewById(R.id.parentCard);

        ImageView DocProfile = view.findViewById(R.id.docProfileImage);
        TextView DocName = view.findViewById(R.id.doctor_name);
        TextView DocSpecialization = view.findViewById(R.id.doctor_specialization);

        TextView AppointmentDate = view.findViewById(R.id.appointmentDate);
        TextView AppointmentTime = view.findViewById(R.id.appointmentTime);

        View TopCornerTriangleView = view.findViewById(R.id.topCornerTriangleView);
        ImageView TopCornerImage = view.findViewById(R.id.topStatusImageV);

        TextView PatientName = view.findViewById(R.id.patientName);
        TextView PatientAge = view.findViewById(R.id.ageTextV);
        TextView PatientGender = view.findViewById(R.id.genderTextV);
        TextView PatientContact = view.findViewById(R.id.phoneTextV);
        TextView PatientEmail = view.findViewById(R.id.emailTextV);
        TextView PatientAddress = view.findViewById(R.id.addressTextV);

        CardView DoneCard = view.findViewById(R.id.doneCardV);
        TextView DoneCardTextV = view.findViewById(R.id.doneCardTextV);
        CardView CancelCard = view.findViewById(R.id.cancelAppointmentCardV);
        CardView DeleteCard = view.findViewById(R.id.deleteCardView);
        CardView MessageCard = view.findViewById(R.id.appointmentMessageCard);
        TextView Message = view.findViewById(R.id.messageTextV);


        // Setting Doctor's Details
        try {
            DocDetail docDetail = new DoctorsDatabaseHandler(getContext()).getDocDetail(DocID);

            DocProfile.setImageBitmap(StringToBitMap(docDetail.getImage()));
            DocName.setText(docDetail.getName());
            DocSpecialization.setText(docDetail.getSpecialization());

        } catch (Exception e) {
            // TODO : If doctor's profile is not available in sql database;
        }

        // Setting Appointment's Details
        try {
            AppointmentDetail detail = new AppointmentsDatabaseHandler(getContext()).getAppointmentDetail(AppointmentOrderID);

            AppointmentDate.setText(detail.getAppointmentDate());
            AppointmentTime.setText(detail.getAppointmentTime());

            PatientName.setText(detail.getPatientName());
            PatientAge.setText(detail.getPatientAge());
            PatientGender.setText(detail.getPatientGender());
            PatientContact.setText(detail.getPatientContact());
            PatientEmail.setText(detail.getPatientEmail());
            PatientAddress.setText(detail.getPatientAddress());

            int Status = detail.getStatus();
            boolean isDone = detail.getDone();

            // 0 : successful, 1 : cancelled, 2 : pending
            if (Status == 1) {
                TopCornerTriangleView.setVisibility(View.VISIBLE);
                TopCornerImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_cancel_20));
                TopCornerImage.setVisibility(View.VISIBLE);
                CancelCard.setVisibility(View.GONE);
                DoneCard.setVisibility(View.INVISIBLE);
                MessageCard.setVisibility(View.VISIBLE);
                Message.setText("This Appointment has been cancelled and the fee amount has been refunded.");
                DeleteCard.setVisibility(View.VISIBLE);
                ParentCard.setStrokeColor(Color.parseColor("#B84040"));
            } // TODO : Else if Status is 0.

            if (isDone) {
                DoneCardTextV.setText("Unmark as Done!");
                TopCornerTriangleView.setVisibility(View.VISIBLE);
                TopCornerImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_check_circle_20));
                TopCornerImage.setVisibility(View.VISIBLE);
            }

            DoneCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DoneCardTextV.getText().toString().equals("Mark as Done!")) {
                        TopCornerTriangleView.setVisibility(View.VISIBLE);
                        TopCornerImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_check_circle_20));
                        TopCornerImage.setVisibility(View.VISIBLE);

                        AppointmentDetail newDetails = new AppointmentDetail();

                        newDetails.setOrderId(detail.getOrderId());
                        newDetails.setDocId(detail.getDocId());
                        newDetails.setPaymentId(detail.getPaymentId());
                        newDetails.setAppointmentTime(detail.getAppointmentTime());
                        newDetails.setAppointmentDate(detail.getAppointmentDate());
                        newDetails.setPatientName(detail.getPatientName());
                        newDetails.setPatientAge(detail.getPatientAge());
                        newDetails.setPatientGender(detail.getPatientGender());
                        newDetails.setPatientContact(detail.getPatientContact());
                        newDetails.setPatientEmail(detail.getPatientEmail());
                        newDetails.setPatientAddress(detail.getPatientAddress());
                        newDetails.setDoctorFee(detail.getDoctorFee());
                        newDetails.setPatientLoginMethod(detail.getPatientLoginMethod());
                        newDetails.setPatientLoginMethod(detail.getPatientLoginId());
                        newDetails.setStatus(detail.getStatus());
                        newDetails.setDone(true);
                        newDetails.setPinned(detail.getPinned());

                        AppointmentsDatabaseHandler handler = new AppointmentsDatabaseHandler(getContext());
                        handler.updateAppointmentDetails(newDetails);

                        DoneCardTextV.setText("Unmark as Done!");

                        // TODO ::

                    } else {
                        TopCornerTriangleView.setVisibility(View.INVISIBLE);
                        TopCornerImage.setVisibility(View.INVISIBLE);
                        AppointmentDetail newDetails = new AppointmentDetail();

                        newDetails.setOrderId(detail.getOrderId());
                        newDetails.setDocId(detail.getDocId());
                        newDetails.setPaymentId(detail.getPaymentId());
                        newDetails.setAppointmentTime(detail.getAppointmentTime());
                        newDetails.setAppointmentDate(detail.getAppointmentDate());
                        newDetails.setPatientName(detail.getPatientName());
                        newDetails.setPatientAge(detail.getPatientAge());
                        newDetails.setPatientGender(detail.getPatientGender());
                        newDetails.setPatientContact(detail.getPatientContact());
                        newDetails.setPatientEmail(detail.getPatientEmail());
                        newDetails.setPatientAddress(detail.getPatientAddress());
                        newDetails.setDoctorFee(detail.getDoctorFee());
                        newDetails.setPatientLoginMethod(detail.getPatientLoginMethod());
                        newDetails.setPatientLoginMethod(detail.getPatientLoginId());
                        newDetails.setStatus(detail.getStatus());
                        newDetails.setDone(false);
                        newDetails.setPinned(detail.getPinned());

                        AppointmentsDatabaseHandler handler = new AppointmentsDatabaseHandler(getContext());
                        handler.updateAppointmentDetails(newDetails);

                        DoneCardTextV.setText("Mark as Done!");
                    }
                }
            });

            DeleteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressBar DeletingProgress = view.findViewById(R.id.deleting_progress);
                    ImageView DeleteIcon = view.findViewById(R.id.delete_icon);
                    DeleteIcon.setVisibility(View.INVISIBLE);
                    DeletingProgress.setVisibility(View.VISIBLE);
                    new userDetails(getContext());
                    db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("appointments_received")
                            .collection("approved").document(detail.getOrderId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            appointmentsContainer.removeAllViews();
                            new AppointmentsDatabaseHandler(getContext()).deleteAppointment(detail.getOrderId());
                            loadAppointmentsFromDatabase();
                            DeletingProgress.setVisibility(View.INVISIBLE);
                            DeleteIcon.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            DeletingProgress.setVisibility(View.INVISIBLE);
                            DeleteIcon.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            CancelCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Are your sure you want to cancel this appointment?");
                    alertDialog.setMessage("This cannot be undo." + "\n\n"
                            + "\u2022 Patient will get their money refunded." + "\n" // \u2022 : Bullet
                            + "\u2022 Patient will be notified about the Appointment cancellation." + "\n");
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setPositiveButton("CANCEL & REFUND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();

                            Map<String, Object> note = new HashMap<>();

                            note.put("orderId", detail.getOrderId());
                            note.put("paymentId", detail.getPaymentId());
                            note.put("docId", detail.getDocId());
                            note.put("appointmentDate", detail.getAppointmentDate());
                            note.put("appointmentTime", detail.getAppointmentTime());
                            note.put("patientName", detail.getPatientName());
                            note.put("patientAge", detail.getPatientAge());
                            note.put("patientGender", detail.getPatientGender());
                            note.put("patientPhone", detail.getPatientContact());
                            note.put("patientEmail", detail.getPatientEmail());
                            note.put("patientAddress", detail.getPatientAddress());
                            note.put("doctorFee", detail.getDoctorFee());
                            note.put("patientLoginMethod", detail.getPatientLoginMethod());
                            note.put("patientLoginId", detail.getPatientLoginId());
                            note.put("status", 1);

                            db.collection("registered_doctors").document(userDetails.login_method).collection(userDetails.userId).document("appointments_received")
                                    .collection("approved").document(detail.getOrderId()).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    AppointmentDetail newDetails = new AppointmentDetail();

                                    newDetails.setOrderId(detail.getOrderId());
                                    newDetails.setDocId(detail.getDocId());
                                    newDetails.setPaymentId(detail.getPaymentId());
                                    newDetails.setAppointmentTime(detail.getAppointmentTime());
                                    newDetails.setAppointmentDate(detail.getAppointmentDate());
                                    newDetails.setPatientName(detail.getPatientName());
                                    newDetails.setPatientAge(detail.getPatientAge());
                                    newDetails.setPatientGender(detail.getPatientGender());
                                    newDetails.setPatientContact(detail.getPatientContact());
                                    newDetails.setPatientEmail(detail.getPatientEmail());
                                    newDetails.setPatientAddress(detail.getPatientAddress());
                                    newDetails.setDoctorFee(detail.getDoctorFee());
                                    newDetails.setPatientLoginMethod(detail.getPatientLoginMethod());
                                    newDetails.setPatientLoginId(detail.getPatientLoginId());
                                    newDetails.setStatus(1); // 0 : done, 1 : cancelled, 2 : pending
                                    newDetails.setDone(detail.getDone());
                                    newDetails.setPinned(detail.getPinned());

                                    new AppointmentsDatabaseHandler(getContext()).updateAppointmentDetails(newDetails);

                                    TopCornerTriangleView.setVisibility(View.VISIBLE);
                                    TopCornerImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_cancel_20));
                                    TopCornerImage.setVisibility(View.VISIBLE);
                                    CancelCard.setVisibility(View.GONE);
                                    DoneCard.setVisibility(View.INVISIBLE);
                                    MessageCard.setVisibility(View.VISIBLE);
                                    Message.setText("This Appointment has been cancelled and the fee amount has been refunded.");
                                    DeleteCard.setVisibility(View.VISIBLE);
                                    ParentCard.setStrokeColor(Color.parseColor("#B84040"));

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }).show();
                }
            });

            appointmentsContainer.addView(view);

        } catch (Exception e) {
            // TODO : If appointment details are not available in sql database;
        }

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
            try {
                editor.putString("profileImageBitmap", BitMapToString(result));
            } catch (Exception e) {
            }
            editor.apply();
        }
    }

    public String BitMapToString(Bitmap bitmap) throws Exception {
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
        hospitalRegistrationMessage = view.findViewById(R.id.hospital_registration_message_card);
        topTextTodaysAppointmentsTextV = view.findViewById(R.id.textView62);

        day_name = view.findViewById(R.id.day_name);
        day_date = view.findViewById(R.id.day_date);
        noAppointmentsCard = view.findViewById(R.id.no_appointments_card);
        total_appointments = view.findViewById(R.id.total_appointments);
        expand = view.findViewById(R.id.expand_image);

        transactionCardV = view.findViewById(R.id.transaction_history_cardv);
        updateHospDetailsCardV = view.findViewById(R.id.update_hosp_details_cardv);
        manageDoctorsCardV = view.findViewById(R.id.manage_doctors_cardv);

        ScanAndCureCardV = view.findViewById(R.id.scan_and_cure_card);
        ShimmerCard = view.findViewById(R.id.loading_card);
        appointmentsContainer = view.findViewById(R.id.todayAppointLinear);
    }

    private void ToggleShimmer(boolean Visibility) {
        if (Visibility)
            ShimmerCard.setVisibility(View.VISIBLE);
        else
            ShimmerCard.setVisibility(View.GONE);
    }

    private void showNoAppointmentsCard() {
        Log.d("Excep", "Hii");

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(year, month, day - 1);
        String dayOfWeek = simpledateformat.format(date);

        day_name.setText(dayOfWeek);
        day_date.setText(String.valueOf(day));
        noAppointmentsCard.setVisibility(View.VISIBLE);

    }

    private boolean checkIfHospitalRegistered() {

        if (HospDetailsSP.contains("hospName"))
            return true;

        return false;
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



















