package yifan.com.runningapplicationtest.model;

import android.graphics.drawable.Drawable;

/**
 * 这是一个实体类,就是描述应用的一些信息
 *
 * Created by yifan on 2017/3/7.
 */
public class TaskInfo {

    // 应用程序的图标
    private Drawable icon;
    // 应用程序的名字
    private String name;
    // 应用程序的包名
    private String packname;
    // 占用内存的大小
    private long memsize;
    // true 用户进程 false 系统进程
    private boolean userTask;

    //是否已经勾选
    private boolean cbchecked;

    public boolean isCbchecked() {
        return cbchecked;
    }

    public void setCbchecked(boolean cbchecked) {
        this.cbchecked = cbchecked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public long getMemsize() {
        return memsize;
    }

    public void setMemsize(long memsize) {
        this.memsize = memsize;
    }

    public boolean isUserTask() {
        return userTask;
    }

    public void setUserTask(boolean userTask) {
        this.userTask = userTask;
    }

    @Override
    public String toString() {
        return "ProgressInfo [icon=" + icon + ", name=" + name + ", packname="
                + packname + ", memsize=" + memsize + ", userTask=" + userTask
                + "]";
    }

}