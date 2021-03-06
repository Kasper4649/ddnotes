package com.dingdangmao.wetouch.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dingdangmao.wetouch.Adapter.AdapterChart;
import com.dingdangmao.wetouch.Bean.ChartModel;

import com.dingdangmao.wetouch.R;
import com.dingdangmao.wetouch.Database.db;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Chart extends Base {


    private db mydb = new db(this, "mydb.db", null, 2);

    private HashMap<Integer, String> mytag = new HashMap<Integer, String>();

    private ArrayList<ChartModel> model = new ArrayList<ChartModel>();

    @BindView(R.id.main)
    public RecyclerView rv;
    public AdapterChart app;

    @BindView(R.id.ok)
    public Button ok;

    @BindView(R.id.year)
    public EditText year;

    @BindView(R.id.month)
    public EditText month;

    @BindView(R.id.day)
    public EditText day;

    @BindView(R.id.lineL)
    public CardView lineL;


    @BindView(R.id.pieL)
    public CardView pieL;

    @BindView(R.id.mainL)
    public CardView mainL;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = year.getText().toString();
                String s2 = month.getText().toString();
                String s3 = day.getText().toString();
                if (s1.length() == 0) {
                    new SweetAlertDialog(Chart.this)
                            .setTitleText("年份不能为空")
                            .show();

                } else {
                    int y = Integer.parseInt(s1);
                    int m = 0;
                    if (s2.length() != 0) {
                        m = Integer.parseInt(s2);
                    }
                    int d = 0;
                    if (s3.length() != 0) {
                        d = Integer.parseInt(s3);
                    }

                    if (m == 0 && d == 0) {
                        listYear(y);
                    } else if (m != 0 && d == 0) {
                        listMonth(y, m);
                    } else {
                        listDay(y, m, d);
                    }

                }
            }
        });


        app = new AdapterChart(model, mytag);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(app);

    }


    public void listDay(final int y, final int m, final int d) {

        DayData(y, m, d);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                app.notifyDataSetChanged();
                mainL.setVisibility(View.VISIBLE);
                hide();
            }
        });
    }


    public void DayData(int y, int m, int d) {
        model.clear();
        Cursor c;
        SQLiteDatabase write = mydb.getWritableDatabase();
        c = write.rawQuery("select year,month,day,total,type from money where year=? and month=? and day=?", new String[]{String.valueOf(y), String.valueOf(m), String.valueOf(d)});
        if (c.moveToFirst()) {
            do {
                float total = c.getFloat(3);
                int type = c.getInt(4);
                model.add(new ChartModel(total, type, String.valueOf(c.getInt(0)) + "-" + String.valueOf(c.getInt(1)) + "-" + String.valueOf(c.getInt(2))));
            } while (c.moveToNext());
        }
        c.close();
        write.close();
    }

    public void listMonth(final int y, final int m) {

        final HashMap<Integer, Float> count = monthData(y, m);
        monthUI(count);
    }

    public HashMap<Integer, Float> monthData(int y, int m) {
        model.clear();
        HashMap<Integer, Float> count = new HashMap<Integer, Float>();
        Cursor c;
        SQLiteDatabase write = mydb.getWritableDatabase();
        c = write.rawQuery("select total,type,month,day from money where year=? and month=? order by day ASC", new String[]{String.valueOf(y), String.valueOf(m)});
        if (c.moveToFirst()) {
            do {
                float total = c.getFloat(0);
                int type = c.getInt(1);
                model.add(new ChartModel(total, type, String.valueOf(c.getInt(2)) + "-" + String.valueOf(c.getInt(3))));
                if (mytag.containsKey(type)) {
                    if (!count.containsKey(type))
                        count.put(type, total);
                    else
                        count.put(type, count.get(type) + total);
                }

            } while (c.moveToNext());
        }
        c.close();
        write.close();
        return count;
    }

    public void monthUI(HashMap<Integer, Float> count) {
        app.notifyDataSetChanged();
        mainL.setVisibility(View.VISIBLE);
        hide();
        PieChart mPieChart = (PieChart) findViewById(R.id.pie);
        mPieChart.clearChart();
        Iterator it = count.keySet().iterator();
        int index = 0;
        Random rand = new Random();
        String[] color = new String[]{"#3F5D7D", "#279B61", "#008AB8", "#993333", "#A3E496", "#95CAE4", "#CC3333", "#FFFF7A", "#CC6699"};
        while (it.hasNext()) {
            int key = (int) it.next();

            if (index >= color.length) {
                index = rand.nextInt(color.length - 1) + 1;
            }
            mPieChart.addPieSlice(new PieModel(mytag.get(key), count.get(key), Color.parseColor(color[index])));
            index++;
        }
        mPieChart.startAnimation();
        pieL.setVisibility(View.VISIBLE);
    }

    public float[] yearData(final int y) {
        model.clear();
        Cursor c;
        SQLiteDatabase write = mydb.getWritableDatabase();
        float[] mtotal = new float[12];
        c = write.rawQuery("select total,type,month from money where year=?", new String[]{String.valueOf(y)});
        if (c.moveToFirst()) {
            do {

                float total = c.getFloat(0);
                int month = c.getInt(2);
                mtotal[month - 1] += total;

            } while (c.moveToNext());
        }
        c.close();
        write.close();
        return mtotal;
    }

    public void listYear(final int y) {
        final float[] m = yearData(y);
        runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        yearUI(m);
                    }
                });

    }

    public void yearUI(float[] mtotal) {
        int i = 0;
        for (i = 0; i < 12; i++) {
            if (mtotal[i] > 0)
                model.add(new ChartModel(mtotal[i], 0, String.valueOf(i + 1)));
        }
        app.notifyDataSetChanged();
        mainL.setVisibility(View.VISIBLE);

        hide();
        try {
            ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.line);
            mCubicValueLineChart.clearChart();
            ValueLineSeries series = new ValueLineSeries();
            series.setColor(0xFF56B7F1);
            series.addPoint(new ValueLinePoint("", 0));
            for (i = 0; i < model.size(); i++) {
                series.addPoint(new ValueLinePoint(model.get(i).getTime(), model.get(i).getMoney()));
            }
            series.addPoint(new ValueLinePoint("", 0));
            mCubicValueLineChart.addSeries(series);
            mCubicValueLineChart.startAnimation();
            lineL.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(Chart.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume() {
        super.onResume();
        Refresh();
    }

    public void Refresh() {
        SQLiteDatabase read = mydb.getWritableDatabase();
        Cursor cursor = read.query("tag", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String tag = cursor.getString(cursor.getColumnIndex("type"));
                mytag.put(id, tag);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void hide() {

        if (lineL.getVisibility() == View.VISIBLE)
            lineL.setVisibility(View.GONE);
        if (pieL.getVisibility() == View.VISIBLE)
            pieL.setVisibility(View.GONE);
    }


}
