package com.example.tbs;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tbs.Base.chartBase;
import com.example.tbs.Network.Iokihttp;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class charts extends chartBase implements OnChartValueSelectedListener {

    private Iokihttp okhttp;
    private SharedPreferences shared;
    private String title;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        okhttp = new Iokihttp();
        shared = this.getSharedPreferences("com.example.tbs", Context.MODE_PRIVATE);
        title = getIntent().getExtras().getString("title");
        type = Integer.parseInt(getIntent().getExtras().getString("chart_type"));
        getReport(getIntent().getExtras().getString("id"), getIntent().getExtras().getString("from"), getIntent().getExtras().getString("to"));

    }

    public void getReport(String id, String from, String to) {
        System.out.println("ID IS : " + id);
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        final JSONObject subJSON = new JSONObject();
        try {

            json.put("method", "reportData");
            subJSON.put("report_id", id);
            subJSON.put("key", shared.getString("key", "0"));
            if (getIntent().getExtras().containsKey("main")) {
                subJSON.put("main_condition_id", getIntent().getExtras().getString("main"));
                subJSON.put("sub_condition_id", getIntent().getExtras().getString("sub"));
            }
            json.put("data", subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (okhttp.isNetworkConnected(getApplicationContext())) {
            okhttp.post(getResources().getString(R.string.url), json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("FAIL");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // hideLoading();

                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            System.out.println(responseStr + "+++++");
                            JSONObject resJSON = new JSONObject(responseStr);
                            final JSONArray subresJSON = new JSONArray(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            makeChart(subresJSON);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                try {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }

            });
        }
    }

    private void makeChart(JSONArray jsonArray) throws JSONException {
        switch (type) {
            case 1: {
                makePie(jsonArray);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            }
            break;
            case 2: {
                makeBar(jsonArray);
            }
            break;
            case 3: {
            }
            break;
        }
    }

    private void makePie(JSONArray jsonArray) throws JSONException {

        PieChart chart = findViewById(R.id.pieChart);
        chart.setVisibility(View.VISIBLE);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterText(title);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        // chart.setOnChartValueSelectedListener(this);
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        //  chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);
        setPieData(chart, jsonArray);

    }

    private void setPieData(PieChart chart, JSONArray table) throws JSONException {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        JSONArray columnHeader = table.getJSONArray(0);
        JSONArray row = table.getJSONArray(1);
        String[] names = new String[row.length()];
        Float[] values = new Float[row.length()];
        Float sum = 0f;
        for (int x = 0; x < names.length; x++) {
            JSONArray jsonArray = row.getJSONArray(x);
            names[x] = jsonArray.getString(0);
            values[x] = Float.parseFloat(jsonArray.getString(1));
            sum = sum + values[x];
        }
        for (int y = 0; y < names.length; y++) {
            float value = 0f;
            try {
                value = (values[y] / sum) * 100;
            } catch (ArithmeticException a) {

            }
            entries.add(new PieEntry(value, names[y]));
        }
        PieDataSet dataSet = new PieDataSet(entries, title);

        dataSet.setDrawIcons(false);

        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);


        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(R.color.chartBlue);
        chart.setData(data);

        chart.highlightValues(null);

        chart.invalidate();
    }

    private void makeBar(JSONArray table) throws JSONException {
        BarChart barChart = findViewById(R.id.barChart);
        barChart.setVisibility(View.VISIBLE);
        List<BarEntry> entries = new ArrayList<>();
        JSONArray columnHeader = table.getJSONArray(0);
        JSONArray row = table.getJSONArray(1);
        String[] names = new String[row.length()];
        Float[] values = new Float[row.length()];
        float sum = 0f;
        for (int x = 0; x < names.length; x++) {
            JSONArray jsonArray = row.getJSONArray(x);
            names[x] = jsonArray.getString(0);
            values[x] = Float.parseFloat(jsonArray.getString(1));
            sum = sum + values[x];
        }

        for (int i = 0; i < names.length; i++) {
            float value = 1;
            try {
                if (sum == 0) sum = 1;
                value = (values[i] / sum) * 100;
            } catch (ArithmeticException a) {

            }
            entries.add(new BarEntry(i, value));
        }
        BarDataSet set = new BarDataSet(entries, title);
        BarData data = new BarData(set);
        data.setValueTextSize(10);
        data.setValueTextColor(R.color.chartBlue);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter());
        set.setColors(ColorTemplate.PASTEL_COLORS);
        barChart.setData(data);
        data.setValueTextColor(Color.WHITE);
        data.setBarWidth(0.9f);
        final String[] quarters = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            quarters[i] = names[i];
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
        };

        XAxis xAxis = barChart.getXAxis();
        YAxis yAxisL = barChart.getAxisLeft();
        YAxis yAxisR = barChart.getAxisRight();

        yAxisL.setTextColor(getResources().getColor(R.color.chartBlue));
        yAxisR.setTextColor(getResources().getColor(R.color.chartBlue));
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setTextColor(getResources().getColor(R.color.chartBlue));
        xAxis.setLabelRotationAngle(90F);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);
        // pieChart.setDescription("Statistical SLA Report");
        // pieChart.setDescriptionTextSize(15.0f);
        barChart.getLegend().setEnabled(true);
        barChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        barChart.getLegend().setTextSize(19.0f);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setTouchEnabled(false);
        barChart.invalidate();


    }

    private void makeLine(JSONArray jsonArray) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Chart chart = findViewById(R.id.pieChart);
        switch (item.getItemId()) {
            case R.id.animateX: {
                chart.animateX(1400);
                break;
            }
            case R.id.animateY: {
                chart.animateY(1400);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(1400, 1400);
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void saveToGallery() {
        PieChart chart = findViewById(R.id.pieChart);
        saveToGallery(chart, title);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
