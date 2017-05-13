package com.example.appmonitor.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appmonitor.R;
import com.example.appmonitor.classes.AppDetail;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21 0021.
 */
public class APPListViewAdapter extends BaseAdapter {

    private Context context;
    private List<AppDetail> data;

    public APPListViewAdapter(Context context, List<AppDetail> data) {
        this.context = context;
        this.data = data;
    }


    public void remove(int deleteIndex){    //删除ListView中位置为deleteIndex的条目
        if(data != null){
            data.remove(deleteIndex);
        }
        notifyDataSetChanged(); //更新ListView
    }

    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.itme, null); //获取itme布局
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.app_img);
        TextView name = (TextView) convertView.findViewById(R.id.app_name);
        TextView time = (TextView) convertView.findViewById(R.id.app_time);
        TextView start_ups = (TextView) convertView.findViewById(R.id.app_start_ups);
        TextView interval = (TextView) convertView.findViewById(R.id.app_interval);
        TextView descripe = (TextView) convertView.findViewById(R.id.app_descripe);

        icon.setImageDrawable(data.get(position).getIcon());
        name.setText(data.get(position).getName());
        time.setText(data.get(position).getTime()+"分钟/天 ");
        start_ups.setText(data.get(position).getStarts()+"次/天 ");
//        interval.setText(" 隔"+data.get(position).getTimeInterval()+"天使用");
        descripe.setText(data.get(position).getDescribe());
        return convertView;
    }

}
