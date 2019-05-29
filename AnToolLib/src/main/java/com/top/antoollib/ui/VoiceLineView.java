package com.top.antoollib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.shapes.Shape;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import com.top.antoollib.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ly
 */
public class VoiceLineView extends View {


    private int voicCircleColor = Color.BLACK;
    //圆形画笔
    private Paint paintVoicCircle;
    private List<CircleInfo> circleInfoLists = new ArrayList<>();
    //圆点数量,默认50个
    private int circleNum = 30;
    //圆点半径
    private int circleRadius = 10;
    //每个圆点间距，圆心距
    private int circleCenterDistance = circleRadius * 3;
    private int circleHeight = 200;

    private int middleLineColor = Color.parseColor("#104E8B");
    private int edgeLineColor = Color.parseColor("#1C86EE");


    public VoiceLineView(Context context) {
        super(context);
    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAtts(context, attrs);
    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context, attrs);
    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAtts(context, attrs);
    }


    private void initAtts(Context context, AttributeSet attrs) {
        //获取配置参数
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.voiceView);

        typedArray.recycle();


        //初始化
        for (int i = 0; i < circleNum; i++) {
            CircleInfo circleInfo = new CircleInfo();
            circleInfo.setColor(voicCircleColor);
            circleInfo.setRadius(circleRadius);
            circleInfo.setWaist(0);
            RectF rectF = new RectF();
            rectF.top = circleHeight / 2 - circleInfo.getWaist() / 2 - circleInfo.getRadius();
            rectF.bottom = circleHeight / 2 + circleInfo.getWaist() / 2 + circleInfo.getRadius();
            rectF.left = circleInfo.getRadius() + i * circleCenterDistance;
            rectF.right = circleInfo.getRadius() + i * circleCenterDistance + 2 * circleInfo.getRadius();
            circleInfo.setRectF(rectF);
            circleInfoLists.add(circleInfo);
        }

        //初始化画笔
        if (paintVoicCircle == null) {
            paintVoicCircle = new Paint();
            paintVoicCircle.setColor(voicCircleColor);
            paintVoicCircle.setAntiAlias(true);
            paintVoicCircle.setStyle(Paint.Style.FILL);
            paintVoicCircle.setStrokeWidth(1);
        }
    }

    private void drawVoiceCircle(Canvas canvas) {

        for (int i = 0; i < circleNum; i++) {
            CircleInfo circleInfo = circleInfoLists.get(i);
            RectF rectF = circleInfoLists.get(i).getRectF();
            rectF.top = circleHeight / 2 - circleInfo.getWaist() / 2 - circleInfo.getRadius();
            rectF.bottom = circleHeight / 2 + circleInfo.getWaist() / 2 + circleInfo.getRadius();
            rectF.left = circleInfo.getRadius() + i * circleCenterDistance;
            rectF.right = circleInfo.getRadius() + i * circleCenterDistance + 2 * circleInfo.getRadius();


            Log.e("Voice", "-------------------------" + this.getRootView().getHeight());

            float x0 = circleInfo.getRadius() + i * circleCenterDistance;
            float y0 = this.getRootView().getHeight() / 2 - circleInfo.getWaist() - circleInfo.getRadius();
            float x1 = circleInfo.getRadius() + i * circleCenterDistance;
            float y1 = this.getRootView().getHeight() / 2 + circleInfo.getWaist() + circleInfo.getRadius();
            int[] colors = new int[]{edgeLineColor, middleLineColor, edgeLineColor};
            float[] positions = new float[]{0, (float) 0.5, 1};

            LinearGradient vircleShape = new LinearGradient(x0, y0, x1, y1, colors, positions, Shader.TileMode.MIRROR);

            //

            if (Math.abs(circleInfoLists.size() / 2 - i) < 4) {
                paintVoicCircle.setColor(middleLineColor);
                paintVoicCircle.setAlpha(250);

            } else if (Math.abs(circleInfoLists.size() / 2 - i) < 10) {
                paintVoicCircle.setColor(middleLineColor);
                paintVoicCircle.setAlpha(200);
            } else {
                paintVoicCircle.setColor(edgeLineColor);
                paintVoicCircle.setAlpha(100);
            }

            if (circleInfo.getWaist() != 0) {
                paintVoicCircle.setShader(vircleShape);

            }
            canvas.drawRoundRect(rectF, 10, 10, paintVoicCircle);
        }
    }


    @Deprecated
    public void setVolume(int volume) {
        for (int i = 0; i < circleInfoLists.size(); i++) {
            CircleInfo circleInfo = circleInfoLists.get(i);
            circleInfo.setWaist((int) (1 + Math.random() * (circleHeight / 2 - circleInfo.getRadius() - 1 + 1)));
        }
    }

    //设置振幅数据
    public void setAmplitude() {

    }

    //设置频率数据
    public void setFrequency() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVoiceCircle(canvas);
        postInvalidateDelayed(100);

    }

    // 圆角矩形
    private class CircleInfo {

        private float radius;            //  半径
        private int color;            //  画笔的颜色
        private int waist = 0; //圆腰长

        private RectF rectF;


        public int getWaist() {
            return waist;
        }

        public void setWaist(int waist) {
            this.waist = waist;
        }


        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public RectF getRectF() {
            return rectF;
        }

        public void setRectF(RectF rectF) {
            this.rectF = rectF;
        }
    }


}
