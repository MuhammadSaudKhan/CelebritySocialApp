package com.saud.celebrityapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.Model.FileModel;
import com.saud.celebrityapp.R;

import com.saud.celebrityapp.video_view;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.viewholder> {
    ArrayList<FileModel> list;
    public onPremiumclickListner listner;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    public VideosAdapter(Context context, ArrayList<FileModel> list)
    {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row,null,false);

        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {

        holder.textView.setText(list.get(position).getName());
        loadVideos(list.get(position).getId(),holder.imageView);
        db.collection(CollectionNames.col_admin_profile).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listner.Onclick(holder.rel,list.get(position).getUrl(),Double.parseDouble(list.get(position).getPrice()),list,position,holder);

            }
        });
        holder.rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.Onclick(holder.rel,list.get(position).getUrl(),Double.parseDouble(list.get(position).getPrice()),list,position,holder);


            }
        });
       if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        db.collection(CollectionNames.col_user_videos)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    return;
                }
                for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    try {
                        String userInDb=documentSnapshot.getString("user_id");
                        String v_url=documentSnapshot.getString("video_url");

                        if (TextUtils.equals(FirebaseAuth.getInstance().getUid(),userInDb) && TextUtils.equals(list.get(position).getUrl(),v_url)){
                            Log.e("Saud khan",userInDb);
                            holder.rel.setVisibility(View.GONE);
                            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent((Activity) context, video_view.class);
                                    intent.putExtra("uri",list.get(position).getUrl());
                                    context.startActivity(intent);
                                }
                            });
                        }

                    }catch (Exception e){
                       Log.e(TAG,e.getMessage());
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }
    private void loadVideos(String id, final ImageView imageView) {
        db.collection("thumbnail").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try{
                    String url=documentSnapshot.get("thumbnail_url").toString();
                    Picasso.get().load(url).into(imageView);
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
        public TextView textView,profile_name;
        public ImageView imageView;
        public RelativeLayout rel;
        public ImageView btnPlay;
        CircleImageView profile_image;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.text);
            imageView=itemView.findViewById(R.id.image);
            rel=itemView.findViewById(R.id.rel);
            btnPlay=itemView.findViewById(R.id.btn_play);
            profile_image=itemView.findViewById(R.id.profile_image);
            profile_name=itemView.findViewById(R.id.profile_name);
        }
    }
    public interface onPremiumclickListner{
        void Onclick(RelativeLayout relativeLayout, String url, double price, List<FileModel> list, int position,viewholder holder);
    }
    public void setOnPremiumClickLisnter(onPremiumclickListner lisnter){
        this.listner=lisnter;
    }
}
