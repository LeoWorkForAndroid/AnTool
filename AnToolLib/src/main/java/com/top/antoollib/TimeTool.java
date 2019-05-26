package com.top.antoollib;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 作者：ProZoom
 * 时间：2018/3/16
 * 描述：设备相关工具类
 */
public class TimeTool {

    private static final String TAG = "TimeTool";

    /**
     * !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
     */
    private static volatile TimeTool instance;
    private static final Object lock = new Object();


    public TimeTool() {

    }


    /**
     * 单例模式，懒汉氏
     * @return
     */
    public static TimeTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new TimeTool();
                }
            }
        }
        return instance;
    }


    //////////////////////////////////////////////
    /**
     * 默认的格式化时间时的模式字符串
     */
    private  String fmtPattern = "yyyy-MM-dd HH:mm:ss";
    /**
     * 格式化时间的工具
     */
    private  SimpleDateFormat sdf = new SimpleDateFormat(fmtPattern, Locale.CHINA);
    /**
     * 被格式化的时间对象
     */
    private Date date = new Date();

    /**
     * 获取格式化后的时间的字符串
     *
     * @param timeMills 以毫秒为单位的时间
     * @return 根据模式字符串格式化后的时间
     */
    public  String getFormatted(long timeMills) {
        return getFormatted("mm:ss", timeMills);
    }

    /**
     * 获取格式化后的时间的字符串
     *
     * @param format    格式化时使用的模式字符串，例如mm:ss
     * @param timeMills 以毫秒为单位的时间
     * @return 根据模式字符串格式化后的时间
     */
    public  String getFormatted(String format, long timeMills) {
        // 应用参数中指定的模式字符串
        if (format != null && !"".equals(format)) {
            sdf.applyPattern(format);
        }
        // 设置被格式化的时间
        date.setTime(timeMills);
        // 执行格式化，并返回
        return sdf.format(date);
    }

    /**
     * 获取当月的 天数
     */
    public  int getCurrentMonthDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据日期取得星期几
     *
     * @param date
     * @return
     */
    public  String getWeekDay(Date date) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 根据日期 找到对应日期的 星期
     */
    public  int getDayOfWeekByDate(String date) {
        int dayOfweek = -1;
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E", Locale.CHINA);
            String str = formatter.format(myDate);
            switch (str) {
                case "周日":
                    dayOfweek = 0;
                    break;
                case "周一":
                    dayOfweek = 1;
                    break;
                case "周二":
                    dayOfweek = 2;
                    break;
                case "周三":
                    dayOfweek = 3;
                    break;
                case "周四":
                    dayOfweek = 4;
                    break;
                case "周五":
                    dayOfweek = 5;
                    break;
                case "周六":
                    dayOfweek = 6;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayOfweek;
    }


    /**
     * 获取当天该时间的前几天或后几天的时间
     *
     * @param today      今天时间戳，Date
     * @param anotherday int 负值:前n天;正值:后n天
     * @param pattern    时间戳格式;"yyyy-MM-dd HH:mm:ss:SSS"
     * @return
     */
    public  String getAnotherDay(Date today, int anotherday, String pattern) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar cld = Calendar.getInstance();
        cld.setTime(today);
        cld.add(Calendar.DAY_OF_MONTH, anotherday);
        Date d2 = cld.getTime();
        return sdf.format(d2);
    }


    /**
     * 获取某年某月月最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public  String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
    }

    /**
     * 获取某年某月第一天
     *
     * @param year
     * @param month
     * @return
     */
    public  String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd ").format(cal.getTime());
    }


    /**
     * 获取时间差
     *
     * @param time1 time2 需要计算的时间
     * @return
     */
    public  String getTimeDeffer(String time1, String time2) {
        String result = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            long dt1 = sdf.parse(time1).getTime();
            long dt2 = sdf.parse(time2).getTime();
            long dc = Math.abs(dt2 - dt1);
            long seconds = dc / 1000;
            //相差的天数
            long date = seconds / (24 * 60 * 60);
            //相差的小时数
            long hour = (seconds - date * 24 * 60 * 60) / (60 * 60);
            //相差的分钟数
            long minut = (seconds - date * 24 * 60 * 60 - hour * 60 * 60) / (60);
            //相差的秒数
            long second = (seconds - date * 24 * 60 * 60 - hour * 60 * 60 - minut * 60);
            return (date == 0 ? "" : (date + "天")) + (hour == 0 ? "" : (hour + "小时")) + (minut == 0 ? "" : (minut + "分")) + (second == 0 ? "" : (second + "秒"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取两段时间差
     * @param before
     * @param now
     */
    public  void getTimeDiff(String before, String now) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sdf.parse(before);
            date2 = sdf.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = date2.getTime() - date1.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒");
    }
}
