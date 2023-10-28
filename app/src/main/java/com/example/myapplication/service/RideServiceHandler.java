package com.example.myapplication.service;

import android.util.Log;

import com.example.myapplication.data.model.RiderTrip;

public class RideServiceHandler {
    private static final String TAG = "RideServiceHandler";

    public static final double FUEL_PER_METER_COST = 9.0 / 1000.0;
    public static final double PER_METER_COST = 15.0 / 1000.0;
    private static final double SERVICE_COST = 20.0;
    public static int getCostFromDistance(int distanceInMeter) {
        double distance = (double) distanceInMeter;
        double cost = SERVICE_COST + (distance * PER_METER_COST);
        int costInt = (int) cost;
        Log.d(TAG, "getCostFromDistance: double: " + cost + " int: " + costInt);
        return costInt;
    }

    public static boolean isFeasibleForRider(RiderTrip riderTrip, int passengerRouteDistance) {
        int passengerCost = getCostFromDistance(passengerRouteDistance);
        int initialRouteCost = (int) ((double) riderTrip.getLocationData().getDistance() * FUEL_PER_METER_COST);
        int finalRouteCost = (int) ((double) riderTrip.getTotalDistance() * FUEL_PER_METER_COST);

        int finalNetCost = finalRouteCost - passengerCost;
        Log.d(TAG, "isFeasibleForRider: rider id: " + riderTrip.getLocationData().getUserID());
        Log.d(TAG, "isFeasibleForRider: passenger: " + passengerCost
                + "initial route cost: " + initialRouteCost
                + "final route net cost: " + finalNetCost
        );
        if(finalNetCost > initialRouteCost / 2) return false;
        return true;
    }
}
