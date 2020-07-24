package com.saud.celebrityapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 100;
    private static final String TAG = "SignupActivity";
    private FirebaseAuth auth;
    EditText username,password,name;
    ImageView profile_image;
    private Button btnLogin;
    private Button btnSignup;
    private ImageView profile;
    Uri uri;
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(SignupActivity.this,MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_signup);

        name=findViewById(R.id.name);
        profile_image=findViewById(R.id.image);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
       // btnLogin = findViewById(R.id.btnLogin);
        btnSignup=findViewById(R.id.btnSignup);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              chooseImage();
            }
        });
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
//            }
//        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().isEmpty()){
                    name.setError("Enter your name");
                    name.requestFocus();
                    return;
                }
                if(username.getText().toString().isEmpty()){
                    username.setError("Enter email address");
                    username.requestFocus();
                    return;
                }
                if (password.getText().toString().isEmpty()){
                    password.setError("Enter password");
                    password.requestFocus();
                    return;
                }
                if(uri==null){
                    Toast.makeText(SignupActivity.this, "Select your profile image", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressBar progressBar=findViewById(R.id.loading);
                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(final AuthResult authResult) {

                        StorageReference ref = storageReference.child("user_profile_images/" + UUID.randomUUID().toString());
                        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Long tsLong = System.currentTimeMillis()/1000;
                                        String ts = tsLong.toString();
                                        Map<String,String> map=new HashMap<>();
                                        map.put(CollectionNames.user.field_name,name.getText().toString().trim());
                                        map.put(CollectionNames.user.field_profile_image,uri.toString());
                                        map.put("created_at",ts);
                                        FirebaseFirestore.getInstance().collection(CollectionNames.col_users).document(authResult.getUser().getUid())
                                                .set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(SignupActivity.this,MainActivity.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        Log.e(TAG,e.getMessage());
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
            });
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),RESULT_LOAD_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK){
            uri = data.getData();
            try {
                profile_image.setImageURI(uri);
                Toast.makeText(this, "Image is  selected", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}