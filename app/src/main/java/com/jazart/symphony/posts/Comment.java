package com.jazart.symphony.posts;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("content")
    private String mContent;

    @SerializedName("authorName")
    private String mAuthorName;

    @SerializedName("authorId")
    private String mAuthorId;

    @SerializedName("likes")
    private int mLikes;

    @SerializedName("postId")
    private String mPostId;

    @SerializedName("profile_pic")
    private Uri mProfilePic;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public String getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(String authorId) {
        mAuthorId = authorId;
    }

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        mLikes = likes;
    }

    public void profilePic(Uri profilePic) {
        mProfilePic = profilePic;
    }

    public Uri getProfilePic() {
        return mProfilePic;
    }
}
