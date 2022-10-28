package com.example.snapshots.Data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SnapShot(
    val id: String = "",
    val title: String = "",
    val photoUrl: String = "",
    val likeList: Map<String, Boolean> = mutableMapOf()
)
