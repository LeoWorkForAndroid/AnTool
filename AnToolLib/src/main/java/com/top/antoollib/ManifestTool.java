package com.top.antoollib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * 作者：李阳
 * 时间：2018/10/8
 * 描述：manifest工具类
 */
public class ManifestTool {


    private static final String TAG ="ManifestTool";

    private static volatile ManifestTool instance;
    private static final Object lock = new Object();


    public ManifestTool() {

    }

    //单例模式，懒汉氏
    public static ManifestTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ManifestTool();
                }
            }
        }
        return instance;
    }




    /**
     * 返回Manifest指定meta-data值
     * @param context 全局context
     * @param key meta-data key
     * @return
     *      成功-value
     *      失败-""
     */
    public  String getMetaData(Context context, String key) {
        ApplicationInfo app_info = null;
        try {
            app_info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if(app_info == null || app_info.metaData == null) {
                return "";
            } else {
                Object obj = app_info.metaData.get(key);
                if(obj != null) {
                    return obj.toString();
                } else {
                    return "";
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "getMetaData error", e);
            return "";
        }
    }

    /**
     * 获取版本名
     * @param context 全局context
     * @return versoin namei
     */
    public  String getVersionName(Context context) {
        String version = "0.0";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (Exception e) {
            Log. i(TAG, "getVersionName error", e);
        }

        return version;
    }

}
