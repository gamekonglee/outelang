package cc.bocang.bocang.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import cc.bocang.bocang.R;

/**
 * @author Jun
 * @time 2016/10/22  16:26
 * @desc ${TODD}
 */
public class LoadingDailog extends AlertDialog {

    private ImageView mLoadingIv;

    public LoadingDailog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_progre);
        mLoadingIv=(ImageView)findViewById(R.id.loading_iv);
        AnimationDrawable drawable = (AnimationDrawable) mLoadingIv.getDrawable();
        drawable.start();
    }


}
