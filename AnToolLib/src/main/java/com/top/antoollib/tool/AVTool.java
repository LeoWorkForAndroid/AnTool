package com.top.antoollib.tool;

import android.media.AudioTrack;
import android.util.Log;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AVTool {

    private static final String TAG = "AVTool";
    private static volatile AVTool instance;// !!必须要加volatile限制指令重排序，不然这是双重检验的漏洞
    private static final Object lock = new Object();

    public AVTool() {

    }

    //单例模式，懒汉氏
    public static AVTool instance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new AVTool();
                }
            }
        }
        return instance;
    }


    public void playPCM(){

    }

    public void pcmToWav(){

    }

    public void pcmToMP3(){

    }

    private void readWavHeader(DataInputStream dis) {
        try {
            byte[] byteIntValue = new byte[4];
            byte[] byteShortValue = new byte[2];
            //读取四个
            String mChunkID = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "mChunkID:" + mChunkID);
            dis.read(byteIntValue);
            int chunkSize = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "chunkSize:" + chunkSize);
            String format = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "format:" + format);
            String subchunk1ID = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "subchunk1ID:" + subchunk1ID);
            dis.read(byteIntValue);
            int subchunk1Size = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "subchunk1Size:" + subchunk1Size);
            dis.read(byteShortValue);
            short audioFormat = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "audioFormat:" + audioFormat);
            dis.read(byteShortValue);
            short numChannels = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "numChannels:" + numChannels);
            dis.read(byteIntValue);
            int sampleRate = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "sampleRate:" + sampleRate);
            dis.read(byteIntValue);
            int byteRate = byteArrayToInt(byteIntValue);
            Log.e("Wav_Header", "byteRate:" + byteRate);
            dis.read(byteShortValue);
            short blockAlign = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "blockAlign:" + blockAlign);
            dis.read(byteShortValue);
            short btsPerSample = byteArrayToShort(byteShortValue);
            Log.e("Wav_Header", "btsPerSample:" + btsPerSample);
            String subchunk2ID = "" + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte() + (char) dis.readByte();
            Log.e("Wav_Header", "subchunk2ID:" + subchunk2ID);
            dis.read(byteIntValue);
            int subchunk2Size = byteArrayToInt(byteIntValue);
            Log.e("subchunk2Size", "subchunk2Size:" + subchunk2Size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int byteArrayToInt(byte[] byteIntValue) {

        return ByteBuffer.wrap(byteIntValue).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    private short byteArrayToShort(byte[] byteShortValue) {

        return ByteBuffer.wrap(byteShortValue).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }


}
