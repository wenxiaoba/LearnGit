package com.example.appmonitor;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.appmonitor.classes.AppClass;
import com.example.appmonitor.classes.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/4/28 0028.
 */
public class BayesMethod {

    private static double r;
    private static double o;
    private static double t;
    private static double sumInstance;
    private static HashMap<String,Integer> regularly = new HashMap<String,Integer>();
    private static HashMap<String,Integer> occasion = new HashMap<String,Integer>();
    private static HashMap<String,Integer> tiny = new HashMap<String,Integer>();

    public static ArrayList<String> makeClassify(Context context, ArrayList<AppInfo> Apps, SharedPreferences reader){
        initializeBayes(context);
        int interMin = reader.getInt("reInterNum",4);
        int interMax = reader.getInt("ocInterNum",7);

        int timeMin = reader.getInt("tiTimeNum",10);
        int timeMax = reader.getInt("ocTimeNum",20);

        int startMin = reader.getInt("tiStartNum",1);
        int startMax = reader.getInt("ocStartNum",3);

//        Log.d("xyz","interMin = "+interMin+" interMax = "+interMax+" timeMin = "+timeMin+" timeMax = "+timeMax+" startMin = "+startMin+" startMax = "+startMax);

        ArrayList<String> classGroup = new ArrayList<String>();
        for(AppInfo app : Apps){
            AppClass appClass  = new AppClass();
            appClass.setTimeC(app.getTime(),timeMin,timeMax);
            appClass.setStartC(app.getStarts(),startMin,startMax);
            appClass.setIntervalC(app.getTimeInterval(),interMin,interMax);
            String result = classResult(appClass);
            classGroup.add(result);
        }
        return classGroup;
    }

    public static String classResult(AppClass appClass){
        double rr = (r * (regularly.get(appClass.getTimeC())) * (regularly.get(appClass.getStartC())) * (regularly.get(appClass.getIntervalC())))
                /(sumInstance * r * r * r);
        double ro = (o * (occasion.get(appClass.getTimeC())) * (occasion.get(appClass.getStartC())) * (occasion.get(appClass.getIntervalC())))
                /(sumInstance * o * o * o);
        double rt = (o * (tiny.get(appClass.getTimeC())) * (tiny.get(appClass.getStartC())) * (tiny.get(appClass.getIntervalC())))
                /(sumInstance * t * t * t);
        if(rr > ro){
            if(rr >= rt)
                return "r";
            else
                return "t";
        }else{
            if( ro >= rt)
                return "o";
            else
                return "t";
        }
    }


    public static ArrayList<AppInfo> getApps(HashMap<String,ArrayList<Integer>> rawData){
//        Log.d("xyz","##################0");
        if(rawData.isEmpty()){
            return null;
        }
        ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
        Iterator it = rawData.keySet().iterator();
        while(it.hasNext()) {
            String key = (String)it.next();
            AppInfo app = new AppInfo();
            ArrayList<Integer> appData = rawData.get(key);
//            app.setPackageName(key);
//            app.setStarts(appData.get(0));
            int sumTime = 0;
            int useDay = 0;
            int interrupt = 0;
            boolean interFlag = false;
            for(int i = 1; i < appData.size(); i++){
                int time = appData.get(i);
                if(time == 0){
                    interFlag = true;
                }
                else{
                    sumTime += time;
                    useDay++;
                    if(interFlag == true){
                        interrupt++;
                        interFlag = false;
                    }
                }
            }
            //--------------------
            app.setPackageName(key);
            //--------------------
            if(useDay == 0){
                app.setTime(0);
                app.setStarts(0);
                app.setTimeInterval(45);
                apps.add(app);
                continue;
            }
            //--------------------
            app.setStarts(appData.get(0)/useDay);
            //--------------------
            app.setTime(sumTime/(useDay * 60));
            if(interrupt == 0){     interrupt = 1;}
            app.setTimeInterval((45 - useDay)/interrupt);
            apps.add(app);
        }
        return apps;
    }

    public static void initializeBayes(Context context){
//        Log.d("xyz","##################2");
        SharedPreferences reader = context.getSharedPreferences("bayesData",Context.MODE_PRIVATE);

        r = reader.getInt("r",10)*1.0;
        regularly.put("Ir",reader.getInt("r.Ir",9));
        regularly.put("Io",reader.getInt("r.Io",1));
        regularly.put("It",reader.getInt("r.It",0));
        regularly.put("Tr",reader.getInt("r.Tr",4));
        regularly.put("To",reader.getInt("r.To",3));
        regularly.put("Tt",reader.getInt("r.Tt",3));
        regularly.put("Sr",reader.getInt("r.Sr",4));
        regularly.put("So",reader.getInt("r.So",3));
        regularly.put("St",reader.getInt("r.St",3));

        o = reader.getInt("o",10)*1.0;
        occasion.put("Ir",reader.getInt("r.Ir",0));
        occasion.put("Io",reader.getInt("r.Io",8));
        occasion.put("It",reader.getInt("r.It",2));
        occasion.put("Tr",reader.getInt("r.Tr",3));
        occasion.put("To",reader.getInt("r.To",4));
        occasion.put("Tt",reader.getInt("r.Tt",3));
        occasion.put("Sr",reader.getInt("r.Sr",4));
        occasion.put("So",reader.getInt("r.So",3));
        occasion.put("St",reader.getInt("r.St",3));

        t = reader.getInt("t",7)*1.0;
        tiny.put("Ir",reader.getInt("r.Ir",0));
        tiny.put("Io",reader.getInt("r.Io",0));
        tiny.put("It",reader.getInt("r.It",7));
        tiny.put("Tr",reader.getInt("r.Tr",2));
        tiny.put("To",reader.getInt("r.To",2));
        tiny.put("Tt",reader.getInt("r.Tt",3));
        tiny.put("Sr",reader.getInt("r.Sr",1));
        tiny.put("So",reader.getInt("r.So",3));
        tiny.put("St",reader.getInt("r.St",3));

        sumInstance = r + o + t;
    }

}
