package com.example.appmonitor.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/21 0021.
 */
public class ForegroundProcess {

    private static final String TAG = "TAGCollector";
    /** first app user */
    public static final int AID_APP = 10000;
    /** offset for uid ranges for each user */
    public static final int AID_USER = 100000;


    public static String getForegroundApp() {
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = -1;
        String foregroundProcess = null;
        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            int pid;
            try {
                //目录名为数字的为进程文件
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                String cgroup = read(String.format("/proc/%d/cgroup", pid));    //获取进程目录的cgroup文件内容
                String[] lines = cgroup.split("\n");
                String cpuaccctSubsystem = null;

                for(int i = 0; i <lines.length; i++){
                    if(lines[i].contains("cpuacct"))
                        cpuaccctSubsystem = lines[i];
                }
                if(cpuaccctSubsystem == null)   //若为App进程文件，则cpuaccctSubsystem不为空，排除非App进程，如root进程和部分System进程
                    continue;

                String cmdline = read(String.format("/proc/%d/cmdline", pid));  //获取cmdline文件内容
                if(cmdline == null){    //App进程文件中，cmdline存放App包名packageName,不为空
                    continue;
                }
                else if (cmdline.contains("com.android.systemui")) {    //系统UI进程
                    continue;
                }

                int uid;
                //包含字符串“uid”的为App进程，据此可以排除非App进程，可以根据cpuaccctSubsystem获取UID
                if(cpuaccctSubsystem.contains("uid_")){
                    uid = Integer.parseInt(
                            cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                }
                else if(cpuaccctSubsystem.contains("uid")){
                    uid = Integer.parseInt(
                            cpuaccctSubsystem.split(":")[2].split("/")[2]);
                }
                else{
                    continue;
                }

                //Log.d(TAG,"       uid = "+uid);
                if (uid >= 1000 && uid <= 1038) {
                    // system process
                    continue;
                }
                int appId = uid - AID_APP;
                int userId = 0;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.
                while (appId > AID_USER) {
                    appId -= AID_USER;
                    userId++;
                }


                if (appId < 0) {
                    continue;
                }
                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }

                }
                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
                if (oomscore >= lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return foregroundProcess;
    }
    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString().trim();//不调用trim()，包名后面会带有乱码
    }
}

