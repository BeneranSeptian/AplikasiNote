package com.septian.nurfaozy.aplikasinote.models

import com.google.gson.annotations.SerializedName

data class Southwest(
    @SerializedName("lat")
    var lat: Double?,
    @SerializedName("lng")
    var lng: Double?
)
