package com.example.appmonitor.classes;

/**
 * Created by Administrator on 2017/4/10 0010.
 */
public class AppClass {

    private String intervalC;   //时间间隔标识符
    private String timeC;   //使用时长标识符
    private String startC;  //启动次数标识符

    public void setIntervalC(int intervalC,int min,int max){
        if(intervalC <= min){
            this.intervalC = "Ir";
        }else if(intervalC <= max){
            this.intervalC = "Io";
        }else{
            this.intervalC = "It";
        }
    }

    public String getIntervalC(){
        return intervalC;
    }

    public void setTimeC(int timeC,int min,int max){
        if(timeC <= min){
            this.timeC = "Tt";
        }else if(timeC <= max){
            this.timeC = "To";
        }else{
            this.timeC = "Tr";
        }
    }

    public String getTimeC(){
        return timeC;
    }

    public void setStartC(int startC,int min,int max){
        if(startC <= min){
            this.startC = "St";
        }else if(startC <= max){
            this.startC = "So";
        }else{
            this.startC = "Sr";
        }
    }

    public String getStartC(){
        return startC;
    }
}
