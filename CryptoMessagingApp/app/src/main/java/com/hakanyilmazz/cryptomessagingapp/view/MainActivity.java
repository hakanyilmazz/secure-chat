package com.hakanyilmazz.cryptomessagingapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hakanyilmazz.cryptomessagingapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                Intent intent = null;
                if (firebaseUser == null) {
                    intent = new Intent(MainActivity.this,
                            SignUpActivity.class);
                } else {
                    intent = new Intent(MainActivity.this,
                            MessagingActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }.start();

    }
}