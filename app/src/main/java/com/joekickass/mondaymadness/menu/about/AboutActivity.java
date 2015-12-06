package com.joekickass.mondaymadness.menu.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.joekickass.mondaymadness.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the about menu, mainly for open source attributions
 */
public class AboutActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mRecyclerView = (RecyclerView) findViewById(R.id.about_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AboutAdapter(getData());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<AboutItem> getData() {
        List<AboutItem> ret = new ArrayList<>();
        ret.add(new AboutItem(getString(R.string.title_version), getVersion()));
        ret.add(new AboutItem(getString(R.string.title_md_icons_attr), getMdIconsAttr()));
        return ret;
    }

    private String getVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return getString(R.string.unknown);
        }
    }

    private String getMdIconsAttr() {
        return getString(R.string.md_icons_attr);
    }
}
