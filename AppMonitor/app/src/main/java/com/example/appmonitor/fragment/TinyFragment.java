package com.example.appmonitor.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.appmonitor.classes.AppDetail;
import com.example.appmonitor.classes.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11 0011.
 */
public class TinyFragment extends ListFragment {

    private APPListViewAdapter adapter; //ListView的适配器
    private ArrayList<AppDetail> data;  //极少使用的App本身的数据，如图标、名称和提议
    PackageManager pckManager;
    PackageInfo pckInfo = null;
    static final int REQUEST_UNINSTALL = 2; //App卸载完成的标志
    AppInfo deleteApp;  //将要卸载的App信息
    int deleteIndex;    //将要卸载的App位置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey("tiny")){ //获取极少使用的App信息并用ListView显示出来
            ArrayList<AppInfo> appInfos = (ArrayList<AppInfo>) getArguments().getSerializable("tiny");
            data = getAppDetailsList(appInfos);
            adapter = new APPListViewAdapter(getActivity(),data);
            setListAdapter(adapter);
        }
    }

    private ArrayList<String> getInstallApp(){
        ArrayList<String> installApp = new ArrayList<String>();
        List<PackageInfo> packageInfoList = pckManager.getInstalledPackages(0);
        for(PackageInfo pckInfo : packageInfoList){
            if ((pckInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if(pckInfo.packageName.equals("com.example.wechatsample"))
                    continue;
                installApp.add(pckInfo.packageName);
            }
        }
        return installApp;
    }

    public ArrayList<AppDetail> getAppDetailsList(ArrayList<AppInfo> appInfos){
        //获取App的所有信息
        ArrayList<AppDetail> apps = new ArrayList<AppDetail>();
        pckManager = getActivity().getPackageManager();
        for(AppInfo app : appInfos){
//            Log.d("xyz",app.toString());
            AppDetail appDetail = new AppDetail();
            appDetail.setPackageName(app.getPackageName());
            appDetail.setTime(app.getTime());
            appDetail.setStarts(app.getStarts());
            appDetail.setTimeInterval(app.getTimeInterval());

            try {
                pckInfo = pckManager.getPackageInfo(appDetail.getPackageName(),0);
                appDetail.setName(pckInfo.applicationInfo.loadLabel(pckManager).toString());
                appDetail.setIcon(pckInfo.applicationInfo.loadIcon(pckManager));
                if(appDetail.getTimeInterval() == 45)
                    appDetail.setDescribe("从未使用，建议卸载！");
                else{
                    appDetail.setDescribe("极少使用");
                }
//                Log.d("xyz",appDetail.getName()+"   "+appDetail.getPackageName()+"  "+appDetail.getTime()+" "+appDetail.getStarts());
                apps.add(appDetail);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                pckInfo = null;
            }
        }
        return apps;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        deleteApp = (AppInfo)(getListAdapter()).getItem(position);
        deleteIndex = position;
        //填入想要实现的逻辑
//        Toast.makeText(getActivity(),app.toString(),Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:"+deleteApp.getPackageName()));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, REQUEST_UNINSTALL);
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        boolean have = false;
        if (requestCode == REQUEST_UNINSTALL) {
            ArrayList<String> installApps = getInstallApp();
            for(int i = 0; i < installApps.size(); i++){
                if(installApps.get(i).equals(deleteApp.getPackageName())){
                    have = true;
                    break;
                }
            }
            if(have == false){
                adapter.remove(deleteIndex);
                Log.d("xyz","you delete the app");
            }
        }
    }


}
