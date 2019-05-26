package com.top.antoollib;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者：李阳
 * 时间：2018/5/24
 * 描述：图片缓存、字段缓存等
 */
public class CacheTool {

    private static final String TAG = "CacheTool";

    private static volatile CacheTool instance;// !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
    private static final Object lock = new Object();

    private NetCache mNetCache;
    private LocalCache mLocalCache;
    private MemoryCache mMemoryCache;

    public CacheTool() {
        mMemoryCache = new MemoryCache();
        mLocalCache = new LocalCache();
        mNetCache = new NetCache(mLocalCache, mMemoryCache);
    }

    //单例模式，懒汉氏
    public static CacheTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new CacheTool();
                }
            }
        }
        return instance;
    }

    /////Sp

    /**
     * 更改SharePreference默认路径
     * @param path
     */
    public void changeSPPath(String path) {

        try {
            Field field;
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // 获取mBase变量
            Object obj = field.get(this);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            // 创建自定义路径
            File file = new File(path);
            //File file = new File(android.os.Environment.getExternalStorageDirectory().getPath() + "/MyVisit/SharedPreference/");
            // 修改mPreferencesDir变量的值
            field.set(obj, file);

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void spPutString(Context context, String fileName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();//提交数据
    }

    public String spGetString(Context context, String fileName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);

        return sharedPreferences.getString(key, value);
    }

    public void spPutBoolean(Context context, String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();//提交数据
    }

    public boolean spGetBoolean(Context context, String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(key, value);
    }


    /*
    * 描述：缓存数据
    * @param [obj, path]
    * @return void
    */
    public void cacheSave(Object obj, String path) {
        try {
            File f = new File(path);
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            Log.i(TAG, "save: " + e.toString());
        }
    }

    /*
    * 描述：读取缓存的数据
    * @param [path]
    * @return java.lang.Object
    */
    public Object cacheLoad(String path) {
        Object obj = null;
        File file = new File(path);
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    obj = ois.readObject();
                } catch (ClassNotFoundException e) {
                }
                ois.close();
            }
        } catch (IOException e) {
            Log.i(TAG, "save: " + e.toString());
        }
        return obj;
    }

    private char[] getChar(int position) {
        String str = String.valueOf(position);
        if (str.length() == 1) {
            str = "0" + str;
        }
        char[] c = {str.charAt(0), str.charAt(1)};
        return c;
    }


    /////////////////////////////////////////////////

    /**
     * 三级图片缓存
     *
     * @param ivPic
     * @param defaultImg
     * @param url
     */
    public void disPlay(ImageView ivPic, int defaultImg, String url) {

        ivPic.setImageResource(defaultImg);

        Bitmap bitmap;
        //内存缓存
        bitmap = mMemoryCache.getBitmapFromMemory(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            Log.i(TAG, "从内存获取图片啦.....");
            return;
        }

        //本地缓存
        bitmap = mLocalCache.getBitmapFromLocal(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            System.out.println("");
            Log.i(TAG, "从本地获取图片啦.....");
            mMemoryCache.setBitmapToMemory(url, bitmap);
            return;
        }
        //网络缓存
        mNetCache.getBitmapFromNet(ivPic, url);
    }


    /**
     * 三级缓存之网络缓存
     */
    public class NetCache {

        private LocalCache mLocalCacheUtils;
        private MemoryCache mMemoryCacheUtils;

        public NetCache(LocalCache localCacheUtils, MemoryCache memoryCacheUtils) {
            mLocalCacheUtils = localCacheUtils;
            mMemoryCacheUtils = memoryCacheUtils;
        }

        /**
         * 从网络下载图片
         *
         * @param ivPic 显示图片的imageview
         * @param url   下载图片的网络地址
         */
        public void getBitmapFromNet(ImageView ivPic, String url) {
            new BitmapTask().execute(ivPic, url);//启动AsyncTask

        }

        /**
         * AsyncTask就是对handler和线程池的封装
         * 第一个泛型:参数类型
         * 第二个泛型:更新进度的泛型
         * 第三个泛型:onPostExecute的返回结果
         */
        class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

            private ImageView ivPic;
            private String url;

            /**
             * 后台耗时操作,存在于子线程中
             *
             * @param params
             * @return
             */
            @Override
            protected Bitmap doInBackground(Object[] params) {
                ivPic = (ImageView) params[0];
                url = (String) params[1];

                return downLoadBitmap(url);
            }

            /**
             * 更新进度,在主线程中
             *
             * @param values
             */
            @Override
            protected void onProgressUpdate(Void[] values) {
                super.onProgressUpdate(values);
            }

            /**
             * 耗时方法结束后执行该方法,主线程中
             *
             * @param result
             */
            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    ivPic.setImageBitmap(result);
                    System.out.println("从网络缓存图片啦.....");

                    //从网络获取图片后,保存至本地缓存
                    mLocalCacheUtils.setBitmapToLocal(url, result);
                    //保存至内存中
                    mMemoryCacheUtils.setBitmapToMemory(url, result);

                }
            }
        }

        /**
         * 网络下载图片
         *
         * @param url
         * @return
         */
        private Bitmap downLoadBitmap(String url) {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    //图片压缩
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;//宽高压缩为原来的1/2
                    options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                    Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }

            return null;
        }
    }


    /**
     * 三级缓存之本地缓存
     */
    public class LocalCache {


        private final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WerbNews";

        /**
         * 从本地读取图片
         *
         * @param url
         */
        public Bitmap getBitmapFromLocal(String url) {
            String fileName = null;//把图片的url当做文件名,并进行MD5加密
            try {
                fileName = AnTools.Encryption().md5(url);
                File file = new File(CACHE_PATH, fileName);

                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 从网络获取图片后,保存至本地缓存
         *
         * @param url
         * @param bitmap
         */
        public void setBitmapToLocal(String url, Bitmap bitmap) {
            try {
                String fileName = AnTools.Encryption().md5(url);//把图片的url当做文件名,并进行MD5加密
                File file = new File(CACHE_PATH, fileName);

                //通过得到文件的父文件,判断父文件是否存在
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }

                //把图片保存至本地
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 三级缓存之内存缓存
     */
    public class MemoryCache {

        // private HashMap<String,Bitmap> mMemoryCache=new HashMap<>();//1.因为强引用,容易造成内存溢出，所以考虑使用下面弱引用的方法
        // private HashMap<String, SoftReference<Bitmap>> mMemoryCache = new HashMap<>();//2.因为在Android2.3+后,系统会优先考虑回收弱引用对象,官方提出使用LruCache
        private LruCache<String, Bitmap> mMemoryCache;

        public MemoryCache() {
            long maxMemory = Runtime.getRuntime().maxMemory() / 8;//得到手机最大允许内存的1/8,即超过指定内存,则开始回收
            //需要传入允许的内存最大值,虚拟机默认内存16M,真机不一定相同
            mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
                //用于计算每个条目的大小
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    int byteCount = value.getByteCount();
                    return byteCount;
                }
            };

        }

        /**
         * 从内存中读图片
         *
         * @param url
         */
        public Bitmap getBitmapFromMemory(String url) {
            //Bitmap bitmap = mMemoryCache.get(url);//1.强引用方法
            /*2.弱引用方法
            SoftReference<Bitmap> bitmapSoftReference = mMemoryCache.get(url);
            if (bitmapSoftReference != null) {
                Bitmap bitmap = bitmapSoftReference.get();
                return bitmap;
            }
            */
            Bitmap bitmap = mMemoryCache.get(url);
            return bitmap;

        }

        /**
         * 往内存中写图片
         *
         * @param url
         * @param bitmap
         */
        public void setBitmapToMemory(String url, Bitmap bitmap) {
            //mMemoryCache.put(url, bitmap);//1.强引用方法
            /*2.弱引用方法
            mMemoryCache.put(url, new SoftReference<>(bitmap));
            */
            mMemoryCache.put(url, bitmap);
        }
    }


    ////////


}
