package com.jazart.symphony.model;

import java.util.Date;
import java.util.List;

public class Song {
    private String mName;
    private List<String> mArtists;
    private Date mDate;
    private String mURI;
    private long mLength;
    private int mLikes;


    public Song(){
        //mArtists = new List<String>();

    }

    public Song(String name, List<String> artists, String uri ){
        mName = name;
        mArtists = artists;
        mURI = uri;

    }
    public void setName(String name) {
        mName = name;
    }

    public void setArtists(List<String> artists) {
        mArtists = artists;
    }


    public String getURI() {
        return mURI;
    }

    public void setURI(String URI) {
        mURI = URI;
    }

    public String getName() {
        return mName;
    }

    public List<String> getArtists() {
        return mArtists;
    }

    public Date getDate() {
        return mDate;
    }

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        mLikes = likes;
    }

}
