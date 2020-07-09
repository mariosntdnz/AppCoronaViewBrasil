package com.example.coronaview.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.coronaview.R
import com.example.coronaview.ui.adapter.SectionsPagerAdapter
import com.example.coronaview.ui.fragments.EstadosFragment
import com.example.coronaview.ui.fragments.GraficoCoronaFragment
import com.example.coronaview.ui.fragments.HomeFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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
                    (sectionsPagerAdapter.getItem(TAB_CORONA_MAP) as GraficoCoronaFragment).onClickFAB()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_options, menu);
        return true;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        when (id) {
            R.id.menu_toolbar_sobre -> {
                intent = Intent(this,SobreActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    companion object{
        const val TAB_POR_ESTADOS = 0
        const val TAB_HOME = 1
        const val TAB_CORONA_MAP = 2
    }
}
