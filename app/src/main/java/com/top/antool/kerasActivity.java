package com.top.antool;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class kerasActivity extends AppCompatActivity {


    private TensorFlowInferenceInterface tf;

    private String MODEL_PATH = "file:///android_asset/squeezenet.pb";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tf=new TensorFlowInferenceInterface(getAssets(),MODEL_PATH);



    }
}
