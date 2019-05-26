package com.top.antoollib;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

/**
 * 作者：ProZoom
 * 时间：2018/3/16
 * 描述：设备相关工具类
 */
public class UiTool {

    private static final String TAG = "AppTool";
    private static volatile UiTool instance;// !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
    private static final Object lock = new Object();


    public UiTool() {

    }

    //单例模式，懒汉氏
    public static UiTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new UiTool();
                }
            }
        }
        return instance;
    }

    //////////////////////////////////////////////////////////////////////
     /**
     * 禁用某一layout下所有控件
     * @param viewGroup
     * @param enable
     */
    public  void disableSubControls(ViewGroup viewGroup, Boolean enable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                if (v instanceof Spinner) {
                    Spinner spinner = (Spinner) v;
                    spinner.setClickable(enable);
                    spinner.setEnabled(enable);

                    //Log.i(TAG, "A Spinner is unabled");
                } else if (v instanceof ListView) {
                    ((ListView) v).setClickable(enable);
                    ((ListView) v).setEnabled(enable);

                    //Log.i(TAG, "A ListView is unabled");
                } else {
                    disableSubControls((ViewGroup) v, enable);
                }
            } else if (v instanceof EditText) {
                ((EditText) v).setEnabled(enable);
                ((EditText) v).setClickable(enable);

                //Log.i(TAG, "A EditText is unabled");
            } else if (v instanceof Button) {
                ((Button) v).setEnabled(enable);

                // Log.i(TAG, "A Button is unabled");
            }
        }
    }


    /**
     * 按钮是否快速点击
     *
     * @return
     */

    private  long lastClickTime;

    public synchronized  boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
