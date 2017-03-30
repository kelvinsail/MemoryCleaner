package yifan.com.runningapplicationtest.model;

/**
 * Created by yifan on 2017/3/10.
 */

public class ApplicationTaskInfo {

    /**
     * 应用名字
     */
    String applicationName;

    /**
     * 包名
     */
    String packageName;

    /**
     * 进程id
     */
    int pid;

    /**
     * 内存驻留
     */
    long memorySize;

    private ApplicationTaskInfo(ProgressInfo info) {
        this.applicationName = info.applicationName;
        this.packageName = info.name;
        this.pid = Integer.parseInt(info.pid);
        this.memorySize = info.memorySize;
    }

    private ApplicationTaskInfo(TaskInfo info, int pid) {
        this.applicationName = info.getName();
        this.packageName = info.getPackname();
        this.pid = pid;
        this.memorySize = info.getMemsize();
    }

    public static ApplicationTaskInfo getApplicationTaskInfo(TaskInfo info, int pid) {
        return new ApplicationTaskInfo(info, pid);
    }

    public static ApplicationTaskInfo getApplicationTaskInfo(ProgressInfo info) {
        return new ApplicationTaskInfo(info);
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }
}
