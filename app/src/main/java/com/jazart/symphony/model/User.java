package com.jazart.symphony.model;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {

    @SerializedName("uId")
    private String mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("date_joined")
    private Date mDateJoined = new Date();

    @SerializedName("friends")
    private User[] mFriends;

    @SerializedName("num_songs")
    private int mNumSongs;


    @SerializedName("num_plays")
    private int mNumPlays;

    @SerializedName("location")
    private GeoPoint mGeoPoint;

    @SerializedName("city")
    private String mCity;

    public User() {

    }

    public User(FirebaseUser authUser) {
        mId = authUser.getUid();
        mName = authUser.getDisplayName();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Date getDateJoined() {
        return mDateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        mDateJoined = dateJoined;
    }

    public User[] getFriends() {
        return mFriends;
    }

    public void setFriends(User[] friends) {
        mFriends = friends;
    }

    public int getNumSongs() {
        return mNumSongs;
    }

    public void setNumSongs(int numSongs) {
        mNumSongs = numSongs;
    }

    public int getNumPlays() {
        return mNumPlays;
    }

    public void setNumPlays(int numPlays) {
        mNumPlays = numPlays;
    }

    public GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint point) {
        mGeoPoint = point;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

}