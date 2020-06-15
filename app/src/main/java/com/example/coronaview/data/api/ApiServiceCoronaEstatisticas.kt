package com.example.coronaview.data.api

import com.example.coronaview.data.api.model.CoronaEstatisticas
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceCoronaEstatisticas {

    @GET("v2/datasets/3S2T1ZBxB9zhRJTBB/items?format=json&clean=1&limit=2&desc=1")
    suspend fun getCoronaData() : Response<List<CoronaEstatisticas>>
}