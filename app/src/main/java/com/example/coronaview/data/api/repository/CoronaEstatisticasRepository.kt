package com.example.coronaview.data.api.repository

import com.example.coronaview.data.api.RestService
import com.example.coronaview.data.api.model.CoronaEstatisticas
import retrofit2.Response
import java.lang.Exception

class CoronaEstatisticasRepository {

    private val api = RestService

    suspend fun getCoronaEstatisticas(): Response<List<CoronaEstatisticas>>{
        try {

            val response = api.getRetrofit().getCoronaData()

            if(response.isSuccessful) return response
            else throw Exception("Erro ao fazer a requisição")

        }
        catch (e: Exception){

            println(e.message)
            throw Exception(e.message)

        }
    }

    fun getUltimoUpDateFromlastUpdatedAtSource(coronaLastUpdate : String): String{
        var separarDataEHora = coronaLastUpdate.split("T")
        var data = separarDataEHora[0].split("-")
        var hora = separarDataEHora[1].split(":")
        data = data.reversed()
        hora = hora.subList(0,hora.size-1)

        return data.joinToString(separator = "/")  + " às " + hora.joinToString(separator = ":")
    }

    fun getNovosCasos(coronaCasos1 : CoronaEstatisticas,coronaCasos2: CoronaEstatisticas): Int{
        return Math.abs(coronaCasos1.infected!! - coronaCasos2.infected!!)
    }

    fun getNovosObitos(coronaCasos1 : CoronaEstatisticas,coronaCasos2: CoronaEstatisticas): Int{
        return Math.abs(coronaCasos1.deceased!! - coronaCasos2.deceased!!)
    }

    fun getNovosRecuperados(coronaCasos1 : CoronaEstatisticas,coronaCasos2: CoronaEstatisticas): Int{
        return Math.abs(coronaCasos1.recovered!! - coronaCasos2.recovered!!)
    }
}