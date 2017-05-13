package com.example.appmonitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.appmonitor.classes.AppInfo;
import com.example.appmonitor.fragment.OccasionFragment;
import com.example.appmonitor.fragment.RegularFragment;
import com.example.appmonitor.fragment.TinyFragment;
import com.example.appmonitor.service.MonitorService;


public class MainActivity extends FragmentActivity {


    private RegularFragment regularFragment;
    private OccasionFragment occasionFragment;
    private TinyFragment tinyFragment;
    static final int REQUEST_UNINSTALL = 2;
    private long mExitTime;

    /**
     * PagerSlidingTabStrip的实例
     */
    private PagerSlidingTabStrip tabs;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    private int Max = 45;

    private ArrayList<AppInfo> regularyApp = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> occasionApp = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> tinyApp = new ArrayList<AppInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent startIntent = new Intent(MainActivity.this,MonitorService.class);
        startService(startIntent);//启动服务

        setOverflowShowingAlways();
        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
        ArrayList<String> installApps = getInstallApp();
        ArrayList<AppInfo> apps = BayesMethod.getApps(getRawData(installApps));
        SharedPreferences setReader = getSharedPreferences("setData",MODE_PRIVATE);
        ArrayList<String> classGroup = BayesMethod.makeClassify(MainActivity.this,apps,setReader);
//        for(AppInfo app : apps){
//            Log.d("xyz",app.toString());
//        }
//        Log.d("xyz","size = "+apps.size()+" == "+classGroup.size());
        makeBranches(apps,classGroup);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        if (requestCode == REQUEST_UNINSTALL) {

            Toast.makeText(MainActivity.this,"get the delete message",Toast.LENGTH_LONG).show();
        }
    }



    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "经常使用", "偶尔使用", "极少使用" };

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (regularFragment == null) {
                        Bundle regularBundle = new Bundle();
                        regularBundle.putSerializable("regulary",regularyApp);
                        regularFragment = new RegularFragment();
                        regularFragment.setArguments(regularBundle);
                    }
                    return regularFragment;

                case 1:
                    if (occasionFragment == null) {
                        Bundle occasionBundle = new Bundle();
                        occasionBundle.putSerializable("occasion",occasionApp);
                        occasionFragment = new OccasionFragment();
                        occasionFragment.setArguments(occasionBundle);
                    }
                    return occasionFragment;
                case 2:
                    if (tinyFragment == null) {
                        Bundle tinyBundle = new Bundle();
                        tinyBundle.putSerializable("tiny",tinyApp);
                        tinyFragment = new TinyFragment();
                        tinyFragment.setArguments(tinyBundle);
                    }
                    return tinyFragment;
                default:
                    return null;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
//                Toast.makeText(this, "you select the Setting button", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makeBranches(ArrayList<AppInfo> apps,ArrayList<String> classGroup){
        for(int i = 0; i < apps.size(); i++){
            AppInfo  app = apps.get(i);
            if(classGroup.get(i) == "r")
                regularyApp.add(app);
            else if(classGroup.get(i) == "o")
                occasionApp.add(app);
            else
                tinyApp.add(app);
        }
    }


    //获取已安装的用户App的包名
    private ArrayList<String> getInstallApp(){
        ArrayList<String> installApp = new ArrayList<String>();
        List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);
        for(PackageInfo pckInfo : packageInfoList){
            if ((pckInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if(pckInfo.packageName.equals("com.example.wechatsample"))
                    continue;
                installApp.add(pckInfo.packageName);
            }
        }
        return installApp;
    }

    private HashMap<String,ArrayList<Integer>> getRawData(ArrayList<String> installApp){
        if(installApp == null){
            return null;
        }
        HashMap<String,ArrayList<Integer>> rawData = new HashMap<String,ArrayList<Integer>>();
        int time = 0;
        int start = 0;
        for(String app : installApp){
            ArrayList<Integer> count = new ArrayList<Integer>();
            count.add(0);
            rawData.put(app,count);
        }
        int day = getSharedPreferences("data",MODE_PRIVATE).getInt("index",0);
        int now;
        for(int index = 0; index < Max; index++){
//            now = (day  - index + Max) % (Max + 1);
            now = (day-index+Max)%Max;
            SharedPreferences reader = getSharedPreferences(now+"",MODE_PRIVATE);
            for(String app : installApp){
                time = reader.getInt(app+".time",0);
                start = reader.getInt(app+".start",0);
                ArrayList<Integer> temp = rawData.get(app);
                temp.add(time);
                if(time != 0){
                    temp.set(0,temp.get(0)+start);
                }
                rawData.put(app,temp);
//                Log.d("xyz","name = "+app+" time = "+time+" start = "+start);
            }
        }
        return rawData;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }

    private void exitBy2Click() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "在按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

}




