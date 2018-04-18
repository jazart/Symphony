package com.jazart.symphony;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserPost {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("body")
    private String mBody;

    @SerializedName("user")
    private User mAuthor;

    @SerializedName("image_uri")
    private Uri mImageUri;

    @SerializedName("post_uri")
    private Uri mPostUri;

    @SerializedName("post_date")
    private Date mPostDate = new Date();


    public UserPost() {

    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public User getAuthor() {
        return mAuthor;
    }

    public void setAuthor(User author) {
        mAuthor = author;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    public Uri getPostUri() {
        return mPostUri;
    }

    public void setPostUri(Uri postUri) {
        mPostUri = postUri;
    }

    public Date getPostDate() {
        return mPostDate;
    }
}
