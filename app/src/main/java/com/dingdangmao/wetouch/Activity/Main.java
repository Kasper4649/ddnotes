package com.dingdangmao.wetouch.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dingdangmao.wetouch.Adapter.Adapter;
import com.dingdangmao.wetouch.Bean.Model;
import com.dingdangmao.wetouch.R;
import com.dingdangmao.wetouch.db;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class Main extends Base {


    @BindView(R.id.dl)
    public DrawerLayout dl;

    @BindView(R.id.drawer)
    public NavigationView nv;


    @BindView(R.id.main)
    public RecyclerView rv;

    
    private Adapter app;
    private db mydb = new db(this, "mydb.db", null, 2);
    private ArrayList<Model> mlist = new ArrayList<Model>();
    private HashMap<Integer, String> mytag = new HashMap<Integer, String>();

    private boolean refresh = true;

    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        ActionBar bar = getSupportActionBar();
        StatusBarUtil.setColorForDrawerLayout(this, dl, ContextCompat.getColor(this, R.color.colorPrimary));

        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
        }


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem mi) {
                switch (mi.getItemId()) {
                    case R.id.count:
                        Intent intent1 = new Intent(Main.this, Chart.class);
                        startActivity(intent1);
                        break;
                    case R.id.type:
                        Intent intent2 = new Intent(Main.this, Type.class);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
                dl.closeDrawers();
                return true;

            }

        });

        app = new Adapter(this, mlist, mytag);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv.setAdapter(app);
        rv.setNestedScrollingEnabled(false);

    }

    @Override
    public int getLayoutId() {

        return R.layout.activity_main;

    }


    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            dl.openDrawer(GravityCompat.START);

        } else if (item.getItemId() == R.id.main_add) {

            Intent intent = new Intent(Main.this, Add.class);
            startActivity(intent);
        }

        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void onResume() {

        super.onResume();
        if (refresh) {
            refresh = false;
            Refresh();
        } else
            Log.i("tag", "not refresh");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void Refresh() {

        SQLiteDatabase write = mydb.getWritableDatabase();
        mlist.clear();
        Cursor cursor = write.rawQuery("select * from money order by id DESC  limit 0,30", null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                int month = cursor.getInt(cursor.getColumnIndex("month"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                float total = cursor.getFloat(cursor.getColumnIndex("total"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String tip = cursor.getString(cursor.getColumnIndex("tip"));
                Log.i("model", id + " ");
                mlist.add(new Model(year + "-" + month + "-" + day, total, type, tip, id));

            } while (cursor.moveToNext());

        }

        cursor.close();
        mytag.clear();

        cursor = write.query("tag", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String tag = cursor.getString(cursor.getColumnIndex("type"));
                mytag.put(id, tag);
            } while (cursor.moveToNext());
        }
        cursor.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                app.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void OnAdd(Add ins) {
        refresh = true;
    }

    @Subscribe
    public void OnEdit(Edit ins) {
        refresh = true;
    }

}



