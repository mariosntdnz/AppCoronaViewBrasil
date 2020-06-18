package com.example.coronaview.data.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RestService {

    private fun initRetrofit(): Retrofit {

    return Retrofit.Builder()
        .baseUrl("https://api.apify.com/")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun getRetrofit() = initRetrofit().create(ApiService::class.java)

}