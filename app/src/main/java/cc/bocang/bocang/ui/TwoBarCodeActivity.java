package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cc.bocang.bocang.R;

/**
 * @author: Jun
 * @date : 2016/11/15 9:37
 * @description :
 */
public class TwoBarCodeActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_bar_code);
    }

    public void goBack(View v){
        finish();
    }
}
