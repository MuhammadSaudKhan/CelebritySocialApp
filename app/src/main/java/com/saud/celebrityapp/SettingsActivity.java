package com.saud.celebrityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.ui.login.LoginActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    private RelativeLayout r_video,r_image,r_message,r_wallet,r_wallet1,r_login,r_logout,r_forget;
    Button btn_login;
    TextView balance;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        balance=findViewById(R.id.balance);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    double bl=documentSnapshot.getDouble(CollectionNames.wallet.field_balance);
                    balance.setText(String.valueOf(bl));
                }catch (Exception ex){
                    Log.e(TAG,ex.getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        (findViewById(R.id.logo)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.title)).setText("Menu");
        initUI();

        final Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
          r_logout.setVisibility(View.GONE);
        }else {
            r_login.setVisibility(View.GONE);
        }
        r_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,ForgetPassActivity.class));
            }
        });
        r_wallet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("page","wallet");
                startActivity(intent);
            }
        });
        r_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("page","wallet");
                startActivity(intent);
            }
        });
        r_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("page","message");
                startActivity(intent);
            }
        });
        r_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("page","image");
                startActivity(intent);
            }
        });
        r_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("page","video");
                startActivity(intent);
            }
        });
        r_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });
        r_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,LoginActivity.class));

            }
        });
    }

    private void initUI() {
        r_forget=findViewById(R.id.open_forget_pass_rl);
        r_video=findViewById(R.id.open_videos_rl);
        r_image=findViewById(R.id.open_image_rl);
        r_wallet=findViewById(R.id.open_wallet_rl);
        r_wallet1=findViewById(R.id.open_wallet1_rl);
        r_login=findViewById(R.id.open_login_rl);
        r_logout=findViewById(R.id.logout_rl);
        r_message=findViewById(R.id.open_message_rl);
        btn_login=findViewById(R.id.btn_login);
        balance=findViewById(R.id.balance);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        finish();
        super.onBackPressed();

    }
}