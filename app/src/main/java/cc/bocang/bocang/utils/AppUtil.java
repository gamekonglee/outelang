package cc.bocang.bocang.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by thinkpad on 2016/9/2.
 */
public class AppUtil {
    /**
     * 获取app版本名称
     *
     * @return 版本名称或null
     */
    public static String localVersionName(Context ct) {
        String versionName = null;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionName = ct.getPackageManager().getPackageInfo(ct.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    /**
     * 安装apk
     */
    public static void installApk(Context ct, Uri apkUri) {
        // 通过Intent安装APK文件
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(apkUri);
        intent.setDataAndType(apkUri,
                "application/vnd.android.package-archive");
        ct.startActivity(intent);
    }
}
