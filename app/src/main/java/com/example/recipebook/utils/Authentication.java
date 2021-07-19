package com.example.recipebook.utils;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.R;
import com.example.recipebook.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Authentication {

    public static final int RC_SIGN_IN = 9001;

    AppCompatActivity context;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseAuth mAuth;

    public Authentication(AppCompatActivity context){
        this.context = context;

        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(context.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(context,gso);
    }

    public void signIn()
    {
        Intent signInIntent = gsc.getSignInIntent();
        context.startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    public void signOut(){ mAuth.signOut(); }

    public void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context,task ->{
                    if(task.isSuccessful()){
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                    else{
                        Log.d("Auth",task.getException().toString());
                    }
                });

    }

    public FirebaseUser getCurrentUser(){ return mAuth.getCurrentUser();}
}
