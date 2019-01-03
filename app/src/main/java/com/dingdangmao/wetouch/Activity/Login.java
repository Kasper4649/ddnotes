package com.dingdangmao.wetouch.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dingdangmao.wetouch.Database.db;
import com.dingdangmao.wetouch.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class Login extends Base {

    @BindView(R.id.et_username)
    public EditText et_username;

    @BindView(R.id.et_password)
    public EditText et_password;

    @BindView(R.id.btn_confirm)
    public Button btn_confirm;

    @BindView(R.id.btn_register)
    public Button btn_register;

    private String username;
    private String password;

    private db mydb = new db(this, "mydb.db", null, 3);

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();


                if(TextUtils.isEmpty(username)) {
                    new SweetAlertDialog(Login.this)
                            .setTitleText("用户名不能为空")
                            .show();
                } else if(TextUtils.isEmpty(password)) {
                    new SweetAlertDialog(Login.this)
                            .setTitleText("密码不能为空")
                            .show();
                } else {
                    login(username, password);
                }
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });


    }

    private void login(String username, String password) {

        SQLiteDatabase write = mydb.getWritableDatabase();
        Cursor cursor = write.rawQuery("select * from user where username='" + username + "' and password='" + password +"'", null);
        if (cursor.moveToFirst()) {
            new SweetAlertDialog(Login.this)
                    .setTitleText("登录成功")
                    .show();
            Intent intent = new Intent(Login.this, Main.class);
            startActivity(intent);
        } else {
            new SweetAlertDialog(Login.this)
                    .setTitleText("登录失败")
                    .show();
            et_username.setText("");
            et_password.setText("");
        }

        cursor.close();
    }
}
