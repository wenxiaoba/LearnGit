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
public class OccasionFragment extends ListFragment {

    private APPListViewAdapter adapter; //ListView的适配器
    private ArrayList<AppDetail> data;  //偶尔使用的App本身数据，如图标、名称和提议
    PackageManager pckManager;
    PackageInfo pckInfo = null;
    static final int REQUEST_UNINSTALL = 2; //App卸载完成的标志
    AppInfo deleteApp;  //将要卸载的App信息
    int deleteIndex;    //将要卸载的App位置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("xyz","OccasionFragment begin!");
        if(getArguments().containsKey("occasion")){ //获取偶尔使用的App信息并用ListView显示出来
            ArrayList<AppInfo> appInfos = (ArrayList<AppInfo>) getArguments().getSerializable("occasion");
            data = getAppDetailsList(appInfos);
            adapter = new APPListViewAdapter(getActivity(),data);
            setListAdapter(adapter);
        }
    }
    public ArrayList<AppDetail> getAppDetailsList(ArrayList<AppInfo> appInfos){
    //获取App的所有信息
        ArrayList<AppDetail> apps = new ArrayList<AppDetail>();
        pckManager = getActivity().getPackageManager();
        for(AppInfo app : appInfos){
            Log.d("xyz",app.toString());
            AppDetail appDetail = new AppDetail();
            appDetail.setPackageName(app.getPackageName()); //设置包名
            appDetail.setTime(app.getTime());   //设置使用时长
            appDetail.setStarts(app.getStarts());   //设置启动次数
            appDetail.setTimeInterval(app.getTimeInterval());   //设置使用间隔

            try {
                pckInfo = pckManager.getPackageInfo(appDetail.getPackageName(),0);
                appDetail.setName(pckInfo.applicationInfo.loadLabel(pckManager).toString());    //设置App名称
                appDetail.setIcon(pckInfo.applicationInfo.loadIcon(pckManager));    //设置App图标
                appDetail.setDescribe("偶尔使用");  //设置分类
//                Log.d("xyz",appDetail.getName()+"   "+appDetail.getPackageName()+"  "+appDetail.getTime()+" "+appDetail.getStarts());
                apps.add(appDetail);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                pckInfo = null;
            }
        }
        return apps;
    }

    private ArrayList<String> getInstallApp(){  //获取手机上已安装的App，用以判断是否卸载成功
        ArrayList<String> installApp = new ArrayList<String>();
        List<PackageInfo> packageInfoList = pckManager.getInstalledPackages(0);
        for(PackageInfo pckInfo : packageInfoList){
            if ((pckInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //是否为用户App
                if(pckInfo.packageName.equals("com.example.wechatsample"))
                    continue;
                installApp.add(pckInfo.packageName);
            }
        }
        return installApp;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        deleteApp = (AppInfo)(getListAdapter()).getItem(position);
        deleteIndex = position;
        //调用系统接口卸载App
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:"+deleteApp.getPackageName()));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, REQUEST_UNINSTALL);
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        boolean have = false;
        if (requestCode == REQUEST_UNINSTALL) { //卸载完成后对返回的信息进行处理处理
            ArrayList<String> installApps = getInstallApp();    //获取当前安装的App
            for(int i = 0; i < installApps.size(); i++){
                if(installApps.get(i).equals(deleteApp.getPackageName())){  //若包含要卸载的App，则表示用户取消了卸载
                    have = true;
                    break;
                }
            }
            if(have == false){  //未包含要卸载的App，表示用户完成了卸载
                adapter.remove(deleteIndex);    //更新ListView
                Log.d("xyz","you delete the app");
            }
        }
    }
}
