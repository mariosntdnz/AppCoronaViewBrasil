package com.example.coronaview.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coronaview.R
import com.example.coronaview.data.api.model.CoronaByRegion
import com.example.coronaview.data.api.model.CoronaEstatisticas
import kotlinx.android.synthetic.main.recycler_view_inflar_estado.view.*

class EstadosAdapter(private val coronaEstatisticas: List<CoronaEstatisticas>,
                     private val contexto : Context) : RecyclerView.Adapter<EstadosAdapter.MyViewHolder>(){
    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(contexto).inflate(R.layout.recycler_view_inflar_estado,parent,false))
    }

    override fun getItemCount(): Int {
        //Todos os estados do Brasil
        return coronaEstatisticas[0]?.infectedByRegion.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.textViewEstado.text = coronaEstatisticas[0].infectedByRegion[position].state

        holder.itemView.textViewNumeroNovosCasosEstado.text  = (coronaEstatisticas[0].infectedByRegion[position].count?.minus(
            coronaEstatisticas[1].infectedByRegion[position].count!!
        )).toString()
        holder.itemView.textViewNumeroTotalCasosEstado.text  = coronaEstatisticas[0].infectedByRegion[position].count.toString()
        holder.itemView.textViewNumeroNovosObitosEstado.text = (coronaEstatisticas[0].deceasedByRegion[position].count?.minus(
            coronaEstatisticas[1].deceasedByRegion[position].count!!
        )).toString()
        holder.itemView.textViewNumeroTotalObitosEstado.text = coronaEstatisticas[0].deceasedByRegion[position].count.toString()

    }

}