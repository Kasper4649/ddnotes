package com.dingdangmao.wetouch.Bean;


public class Model {
    private int id;
    private String time;
    private float money;
    private int type;
    private String tip;

    public Model(String time,float money,int type,String tip,int id){
        this.time = time;
        this.money = money;
        this.type = type;
        this.tip = tip;
        this.id = id;
    }

    public String getTime() {
        return String.valueOf(time);
    }

    public float getMoney() {
        return this.money;
    }

    public int getType() {
        return this.type;

    }

    public String getTip() {
        return this.tip;
    }

    public int getId() {
        return id;
    }
}
