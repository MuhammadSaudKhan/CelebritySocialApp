package com.saud.celebrityapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.saud.celebrityapp.Database.CollectionNames;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Bundle;

public class Checkout_activity extends AppCompatActivity {
    // 10.0.2.2 is the Android emulator's alias to localhost
    private static final String BACKEND_URL = "https://celebrity-stripe-backend1.herokuapp.com/";
    private static final String TAG = "Checkout_activity";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
     double amount=0;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_activity);
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        if(intent.getStringExtra("amount")!=null){
            amount=Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("amount")));
        }
        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51GukNgLdNxDbTEjSTCs0JFTAgGOoZrvN9Srg4H7qGAxskXHCEWeUNqFPzlPdSi01eOHz3GSHJgDbApdf6QsZQo7m00Sh8OG8id")
        );
        startCheckout(amount);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }
    private void startCheckout(final double amount) {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        Map<String,Object> payMap=new HashMap<>();
        Map<String,Object> itemMap=new HashMap<>();
        List<Map<String,Object>> itemList=new ArrayList<>();
        payMap.put("currency","usd");
        itemMap.put("id","photo_subscription");
        itemMap.put("amount",amount*100);
        itemList.add(itemMap);
        payMap.put("items",itemList);
        String json=new Gson().toJson(payMap);


//        String json = "{"
//                + "\"currency\":\"usd\","
//                + "\"items\":["
//                + "{\"id\":\"photo_subscription\"}"
//                + "]"
//                + "}";
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));
        // Hook up the pay button to the card widget and stripe instance
        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        cardInputWidget.setPostalCodeEnabled(false);
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                cardInputWidget.setPostalCodeEnabled(false);
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
                if (params != null) {

                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                    stripe.confirmPayment(Checkout_activity.this, confirmParams);
                }
            }
        });
    }
    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<Checkout_activity> activityRef;
        PayCallback(@NonNull Checkout_activity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull final IOException e) {
            final Checkout_activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final Checkout_activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show();
                    }
                });
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }
    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
    }
    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        private FirebaseFirestore db=FirebaseFirestore.getInstance();
        private FirebaseAuth auth=FirebaseAuth.getInstance();
        @NonNull private final WeakReference<Checkout_activity> activityRef;
        PaymentResultCallback(@NonNull Checkout_activity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final Checkout_activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                db.collection(CollectionNames.col_wallet).document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String balanceInDb="";
                        if (documentSnapshot!=null){
                            try {
                                balanceInDb= Objects.requireNonNull(documentSnapshot.get("balance")).toString();
                                double currentBalance=Double.parseDouble(balanceInDb)+activity.amount;
                                Map<String,Object> map=new HashMap<>();
                                map.put("balance",currentBalance);
                                db.collection(CollectionNames.col_wallet).document(auth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        activity.finish();
                                        Toast.makeText(activity, "Money is added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                                    }
                                });
                            }catch (NullPointerException e){

                                Map<String,Object> map=new HashMap<>();
                                map.put("balance",activity.amount);
                                db.collection(CollectionNames.col_wallet).document(auth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        activity.finish();
                                        Toast.makeText(activity, "Money is added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                                    }
                                });
                            }


                        }else{

                        }


                    }
                });
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent)
//                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }
        @Override
        public void onError(@NonNull Exception e) {
            final Checkout_activity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }
    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }
}