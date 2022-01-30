package com.curicfordoc.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.curicfordoc.app.database.AppointmentDetail;
import com.curicfordoc.app.database.AppointmentsDatabaseHandler;
import com.curicfordoc.app.database.DocDetail;
import com.curicfordoc.app.database.DoctorsDatabaseHandler;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    CardView hospitalRegistrationMessage;
    LinearLayout appointmentsContainer;

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
        for (int i = 0; i < 2; i++) {
            AppointmentDetail detail = new AppointmentDetail();
            detail.setOrderId(i + "");
            detail.setPaymentId("this is payment Id");
            detail.setDocId("2343");
            detail.setAppointmentDate("12/02/2022");
            detail.setAppointmentTime("03:00am to 03:30am");
            detail.setPatientName("shu");
            detail.setPatientAge("21");
            detail.setPatientGender("Male");
            detail.setPatientContact("+91 7979958673");
            detail.setPatientEmail("shubhamkumardps10@gmail.com");
            detail.setPatientAddress("AP Colony, Bhatbigha, Near Modern Academy, Gaya, Bihar 823001");
            detail.setDoctorFee("400");
            detail.setPatientLoginMethod("gmail_users");
            detail.setPatientLoginId("shubhamkumardps10@gmail.com");
            detail.setStatus(2); // 0 : successful, 1 : cancelled

            ADHandler.addAppointment(detail);
        }

        showAppointmentDetails("334909310", "0");
        showAppointmentDetails("469664598", "0");
        showAppointmentDetails("510430864", "0");
        showAppointmentDetails("580538425", "0");
        showAppointmentDetails("580563842", "0");

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


        return view;
    }

    private void loadAllAppointments(String login_method, String userId) {
        db.collection("registered_doctors").document(login_method).collection(userId).document("appointments_received").collection("approved")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean taskIsNull = true;
                AppointmentsDatabaseHandler ADHandler = new AppointmentsDatabaseHandler(getContext());
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
                    detail.setStatus(Integer.parseInt(document.getString("status")));
                    try {
                        detail.setDone(ADHandler.getAppointmentDetail(document.getString("orderId")).getDone());
                        detail.setPinned(ADHandler.getAppointmentDetail(document.getString("orderId")).getPinned());
                    } catch (Exception e) {
                        detail.setDone(false);
                        detail.setPinned(false);
                    }


                    ADHandler.addAppointment(detail);
                    taskIsNull = false;
                }

                if (taskIsNull) {
                    ToggleShimmer(false);
                    showNoAppointmentsCard();
                } else {
                    ToggleShimmer(false);
                }
            }
        });
    }

    private void showAppointmentDetails(String DocID, String AppointmentOrderID) {
        View view = View.inflate(getContext(), R.layout.appointment_details_card, null);

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
                TopCornerImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_check_circle_20));
                TopCornerImage.setVisibility(View.VISIBLE);
                CancelCard.setVisibility(View.GONE);
            }

            if (isDone){
                DoneCardTextV.setText("UnMark as Done!");
            }

            DoneCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(DoneCardTextV.getText().toString().equals("Mark as Done!")){
                        AppointmentDetail newDetails = new AppointmentDetail();
                        newDetails.set
                    }else{

                    }
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

        appointmentsContainer = view.findViewById(R.id.todayAppointLinear);
    }

    private void ToggleShimmer(boolean Visibility) {
        CardView ShimmerCard = view.findViewById(R.id.loading_card);
        if (Visibility)
            ShimmerCard.setVisibility(View.VISIBLE);
        else
            ShimmerCard.setVisibility(View.GONE);
    }

    private void showNoAppointmentsCard() {
        TextView day_name = view.findViewById(R.id.day_name);
        TextView day_date = view.findViewById(R.id.day_date);
        CardView noAppointmentsCard = view.findViewById(R.id.no_appointments_card);

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



















