package com.dingdangmao.wetouch.Activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dingdangmao.wetouch.R;

import butterknife.ButterKnife;



abstract public class Base extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initToolbar();
        init(savedInstanceState);

    }


    public void init(@Nullable Bundle savedInstanceState){

    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    abstract public int getLayoutId();

}
