package com.dingdangmao.wetouch.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dingdangmao.wetouch.Database.db;
import com.dingdangmao.wetouch.R;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Register extends Base {


    @BindView(R.id.et_username)
    public EditText et_username;

    @BindView(R.id.et_password)
    public EditText et_password;

    @BindView(R.id.et_confirm_password)
    public EditText et_confirm_password;

    @BindView(R.id.btn_confirm)
    public Button btn_confirm;

    @BindView(R.id.btn_cancel)
    public Button btn_cancel;

    private String username;
    private String password;
    private String confirm_password;

    private db mydb = new db(this, "mydb.db", null, 3);

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                confirm_password = et_confirm_password.getText().toString().trim();



                if(TextUtils.isEmpty(username)) {
                    new SweetAlertDialog(Register.this)
                            .setTitleText("用户名不能为空")
                            .show();
                } else if(TextUtils.isEmpty(password)) {
                    new SweetAlertDialog(Register.this)
                            .setTitleText("密码不能为空")
                            .show();
                } else if(TextUtils.isEmpty(confirm_password)) {
                    new SweetAlertDialog(Register.this)
                            .setTitleText("确认密码不能为空")
                            .show();
                } else if(!password.equals(confirm_password)) {
                    new SweetAlertDialog(Register.this)
                            .setTitleText("两次密码须一致")
                            .show();
                } else {
                    register(username, password);
                }
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_username.setText("");
                et_password.setText("");
                et_confirm_password.setText("");
            }
        });


    }

    private void register(String username, String password) {

        SQLiteDatabase write = mydb.getWritableDatabase();
        write.execSQL("insert into user(username, password) values(?, ?)",
                new String[]{username, password});


        new SweetAlertDialog(Register.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("注册成功")
                .setContentText("是否跳转到登录界面")
                .setCancelText("取消")
                .setConfirmText("确定")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        }).show();

        write.close();
    }
}
