package com.jazart.symphony.posts;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserPost {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("body")
    private String mBody;

    @SerializedName("user")
    private String mAuthor;

    @SerializedName("image_uri")
    private Uri mImageUri;

    @SerializedName("post_uri")
    private Uri mPostUri;

    @SerializedName("post_date")
    private Date mPostDate = new Date();


    public UserPost() {

    }

    public UserPost(UserPost.Builder builder) {
        mAuthor = builder.mAuthor;
        mBody = builder.mBody;
        mImageUri = builder.mImg;
        mPostDate = builder.mPostDate;
        mTitle = builder.mTitle;
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

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
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

    public static class Builder {
        private String mTitle;
        private String mBody;
        private String mAuthor;
        private Date mPostDate = new Date();
        private Uri mImg;

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public Builder body(String body) {
            mBody = body;
            return this;
        }

        public Builder author(String author) {
            mAuthor = author;
            return this;
        }


        public UserPost build() {
            return new UserPost(this);
        }

    }
}
