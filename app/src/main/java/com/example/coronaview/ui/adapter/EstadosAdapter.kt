package com.example.coronaview.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coronaview.R
import com.example.coronaview.data.api.model.CoronaByRegion
import com.example.coronaview.data.api.model.CoronaEstatisticas
import com.example.coronaview.utils.CoronaEstatisticasAuxiliarExibirEstados
import kotlinx.android.synthetic.main.recycler_view_inflar_estado.view.*

class EstadosAdapter(private val coronaEstatisticas: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>,
                     private val contexto : Context) : RecyclerView.Adapter<EstadosAdapter.MyViewHolder>(){
    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(contexto).inflate(R.layout.recycler_view_inflar_estado,parent,false))
    }

    override fun getItemCount(): Int {
        //Todos os estados do Brasil
        return coronaEstatisticas.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.textViewEstado.text = coronaEstatisticas[position].estado
        holder.itemView.textViewNumeroNovosCasosEstado.text  = coronaEstatisticas[position].novosCasos.toString()
        holder.itemView.textViewNumeroTotalCasosEstado.text  = coronaEstatisticas[position].totalCasos.toString()
        holder.itemView.textViewNumeroNovosObitosEstado.text = coronaEstatisticas[position].novosObitos.toString()
        holder.itemView.textViewNumeroTotalObitosEstado.text = coronaEstatisticas[position].totalObitos.toString()

    }

}