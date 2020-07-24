package com.saud.celebrityapp.ui.home;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saud.celebrityapp.R;
import com.saud.celebrityapp.StaticVariables;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlFormatter;
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder;


public class HomeFragment extends Fragment {
    TextView quotation;
    ImageView imageView;
    //private FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        imageView=view.findViewById(R.id.bg_image);
        quotation=view.findViewById(R.id.home_text);
        imageView.setImageBitmap(StaticVariables.home_image);
        quotation.setText(StaticVariables.home_quotes);
//        db.collection("home_screen_image").document("image").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                String url=documentSnapshot.getString("url");
//                final String quote=documentSnapshot.getString("quotation");
//                Picasso.get().load(url).into(imageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Spanned formattedHtml = HtmlFormatter.formatHtml(new HtmlFormatterBuilder().setHtml(" “"+quote+"”"));
//                        SpannableString str = new SpannableString(formattedHtml);
//                        str.setSpan(new BackgroundColorSpan(0x99000000), 0, str.length(), 0);
//                        str.setSpan(new RelativeSizeSpan(2f), 0, 1, 0  );
//                        str.setSpan(new RelativeSizeSpan(2f), str.length()-1, str.length(), 0  );
//                        quotation.setText(str);
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//                });
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });

        //You're constantly being told you're not pretty... you're not this not that. Love yourself and your body, because you're beautiful the way you are.
        // Inflate the layout for this fragment
        return view;
    }
}