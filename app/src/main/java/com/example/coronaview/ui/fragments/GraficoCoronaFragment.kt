package com.example.coronaview.ui.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.coronaview.R
import com.example.coronaview.common.Response
import com.example.coronaview.common.Status
import com.example.coronaview.data.api.model.CoronaEstatisticas
import com.example.coronaview.ui.viewModel.EstatisticasViewModel
import com.example.coronaview.utils.ConstantesRegiao.REGIAO
import com.example.coronaview.utils.ConstantesRegiao.SIGLAS_ESTADO
import com.example.coronaview.utils.ParserCoronaEstatisticasToListExibirDadosEstados
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_grafico_corona.*


class GraficoCoronaFragment : Fragment() {

    companion object {
        fun newInstance() = GraficoCoronaFragment()
    }

    private var viewModel = EstatisticasViewModel()

    private var coronaEstatisticas : List<CoronaEstatisticas>? = null
    private val parser = ParserCoronaEstatisticasToListExibirDadosEstados()

    private lateinit var barChart : BarChart
    private var labelsEstados = SIGLAS_ESTADO.keys.toTypedArray().toCollection(ArrayList())

    private lateinit var checkBoxTodas : CheckBox
    private lateinit var checkBoxSul   : CheckBox
    private lateinit var checkBoxSudeste     : CheckBox
    private lateinit var checkBoxCentroOeste : CheckBox
    private lateinit var checkBoxNorte       : CheckBox
    private lateinit var checkBoxNordeste    : CheckBox

    private lateinit var checkBoxObitos  : CheckBox
    private lateinit var checkBoxCasos   : CheckBox

    @RequiresApi(Build.VERSION_CODES.N)
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
        return inflater.inflate(R.layout.fragment_grafico_corona, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun processResponse(response: Response) {
        when (response.status) {
            Status.SUCCESS -> responseSuccess(response.data)
            Status.ERROR -> responseFailure(response.error)
            else -> throw Exception("processResponse error")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun responseSuccess(result : Any?){

        coronaEstatisticas = result as List<CoronaEstatisticas>
        var exibirDados = parser.parseCoronaEstatisticasToListExibirDadosEstados(coronaEstatisticas!!)
            exibirDados.sortBy { it.estado }

        checkBoxTodas       = requireActivity().findViewById<CheckBox>(R.id.checkBoxTodas)
        checkBoxSul         = requireActivity().findViewById<CheckBox>(R.id.checkBoxSul)
        checkBoxSudeste     = requireActivity().findViewById<CheckBox>(R.id.checkBoxSudeste)
        checkBoxCentroOeste = requireActivity().findViewById<CheckBox>(R.id.checkBoxCentroOeste)
        checkBoxNorte       = requireActivity().findViewById<CheckBox>(R.id.checkBoxNorte)
        checkBoxNordeste    = requireActivity().findViewById<CheckBox>(R.id.checkBoxNordeste)

        checkBoxObitos  = requireActivity().findViewById<CheckBox>(R.id.checkBoxObitos)
        checkBoxCasos   = requireActivity().findViewById<CheckBox>(R.id.checkBoxCasos)

        barChart = requireActivity().findViewById(R.id.barchart) as BarChart

        var barEntriesNovosCasos = ArrayList<BarEntry>()
        var barEntriesTotalCasos = ArrayList<BarEntry>()
        var barEntriesNovosObitos = ArrayList<BarEntry>()
        var barEntriesTotalObitos = ArrayList<BarEntry>()

        for(i in 0..exibirDados.size-1) {

            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
        }

        var barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
        barDataSetNovosCasos.setColor(Color.GRAY)

        var barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
        barDataSetTotalCasos.setColor(Color.DKGRAY)

        var barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
        barDataSetNovosObitos.setColor(Color.RED)

        var barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
        barDataSetTotalObitos.setColor(Color.BLACK)

        var dataSet = ArrayList<BarDataSet>()

        dataSet.add(barDataSetNovosCasos)
        dataSet.add(barDataSetTotalCasos)
        dataSet.add(barDataSetNovosObitos)
        dataSet.add(barDataSetTotalObitos)

        var mutable_datasets = dataSet as MutableList<IBarDataSet>
        var  data = BarData(mutable_datasets)

        labelsEstados.sort()
        labelsEstados.add(0,"")
        labelsEstados.add(labelsEstados.size,"")

        var barSpace = 0.0f
        var groupSpace = 0.2f
        var barWidth = 0.2f
        // (barSpace + barWidth) * n° de dataset  + groupSpace = 1

        configureBarChart(labelsEstados,data,barSpace,groupSpace,barWidth)
        barchart.invalidate()

        var labels = labelsEstados

        checkBoxTodas.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == false){
                checkBoxSul.isChecked         = false
                checkBoxSudeste.isChecked     = false
                checkBoxCentroOeste.isChecked = false
                checkBoxNorte.isChecked       = false
                checkBoxNordeste.isChecked    = false
            }
            else{
                checkBoxSul.isChecked         = true
                checkBoxSudeste.isChecked     = true
                checkBoxCentroOeste.isChecked = true
                checkBoxNorte.isChecked       = true
                checkBoxNordeste.isChecked    = true
            }
        }
        checkBoxSul.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == false){

                labels = labels.filter {
                    it !in SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Sul"]!! }.keys
                }.toCollection(ArrayList())

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)

                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
            else{

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                labels.addAll(SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Sul"]!! }.keys)
                labels.removeIf { it == "" }
                labels.sort()
                labels.add(0,"")
                labels.add(labels.size,"")

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)

                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
        }
        checkBoxSudeste.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == false){

                labels = labels.filter {
                    it !in SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Sudeste"]!! }.keys
                }.toCollection(ArrayList())

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)
                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
            else{

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                labels.addAll(SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Sudeste"]!! }.keys)
                labels.removeIf { it == "" }
                labels.sort()
                labels.add(0,"")
                labels.add(labels.size,"")

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)

                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
        }
        checkBoxCentroOeste.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == false){

                labels = labels.filter {
                    it !in SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Centro-Oeste"]!! }.keys
                }.toCollection(ArrayList())

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)
                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
            else{

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                labels.addAll(SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Centro-Oeste"]!! }.keys)
                labels.removeIf { it == "" }
                labels.sort()
                labels.add(0,"")
                labels.add(labels.size,"")

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)

                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
        }
        checkBoxNorte.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == false){

                labels = labels.filter {
                    it !in SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Norte"]!! }.keys
                }.toCollection(ArrayList())

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)
                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
            else{

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                labels.addAll(SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Norte"]!! }.keys)
                labels.removeIf { it == "" }
                labels.sort()
                labels.add(0,"")
                labels.add(labels.size,"")

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)

                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
        }
        checkBoxNordeste.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == false){

                labels = labels.filter {
                    it !in SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Nordeste"]!! }.keys
                }.toCollection(ArrayList())

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)
                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
            else{

                barEntriesNovosCasos = ArrayList<BarEntry>()
                barEntriesTotalCasos = ArrayList<BarEntry>()
                barEntriesNovosObitos = ArrayList<BarEntry>()
                barEntriesTotalObitos = ArrayList<BarEntry>()

                labels.addAll(SIGLAS_ESTADO.filter { it-> it.value in REGIAO["Nordeste"]!! }.keys)
                labels.removeIf { it == "" }
                labels.sort()
                labels.add(0,"")
                labels.add(labels.size,"")

                for(i in 0..exibirDados.size-1) {
                    labels.map {
                        if(it == exibirDados[i].estado){
                            barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                            barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                            barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                            barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                        }
                    }
                }

                barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
                barDataSetNovosCasos.setColor(Color.GRAY)

                barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
                barDataSetTotalCasos.setColor(Color.DKGRAY)

                barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
                barDataSetNovosObitos.setColor(Color.RED)

                barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
                barDataSetTotalObitos.setColor(Color.BLACK)

                dataSet = ArrayList<BarDataSet>()

                if(checkBoxCasos.isChecked == true) {
                    dataSet.add(barDataSetNovosCasos)
                    dataSet.add(barDataSetTotalCasos)
                }
                if(checkBoxObitos.isChecked == true) {
                    dataSet.add(barDataSetNovosObitos)
                    dataSet.add(barDataSetTotalObitos)
                }

                mutable_datasets = dataSet as MutableList<IBarDataSet>
                data = BarData(mutable_datasets)

                configureBarChart(labels,data,barSpace,groupSpace,barWidth)

                barChart.notifyDataSetChanged()
                barchart.invalidate()
            }
        }

        checkBoxObitos.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == false) barWidth *= 2
            else                   barWidth /= 2
            checkBoxCentroOeste.isChecked   = !checkBoxCentroOeste.isChecked
            checkBoxNordeste.isChecked      = !checkBoxNordeste.isChecked
            checkBoxNorte.isChecked         = !checkBoxNorte.isChecked
            checkBoxSudeste.isChecked       = !checkBoxSudeste.isChecked
            checkBoxSul.isChecked           = !checkBoxSul.isChecked
            checkBoxCentroOeste.isChecked   = !checkBoxCentroOeste.isChecked
            checkBoxNordeste.isChecked      = !checkBoxNordeste.isChecked
            checkBoxNorte.isChecked         = !checkBoxNorte.isChecked
            checkBoxSudeste.isChecked       = !checkBoxSudeste.isChecked
            checkBoxSul.isChecked           = !checkBoxSul.isChecked

        }
        checkBoxCasos.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked == false) barWidth *= 2
            else                   barWidth /= 2

            checkBoxCentroOeste.isChecked   = !checkBoxCentroOeste.isChecked
            checkBoxNordeste.isChecked      = !checkBoxNordeste.isChecked
            checkBoxNorte.isChecked         = !checkBoxNorte.isChecked
            checkBoxSudeste.isChecked       = !checkBoxSudeste.isChecked
            checkBoxSul.isChecked           = !checkBoxSul.isChecked
            checkBoxCentroOeste.isChecked   = !checkBoxCentroOeste.isChecked
            checkBoxNordeste.isChecked      = !checkBoxNordeste.isChecked
            checkBoxNorte.isChecked         = !checkBoxNorte.isChecked
            checkBoxSudeste.isChecked       = !checkBoxSudeste.isChecked
            checkBoxSul.isChecked           = !checkBoxSul.isChecked
        }
    }

    private fun responseFailure(error : Throwable?) = Toast.makeText(activity,error?.message?:"Falha", Toast.LENGTH_SHORT).show()

    private fun configureBarChart(labels : ArrayList<String>, data : BarData,barSpace : Float,groupSpace : Float,barWidth : Float){

        barChart.data = data

        var xAxis = barChart.xAxis
        xAxis.setValueFormatter(object : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                return if( value >= 0 && value < labels.size) labels.get(value.toInt()) else ""
            }

            override fun getDecimalDigits(): Int {
                return labels.size
            }

        })

        xAxis.setCenterAxisLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        barChart.isDragEnabled = true
        barchart.setVisibleXRangeMaximum(3.toFloat())
        // (barSpace + barWidth) * 4(n de dataset)  + groupSpace = 1
        //var barSpace = 0.0f
        //var groupSpace = 0.2f
        //data.barWidth = 0.2f

        data.barWidth = barWidth

        barChart.xAxis.axisMinimum = 1f
        barchart.xAxis.axisMaximum = labels.size - 1.1f
        //barchart.axisLeft.axisMinimum = 0f

        if(data.dataSetCount >= 2) barChart.groupBars(1f,groupSpace,barSpace)
    }

    fun onClickFAB(){}
}