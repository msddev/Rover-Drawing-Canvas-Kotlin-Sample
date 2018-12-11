package com.mkdev.roverapp.api

import com.mkdev.roverapp.model.RoverCommandModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {

    @FormUrlEncoded
    @POST("/")
    fun getRoverCommand(@Field("rover_id") roverId: String): Call<RoverCommandModel>
}