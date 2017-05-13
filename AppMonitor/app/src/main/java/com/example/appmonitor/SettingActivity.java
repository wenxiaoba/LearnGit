package com.example.appmonitor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements View.OnClickListener {

    private static String dialogTitle;
    private static int dialogInput;
    private  int yourChoice;

    private TextView changeTv;

    SharedPreferences reader;
    SharedPreferences.Editor writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        reader = getSharedPreferences("setData",MODE_PRIVATE);
        writer = getSharedPreferences("setData",MODE_PRIVATE).edit();

        Message msg = new Message();
        msg.what = 1;
        numChange.sendMessage(msg);

        LinearLayout reTimeLy = (LinearLayout) findViewById(R.id.reTimeLy);
        LinearLayout reStartLy = (LinearLayout) findViewById(R.id.reStartLy);
        LinearLayout reInterLy = (LinearLayout) findViewById(R.id.reInterLy);
        LinearLayout ocTimeLy = (LinearLayout) findViewById(R.id.ocTimeLy);
        LinearLayout ocStartLy = (LinearLayout) findViewById(R.id.ocStartLy);
        LinearLayout ocInterLy = (LinearLayout) findViewById(R.id.ocInterLy);
        LinearLayout tiTimeLy = (LinearLayout) findViewById(R.id.tiTimeLy);
        LinearLayout tiStartLy = (LinearLayout) findViewById(R.id.tiStartLy);
        LinearLayout tiInterLy = (LinearLayout) findViewById(R.id.tiInterLy);
        Button commit = (Button) findViewById(R.id.setCommit);

        reTimeLy.setOnClickListener(SettingActivity.this);
        reStartLy.setOnClickListener(SettingActivity.this);
        reInterLy.setOnClickListener(SettingActivity.this);
        ocTimeLy.setOnClickListener(SettingActivity.this);
        ocStartLy.setOnClickListener(SettingActivity.this);
        ocInterLy.setOnClickListener(SettingActivity.this);
        tiTimeLy.setOnClickListener(SettingActivity.this);
        tiStartLy.setOnClickListener(SettingActivity.this);
        tiInterLy.setOnClickListener(SettingActivity.this);
        commit.setOnClickListener(SettingActivity.this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reInterLy:
                changeTv = (TextView) findViewById(R.id.reInterNum);
                dialogTitle = "经常使用时间间隔";
                showSingleChoiceDialog();
                break;

            case R.id.reTimeLy:
                changeTv = (TextView) findViewById(R.id.reTimeNum);
                dialogTitle = "经常使用时长";
                showCustomizeDialog();
                break;

            case R.id.reStartLy:
                changeTv = (TextView) findViewById(R.id.reStartNum);
                dialogTitle = "经常使用启动次数";
                showCustomizeDialog();
                break;

            case R.id.ocInterLy:
                changeTv = (TextView) findViewById(R.id.ocInterNum);
                dialogTitle = "偶尔使用时间间隔";
                showSingleChoiceDialog();
                break;

            case R.id.ocTimeLy:
                changeTv = (TextView) findViewById(R.id.ocTimeNum);
                dialogTitle = "偶尔使用时长";
                showCustomizeDialog();
                break;

            case R.id.ocStartLy:
                changeTv = (TextView) findViewById(R.id.ocStartNum);
                dialogTitle = "偶尔使用启动次数";
                showCustomizeDialog();
                break;

            case R.id.tiInterLy:
                changeTv = (TextView) findViewById(R.id.tiInterNum);
                dialogTitle = "极少使用时间间隔";
                showSingleChoiceDialog();
                break;

            case R.id.tiTimeLy:
                changeTv = (TextView) findViewById(R.id.tiTimeNum);
                dialogTitle = "极少使用时长";
                showCustomizeDialog();
                break;

            case R.id.tiStartLy:
                changeTv = (TextView) findViewById(R.id.tiStartNum);
                dialogTitle = "极少使用启动次数";
                showCustomizeDialog();
                break;

            case R.id.setCommit:
                char result = judgeSetCommit();
                if(result == 's'){
                    Toast.makeText(SettingActivity.this,"You start is set incorrectly!",Toast.LENGTH_LONG).show();
                }else if(result == 'i'){
                    Toast.makeText(SettingActivity.this,"You interval is set incorrectly!",Toast.LENGTH_LONG).show();
                }else if(result == 't'){
                    Toast.makeText(SettingActivity.this,"You time is set incorrectly!",Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                startActivity(intent);
                SettingActivity.this.finish();
                break;


            default:
                break;
        }

    }


    public char judgeSetCommit(){
        TextView reTimeNum = (TextView) findViewById(R.id.reTimeNum);
        TextView reInterNum = (TextView) findViewById(R.id.reInterNum);
        TextView reStartNum = (TextView) findViewById(R.id.reStartNum);
        TextView ocTimeNum = (TextView) findViewById(R.id.ocTimeNum);
        TextView ocInterNum = (TextView) findViewById(R.id.ocInterNum);
        TextView ocStartNum = (TextView) findViewById(R.id.ocStartNum);
        TextView tiTimeNum = (TextView) findViewById(R.id.tiTimeNum);
        TextView tiInterNum = (TextView) findViewById(R.id.tiInterNum);
        TextView tiStartNum = (TextView) findViewById(R.id.tiStartNum);

        int reTN = Integer.parseInt(reTimeNum.getText().toString());
        int reSN = Integer.parseInt(reStartNum.getText().toString());
        int reIN = Integer.parseInt(reInterNum.getText().toString());
        int ocTN = Integer.parseInt(ocTimeNum.getText().toString());
        int ocSN = Integer.parseInt(ocStartNum.getText().toString());
        int ocIN = Integer.parseInt(ocInterNum.getText().toString());
        int tiTN = Integer.parseInt(tiTimeNum.getText().toString());
        int tiSN = Integer.parseInt(tiStartNum.getText().toString());
        int tiIN = Integer.parseInt(tiInterNum.getText().toString());

        if((reTN >= ocTN) &&(ocTN > tiTN)){
            if((reIN < ocIN) && (ocIN <= tiTN)){
                if((reSN > ocSN) && (ocSN > tiSN)){
                    writer.putInt("reTimeNum",reTN);
                    writer.putInt("reStartNum",reSN);
                    writer.putInt("reInterNum",reIN);

                    writer.putInt("ocTimeNum",ocTN);
                    writer.putInt("ocStartNum",ocSN);
                    writer.putInt("ocInterNum",ocIN);

                    writer.putInt("tiTimeNum",tiTN);
                    writer.putInt("tiStartNum",tiSN);
                    writer.putInt("tiInterNum",tiIN);
                    writer.commit();
                    return 'y';
                }else
                {
                    return 's';
                }

            }
            return 'i';
        }
        return 't';
    }


    private void showSingleChoiceDialog(){


        final String[] items = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(SettingActivity.this);
        singleChoiceDialog.setTitle(dialogTitle);
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                            changeTv.setText(yourChoice+"");
                        }
                    }
                });
        singleChoiceDialog.show();
    }

    Handler numChange = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                TextView reTimeNum = (TextView) findViewById(R.id.reTimeNum);
                TextView reInterNum = (TextView) findViewById(R.id.reInterNum);
                TextView reStartNum = (TextView) findViewById(R.id.reStartNum);
                TextView ocTimeNum = (TextView) findViewById(R.id.ocTimeNum);
                TextView ocInterNum = (TextView) findViewById(R.id.ocInterNum);
                TextView ocStartNum = (TextView) findViewById(R.id.ocStartNum);
                TextView tiTimeNum = (TextView) findViewById(R.id.tiTimeNum);
                TextView tiInterNum = (TextView) findViewById(R.id.tiInterNum);
                TextView tiStartNum = (TextView) findViewById(R.id.tiStartNum);

                int num;
                num = reader.getInt("reTimeNum",20);
                reTimeNum.setText(num+"");
                num = reader.getInt("reInterNum",4);
                reInterNum.setText(num+"");
                num = reader.getInt("reStartNum",3);
                reStartNum.setText(num+"");
                Log.d("xyz","Handler run complete");

                num = reader.getInt("ocTimeNum",20);
                ocTimeNum.setText(num+"");
                num = reader.getInt("ocInterNum",7);
                ocInterNum.setText(num+"");
                num = reader.getInt("ocStartNum",2);
                ocStartNum.setText(num+"");

                num = reader.getInt("tiTimeNum",10);
                tiTimeNum.setText(num+"");
                num = reader.getInt("tiInterNum",7);
                tiInterNum.setText(num+"");
                num = reader.getInt("tiStartNum",1);
                tiStartNum.setText(num+"");

            }
        }
    };


    private void showCustomizeDialog() {

        final View dialogView = LayoutInflater.from(SettingActivity.this)
                .inflate(R.layout.dialog_setting,null);

        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(SettingActivity.this);

        customizeDialog.setTitle(dialogTitle);
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText edit_text =
                                (EditText) dialogView.findViewById(R.id.timeInput);
                        String num = edit_text.getText().toString();
//                        Log.d("xyz","---->"+num+"lalalla");
                        try{
                            dialogInput = Integer.parseInt(num);

                            changeTv.setText(dialogInput+"");
                        }catch (Exception e){
//                            Log.d("xyz","-->"+num+";");
//                            Log.d("xyz",e.toString());
                            Toast.makeText(getBaseContext(),"You enter a wrong number!",Toast.LENGTH_LONG).show();
                        }

                    }
                });
        customizeDialog.show();
    }



}

