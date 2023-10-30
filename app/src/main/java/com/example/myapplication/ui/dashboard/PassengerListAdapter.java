package com.example.myapplication.ui.dashboard;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.ui.notifications.UpdateProfileActivity;

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
        LocationDB locationDB = new LocationDB();
        final String currentRiderID = listData.riderID;
        locationDB.getImageURL(currentRiderID,new Callback<Uri>() {
            @Override
            public void onComplete(Uri response) {
                if (currentRiderID.equals(listData.riderID)) {
                    Log.d("TAG", "onComplete: " + currentRiderID + " "+ response);

                    Glide.with(getContext())
                            .load(response)
                            .skipMemoryCache(true) // You can use this line to test if caching is the issue
                            .into(listImage);
                } else {
                    Log.d("TAG", "onComplete: riderID has changed, skipping Glide load");
                }




            }
        });

        listName.setText("Rider "+listData.RiderName);
        listTime.setText("Phone: "+listData.phone);
        listFare.setText("Fare: "+listData.Fare);

        return view;
    }

}