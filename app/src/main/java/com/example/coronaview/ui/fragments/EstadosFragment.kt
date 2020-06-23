package com.example.coronaview.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coronaview.R
import com.example.coronaview.common.Response
import com.example.coronaview.common.Status
import com.example.coronaview.data.api.model.CoronaEstatisticas
import com.example.coronaview.ui.adapter.EstadosAdapter
import com.example.coronaview.ui.viewModel.EstatisticasViewModel
import com.example.coronaview.utils.alertDialog.AlertDialogManagerPesquisar
import com.example.coronaview.utils.CustomAlertDialogBuilder
import com.example.coronaview.utils.CustomAlertDialog
import kotlinx.android.synthetic.main.alert_dialog_filtro_estados.*


class EstadosFragment : Fragment() {

    companion object {
        fun newInstance() = EstadosFragment()
    }

    val viewModel = EstatisticasViewModel()

    private val SIGLAS_ESTADO = mapOf<String,String>(
        "AC" to "Acre",
        "AL" to "Alagoas" ,
        "AP" to "Amapá" ,
        "AM" to "Amazonas",
        "BA" to "Bahia",
        "CE" to "Ceará",
        "DF" to "Distrito Federal"  ,
        "ES" to "Espírito Santo" ,
        "GO" to "Goiás",
        "MA" to "Maranhão" ,
        "MT" to "Mato Grosso" ,
        "MS" to "Mato Grosso do Sul",
        "MG" to "Minas Gerais" ,
        "PA" to "Pará" ,
        "PB" to "Paraíba",
        "PR" to "Paraná",
        "PE" to "Pernambuco",
        "PI" to "Piauí",
        "RJ" to "Rio de Janeiro",
        "RN" to "Rio Grande do Norte" ,
        "RS" to "Rio Grande Sul" ,
        "RO" to "Rondônia",
        "RR" to "Roraima" ,
        "SC" to "Santa Catarina",
        "SP" to "São Paulo" ,
        "SE" to "Sergipe" ,
        "TO" to "Tocantins"
    )

    private val regiao_sudeste = arrayOf("Rio de Janeiro","São Paulo","Espírito Santo","Minas Gerais")
    private val regiao_nordeste = arrayOf("Maranhão","Piauí","Rio Grande do Norte","Ceará","Paraíba","Bahia","Pernambuco","Alagoas","Sergipe")
    private val regiao_norte = arrayOf("Amazonas","Acre","Rondônia","Roraima","Amapá","Pará","Tocantins")
    private val regiao_sul = arrayOf("Rio Grande Sul","Santa Catarina","Paraná")
    private val regiao_centroOeste = arrayOf("Goiás","Mato Grosso","Mato Grosso do Sul","Distrito Federal")

    private val REGIAO = mapOf<String,Array<String>>(
        "Sudeste"       to regiao_sudeste,
        "Nordeste"      to regiao_nordeste,
        "Norte"         to regiao_norte,
        "Sul"           to regiao_sul,
        "Centro-Oeste"  to regiao_centroOeste
    )

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
        val r_view_estados = requireActivity().findViewById<RecyclerView>(R.id.recyclerViewEstatisticasPorEstado)
        val coronaEstatisticas = result as List<CoronaEstatisticas>

        coronaEstatisticas.map {itCoronaEstatisticas ->
            itCoronaEstatisticas.infectedByRegion.map {itCoronaByRegion->
                itCoronaByRegion.state = SIGLAS_ESTADO[itCoronaByRegion.state]
            }
            itCoronaEstatisticas.deceasedByRegion.map {itCoronaByRegion->
                itCoronaByRegion.state = SIGLAS_ESTADO[itCoronaByRegion.state]
            }
        }

        r_view_estados.adapter = EstadosAdapter(coronaEstatisticas,requireActivity().applicationContext)
        r_view_estados.layoutManager = LinearLayoutManager(requireActivity().applicationContext,LinearLayoutManager.VERTICAL,false)
        r_view_estados.visibility = View.VISIBLE

    }

    private fun responseFailure(error : Throwable?) = Toast.makeText(activity,error?.message?:"Falha",Toast.LENGTH_SHORT).show()

    fun onClickFAB(){

        val viewAlert = getViewAlertDialogPesquisar()
        val alertDialog = createAlertDialogPesquisar(context,viewAlert)
        val alertDialogManagerPesquisar = AlertDialogManagerPesquisar(requireContext(),alertDialog,viewAlert)

        alertDialogManagerPesquisar.exibeAlerta()
        alertDialogManagerPesquisar.inserirOpcoesSpinner(arrayListOf("Todas") + REGIAO.keys.toTypedArray(),"Região")
        alertDialogManagerPesquisar.inserirOpcoesSpinner(arrayListOf("Todos") + SIGLAS_ESTADO.values.toTypedArray(),"Estado")

        alertDialogManagerPesquisar.listenerSpinners(arrayListOf("Todos") + SIGLAS_ESTADO.values.toTypedArray(),REGIAO)

    }

    private fun createAlertDialogPesquisar(context: Context?, v : View): CustomAlertDialog {

        val alert = CustomAlertDialogBuilder(context)
            .customBuilder(
                v,
                "Cancelar",
                "Pesquisar")
        val customAlert = CustomAlertDialog(context,alert.create())
        customAlert.customButtonPositive(R.color.azul,14) //14 ->  default text size in android
        customAlert.customButtonNegative(R.color.azul,14) //14 ->  default text size in android

        return customAlert
    }

    private fun getViewAlertDialogPesquisar(): View =  layoutInflater.inflate(R.layout.alert_dialog_filtro_estados, null)

    /*fun exibeAlertDialog(){

        val v: View = layoutInflater.inflate(R.layout.alert_dialog_filtro_estados, null)
        val alert = AlertDialog.Builder(context)
        alert.setView(v)
        alert.setCancelable(true)
        alert.setPositiveButton("Pesquisar", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int -> })
        alert.setNeutralButton("Cancelar",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int -> })
        val alertSetColor = alert.create()
        alertSetColor.show()
        alertSetColor.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.azul))
        alertSetColor.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(resources.getColor(R.color.azul))

        val opcoesSpinnerRegiao = arrayListOf("Todas") + REGIAO.keys.toTypedArray()
        var opcoesSpinnerEstado = arrayListOf("Todos") + SIGLAS_ESTADO.values.toTypedArray()

        val spinnerRegiao = v.findViewById<Spinner>(R.id.spinnerRegiao)
        val adapterRegiao = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_list_item_spinner,
            opcoesSpinnerRegiao)
        adapterRegiao.setDropDownViewResource(R.layout.simple_list_item_spinner)
        spinnerRegiao.adapter = adapterRegiao

        var regiaoSelecionada = spinnerRegiao.selectedItem.toString()

        opcoesSpinnerEstado = opcoesSpinnerEstado
                              if (regiaoSelecionada == opcoesSpinnerRegiao[0])
                              else opcoesSpinnerEstado.filter { REGIAO[regiaoSelecionada]?.contains(it)!!}

        val spinnerEstado = v.findViewById<Spinner>(R.id.spinnerEstado)
        var adapterEstado = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_list_item_spinner,
            opcoesSpinnerEstado)
        adapterEstado.setDropDownViewResource(R.layout.simple_list_item_spinner)
        spinnerEstado.adapter = adapterEstado

        listenerSpinners(spinnerRegiao,spinnerEstado)

    }

    fun listenerSpinners(spinnerRegiao : Spinner,spinnerEstado : Spinner){

        var opcoesSpinnerEstado = arrayListOf("Todos") + SIGLAS_ESTADO.values.toTypedArray()

        spinnerRegiao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ){
                var regiaoSelecionada = spinnerRegiao.selectedItem.toString()
                var opcoesAux = opcoesSpinnerEstado
                if (regiaoSelecionada == "Todas")  opcoesSpinnerEstado = opcoesSpinnerEstado
                else  opcoesAux = opcoesSpinnerEstado.filter { it!! in REGIAO[regiaoSelecionada]!! }

                var adapterEstado = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.simple_list_item_spinner,
                    arrayOf("Todos") + opcoesAux)
                adapterEstado.setDropDownViewResource(R.layout.simple_list_item_spinner)
                spinnerEstado.adapter = adapterEstado
            }
        }
    }*/
}
