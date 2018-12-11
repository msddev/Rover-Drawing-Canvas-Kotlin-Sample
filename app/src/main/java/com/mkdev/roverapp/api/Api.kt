package com.mkdev.roverapp.api

import com.mkdev.roverapp.model.RoverCommandModel
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {

    @FormUrlEncoded
    @POST("/")
    fun getRoverCommand(@Field("rover_id") roverId: String): Single<RoverCommandModel>
}