package com.jazart.symphony.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Song {

    @Expose
    @SerializedName("name")
    private String mName;

    @Expose
    @SerializedName("artists")
    private List<String> mArtists;

    @Expose
    @SerializedName("date")
    private Date mDate;

    @Expose
    @SerializedName("uri")
    private String mURI;

    //private long mLength;

    @Expose
    @SerializedName("likes")
    private int mLikes;

    @Expose
    @SerializedName("author")
    private String mAuthor;


    public Song(){
        //mArtists = new List<String>();
        mDate = new Date();
        mLikes = 0;

    }

    public Song(List<String> artists, String author, Date date, int likes, String name, String uri ){
        mName = name;
        mArtists = artists;
        mURI = uri;
        mLikes = likes;
        mDate = date;
        mAuthor = author;

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getArtists() {
        return mArtists;
    }

    public void setArtists(List<String> artists) {
        mArtists = artists;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getURI() {
        return mURI;
    }

    public void setURI(String URI) {
        mURI = URI;
    }

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        mLikes = likes;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }
}
