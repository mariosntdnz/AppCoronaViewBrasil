package com.example.coronaview.ui.fragments

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.coronaview.R
import com.example.coronaview.common.Response
import com.example.coronaview.common.Status
import com.example.coronaview.data.api.model.CoronaEstatisticas
import com.example.coronaview.ui.adapter.EstadosAdapter
import com.example.coronaview.ui.adapter.SectionsPagerAdapter
import com.example.coronaview.ui.viewModel.EstatisticasViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class EstadosFragment : Fragment() {

    companion object {
        fun newInstance() = EstadosFragment()
    }

    val viewModel = EstatisticasViewModel()

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

        r_view_estados.adapter = EstadosAdapter(coronaEstatisticas,requireActivity().applicationContext)
        r_view_estados.layoutManager = LinearLayoutManager(requireActivity().applicationContext,LinearLayoutManager.VERTICAL,false)
        r_view_estados.visibility = View.VISIBLE

    }

    private fun responseFailure(error : Throwable?) = Toast.makeText(activity,error?.message?:"Falha",Toast.LENGTH_SHORT).show()

}
