package com.top.antoollib.tool;

import java.io.File;

/**
 * 作者：ProZoom
 * 时间：2018/3/16
 * 描述：设备相关工具类
 */
public class FileTool {

    private static final String TAG = "DeviceTool";
    private static volatile FileTool instance;// !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
    private static final Object lock = new Object();


    public FileTool() {

    }

    //单例模式，懒汉氏
    public static FileTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new FileTool();
                }
            }
        }
        return instance;
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public File getFileByPath(String filePath) {

        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean isFileExists(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists()) {
            return true;
        } else {
            return false;
        }
    }




}
