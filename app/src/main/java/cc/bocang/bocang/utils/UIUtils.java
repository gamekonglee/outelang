package cc.bocang.bocang.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cc.bocang.bocang.R;
import cc.bocang.bocang.global.MyApplication;

/**
 * @author Jun
 * @time 2016/8/19  10:37
 * @desc ${TODD}
 */
public class UIUtils {

    /**
     * 得到上下文
     * @return
     */
    public static Context getContext(){
        return MyApplication.getInstance();
    }


    public static String getDeviceId(){
        return ((TelephonyManager) getContext().getSystemService(getContext().TELEPHONY_SERVICE))
                .getDeviceId();
    }


    /**
     *mac
     * @param context
     * @return String
     */
    public static String getLocalMac(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac=info.getMacAddress();
        if(mac.equals("02:00:00:00:00:00")){
            return getInterfaceLocalmac();
        }else {
            return mac;
        }
    }



    public static String  getInterfaceLocalmac(){
        String mac="";
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iF = interfaces.nextElement();
                byte[] addr = iF.getHardwareAddress();
                if (addr == null || addr.length == 0) {
                    continue;
                }
                StringBuilder buf = new StringBuilder();
                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                mac = buf.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }


    /**
     * 得到Resources对象
     * @return
     */
    public static Resources getResources(){
        return getContext().getResources();
    }

    /**
     * 得到包名
     * @return
     */
    public static String getpackageName(){
        return  getContext().getPackageName();
    }

    /**
     * 得到配置的String信息
     * @param resId
     * @return
     */
    public static String getString(int resId){
        return getResources().getString(resId);
    }

    /**
     * 得到配置的String信息
     * @param resId
     * @return
     */
    public static String getString(int resId,Object ...formatAgs){
        return getResources().getString(resId,formatAgs);
    }

    /**
     * 得到配置String数组
     * @param resId
     * @return
     */
    public static String[] getStringArr(int resId){
        return getResources().getStringArray(resId);
    }
    public static int dip2PX(int dip) {
        //拿到设备密度
        float density=getResources().getDisplayMetrics().density;
        int px= (int) (dip*density+.5f);
        return px;
    }

    public static Dialog showBottomInDialog(Activity activity, int layout_res, int height) {
        Dialog dialog = new Dialog(activity, R.style.customDialog);
        dialog.setContentView(layout_res);
        dialog.setCanceledOnTouchOutside(true);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        lp.width = width;
        lp.height = height;
        lp.x=0;
        win.setAttributes(lp);
        dialog.show();
        return  dialog;
    }
}
