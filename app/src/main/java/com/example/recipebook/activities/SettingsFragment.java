package com.example.recipebook.activities;

import android.os.Bundle;


import androidx.preference.PreferenceFragmentCompat;

import com.example.recipebook.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

}
