package com.joekickass.mondaymadness.menu.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import com.joekickass.mondaymadness.R
import com.joekickass.mondaymadness.menu.about.AboutAdapter
import com.joekickass.mondaymadness.menu.about.AboutItem

import java.util.ArrayList

/**
 * Shows the about menu, mainly for open source attributions
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val recyclerView = findViewById(R.id.about_list) as RecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = AboutAdapter(data)
        recyclerView.adapter = adapter
    }

    private val data: List<AboutItem>
        get() {
            val ret = ArrayList<AboutItem>()
            ret.add(AboutItem(getString(R.string.title_version), version))
            ret.add(AboutItem(getString(R.string.title_md_icons_attr), mdIconsAttr))
            return ret
        }

    private val version: String
        get() {
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                return packageInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                return getString(R.string.unknown)
            }

        }

    private val mdIconsAttr: String
        get() = getString(R.string.md_icons_attr)
}
