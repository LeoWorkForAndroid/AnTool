package android_serialport_api;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import static android.content.Context.MODE_PRIVATE;

/**
 * 作者：李阳
 * 时间：2018/7/18
 * 描述：
 */
public class SerialPortManager {

    private static SerialPortManager mSerialPortManager = new SerialPortManager();

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();

    private String SerialPortPref = "android_serialport_api.sample_preferences";

    private String Device = "DEVICE";

    private String Baudrate = "BAUDRATE";

    private SerialPort mSerialPort = null;

    /**
     * 获取该类的实例对象，为单例
     *
     * @return
     */
    public static SerialPortManager getInstance() {
        return mSerialPortManager;
    }


    public SerialPort getSerialPort(Context context) throws SecurityException, IOException, InvalidParameterException {

        if (mSerialPort == null) {
            /* Read serial port parameters */
            SharedPreferences sp = context.getSharedPreferences(SerialPortPref, MODE_PRIVATE);
            String path = sp.getString(Device, "");
            int baudrate = Integer.decode(sp.getString(Baudrate, "-1"));

			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


}
