package com.curicfordoc.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class TransactionHistory extends AppCompatActivity {

    ImageView profileImage;
    TextView userNameTextV;

    LinearLayout container;
    ConstraintLayout noDataAnimConstraint;

    SharedPreferences HospDetailsSP;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(TransactionHistory.this, R.color.transactionDetailsColor));
        setContentView(R.layout.activity_transaction_history);

        hook();

        HospDetailsSP = getSharedPreferences("HospDetails", Context.MODE_PRIVATE);

        Bitmap image = StringToBitMap(HospDetailsSP.getString("profileImageBitmap", " "));
        if (image != null)
            profileImage.setImageBitmap(image);

        userNameTextV.setText(HospDetailsSP.getString("docName", " "));

        addTransaction(getDrawable(R.drawable.arr_blue), "Amount received for order id_234lAS324SDF", "Transaction Date: 12/10/2021", "300", false);
        addTransaction(getDrawable(R.drawable.arr_green), "Credited to Bank Account No. ends with 2343", "Transaction Date: 13/10/2021", "200", true);
        addTransaction(getDrawable(R.drawable.arr_green), "Credited to Bank Account No. ends with 2343", "Transaction Date: 13/10/2021", "100", true);
        addTransaction(getDrawable(R.drawable.arr_blue), "Amount received order id_234lASwer4SDF", "Transaction Date: 15/10/2021", "300", false);

    }

    private void loadTransactions(String HospID){

    }

    private void hook() {
        profileImage = findViewById(R.id.profileImage);
        userNameTextV = findViewById(R.id.userNameTextV);

        container = findViewById(R.id.container_linear);
        noDataAnimConstraint = findViewById(R.id.noDataAnimConstraint);
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

    private void addTransaction(Drawable Image, String TransactionDescription, String TransactionDate, String TransactionAmount, boolean isDebited){
        View view = getLayoutInflater().inflate(R.layout.transaction_details_card, null);

        ImageView TransactionImage = view.findViewById(R.id.transactionImage);
        TextView TransactionDesc = view.findViewById(R.id.paymentDescTextV);
        TextView TransactionDt = view.findViewById(R.id.transactionDate);
        TextView TransactionAm = view.findViewById(R.id.transactionAmount);

        TransactionImage.setImageDrawable(Image);
        TransactionDesc.setText(TransactionDescription);
        TransactionDt.setText(TransactionDate);

        if(isDebited){
            TransactionAm.setTextColor(Color.parseColor("#35A726"));
            TransactionAm.setText("- ₹"+TransactionAmount);
        }else{
            TransactionAm.setTextColor(Color.parseColor("#3949AB"));
            TransactionAm.setText("+ ₹"+TransactionAmount);
        }

        container.addView(view);

    }
}














