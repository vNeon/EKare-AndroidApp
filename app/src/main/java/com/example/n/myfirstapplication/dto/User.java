package com.example.n.myfirstapplication.dto;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;

/**
 * Created by n on 2/07/2017.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String phone;
    private String gender;
    private String role;
    private String profilePicUri;
    public List<Message> messagesReceived;
    public HashMap<String,Contact> contacts;
    public HashMap<String,Contact> requests;
    public String deviceToken;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String phone, String gender, String role,String Uri){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender =gender;
        this.role= role;
        this.profilePicUri = Uri;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }

    public List<Message> getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(List<Message> messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    public HashMap<String, Contact> getContacts() {
        return contacts;
    }

    public void setContacts(HashMap<String, Contact> contacts) {
        this.contacts = contacts;
    }

    public HashMap<String, Contact> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<String, Contact> requests) {
        this.requests = requests;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
