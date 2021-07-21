package com.example.recipebook.utils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthGoogleService {

    private static AuthGoogleService authGoogleServiceInstance = null;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseCurrentUser;

    private AuthGoogleService() {
        firebaseAuth = FirebaseAuth.getInstance();
        setFirebaseCurrentUser();
    }

    public static AuthGoogleService getInstance() {
        if (authGoogleServiceInstance == null)
            authGoogleServiceInstance = new AuthGoogleService();
        return authGoogleServiceInstance;
    }

    public GoogleSignInClient getGoogleSignInClient(AppCompatActivity context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(context.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context, gso);
    }


    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseCurrentUser() {
        firebaseCurrentUser = firebaseAuth.getCurrentUser();
    }

    public FirebaseUser getFirebaseCurrentUser() {
        return firebaseCurrentUser;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
