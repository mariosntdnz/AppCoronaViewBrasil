package com.example.coronaview.utils.alertDialog

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.coronaview.R
import com.example.coronaview.utils.CustomAlertDialog
import kotlinx.android.synthetic.main.alert_dialog_filtro_estados.view.*

class AlertDialogManagerPesquisar(val context : Context,
                                  val customAlertDialog: CustomAlertDialog,
                                  val viewAlert : View
                                  ){


    fun exibeAlerta() = customAlertDialog.show()

    fun inserirOpcoesSpinner(opcoes: List<String>,spinner : String){
        if(spinner == "Região") inserirOpcoesSpinnerRegiao(opcoes)
        else                    inserirOpcoesSpinnerEstado(opcoes)
    }

    fun listenerSpinners(opcoesEstados: List<String>,regioes : Map<String,Array<String>>){

        var opcoesEstado = opcoesEstados

        viewAlert.spinnerRegiao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var regiaoSelecionada = viewAlert.spinnerRegiao.selectedItem.toString()
                var opcoesAux = opcoesEstado

                if (regiaoSelecionada != "Todas") {
                    opcoesAux = arrayListOf("Todos") + opcoesEstado.filter { it!! in regioes[regiaoSelecionada]!! }

                }

                inserirOpcoesSpinner(opcoesAux,"Estado")
            }
        }
    }

    private fun inserirOpcoesSpinnerRegiao(opcoesRegiao : List<String>){
        var adapter = getAdapterFromSpinner(opcoesRegiao)
        adapter.setDropDownViewResource(R.layout.simple_list_item_spinner)
        viewAlert.spinnerRegiao.adapter = adapter
    }

    private fun inserirOpcoesSpinnerEstado(opcoesEstado : List<String>){
        var adapter = getAdapterFromSpinner(opcoesEstado)
        adapter.setDropDownViewResource(R.layout.simple_list_item_spinner)
        viewAlert.spinnerEstado.adapter = adapter
    }

    private fun getAdapterFromSpinner(opcoes: List<String>): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            context,
            R.layout.simple_list_item_spinner,
            opcoes)
    }

}