package com.example.socmed.models

import com.google.firebase.firestore.PropertyName

data class Post(
    var description : String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl : String = "",
    @get:PropertyName(  "creation_time_ms") @set:PropertyName(  "creation_time_ms") var creationTimeMs : Long = 0,
    // here the syntax we used was that of kotlin being camelcase
    // while the names as in the firebase was different
    // so we used getters and setters to balance it out

    var user: User? = null
)