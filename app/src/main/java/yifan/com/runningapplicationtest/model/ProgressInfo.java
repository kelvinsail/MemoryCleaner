package yifan.com.runningapplicationtest.model;

import android.content.pm.ApplicationInfo;

/**
 * Created by yifan on 2017/3/10.
 */

public class ProgressInfo {
    public String user;//  进程的当前用户；
    public String pid;// 毫无疑问, process ID的缩写，也就进程号；
    public String ppid;//process parent ID，父进程ID
    public String vsize;// virtual size，进程虚拟地址空间大小；
    public String rss;// 进程正在使用的物理内存的大小；
    public String wchan;//进程如果处于休眠状态的话，在内核中的地址；
    public String pc;// program counter，
    public String unknown_symble;//未知符号含义
    public String name; //process name，进程的名称，即包名

    public String applicationName;//应用名
    public ApplicationInfo info; //应用信息
    public long memorySize;//内存使用量
}

