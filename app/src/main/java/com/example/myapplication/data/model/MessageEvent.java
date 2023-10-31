package com.example.myapplication.data.model;

public class MessageEvent {
    public final String message;
    public  String fare="" ;

    public MessageEvent(String message , String fare) {
        this.message = message;
        this.fare = fare;
    }
    public MessageEvent(String message ) {
        this.message = message;

    }
}