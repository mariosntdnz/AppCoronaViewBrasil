package com.example.coronaview.ui.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.coronaview.R
import kotlinx.android.synthetic.main.activity_main.*
import mehdi.sakout.aboutpage.AboutPage

class SobreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var viewSobre= AboutPage(this)
            .isRTL(false)
            .setDescription("*** Sobre Nós ***")
            .setImage(R.drawable.ic_info_black_24dp)
            .addGroup("Entre em contato")
            .addEmail("helberfrd1@gmail.com")
            .addGitHub("mariosntdnz","Visite os projetos do Git")
            .addInstagram("marionogueira90")
            .create()

        setContentView(viewSobre)
    }
}