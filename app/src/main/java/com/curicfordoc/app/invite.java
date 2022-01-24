package com.curicfordoc.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class invite extends AppCompatActivity {

    ImageView whatsappInvite, moreOptions, linkCopy, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        hook();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        linkCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(getApplicationContext(), getString(R.string.playstore_link));
            }
        });
        whatsappInvite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                //whatsappIntent.setType("text/plain");

                BitmapDrawable drawable = (BitmapDrawable) getDrawable(R.drawable.invite_banner);
                Bitmap bitmap = drawable.getBitmap();
                try{
                    String bitmappath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
                    Uri uri = Uri.parse(bitmappath);
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hey, install Curic and get Medical Services near you." + "\n" + getString(R.string.playstore_link));
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.setType("image/png");
                    startActivity(whatsappIntent);
                }catch(Exception e){
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hey, install Curic and get Medical Services near you." + "\n" + getString(R.string.playstore_link));
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.setType("text/plain");
                    startActivity(whatsappIntent);
                }



            }
        });
        moreOptions.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent share = new Intent(Intent.ACTION_SEND);
                @SuppressLint("UseCompatLoadingForDrawables")
                BitmapDrawable drawable = (BitmapDrawable) getDrawable(R.drawable.invite_banner);
                Bitmap bitmap = drawable.getBitmap();
                try {
                    String bitmappath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
                    Uri uri = Uri.parse(bitmappath);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setType("image/png");
                    share.putExtra(Intent.EXTRA_TEXT, "Hey, i am inviting you to install Curic forDoctors." + "\n" + getString(R.string.playstore_link));
                    startActivity(Intent.createChooser(share, "Share using"));
                }catch(Exception e){
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, "Hey, i am inviting you to install Curic forDoctors." + "\n" + getString(R.string.playstore_link));
                    startActivity(Intent.createChooser(share, "Share using"));
                }

            }
        });
    }

    private void hook() {
        whatsappInvite = (ImageView) findViewById(R.id.inviteViaWhatsapp);
        moreOptions = (ImageView) findViewById(R.id.moreOptionButton);
        linkCopy = (ImageView) findViewById(R.id.copyLinkButton);
        backButton = (ImageView) findViewById(R.id.backButton);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
            Toast.makeText(invite.this, "Link copied to Clipboard", Toast.LENGTH_SHORT).show();
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(invite.this, "Link copied to Clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}