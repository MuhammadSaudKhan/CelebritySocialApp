package com.saud.celebrityapp.ui.Wallet.payments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saud.celebrityapp.Adapter.VideosAdapter;
import com.saud.celebrityapp.CardActivity;
import com.saud.celebrityapp.Checkout_activity;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.Database.OnDataFetched;
import com.saud.celebrityapp.Model.FileModel;
import com.saud.celebrityapp.R;
import com.saud.celebrityapp.ui.Wallet.payments.WalletViewModel;
import com.saud.celebrityapp.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {

    private static final String TAG = "WalletFragment";
    private WalletViewModel walletViewModel;
    private TextView total_amount;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private ArrayList<FileModel> videoData=new ArrayList<>();

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        walletViewModel =
                ViewModelProviders.of(this).get(WalletViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_wallet, container, false);
//        if (FirebaseAuth.getInstance().getCurrentUser()==null){
//            return root;
//        }

        total_amount=root.findViewById(R.id.total_amount);
        btnAdd=root.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser()==null){
                    Snackbar.make(root, "You need to login first", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                View view=LayoutInflater.from(getContext()).inflate(R.layout.alert_enter_amount,null,false);
                final EditText edtAmount=view.findViewById(R.id.amount);
                Button btnSave=view.findViewById(R.id.btn_save);
                builder.setView(view);
                final AlertDialog alertDialog=builder.create();
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtAmount.getText().toString().isEmpty()) {
                            edtAmount.setError("Enter amount");
                        }else {
                               Intent intent=new Intent(getActivity(), Checkout_activity.class);
                               intent.putExtra("amount",edtAmount.getText().toString());
                               startActivity(intent);
                               alertDialog.dismiss();
                        }
                    }
                });
                builder.setView(view);
                alertDialog.show();
            }
        });
        //loadbalance();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
            loadbalance();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
            loadbalance();
    }

    private void loadbalance() {
        db.collection(CollectionNames.col_wallet).document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double balance=0;
                try {
                    balance =documentSnapshot.getDouble("balance");
                    total_amount.setText("$"+balance);
                }catch (NullPointerException e){

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}