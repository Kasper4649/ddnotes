package com.dingdangmao.wetouch.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dingdangmao.wetouch.R;

import butterknife.ButterKnife;



abstract public class Base extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        beforeView();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initToolbar();
        init(savedInstanceState);

    }


    public void beforeView(){

    }

    public void init(@Nullable Bundle savedInstanceState){

    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    abstract public int getLayoutId();

}
