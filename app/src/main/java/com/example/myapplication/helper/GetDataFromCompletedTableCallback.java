package com.example.myapplication.helper;

import android.util.Pair;

import java.util.ArrayList;

public interface GetDataFromCompletedTableCallback {
    void onGetDataFromCompletedTableComplete(ArrayList<ArrayList<Pair<String, String>>> resultList );
}
