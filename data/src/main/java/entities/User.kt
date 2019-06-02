package entities

import java.io.Serializable
import java.util.*

data class User @JvmOverloads constructor(var id: String = "",
                                          var name: String = "",
                                          var dateJoined: Date = Date(),
                                          var friends: List<String> = listOf(),
                                          var numPlays: Int = 0,
                                          var city: String = "",
                                          var photoUri: String = "") : Serializable