package com.top.antoollib.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 作者：ProZoom
 * 时间：2018/3/16
 * 描述：设备相关工具类
 */
public class DeviceTool {

    private static final String TAG = "DeviceTool";
    /**
     * !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
     */
    private static volatile DeviceTool instance;
    private static final Object lock = new Object();


    public DeviceTool() {

    }

    /**
     * 单例模式，懒汉氏
     * @return
     */
    public static DeviceTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DeviceTool();
                }
            }
        }
        return instance;
    }


    /**
     * 获取手机屏幕信息
     *
     * @param activity
     * @return
     */
    public DisplayMetrics getScreenInfos(Activity activity) {
        // DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        // 获取手机窗口的Display 来初始化DisplayMetrics 对象
        // getManager()获取显示定制窗口的管理器。
        // 获取默认显示Display对象
        // 通过Display 对象的数据来初始化一个DisplayMetrics 对象
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    /**
     * 获取屏幕密度
     *
     * @param context
     * @return
     */
    public float getScreenDensity(Context context) {

        //获取系统的窗口管理服务
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();

        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(dm);
        }

        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;

        return dm.density;
    }

    /**
     * 获取屏幕像素
     *
     * @param context
     * @return Pix[0]:屏幕宽度像素
     * Pix[1[:屏幕高度像素
     */
    public float[] getScreenPix(Context context) {
        float[] pix = new float[]{0, 0};
        //获取系统的窗口管理服务
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(dm);
        }
        pix[0] = dm.widthPixels;
        pix[1] = dm.heightPixels;
        return pix;
    }

    /**
     * 获取屏幕
     *
     * @param context
     * @return dip[0]:屏幕宽度
     * dip[1[:屏幕高度像素
     */
    public float[] getScreenDip(Context context) {
        float[] dix = new float[]{0, 0};
        //获取系统的窗口管理服务
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(dm);
        }
        dix[0] = dm.widthPixels / getScreenDensity(context);
        dix[1] = dm.heightPixels / getScreenDensity(context);
        return dix;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return px
     */
    public int getStatusBarHeight(Context context) {

        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 根据手机的分辨率从从dp转成为px(像素)
     * @param context 全局context
     * @param dpValue dp值
     * @return px像素值
     */
    public  int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param context 全局context
     * @param pxValue px像素值
     * @return dp值
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    //////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    /**
     * 获取手机型号
     *
     * @return
     */
    public String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取系统的版本信息
     * @return
     */
    public String[] getAndroidVersion() {
        String[] version = {"null", "null", "null", "null"};
        String str1 = "/proc/version";
        String str2;
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            version[0] = arrayOfString[2];//KernelVersion  
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,e.toString());
        }
        version[1] = Build.VERSION.RELEASE;// firmware version  
        version[2] = Build.MODEL;//model  
        version[3] = Build.DISPLAY;//system version  
        return version;
    }


    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public String getIMEI(Context context) {

        //获取sim管理器
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //获取imei
        @SuppressLint("MissingPermission")
        String imei = telephonyManager.getDeviceId();

        if (TextUtils.isEmpty(imei)) {
            return "0-0-0";
        }
        return imei;
    }

    /**
     * 获取系统Mac地址
     *
     * @param context
     * @return
     */
    public String getMac(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String macAddress = wifiInfo.getMacAddress();

        if (TextUtils.isEmpty(macAddress)) {
            macAddress = "";
        }

        return macAddress;
    }

    /**
     * 获取本地ip地址
     * @return
     */
    public String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface inf = en.nextElement();
                for (Enumeration<InetAddress> emumIpAddr = inf.getInetAddresses(); emumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = emumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Log.i(TAG, "获取IP失败---->" + e.toString());
            return "0.0.0.0";
        }
        return "0.0.0.0";
    }




    /**
     * "/system/bin/cat" 命令行
     *  "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
     *  获取CPU最大频率（单位KHZ）
     * @return
     */
    public String getMaxCPUFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    /**
     * 获取CPU最小频率（单位KHZ）
     * @return
     */
    public String getMinCPUFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    /**
     * 实时获取CPU当前频率（单位KHZ）
     * @return
     */
    public String getCurCPUFreq() {
        String result = "N/A";
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取CPU名字
     * @return
     */
    public String getCPUName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取内存大小
     */
    public void getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2 = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                Log.i(TAG, "---" + str2);
            }
        } catch (IOException e) {

        }
    }


    /**
     * 获取Rom大小
     * @return
     */
    public long[] getRomMemroy() {
        long[] romInfo = new long[2];//Total rom memory  
        romInfo[0] = getTotalInternalMemorySize();
        //Available rom memory  
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        romInfo[1] = blockSize * availableBlocks;
        //getVersion();
        return romInfo;
    }

    /**
     * 获取内置内存大小
     * @return
     */
    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取SD卡大小
     * @return
     */
    public long[] getSDCardMemory() {
        long[] sdCardInfo = new long[2];
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSize();
            long bCount = sf.getBlockCount();
            long availBlocks = sf.getAvailableBlocks();
            sdCardInfo[0] = bSize * bCount;//总大小  
            sdCardInfo[1] = bSize * availBlocks;//可用大小  
        }
        return sdCardInfo;
    }
}
