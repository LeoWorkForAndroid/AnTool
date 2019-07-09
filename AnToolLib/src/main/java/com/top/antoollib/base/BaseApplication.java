package com.top.antoollib.base;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

/**
 * 作者：ProZoom
 * 时间：2017/12/30
 * 描述：
 */
public abstract class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private ArrayList<Activity> activityList = new ArrayList<>();

    /**
     * 添加到ArrayList<Activity>
     *
     * @param activity：Activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 退出所有的Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}
