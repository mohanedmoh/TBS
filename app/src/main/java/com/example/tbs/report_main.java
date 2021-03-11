package com.example.tbs;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.tbs.adapter.TabAdapter;
import com.example.tbs.tabs.chartReport;
import com.example.tbs.tabs.generalReport;
import com.example.tbs.tabs.spacialReport;


public class report_main extends AppCompatActivity implements chartReport.OnFragmentInteractionListener,generalReport.OnFragmentInteractionListener,spacialReport.OnFragmentInteractionListener{
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fi_report_main);
        init();
    }
    public void init(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new generalReport(), "General");
        adapter.addFragment(new spacialReport(), "Special");
        adapter.addFragment(new chartReport(), "Charts");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
