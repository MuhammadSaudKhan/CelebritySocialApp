package com.saud.celebrityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saud.celebrityapp.Database.CollectionNames;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.sufficientlysecure.htmltextview.HtmlFormatter;
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder;

public class ScreenSaver extends AppCompatActivity implements Target{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_screen_saver);

        FirebaseFirestore.getInstance().collection("home_screen_image").document("image").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final String quote=documentSnapshot.getString("quotation");
                String url=documentSnapshot.getString("url");
                Picasso.get().load(url).into(ScreenSaver.this);
                Spanned formattedHtml = HtmlFormatter.formatHtml(new HtmlFormatterBuilder().setHtml(" “"+quote+"”"));
                SpannableString str = new SpannableString(formattedHtml);
                str.setSpan(new BackgroundColorSpan(0x99000000), 0, str.length(), 0);
                str.setSpan(new RelativeSizeSpan(2f), 0, 1, 0  );
                str.setSpan(new RelativeSizeSpan(2f), str.length()-1, str.length(), 0  );
                StaticVariables.home_quotes=str;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScreenSaver.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        startActivity(new Intent(ScreenSaver.this,MainActivity.class));
        finish();
        StaticVariables.home_image=bitmap;
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}