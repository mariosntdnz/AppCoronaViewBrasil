package com.example.coronaview.ui.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.coronaview.R
import com.example.coronaview.common.Response
import com.example.coronaview.common.Status
import com.example.coronaview.data.api.model.CoronaEstatisticas
import com.example.coronaview.ui.viewModel.EstatisticasViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_principal.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

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
        return inflater.inflate(R.layout.fragment_principal, container, false)
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

        var textViewUltimaAtt =
            requireActivity().findViewById<TextView>(R.id.textViewUltimaAtualizacao)

        var textViewCasosNovos =
            requireActivity().findViewById<TextView>(R.id.textViewNumeroNovosCasos)
        var textViewCasosTotais =
            requireActivity().findViewById<TextView>(R.id.textViewNumeroTotalCasos)

        var textViewObitosNovos =
            requireActivity().findViewById<TextView>(R.id.textViewNumeroNovosObitos)
        var textViewObitosTotais =
            requireActivity().findViewById<TextView>(R.id.textViewNumeroTotalObitos)

        var textViewRecuperadosNovos =
            requireActivity().findViewById<TextView>(R.id.textViewNumeroNovosRecuperados)
        var textViewRecuperadosTotais =
            requireActivity().findViewById<TextView>(R.id.textViewNumeroTotalRecuperados)

        textViewCasosNovos.text = "\n" + viewModel.coronaEstatisticasRepository.getNovosCasos(
            coronaEstatisticas[0],
            coronaEstatisticas[1]
        ).toString()
        textViewObitosNovos.text = "\n" + viewModel.coronaEstatisticasRepository.getNovosObitos(
            coronaEstatisticas[0],
            coronaEstatisticas[1]
        ).toString()
        textViewRecuperadosNovos.text =
            "\n" + viewModel.coronaEstatisticasRepository.getNovosRecuperados(
                coronaEstatisticas[0],
                coronaEstatisticas[1]
            ).toString()

        textViewCasosTotais.text = "\n" + coronaEstatisticas[0].infected.toString()
        textViewObitosTotais.text = "\n" + coronaEstatisticas[0].deceased.toString()
        textViewRecuperadosTotais.text = "\n" + coronaEstatisticas[0].recovered.toString()

        textViewUltimaAtt.text = "Atualizado em " +
                viewModel.coronaEstatisticasRepository.getUltimoUpDateFromlastUpdatedAtSource(
                    coronaEstatisticas[0].lastUpdatedAtSource!!
                ) +
                "\n- Última atualização disponível -"

        cardViewCasos.visibility = View.VISIBLE
        cardViewObitos.visibility = View.VISIBLE
        cardViewRecuperados.visibility = View.VISIBLE
        textViewUltimaAtt.visibility = View.VISIBLE

        progressBarRequisicao.visibility = View.GONE

        var fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
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



            /*val returnedBitmap = Bitmap.createBitmap(
                requireView().width,
                requireView().height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(returnedBitmap)
            val bgDrawable = requireView().background
            if (bgDrawable != null) bgDrawable.draw(canvas)
            else canvas.drawColor(Color.WHITE)
            requireView().draw(canvas)
            var path = Environment.getEStorageDirectory().toString() + "/a.jpg";
            var imageFile = File(path);

            var out: FileOutputStream? = null

            try {
                out = FileOutputStream(imageFile);
                // choose JPEG format
                returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                println("CLICK")
            } catch (e: FileNotFoundException) {
                // manage exception ...
                println("CLICK2")
            } catch (e: IOException) {
                // manage exception ...
                println("CLICK3")
            } finally {

                try {
                    if (out != null) {
                        out.close();
                    }

                } catch (e: Exception) {
                }

            }*/
        }
    }

    private fun responseFailure(error : Throwable?) = Toast.makeText(activity,error?.message?:"Falha",Toast.LENGTH_SHORT).show()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}