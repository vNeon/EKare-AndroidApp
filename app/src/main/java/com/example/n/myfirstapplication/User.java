package com.example.n.myfirstapplication;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by n on 2/07/2017.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String phone;
    public List<Message> messagesRecieved;
    public List<User> contacts;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
