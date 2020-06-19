package com.example.coronaview.data.api.model

import java.io.Serializable

class CoronaByRegion(
    var state : String?,
    val count : Int?
) : Serializable