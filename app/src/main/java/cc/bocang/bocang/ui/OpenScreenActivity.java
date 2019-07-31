package cc.bocang.bocang.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.lib.common.hxp.global.UserSp;
import com.squareup.okhttp.ResponseBody;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.data.parser.ParseGetUserInfoResp;
import cc.bocang.bocang.data.response.GetUserInfoResp;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.utils.ImageUtil;
import cc.bocang.bocang.utils.SignIdUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OpenScreenActivity extends AppCompatActivity {
    private final String TAG = OpenScreenActivity.class.getSimpleName();
    private OpenScreenActivity ct = this;

    private long mBeginTime;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_openscreen);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);

        Bitmap bmp = ImageUtil.getBitmapById(ct, R.mipmap.bg_openscreen);
        mContentView.setBackgroundDrawable(new BitmapDrawable(bmp));
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.push_in);
        mContentView.startAnimation(anim);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        mBeginTime = System.currentTimeMillis();

        getUserInfo();

    }

    private void getUserInfo() {
        HDApiService apiService = HDRetrofit.create(HDApiService.class);
        Call<ResponseBody> call = apiService.getUserInfo(SignIdUtil.getSignId(ct));
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    Log.i("520", "获取用户信息："+json);
                    GetUserInfoResp resp = ParseGetUserInfoResp.parse(json);
                    Log.i(TAG, resp.toString());
                    if (null != resp && resp.isSuccess()) {
                        UserInfo bean = resp.getBean();
                        int code = bean.getIs_use();
                        if (code == 0) {
                            showDialog("该设备未被授权使用");
                        } else if (code == 1) {
                            UserSp userSp = new UserSp(ct);
                            userSp.setInt(userSp.getSP_USERID(), bean.getId());
                            userSp.setString(userSp.getSP_ZHU_PHONE(), bean.getZhu_phone());
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    // 保证最少在这个页面停留2秒
                                    if (System.currentTimeMillis() - mBeginTime < 1800) {
                                        try {
                                            Thread.sleep(1800 + mBeginTime - System.currentTimeMillis());
                                            goOn();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        goOn();
                                    }
                                }
                            });
                            ((MyApplication)getApplication()).mUserInfo=bean;
                            thread.setName("SleepThread");
                            thread.start();


                        }
                    } else if (null != resp && !resp.isSuccess()) {
                        if (0 == resp.getErrorCode())
                            showDialog("该设备未被授权使用");
                        else if (2 == resp.getErrorCode()) {
                            startActivity(new Intent(ct, RegisterActivity.class));
                            finish();
                        }
                    }
                } catch (Exception e) {
                    showDialog("数据异常！");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == ct || ct.isFinishing())
                    return;
                showDialog("无法连接服务器！");
            }
        });
    }

    private void showDialog(String msg) {
        final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(ct);
        dialog.withTitle("提示：")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("\n" + msg + "\n")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor(getResources().getColor(R.color.colorPrimary))
                .isCancelableOnTouchOutside(false)
                .isCancelable(true)
                .withDuration(700)
                .withEffect(Effectstype.Shake)
                .withButton1Text("返回").
                setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ct.finish();
                    }
                })
                .show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(0);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private final int GO_ON = 1;
    private Handler mHandler = new Handler() {

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            if (null != ct && !ct.isFinishing()) {
                switch (msg.what) {
                    case GO_ON:
                        startActivity(new Intent(ct, IndexActivity.class));
                        finish();
                        break;
                }
            }
        }
    };

    private void goOn() {
        mHandler.sendEmptyMessage(GO_ON);
    }
}
