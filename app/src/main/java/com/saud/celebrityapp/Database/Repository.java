package com.saud.celebrityapp.Database;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saud.celebrityapp.Model.FileModel;
import com.saud.celebrityapp.Model.Person;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Repository {
    private static final String TAG="Repository";
    private static Repository mInstance;
    private ArrayList<Person> personData=new ArrayList<>();
    private ArrayList<FileModel> imageData;
    private ArrayList<FileModel> videoData;



    //Firebase
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Repository() {
        imageData=new ArrayList<>();
        videoData=new ArrayList<>();
    }

    private static OnDataFetched onDataFetched;
    private static OnDataAdded onDataAdded;
    public static Repository getInstance(Context context){
        if(mInstance==null){
            mInstance=new Repository();
        }

        onDataFetched= (OnDataFetched) context;
        onDataAdded=(OnDataAdded) context;
        return mInstance;
    }
    public MutableLiveData<ArrayList<Person>> getPersonData(){
        loadPersonData();
        MutableLiveData<ArrayList<Person>> data=new MutableLiveData<>();
        data.setValue(personData);
        return data;
    }

    public MutableLiveData<ArrayList<FileModel>> getImages(){
        loadImages();
        MutableLiveData<ArrayList<FileModel>> data=new MutableLiveData<>();
        data.setValue(imageData);
        return data;
    }
    public MutableLiveData<ArrayList<FileModel>> getVideos(){
        loadVideos();
        MutableLiveData<ArrayList<FileModel>> data=new MutableLiveData<>();
        data.setValue(videoData);
        return data;
    }
    public void addImage(final FileModel fileModel, Uri uri){
        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String,String> map=new HashMap<>();
                        String id=db.collection(CollectionNames.col_images).document().getId();
                        map.put(CollectionNames.images.field_id,id);
                        map.put(CollectionNames.images.field_name,fileModel.getName());
                        map.put(CollectionNames.images.field_url,uri.toString());
                        map.put(CollectionNames.images.field_price,String.valueOf(fileModel.getPrice()));

                        db.collection(CollectionNames.col_images).document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                onDataAdded.onAddData();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,e.getMessage());
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });

    }
    public void changeScreenSaver(Uri uri){
        StorageReference ref = storageReference.child("screensaver/"+ UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String,Object> map=new HashMap<>();
                        map.put(CollectionNames.screensaver.field_image_url,uri.toString());

                        db.collection(CollectionNames.col_screensaver).document(CollectionNames.screensaver.doc_name).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                onDataAdded.onAddData();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,e.getMessage());
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });

    }
    public void addVideo(final FileModel fileModel, Uri uri, final Uri thumbnail_uri) {
        StorageReference ref = storageReference.child("videos/" + UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String, String> map = new HashMap<>();
                        final String id = db.collection(CollectionNames.col_videos).document().getId();
                        map.put(CollectionNames.videos.field_id, id);
                        map.put(CollectionNames.videos.field_name, fileModel.getName());
                        map.put(CollectionNames.videos.field_url, uri.toString());
                        map.put(CollectionNames.videos.field_price, String.valueOf(fileModel.getPrice()));
                        db.collection(CollectionNames.col_videos).document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addVideoThumbnail(id, thumbnail_uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });

            }
        });
    }
   public void addVideoThumbnail(final String video_id, Uri uri){
        StorageReference ref = storageReference.child("video_thumbnail/"+ UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String,String> map=new HashMap<>();
                        map.put("thumbnail_url",uri.toString());

                        db.collection("thumbnail").document(video_id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            onDataAdded.onAddData();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });

    }
    private void loadImages() {
        db.collection(CollectionNames.col_images).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot: list) {
                        imageData.add(documentSnapshot.toObject(FileModel.class));
                    }
                    onDataFetched.onGetData();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
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
                    onDataFetched.onGetData();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }
    private void loadPersonData() {
        db.collection("person").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot: list) {
                        personData.add(documentSnapshot.toObject(Person.class));
                    }
                    onDataFetched.onGetData();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }

}