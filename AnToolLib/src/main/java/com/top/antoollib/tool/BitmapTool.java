package com.top.antoollib.tool;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ProZoom
 * 时间：2018/3/16
 * 描述：设备相关工具类
 */
public class BitmapTool {

    private static final String TAG = "BitmapTool";
    private static volatile BitmapTool instance;// !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
    private static final Object lock = new Object();

    public static final double KB = 1024.0;
    public static final double MB = KB * KB;
    public static final double GB = KB * KB * KB;

    public BitmapTool() {

    }

    //单例模式，懒汉氏
    public static BitmapTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new BitmapTool();
                }
            }
        }
        return instance;
    }



    /**
     * 图片二值化
     * @param bitmap
     * @param threshold
     * @return
     */
    public  Bitmap Image2SigleColor(Bitmap bitmap, int threshold) {

        StringBuffer stringBuffer = new StringBuffer();
        int[] oldPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(oldPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[] newPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        int size = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {

                int px = oldPx[x + bitmap.getWidth() * y];
                int b = Color.blue(px);
                int g = Color.green(px);
                int r = Color.red(px);

                int light = (r + g + b) / 3;
                //二值化
                if (light > threshold) {
                    newPx[x + bitmap.getWidth() * y] = Color.argb(0xff, 0xff, 0xff, 0xff);
                } else {
                    newPx[x + bitmap.getWidth() * y] = Color.argb(0xff, 0x00, 0x00, 0x00);
                }

            }
            size++;
        }
        Log.d(TAG, "image2String: x==" + size);
        Log.d(TAG, "image2String: height==" + bitmap.getHeight());
        Log.d(TAG, "image2String: width==" + bitmap.getWidth());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(newPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return newBitmap;
    }

    /**
     * 黑白照片
     * @param bitmap
     * @return
     */
    public  Bitmap image2BlackColor(Bitmap bitmap) {
        StringBuffer stringBuffer = new StringBuffer();
        int[] oldPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(oldPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[] newPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        int size = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {

                int px = oldPx[x + bitmap.getWidth() * y];
                int b = Color.blue(px);
                int g = Color.green(px);
                int r = Color.red(px);

                int light = (r + g + b) / 3;
                //二值化
//                if (light > 0xff / 3 * 2) {
//                    newPx[x + bitmap.getWidth() * y] = Color.argb(0xff, 0xff, 0xff, 0xff);
//                } else {
//                    newPx[x + bitmap.getWidth() * y] = Color.argb(0xff, 0x00, 0x00, 0x00);
//                }

                newPx[x + bitmap.getWidth() * y] = Color.argb(0xff, light, light, light);
            }
            size++;
        }
        Log.d(TAG, "image2String: x==" + size);
        Log.d(TAG, "image2String: height==" + bitmap.getHeight());
        Log.d(TAG, "image2String: width==" + bitmap.getWidth());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(newPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return newBitmap;
    }

    /**
     * 负片效果
     * @param bitmap
     * @return
     */
    public  Bitmap image2Negative(Bitmap bitmap) {
        StringBuffer stringBuffer = new StringBuffer();
        int[] oldPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(oldPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[] newPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        int size = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {

                int px = oldPx[x + bitmap.getWidth() * y];
                int b = Color.blue(px);
                int g = Color.green(px);
                int r = Color.red(px);

                int light = (r + g + b) / 3;

                newPx[x + bitmap.getWidth() * y] = Color.argb(0xff, 0xff - r, 0xff - g, 0xff - b);
//                int newb=b+30<255?b+10:255;
//                int newg=g+30<255?g+10:255;
//                int newr=r+30<255?r+10:255;
//                newPx[x + bitmap.getWidth() * y] = Color.argb(0xff, newr, newg, newb);
            }
            size++;
        }
        Log.d(TAG, "image2String: x==" + size);
        Log.d(TAG, "image2String: height==" + bitmap.getHeight());
        Log.d(TAG, "image2String: width==" + bitmap.getWidth());
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(newPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return newBitmap;
    }

    /**
     * 把图片转成byte
     * @param bitmap
     * @return
     */
    public  byte[] image2Bytes(Bitmap bitmap) {
        int newHeight = bitmap.getHeight();
        int newWidth = bitmap.getWidth() % 8 == 0 ? bitmap.getWidth() / 8 : bitmap.getWidth() / 8 + 1;

        byte[] dataBytes = new byte[newHeight * newWidth];
        byte[] heightBytes = int2Byte2(newHeight);
        byte[] widthBytes = int2Byte2(newWidth);
        byte[] resultBytes = new byte[8 + newHeight * newWidth];


//        stringBuffer.append(strWidth);
//        stringBuffer.append(strHeight);


        int[] oldPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(oldPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int size = 0;
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < newWidth; x++) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < 8; i++) {
                    if (x * 8 + i < bitmap.getWidth()) {
                        int px = oldPx[x * 8 + i + bitmap.getWidth() * y];
                        int b = Color.blue(px);
                        int g = Color.green(px);
                        int r = Color.red(px);

                        int light = (r + g + b) / 3;
                        if (light > 0xff / 3 * 2) {
                            sb.append(1);
                        } else {
                            sb.append(0);
                        }
                    } else {
                        sb.append(0);
                    }
                }

                byte c = (byte) (Integer.parseInt(sb.reverse().toString(), 2) & 0xff);
                dataBytes[x + y * newWidth] = c;
            }
            size++;
        }
        Log.d(TAG, "image2String: x==" + size);
//        Log.d(TAG, "stringBuffer.size: x==" + stringBuffer.length());
        System.arraycopy(widthBytes, 0, resultBytes, 0, 4);
        System.arraycopy(heightBytes, 0, resultBytes, 4, 4);
        System.arraycopy(dataBytes, 0, resultBytes, 8, dataBytes.length);
        return resultBytes;
    }

    /**
     * 把字符串转成图片
     * @param resultBytes
     * @return
     */
    public  Bitmap string2Image(byte[] resultBytes) {


        byte[] heightBytes = new byte[4];
        byte[] widthBytes = new byte[4];

        System.arraycopy(resultBytes, 0, widthBytes, 0, 4);
        System.arraycopy(resultBytes, 4, heightBytes, 0, 4);

        int width = getInt(widthBytes);
        int height = byteArrayToInt(heightBytes);
        int realWidth = width * 8;

        byte[] dataBytes = new byte[height * width];
        System.arraycopy(resultBytes, 8, dataBytes, 0, height * width);


        int[] newPx = new int[realWidth * height];
        Log.d(TAG, "string2Image: width===" + width);
        Log.d(TAG, "string2Image: height===" + height);
        int size = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte c = dataBytes[x + y * width];

                for (int i = 0; i < 8; i++) {
                    int index = x * 8 + i + realWidth * y;
//                    size=index;
//                    int px=c>>i|0x01;
                    int px = c >> i & 0x01;

                    if (px == 1) {
//                        Log.d(TAG, "string2Image: px=="+px);
                        newPx[index] = Color.argb(0xff, 0xff, 0xff, 0xff);
                    } else {
                        newPx[index] = Color.argb(0xff, 0x00, 0x00, 0x00);
                    }

                }
//                int index=x+realWidth*y;
//                size=index;
//                newPx[index]=Color.argb(0xff, 0x00, 0x00, 0x00);
            }
        }
        Log.d(TAG, "string2Image: size==" + (size + 1));
        Log.d(TAG, "string2Image: needSize==" + realWidth * height);
//        for (int i=0;i<newPx.length;i++){
//            newPx[i]=Color.argb(0xff, 0x00, 0x00, 0x00);
//        }
//        for (char c:string.toCharArray()){
//            if (c=='\n'){
//
//            }else {
//               String str= Integer.toBinaryString(c);
//                Log.d(TAG, "string2Image: str==="+str);
//               for (char px:str.toCharArray()){
//                   if (px==1){
//                       integerList.add( Color.argb(0xff, 0xff, 0xff, 0xff));
//                   }else {
//                       integerList.add(Color.argb(0xff, 0x00, 0x00, 0x00));
//                   }
//               }
//            }
//        }
//
//
//
//        Log.d(TAG, "string2Image: integerList==="+integerList.size());
//        Log.d(TAG, "string2Image: width*height==="+width*height);

//
//        for (int i = 0; i < integerList.size(); i++) {
//            newPx[i] = integerList.get(i);
//        }
//        Log.d(TAG, "string2Image: string=="+string);
        Bitmap newBitmap = Bitmap.createBitmap(realWidth, height, Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(newPx, 0, realWidth, 0, 0, realWidth, height);
        return newBitmap;
    }

    /**
 * byte[]转int
 * @param bytes
 * @return
 */
private  int byteArrayToInt(byte[] bytes) {
    int value = 0;
    // 由高位到低位
    for (int i = 0; i < 4; i++) {
        int shift = (4 - 1 - i) * 8;
        value += (bytes[i] & 0x000000FF) << shift;// 往高位游
    }
    return value;
}


    /**
     * 从图片中取出二维码
     * @param mybitmap
     * @return
     */
    public  Bitmap ImageFindQR(Bitmap mybitmap) {
        Bitmap bitmap= Image2SigleColor(mybitmap,70);
        List<Point> points = new ArrayList<>(); //用来保存特征点
        List<Markpoint> findPoints=new ArrayList<>();
        int pointSize = 0;
        int qrLeftSize = bitmap.getWidth() / 50 < bitmap.getHeight() / 50 ? bitmap.getWidth() / 50 : bitmap.getHeight() / 50; //二维码左上角宽度阈值
        int qrLegthSize = 3;

        int black = Color.argb(0xff, 0x00, 0x00, 0x00);
        int red = Color.argb(0xff, 0xff, 0x00, 0x00);
        int transparent = Color.argb(0x00, 0x00, 0x00, 0x00);

        int[] oldPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(oldPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[] newPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        for (int i = 0; i < newPx.length; i++) {
            newPx[i] = transparent;
        }
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {

                int px = oldPx[x + bitmap.getWidth() * y];

                if (px == black) {
                    int x1Size = 0;
                    int y1Size = 0;

                    int x2Size = 0;
                    int y2Size = 0;

                    int pointx1 = x;
                    int pointy1 = y;

                    if (bitmap.getWidth() - x <= qrLeftSize || bitmap.getHeight() - y <= qrLeftSize) {
                        continue;
                    }

                    if (oldPx[x + qrLeftSize + bitmap.getWidth() * y] != black ||
                            oldPx[x + bitmap.getWidth() * (y + qrLeftSize)] != black) {
                        continue;
                    }

                    for (int x1 = x; x1 < bitmap.getWidth(); x1++) { //找到二维码左标志的上横
                        int tmpPx = oldPx[x1 + bitmap.getWidth() * y];
                        if (tmpPx == black) {
                            x1Size++;
                            pointx1 = x1;
                        } else {
                            break;
                        }
                    }
                    if (x1Size > qrLeftSize) { //二维码左标志的横大于阈值
                        for (int y1 = y; y1 < bitmap.getHeight(); y1++) { //找到二维码左标志的左竖
                            int tmpPx = oldPx[x + bitmap.getWidth() * y1];
                            if (tmpPx == black) {
                                y1Size++;
                                pointy1 = y1;
                            } else {
                                break;
                            }
                        }
                        if (Math.abs(x1Size - y1Size) < qrLegthSize) {   //如果左右宽度差不多
                            // Log.d(TAG, "ImageFindQR: ("+x+","+y+")");

                            int dSize = x1Size < y1Size ? x1Size : y1Size;

                            for (int x1 = x; x1 < bitmap.getWidth(); x1++) { //找到二维码左标志的下横
                                int tmpPx = oldPx[x1 + bitmap.getWidth() * (pointy1)];
                                if (tmpPx == black) {
                                    x2Size++;
                                } else {
                                    break;
                                }
                            }
                            if (x2Size > qrLeftSize) { //二维码左标志的横大于阈值
                                for (int y1 = y; y1 < bitmap.getHeight(); y1++) { //找到二维码左标志的左竖
                                    int tmpPx = oldPx[pointx1 + bitmap.getWidth() * y1];
                                    if (tmpPx == black) {
                                        y2Size++;
                                    } else {
                                        break;
                                    }
                                }
//                                Log.d(TAG, "ImageFindQR: x1Size"+x1Size);
//                                Log.d(TAG, "ImageFindQR: x2Size"+x2Size);
//                                Log.d(TAG, "ImageFindQR: y1Size"+y1Size);
//                                //      Log.d(TAG, "ImageFindQR: dSize"+dSize);
//                                Log.d(TAG, "ImageFindQR: y2Size"+y2Size);
                                if (
                                        Math.abs(x1Size - y1Size) < qrLegthSize &&
                                                Math.abs(x1Size - y2Size) < qrLegthSize &&
                                                Math.abs(x2Size - y1Size) < qrLegthSize &&
                                                Math.abs(x2Size - y2Size) < qrLegthSize) {   //如果左右宽度差不多
                                    for (int i = 0; i < dSize; i++) {
                                        for (int j = 0; j < dSize; j++) {
                                            newPx[x + i + (y + j) * bitmap.getWidth()] = red;
                                        }
                                    }
                                    pointSize++;
                                }
                            }

//                            for (int i = 0; i < dSize; i++) {
//                                for (int j = 0; j < dSize; j++) {
//                                    newPx[x + i + (y + j) * bitmap.getWidth()] = Color.argb(0xff, 0xff, 0x00, 0x00);
//                                }
//                            }
                        }
                    }


                }


            }
        }
        for (int y = 1; y < bitmap.getHeight()-1; y++) {
            for (int x = 1; x < bitmap.getWidth()-1; x++) {
                int px = newPx[x + bitmap.getWidth() * y];
                int topPx = newPx[x + bitmap.getWidth() * (y - 1)];
                int leftPx = newPx[x - 1 + bitmap.getWidth()*y];
//                int bottonPx=newPx[x + bitmap.getWidth() * (y +1)];
//                int rightPx = newPx[x + 1 + bitmap.getWidth()*y];
                if (px == red &&
                        topPx == transparent &&
                        leftPx == transparent) {
                    points.add(new Point(x, y));
                }
            }
        }
        for (int i=0;i<points.size();i++){
            for (int j=i+1;j<points.size();j++){
                if (Math.abs(points.get(i).y-points.get(j).y)<qrLegthSize){
                    for (int k=0;k<points.size();k++){
                        if (k!=i&&k!=j){
                            if ((Math.abs(points.get(i).x-points.get(k).x)<qrLegthSize)&&
                                    ( (Math.abs(points.get(k).y-points.get(i).y)-Math.abs(points.get(i).x-points.get(j).x))<qrLegthSize)){
                                Markpoint markpoint=new Markpoint(points.get(i),points.get(j),points.get(k));
                                findPoints.add(markpoint);

                                Log.d(TAG, "ImageFindQR: i"+i);
                                Log.d(TAG, "ImageFindQR: j"+j);
                                Log.d(TAG, "ImageFindQR: k"+k);

                                Log.d(TAG, "ImageFindQR: pi"+points.get(i).toString());
                                Log.d(TAG, "ImageFindQR: pj"+points.get(j).toString());
                                Log.d(TAG, "ImageFindQR: pk"+points.get(k).toString());
                                for (int a=0;a<10;a++){
                                    for (int b=0;b<10;b++){
                                        newPx[points.get(i).x+a+(points.get(i).y+b)*bitmap.getWidth()]=Color.argb(0xff,00,00,0xff);
                                    }
                                }
                                for (int a=0;a<10;a++){
                                    for (int b=0;b<10;b++){
                                        newPx[points.get(j).x+a+(points.get(j).y+b)*bitmap.getWidth()]=Color.argb(0xff,00,00,0xff);
                                    }
                                }
                                for (int a=0;a<10;a++){
                                    for (int b=0;b<10;b++){
                                        newPx[points.get(k).x+a+(points.get(k).y+b)*bitmap.getWidth()]=Color.argb(0xff,00,00,0xff);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Point leftPoint=findPoints.get(0).p1;
        Point rightPoint=findPoints.get(0).p1;
        Point topPoint=findPoints.get(0).p1;
        Point bottomPoint=findPoints.get(0).p1;
        int maxWidth=0;
        int maxHeight=0;

        for (Markpoint m:findPoints){
            leftPoint=leftPoint.x<m.p1.x?leftPoint:m.p1;
            rightPoint=rightPoint.x>m.p2.x?rightPoint:m.p2;
            topPoint=topPoint.y<m.p1.y?topPoint:m.p1;
            bottomPoint=bottomPoint.y>m.p3.y?bottomPoint:m.p3;

        }
//        for (int x=rightPoint.x;x<bitmap.getWidth();x++){
//
//            int tmpPx = oldPx[x + bitmap.getWidth() * rightPoint.y];
//            if (tmpPx == red) {
//                rightPoint.x=x;
//            } else {
//                break;
//            }
//        }
//
//        for (int y=bottomPoint.y;y<bitmap.getHeight();y++){
//            int tmpPx = oldPx[bottomPoint.x + bitmap.getWidth() * y];
//            if (tmpPx == red) {
//                bottomPoint.y=y;
//            } else {
//                break;
//            }
//        }
        Log.d(TAG, "ImageFindQR: height==" + bitmap.getHeight());
        Log.d(TAG, "ImageFindQR: width==" + bitmap.getWidth());
        Log.d(TAG, "ImageFindQR: markpoints.size==" + points.size());
        Log.d(TAG, "ImageFindQR: pointSize==" + pointSize);
        Log.d(TAG, "ImageFindQR: findPoints==" + findPoints.size());


        int finalLeft=leftPoint.x-10<0?0:leftPoint.x-10;
        int finalTop=topPoint.y-10<0?0:topPoint.y-10;
        int finalRight=rightPoint.x+100>bitmap.getWidth()?bitmap.getWidth():rightPoint.x+100;
        int finalBottom=bottomPoint.y+100>bitmap.getHeight()?bitmap.getHeight():bottomPoint.y+100;
        int finalWidth=finalRight-finalLeft;
        int finalHeight=finalBottom-finalTop;



        Log.d(TAG, "ImageFindQR: finalLeft="+finalLeft);
        Log.d(TAG, "ImageFindQR: finalTop="+finalTop);
        Log.d(TAG, "ImageFindQR: finalRight="+finalRight);
        Log.d(TAG, "ImageFindQR: finalBottom="+finalBottom);
        Log.d(TAG, "ImageFindQR: finalWidth="+finalWidth);
        Log.d(TAG, "ImageFindQR: finalHeight="+finalHeight);


        Log.d(TAG, "ImageFindQR: 准备赋值");

        int[] fatherPx = new int[bitmap.getHeight() * bitmap.getWidth()];
        mybitmap.getPixels(fatherPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int[] finalPx=new int[finalWidth*finalHeight];
        for (int y=finalTop;y<finalBottom;y++){
            for (int x=finalLeft;x<finalRight;x++){
                int realx=x-finalLeft;
                int realy=y-finalTop;
                int finalIndex=realx+realy*finalWidth;
                int oldIndex=x+y*bitmap.getWidth();

//                Log.d(TAG, "ImageFindQR: realx=" + realx);
//                Log.d(TAG, "ImageFindQR: realy=" + realy);
//
//                Log.d(TAG, "ImageFindQR: finalIndex=" + finalIndex);
//                Log.d(TAG, "ImageFindQR: oldIndex=" + oldIndex);
                finalPx[finalIndex]=fatherPx[oldIndex];

            }
        }
        Log.d(TAG, "ImageFindQR: 赋值完成");


        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(newPx, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        Bitmap finalBitmap = Bitmap.createBitmap(finalWidth,finalHeight, Bitmap.Config.ARGB_8888);
        finalBitmap.setPixels(finalPx, 0, finalWidth, 0, 0, finalWidth, finalHeight);
        Log.d(TAG, "ImageFindQR: 创建图片完成");
        return finalBitmap;
    }

    static class Markpoint {
        private Point p1, p2, p3; //左上，右上，左下，右下

        public Markpoint(Point p1, Point p2, Point p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }


    }

    public  class Point {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }


    /////////////////////////////////////




    /**
     * convert Bitmap to byte array
     * Bitmap转byte数组
     *
     * @param b
     * @return
     */
    public byte[] bitmapToByte(Bitmap b) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    /**
     * convert byte array to Bitmap
     * byte数组转bitmp
     *
     * @param b
     * @return
     */
    public Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * 把bitmap转换成Base64编码String
     *
     * @param bitmap
     * @return
     */
    public String bitmapToString(Bitmap bitmap) {
        return Base64.encodeToString(bitmapToByte(bitmap), Base64.DEFAULT);
    }

    /**
     * convert Drawable to Bitmap
     *
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * convert Bitmap to Drawable
     *
     * @param bitmap
     * @return
     */
    public Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    /**
     * scale image
     *
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     *
     * @param src
     * @param scaleWidth
     * @param scaleHeight
     * @return
     */
    public Bitmap scaleImage(Bitmap src, float scaleWidth, float scaleHeight) {
        if (src == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /**
     * 圆bitmap
     *
     * @param bitmap
     * @return
     */
    public Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 生成bitmap缩略图
     *
     * @param bitmap
     * @param needRecycle 是否释放bitmap原图
     * @param newHeight   目标宽度
     * @param newWidth    目标高度
     * @return
     */
    public Bitmap createBitmapThumbnail(Bitmap bitmap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (needRecycle)
            bitmap.recycle();
        return newBitMap;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap
     * @param target
     */
    public void saveBitmap(Bitmap bitmap, File target) {
        if (target.exists()) {
            target.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap
     * @param quality 保存质量 0..100
     * @param target
     */
    public void saveBitmap(Bitmap bitmap, int quality, File target) {
        if (target.exists()) {
            target.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩bitmp到目标大小（质量压缩）
     *
     * @param bitmap
     * @param needRecycle
     * @param maxSize
     * @return
     */
    public Bitmap compressBitmap(Bitmap bitmap, boolean needRecycle, long maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length > maxSize) {
            baos.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bm = BitmapFactory.decodeStream(isBm, null, null);
        if (needRecycle) {
            bitmap.recycle();
        }
        bitmap = bm;
        return bitmap;
    }

    /**
     * 等比压缩（宽高等比缩放）
     *
     * @param bitmap
     * @param needRecycle
     * @param targetWidth
     * @param targeHeight
     * @return
     */
    public Bitmap compressBitmap(Bitmap bitmap, boolean needRecycle, int targetWidth, int targeHeight) {
        float sourceWidth = bitmap.getWidth();
        float sourceHeight = bitmap.getHeight();

        float scaleWidth = targetWidth / sourceWidth;
        float scaleHeight = targeHeight / sourceHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight); //长和宽放大缩小的比例
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (needRecycle) {
            bitmap.recycle();
        }
        bitmap = bm;
        return bitmap;
    }

    public Bitmap compressBitmap(String imageFile, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        return compress(imageFile, null, false, qualityCompress, maxSize, targetWidth, targeHeight);
    }

    private Bitmap compress(String imageFile, String targetFile, boolean isSave, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options); //加载图片信息
        int sourceWidth = options.outWidth;
        int sourceHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        //计算inSampleSize
        int inSampleSize = 1;
        //先根据宽度进行缩小
        while (sourceWidth / inSampleSize > targetWidth) {
            inSampleSize++;
        }
        //然后根据高度进行缩小
        while (sourceHeight / inSampleSize > targeHeight) {
            inSampleSize++;
        }

        if (inSampleSize <= 0) {
            inSampleSize = 1;
        }
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile, options);//加载真正bitmap

        bitmap = compressBitmap(bitmap, false, targetWidth, targeHeight); //等比缩放
        if (qualityCompress) {
            bitmap = compressBitmap(bitmap, true, maxSize); //压缩质量
        }

        if (isSave) {
            String savePath = imageFile;
            if ((targetFile != null)) {
                savePath = targetFile;
            }

            saveBitmap(bitmap, new File(savePath));//保存图片
        }

        return bitmap;
    }

    /**
     * 压缩某张图片(执行步骤sampleSize压缩->等比压缩->质量压缩)
     *
     * @param imageFile
     * @param targetFile      保存目标，为空表示源地址保存
     * @param qualityCompress 是否做质量压缩
     * @param maxSize         目标图片大小
     * @param targetWidth
     * @param targeHeight
     */
    public void compressImage(String imageFile, String targetFile, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        Bitmap bitmap = compress(imageFile, targetFile, true, qualityCompress, maxSize, targetWidth, targeHeight);
        bitmap.recycle();
    }

    public void compressImage(String imageFile, boolean qualityCompress, long maxSize, int targetWidth, int targeHeight) {
        compressImage(imageFile, null, qualityCompress, maxSize, targetWidth, targeHeight);
    }

    /**
     * 图片缩放-尺寸缩放
     *
     * @param imageFile
     * @param targetWidth
     * @param targeHeight
     */
    public void compressImage(String imageFile, int targetWidth, int targeHeight) {
        compressImage(imageFile, null, false, 0L, targetWidth, targeHeight);
    }

    /**
     * 图片缩放-尺寸缩放
     *
     * @param imageFile
     * @param targetWidth
     * @param targeHeight
     * @return
     */
    public Bitmap compressBitmap(String imageFile, int targetWidth, int targeHeight) {
        return compressBitmap(imageFile, false, 0L, targetWidth, targeHeight);
    }

    /**
     * 图片缩放-尺寸缩放
     *
     * @param imageFile
     * @param scale     图片缩小倍速
     */
    public void compressImageSmall(String imageFile, int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        int targetWidth = options.outWidth / scale;
        int targeHeight = options.outHeight / scale;
        compressImage(imageFile, targetWidth, targeHeight);
    }

    /**
     * 图片缩放-尺寸缩放
     *
     * @param imageFile
     * @param scale     图片缩小倍速
     * @return
     */
    public Bitmap compressBitmapSmall(String imageFile, int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        int targetWidth = options.outWidth / scale;
        int targeHeight = options.outHeight / scale;
        return compressBitmap(imageFile, targetWidth, targeHeight);
    }

    /**
     * 图片缩放-尺寸缩放
     *
     * @param imageFile
     * @param scale     图片放大倍速
     */
    public void compressImageBig(String imageFile, int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        int targetWidth = options.outWidth * scale;
        int targeHeight = options.outHeight * scale;
        compressImage(imageFile, targetWidth, targeHeight);

    }

    /**
     * 图片缩放-尺寸缩放
     *
     * @param imageFile
     * @param scale     图片放大倍速
     * @return
     */
    public Bitmap compressBitmapBig(String imageFile, int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        int targetWidth = options.outWidth * scale;
        int targeHeight = options.outHeight * scale;
        return compressBitmap(imageFile, targetWidth, targeHeight);
    }

    /**
     * 质量压缩图片
     *
     * @param imageFile
     * @param targetFile
     * @param qualityCompress
     * @param maxSize
     */
    public void compressImage(String imageFile, String targetFile, boolean qualityCompress, long maxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        int targetWidth = options.outWidth / 2;
        int targeHeight = options.outHeight / 2;
        compressImage(imageFile, targetFile, qualityCompress, maxSize, targetWidth, targeHeight);
    }

    /**
     * 质量压缩图片
     *
     * @param imageFile
     * @param qualityCompress
     * @param maxSize
     */
    public void compressImage(String imageFile, boolean qualityCompress, long maxSize) {
        compressImage(imageFile, null, qualityCompress, maxSize);
    }

    /**
     * 质量压缩图片
     *
     * @param imageFile
     * @param qualityCompress
     * @param maxSize
     * @return
     */
    public Bitmap compressBitmap(String imageFile, boolean qualityCompress, long maxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);
        int targetWidth = options.outWidth / 2;
        int targeHeight = options.outHeight / 2;
        return compressBitmap(imageFile, qualityCompress, maxSize, targetWidth, targeHeight);
    }

    /**
     * 质量压缩图片-压缩在maxSize以内
     *
     * @param imageFile
     * @param maxSize
     */
    public void compressImage(String imageFile, long maxSize) {
        compressImage(imageFile, true, maxSize);
    }

    /**
     * 质量压缩图片-压缩在maxSize以内
     *
     * @param imageFile
     * @param maxSize
     * @return
     */
    public Bitmap compressBimap(String imageFile, long maxSize) {
        return compressBitmap(imageFile, true, maxSize);
    }

    /**
     * 质量压缩图片-压缩在1M以内
     *
     * @param imageFile
     */
    public void compressImage(String imageFile) {
        compressImage(imageFile, true, (long) (1 * MB));
    }

    /**
     * 质量压缩图片-压缩在1M以内
     *
     * @param imageFile
     * @return
     */
    public Bitmap compressBitmap(String imageFile) {
        return compressBitmap(imageFile, true, (long) (1 * MB));
    }

    /**
     * 旋转bitmap
     *
     * @param bitmap
     * @param degress     旋转角度
     * @param needRecycle
     * @return
     */
    public Bitmap rotateBitmap(Bitmap bitmap, int degress, boolean needRecycle) {
        Matrix m = new Matrix();
        m.postRotate(degress);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        if (needRecycle) {
            bitmap.recycle();
        }
        return bm;
    }

    /**
     * 根据path, 获取图片旋转角度
     *
     * @param path
     * @return
     */
    public final int getDegress(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    public File writeToFile(byte[] data, String status) {
        String dir = rootPath + File.separator + "face_image";
        File dirPath = new File(dir);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }

        String filePath = dir + "/" + status + "_" + ".bmp";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 压缩图片（质量压缩）
     *
     * @param bitmap
     */
    public File compressImage(Bitmap bitmap, String status) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }
        String dir = rootPath + File.separator + "face_image";
        File dirPath = new File(dir);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }

        String filePath = dir + "/" + status + "_" + ".bmp";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        recycleBitmap(bitmap);
        return file;
    }

    /**
     * bitmap转换为byte[]并且压缩
     *
     * @param bm
     * @param kb
     * @return
     */
    public byte[] bitmap2Bytes(Bitmap bm, int kb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > kb) {  //循环判断如果压缩后图片是否大于kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        return baos.toByteArray();
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }


    /**
     * RGB_565方式读取资源到Bitmap
     *
     * @param context 全局context
     * @param resId   资源id
     * @return bitmap
     */
    public Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * RGB_565方式读取资源到Bitmap
     *
     * @param path 文件图片路径
     * @return bitmap
     */
    public Bitmap readBitMap(String path) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return BitmapFactory.decodeFile(path, opt);
    }

    /**
     * Bitmap 转 byte[]
     *
     * @param bitmap 待转bitmap
     * @return 成功-byte[] 失败-null
     */
    public byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        if (bitmap != null && !bitmap.isRecycled()) {
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                if (byteArrayOutputStream.toByteArray() == null) {
                    Log.i(TAG, "bitmap2Bytes byteArrayOutputStream toByteArray=null");
                }
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                Log.i(TAG, "bitmap2Bytes exception", e);

            } finally {
                if (byteArrayOutputStream != null) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException var14) {
                        ;
                    }
                }
            }

            return null;
        } else {
            Log.i(TAG, "bitmap2Bytes bitmap == null or bitmap.isRecycled()");
            return null;
        }
    }

    /**
     * 压缩图片到指定byte大小 (在保证质量的情况下尽可能压缩 不保证压缩到指定字节)
     *
     * @param datas     图片byte格式
     * @param byteCount 指定压缩到字节数
     * @return 压缩后的byte[] (不保证压缩到指定字节)
     */
    public byte[] compressBitmap(byte[] datas, int byteCount) {
        boolean isFinish = false;
        if (datas != null && datas.length > byteCount) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap tmpBitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
            int times = 1;
            double percentage = 1.0D;

            while (!isFinish && times <= 10) {
                percentage = Math.pow(0.8D, (double) times);
                int compress_datas = (int) (100.0D * percentage);
                tmpBitmap.compress(Bitmap.CompressFormat.JPEG, compress_datas, outputStream);
                if (outputStream != null && outputStream.size() < byteCount) {
                    isFinish = true;
                } else {
                    outputStream.reset();
                    ++times;
                }
            }

            if (outputStream != null) {
                byte[] outputStreamByte = outputStream.toByteArray();
                if (!tmpBitmap.isRecycled()) {
                    tmpBitmap.recycle();
                }

                if (outputStreamByte.length > byteCount) {
                    Log.i(TAG, "compressBitmap cannot compress to " + byteCount + ", after compress size=" + outputStreamByte.length);
                }

                return outputStreamByte;
            }
        }

        return datas;
    }



    private   byte[] int2Byte2(int len) {
        byte[] data = new byte[2];
        data[0] = (byte) (len >> 8);
        data[1] = (byte) (len >> 0);
        return data;
    }

     private   int getInt(byte[] data) {
        int temp = 0;
        if (data == null) {
            return 0;
        }
        int length = data.length;
        for (int i = 0; i < length; i++) {
            temp |= (data[i] & 0xff);
            if (i != length - 1) {
                temp <<= 8;
            }
        }
        return temp;
    }


}
