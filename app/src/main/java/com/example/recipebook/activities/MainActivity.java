package com.example.recipebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.utils.Authentication;
import com.example.recipebook.utils.Constants;
import com.example.recipebook.viewmodel.RecipesViewModel;
import com.example.recipebook.R;
import com.example.recipebook.adapters.RecipesAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.recipebook.utils.Constants.SIGN_IN_CODE_ID;
import static com.example.recipebook.utils.Constants.USER_SIGNED;


public class MainActivity extends AppCompatActivity {

    RecipesAdapter adapter;
    RecipesViewModel viewModel;

    private boolean userAlreadySignedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfUserSigned();

        viewModel = new ViewModelProvider(this).get(RecipesViewModel.class);

        setRecyclerViewAdapter();

    }

    private void setRecyclerViewAdapter() {
        RecyclerView rv = findViewById(R.id.rv_recipes_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipesAdapter(this, viewModel);
        rv.setAdapter(adapter);
    }

    /*------------------MENU-HANDLING---------------------------------*/
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.search_item:
                //handle search
                return true;
            case R.id.settings_item:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.signin_item:
                intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, Constants.SIGN_IN_CODE_ID);
            case R.id.logout_item:
                //...need to handle...
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logOutItem = menu.findItem(R.id.logout_item);
        MenuItem singInItem = menu.findItem(R.id.signin_item);

        if (userAlreadySignedFlag) {
            logOutItem.setVisible(true);
            singInItem.setVisible(false);
        } else {
            singInItem.setVisible(true);
            logOutItem.setVisible(false);
        }
        return true;
    }

    /*----------------------------------------------------------------*/
    public void AddRecipe(View view) {
        Intent intent = new Intent(this, AddRecipeActivity.class);
        startActivity(intent);

    }
    /*----------------------------------------------------------------*/


    private void checkIfUserSigned() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //user already signed
            userAlreadySignedFlag = true;

            //...need to handle...
        } else {
            //user not signed
            Intent intent = new Intent(this,LoginActivity2.class);
            startActivity(intent);


        }
    }
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//
//        }
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (requestCode == SIGN_IN_CODE_ID) {
                userAlreadySignedFlag = data.getExtras().getBoolean(USER_SIGNED);

                //...need to handle...
            }
    }

}