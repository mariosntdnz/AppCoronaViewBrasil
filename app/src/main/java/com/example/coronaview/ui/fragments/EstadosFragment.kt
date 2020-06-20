package com.example.coronaview.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.solver.widgets.WidgetContainer
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
import com.google.android.material.resources.TextAppearance


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

    private val regiao_sudeste = arrayOf("RJ","SP","ES","MG")
    private val regiao_nordeste = arrayOf("MA","PI","RN","CE","PB","BA","PE","AL","SE")
    private val regiao_norte = arrayOf("AM","AC","RO","RR","AP","PA","TO")
    private val regiao_sul = arrayOf("RS","SC","PR")
    private val regiao_centroOeste = arrayOf("GO","MT","MS","DF")

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
        exibeAlertDialog()
    }

    fun exibeAlertDialog(){
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

        val spinnerRegiao = v.findViewById<Spinner>(R.id.spinnerRegiao)
        val adapterRegiao = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_list_item_spinner,
            arrayListOf("Todas") + REGIAO.keys.toTypedArray())
        adapterRegiao.setDropDownViewResource(R.layout.simple_list_item_spinner)
        spinnerRegiao.adapter = adapterRegiao

        val spinnerEstado = v.findViewById<Spinner>(R.id.spinnerEstado)
        val adapterEstado = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_list_item_spinner,
            arrayListOf("Todos") + SIGLAS_ESTADO.values.toTypedArray())
        adapterEstado.setDropDownViewResource(R.layout.simple_list_item_spinner)
        spinnerEstado.adapter = adapterEstado
    }
}
