package yifan.com.runningapplicationtest;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

import yifan.com.runningapplicationtest.model.ApplicationTaskInfo;
import yifan.com.runningapplicationtest.model.ProgressInfo;
import yifan.com.runningapplicationtest.model.TaskInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "getProcess";

    ProgressDialog mDialog;
    SwipeRefreshLayout mPtr;
    List<ApplicationTaskInfo> mTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(this);
        mPtr = (SwipeRefreshLayout) findViewById(R.id.ptr_main);
        mPtr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder _str = new StringBuilder();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    mTaskList = getTaskInfos(MainActivity.this);
                } else {
                    mTaskList = getProgressInfos();
                }

                if (null != mTaskList) {
                    for (ApplicationTaskInfo info : mTaskList) {
                        if (_str.length() > 0) {
                            _str.append("\n").append("\n");
                        }
                        _str.append("Name: ").append(info.getApplicationName()).append("\n")
                                .append("Package: ").append(info.getPackageName()).append("\n")
                                .append("MemorySize:").append(info.getMemorySize() / 1024 / 1024).append("MB");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_main)).setText(_str.toString());
                        if (null != mPtr) {
                            mPtr.setRefreshing(false);
                        }
                        if (null != mDialog) {
                            mDialog.dismiss();
                            mDialog = null;
                        }
                    }
                });
            }
        }).start();
    }


    /**
     * 获取系统运行的进程信息
     *
     * @param context
     * @return
     */
    public static List<ApplicationTaskInfo> getTaskInfos(Context context) {
        // 应用程序管理器
        ActivityManager am = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);

        // 应用程序包管理器
        PackageManager pm = context.getPackageManager();

        // 获取正在运行的程序信息, 就是以下粗体的这句代码,获取系统运行的进程
        List<AndroidAppProcess> processInfos = ProcessManager.getRunningAppProcesses();
        //转换之后进程列表数据
        List<ApplicationTaskInfo> taskinfos = new ArrayList<ApplicationTaskInfo>();
        // 遍历运行的程序,并且获取其中的信息
        for (AndroidAppProcess processInfo : processInfos) {
            if (processInfo.getPackageName().equals(context.getPackageName())) {
                continue;
            }
            TaskInfo taskinfo = new TaskInfo();
            // 应用程序的包名
            String packname = processInfo.name;
            if (packname.indexOf(":") >= 0
                    || packname.indexOf("/") >= 0) {
                continue;
            }
            taskinfo.setPackname(packname);
            // 获取应用程序的内存 信息
            android.os.Debug.MemoryInfo[] memoryInfos = am
                    .getProcessMemoryInfo(new int[]{processInfo.pid});
            long memsize = memoryInfos[0].getTotalPrivateDirty() * 1024L;
            taskinfo.setMemsize(memsize);
            try {
                // 获取应用程序信息
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskinfo.setIcon(icon);
                String name = applicationInfo.loadLabel(pm).toString();
                taskinfo.setName(name);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // 用户进程
                    taskinfo.setUserTask(true);
                } else {
                    // 系统进程
                    taskinfo.setUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                //                e.printStackTrace();
                // 系统内核进程 没有名称
                continue;
            }
            if (taskinfo != null && taskinfo.isUserTask()) {
                taskinfos.add(ApplicationTaskInfo.getApplicationTaskInfo(taskinfo, processInfo.pid));
            }
        }
        return taskinfos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog = new ProgressDialog(MainActivity.this);
                                mDialog.setMessage("Cleanning...");
                                mDialog.show();
                                mDialog.setCancelable(false);
                            }
                        });
                        if (null != mTaskList && mTaskList.size() > 0) {
                            try {
                                //防止以后Android版本升级后，封锁权限导致抛出异常
                                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                for (ApplicationTaskInfo info : mTaskList) {
                                    Log.i(TAG, "close: " + info.getPackageName());
                                    activityManager.killBackgroundProcesses(info.getPackageName());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadData();
                            }
                        });
                    }
                }).start();

                break;
        }
    }


    /**
     * root下通过shell ps命令的方式获取进程列表数据
     *
     * @return
     */
    public List<ApplicationTaskInfo> getProgressInfos() {
        //转换后的数组数据
        List<ApplicationTaskInfo> list = new ArrayList<>();
        CommandExecution.CommandResult result = CommandExecution.execCommand(new String[]{"ps |grep SyS_epoll_"}, true, true);
        if (result.result == 0 && !TextUtils.isEmpty(result.successMsg)) {
            //按照换行符切分
            String[] infos = result.successMsg.split("\\n");
            //获取包管理器
            PackageManager pm = getPackageManager();
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (String info : infos) {
                String[] params = info.split(" ");
                ProgressInfo taskInfo = new ProgressInfo();
                for (String param : params) {
                    if (TextUtils.isEmpty(param)) {
                        continue;
                    }
                    if (TextUtils.isEmpty(taskInfo.user)) {
                        taskInfo.user = param;
                    } else if (TextUtils.isEmpty(taskInfo.pid)) {
                        taskInfo.pid = param;
                    } else if (TextUtils.isEmpty(taskInfo.ppid)) {
                        taskInfo.ppid = param;
                    } else if (TextUtils.isEmpty(taskInfo.vsize)) {
                        taskInfo.vsize = param;
                    } else if (TextUtils.isEmpty(taskInfo.rss)) {
                        taskInfo.rss = param;
                    } else if (TextUtils.isEmpty(taskInfo.wchan)) {
                        taskInfo.wchan = param;
                    } else if (TextUtils.isEmpty(taskInfo.pc)) {
                        taskInfo.pc = param;
                    } else if (TextUtils.isEmpty(taskInfo.unknown_symble)) {
                        taskInfo.unknown_symble = param;
                    } else if (TextUtils.isEmpty(taskInfo.name)) {
                        //如果进程名不含"."，则不是应用进程
                        if (param.indexOf(".") <= 0) {
                            continue;
                        }
                        //包含其他符号，也不是应用进程
                        if (param.indexOf("/") >= 0
                                || param.indexOf(":") >= 0) {
                            continue;
                        }
                        //排除当前app
                        if (param.equals(getPackageName())) {
                            continue;
                        }
                        //检查该进程是否有可启动的界面
                        if (pm.getLaunchIntentForPackage(taskInfo.name) == null) {
                            continue;
                        }
                        //获取应用名称等信息
                        try {
                            ApplicationInfo applicationInfo = pm.getApplicationInfo(param, 0);
                            if (null == applicationInfo) {
                                continue;
                            }
                            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                                continue;
                            }
                            taskInfo.applicationName = (String) pm.getApplicationLabel(applicationInfo);
                            taskInfo.info = applicationInfo;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            //查询发生异常，不是用户安装的app
                            continue;
                        }
                        taskInfo.name = param;
                    }
                }
                if (!TextUtils.isEmpty(taskInfo.name)) {
                    // 获取应用程序的内存 信息
                    android.os.Debug.MemoryInfo[] memoryInfos = am
                            .getProcessMemoryInfo(new int[]{Integer.parseInt(taskInfo.pid)});
                    taskInfo.memorySize = memoryInfos[0].getTotalPrivateDirty() * 1024L;
                    list.add(ApplicationTaskInfo.getApplicationTaskInfo(taskInfo));
                }
            }
        }
        return list;
    }
}
