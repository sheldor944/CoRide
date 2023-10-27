package com.example.myapplication.ui.dashboard.RiderHistory;

public class RiderListData {
    public  String riderName , passengerName , From , To , Fare ;

    public RiderListData(String riderName, String passengerName, String from, String to, String fare) {
        this.riderName = riderName;
        this.passengerName = passengerName;
        From = from;
        To = to;
        Fare = fare;
    }
}