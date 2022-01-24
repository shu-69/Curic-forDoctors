package com.curicfordoc.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xmlpull.v1.XmlPullParser;

import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link third_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class third_frag extends Fragment {

    TextView hospName, ownerName, wrng_txt, userIdTextV, noRatingTextV;
    View view;
    ImageView verification_badge, profileImage;
    CardView updateDetails_card, ManageDoctorsCard, TransactionHistoryCard, InviteCard, FeedbackCard, RateUsCard, HelpAndSupportCard, BugReportCard, AboutCuricCard;
    CardView profileImageCard, userDetailsCard, hospitalRegistrationCard, uploadDetailsCard;
    LinearLayout ratingLayout;
    ConstraintLayout fundsDetailsLayout;

    SharedPreferences HospDetailsSP;
    SharedPreferences.Editor editor;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public third_frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment third_frag.
     */
    // TODO: Rename and change types and number of parameters
    public static third_frag newInstance(String param1, String param2) {
        third_frag fragment = new third_frag();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_third_frag, container, false);
        hook();
        HospDetailsSP = getContext().getSharedPreferences("HospDetails", Context.MODE_PRIVATE);
        editor = HospDetailsSP.edit();
        new userDetails().initialize(getContext());
        new userDetails().onCreate();

        setUserID(true, userDetails.userId, userIdTextV);
        setRating(getContext().getDrawable(R.drawable.rating_full_star), getContext().getDrawable(R.drawable.rating_half_star),
                Float.parseFloat(HospDetailsSP.getString("ratingValue", "0.0")), ratingLayout);

        if (HospDetailsSP.contains("hospName")) {
            hospName.setText(HospDetailsSP.getString("hospName", ""));
            ownerName.setText(HospDetailsSP.getString("ownerName", ""));
            checkUserVerifiedOrNot(HospDetailsSP.getBoolean("isVerified", false));
            Bitmap image = StringToBitMap(HospDetailsSP.getString("profileImageBitmap", " "));
            if (image != null)
                profileImage.setImageBitmap(image);
        } else {
            hospitalRegistrationCard.setVisibility(View.VISIBLE);
            userDetailsCard.setVisibility(View.GONE);
            fundsDetailsLayout.setVisibility(View.GONE);
            uploadDetailsCard.setOnClickListener(v -> startActivity(new Intent(getContext(), clinic_details.class)));
        }

        // All Card view listeners
        allCardListener();

        return view;
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

    private void checkUserVerifiedOrNot(boolean flag) {
        if (flag) {
            wrng_txt.setVisibility(View.GONE);
            verification_badge.setVisibility(View.VISIBLE);
        } else {
            // If flag is false then check isVerified from database and update in SharedPreference;

            wrng_txt.setVisibility(View.VISIBLE);
            verification_badge.setVisibility(View.INVISIBLE);

            db.collection("registered_doctors").document(HospDetailsSP.getString("country", " ")).collection(HospDetailsSP.getString("state", " ")).
                    document(HospDetailsSP.getString("city", " ")).collection(userDetails.userId).document("hospital_details").get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists())
                                if (documentSnapshot.getBoolean("details_verified")) {
                                    editor.putBoolean("isVerified", true);
                                    editor.apply();
                                    wrng_txt.setVisibility(View.GONE);
                                    verification_badge.setVisibility(View.VISIBLE);
                                }
                        }
                    });
        }
    }

    private void allCardListener() {
        updateDetails_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HospDetailsSP.contains("hospName")) {
                    Intent intent = new Intent(getActivity(), clinic_details.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        ManageDoctorsCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManageDoctors.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        TransactionHistoryCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionHistory.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        InviteCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), invite.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        FeedbackCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), helpAndSupport.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }

    private void socialMediaListener() {
        view.findViewById(R.id.fb_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.fbHandle)));
                startActivity(browserIntent);
            }
        });

        view.findViewById(R.id.twitter_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitterHandle)));
                startActivity(browserIntent);
            }
        });

        view.findViewById(R.id.insta_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instaHandle)));
                startActivity(browserIntent);
            }
        });
    }

    private void hook() {
        hospName = (TextView) view.findViewById(R.id.hospital_name);
        ownerName = (TextView) view.findViewById(R.id.doctor_name);
        verification_badge = (ImageView) view.findViewById(R.id.verified_badge);
        wrng_txt = (TextView) view.findViewById(R.id.wrng_msg_txt);
        profileImage = view.findViewById(R.id.profileImage);
        userIdTextV = view.findViewById(R.id.userIdTextV);
        noRatingTextV = view.findViewById(R.id.noRatingText);

        updateDetails_card = view.findViewById(R.id.updateDetails_card);
        ManageDoctorsCard = view.findViewById(R.id.manageDoctors_card);
        TransactionHistoryCard = view.findViewById(R.id.transactionHistory);
        InviteCard = view.findViewById(R.id.inviteCard);
        FeedbackCard = view.findViewById(R.id.feedbackCard);
        RateUsCard = view.findViewById(R.id.rateUsCard);
        HelpAndSupportCard = view.findViewById(R.id.helpAndSupportCard);
        BugReportCard = view.findViewById(R.id.bugReportCard);
        AboutCuricCard = view.findViewById(R.id.aboutCuricCard);


        profileImageCard = view.findViewById(R.id.profileImageCard);
        ratingLayout = view.findViewById(R.id.ratingLayout);
        userDetailsCard = view.findViewById(R.id.userDetailsCard);
        hospitalRegistrationCard = view.findViewById(R.id.hospital_registration_message_card);
        uploadDetailsCard = view.findViewById(R.id.upload_details_card);
        fundsDetailsLayout = view.findViewById(R.id.secCardConstraint);
    }

    private void setUserID(boolean isHidden, String ID, TextView TextView) {
        if (isHidden) {
            int start = (int) (ID.length() / 5);
            int end = ID.length() - start;
            String tempID = "";
            for (int i = 0; i < ID.length(); i++)
                if (ID.charAt(i) == '@')
                    end = i - 2;
            for (int i = 0; i < ID.length(); i++) {
                if (i >= start && i < end) {
                    tempID += "*";
                } else {
                    tempID += ID.charAt(i);
                }
            }
            TextView.setText(tempID);
        } else {
            TextView.setText(ID);
        }
    }

    private void setRating(Drawable FullStar, Drawable HalfStar, float RatingValue, LinearLayout RatingLayout) {
//        ImageView ImageView = new ImageView(getContext());
//        ImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        int fullStars = (int) RatingValue;

        if (RatingLayout.getParent() != null) {
            //((ViewGroup) RatingLayout.getParent()).removeView(RatingLayout.getRootView());
            RatingLayout.removeView(RatingLayout.getRootView());
        }

        if (RatingValue == 0) {
            noRatingTextV.setVisibility(View.VISIBLE);
        } else if (fullStars == RatingValue) {
            for (int i = 0; i < fullStars; i++) {
                View view = getLayoutInflater().inflate(R.layout.simple_image_view, null);
                ImageView imageView = view.findViewById(R.id.image);
                imageView.setImageDrawable(FullStar);
                RatingLayout.addView(view);
            }
        } else {
            for (int i = 0; i < fullStars; i++) {
                View view = getLayoutInflater().inflate(R.layout.simple_image_view, null);
                ImageView imageView = view.findViewById(R.id.image);
                imageView.setImageDrawable(FullStar);
                RatingLayout.addView(view);
            }
            View view = getLayoutInflater().inflate(R.layout.simple_image_view, null);
            ImageView imageView = view.findViewById(R.id.image);
            imageView.setImageDrawable(HalfStar);
            RatingLayout.addView(view);
        }
    }
}















