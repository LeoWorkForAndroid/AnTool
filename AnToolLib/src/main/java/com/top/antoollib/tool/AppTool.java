package com.top.antoollib.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 作者：ProZoom
 * 时间：2018/3/16
 * 描述：设备相关工具类
 */
public class AppTool {

    private static final String TAG = "AppTool";
    /**
     * !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
     */
    private static volatile AppTool instance;
    private static final Object lock = new Object();


    public AppTool() {}

    /**
     * 单例模式，懒汉氏
     *
     * @return
     */
    public static AppTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AppTool();
                }
            }
        }
        return instance;
    }


    /**
     * 获取VersionName
     *
     * @param context
     * @return
     */
    public String getAppVersionName(Context context) {
        try {
            String pkName = context.getPackageName();
            return context.getPackageManager().getPackageInfo(pkName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @param context
     * @return
     */
    public int getAppVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取当前App图标
     *
     * @param context 上下文
     * @return App图标
     */
    public Drawable getAppIcon(Context context) {
        return getAppIcon(context, context.getPackageName());
    }

    /**
     * 获取指定App图标
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App图标
     */
    public Drawable getAppIcon(Context context, String packageName) {
        if (packageName == null) {
            return null;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取当前App路径
     *
     * @param context 上下文
     * @return App路径
     */
    public String getAppPath(Context context) {
        return getAppPath(context, context.getPackageName());
    }

    /**
     * 获取指定App路径
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App路径
     */
    public String getAppPath(Context context, String packageName) {
        if (packageName == null) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 判断当前App是否是系统应用
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isSystemApp(Context context) {
        return isSystemApp(context, context.getPackageName());
    }

    /**
     * 判断指定App是否是系统应用
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isSystemApp(Context context, String packageName) {
        if (packageName == null) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断App是否是Debug版本
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isAppDebug(Context context) {
        return isAppDebug(context, context.getPackageName());
    }

    /**
     * 判断App是否是Debug版本
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isAppDebug(Context context, String packageName) {
        if (packageName == null) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取App签名
     *
     * @param context 上下文
     * @return App签名
     */
    public Signature[] getAppSignature(Context context) {
        return getAppSignature(context, context.getPackageName());
    }

    /**
     * 获取App签名
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App签名
     */
    @SuppressLint("PackageManagerGetSignatures")
    public Signature[] getAppSignature(Context context, String packageName) {
        if (packageName == null) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据包名判断手机上是否安装了此app
     *
     * @param context
     * @param packageName
     * @return
     */
    public final boolean isApkInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * 判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 判断是否Wi-Fi上网
     *
     * @param context
     * @return
     */
    public boolean isWifi(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting()
                && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

}
