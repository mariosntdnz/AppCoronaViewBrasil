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
import com.example.coronaview.utils.CoronaEstatisticasAuxiliarExibirEstados
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
        val v = inflater.inflate(R.layout.fragment_grafico_corona, container, false)

        checkBoxTodas       = v.findViewById<CheckBox>(R.id.checkBoxTodas)
        checkBoxSul         = v.findViewById<CheckBox>(R.id.checkBoxSul)
        checkBoxSudeste     = v.findViewById<CheckBox>(R.id.checkBoxSudeste)
        checkBoxCentroOeste = v.findViewById<CheckBox>(R.id.checkBoxCentroOeste)
        checkBoxNorte       = v.findViewById<CheckBox>(R.id.checkBoxNorte)
        checkBoxNordeste    = v.findViewById<CheckBox>(R.id.checkBoxNordeste)

        checkBoxObitos  = v.findViewById<CheckBox>(R.id.checkBoxObitos)
        checkBoxCasos   = v.findViewById<CheckBox>(R.id.checkBoxCasos)

        barChart = v.findViewById(R.id.barchart) as BarChart

        return v
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

        plotGrafico(exibirDados)
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

        data.barWidth = barWidth

        barChart.xAxis.axisMinimum = 1f
        barchart.xAxis.axisMaximum = labels.size - 1.1f

        if(data.dataSetCount >= 2) barChart.groupBars(1f,groupSpace,barSpace)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun plotGrafico(exibirDados: ArrayList<CoronaEstatisticasAuxiliarExibirEstados>){

        var barEntriesNovosCasos    = ArrayList<BarEntry>()
        var barEntriesTotalCasos    = ArrayList<BarEntry>()
        var barEntriesNovosObitos   = ArrayList<BarEntry>()
        var barEntriesTotalObitos   = ArrayList<BarEntry>()

        fun setBarEntriesForEmpty(){
            barEntriesNovosCasos    = ArrayList<BarEntry>()
            barEntriesTotalCasos    = ArrayList<BarEntry>()
            barEntriesNovosObitos   = ArrayList<BarEntry>()
            barEntriesTotalObitos   = ArrayList<BarEntry>()
        }

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

        fun intoValuesInBarData(){
            barDataSetNovosCasos = BarDataSet(barEntriesNovosCasos,"Novos Casos")
            barDataSetTotalCasos = BarDataSet(barEntriesTotalCasos,"Total Casos")
            barDataSetNovosObitos = BarDataSet(barEntriesNovosObitos,"Novos Óbitos")
            barDataSetTotalObitos = BarDataSet(barEntriesTotalObitos,"Total Óbitos")
        }

        fun setDeafultColorOfBarData(){
            barDataSetNovosCasos.setColor(Color.GRAY)
            barDataSetTotalCasos.setColor(Color.DKGRAY)
            barDataSetNovosObitos.setColor(Color.RED)
            barDataSetTotalObitos.setColor(Color.BLACK)
        }

        var dataSet = ArrayList<BarDataSet>()

        fun setDataSetForEmpty(){
            dataSet = ArrayList<BarDataSet>()
        }

        dataSet.add(barDataSetNovosCasos)
        dataSet.add(barDataSetTotalCasos)
        dataSet.add(barDataSetNovosObitos)
        dataSet.add(barDataSetTotalObitos)

        fun addAllBarDataInDataSet(casos : Boolean,obitos : Boolean){
            if(casos == true) {
                dataSet.add(barDataSetNovosCasos)
                dataSet.add(barDataSetTotalCasos)
            }
            if(obitos == true) {
                dataSet.add(barDataSetNovosObitos)
                dataSet.add(barDataSetTotalObitos)
            }
        }

        var mutable_datasets = dataSet as MutableList<IBarDataSet>
        var data = BarData(mutable_datasets)

        fun setDataWithDataSet(){
            mutable_datasets = dataSet as MutableList<IBarDataSet>
            data = BarData(mutable_datasets)
        }

        labelsEstados.sort()
        labelsEstados.add(0,"")
        labelsEstados.add(labelsEstados.size,"")

        @RequiresApi(Build.VERSION_CODES.N)
        fun prepareLabel(labels: ArrayList<String>) : ArrayList<String>{
            labels.removeIf { it == "" }
            labels.sort()
            labels.add(0,"")
            labels.add(labels.size,"")
            return labels
        }

        var barSpace    = 0.0f
        var groupSpace  = 0.2f
        var barWidth    = 0.2f
        // (barSpace + barWidth) * n° de dataset  + groupSpace = 1

        fun barWidthControl(value : Boolean){
            if(value == false) barWidth *= 2
            else                   barWidth /= 2
        }

        configureBarChart(labelsEstados,data,barSpace,groupSpace,barWidth)
        barchart.invalidate()

        var labels = labelsEstados

        fun setBarEntriesWithLabels(){
            for(i in 0..exibirDados.size-1){
                labels.map {
                    if(it == exibirDados[i].estado){
                        barEntriesNovosCasos.add(BarEntry(i.toFloat(),exibirDados[i].novosCasos.toFloat()))
                        barEntriesTotalCasos.add(BarEntry(i.toFloat(),exibirDados[i].totalCasos.toFloat()))
                        barEntriesNovosObitos.add(BarEntry(i.toFloat(),exibirDados[i].novosObitos.toFloat()))
                        barEntriesTotalObitos.add(BarEntry(i.toFloat(),exibirDados[i].totalObitos.toFloat()))
                    }
                }
            }
        }

        fun refreshGraph(value : Boolean,regiao : String){
            if(value){
                labels.addAll(SIGLAS_ESTADO.filter { it-> it.value in REGIAO[regiao]!! }.keys)
                labels = prepareLabel(labels)
            }
            else{
                labels = labels.filter {
                    it !in SIGLAS_ESTADO.filter { it-> it.value in REGIAO[regiao]!! }.keys
                }.toCollection(ArrayList())
            }

            setBarEntriesForEmpty()
            setBarEntriesWithLabels()
            intoValuesInBarData()
            setDeafultColorOfBarData()
            setDataSetForEmpty()
            addAllBarDataInDataSet(checkBoxCasos.isChecked,checkBoxObitos.isChecked)
            setDataWithDataSet()

            configureBarChart(labels,data,barSpace,groupSpace,barWidth)

            barChart.notifyDataSetChanged()
            barchart.invalidate()
        }

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
            refreshGraph(isChecked,"Sul")
        }

        checkBoxSudeste.setOnCheckedChangeListener { buttonView, isChecked ->
            refreshGraph(isChecked,"Sudeste")
        }
        checkBoxCentroOeste.setOnCheckedChangeListener { buttonView, isChecked ->
            refreshGraph(isChecked,"Centro-Oeste")
        }
        checkBoxNorte.setOnCheckedChangeListener { buttonView, isChecked ->
            refreshGraph(isChecked,"Norte")
        }
        checkBoxNordeste.setOnCheckedChangeListener { buttonView, isChecked ->
            refreshGraph(isChecked,"Nordeste")
        }

        fun deMorganAllCheckBoxRegions(){
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

            /*
                Para dar um refresh
                Executar o refreshGraph de todas regioes atualizando os dados
            */
        }

        checkBoxObitos.setOnCheckedChangeListener { buttonView, isChecked ->
            barWidthControl(isChecked)
            deMorganAllCheckBoxRegions()

        }
        checkBoxCasos.setOnCheckedChangeListener { buttonView, isChecked ->
            barWidthControl(isChecked)
            deMorganAllCheckBoxRegions()
        }

    }

    fun onClickFAB(){}
}