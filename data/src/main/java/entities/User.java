//package entities;
//
//
//import android.net.Uri;
//
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.GeoPoint;
//import com.google.gson.annotations.SerializedName;
//
//import java.util.Date;
//import java.util.List;
//
//public class User {
//
//    @SerializedName("id")
//    private String mId;
//
//    @SerializedName("name")
//    private String mName;
//
//    @SerializedName("date_joined")
//    private Date mDateJoined = new Date();
//
//    @SerializedName("friends")
//    private List<String> mFriends;
//
//    @SerializedName("numSongs")
//    private int mNumSongs;
//
//    @SerializedName("numPlays")
//    private int mNumPlays;
//
//    @SerializedName("location")
//    private GeoPoint mLocation;
//
//    @SerializedName("city")
//    private String mCity;
//
//    @SerializedName("photo")
//    private Uri photoURI;
//
//    public User() {
//
//    }
//
//    public User(FirebaseUser authUser) {
//        mId = authUser.getUid();
//        mName = authUser.getDisplayName();
//    }
//
//    public String getName() {
//        return mName;
//    }
//
//    public void setName(String name) {
//        mName = name;
//    }
//
//    public String getId() {
//        return mId;
//    }
//
//    public void setId(String id) {
//        mId = id;
//    }
//
//    public Date getDateJoined() {
//        return mDateJoined;
//    }
//
//    public void setDateJoined(Date dateJoined) {
//        mDateJoined = dateJoined;
//    }
//
//    public List<String> getFriends() {
//        return mFriends;
//    }
//
//    public void setFriends(List<String> friends) {
//        mFriends = friends;
//    }
//
//    public int getNumSongs() {
//        return mNumSongs;
//    }
//
//    public void setNumSongs(int numSongs) {
//        mNumSongs = numSongs;
//    }
//
//    public int getNumPlays() {
//        return mNumPlays;
//    }
//
//    public void setNumPlays(int numPlays) {
//        mNumPlays = numPlays;
//    }
//
//    public GeoPoint getLocation() {
//        return mLocation;
//    }
//
//    public void setLocation(GeoPoint point) {
//        mLocation = point;
//    }
//
//    public String getCity() {
//        return mCity;
//    }
//
//    public void setCity(String city) {
//        mCity = city;
//    }
//
//    public Uri getPhotoURI() {
//        return photoURI;
//    }
//
//    public void setPhotoURI(Uri photoURI) {
//        this.photoURI = photoURI;
//    }
//}