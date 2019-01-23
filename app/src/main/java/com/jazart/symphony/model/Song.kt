package com.jazart.symphony.model


import com.google.firebase.Timestamp

data class Song constructor(
        var name: String = "",
        var artists: List<String> = mutableListOf(),
        var date: Timestamp = Timestamp.now(),
        var uri: String = "",
        var likes: Int = 0,
        var author: String = "",
        var id: String = "")
