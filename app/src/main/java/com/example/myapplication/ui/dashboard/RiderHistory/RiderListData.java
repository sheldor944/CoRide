package com.example.myapplication.ui.dashboard.RiderHistory;

public class RiderListData {
    public  String riderName , passengerName , From , To , Fare  , phone=""  ;

    public RiderListData(String riderName, String passengerName, String from, String to, String fare) {
        this.riderName = riderName;
        this.passengerName = passengerName;
        From = from;
        To = to;
        Fare = fare;
    }

    public RiderListData(String riderName, String passengerName, String from, String to, String fare , String phone ) {
        this.riderName = riderName;
        this.passengerName = passengerName;
        From = from;
        To = to;
        Fare = fare;
        this.phone = phone ;
    }
}