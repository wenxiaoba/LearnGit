package com.example.appmonitor.classes;

import java.io.Serializable;

public class AppInfo implements Serializable {

	private String packageName; // 包名
	private int time;//使用时长
	private int start_ups;//启动次数
	private int timeInterval;//时间间隔


	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getTime(){
		return time;
	}

	public void setTime(int time){
		this.time = time;
	}

	public int getStarts(){
		return start_ups;
	}

	public void setStarts(int start_ups){
		this.start_ups = start_ups;
	}

	public int getTimeInterval(){
		return timeInterval;
	}

	public void setTimeInterval(int timeInterval){
		this.timeInterval = timeInterval;
	}

	@Override
	public String toString(){
		return packageName+"  "+time+"   "+start_ups+"   "+timeInterval;
	}

}