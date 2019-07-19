package com.top.antoollib.ui;

import android.content.Context;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView {

    private static final String TAG ="CameraSurfaceView";


    private CameraManager cameraManager;


    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private float mOldDistance;

    public CameraSurfaceView(Context context) {
        super(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCamera2(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCamera2(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCamera2(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }


    private void initCamera2(Context context){
        try {
            cameraManager= (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String item:cameraIdList){
                Log.e(TAG,"-----------------------------: "+item);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }

      //  cameraManager.getCameraCharacteristics()

    }

    /**
     * 查询设备对应Camera2的支持情况.
     * INFO_SUPPORTED_HARDWARE_LEVEL
     * 硬件层面支持的Camera2功能等级, 主要分为5个等级:
     *
     * INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
     * INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED
     * INFO_SUPPORTED_HARDWARE_LEVEL_FULL
     * INFO_SUPPORTED_HARDWARE_LEVEL_3
     * INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL
     *
     * LEVEL_LEGACY: 向后兼容模式, 如果是此等级, 基本没有额外功能, HAL层大概率就是HAL1(我遇到过的都是)
     * LEVEL_LIMITED: 有最基本的功能, 还支持一些额外的高级功能, 这些高级功能是LEVEL_FULL的子集
     * LEVEL_FULL: 支持对每一帧数据进行控制,还支持高速率的图片拍摄
     * LEVEL_3: 支持YUV后处理和Raw格式图片拍摄, 还支持额外的输出流配置
     * LEVEL_EXTERNAL: API28中加入的, 应该是外接的摄像头, 功能和LIMITED类似
     * 各个等级从支持的功能多少排序为: LEGACY < LIMITED < FULL < LEVEL_3
     *
     * @param cameraCharacteristics
     * @return
     */
    private int isHardwareSupported(CameraCharacteristics cameraCharacteristics){

        Integer level = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (level==null){
            Log.e(TAG,"can not get INFO_SUPPORTED_HARDWARE_LEVEL");
            return -1;
        }

        switch (level){
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                Log.w(TAG, "hardware supported level:LEVEL_FULL");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                Log.w(TAG, "hardware supported level:LEVEL_LEGACY");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                Log.w(TAG, "hardware supported level:LEVEL_3");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                Log.w(TAG, "hardware supported level:LEVEL_LIMITED");
                break;
        }
        return level;
    }



    public void openCapera(){
        if (cameraManager!=null ){
           // cameraManager.openCamera("0");
        }
    }



}
