package com.saud.celebrityapp.ui.photos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.saud.celebrityapp.Adapter.ImagesAdapter;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.Database.OnDataFetched;
import com.saud.celebrityapp.Model.FileModel;
import com.saud.celebrityapp.R;
import com.saud.celebrityapp.ui.login.LoginActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class PhotosFragment extends Fragment implements OnDataFetched {

    private PhotosViewModel photosViewModel;
    private RecyclerView recyclerView;
    private ImagesAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<FileModel> imageData=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        photosViewModel =
                ViewModelProviders.of(this).get(PhotosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_photos, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        loadImages();
        return root;
    }
    private void loadImages() {
        db.collection(CollectionNames.col_images).orderBy("created_at", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot: list) {
                        imageData.add(documentSnapshot.toObject(FileModel.class));
                    }
                    fetchImages();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }
    private void fetchImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ImagesAdapter(getActivity(),imageData);
        adapter.setOnUnlockListner(new ImagesAdapter.OnUnlockListner() {
            @Override
            public void OnSuccess(final ArrayList<FileModel> list, final ImagesAdapter.viewholder holder, final int position) {

                AlertDialog.Builder builder=new AlertDialog.Builder((Activity)holder.itemView.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("The price is $"+list.get(position).getPrice());
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (FirebaseAuth.getInstance().getCurrentUser()==null){
                            Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                try{
                                    double balance=documentSnapshot.getDouble(CollectionNames.wallet.field_balance);
                                    if (balance>=Double.parseDouble(list.get(position).getPrice())){
                                        Map<String,Object> map=new HashMap<>();
                                        double cbalance=balance-Double.parseDouble(list.get(position).getPrice());
                                        map.put(CollectionNames.wallet.field_balance,cbalance);
                                        db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Map<String,String> map=new HashMap<>();
                                                map.put(CollectionNames.user_images.field_user_id,FirebaseAuth.getInstance().getUid());
                                                map.put(CollectionNames.user_images.field_image_url,list.get(position).getUrl());
                                                db.collection(CollectionNames.col_user_images).add(map)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
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
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getContext(), "You balance is low", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (NullPointerException e){
                                    Toast.makeText(getContext(), "You balance is low", Toast.LENGTH_SHORT).show();
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

    }
}