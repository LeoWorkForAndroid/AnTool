package com.top.antoollib.base;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.os.Bundle;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;


/**
 * 作者：李阳
 * 时间：2018/8/22
 * 描述：
 */
public abstract class USBActivity extends AppCompatActivity {

    private static final String TAG = "USBActivity";

    UsbManager usbManager;
    private UsbDevice usbDevice;   //找到的USB设备

    private HashMap<String, UsbDevice> deviceList;  //设备列表

    UsbEndpoint inEndpoint;
    UsbEndpoint outEndpoint;
    UsbDeviceConnection usbDeviceConnection;
    // USBCommunicationManager manager;
    UsbInterface intf;

    byte[] open = {(byte) 0xCA, (byte) 0xDF, 0x04, 0x00, 0x00, (byte) 0xe3}; //开场
    byte[] close = {(byte) 0xCA, (byte) 0xDF, 0x05, 0x00, 0x00, (byte) 0xe3}; //关场

    //要发送信息字节
    private byte[] sendbytes = new byte[1024];
    //接收到的信息字节
    private byte[] receiveytes = new byte[1024];

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    PendingIntent mPermissionIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(receiver, filter);


    }


    private void initUsbDevice() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        deviceList = usbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();//迭代
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if ((device.getVendorId() == 1107 && device.getProductId() == 36869)
                    || (device.getVendorId() == 8457 && device.getProductId() == 30264)
                    || (device.getVendorId() == 4660 && device.getProductId() == 22136)
                    || (device.getVendorId() == 1659 && device.getProductId() == 8963)) {
                usbDevice = device;
            }
        }
        findUSBInterface();
    }

    private void findUSBInterface() {
        if (usbDevice == null) {
            Log.i(TAG, "没有找到设备");
            return;
        }

        for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            //一个设备上一般只有一个接口，有两个端点，分别接受和发送数据
            UsbInterface usbInterface = usbDevice.getInterface(i);
            intf = usbInterface;
            Log.i(TAG, intf.toString());
            break;
        }

        //获取端点
        for (int i = 0; i < intf.getEndpointCount(); i++) {
            UsbEndpoint ep = intf.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    outEndpoint = ep;
                    Log.i(TAG, "获取发送数据的端点");
                } else {
                    inEndpoint = ep;
                    Log.i(TAG, "获取接收数据的端点");
                }

            }
        }


        if (intf != null) {
            UsbDeviceConnection connection;

            if (usbManager.hasPermission(usbDevice)) {
                Log.i(TAG, "已经获取权限");
                connection = usbManager.openDevice(usbDevice);
                if (connection == null) {
                    Log.i(TAG, "设备连接为空");
                    return;
                }
                if (connection != null && connection.claimInterface(intf, true)) {
                    usbDeviceConnection = connection;
                } else {
                    connection.close();
                }
                // manager = new USBCommunicationManager(usbDeviceConnection, inEndpoint, outEndpoint);
            } else {
                usbManager.requestPermission(usbDevice, mPermissionIntent);
                if (usbManager.hasPermission(usbDevice)) {
                    Log.i(TAG, "获取权限");
                } else {
                    Log.i(TAG, "没有权限");
                }
            }
        } else {
            Log.i(TAG, "没有找到接口");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initUsbDevice();
        new Thread(new Runnable() {
            @Override
            public void run() {
              /*  if (manager == null) {
                    return;
                }*/
                int sendToUsb = sendToUsb(close);

            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);//取消广播
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                Log.i(TAG, "------Granted-----" + granted);
            }
        }
    };


    //给usb发送数据
    private int sendToUsb(byte[] conmand) {
        int ret = -1;
        //发送准备命令
        ret = usbDeviceConnection.bulkTransfer(outEndpoint, conmand, conmand.length, 2000);
        //Log.i(TAG, DataUtils.toHexString(conmand) + "---ret---" + ret);
        //Toast.makeText(this, "指令已发送,ret="+ret, Toast.LENGTH_SHORT).show();
        //usbDeviceConnection.close();
        return ret;
    }

    private void readFromUsb() {
        //读取数据2
        int outMax = outEndpoint.getMaxPacketSize();
        int inMax = inEndpoint.getMaxPacketSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(inMax);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(usbDeviceConnection, inEndpoint);
        usbRequest.queue(byteBuffer, inMax);
        if (usbDeviceConnection.requestWait() == usbRequest) {
            byte[] retData = byteBuffer.array();
            try {
                Log.i(TAG, "收到数据：" + new String(retData, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

}
