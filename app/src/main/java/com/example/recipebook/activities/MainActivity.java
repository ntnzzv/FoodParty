package com.example.recipebook.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipebook.services.MyForegroundService;
import com.example.recipebook.firebase.AuthGoogleService;

import com.example.recipebook.broadcastreceivers.NetworkStateReceiver;
import com.example.recipebook.viewmodel.RecipesViewModel;
import com.example.recipebook.R;
import com.example.recipebook.adapters.RecipesAdapter;

import static com.example.recipebook.utils.Constants.USER_SIGNED;


public class MainActivity extends AppCompatActivity {
    public static final int SIGN_IN_CODE_ID = 222;
    RecipesAdapter adapter;
    RecipesViewModel viewModel;

    private boolean userAlreadySignedFlag;

    private NetworkStateReceiver netStateReceiver;
    AuthGoogleService authGoogleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authGoogleService = AuthGoogleService.getInstance();
        checkIfUserSigned();

        //get view model
        viewModel = new ViewModelProvider(this).get(RecipesViewModel.class);

        //set recycler view adapter
        setRecyclerViewAdapter();

        //Broadcast receiver for network state
        netStateReceiver = new NetworkStateReceiver();



    }

    @Override
    protected void onResume() {
        super.onResume();

        //create intent filter and register our receiver to get network state changes
        registerReceiver(netStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister receiver
        unregisterReceiver(netStateReceiver);

    }

    /*----------------------------------------------------------------*/
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
                openLoginActivity();
            case R.id.logout_item:
                authGoogleService.signOut();
                authGoogleService.getGoogleSignInClient(this).signOut();
                intent = new Intent(this, LoginActivity.class);
                userAlreadySignedFlag = false;
                startActivityForResult(intent, SIGN_IN_CODE_ID);
            default:
                return false;
        }
    }

    private void openLoginActivity() {
        Intent intent;
        intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, SIGN_IN_CODE_ID);
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

    //add button handler
    public void onAddRecipe(View view) {
        if (userAlreadySignedFlag) {
            Intent intent = new Intent(this, AddRecipeActivity.class);
            startActivity(intent);
        } else
            openLoginActivity();


    }
    /*----------------------------------------------------------------*/

    private void checkIfUserSigned() {
        if (authGoogleService.getFirebaseCurrentUser() != null) {
            userAlreadySignedFlag = true;
        } else {
            userAlreadySignedFlag = false;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, SIGN_IN_CODE_ID);
        }
    }

    //After login activity closed
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (requestCode == SIGN_IN_CODE_ID) {
                userAlreadySignedFlag = data.getExtras().getBoolean(USER_SIGNED);
            }
    }

}