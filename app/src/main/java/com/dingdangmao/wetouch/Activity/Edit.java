package com.dingdangmao.wetouch.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.dingdangmao.wetouch.Event.Tag;
import com.dingdangmao.wetouch.R;
import com.dingdangmao.wetouch.db;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class Edit extends Base {

    @BindView(R.id.ok)
    public Button ok;

    @BindView(R.id.date)
    public Button date;

    @BindView(R.id.tip_date)
    public Button tip_date;

    @BindView(R.id.tip_type)
    public Button tip_type;

    @BindView(R.id.money)
    public TextView money;

    @BindView(R.id.tip)
    public TextView tip;

    @BindView(R.id.ttag)
    public TagContainerLayout mTag;

    private db mydb = new db(this, "mydb.db", null, 3);
    private HashMap<String, Integer> type = new HashMap<String, Integer>();
    private int cur;

    private String timestr;

    private boolean refresh = true;

    private boolean is_type_selected = false;

    private int edit_id, edit_year, edit_month, edit_day, edit_cur;
    private float edit_money;
    private String edit_tip;


    @Override
    public int getLayoutId() {
        return R.layout.activity_add;
    }


    @Override
    public void init(@Nullable Bundle savedInstanceState) {


        EventBus.getDefault().register(this);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState != null) {
            cur = savedInstanceState.getInt("cur");
            timestr = savedInstanceState.getString("timestr");
            tip.setText(savedInstanceState.getString("tip"));
            money.setText(savedInstanceState.getString("money"));
        }

        fill_data();


        mTag.setTheme(0);
        mTag.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String tag) {
                if (tag.equals("自定义")) {
                    Intent intent = new Intent(Edit.this, Type.class);
                    intent.putExtra("flag", true);
                    startActivity(intent);
                } else {
                    cur = type.get(tag);
                    tip_type.setText(tag);
                    is_type_selected = true;
                }
            }

            @Override
            public void onTagLongClick(final int position, String text) {
            }

            @Override
            public void onTagCrossClick(int position) {
            }
        });




        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 不知道为什么，在点击事件外面时候 cur 还有值，在这里面就为默认 0 了....
                // 只能重新赋值
                if(!is_type_selected)
                    cur = edit_cur;


                SQLiteDatabase write = mydb.getWritableDatabase();
                String mstr = money.getText().toString();
                String tstr = tip.getText().toString();
                if (mstr.length() == 0) {
                    new SweetAlertDialog(Edit.this)
                            .setTitleText("金额不能为空")
                            .show();

                }  else {

                    String[] tmp = timestr.split("-");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(tmp[0] + "-" + tmp[1] + "-" + tmp[2]);

                    } catch (Exception e) {

                    }


                    int r = (int) (date.getTime() / 1000);
                    Log.i("tag", r + "");

                    write.execSQL("update money set year=?,month=?,day=?,unix=?, total=?, tip=?, type=? where id=?",
                            new String[]{tmp[0], tmp[1], tmp[2], String.valueOf(r), mstr,
                            tstr, String.valueOf(cur), String.valueOf(edit_id)}
                    );

                    write.close();
                    EventBus.getDefault().post(new Edit());

                    new SweetAlertDialog(Edit.this)
                            .setTitleText("修改成功")
                            .show();
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] type = new boolean[]{true, true, true, false, false, false};
                TimePickerView pvTime = new TimePickerView.Builder(Edit.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        timestr = formatter.format(date);
                        tip_date.setText(timestr);
                    }
                }).setType(type).setCancelText("取消").setSubmitText("确定")
                        .setSubmitColor(R.color.colorButton)
                        .setCancelColor(R.color.colorTip).build();
                pvTime.show();
            }
        });
    }

    private void fill_data() {

        Intent intent = getIntent();
        edit_id = intent.getIntExtra("edit_id", -1);
        SQLiteDatabase write = mydb.getWritableDatabase();
        Cursor cursor = write.rawQuery("select * from money where id = " + edit_id, null);
        if (cursor.moveToFirst()) {
            do {
                edit_year = cursor.getInt(cursor.getColumnIndex("year"));
                edit_month = cursor.getInt(cursor.getColumnIndex("month"));
                edit_day = cursor.getInt(cursor.getColumnIndex("day"));
                edit_cur = cursor.getInt(cursor.getColumnIndex("type"));
                edit_money = cursor.getFloat(cursor.getColumnIndex("total"));
                edit_tip = cursor.getString(cursor.getColumnIndex("tip"));
            } while (cursor.moveToNext());
        }

        cursor.close();

        SQLiteDatabase read = mydb.getWritableDatabase();
        cursor = read.rawQuery("select * from tag where id = " + edit_cur, null);
        if (cursor.moveToFirst()) {
            do {
                String tag = cursor.getString(cursor.getColumnIndex("type"));
                tip_type.setText(tag);
            } while (cursor.moveToNext());
        }
        cursor.close();

        tip_type.setVisibility(View.VISIBLE);
        timestr = String.valueOf(edit_year) + "-" + String.valueOf(edit_month) + "-" + String.valueOf(edit_day);
        tip_date.setText(timestr);
        money.setText(String.valueOf(edit_money));
        tip.setText(edit_tip);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    public void onResume() {
        super.onResume();
        if (refresh) {
            refresh = false;
            Refresh();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void Refresh() {
        SQLiteDatabase read = mydb.getWritableDatabase();
        Cursor cursor = read.query("tag", null, null, null, null, null, "id asc");
        type.clear();
        cur = 0;
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String tag = cursor.getString(cursor.getColumnIndex("type"));
                type.put(tag, id);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Set<String> myset = type.keySet();
        final List<String> taglist = new ArrayList<String>();
        taglist.addAll(myset);
        taglist.add("自定义");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTag.setTags(taglist);
            }
        });

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("cur", cur);
        outState.putString("timestr", timestr);
        outState.putString("money", money.getText().toString());
        outState.putString("tip", tip.getText().toString());
    }


    @Subscribe
    public void OnTagChange(Tag t) {
        refresh = true;
    }


}


