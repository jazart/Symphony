package com.jazart.symphony.posts;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("content")
    private String mContent;

    @SerializedName("author_name")
    private String mAuthorName;

    @SerializedName("author_id")
    private String mAuthorId;

    @SerializedName("likes")
    private int mLikes;

    @SerializedName("post_id")
    private String mPostId;

    @SerializedName("profile_pic")
    private String mProfilePic;

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
}
