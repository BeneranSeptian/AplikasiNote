package com.septian.nurfaozy.aplikasinote.models


import com.google.gson.annotations.SerializedName

data class Bounds(
    @SerializedName("northeast")
    var northeast: Northeast?,
    @SerializedName("southwest")
    var southwest: Southwest?
)