package com.top.antoollib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
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


    private int middleLineColor = Color.BLACK;
    private int voiceLineColor = Color.BLACK;
    private float middleLineHeight = 4;
    private Paint paint;
    private Paint paintVoicLine;
    /**
     * 灵敏度
     */
    private int sensibility = 4;

    private float maxVolume = 100;


    private boolean isSet = false;

    //振幅
    private float amplitude = 1;
    //音量
    private float volume = 10;
    private int fineness = 1;
    private float targetVolume = 1;


    private long speedY = 50;
    private float rectWidth = 25;
    private float rectSpace = 5;
    private float rectInitHeight = 4;
    private List<Rect> rectList;



    public VoiceLineView(Context context) {
        super(context);
    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAtts(context,attrs);
    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context,attrs);
    }

    public VoiceLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAtts(context,attrs);
    }



    private void initAtts(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.voiceView);

        voiceLineColor = typedArray.getColor(R.styleable.voiceView_voiceLine, Color.BLACK);
        maxVolume = typedArray.getFloat(R.styleable.voiceView_maxVolume, 100);
        sensibility = typedArray.getInt(R.styleable.voiceView_sensibility, 4);

        rectWidth = typedArray.getDimension(R.styleable.voiceView_rectWidth, 25);
        rectSpace = typedArray.getDimension(R.styleable.voiceView_rectSpace, 5);
        rectInitHeight = typedArray.getDimension(R.styleable.voiceView_rectInitHeight, 4);

        typedArray.recycle();
    }

    private void drawVoiceRect(Canvas canvas) {
        if (paintVoicLine == null) {
            paintVoicLine = new Paint();
            paintVoicLine.setColor(voiceLineColor);
            paintVoicLine.setAntiAlias(true);
            paintVoicLine.setStyle(Paint.Style.STROKE);
            paintVoicLine.setStrokeWidth(2);
        }
        if (rectList == null) {
            rectList = new LinkedList<>();
        }
        int totalWidth = (int) (rectSpace + rectWidth);
        if (speedY % totalWidth < 6) {
            Rect rect = new Rect((int) (-rectWidth - 10 - speedY + speedY % totalWidth),
                    (int) (getHeight() / 2 - rectInitHeight / 2 - (volume == 10 ? 0 : volume / 2)),
                    (int) (-10 - speedY + speedY % totalWidth),
                    (int) (getHeight() / 2 + rectInitHeight / 2 + (volume == 10 ? 0 : volume / 2)));
            if (rectList.size() > getWidth() / (rectSpace + rectWidth) + 2) {
                rectList.remove(0);
            }
            rectList.add(rect);
        }
        canvas.translate(speedY, 0);
        for (int i = rectList.size() - 1; i >= 0; i--) {
            canvas.drawRect(rectList.get(i), paintVoicLine);
        }
        rectChange();
    }

    private void rectChange() {
        speedY += 6;
        if (volume < targetVolume && isSet) {
            volume += getHeight() / 30;
        } else {
            isSet = false;
            if (volume <= 10) {
                volume = 10;
            } else {
                if (volume < getHeight() / 30) {
                    volume -= getHeight() / 60;
                } else {
                    volume -= getHeight() / 30;
                }
            }
        }
    }

    public void setVolume(int volume) {
        if (volume > maxVolume * sensibility / 25) {
            isSet = true;
            this.targetVolume = getHeight() * volume / 2 / maxVolume;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVoiceRect(canvas);
        postInvalidateDelayed(30);

    }
}
