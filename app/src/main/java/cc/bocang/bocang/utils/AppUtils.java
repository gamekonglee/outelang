package cc.bocang.bocang.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AndroidRuntimeException;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Jun
 */
public class AppUtils {

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof String) {
            return ((String) o).trim().isEmpty() ? true : false;
        } else if (o instanceof JSONObject) {
            return ((JSONObject) o).size() == 0 ? true : false;
        } else if (o instanceof JSONArray) {
            return ((JSONArray) o).size() == 0 ? true : false;
        } else {
            return false;
        }
    }



    /**
     * convert String to int
     *
     * @param value
     * @return int value if success else 0
     */
    public int parseInt(String value) {
        int ret = 0;

        if (value == null || value.length() == 0) {
            return 0;
        }

        try {
            ret = Integer.parseInt(value);
        } catch (NumberFormatException e) {

        }

        return ret;
    }

    public String getNow() {
        String ret = null;

        try {
            ret = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());

        } catch (NullPointerException e) {
        } catch (IllegalArgumentException e) {

        }

        // ret = String.valueOf(System.currentTimeMillis());

        return ret;
    }

    /**
     * 从view 得到图片
     *
     * @param view
     * @return Bitmap
     */
    public Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }

    /**
     * dp转像素
     *
     * @param context 上下文
     * @param dp      dp值
     * @return 像素值
     */
    public int dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 像素转dp
     *
     * @param context 上下文
     * @param px      像素值
     * @return dp值
     */
    public int px2dip(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * resize图片.创建固定宽度的新图片<br />
     * 缩小
     *
     * @param src
     * @param dstWidth
     * @return Bitmap
     */
    public Bitmap createBitmap(Bitmap src, int dstWidth) throws Exception {

        Bitmap ret = null;

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        float ratio = ((float) dstWidth) / srcWidth;
        if (ratio >= 1)
            return src;
        int dstHeight = (int) (srcHeight * ratio);

        ret = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);

        // return
        return ret;
    }

    /**
     * resize图片.创建固定宽度高度的新图片<br />
     * 先缩小后剪切
     *
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return Bitmap
     */
    public Bitmap createBitmap(Bitmap src, int dstWidth, int dstHeight) {

        Bitmap ret = null;

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        float ratioX = ((float) dstWidth) / srcWidth;
        float ratioY = ((float) dstHeight) / srcHeight;

        float ratio = ratioX > ratioY ? ratioX : ratioY;

        int orgWidth = (int) (srcWidth * ratio);
        int orgHeight = (int) (srcHeight * ratio);

        // 缩小
        Bitmap org = Bitmap.createScaledBitmap(src, orgWidth, orgHeight, false);

        // 剪切
        ret = Bitmap.createBitmap(org, 0, 0, dstWidth > orgWidth ? orgWidth : dstWidth,
                dstHeight > orgHeight ? orgHeight : dstHeight);

        // return
        return ret;
    }

    /**
     * 保存bitmap文件
     *
     * @param bitmap
     * @param fileName
     * @param quality  :0 ~ 100
     * @return return null if ok,else error
     */
    public String saveBitmapFile(Bitmap bitmap, String fileName, int quality) {

        String ret = null;

        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        if (bitmap == null) {
            return "图片内容是空的";
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            ret = e.getMessage();
        } catch (IOException e) {
            ret = e.getMessage();
        } catch (AndroidRuntimeException e) {
            ret = e.getMessage();
        } catch (RuntimeException e) {
        } finally {

        }

        // return
        return ret;
    }

    public String getValidString(String source) {

        if (source == null)
            return "";

        //
        StringBuilder ret = new StringBuilder();
        int i;

        for (i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            boolean b = true;

            if ((c >= 0x20) && (c <= 0xD7FF)) {
                if (c == 0x22 || c == 0x26 || c == 0x27)
                    b = false;
            } else if ((c >= 0xE000) && (c <= 0xFFFD)) {
            } else if ((c >= 0x10000) && (c <= 0x10FFFF)) {

            } else {
                b = false;
            }

            if (b) {
                ret.append(c);
            }
        }

        return ret.toString();
    }

    public int validString(String source) {

        int ret = 0;

        if (source == null)
            return ret;

        int i;

        for (i = 0; i < source.length(); i++) {
            char c = source.charAt(i);

            if ((c == 0x0) || (c == 0x9) || (c == 0xA) || (c == 0xD) ||

                    (c == 0x22) || (c == 0x26) ||

                    ((c >= 0x20) && (c <= 0xD7FF)) || ((c >= 0xE000) && (c <= 0xFFFD)) || ((c >= 0x10000) && (c <= 0x10FFFF))) {

                ret = i + 1;
                break;
            }
        }

        return ret;
    }

    public boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

//    /**
//     * 检查网络状态
//     *
//     * @return
//     */
//    public static Boolean checkNetwork() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getInstance()
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//        if (info == null || !info.isAvailable()) {
//            return false;
//        }
//
//        return true;
//    }

    public static boolean isData(String str) {

        String eL = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(str);
        boolean b = m.matches();
        if (b) {
            return true;
        } else {
            return false;
        }

    }

    public static String getAns(JSONObject jsonObject) {
        if (jsonObject == null)
            return "服务器未回应,请检查网络是否连接";
        String ans = jsonObject.getString("error_code");
        return ans == null ? "服务器无效的回答" : ans;
    }
}
