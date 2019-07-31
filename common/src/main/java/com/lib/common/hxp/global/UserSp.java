package com.lib.common.hxp.global;

import android.content.Context;
import android.content.SharedPreferences;

/***
 * Created by thinkpad on 2016/3/22.
 */
public class UserSp {

    private SharedPreferences sp;

    /**
     * 最新版apk下载完成后存在本地的URI
     */
    public String getSP_APK_URI() {
        return "apkUri";
    }

    /**
     * 服务器app版本
     */
    public String getSP_SERVER_VERSION() {
        return "serverAPKVersion";
    }

    /**
     * 用户id
     *
     * @return
     */
    public String getSP_USERID() {
        return "userId";
    }
    /**
     * 父经销商电话
     *
     * @return
     */
    public String getSP_ZHU_PHONE() {
        return "zhuPhone";
    }

    public UserSp(Context ct) {
        //存用户信息的SharedPreferences的文件名
        sp = ct.getSharedPreferences("userinfos", Context.MODE_PRIVATE);
    }

    /***
     * 获取userinfos.xml(SharedPreferences)定义的字符串值
     *
     * @param key          定义的字段名
     * @param defaultValue 获取失败返回的默认值
     * @return String
     */
    public String getString(String key, String defaultValue) {
        String value = sp.getString(key, defaultValue);
        return value;
    }

    /***
     * 获取userinfos.xml(SharedPreferences)定义的整型值
     *
     * @param key          定义的字段
     * @param defaultValue 获取失败返回的默认值
     * @return int
     */
    public int getInt(String key, int defaultValue) {
        int value = sp.getInt(key, defaultValue);
        return value;
    }

    /***
     * 获取userinfos.xml(SharedPreferences)定义的布尔值
     *
     * @param key          定义的字段
     * @param defaultValue 获取失败返回的默认值
     * @return boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        boolean value = sp.getBoolean(key, defaultValue);
        return value;
    }

    /**
     * 把字符串写入userinfos.xml(SharedPreferences)文件
     *
     * @param key   字段名
     * @param value 字段值
     */
    public void setString(String key, String value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(key, value);
        ed.commit();
    }

    /***
     * 把字符串写入userinfos.xml(SharedPreferences)文件
     *
     * @param keys   字段名数组
     * @param values 字段值数组
     */
    public boolean setStrings(String[] keys, String[] values) {
        SharedPreferences.Editor ed = sp.edit();
        for (int i = 0; i < keys.length; i++) {
            ed.putString(keys[i], values[i]);
        }
        return ed.commit();
    }

    /***
     * 把布尔值写入userinfos.xml(SharedPreferences)文件
     *
     * @param keys   字段名数组
     * @param values 字段值数组
     */
    public boolean setBooleans(String[] keys, boolean[] values) {
        SharedPreferences.Editor ed = sp.edit();
        for (int i = 0; i < keys.length; i++) {
            ed.putBoolean(keys[i],
                    values[i]);
        }
        return ed.commit();
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(key, value);
        ed.commit();
    }

    /***
     * 把整型写入userinfos.xml(SharedPreferences)文件
     *
     * @param keys   字段名数组
     * @param values 字段值数组
     */
    public boolean setInts(String[] keys, int[] values) {
        SharedPreferences.Editor ed = sp.edit();
        for (int i = 0; i < keys.length; i++) {
            ed.putInt(keys[i], values[i]);
        }
        return ed.commit();
    }
}
