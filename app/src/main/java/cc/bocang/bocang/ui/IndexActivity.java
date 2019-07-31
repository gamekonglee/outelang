package cc.bocang.bocang.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.lib.common.hxp.global.UserSp;

import java.util.Iterator;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.broadcast.Broad;
import cc.bocang.bocang.data.api.OtherApi;
import cc.bocang.bocang.data.dao.CartDao;
import cc.bocang.bocang.data.model.AppVersion;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.Result;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.service.UpdateApkService;
import cc.bocang.bocang.utils.AppUtil;
import cc.bocang.bocang.utils.UpAppUtils;
import cc.bocang.bocang.utils.VersionUtil;
import cc.bocang.bocang.view.MyViewpage;

public class IndexActivity extends BaseActivity implements OnClickListener {
    private final String TAG = IndexActivity.class.getSimpleName();

    private LinearLayout mHomeLl, mProductLl, mMatchLl, mMoreLl;
    private RelativeLayout mCartRl;
    private ImageButton mHomeImgBtn, mProductImgBtn, mMatchImgBtn, mCartImgBtn, mMoreImgBtn;
    private TextView mHomeTv, mProductTv, mMatchTv, mCartTv, mMoreTv, mUnReadTv,mDeletetv;
    private Button mScanBtn, mRightBtn,mCollectBtn;

    protected MyViewpage mViewPager;
    protected FragmentPagerAdapter mFragmentPagerAdapter;
    protected boolean[] fragmentsUpdateFlag = {false, false, false, false};
    public static int titlePos;
    public static int itemPos;
    public static int goodsId;
    public static boolean isClickFmHomeItem;
    public static  String mCId="";

    private int mScreenWidth;
    private IndexActivity ct = this;

    private final int CONN_FAIL = 0;
    private final int NEED_UPDATE = 1;

    private LocalBroadcastManager mLocalBroadcastManager;
    private MyBroadcastReciver mBroadcastReciver;

    private void connFail() {
        mHandler.sendEmptyMessage(CONN_FAIL);
    }

    private void needUpdate() {
        mHandler.sendEmptyMessage(NEED_UPDATE);
    }

    private Handler mHandler = new Handler() {

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void handleMessage(Message msg) {
            if (!IndexActivity.this.isFinishing()) {
                switch (msg.what) {
                    case CONN_FAIL:
                        Toast.makeText(IndexActivity.this, "连接失败，请检查网络状态...", Toast.LENGTH_LONG).show();
                        break;
                    case NEED_UPDATE:
                        final UserSp userSp = new UserSp(ct);
                        final String apkUrl = userSp.getString(userSp.getSP_APK_URI(), "");
                        if (!"".equals(apkUrl)) {
                            showInstallDialog("");
                        }

                        if (Constant.isDebug) {
                            Log.i(TAG, System.currentTimeMillis() + "开始下载apk！");
                        }
                        broadcastReceiver = new UpdateApkBroadcastReceiver();
                        IndexActivity.this.registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        Intent intent = new Intent(IndexActivity.this, UpdateApkService.class);
                        IndexActivity.this.startService(intent);
                        break;

                }
            }
        }
    };

    private UpdateApkBroadcastReceiver broadcastReceiver;

    private class UpdateApkBroadcastReceiver extends BroadcastReceiver {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onReceive(Context context, final Intent intent) {
            // 判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                // 注销广播
                unregisterReceiver(broadcastReceiver);
                broadcastReceiver = null;

                // 获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager down = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                final Uri uri = down.getUriForDownloadedFile(downId);
                if (Constant.isDebug) {
                    Log.i(TAG, System.currentTimeMillis() + "下载完成！Uri：" + uri);
                }
                final UserSp userSp = new UserSp(ct);
                userSp.setString(userSp.getSP_APK_URI(), uri.toString());
                showInstallDialog("");
            }
        }
    }

//    private void showInstallDialog() {
//        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(IndexActivity.this);
//        dialogBuilder.withTitle("提示：")
//                .withTitleColor("#FFFFFF")
//                .withDividerColor("#11000000")
//                .withMessage("新的版本已在WIFI环境下完成下载，是否现在安装？")
//                .withMessageColor("#FFFFFFFF")
//                .withDialogColor(ct.getResources().getColor(R.color.colorPrimary))
//                // .withIcon(getResources().getDrawable(R.drawable.icon))
//                .isCancelableOnTouchOutside(false)
//                .isCancelable(false)
//                .withDuration(700)
//                .withEffect(Effectstype.Shake)
//                .withButton1Text("取消")
//                .withButton2Text("安装")
//                .setButton1Click(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialogBuilder.dismiss();
//                    }
//                })
//                .setButton2Click(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        UserSp userSp = new UserSp(ct);
//                        String apkUrl = userSp.getString(userSp.getSP_APK_URI(), "");
//                        userSp.setString(userSp.getSP_APK_URI(), "");
//                        AppUtil.installApk(ct, Uri.parse(apkUrl));
//                        dialogBuilder.dismiss();
//                    }
//                }).show();
//    }

    private void showInstallDialog(final String version) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(IndexActivity.this);
        dialogBuilder.withTitle("提示：")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("有新的版本 :"+version+"，是否下载?")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor(ct.getResources().getColor(R.color.colorPrimary))
                        // .withIcon(getResources().getDrawable(R.drawable.icon))
                .isCancelableOnTouchOutside(false)
                .isCancelable(false)
                .withDuration(700)
                .withEffect(Effectstype.Shake)
                .withButton1Text("取消")
                .withButton2Text("确定")
                .setButton1Click(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent("android.intent.action.VIEW");
                        Uri uri=Uri.parse(Constant.APK_URL);
                        intent.setData(uri);
                        startActivity(intent);
//                        AppVersion appVersion = new AppVersion();
//                        appVersion.setVersion(version);
//                        appVersion.setName("");
//                        appVersion.setDes("");
//                        appVersion.setForcedUpdate("0");
//                        appVersion.setUrl(Constant.APK_URL);
//                        if (appVersion != null) {
//                            new UpAppUtils(IndexActivity.this, appVersion);
//                        }
                        dialogBuilder.dismiss();
                    }
                }).show();
    }

    public List<Goods> goodses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();

        CartDao dao = new CartDao(ct);
        goodses = dao.getAll();
        Log.i(TAG, goodses.toString());
        if (goodses.isEmpty()) {
            mUnReadTv.setVisibility(View.GONE);
        } else {
            mUnReadTv.setVisibility(View.VISIBLE);
            mUnReadTv.setText(goodses.size() + "");
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE},
                1);

        //沉浸式状态栏
        setColor(this, getResources().getColor(R.color.colorPrimary));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String serverVersion = OtherApi.getAppVersion();
                String localVersion = AppUtil.localVersionName(ct.getApplicationContext());
                if ("-1".equals(serverVersion)) {
                    connFail();
                } else {
//                    UserSp userSp = new UserSp(ct);
//                    userSp.setString(userSp.getSP_SERVER_VERSION(), serverVersion);//用于下载完apk文件名
//                    boolean isNeedUpdate = VersionUtil.isNeedUpdate(localVersion, serverVersion);
//                    if (isNeedUpdate)
//                        needUpdate(); boolean isNeedUpdate = VersionUtil.isNeedUpdate(localVersion, serverVersion);
                    boolean isNeedUpdate = VersionUtil.isNeedUpdate(localVersion, serverVersion);
                    if (isNeedUpdate){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showInstallDialog(serverVersion);
                            }
                        });

                    }

                }
            }
        });
        thread.start();


        new Thread(new Runnable() {
            @Override
            public void run() {
//                mCId
                Result result=new Result();
                String jsonString = OtherApi.doGet(Constant.PROUCT_CID);
                if (!TextUtils.isEmpty(jsonString)) {
                    result = JSON.parseObject(jsonString, Result.class);
                }
                mCId=result.getContent();


            }
        }).start();

        mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mViewPager.setCurrentItem(0);
    }


    @Override
    public void onClick(View v) {
        if (v == mHomeLl) {
            mViewPager.setCurrentItem(0, false);
        } else if (v == mProductLl) {
            mViewPager.setCurrentItem(1, false);
        } else if (v == mMatchLl) {
            mViewPager.setCurrentItem(2, false);
        } else if (v == mCartRl) {
            mViewPager.setCurrentItem(3, false);
        } else if (v == mMoreLl) {
            mViewPager.setCurrentItem(4, false);
        } else if (v == mScanBtn) {
            Intent intent = new Intent();
            intent.setClass(IndexActivity.this, SimpleScannerActivity.class);
            //调用一个新的Activity
            startActivity(intent);

        } else if (v == mRightBtn) {
            int position = mViewPager.getCurrentItem();
            Log.v("520it",position+"");
            if (position == 1) {
                Intent intent = new Intent(ct, ContainerActivity.class);
                intent.putExtra("title", "我的收藏");
                intent.putExtra("fm", FmCollect.class.getSimpleName());
                ct.startActivity(intent);
            }
        }else if(v==mCollectBtn){
            int position = mViewPager.getCurrentItem();
            Log.v("520it",position+"");
            if(position==0 || position==1){
                getSeachProduct();
            }
        }else if(v==mDeletetv){
            boolean hasDelete = false;
            Iterator<Goods> iterator = goodses.iterator();
            while (iterator.hasNext()) {
                Goods goods = iterator.next();
                if (goods.delete) {
                    iterator.remove();   //注意这个地方
                    CartDao dao = new CartDao(ct);
                    dao.deleteOne(goods.getId());
                    hasDelete = true;
                }
            }
            if (hasDelete)
                Broad.sendLocalBroadcast(ct, Broad.CART_CHANGE_ACTION, null);//发送广播
        }
    }

    private void getSeachProduct() {
        Intent intent = new Intent(ct, SearchActivity.class);
        intent.putExtra("title", "产品搜索");
        intent.putExtra("okcat_id",0);
        intent.putExtra("fm", FmCollect.class.getSimpleName());
        ct.startActivity(intent);
    }


    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // 该方法返回值表明该Adapter总共包含多少个Fragment
        @Override
        public int getCount() {
            return 5;
        }

        // 获取第position位置的Fragment
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (0 == position) {
                fragment = new FmHome();
                fragment.setArguments(new Bundle());
            } else if (1 == position) {
                fragment = new FmProduct();
                fragment.setArguments(new Bundle());
            } else if (2 == position) {
                fragment = new FmScene();
                fragment.setArguments(new Bundle());
            } else if (3 == position) {
                fragment = new FmCart();
                fragment.setArguments(new Bundle());
            } else if (4 == position) {
                fragment = new FmMore();
                fragment.setArguments(new Bundle());
            }
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            // 得到tag，这点很重要
            String fragmentTag = fragment.getTag();

            if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
                // 如果这个fragment需要更新
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // 移除旧的fragment
//                ft.remove(fragment);
                // 换成新的fragment
                if (position == 0) {// 第一个页面
                    fragment = new FmHome();// 换成新的fragment（可用于显示新的布局）
                    fragment.setArguments(new Bundle());
                } else if (position == 1) {
                    fragment = new FmProduct();// 换成新的fragment（可用于显示新的布局）
                } else if (position == 2) {
                    fragment = new FmScene();// 换成新的fragment（可用于显示新的布局）
                    fragment.setArguments(new Bundle());
                } else if (position == 3) {
                    fragment = new FmCart();// 换成新的fragment（可用于显示新的布局）
                    fragment.setArguments(new Bundle());
                } else if (position == 4) {
                    fragment = new FmMore();// 换成新的fragment（可用于显示新的布局）
                    fragment.setArguments(new Bundle());
                }

                // 添加新fragment时必须用前面获得的tag，这点很重要
                ft.add(container.getId(), fragment, fragmentTag);
                ft.attach(fragment);
                ft.commitAllowingStateLoss();

                // 复位更新标志
                fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
            }

            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    setTabSelector(mHomeImgBtn, mHomeTv);
                    break;
                case 1:
                    setTabSelector(mProductImgBtn, mProductTv);
                    break;
                case 2:
                    setTabSelector(mMatchImgBtn, mMatchTv);
                    break;
                case 3:
                    setTabSelector(mCartImgBtn, mCartTv);
                    break;
                case 4:
                    setTabSelector(mMoreImgBtn, mMoreTv);
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        private void setTabSelector(ImageButton imgBtn, TextView tv) {
            mHomeImgBtn.setBackgroundResource(R.mipmap.ic_home_normal);
            mProductImgBtn.setBackgroundResource(R.mipmap.ic_product_normal);
            mMatchImgBtn.setBackgroundResource(R.mipmap.ic_match_normal);
            mCartImgBtn.setBackgroundResource(R.mipmap.ic_cart_normal);
            mMoreImgBtn.setBackgroundResource(R.mipmap.ic_more_normal);

            if (imgBtn == mHomeImgBtn) {
                mRightBtn.setVisibility(View.GONE);
                mDeletetv.setVisibility(View.GONE);
                mCollectBtn.setVisibility(View.VISIBLE);
                mCollectBtn.setBackgroundResource(R.mipmap.sousuo);
                mHomeImgBtn.setBackgroundResource(R.mipmap.ic_home_press);
                mRightBtn.setBackgroundResource(R.mipmap.sousuo);
            } else if (imgBtn == mProductImgBtn) {
                mRightBtn.setVisibility(View.VISIBLE);
                mDeletetv.setVisibility(View.GONE);
                mCollectBtn.setVisibility(View.VISIBLE);
                mCollectBtn.setBackgroundResource(R.mipmap.sousuo);
                mRightBtn.setBackgroundResource(R.mipmap.ic_collect_1);
                mProductImgBtn.setBackgroundResource(R.mipmap.ic_product_press);
            } else if (imgBtn == mMatchImgBtn) {
                mRightBtn.setVisibility(View.GONE);
                mDeletetv.setVisibility(View.GONE);
                mCollectBtn.setVisibility(View.GONE);
                mMatchImgBtn.setBackgroundResource(R.mipmap.ic_match_press);
            } else if (imgBtn == mCartImgBtn) {
                mRightBtn.setVisibility(View.GONE);
                mDeletetv.setVisibility(View.VISIBLE);
                mCollectBtn.setVisibility(View.GONE);
                mCollectBtn.setBackgroundResource(R.drawable.selector_title_bar_delete);
                mCartImgBtn.setBackgroundResource(R.mipmap.ic_cart_press);
            } else if (imgBtn == mMoreImgBtn) {
                mRightBtn.setVisibility(View.GONE);
                mDeletetv.setVisibility(View.GONE);
                mCollectBtn.setVisibility(View.GONE);
                mMoreImgBtn.setBackgroundResource(R.mipmap.ic_more_press);
            }

            mHomeTv.setTextColor(Color.parseColor("#999999"));
            mProductTv.setTextColor(Color.parseColor("#999999"));
            mMatchTv.setTextColor(Color.parseColor("#999999"));
            mCartTv.setTextColor(Color.parseColor("#999999"));
            mMoreTv.setTextColor(Color.parseColor("#999999"));
            tv.setTextColor(0xFFCA0000);
        }
    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭下拉菜单
        if (null != FmProduct.dropDownMenu && FmProduct.dropDownMenu.isShowing()) {
            FmProduct.dropDownMenu.close();
        } else if (null != FmScene.dropDownMenu && FmScene.dropDownMenu.isShowing()) {
            FmScene.dropDownMenu.close();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReciver);
    }

    private class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Broad.CART_CHANGE_ACTION)) {
                CartDao dao = new CartDao(ct);
                goodses = dao.getAll();
                if (goodses.isEmpty()) {
                    mUnReadTv.setVisibility(View.GONE);
                } else {
                    mUnReadTv.setVisibility(View.VISIBLE);
                    mUnReadTv.setText(goodses.size() + "");
                }
                fragmentsUpdateFlag[3] = true;
                mFragmentPagerAdapter.notifyDataSetChanged();
            }
        }
    }



    private void initView() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mViewPager = (MyViewpage) findViewById(R.id.viewPager);
        mViewPager.setNoScroll(true);
        mHomeLl = (LinearLayout) findViewById(R.id.homeLl);
        mProductLl = (LinearLayout) findViewById(R.id.productLl);
        mMatchLl = (LinearLayout) findViewById(R.id.matchLl);
        mCartRl = (RelativeLayout) findViewById(R.id.cartRl);
        mMoreLl = (LinearLayout) findViewById(R.id.moreLl);
        mHomeLl.setOnClickListener(this);
        mProductLl.setOnClickListener(this);
        mMatchLl.setOnClickListener(this);
        mCartRl.setOnClickListener(this);
        mMoreLl.setOnClickListener(this);
        mHomeImgBtn = (ImageButton) findViewById(R.id.homeImgBtn);
        mProductImgBtn = (ImageButton) findViewById(R.id.productImgBtn);
        mMatchImgBtn = (ImageButton) findViewById(R.id.matchImgBtn);
        mCartImgBtn = (ImageButton) findViewById(R.id.cartImgBtn);
        mMoreImgBtn = (ImageButton) findViewById(R.id.moreImgBtn);
        mHomeTv = (TextView) findViewById(R.id.homeTv);
        mProductTv = (TextView) findViewById(R.id.productTv);
        mMatchTv = (TextView) findViewById(R.id.matchTv);
        mCartTv = (TextView) findViewById(R.id.cartTv);
        mUnReadTv = (TextView) findViewById(R.id.unReadTv);
        mMoreTv = (TextView) findViewById(R.id.moreTv);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mScanBtn = (Button) findViewById(R.id.topLeftBtn);
        mScanBtn.setOnClickListener(this);
        mRightBtn = (Button) findViewById(R.id.topRightBtn);
        mRightBtn.setOnClickListener(this);
        mCollectBtn = (Button)findViewById(R.id.collectBtn);
        mCollectBtn.setOnClickListener(this);

        mDeletetv = (TextView)findViewById(R.id.deletetv);
        mDeletetv.setOnClickListener(this);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(ct);
        mBroadcastReciver = new MyBroadcastReciver();
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction(Broad.CART_CHANGE_ACTION);
        mLocalBroadcastManager.registerReceiver(mBroadcastReciver, localIntentFilter);
    }

}
