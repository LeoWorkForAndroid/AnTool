package com.top.antool;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import com.top.antoollib.ui.VoiceLineView;

import java.io.*;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements Runnable {

    private String TAG = "VoiceLineView";


    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;

    //指定采样率 （MediaRecoder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int mSampleRateInHz = 44100;

    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道

    //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;

    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);//计算最小缓冲区

    //创建AudioRecord。AudioRecord类实际上不会保存捕获的音频，因此需要手动创建文件并保存下载。
    private AudioRecord mAudioRecord;//创建AudioRecorder对象
    private VoiceLineView voiceLineView;
    private AppCompatButton button;
    private boolean isRecording = false;

    private File mFileRoot = null;//文件目录
    //存放的目录路径名称
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudiioRecordFile";

    private static final String mFileName = "audiorecordtest.pcm";

    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;
    Visualizer.OnDataCaptureListener dataCaptureListener;
    Visualizer visualizer = null;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private static final int REQUEST_RECORD_AUDIO = 0X01;
    private static final int REQUEST_READ_STORAGE = 0x01;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceLineView = (VoiceLineView) findViewById(R.id.voicLine);

        button = findViewById(R.id.btn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            }
        }


        mFileRoot = new File(mPathName);
        if (!mFileRoot.exists())
            mFileRoot.mkdirs();//创建文件夹

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        button.setText("Recording");
                        startRecord();
                        return true;
                    case MotionEvent.ACTION_UP:
                        button.setText("录音");
                        stopRecord();
                        return true;
                }
                return false;
            }
        });
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes);
        dataCaptureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

            }
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                //fft
                Log.i(TAG, "fft length: " + fft.length);
                for (int i = 0; i < fft.length; i += 2) {
                    float magnitude = (float) Math.hypot(fft[i], fft[i + 1]);
                    float phase = (float) Math.atan2(fft[i + 1], fft[i]);
                }
            }
        };
    }

    //开始录音
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startRecord() {

        //AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        } else {
            destroyThread();
            isRecording = true;
            if (mThread == null) {
                mThread = new Thread(this);
                mThread.start();//开启线程
            }
        }

        try {
            int mediaPlayerId = mAudioRecord.getAudioSessionId();
            if (visualizer != null) {
                visualizer.release();
            }
            visualizer = new Visualizer(mediaPlayerId);


            /**
             *可视化数据的大小： getCaptureSizeRange()[0]为最小值，getCaptureSizeRange()[1]为最大值
             *
             */
            int captureSize = Visualizer.getCaptureSizeRange()[1];
            int captureRate = Visualizer.getMaxCaptureRate() * 3 / 4;

            visualizer.setCaptureSize(captureSize);
            visualizer.setDataCaptureListener(dataCaptureListener, captureRate, true, true);
            visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
            visualizer.setEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "请检查录音权限"+e.toString());
        }
    }

    //停止录音
    public void stopRecord() {
        visualizer.setEnabled(false);
        if (visualizer != null) {
            visualizer.release();
        }
        isRecording = false;
        //停止录音，回收AudioRecord对象，释放内存
        if (mAudioRecord != null) {
            if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                mAudioRecord.stop();
            }
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mAudioRecord.release();
            }
        }
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(500);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish();
            }
        }
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish();
            }
        }
    }


    @Override
    public void run() {
        //标记为开始采集状态
        isRecording = true;
        //创建一个流，存放从AudioRecord读取的数据
        File mRecordingFile = new File(mFileRoot, mFileName);
        if (mRecordingFile.exists()) {//音频文件保存过了删除
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();//创建新文件
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "创建储存音频文件出错");
        }

        try {
            //获取到文件的数据流
            DataOutputStream mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));
            byte[] buffer = new byte[mBufferSizeInBytes];
            mAudioRecord.startRecording();//开始录音
            //getRecordingState获取当前AudioReroding是否正在采集数据的状态
            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
                for (int i = 0; i < bufferReadResult; i++) {
                    mDataOutputStream.write(buffer[i]);

                }
            }
            mDataOutputStream.close();
        } catch (Throwable t) {
            Log.e(TAG, "Recording Failed");
            stopRecord();
        }
    }
}


