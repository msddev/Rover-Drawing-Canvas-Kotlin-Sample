package com.mkdev.roverapp.model

import com.google.gson.annotations.SerializedName

data class RoverCommandModel(

    @field:SerializedName("weirs")
    val weirs: List<WeirsItem> = listOf(),

    @field:SerializedName("start_point")
    val startPoint: StartPoint = StartPoint(),

    @field:SerializedName("command")
    val command: String = ""
)