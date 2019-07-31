package cc.bocang.bocang.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xpHuang on 2016/8/31.
 */
public class EncryptUtil {

    /**
     * 获取字符串的MD5值
     * @param str 字符串
     * @return md5值或null
     */
    public static String getMD5(String str) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
