package com.example.coronaview.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coronaview.R
import com.example.coronaview.common.Response
import com.example.coronaview.common.Status
import com.example.coronaview.data.api.model.CoronaEstatisticas
import com.example.coronaview.ui.adapter.EstadosAdapter
import com.example.coronaview.ui.viewModel.EstatisticasViewModel
import com.example.coronaview.utils.ConstantesRegiao.REGIAO
import com.example.coronaview.utils.ConstantesRegiao.SIGLAS_ESTADO
import com.example.coronaview.utils.CoronaEstatisticasAuxiliarExibirEstados
import com.example.coronaview.utils.CustomAlertDialogBuilder
import com.example.coronaview.utils.CustomAlertDialog
import com.example.coronaview.utils.ParserCoronaEstatisticasToListExibirDadosEstados
import java.util.*
import kotlin.collections.ArrayList


class EstadosFragment : Fragment() {

    companion object {
        fun newInstance() = EstadosFragment()
    }

    val viewModel = EstatisticasViewModel()

    var coronaEstatisticas : List<CoronaEstatisticas>? = null
    private val parser = ParserCoronaEstatisticasToListExibirDadosEstados()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            viewModel.responseCoronaEstatisticas.observe(it, Observer { response ->
                processResponse(response)
            })
        }
        viewModel.getCoronaEstatisticas()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_estados, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    private fun processResponse(response: Response) {
        when (response.status) {
            Status.SUCCESS -> responseSuccess(response.data)
            Status.ERROR -> responseFailure(response.error)
            else -> throw Exception("processResponse error")
        }
    }

    private fun responseSuccess(result : Any?){

        coronaEstatisticas = result as List<CoronaEstatisticas>
        coronaEstatisticas = replaceSiglaPorNome(coronaEstatisticas!!)

        var coronaEstatisticasPorEstado = parser.parseCoronaEstatisticasToListExibirDadosEstados(coronaEstatisticas!!)

        exibirPorEstado(coronaEstatisticasPorEstado)
    }

    private fun responseFailure(error : Throwable?) = Toast.makeText(activity,error?.message?:"Falha",Toast.LENGTH_SHORT).show()

    private fun exibirPorEstado(coronaEstatisticasPorEstado: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>){
        val r_view_estados = requireActivity().findViewById<RecyclerView>(R.id.recyclerViewEstatisticasPorEstado)
        r_view_estados.adapter = EstadosAdapter(coronaEstatisticasPorEstado,requireActivity().applicationContext)
        r_view_estados.layoutManager = LinearLayoutManager(requireActivity().applicationContext,LinearLayoutManager.VERTICAL,false)
        r_view_estados.visibility = View.VISIBLE
    }

    private fun replaceSiglaPorNome(coronaEstatisticas : List<CoronaEstatisticas>): List<CoronaEstatisticas> {

        coronaEstatisticas!!.map {itCoronaEstatisticas ->
            itCoronaEstatisticas.infectedByRegion.map {itCoronaByRegion->
                itCoronaByRegion.state = SIGLAS_ESTADO[itCoronaByRegion.state]
            }
            itCoronaEstatisticas.deceasedByRegion.map {itCoronaByRegion->
                itCoronaByRegion.state = SIGLAS_ESTADO[itCoronaByRegion.state]
            }
        }

        return coronaEstatisticas
    }

    fun onClickFAB(){

        val viewAlert = getViewAlertDialogPesquisar()
        val alertDialog = createAlertDialogPesquisar(context,viewAlert)
        alertDialog.show()
        gerenciarDialogo(alertDialog,viewAlert)

    }

    private fun gerenciarDialogo(alertDialog: CustomAlertDialog, viewAlert : View){

        var opcoesRegiao = arrayListOf("Todas") + REGIAO.keys.toTypedArray()
        var opcoesEstados = arrayListOf("Todos") + SIGLAS_ESTADO.values.toTypedArray()

        var spinnerRegiao = viewAlert.findViewById<Spinner>(R.id.spinnerRegiao)
        var spinnerEstado = viewAlert.findViewById<Spinner>(R.id.spinnerEstado)

        inserirOpcoesSpinner(opcoesEstados,spinnerEstado)
        inserirOpcoesSpinner(opcoesRegiao,spinnerRegiao)

        listenerSpinners(opcoesEstados,REGIAO,spinnerRegiao,spinnerEstado)

        alertDialog.positiveClick.observe(viewLifecycleOwner, Observer {
            if(it == true) {

                var radioGroupRegiao = viewAlert.findViewById<RadioGroup>(R.id.radioGroupOrdenar)
                var radioButtonRegiaoChecked = viewAlert.findViewById<RadioButton>(radioGroupRegiao.checkedRadioButtonId)

                var radioGroupOrdenarPor = viewAlert.findViewById<RadioGroup>(R.id.radioGroupOrdenarPor)
                var radioButtonEstadoChecked = viewAlert.findViewById<RadioButton>(radioGroupOrdenarPor.checkedRadioButtonId)

                var exibirDados = parser.parseCoronaEstatisticasToListExibirDadosEstados(coronaEstatisticas!!)

                var coronaEstatisticasFiltrado = filterListExibirDadosEstadosListFromAlertDialog(
                    exibirDados,
                    spinnerRegiao.selectedItem.toString(),
                    spinnerEstado.selectedItem.toString(),
                    radioButtonRegiaoChecked.text.toString(),
                    radioButtonEstadoChecked.text.toString()
                )
                exibirPorEstado(coronaEstatisticasFiltrado)
            }

        })

    }

    private fun createAlertDialogPesquisar(context: Context?, v : View): CustomAlertDialog {

        val alert = CustomAlertDialogBuilder(context)
            .customBuilder(
                v,
                "Pesquisar",
                "Cancelar")
        val customAlert = CustomAlertDialog(context,alert.create())
        customAlert.customButtonPositive(R.color.azul,14) //14 ->  default text size in android
        customAlert.customButtonNegative(R.color.azul,14) //14 ->  default text size in android

        return customAlert
    }

    private fun getViewAlertDialogPesquisar(): View =  layoutInflater.inflate(R.layout.alert_dialog_filtro_estados, null)

    private fun listenerSpinners(opcoesEstados: List<String>,regioes : Map<String,Array<String>>,spinnerRegiao: Spinner,spinnerEstado: Spinner){

        spinnerRegiao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var regiaoSelecionada = spinnerRegiao.selectedItem.toString()
                var opcoesAux = opcoesEstados

                if (regiaoSelecionada != "Todas") {
                    opcoesAux = arrayListOf("Todos") + opcoesEstados.filter { it!! in regioes[regiaoSelecionada]!! }
                }

                inserirOpcoesSpinner(opcoesAux,spinnerEstado)
            }
        }
    }

    private fun inserirOpcoesSpinner(opcoes : List<String>, spinner : Spinner){
        var adapter = getAdapterFromSpinner(opcoes)
        adapter.setDropDownViewResource(R.layout.simple_list_item_spinner)
        spinner.adapter = adapter
    }

    private fun getAdapterFromSpinner(opcoes: List<String>): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_list_item_spinner,
            opcoes)
    }
    /*private fun parseCoronaEstatisticasToListExibirDadosEstados(): ArrayList<CoronaEstatisticasAuxiliarExibirEstados> {
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
    }*/

    private fun filterListExibirDadosEstadosListFromAlertDialog(exibirDados:ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,regiaoSelecionada : String, estadoSelecionado : String, ordenarPor : String,exibirEmOrdem : String) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados> {

        var coronaEstatisticasPorEstadoFiltrado = filtrarEstadosAlertDiaolg(exibirDados,regiaoSelecionada,estadoSelecionado)
        var coronaEstatisticasOrdenado = ordenarEstadosAlertDialog(coronaEstatisticasPorEstadoFiltrado ,ordenarPor,exibirEmOrdem)

        return coronaEstatisticasOrdenado
    }

    private fun filtrarEstadosAlertDiaolg(exibirDados:ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,regiaoSelecionada : String, estadoSelecionado : String) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados>{
        return ArrayList<CoronaEstatisticasAuxiliarExibirEstados>(
            exibirDados.filter {
                (regiaoSelecionada == "Todas" && estadoSelecionado == "Todos") ||
                        (regiaoSelecionada == "Todas" && it.estado == estadoSelecionado)||
                        (regiaoSelecionada != "Todas" && it.estado!! in REGIAO[regiaoSelecionada]!! && estadoSelecionado == "Todos")||
                        (regiaoSelecionada != "Todas" && it.estado == estadoSelecionado)
            })
    }
    private fun ordenarEstadosAlertDialog(coronaEstatisticasFiltrado: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,ordenarPor : String,exibirEmOrdem : String): ArrayList<CoronaEstatisticasAuxiliarExibirEstados> {

        var ordenado = ArrayList<CoronaEstatisticasAuxiliarExibirEstados>()

        when(ordenarPor){
            "Ordem Alfabética"  -> ordenado = ordenarPorOrdemAlfabetica(coronaEstatisticasFiltrado,exibirEmOrdem)
            "Novos Casos"       -> ordenado = ordenarPorNovosCasos(coronaEstatisticasFiltrado,exibirEmOrdem)
            "Total de Casos"    -> ordenado = ordenarPorTotalDeCasos(coronaEstatisticasFiltrado,exibirEmOrdem)
            "Novos Óbitos"      -> ordenado = ordenarPorNovosObitos(coronaEstatisticasFiltrado,exibirEmOrdem)
            "Total de Óbitos"   -> ordenado = ordenarPorTotalDeObitos(coronaEstatisticasFiltrado,exibirEmOrdem)
        }

        return ordenado
    }

    private fun ordenarPorOrdemAlfabetica(coronaEstatisticasFiltrado: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,exibirEmOrdem:String) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados>{
        var ordenado = coronaEstatisticasFiltrado

        if(exibirEmOrdem == "Crescente") ordenado.sortBy{ it.estado}
        else                             ordenado.sortByDescending{it.estado}
        return ordenado

    }

    private fun ordenarPorNovosCasos(coronaEstatisticasFiltrado: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,exibirEmOrdem:String) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados>{
        var ordenado = coronaEstatisticasFiltrado

        if(exibirEmOrdem == "Crescente") ordenado.sortBy{ it.novosCasos}
        else                             ordenado.sortBy{it.novosCasos}
        return ordenado
    }

    private fun ordenarPorTotalDeCasos(coronaEstatisticasFiltrado: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,exibirEmOrdem:String) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados>{
        var ordenado = coronaEstatisticasFiltrado

        if(exibirEmOrdem == "Crescente") ordenado.sortBy{ it.totalCasos}
        else                             ordenado.sortByDescending{it.totalCasos}
        return ordenado
    }

    private fun ordenarPorNovosObitos(coronaEstatisticasFiltrado: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,exibirEmOrdem:String) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados>{
        var ordenado = coronaEstatisticasFiltrado

        if(exibirEmOrdem == "Crescente") ordenado.sortBy{ it.novosObitos}
        else                             ordenado.sortByDescending{it.novosObitos}
        return ordenado
    }

    private fun ordenarPorTotalDeObitos(coronaEstatisticasFiltrado: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,exibirEmOrdem:String) : ArrayList<CoronaEstatisticasAuxiliarExibirEstados>{
        var ordenado = coronaEstatisticasFiltrado

        if(exibirEmOrdem == "Crescente") ordenado.sortBy{ it.totalObitos}
        else                             ordenado.sortByDescending{it.totalObitos}
        return ordenado
    }

}
