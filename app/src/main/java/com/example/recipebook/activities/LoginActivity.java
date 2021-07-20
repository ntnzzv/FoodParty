package com.example.recipebook.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.R;
import com.example.recipebook.utils.Authentication;
import com.example.recipebook.utils.Instances;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    Authentication auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Instances.auth = new Authentication(this);

        findViewById(R.id.google_signIn).setOnClickListener(v -> Instances.auth.signIn());
        findViewById(R.id.google_signIn).setOnClickListener(v -> auth.signIn());
        findViewById(R.id.without_signIn).setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Authentication.RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    auth.firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                }
            }
        }
    }
}
