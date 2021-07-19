package com.example.recipebook.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SharedPreferenceFileHandler {
    private final Context context;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private final String fileName, key;

    public SharedPreferenceFileHandler(Context context, String fileName, String key) {
        this.context = context;
        this.fileName = fileName;
        this.key = key;
        sharedPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /*---------------------PUBLIC METHODS-----------------------------*/

    public void remove(String id) {
        Set<String> favoritesSet = readFromSpFile();
        favoritesSet.remove(id);
        writeToSpFile(favoritesSet);
    }

    public void add(String id) {
        Set<String> favoritesSet = readFromSpFile();
        favoritesSet.add(id);
        writeToSpFile(favoritesSet);
    }

    public SharedPreferences getShredPref() {
        return sharedPref;
    }

    public boolean contains(String id) {
        Set<String> s = (Set<String>) sharedPref.getStringSet(key, Collections.singleton(""));
        return s.contains(id);
    }

    public Set<String> read() {
        return (Set<String>) sharedPref.getStringSet(key, Collections.singleton(""));
    }

    /*---------------------PRIVATE METHODS----------------------------*/

    private Set<String> readFromSpFile() {
        //sharedPref =getShredPref();
        editor = sharedPref.edit();
        Set<String> favoritesSet = (HashSet<String>) sharedPref.getStringSet(key, new HashSet<String>());
        return (Set<String>) new HashSet(favoritesSet);
    }

    private void writeToSpFile(Set<String> favoritesSet) {
        editor.putStringSet(key, favoritesSet);
        editor.apply();
    }

}
