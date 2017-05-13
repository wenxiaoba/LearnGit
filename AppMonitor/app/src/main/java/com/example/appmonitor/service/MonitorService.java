package com.example.appmonitor.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.appmonitor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorService extends Service {


    String pckName_now;
    PackageManager pckManager;
    private int time;
    PackageInfo pckInfo = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    static int index_now;
    static String date_now;
    boolean user = false;
    SharedPreferences reader;
    SharedPreferences.Editor writer;
    ArrayList<String> installApp = new ArrayList<String>();
    ArrayList<String> runningApp = new ArrayList<String>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder
                .setContentTitle("这是通知标题")
                .setContentText("这是通知内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();
        manager.notify(1, notification);
        Log.d("MyService", "onCreate executed");
        inilize();
        timer.schedule(task, 0, 2000); //开始监听应用，每2000毫秒查询一次
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler_listen.sendMessage(message);
        }
    };

    Handler handler_listen = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //  Add your Code here!
                Log.d("xyz",pckName_now);
                if(user == true){
                    time += 2;
                    Log.d("xyz","     @  -->"+pckName_now+"   index is :@@@"+index_now);
                    String date = sdf.format(new Date());
                    //当日期改变后
                    if(date.equals(date_now) == false){
                        int timeAll = reader.getInt(pckName_now+".time",0) + time;
                        writer.putInt(pckName_now+".time",timeAll);
                        writer.commit();
                        time = 0;

                        index_now = (index_now+1)%46;//index增1
                        date_now = date;//修改日期
                        Log.d("xyz","   the index is  @   "+index_now);
                        //将修改后的数据写入data.xml保存
                        SharedPreferences.Editor writeData = getSharedPreferences("data",MODE_PRIVATE).edit();
                        writeData.putInt("index",index_now);
                        writeData.putString("index_date",date_now);
                        writeData.commit();
                        //将需要用到的下一个.xml的数据全部清除
                        writer = getSharedPreferences(index_now+"",MODE_PRIVATE).edit();
                        writer.clear();
                        writer.commit();
                        reader = getSharedPreferences(index_now+"",MODE_PRIVATE);
                    }
                }
                //使用次数的操作：
                ArrayList<String> nowApps = runningApp();
                compareRunApp(runningApp,nowApps);
                runningApp = nowApps;
                //使用时间的操作
                String packageName = ForegroundProcess.getForegroundApp();//获取到最顶层的应用包名
//                if(packageName != null){
//                }
                if(pckName_now != null){
                    if(pckName_now.equals(packageName) == false){//App切换之后
                        if(user == true){
                            int timeAll = reader.getInt(pckName_now+".time",0) + time;
                            writer.putInt(pckName_now+".time",timeAll);
                            writer.commit();
                            Log.d("xyz","    @    userTime-->"+timeAll+"  time = "+time);
                        }

                        time = 0;
                        user = false;
                        if(packageName != null){
                            pckName_now = packageName;
                        }else{
                            pckName_now = "com.example.wechatsample1";
                        }

                        if(installApp.contains(pckName_now)){
                            user = true;
                        }else{
                            try {
                                pckInfo = pckManager.getPackageInfo(pckName_now,0);
                                if ((pckInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                                    user = true;
                                    installApp.add(pckName_now);
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                                pckInfo = null;
                            }
                        }
                    }

                }
            }
            super.handleMessage(msg);
        };
    };

    public ArrayList<String> runningApp(){
        ArrayList<String> runningApps = new ArrayList<String>();
        List<ProcessManager.Process> runPro = ProcessManager.getRunningApps();
        for(ProcessManager.Process runningPro : runPro){
            String pckName = runningPro.getPackageName();
            if(installApp.contains(pckName) && (runningApps.contains(pckName) == false)){
                runningApps.add(pckName);
            }
        }
        return runningApps;
    }

    public void compareRunApp(List<String> prePro,List<String> curPro){
        for(String s:curPro){
            if(prePro.contains(s)){
                continue;
            }
            int start_ups = reader.getInt(s+".start",0) + 1;
            writer.putInt(s+".start",start_ups);
            writer.commit();
            Log.d("xyz","     @startUps-->"+s+"    次数"+start_ups);
        }
    }

    public void inilize(){
        pckManager = getPackageManager();
        SharedPreferences readData = getSharedPreferences("data",MODE_PRIVATE);
        index_now = readData.getInt("index",0);
        date_now = readData.getString("index_date",sdf.format(new Date()));
        time = 0;
        reader = getSharedPreferences(index_now+"",MODE_PRIVATE);
        writer = getSharedPreferences(index_now+"",MODE_PRIVATE).edit();
        Log.d("xyz","the index is"+index_now+"    date = "+date_now);
        List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
        for(PackageInfo pckInfo : packageInfoList){
            if ((pckInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                installApp.add(pckInfo.packageName);
            }
        }
        pckName_now = "com.example.wechatsample1";
        if(installApp.contains(pckName_now)){
            user = true;
        }
    }

}
