package com.baiiu.filter.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baiiu on 15/11/25.
 * 简单的通用util
 */
public class CommonUtil {

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean notEmpty(List list) {
        return !isEmpty(list);
    }
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


    //全部为空
    public static boolean isAllEmpty(List... lists) {
        List<Boolean> booleans = new ArrayList<>();

        for (List list : lists) {
            if (isEmpty(list)) {
                booleans.add(Boolean.TRUE);
            }
        }

        return notEmpty(booleans) && booleans.contains(Boolean.FALSE);
    }

    //只有一个为空
    public static boolean isOneEmpty(List... lists) {
        for (List list : lists) {
            if (isEmpty(list)) {
                return true;
            }
        }

        return false;
    }


    //====================================================================
    //====================================================================

    public static boolean isEmpty(String s) {
        return s == null || TextUtils.isEmpty(s);
    }

    public static boolean notEmpty(String s) {
        return s != null && !TextUtils.isEmpty(s.trim());
    }

    public static boolean isAllEmpty(String... strings) {
        List<Boolean> booleans = new ArrayList<>();

        for (String s : strings) {
            booleans.add(CommonUtil.isEmpty(s));
        }

        return notEmpty(booleans) && booleans.contains(Boolean.FALSE);
    }

    //只有一个为空
    public static boolean isOneEmpty(String... strings) {
        for (String s : strings) {
            if (isEmpty(s)) {
                return true;
            }
        }

        return false;
    }

    public static long mLastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }

    public static boolean isMobileNO(String number) {
        boolean re = false;
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        if (number.length() == 11) {
            if (number.startsWith("13")) {
                re = true;
            } else if (number.startsWith("14")) {
                re = true;
            } else if (number.startsWith("15")) {
                re = true;
            } else if (number.startsWith("17")) {
                re = true;
            } else if (number.startsWith("18")) {
                re = true;
            }
        }
        return re;
    }

}
