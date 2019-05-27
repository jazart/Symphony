package com.jazart.symphony.posts;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Post implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Expose
    @SerializedName("title")
    private String mTitle;

    @Expose
    @SerializedName("body")
    private String mBody;

    @Expose
    @SerializedName("user")
    private String mAuthor;

    @Expose
    @SerializedName("image_uri")
    private Uri mImageUri;

    @Expose
    @SerializedName("post_date")
    private Date mPostDate = new Date();

    @Expose
    @SerializedName("user_pic")
    private String mProfilePic;

    @Expose
    @SerializedName("author_name")
    private String mAuthorName;

    @Expose
    @SerializedName("likes")
    private int mLikes;

    @Expose
    @SerializedName("comments")
    private List<Comment> mComments;
    @Exclude
    private String mId;



    public Post() {

    }

    Post(Post.Builder builder) {
        mAuthor = builder.mAuthor;
        mBody = builder.mBody;
        mImageUri = builder.mImg;
        mPostDate = builder.mPostDate;
        mTitle = builder.mTitle;
        mProfilePic = builder.mProfilePic;
        mAuthorName = builder.mAuthorName;
        mLikes = builder.mLikes;
    }

    Post(Parcel in) {
        mAuthor = in.readString();
        mAuthorName = in.readString();
        mBody = in.readString();
        mTitle = in.readString();
        mLikes = in.readInt();
        mPostDate = (Date) in.readSerializable();
        mId = in.readString();
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

    public int getLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        mLikes = likes;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    public void addComment(Comment comment) {
        mComments.add(comment);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mLikes);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mId);
        dest.writeString(this.mAuthorName);
        dest.writeString(this.mBody);
        dest.writeString(this.mTitle);
        dest.writeSerializable(this.mPostDate);
    }

    public static class Builder {
        private String mTitle;
        private String mBody;
        private String mAuthor;
        private final Date mPostDate = new Date();
        private Uri mImg;
        private String mProfilePic;
        private String mAuthorName;
        private int mLikes;

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

        public Builder likes(int likes) {
            mLikes = likes;
            return this;
        }


        public Post build() {
            return new Post(this);
        }

    }
}
