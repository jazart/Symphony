package com.jazart.symphony.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Date

class Song {

    @Expose
    @SerializedName("name")
    var name: String? = null

    @Expose
    @SerializedName("artists")
    var artists: List<String>? = null

    @Expose
    @SerializedName("date")
    var date: Date? = null

    @Expose
    @SerializedName("uri")
    var uri: String? = null

    //private long mLength;

    @Expose
    @SerializedName("likes")
    var likes: Int = 0

    @Expose
    @SerializedName("author")
    var author: String? = null


    constructor() {
        //mArtists = new List<String>();
        date = Date()
        likes = 0

    }

    constructor(artists: List<String>, author: String, date: Date, likes: Int, name: String, uri: String) {
        this.name = name
        this.artists = artists
        this.uri = uri
        this.likes = likes
        this.date = date
        this.author = author

    }
}
