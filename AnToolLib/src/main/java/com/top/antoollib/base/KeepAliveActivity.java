package com.top.antoollib.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 作者：李阳
 * 时间：2018/9/4
 * 描述：
 */
public abstract class KeepAliveActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //只有左上角的一个点，主要为了使用户无感知
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);

        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

}
