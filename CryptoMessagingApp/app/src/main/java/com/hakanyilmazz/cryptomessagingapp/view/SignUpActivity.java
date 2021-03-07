package com.hakanyilmazz.cryptomessagingapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hakanyilmazz.cryptomessagingapp.R;

public class SignUpActivity extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailText    = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View view) {
        String email    = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (!isUserValuesValid(email, password)) {
            Toast.makeText(this, "Error! Please check your email and password.", Toast.LENGTH_LONG)
                    .show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startIntentToMessagingActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }

    public void signUp(View view) {
        String email    = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (!isUserValuesValid(email, password)) {
            Toast.makeText(this, "Error! Please check your email and password.", Toast.LENGTH_LONG)
                    .show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startIntentToMessagingActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }

    public void startIntentToMessagingActivity() {
        Intent intentToMessagingActivity = new Intent(SignUpActivity.this,
                MessagingActivity.class);
        startActivity(intentToMessagingActivity);
        finish();
    }

    public boolean isUserValuesValid(String email, String password) {
        boolean isNullOrEmptyEmail    = isNullOrEmpty(email);
        boolean isNullOrEmptyPassword = isNullOrEmpty(password);
        boolean isUserMailValid       = isEmailValid(email);

        return !isNullOrEmptyEmail && !isNullOrEmptyPassword && isUserMailValid;
    }

    public boolean isNullOrEmpty(String text) {
        return text.equals("") || text == null;
    }

    public boolean isEmailValid(String email) {
        // It can't control that itself finishes with ".com"
        boolean isEmailFormat = email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        boolean isFinishingWithDotCom = email.endsWith(".com");

        return isEmailFormat && isFinishingWithDotCom;
    }

}