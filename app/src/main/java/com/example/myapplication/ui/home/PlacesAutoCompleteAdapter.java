package com.example.myapplication.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> {
    private List<AutocompletePrediction> predictions;

    public PlacesAutoCompleteAdapter(Context context, PlacesClient placesClient) {
        super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        this.predictions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getFullText(null));
        return convertView;
    }

    @Nullable
    @Override
    public AutocompletePrediction getItem(int position) {
        return predictions.get(position);
    }

    @Override
    public int getCount() {
        return predictions.size();
    }



    void setPredictions(List<AutocompletePrediction> predictions , AutoCompleteTextView autoCompleteTextView ) {
        this.predictions = predictions;
        notifyDataSetChanged();
        if (predictions != null && predictions.size() > 0) {
            // Show the dropdown list when there are predictions
            autoCompleteTextView.showDropDown();
        }
    }

}