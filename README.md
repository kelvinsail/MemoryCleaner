# MemoryCleaner
内存清理／手机加速demo

原有的getRunningTask()、getRunninAppgProcess()接口在5.0/5.1中分别失效，还有以下两种方式获取相对应的进程信息

方式一：通过com.jaredrummler.android.processes所提供的ProcessManager类，该类通过读取/proc来获取正在运行的进程以及pid等信息，但是该方法在android 7.0中失效；

方式二：在root环境下，通过shell ps命令来获取进程列表，手动过滤相关参数得到包名以及进程pid，缺点是必须root下才能使用；

如果要强制结束进程，则使用ActivityManager.killBackgroundProcesses(PackageName)
