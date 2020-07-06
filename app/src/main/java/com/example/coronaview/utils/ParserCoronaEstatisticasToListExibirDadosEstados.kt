package com.example.coronaview.utils

import com.example.coronaview.data.api.model.CoronaEstatisticas

class ParserCoronaEstatisticasToListExibirDadosEstados {

    fun parseCoronaEstatisticasToListExibirDadosEstados(coronaEstatisticas: List<CoronaEstatisticas>) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados>{
        var exibirDados = ArrayList<CoronaEstatisticasAuxiliarExibirEstados>()
        for (i in 0..coronaEstatisticas?.get(0)!!.infectedByRegion.size-1){
            var estado    = coronaEstatisticas?.get(0)!!.infectedByRegion[i].state!!
            var casosNovos  = coronaEstatisticas?.get(0)!!.infectedByRegion[i].count!! - coronaEstatisticas?.get(1)!!.infectedByRegion[i].count!!
            var obitosNovos = coronaEstatisticas?.get(0)!!.deceasedByRegion[i].count!! - coronaEstatisticas?.get(1)!!.deceasedByRegion[i].count!!
            var totalCasos  = coronaEstatisticas?.get(0)!!.infectedByRegion[i].count!!
            var totalObitos = coronaEstatisticas?.get(0)!!.deceasedByRegion[i].count!!

            exibirDados.add(CoronaEstatisticasAuxiliarExibirEstados(
                estado,
                casosNovos,
                totalCasos,
                obitosNovos,
                totalObitos
            ))

        }

        return exibirDados
    }
}