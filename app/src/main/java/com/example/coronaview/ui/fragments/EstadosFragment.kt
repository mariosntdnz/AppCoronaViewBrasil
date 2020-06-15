package com.example.coronaview.ui.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.coronaview.R
import com.example.coronaview.ui.viewModel.EstatisticasViewModel

class EstadosFragment : Fragment() {

    companion object {
        fun newInstance() = EstadosFragment()
    }

    private lateinit var viewModel: EstatisticasViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_estados, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EstatisticasViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
