package com.top.antool;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.top.antoollib.ui.VoiceLineView;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.io.*;

public class VoiceLineActivity extends AppCompatActivity {

    private static final String TAG = "VoiceLineActivity";
    @BindView(R.id.voicLine)
    VoiceLineView voicLine;
    @BindView(R.id.btn)
    AppCompatButton btn;

    private AppCompatButton button;


    //用来播放pcm
    private AudioTrack mAudioTrack;

    private AudioRecord mAudioRecord;
    boolean isRunning = false;
    private File targetFile;

    private int audioSource;
    private int frequency;
    private int channelConfig;
    private int audioFormat;
    private int recordBufSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @PermissionSuccess(requestCode = 100)
    private void init() {
        button = findViewById(R.id.btn);
        button.setText("开始录音");
        initAudioRecord();


    }

    private void initAudioRecord() {

        targetFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecord");
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        //指定音频源
        audioSource = MediaRecorder.AudioSource.MIC;
        //指定采样率(MediaRecoder 的采样率通常是8000Hz CD的通常是44100Hz 不同的Android手机硬件将能够以不同的采样率进行采样。其中11025是一个常见的采样率)
        frequency = 44100;
        //指定捕获音频的通道数目.在AudioFormat类中指定用于此的常量
        channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
        //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        recordBufSize = AudioRecord.getMinBufferSize(frequency, channelConfig, audioFormat);
        mAudioRecord = new AudioRecord(audioSource, frequency, channelConfig, audioFormat, recordBufSize);
    }


    private void stopRecord() {
        isRunning = false;
    }

    /**
     * 获取录取的音频,并且写入文件
     */
    private void startRecord() {
        

        isRunning = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                File file = new File(targetFile, "audio.pcm");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DataOutputStream outputStream = null;
                try {
                    outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    byte[] buffer = new byte[recordBufSize];
                    //开始录音
                    mAudioRecord.startRecording();
                    int r = 0;
                    while (isRunning) {
                        int readResult = mAudioRecord.read(buffer, 0, recordBufSize);
                        for (int i = 0; i < readResult; i++) {
                            outputStream.write(buffer[i]);
                        }
                        r++;
                    }
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @OnClick({R.id.btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn:
                if (isRunning) {
                    //停止操作
                    button.setText("开始录音");
                    isRunning = false;
                    stopRecord();
                } else {
                    //开始操作
                    button.setText("录音中...");
                    isRunning = true;
                    startRecord();
                }
                break;
        }
    }

}
