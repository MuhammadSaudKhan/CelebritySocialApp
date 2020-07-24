package com.saud.celebrityapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.Model.FileModel;
import com.saud.celebrityapp.PhotoViewerActivity;
import com.saud.celebrityapp.R;
import com.saud.celebrityapp.StaticVariables;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.viewholder> implements Target{
    private static final String TAG = "ImagesAdapter";
    ArrayList<FileModel> list;
    Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    public OnUnlockListner listner;
    public ImagesAdapter(Context context,ArrayList<FileModel> list) {
        this.list = list;
        this.context=context;
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row,null,false);

        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {
        holder.textView.setText(list.get(position).getName());

        holder.lock.setVisibility(View.VISIBLE);
        holder.btnUnlock.setVisibility(View.VISIBLE);
        db.collection(CollectionNames.col_admin_profile).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    Picasso.get().load(documentSnapshot.get("image_url").toString()).into(holder.profile_image);
                    holder.profile_name.setText(documentSnapshot.get("name").toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        Picasso.get()
                .load(list.get(position).getUrl())
                .transform(new BlurTransformation(holder.itemView.getContext(), 50, 1))
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG,e.getMessage());
                    }
                });
        holder.btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.OnSuccess(list,holder,position);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection(CollectionNames.col_user_images).whereEqualTo(CollectionNames.user_images.field_image_url,list.get(position).getUrl()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String userid=documentSnapshot.getString(CollectionNames.user_images.field_user_id);
                            if (auth.getCurrentUser()!=null)
                                if (userid.toLowerCase().equals(auth.getUid().toLowerCase())){

                                    Picasso.get()
                                            .load(list.get(position).getUrl())
                                            .into(ImagesAdapter.this);
                                }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(holder.itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        db.collection(CollectionNames.col_user_images).whereEqualTo(CollectionNames.user_images.field_image_url,list.get(position).getUrl()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                     for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                         String userid=documentSnapshot.getString(CollectionNames.user_images.field_user_id);
                         if (auth.getCurrentUser()!=null)
                         if (userid.toLowerCase().equals(auth.getUid().toLowerCase())){
                             holder.btnUnlock.setVisibility(View.GONE);
                             holder.lock.setVisibility(View.GONE);
                             Picasso.get()
                                     .load(list.get(position).getUrl())
                                     .into(holder.imageView, new Callback() {
                                         @Override
                                         public void onSuccess() {
                                             holder.progressBar.setVisibility(View.GONE);
                                         }

                                         @Override
                                         public void onError(Exception e) {
                                             Log.e(TAG,e.getMessage());
                                         }
                                     });
                         }else{
                             Log.e(TAG,"saud");

                             Picasso.get()
                                     .load(list.get(position).getUrl())
                                     .transform(new BlurTransformation(holder.itemView.getContext(), 50, 1))
                                     .into(holder.imageView, new Callback() {
                                         @Override
                                         public void onSuccess() {
                                             holder.progressBar.setVisibility(View.GONE);
                                         }

                                         @Override
                                         public void onError(Exception e) {
                                             Log.e(TAG,e.getMessage());
                                         }
                                     });
                         }
                     }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
                Picasso.get()
                        .load(list.get(position).getUrl())
                        .transform(new BlurTransformation(holder.itemView.getContext(), 50, 1))
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG,e.getMessage());
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        context.startActivity(new Intent(context, PhotoViewerActivity.class));
        StaticVariables.photo_image=bitmap;
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    public class viewholder extends RecyclerView.ViewHolder{
      public   TextView textView,profile_name;
      public   ImageView imageView,lock;
      public   Button btnUnlock;
      public   ProgressBar progressBar;
      public   CircleImageView profile_image;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressBar);
            textView=itemView.findViewById(R.id.text);
            imageView=itemView.findViewById(R.id.imageView);
            lock=itemView.findViewById(R.id.lock);
            btnUnlock=itemView.findViewById(R.id.btnUnlock);
            profile_image=itemView.findViewById(R.id.profile_image);
            profile_name=itemView.findViewById(R.id.profile_name);
        }
    }
    public interface OnUnlockListner{
        void OnSuccess(ArrayList<FileModel> list,viewholder holder,int position);
    }
    public void setOnUnlockListner(OnUnlockListner listner){
        this.listner=listner;
    }
}
