package com.example.coronaview.ui.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.coronaview.BR
import com.example.coronaview.R
import com.example.coronaview.common.Response
import com.example.coronaview.common.Status
import com.example.coronaview.data.api.model.CoronaEstatisticas
import com.example.coronaview.modelDataBinding.HomeFragmentDataBinding
import com.example.coronaview.ui.viewModel.EstatisticasViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_principal.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class HomeFragment : Fragment(){

    companion object {
        fun newInstance() = HomeFragment()
        @set:BindingAdapter("bind:isVisible")
        @JvmStatic
        var View.isVisible:Boolean
            get() = visibility == View.VISIBLE
            set(value){
                visibility = if (value) View.VISIBLE else View.INVISIBLE
            }
    }

    private lateinit var binding: ViewDataBinding
    private var homeBinding = getEmptyHomeFragmentDataBinding()
    //private var textViewUltimaAtt: String = ""

    val viewModel = EstatisticasViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            viewModel.responseCoronaEstatisticas.observe(it, Observer { response ->
                processResponse(response, requireActivity())
            })
        }
        viewModel.getCoronaEstatisticas()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_principal, container, false)
        binding = DataBindingUtil.bind(v)!!
        binding.setVariable(BR.data,homeBinding)
        return binding.getRoot()
    }

    private fun processResponse(response: Response, contexto : Context) {
        when (response.status) {
            Status.SUCCESS -> responseSuccess(response.data)
            Status.ERROR -> responseFailure(response.error)
            else -> throw Exception("processResponse error")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun responseSuccess(result : Any?) {
        
        var coronaEstatisticas = result as List<CoronaEstatisticas>

        homeBinding.apply {
                status = true
                casosNovos        = "\n" + viewModel.coronaEstatisticasRepository.getNovosCasos(coronaEstatisticas[0],coronaEstatisticas[1]).toString()
                obitosNovos       = "\n" + viewModel.coronaEstatisticasRepository.getNovosObitos(coronaEstatisticas[0],coronaEstatisticas[1]).toString()
                recuperadosNovos  = "\n" + viewModel.coronaEstatisticasRepository.getNovosRecuperados(coronaEstatisticas[0],coronaEstatisticas[1]).toString()
                casosTotais       = "\n" + coronaEstatisticas[0].infected.toString()
                obitosTotais      = "\n" + coronaEstatisticas[0].deceased.toString()
                recuperadosTotais = "\n" + coronaEstatisticas[0].recovered.toString()
                ultimaAtt         = "Atualizado em " +
                                    viewModel.coronaEstatisticasRepository.getUltimoUpDateFromlastUpdatedAtSource(
                                    coronaEstatisticas[0].lastUpdatedAtSource!!) +
                                    "\n- Última atualização disponível -"
        }
        binding.setVariable(BR.data,homeBinding)
        binding.executePendingBindings()
    }

    private fun responseFailure(error : Throwable?) = Toast.makeText(activity,error?.message?:"Falha",Toast.LENGTH_SHORT).show()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun getEmptyHomeFragmentDataBinding() : HomeFragmentDataBinding = HomeFragmentDataBinding(false,"","","","","","","")


    fun onClickFAB(){
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf<String>(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            PackageManager.PERMISSION_GRANTED
        )
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val view1: View = requireActivity().window.decorView.rootView
        view1.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view1.drawingCache)
        view1.isDrawingCacheEnabled = false

        val filePath: String = Environment.getExternalStorageDirectory()
            .toString() + "/Download/" + Calendar.getInstance().getTime().toString() + ".jpg"
        val fileScreenshot = File(filePath)
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(fileScreenshot)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val intent = Intent(Intent.ACTION_SEND)
        val uri: Uri = Uri.fromFile(fileScreenshot)
        intent.setDataAndType(uri, "image/jpeg")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Baixe o app em : LINK")
        this.startActivity(Intent.createChooser(intent,"Compartilhe"))

    }
}