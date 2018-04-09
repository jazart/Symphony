package com.jazart.symphony;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class Song {
    private String mName;
    private List<String> mArtists;
    private Date mDate;
    private URI mURI;
    private long mLength;

    public Song(){

    }

    public Song(String name, List<String> artists, URI uri ){
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


    public URI getURI() {
        return mURI;
    }

    public void setURI(URI URI) {
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
}
