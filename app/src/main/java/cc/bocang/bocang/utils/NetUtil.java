package cc.bocang.bocang.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;

/**
 * Created by thinkpad on 2016/3/23.
 */
public class NetUtil {
    /**
     * make true current connect service is wifi
     *
     * @return true：是WiFi；false：不是WiFi
     */
    public static boolean isWifi(Context ct) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ct.getSystemService(ct.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否连接网络并且能ping通外网
     * @return true：能上外网；false：没连接网络或连接局域网
     */
    public static boolean isWAN(Context ct){
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            boolean isConn =  manager.getActiveNetworkInfo().isAvailable();
            if(isConn){
                try {
                    String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
                    Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
                    // ping的状态
                    int status = p.waitFor();
                    if (status == 0) {
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
