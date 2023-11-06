package com.example.myapplication.ui.history.RiderHistory;

public class RiderListData {
    public  String riderName , passengerName , From , To , Fare  , phone="" , riderID="" , passengerID=""  ;

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

    public RiderListData(String riderName, String passengerName, String from, String to, String fare, String phone, String riderID, String passengerID) {
        this.riderName = riderName;
        this.passengerName = passengerName;
        From = from;
        To = to;
        Fare = fare;
        this.phone = phone;
        this.riderID = riderID;
        this.passengerID = passengerID;
    }
}