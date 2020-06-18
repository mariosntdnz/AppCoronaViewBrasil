package com.example.coronaview.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.example.coronaview.R
import com.example.coronaview.common.Response
import com.example.coronaview.common.Status
import com.example.coronaview.ui.adapter.SectionsPagerAdapter
import com.example.coronaview.ui.fragments.EstadosFragment
import com.example.coronaview.ui.fragments.HomeFragment
import com.example.coronaview.ui.fragments.MapCoronaFragment
import com.example.coronaview.ui.viewModel.EstatisticasViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.fragment_estados.*
import kotlinx.android.synthetic.main.fragment_principal.*


class EstatisticasActivity : AppCompatActivity() {

    var colorIntArray = intArrayOf(
        R.color.fundoFab,
        R.color.fundoFab,
        R.color.fundoFab
    )
    var iconIntArray = intArrayOf(
        R.drawable.ic_pesquisar,
        R.drawable.ic_compartilhar,
        R.drawable.ic_info
    )

    lateinit var fab : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                supportFragmentManager
            )

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = sectionsPagerAdapter.count;
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(TAB_HOME)?.select()

        fab = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            when(tabs.selectedTabPosition){
                TAB_HOME ->
                    (sectionsPagerAdapter.getItem(TAB_HOME) as HomeFragment).onClickFAB()
                TAB_POR_ESTADOS ->
                    (sectionsPagerAdapter.getItem(TAB_POR_ESTADOS) as EstadosFragment).onClickFAB()
                TAB_CORONA_MAP ->
                    (sectionsPagerAdapter.getItem(TAB_CORONA_MAP) as MapCoronaFragment).onClickFAB()
            }
        }

        tabs.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.setCurrentItem(tab.position)
                fab.setBackgroundTintList(
                    resources.getColorStateList(
                        colorIntArray[tab.position]
                    ))
                fab.setImageDrawable(resources.getDrawable(iconIntArray[tab.position], null))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    companion object{
        const val TAB_POR_ESTADOS = 0
        const val TAB_HOME = 1
        const val TAB_CORONA_MAP = 2
    }
}
