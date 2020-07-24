package com.saud.celebrityapp.ui.messages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.saud.celebrityapp.Adapter.chat_adapter;
import com.saud.celebrityapp.Database.CollectionNames;
import com.saud.celebrityapp.MainActivity;
import com.saud.celebrityapp.Model.Message;
import com.saud.celebrityapp.R;
import com.saud.celebrityapp.ui.login.LoginActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.braintreepayments.cardform.CardScanningFragment.TAG;

public class MessagesFragment extends Fragment {


    private ImageView send;
    private EditText msg;
    private TextView msg_price,emoji_1,emoji_2,emoji_3,emoji_4,emoji_5,emoji_6,emoji_7,emoji_8;
    private RecyclerView recyclerView;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    double total_amount;
    double message_price;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_messages, container, false);



        loadbalance();
        emoji_1=root.findViewById(R.id.emoji_1);
        emoji_2=root.findViewById(R.id.emoji_2);
        emoji_3=root.findViewById(R.id.emoji_3);
        emoji_4=root.findViewById(R.id.emoji_4);
        emoji_5=root.findViewById(R.id.emoji_5);
        emoji_6=root.findViewById(R.id.emoji_6);
        emoji_7=root.findViewById(R.id.emoji_7);
        emoji_8=root.findViewById(R.id.emoji_8);
        //EmojiCompat.get().registerInitCallback(new InitCallback(emoji_1,getEmojiByUnicode(0x1F61A)));
        emoji_1.setText(getEmojiByUnicode(0x1F60A));
        emoji_2.setText(getEmojiByUnicode(0x1F61A));
        emoji_3.setText(getEmojiByUnicode(0x1F618));
        emoji_4.setText(getEmojiByUnicode(0x1F970));
        emoji_5.setText(getEmojiByUnicode(0x1F60D));
        emoji_6.setText(getEmojiByUnicode(0x1F929));
        emoji_7.setText(getEmojiByUnicode(0x1F917));
        emoji_8.setText(getEmojiByUnicode(0x1F48B));

        emoji_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_1.getText().toString(),emoji_1);
            }
        });

        emoji_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_2.getText().toString(),emoji_2);
            }
        });

        emoji_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_3.getText().toString(),emoji_3);
            }
        });

        emoji_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_4.getText().toString(),emoji_4);
            }
        });

        emoji_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_5.getText().toString(),emoji_5);
            }
        });

        emoji_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_6.getText().toString(),emoji_6);
            }
        });

        emoji_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_7.getText().toString(),emoji_7);
            }
        });

        emoji_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmoji(root,emoji_8.getText().toString(),emoji_8);
            }
        });
        send=root.findViewById(R.id.btnSend);
        msg=root.findViewById(R.id.msg);
        recyclerView=root.findViewById(R.id.recyclerView);
        msg_price=root.findViewById(R.id.msg_price);
        db.collection(CollectionNames.col_price).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document:
                     queryDocumentSnapshots) {
                    try {
                        message_price=document.getDouble("price");
                        msg_price.setText(String.valueOf(document.getDouble("price")));
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Message Price issue", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                if(msg.getText().toString().isEmpty()){
                    return;
                }
                if (total_amount==0){
                    Toast.makeText(getContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (total_amount<Double.parseDouble(msg_price.getText().toString())){
                    Toast.makeText(getContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("The price will be $"+message_price+". Confirm?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date dNow = new Date();
                        SimpleDateFormat ft =
                                new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm:ss a zzz");
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();
                        final Map<String,String> map=new HashMap<>();
                        map.put(CollectionNames.messages.field_msg,msg.getText().toString());
                        map.put(CollectionNames.messages.field_sender_id, FirebaseAuth.getInstance().getUid());
                        map.put(CollectionNames.messages.field_receiver_id, "admin");
                        map.put(CollectionNames.messages.field_msg_type,"sent");
                        map.put(CollectionNames.messages.field_date_time,ft.format(dNow));
                        map.put("created_at",ts);
                        //Toast.makeText(getActivity(), "Sending", Toast.LENGTH_SHORT).show();
                        msg.setText("");
                        send.setEnabled(false);
                        db.collection(CollectionNames.col_msg).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                double newbalance=total_amount-Double.parseDouble(msg_price.getText().toString());
                                Map<String,Object> map1=new HashMap<>();
                                map1.put("balance",newbalance);
                                db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).set(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loadbalance();
                                        send.setEnabled(true);
                                        Toast.makeText(getContext(), "Sent", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG,e.getMessage());
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
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });
        fetchMessages();
        return root;
    }

    private void sendEmoji(View root, final String emoji, final TextView textView) {
        if (total_amount==0){
            Toast.makeText(getContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
            return;
        }
        if (total_amount<Double.parseDouble("5")){
            Toast.makeText(getContext(), "You don't have enough balance", Toast.LENGTH_SHORT).show();
            return;
        }
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
        builder.setTitle("The price will be $5. Confirm?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm:ss a zzz");
                final Map<String,String> map=new HashMap<>();
                map.put(CollectionNames.messages.field_msg,emoji);
                map.put(CollectionNames.messages.field_sender_id, FirebaseAuth.getInstance().getUid());
                map.put(CollectionNames.messages.field_receiver_id, "admin");
                map.put(CollectionNames.messages.field_msg_type,"sent");
                map.put(CollectionNames.messages.field_date_time,ft.format(dNow));
                map.put("created_at",ts);
                textView.setEnabled(false);
                //Toast.makeText(getContext(), "Sending", Toast.LENGTH_SHORT).show();
                db.collection(CollectionNames.col_msg).add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        double newbalance=total_amount-Double.parseDouble("5");
                        Map<String,Object> map1=new HashMap<>();
                        map1.put("balance",newbalance);
                        db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).set(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                textView.setEnabled(true);
                                loadbalance();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }

    private void fetchMessages() {
        db.collection(CollectionNames.col_msg)
                .whereEqualTo(CollectionNames.messages.field_sender_id,FirebaseAuth.getInstance().getUid())
                .orderBy(CollectionNames.messages.field_date_time)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        List<Message> list=new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            try {
                                String msg=documentSnapshot.get(CollectionNames.messages.field_msg).toString();
                                //String date=documentSnapshot.get(CollectionNames.messages.field_date_time).toString();
                                String type=documentSnapshot.get(CollectionNames.messages.field_msg_type).toString();
                                String user_id=documentSnapshot.get(CollectionNames.messages.field_sender_id).toString();
                                Message ms=new Message();
                                ms.setUser_id(user_id);
                                ms.setMsg(msg);
                                ms.setType(type);
                                //ms.setDate(date);
                                list.add(ms);
                            }catch (Exception ex){
                                Log.e(TAG,ex.getMessage());
                            }
                        }
                        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        chat_adapter adapter=new chat_adapter(list,getActivity());
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(list.size() - 1);
                    }
                });
    }
    private void loadbalance() {
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        db.collection(CollectionNames.col_wallet).document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double balance=0;
                try {
                    balance =documentSnapshot.getDouble("balance");
                    total_amount=balance;
                }catch (NullPointerException e){

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
    private static class InitCallback extends EmojiCompat.InitCallback {

        private final WeakReference<TextView> mRegularTextViewRef;
        String emoji;
        InitCallback(TextView regularTextView,String emoji) {
            mRegularTextViewRef = new WeakReference<>(regularTextView);
            this.emoji=emoji;
        }

        @Override
        public void onInitialized() {
            final TextView regularTextView = mRegularTextViewRef.get();
            if (regularTextView != null) {
                final EmojiCompat compat = EmojiCompat.get();
                regularTextView.setText(compat.process("\u1F60A"));
            }
        }

    }
}