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

    @SerializedName("post_date")
    private Date mPostDate = new Date();

    @SerializedName("user_pic")
    private String mProfilePic;

    @SerializedName("author_name")
    private String mAuthorName;


    public UserPost() {

    }

    public UserPost(UserPost.Builder builder) {
        mAuthor = builder.mAuthor;
        mBody = builder.mBody;
        mImageUri = builder.mImg;
        mPostDate = builder.mPostDate;
        mTitle = builder.mTitle;
        mProfilePic = builder.mProfilePic;
        mAuthorName = builder.mAuthorName;
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

    public String getProfilePic() {
        return mProfilePic;
    }

    public Date getPostDate() {
        return mPostDate;
    }

    public void setProfilePic(String postUri) {
        mProfilePic = postUri;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public static class Builder {
        private String mTitle;
        private String mBody;
        private String mAuthor;
        private Date mPostDate = new Date();
        private Uri mImg;
        private String mProfilePic;
        private String mAuthorName;

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

        public Builder profilePic(String profilePic) {
            mProfilePic = profilePic;
            return this;
        }

        public Builder image(Uri image) {
            mImg = image;
            return this;
        }

        public Builder authorName(String name) {
            mAuthorName = name;
            return this;
        }


        public UserPost build() {
            return new UserPost(this);
        }

    }
}
