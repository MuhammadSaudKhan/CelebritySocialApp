package com.saud.celebrityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.braintreepayments.cardform.view.CardForm;


public class CardActivity extends AppCompatActivity {
    private Button btnBuy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        btnBuy=findViewById(R.id.btnBuy);
        final CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Add Money to wallet")
                .setup(CardActivity.this);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardForm.isValid()){

                }else {

                }
            }
        });
    }
}