package com.dingdangmao.wetouch.Bean;



public class ChartModel {
        private float money;
        private int type;
        private String time;

        public ChartModel(float money,int type,String time){

            this.money=money;
            this.type=type;
            this.time=time;
        }
        public float getMoney(){ return this.money;}
        public int getType(){ return this.type;}
        public String getTime(){ return this.time;}

}
