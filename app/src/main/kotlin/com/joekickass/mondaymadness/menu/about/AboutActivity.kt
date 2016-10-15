package com.joekickass.mondaymadness.menu.about

import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager

import com.joekickass.mondaymadness.R
import com.joekickass.mondaymadness.R.string.*

import java.util.ArrayList

import kotlinx.android.synthetic.main.activity_about.*

/**
 * Shows the about menu, mainly for open source attributions
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        about_list.setHasFixedSize(true)
        about_list.layoutManager = LinearLayoutManager(this)
        about_list.adapter = AboutAdapter(data)
    }

    private val data: List<AboutItem>
        get() {
            val ret = ArrayList<AboutItem>()
            ret.add(AboutItem(getString(title_version), version))
            ret.add(AboutItem(getString(title_md_icons_attr), mdIconsAttr))
            return ret
        }

    private val version: String
        get() {
            return try { packageManager.getPackageInfo(packageName, 0).versionName }
                catch (e: NameNotFoundException) { getString(unknown) }
        }

    private val mdIconsAttr: String
        get() = getString(md_icons_attr)
}
