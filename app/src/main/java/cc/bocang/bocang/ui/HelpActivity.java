package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cc.bocang.bocang.R;

/**
 * @author Jun
 * @time 2016/10/25  10:14
 * @desc ${TODD}
 */
public class HelpActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));
    }

    public void goBack(View v){
        finish();
    }
}
