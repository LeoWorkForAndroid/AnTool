package com.top.antoollib;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * 作者：李阳
 * 时间：2018/10/8
 * 描述：数学工具类
 */
public class MathTool {

    private static volatile MathTool instance;
    private static final Object lock = new Object();


    public MathTool() {

    }

    //单例模式，懒汉氏
    public static MathTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MathTool();
                }
            }
        }
        return instance;
    }


    //////////////////////////////////////////////////////////

    /*private final static double dmax = 999;//Double.MAX_VALUE;//Double类型的最大值，太大的double值，相乘会达到无穷大
    private final static double dmin = Double.MIN_VALUE;//Double类型的最小值
    private final static int n = 100;//假设求取100个doubl数的方差和标准差

    public static void main(String[] args) {
        Random random = new Random();
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {//随机生成n个double数
            x[i] = Double.valueOf(Math.floor(random.nextDouble() * (dmax - dmin)));
            System.out.println(x[i]);
        }
        //设置doubl字符串输出格式，不以科学计数法输出
        DecimalFormat df = new DecimalFormat("#,##0.00");//格式化设置
        //计算方差
        double dV = Variance(x);
        System.out.println("方差=" + df.format(dV));
        //计算标准差
        double dS = StandardDiviation(x);
        System.out.println("标准差=" + df.format(dS));
    }*/

    /**
     *  方差s^2=[(x1-x)^2 +...(xn-x)^2]/n
     */
    public  double Variance(double[] x) {
        int m = x.length;
        double sum = 0;
        //求和
        for (int i = 0; i < m; i++) {
            sum += x[i];
        }
        //求平均值
        double dAve = sum / m;
        double dVar = 0;
        //求方差
        for (int i = 0; i < m; i++) {
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return dVar / m;
    }

    /**
     * 标准差σ=sqrt(s^2)
     */

    public  double StandardDiviation(double[] x) {
        int m = x.length;
        double sum = 0;
        //求和
        for (int i = 0; i < m; i++) {
            sum += x[i];
        }
        //求平均值
        double dAve = sum / m;
        double dVar = 0;
        //求方差
        for (int i = 0; i < m; i++) {
            dVar += (x[i] - dAve) * (x[i] - dAve);
        }
        return Math.sqrt(dVar / m);
    }


     /**
     * 获取百分比
     * @param num
     * @param all
     * @return
     */
    public  String getPerCent(int num, int all) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num / (float) all * 100);
        System.out.println("num1和num2的百分比为:" + result + "%");
        return result+"%";
    }

    /**
     * 除法
     * @param num
     * @param all
     * @return
     */
    public  String div(long num, long all) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num / (float) all * 100);
        //System.out.println("num1和num2的百分比为:" + result + "%");
        return result;
    }


    /**
     * 将数字格式化为金钱符号
     * @param number
     * @param locale
     * @return
     */
    public  String  getMoneyNumber(double number,Locale locale) {

        //NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        System.out.println("Locale.CHINA: " + format.format(number));
        format = NumberFormat.getCurrencyInstance(Locale.US);
        System.out.println("Locale.US: " + format.format(number));

        return format.format(number);
    }

}
