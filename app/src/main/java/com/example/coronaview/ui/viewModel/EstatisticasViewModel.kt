package com.example.coronaview.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coronaview.data.api.repository.CoronaEstatisticasRepository
import com.example.coronaview.common.Response
import kotlinx.coroutines.launch

class EstatisticasViewModel : ViewModel() {

    val coronaEstatisticasRepository = CoronaEstatisticasRepository()
    val responseCoronaEstatisticas = MutableLiveData<Response>()

    fun getCoronaEstatisticas(){
        viewModelScope.launch {
            try {
                val requisicao = coronaEstatisticasRepository.getCoronaEstatisticas()
                responseCoronaEstatisticas.postValue(Response.success(requisicao.body()))
            }
            catch (t : Throwable){
                println(t.message + "getCoronaModel viewModel")
                responseCoronaEstatisticas.postValue(Response.error(t))
            }
        }
    }
}
