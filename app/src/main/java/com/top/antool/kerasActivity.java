package com.top.antool;

import android.graphics.*;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.top.antoollib.tf.Classifier;
import com.top.antoollib.tf.TensorFlowImageClassifier;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class kerasActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "kerasActivity";

    @BindView(R.id.sv)
    SurfaceView sv;
    @BindView(R.id.tv)
    AppCompatTextView tv;

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    //线程池
    private Executor executor;

    // 模型相关配置
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";
    private static final String MODEL_FILE = "file:///android_asset/model/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/model/imagenet_comp_graph_label_strings.txt";
    private Classifier classifier;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tf);
        ButterKnife.bind(this);

        surfaceHolder = sv.getHolder();
        surfaceHolder.addCallback(this);


        // 初始化线程池，定时任务
        executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = new Thread(r);
                //守护线程，JAVA线程分为即实线程与守护线程，守护线程是优先级低
                thread.setDaemon(true);
                thread.setName("ThreadPool-ImageClassifier");
                return thread;
            }
        });

        // 创建 TensorFlowImageClassifier
        classifier = TensorFlowImageClassifier.create(getAssets(), MODEL_FILE, LABEL_FILE, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "---------surfaceCreated----------");
        initCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "---------surfaceChanged----------");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "---------surfaceDestroyed----------");
        releaseCamera();
    }

    public void initCamera() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.setDisplayOrientation(90);
        //获取尺寸,格式转换的时候要用到
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        final int width = previewSize.width;
        final int height = previewSize.height;
        Log.e(TAG,width + ":" + height);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                //处理data
                YuvImage yuvimage = new YuvImage(
                        data,
                        ImageFormat.NV21,
                        width,
                        height,
                        null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 80--JPG图片的质量[0-100],100最高
                yuvimage.compressToJpeg(new Rect(0, 0, width, height), 100, baos);

                byte[] rawImage = baos.toByteArray();
                //将rawImage转换成bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
                startImageClassifier(bitmap);
            }

        });
        camera.startPreview();
    }


    public void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    /**
     * 开始图片识别匹配
     *
     * @param bitmap
     */
    private void startImageClassifier(final Bitmap bitmap) {
        executor.execute(new Runnable() {
            @Override
            public void run() {


                try {
                    Log.i(TAG, Thread.currentThread().getName() + " startImageClassifier");
                    Bitmap croppedBitmap = getScaleBitmap(bitmap, INPUT_SIZE);
                    final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);
                    Log.i(TAG, "startImageClassifier results: " + results);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(String.format("results: %s", results));
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "startImageClassifier getScaleBitmap " + e.getMessage());
                }


            }
        });
    }

    /**
     * 对图片进行缩放
     *
     * @param bitmap
     * @param size
     * @return
     * @throws IOException
     */
    private static Bitmap getScaleBitmap(Bitmap bitmap, int size) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) size) / width;
        float scaleHeight = ((float) size) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap1;
    }
}
