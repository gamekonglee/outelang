package cc.bocang.bocang.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;


/**
 * Created by xpHuang on 2016/9/29.
 */

public class Broad {
    public static final String CART_CHANGE_ACTION = "cart_change_ACTION";
    /**
     * 发送一条本地广播
     *
     * @param action
     * @param bundle
     */
    public static void sendLocalBroadcast(Context ct, String action, Bundle bundle) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ct);
        Intent intent = new Intent();
        intent.setAction(action);
        if (null != bundle)
            intent.putExtras(bundle);
        localBroadcastManager.sendBroadcast(intent);
    }
}
