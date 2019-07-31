package cc.bocang.bocang.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import cc.bocang.bocang.global.Constant;

public class SignIdUtil {

    /**
     * 获取设备唯一标识码
     *
     * @param ct 上下文
     * @return MAC地址或Android_ID或imei或序列码
     */
    public static String getSignId(Context ct) {
        if (Constant.isDebug)
            return "ce97c8f7e1733564b4f20046d5d24831";

        String macAddr = getMacAddress(ct);
        if (macAddr != null && !"".equals(macAddr) && !"02:00:00:00:00:00".equals(macAddr.trim())) {
            return macAddr;
        }
        String androidId = getAndroid_id(ct);
        if (null != androidId && !"9774d56d682e549c".equals(androidId.trim())) {
            return androidId;
        }
        String serialNum = getSerialNum();
        if (null != serialNum) {
            return serialNum;
        }
        String imei = getIMEI(ct);// IMEI码
        if (imei != null && imei.length() >= 14 && !"".equals(imei) && imei.length() < 16) {
            return imei;
        }

        return Build.MODEL + " " + Build.VERSION.RELEASE + " " + Build.VERSION.SDK_INT;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param ct
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context ct) {
        return (ct.getResources()
                .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 获取IMEI
     *
     * @param ct
     * @return String
     */
    public static String getIMEI(Context ct) {
        TelephonyManager telephonyManager = (TelephonyManager) ct
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();// GSM手机返回IMEI，对于CDMA手机返回MEID,如果设备不可用则返回NULL，比如在模拟器上。
    }

    /**
     * 获取android_id:表示一个64位的数字，在设备第一次启动的时候随机生成并在设备的整个生命周期中不变。（如果重新进行出厂设置可能会改变）
     *
     * @param ct
     * @return String
     */
    public static String getAndroid_id(Context ct) {
        return Secure.getString(ct.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * 获取序列号
     *
     * @return String
     */
    public static String getSerialNum() {
        String serialnum;// 序列号
        try {

            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class, String.class);

            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));

        } catch (Exception e) {
            serialnum = null;
        }
        return serialnum;
    }

    /**
     * 获取mac地址
     *
     * @param ct
     * @return String
     */
    public static String getMacAddress(Context ct) {

        WifiManager wifi = (WifiManager) ct
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();

    }

}
