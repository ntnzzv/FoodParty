package com.example.recipebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.R;
import com.example.recipebook.firebase.AuthGoogleService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.recipebook.utils.Constants.USER_SIGNED;

public class LoginActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 9001;
    AuthGoogleService authGoogleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        authGoogleService = AuthGoogleService.getInstance();

        findViewById(R.id.google_signIn).setOnClickListener(v -> signIn());
        findViewById(R.id.without_signIn).setOnClickListener(v -> finish());
    }


    public void signIn() {
        Intent signInIntent = authGoogleService.getGoogleSignInClient(this).getSignInIntent();
        //opening google sign in window
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //After user sign in to google account
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    //add the user to users in firebase authentication
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                }
            }
        }
    }


    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        authGoogleService.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {


                        //return to main activity
                        Intent intent = new Intent();
                        intent.putExtra(USER_SIGNED, true);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Log.d("Auth", task.getException().toString());
                    }
                });

    }

}
