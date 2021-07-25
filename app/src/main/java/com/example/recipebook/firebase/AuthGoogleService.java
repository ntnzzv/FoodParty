package com.example.recipebook.firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.R;
import com.example.recipebook.entities.Recipe;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthGoogleService {

    private static AuthGoogleService authGoogleServiceInstance = null;
    FirebaseAuth firebaseAuth;


    private AuthGoogleService() {
        firebaseAuth = FirebaseAuth.getInstance();
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

    public FirebaseUser getFirebaseCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public static boolean currentUserCreateThisRecipe(Recipe recipe) {
        return recipe.getCreatorId().
                equals(AuthGoogleService.getInstance().getFirebaseCurrentUser().getUid());
    }
    public static boolean userSigned() {
        return AuthGoogleService.getInstance().getFirebaseCurrentUser()!=null;
    }
}