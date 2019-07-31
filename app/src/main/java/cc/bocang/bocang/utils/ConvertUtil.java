package cc.bocang.bocang.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by xpHuang on 2016/8/23.
 */
public class ConvertUtil {
    /**
     * dp转px
     * @param ct
     * @param dp
     * @return
     */
    public static float dp2px(Context ct, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ct.getResources().getDisplayMetrics());
    }
}
