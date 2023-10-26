package com.example.myapplication;

public class UserModel {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String NationalID;
    private String password;
    private String imageURL;


    public UserModel(){}
    public UserModel(String firstName, String lastName, String email, String phone, String NationalID , String password , String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.NationalID = NationalID;
        this.password = password;
        this.imageURL = imageURL;
    }
    public UserModel(String firstName, String lastName, String email, String phone, String NationalID , String password ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.NationalID = NationalID;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getImageURL(){return  imageURL;}
    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getNationalID() {
        return NationalID;
    }
    public String getThePassword(){
        return password;
    }
}
