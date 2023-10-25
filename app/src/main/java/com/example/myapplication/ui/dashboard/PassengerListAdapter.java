package com.example.myapplication.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PassengerListAdapter extends ArrayAdapter<PassengerListData> {
    public PassengerListAdapter(@NonNull Context context, ArrayList<PassengerListData> dataArrayList) {
        super(context, R.layout.list_item , dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        PassengerListData listData = getItem(position);
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listName = view.findViewById(R.id.listName);
        TextView listTime = view.findViewById(R.id.listTime);
        TextView listFare = view.findViewById(R.id.Fare);

        listName.setText(listData.From);
        listTime.setText(listData.To);
        listFare.setText(listData.Fare);
        return view;
    }

}
