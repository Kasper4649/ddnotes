package com.dingdangmao.wetouch.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;



public class db extends SQLiteOpenHelper {
    public static final String TABLE_MONEY = "create table money("
            + "id integer primary key autoincrement,"
            + "year integer, month integer, day integer, unix integer, total integer, tip text, type integer"
            + ", FOREIGN KEY (type) REFERENCES tag(id))";
    public static final String TABLE_TYPE = "create table tag("
            + "id integer primary key autoincrement,"
            + "type text)";
    public static final String TABLE_USER = "create table user("
            + "id integer primary key autoincrement,"
            + "username text, password text)";

    private Context mcontext;

    public db(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, 1);
        mcontext = context;
    }
    public void onCreate(SQLiteDatabase db){

        try {
                db.execSQL(TABLE_MONEY);
                db.execSQL(TABLE_TYPE);
                db.execSQL(TABLE_USER);
             } catch(Exception e) {
                 Toast.makeText(mcontext, e.toString(), Toast.LENGTH_SHORT).show();
            }
    }
    public void onUpgrade(SQLiteDatabase db,int old,int newv){
        db.execSQL("drop table if exists money");
        db.execSQL("drop table if exists type");
        db.execSQL("drop table if exists user");
        onCreate(db);
    }

}
