package com.example.myapplication.ui.dashboard;

public class PassengerListData {
    public  String passengerName , RiderName , From , To , Fare , phone ;

    public PassengerListData(String passengerName, String riderName, String from, String to, String fare) {
        this.passengerName = passengerName;
        RiderName = riderName;
        From = from;
        To = to;
        Fare = fare;
    }
    public PassengerListData(String passengerName, String riderName, String from, String to, String fare , String phone) {
        this.passengerName = passengerName;
        RiderName = riderName;
        From = from;
        To = to;
        Fare = fare;
        this.phone = phone ;
    }
}