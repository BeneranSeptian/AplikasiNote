package com.septian.nurfaozy.aplikasinote.network


import com.septian.nurfaozy.aplikasinote.models.DirectionResponses
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {

    @GET("maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String = "AIzaSyBpB0xoX5lMPToqL_ShejCixY_5-q22pa0"
    ): Response<DirectionResponses>
}