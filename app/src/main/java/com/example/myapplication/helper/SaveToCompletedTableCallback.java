package com.example.myapplication.helper;

import android.util.Pair;

import java.util.ArrayList;

public interface SaveToCompletedTableCallback {
    void onSaveToCompletedTableComplete( ArrayList<Pair<String,String>> result);
}
