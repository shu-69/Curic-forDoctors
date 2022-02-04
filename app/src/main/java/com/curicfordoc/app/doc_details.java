package com.curicfordoc.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ipsec.ike.SaProposal;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.curicfordoc.app.database.DocDetail;
import com.curicfordoc.app.database.DoctorsDatabaseHandler;

public class doc_details extends AppCompatActivity {

    ImageView ProfileImage;
    TextView Name, Specialization, Experience, Fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(doc_details.this, R.color.doc_detailsColor));
        setContentView(R.layout.activity_doc_details);

        ProfileImage = findViewById(R.id.profileImg);
        Name = findViewById(R.id.docNameTextV);
        Specialization = findViewById(R.id.specTextV);
        Experience = findViewById(R.id.experience_textV);
        Fee = findViewById(R.id.fee_textV);

        findViewById(R.id.backCardV).setOnClickListener(v -> finish());

        String DocId = null;
        try {
            DocId = getIntent().getStringExtra("docId");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(DocId)){

            try {
                DocDetail detail = new DoctorsDatabaseHandler(doc_details.this).getDocDetail(DocId);

                ProfileImage.setImageBitmap(StringToBitMap(detail.getImage()));
                Name.setText(detail.getName());
                Specialization.setText(detail.getSpecialization());
                Experience.setText(detail.getExperience() + " yrs");
                Fee.setText(detail.getFee());


            } catch (Exception e) {
                Toast.makeText(this, "Doctor's profile not found!", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Doctor's profile not found!", Toast.LENGTH_SHORT).show();
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
}