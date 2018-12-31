package com.dingdangmao.wetouch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.dingdangmao.wetouch.R;



public class Start extends Base {


    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);

        //延迟跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Start.this, Login.class);
                Start.this.startActivity(intent);
                Start.this.finish();
            }
        },3000);
    }

    @Override
    public int getLayoutId() {

        return R.layout.activity_start;

    }



}
