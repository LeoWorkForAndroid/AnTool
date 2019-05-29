package com.top.antoollib;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class AudioTool {

    private static final String TAG = "AppTool";
    /**
     * !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
     */
    private static volatile AudioTool instance;
    private static final Object lock = new Object();


    public AudioTool() {}

    /**
     * 单例模式，懒汉氏
     *
     * @return
     */
    public static AudioTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AudioTool();
                }
            }
        }
        return instance;
    }


    public void pcmToMp4(){

        //PCM(Pulse Code Modulation)，脉冲编码调制。人耳听到的是模拟信号，PCM是把声音从模拟信号转化为数字信号的技术。
        // 原理是用一个固定的频率对模拟信号进行采样，采样后的信号在波形上看就像一串连续的幅值不一的脉冲(脉搏似的短暂起伏的电冲击)，
        // 把这些脉冲的幅值按一定精度进行量化，这些量化后的数值被连续的输出、传输、处理或记录到存储介质中，
        // 所有这些组成了数字音频的产生过程(抽样、量化、编码三个过程)。

    }


    public void pcmToWAV(){
        //PCM(Pulse Code Modulation)，脉冲编码调制。人耳听到的是模拟信号，PCM是把声音从模拟信号转化为数字信号的技术。
        // 原理是用一个固定的频率对模拟信号进行采样，采样后的信号在波形上看就像一串连续的幅值不一的脉冲(脉搏似的短暂起伏的电冲击)，
        // 把这些脉冲的幅值按一定精度进行量化，这些量化后的数值被连续的输出、传输、处理或记录到存储介质中，
        // 所有这些组成了数字音频的产生过程(抽样、量化、编码三个过程)。


    }

}
