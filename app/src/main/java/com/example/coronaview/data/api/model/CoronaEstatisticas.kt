package com.example.coronaview.data.api.model

import java.io.Serializable

class CoronaEstatisticas(
    val recovered  : Int?,
    val infected  : Int?,
    val deceased : Int?,
    val lastUpdatedAtSource : String?,
    val infectedByRegion : List<CoronaByRegion>,
    val deceasedByRegion : List<CoronaByRegion>
) : Serializable