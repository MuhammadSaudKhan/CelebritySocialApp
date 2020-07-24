package com.saud.celebrityapp.ui.videos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saud.celebrityapp.Adapter.ImagesAdapter;
import com.saud.celebrityapp.Adapter.VideosAdapter;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.Database.OnDataAdded;
import com.saud.celebrityapp.Database.OnDataFetched;
import com.saud.celebrityapp.Model.FileModel;
import com.saud.celebrityapp.R;
import com.saud.celebrityapp.ui.login.LoginActivity;
import com.saud.celebrityapp.video_view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideosFragment extends Fragment implements OnDataFetched {

    private static final String TAG = "VdieosFragment";
    private VideosViewModel videosViewModel;
    RecyclerView recyclerView;
    private VideosAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<FileModel> videoData=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        videosViewModel =
                ViewModelProviders.of(this).get(VideosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_videos, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        loadVideos();
        return root;
    }
    private void loadVideos() {
        db.collection(CollectionNames.col_videos).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot: list) {
                        videoData.add(documentSnapshot.toObject(FileModel.class));
                    }
                    fetchVideos();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }
    private void fetchVideos() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter=new VideosAdapter(getActivity(),videoData);
       adapter.setOnPremiumClickLisnter(new VideosAdapter.onPremiumclickListner() {
           @Override
           public void Onclick(final RelativeLayout relativeLayout, final String url, final double price, final List<FileModel> list, final int position, final VideosAdapter.viewholder holder) {
               if (FirebaseAuth.getInstance().getCurrentUser()==null){
                   Toast.makeText(getActivity(), "You need to login first", Toast.LENGTH_SHORT).show();
                   return;
               }
               AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
               builder.setTitle("Confirmation");
               builder.setMessage("The price will be $"+list.get(position).getPrice());
               builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (FirebaseAuth.getInstance().getCurrentUser()==null){
                           Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                           @Override
                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                               double balanceInDb=0;
                               if (documentSnapshot.exists()){
                                   try {
                                       balanceInDb=documentSnapshot.getDouble("balance");
                                       if (balanceInDb<price){
                                           Toast.makeText(getContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
                                           return;
                                       }
                                       double currentBalance=balanceInDb-price;
                                       Map<String,Object> map=new HashMap<>();
                                       map.put("balance",currentBalance);
                                       db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               relativeLayout.setVisibility(View.GONE);
                                               holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       Intent intent=new Intent((Activity) getActivity(), video_view.class);
                                                       intent.putExtra("uri",list.get(position).getUrl());
                                                       startActivity(intent);
                                                   }
                                               });
                                               Map<String,Object> map=new HashMap<>();
                                               map.put(CollectionNames.user_videos.field_user_id, FirebaseAuth.getInstance().getUid());
                                               map.put(CollectionNames.user_videos.field_image_url,url);
                                               db.collection(CollectionNames.col_user_videos).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                   @Override
                                                   public void onSuccess(DocumentReference documentReference) {

                                                   }
                                               }).addOnFailureListener(new OnFailureListener() {
                                                   @Override
                                                   public void onFailure(@NonNull Exception e) {

                                                   }
                                               });
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               Log.e(TAG,e.getMessage());
                                               Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                   }catch (NullPointerException e){
                                       Log.e(TAG,e.getMessage());
                                       Toast.makeText(getContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
                                   }
                               }else {
                                   Toast.makeText(getContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
                               }



                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {

                           }
                       });

                   }
               });
               builder.create().show();

           }
       });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGetData() {
        adapter.notifyDataSetChanged();
    }
}