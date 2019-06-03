package entities


import java.io.Serializable
import java.util.*


data class Song constructor(
        var name: String = "",
        var artists: List<String> = mutableListOf(),
        var date: Date = Date(),
        var uri: String = "",
        var likes: Int = 0,
        var author: String = "",
        var id: String = "") : Serializable
