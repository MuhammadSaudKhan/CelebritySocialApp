package com.saud.celebrityapp;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stripe.android.PaymentConfiguration;

public class MyApp extends Application {
    private static final int CERTIFICATES = 100;

    @Override
    public void onCreate() {
        super.onCreate();
        FontRequest fontRequest = new FontRequest(
                "com.example.fontprovider",
                "com.example",
                "emoji compat Font Query",
                CERTIFICATES);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this,fontRequest);
        EmojiCompat.init(config);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_TYooMQauvdEDq54NiTphI7jx"
        );


    }
}
