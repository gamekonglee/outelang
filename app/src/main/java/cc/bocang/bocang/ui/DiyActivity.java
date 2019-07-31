package cc.bocang.bocang.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cc.bocang.bocang.R;
import cc.bocang.bocang.broadcast.Broad;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.api.OtherApi;
import cc.bocang.bocang.data.dao.CartDao;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.model.GoodsAttr;
import cc.bocang.bocang.data.model.GoodsClass;
import cc.bocang.bocang.data.model.Result;
import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.data.model.SceneAllAttr;
import cc.bocang.bocang.data.model.SceneAttr;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.data.parser.ParseGetGoodsListResp;
import cc.bocang.bocang.data.parser.ParseGetSceneListResp;
import cc.bocang.bocang.data.response.GetGoodsListResp;
import cc.bocang.bocang.data.response.GetSceneListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.utils.FileUtil;
import cc.bocang.bocang.utils.ImageUtil;
import cc.bocang.bocang.utils.LoadingDailog;
import cc.bocang.bocang.utils.PermissionUtils;
import cc.bocang.bocang.utils.ShareUtil;
import cc.bocang.bocang.utils.UIUtils;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;
import cc.bocang.bocang.view.TouchView;
import it.sephiroth.android.library.picasso.Picasso;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DiyActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private final String TAG = DiyActivity.class.getSimpleName();
    private DiyActivity ct = this;

    private final int PHOTO_WITH_DATA = 1; // 从SD卡中得到图片
    private final int PHOTO_WITH_CAMERA = 2;// 拍摄照片

    private String photoName;// 拍照保存的相片名称（不包含后缀名）
    private File cameraPath;// 拍照保存的相片路径

    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    private HDApiService apiService;
    private boolean displayFirstScene;
    private UserInfo mInfo;
    private LoadingDailog mLodingDailog;
    private String screePath;
    private boolean isShare=false;
    private boolean isCreate=true;
    private int goodsclassId;
    private Network mNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy);
        mNetwork=new Network();
        if(isCreate) {
            apiService = HDRetrofit.create(HDApiService.class);
            mInfo = ((MyApplication)getApplication()).mUserInfo;
            Intent intent = getIntent();
            initView();
            initImageLoader();
            mLodingDailog=new LoadingDailog(this, R.style.CustomDialog);
            String from = intent.getStringExtra("from");
            if ("scene".equals(from)) {
                String url=intent.getStringExtra("url");
                String path = intent.getStringExtra("path");
                displaySceneBg(url + path);
                mProIv.performClick();
            } else if ("goods".equals(from)) {
                Goods goods = (Goods) intent.getSerializableExtra("goods");
                displayFirstScene = true;
                mSceneIv.performClick();
                displayCheckedGoods(goods);
            }
        }

        Log.v("520it","出发到A"+isCreate);
        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));

    }

    private boolean isFullScreen;
    private int mSelectedTab;
    private int page = 1;

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        if (mSelectedTab == 1) {
            callGoodsClass(false);
            callGoodsListItem(apiService, goodsclassId,page);
//            callGoodsList(apiService, 0, page, null, null, fitterStr);

        }
        else if (mSelectedTab == 2)
            callSceneList(apiService, 0, page, null, null, fitterStr);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        if (mSelectedTab == 1) {
            callGoodsList(apiService, IndexActivity.mCId, ++page, null, null, fitterStr);
            callGoodsListItem(apiService, goodsclassId,++page);
//            callGoodsClass(apiService);
        }
        else if (mSelectedTab == 2)
            callSceneList(apiService, 0, ++page, null, null, fitterStr);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                takePhoto();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mFrameLayout) {
            if (!isFullScreen) {
                mDiyContainerRl.setVisibility(View.INVISIBLE);
                isFullScreen = true;
            } else {
                mDiyContainerRl.setVisibility(View.VISIBLE);
                isFullScreen = false;
            }
        } else if (view == mProIv) {
            setTabBg(mProIv);
            mListViewAdapter.setSelection(0);
            page = 1;
            callGoodsClass(true);

            mSelectedTab = 1;
            mOtherRl.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else if (view == mSceneIv) {
            setTabBg(mSceneIv);
            mListViewAdapter.setSelection(0);
            page = 1;
            callSceneList(apiService, 0, 1, null, null, "0.0.0");
            mSelectedTab = 2;
            mOtherRl.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else if (view == mOtherIv) {
            setTabBg(mOtherIv);
            mOtherRl.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else if (view == mCameraIv) {
            PermissionUtils.requestPermission(DiyActivity.this, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    takePhoto();
                }
            });
        } else if (view == mAlbumIv) {
            pickPhoto();
        }else {
            if (view == mShareIv) {//分享
                if(isShare==true) return;
                isShare=true;
//                产品ID的累加
                StringBuffer goodsid = new StringBuffer();
                for (int i = 0; i < mSelectedLightSA.size(); i++) {
                    goodsid.append(mSelectedLightSA.get(i).getId() + "");
                    if (i < mSelectedLightSA.size() - 1) {
                        goodsid.append(",");
                    }
                }

                mDiyContainerRl.setVisibility(View.INVISIBLE);
                Log.v("520", "前时间："+System.currentTimeMillis());
                //截图
                final Bitmap imageData =ImageUtil.takeScreenShot(this);
                mDiyContainerRl.setVisibility(View.VISIBLE);
                mLodingDailog.show();
                Log.v("520", "后时间："+System.currentTimeMillis());
                final String url =Constant.SUBMITPLAN;//地址
                final Map<String, String> params = new HashMap<String, String>();
                params.put("goods_id", goodsid.toString());
                params.put("phone", "android");
                params.put("title", "share");
                params.put("user_id", mInfo.getId() + "");
                params.put("village", "unknown");

                final String imageName= new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".png";
                new Thread(new Runnable() { //开启线程上传文件
                    @Override
                    public void run() {
                        final String resultJson=uploadFile(imageData, url, params,imageName);
                        final Result result=JSON.parseObject(resultJson,Result.class);
                        Log.v("520", "上传时间："+System.currentTimeMillis());
                        //分享的操作
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLodingDailog.dismiss();
                                Log.v("520it","分享成功!");
                                if(TextUtils.isEmpty(result.getResult())||result.getResult().equals("0")){
                                    return;
                                }
                                showShare(result.getResult(), Constant.SHAPE_SCEEN + result.getPath());
                            }
                        });

                    }
                }).start();
//                showShare("84");
                isShare=false;


            } else if (view == mGocarIv) {//加入购物车
                CartDao dao = new CartDao(ct);
                boolean isCarShop=false;
                for(int i=0;i<mSelectedLightSA.size();i++){
                    if (-1 != dao.replaceOne(mSelectedLightSA.valueAt(i))) {
                        isCarShop=true;
                    }
                }
                if(isCarShop==true){
                    tip("已添加到购物车");
                    Broad.sendLocalBroadcast(ct, Broad.CART_CHANGE_ACTION, null);//发送广播
                }
            }
        }
    }


    /**
     * 截屏
     * @param v			视图
     */
    private Bitmap getScreenHot(View v)
    {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        v.draw(canvas);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length);
    }

//    /**
//     * 提交方案
//     * @param apiService
//     * @param goodsid
//     * @param phone
//     * @param title
//     * @param userID
//     * @param village
//     * @param file
//     */
//    private void getSubmitPlan(HDApiService  apiService,String goodsid,String phone,String title,String userID,String village, Image file) {
//        Call<ResponseBody> call = apiService.submitPlan(file,goodsid,phone,title,userID,village);
//        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
//            @Override
//            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
//                if (null == DiyActivity.this || DiyActivity.this.isFinishing())
//                    return;
//
//                try {
//                    String json = response.body().string();
//                    Result result= JSON.parseObject(json,Result.class);
//                    Log.v("520it","result"+result.getResult());
//                    if(result.getResult().equals("0")){
//                        tip("上传方案失败!");
//                    }else{
//                        //分享操作
////                        showShare(result.getResult());
//
//                        tip("上传成功!");
//
//                    }
//                } catch (Exception e) {
//                    if (null != DiyActivity.this && ! DiyActivity.this.isFinishing())
//                        tip("上传方案失败!");
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                if (null ==  DiyActivity.this ||  DiyActivity.this.isFinishing())
//                    return;
//                tip("上传方案失败");
//            }
//        });
//    }
 /**
     * 分享操作
     */
    private void showShare(final String id,final String sceenpath) {

        if(TextUtils.isEmpty(id)){
            return;
        }
        final Dialog dialog= UIUtils.showBottomInDialog(this,R.layout.share_dialog,UIUtils.dip2PX(150));
        TextView tv_cancel= (TextView) dialog.findViewById(R.id.tv_cancel);
        LinearLayout ll_wx= (LinearLayout) dialog.findViewById(R.id.ll_wx);
        LinearLayout ll_pyq= (LinearLayout) dialog.findViewById(R.id.ll_pyq);
        LinearLayout ll_qq= (LinearLayout) dialog.findViewById(R.id.ll_qq);
        final String mGoodsname="来自"+getString(R.string.app_name)+"配灯的分享";
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final String url=Constant.SHAREPLAN+"id="+id;
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWx(DiyActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.sharePyq(DiyActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareQQ(DiyActivity.this, mGoodsname, url,sceenpath);
                dialog.dismiss();
            }
        });
    }

//    /**
//     * 分享操作
//     */
//    private void showShare(final String id,final String sceenpath) {
//
//        if(TextUtils.isEmpty(id)){
//            return;
//        }
//        ShareSDK.initSDK(this);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle("来自"+getString(R.string.app_name)+"配灯的分享");
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(Constant.SHAREPLAN+"id="+id);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("来自"+getString(R.string.app_name)+"配灯的分享");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(Constant.SHAREPLAN+"id="+id);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("来自"+getString(R.string.app_name)+"配灯的分享");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(Constant.SHAREPLAN+"id="+id);
//        //图片地址
////        mImgUrl= Constant.PRODUCT_URL+mImgUrl+ "!400X400.png";
////        Log.v("520it","'分享:"+mImgUrl);
////        Log.v("520it","产品地址:"+Constant.SHAREPLAN+"id="+id));
//        oks.setImageUrl(sceenpath);
//
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            @Override
//            public void onShare(Platform platform, final cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
//                if ("QZone".equals(platform.getName())) {
//                    paramsToShare.setTitle(null);
//                    paramsToShare.setTitleUrl(null);
//                }
//                if ("SinaWeibo".equals(platform.getName())) {
//                    paramsToShare.setUrl(null);
//                    paramsToShare.setText("分享文本 "+Constant.SHAREPLAN+"id="+id);
//                }
//                if ("Wechat".equals(platform.getName())) {
//                    ImageView img=new ImageView(DiyActivity.this);
//                    Picasso.with(DiyActivity.this).load(sceenpath).into(img);
//                }
//                if ("WechatMoments".equals(platform.getName())) {
//                    ImageView img=new ImageView(DiyActivity.this);
//                    Picasso.with(DiyActivity.this).load(sceenpath).into(img);
//                }
//
//            }
//        });
//       Log.v("520it","");
//        // 启动分享GUI
//        oks.show(this);
//    }



    private String fitterStr;


    /**
     * 弹出提示
     */
    private void showInstallDialog() {
        final EditText inputServer = new EditText(this);
        inputServer.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入密码查看此分类!").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String urlPath=Constant.SETUPPROUCT+inputServer.getText().toString();
                        String json = OtherApi.doGet(urlPath);
                        final Result result= JSON.parseObject(json, Result.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result.getCode().equals("1")) {
                                    callGoodsListItem(apiService, goodsclassId,1);
                                } else {
                                    Toast.makeText(DiyActivity.this, "密码错误，请重新输入!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();

            }
        });
        builder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       Log.v("520it","ffff");
        if (parent == mListView) {
            mListViewAdapter.setSelection(position);
            mListViewAdapter.notifyDataSetChanged();
            if (mSelectedTab == 1) {
                if (null != goodsAllAttrs)
//                    fitterStr = goodsAllAttrs.get(0).getGoodsAttrs().get(position).getGoods_id() + ".0.0";
//                    fitterStr = 33 + ".0.0";
                Log.v("520it",fitterStr);
                page = 1;
                goodsclassId = goodsClassList.get(position).getId();
                Log.v("520it","goodsclassId:"+goodsclassId);
//
                Log.v("520it","xx:"+IndexActivity.mCId);
                if(TextUtils.isEmpty(IndexActivity.mCId)){
                    callGoodsListItem(apiService, goodsclassId, 1);
                    return;
                }

                String[] cIdArrys = IndexActivity.mCId.split(",");
                Log.v("520it","cIdArrys:"+cIdArrys.toString());
                if (cIdArrys.length > 0) {
                    if (Arrays.binarySearch(cIdArrys, goodsclassId + "") >= 0) {
                        showInstallDialog();
                    }
                } else {
//                    callGoodsListItem(apiService, goodsclassId, 1);
                }
//                callGoodsListItem(apiService, goodsclassId, 1);
            } else if (mSelectedTab == 2) {
                if (null != sceneAllAttrs)
                    fitterStr = sceneAllAttrs.get(0).getSceneAttrs().get(position).getScene_id() + ".0";
                page = 1;
                callSceneList(apiService, 0, 1, null, null, fitterStr);
            }
        } else if (parent == mGridView) {
            if (mSelectedTab == 1) {
                Log.v("520it","出发到B");
                displayCheckedGoods(goodses.get(position));
            } else if (mSelectedTab == 2) {
                int ids= Integer.parseInt(scenes.get(position).getId());
                if(ids>1551){
                    displaySceneBg(Constant.SCENE_URL_2 + scenes.get(position).getPath());
                }else {
                    displaySceneBg(Constant.SCENE_URL + scenes.get(position).getPath());
                }

            }
        }
    }

    private List<GoodsAllAttr> goodsAllAttrs;

    private List<Goods> goodses;
    private List<String> goodsTypeList = new ArrayList<>();
    private List<GoodsClass> goodsClassList = new ArrayList<>();

    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr){
        pd.setVisibility(View.VISIBLE);
        mNetwork.sendGoodsList(c_id, page,1+"", keywords, type, filter_attr, this, new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        if (null == goodsAllAttrs)
                            goodsAllAttrs = resp.getGoodsAllAttrs();
                        if (goodsTypeList.isEmpty())
                            for (GoodsAttr goodsAttr : goodsAllAttrs.get(0).getGoodsAttrs()) {
                                goodsTypeList.add(goodsAttr.getAttr_value());
                            }
                        mListViewAdapter.setData(goodsTypeList);
                        mListViewAdapter.notifyDataSetChanged();

                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Goods goods : goodses) {
                            names.add(goods.getName());
                            paths.add(goods.getImg_url());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setShow(1);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }


    private void callGoodsList02(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getGoodsList(c_id, page,1, keywords, type, filter_attr);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        if (null == goodsAllAttrs)
                            goodsAllAttrs = resp.getGoodsAllAttrs();
                        if (goodsTypeList.isEmpty())
                            for (GoodsAttr goodsAttr : goodsAllAttrs.get(0).getGoodsAttrs()) {
                                goodsTypeList.add(goodsAttr.getAttr_value());
                            }
                        mListViewAdapter.setData(goodsTypeList);
                        mListViewAdapter.notifyDataSetChanged();

                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Goods goods : goodses) {
                            names.add(goods.getName());
                            paths.add(goods.getImg_url());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setShow(1);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
//                    Toast.makeText(ct, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                Toast.makeText(ct, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callGoodsListItem(HDApiService apiService, int c_id, final int page) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getGoodsListItem(c_id, page);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
//                        if (null == goodsAllAttrs)
//                            goodsAllAttrs = resp.getGoodsAllAttrs();
//                        if (goodsTypeList.isEmpty())
//                            for (GoodsAttr goodsAttr : goodsAllAttrs.get(0).getGoodsAttrs()) {
//                                goodsTypeList.add(goodsAttr.getAttr_value());
//                            }
//                        mListViewAdapter.setData(goodsTypeList);
//                        mListViewAdapter.notifyDataSetChanged();

                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Goods goods : goodses) {
                            names.add(goods.getName());
                            paths.add(goods.getImg_url());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setShow(1);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
//                    Toast.makeText(ct, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                Toast.makeText(ct, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 获取产品列表
     * @param isClick 判断是否是点击按钮触发的事件
     */
    private void callGoodsClass(final boolean isClick){
        Log.v("520it","刷新");
        pd.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String json = OtherApi.getAppGoodsClass();
                Log.i(TAG, json);

               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       goodsClassList=JSON.parseArray(
                               json, GoodsClass.class);
                       if (goodsTypeList.isEmpty())
                           for (GoodsClass goodsClass : goodsClassList) {
                               goodsTypeList.add(goodsClass.getName());
                           }
                       mListViewAdapter.setData(goodsTypeList);
                       mListViewAdapter.notifyDataSetChanged();
                       if(isClick){
                           callGoodsListItem(apiService, goodsClassList.get(0).getId(),1);
                       }
                   }
               });
            }
        }).start();

    }


    private List<SceneAllAttr> sceneAllAttrs;
    private List<Scene> scenes;
    private List<String> sceneSpaceList = new ArrayList<>();

    private void callSceneList(HDApiService apiService, int c_id, final int page, String keywords, String type, String filter_attr) {
        pd.setVisibility(View.VISIBLE);
        mNetwork.sendSceneList(c_id + "", page, keywords, type, filter_attr, this, new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetSceneListResp resp = ParseGetSceneListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        if (null == sceneAllAttrs)
                            sceneAllAttrs = resp.getSceneAllAttrs();
                        if (sceneSpaceList.isEmpty())
                            for (SceneAttr sceneAttr : sceneAllAttrs.get(0).getSceneAttrs()) {
                                sceneSpaceList.add(sceneAttr.getAttr_value());
                            }
                        mListViewAdapter.setData(sceneSpaceList);
                        mListViewAdapter.notifyDataSetChanged();

                        List<Scene> sceneList = resp.getScenes();
                        if (1 == page)
                            scenes = sceneList;
                        else if (null != scenes) {
                            scenes.addAll(sceneList);
                            if (sceneList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        if (displayFirstScene && null != scenes && !scenes.isEmpty()) {//点击产品进来第一次展示背景
                            int position=new Random().nextInt(scenes.size());
                            int ids= Integer.parseInt(scenes.get(position).getId());
                            if(ids>1551){
                                displaySceneBg(Constant.SCENE_URL_2 + scenes.get(position).getPath());
                            }else {
                                displaySceneBg(Constant.SCENE_URL + scenes.get(position).getPath());
                            }
                            displayFirstScene = false;
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Scene scene : scenes) {
                            names.add(scene.getName());
                            paths.add(scene.getPath());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setScene(scenes);
                        mGridViewAdapter.setShow(2);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                ct.page--;
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }


    private void callSceneList02(HDApiService apiService, int c_id, final int page, String keywords, String type, String filter_attr) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getSceneList(c_id, page, keywords, type, filter_attr);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetSceneListResp resp = ParseGetSceneListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        if (null == sceneAllAttrs)
                            sceneAllAttrs = resp.getSceneAllAttrs();
                        if (sceneSpaceList.isEmpty())
                            for (SceneAttr sceneAttr : sceneAllAttrs.get(0).getSceneAttrs()) {
                                sceneSpaceList.add(sceneAttr.getAttr_value());
                            }
                        mListViewAdapter.setData(sceneSpaceList);
                        mListViewAdapter.notifyDataSetChanged();

                        List<Scene> sceneList = resp.getScenes();
                        if (1 == page)
                            scenes = sceneList;
                        else if (null != scenes) {
                            scenes.addAll(sceneList);
                            if (sceneList.isEmpty())
                                Toast.makeText(ct, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }

                        if (displayFirstScene && null != scenes && !scenes.isEmpty()) {//点击产品进来第一次展示背景
                            int position=new Random().nextInt(scenes.size());
                            int ids= Integer.parseInt(scenes.get(position).getId());
                            if(ids>1551){
                                displaySceneBg(Constant.SCENE_URL_2 + scenes.get(position).getPath());
                            }else {
                                displaySceneBg(Constant.SCENE_URL + scenes.get(position).getPath());
                            }
                            displayFirstScene = false;
                        }

                        List<String> names = new ArrayList<>();
                        List<String> paths = new ArrayList<>();
                        for (Scene scene : scenes) {
                            names.add(scene.getName());
                            paths.add(scene.getPath());
                        }
                        mGridViewAdapter.setNames(names);
                        mGridViewAdapter.setPaths(paths);
                        mGridViewAdapter.setScene(scenes);
                        mGridViewAdapter.setShow(2);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
//                    Toast.makeText(ct, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                ct.page--;
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                Toast.makeText(ct, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTabBg(ImageView imageView) {
        mProIv.setBackgroundResource(android.R.color.transparent);
        mSceneIv.setBackgroundResource(android.R.color.transparent);
        mOtherIv.setBackgroundResource(android.R.color.transparent);
        imageView.setBackgroundResource(R.color.colorPrimary);
    }

    private int mLightNumber = -1;// 点出来的灯的编号
    public SparseArray<Goods> mSelectedLightSA = new SparseArray<>();// key为自增编号，value为点出来的灯
    private int leftMargin = 0;

    private void displayCheckedGoods(final Goods goods) {
        if (mSelectedLightSA.size() >= 3) {
            Toast.makeText(DiyActivity.this, "调出灯超数，长按可删除", Toast.LENGTH_LONG).show();
            return;
        }

        imageLoader.loadImage(Constant.PRODUCT_URL + goods.getImg_url() + "!400X400.png", options,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);

                        // 被点击的灯的编号加1
                        mLightNumber++;
                        // 把点击的灯放到集合里
                        mSelectedLightSA.put(mLightNumber, goods);

                        // 设置灯图的ImageView的初始宽高和位置
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                mScreenWidth / 3 * 2 / 3,
                                (mScreenWidth / 3 * 2 / 3 * loadedImage.getHeight()) / loadedImage.getWidth());
                        // 设置灯点击出来的位置
                        if (mSelectedLightSA.size() == 1) {
                            leftMargin = mScreenWidth / 3 * 2 / 3;
                        } else if (mSelectedLightSA.size() == 2) {
                            leftMargin = mScreenWidth / 3 * 2 / 3 * 2;
                        } else if (mSelectedLightSA.size() == 3) {
                            leftMargin = 0;
                        }
                        lp.setMargins(leftMargin, 0, 0, 0);

                        TouchView touchView = new TouchView(ct);
                        touchView.setLayoutParams(lp);
                        touchView.setImageBitmap(loadedImage);// 设置被点击的灯的图片
                        touchView.setmLightCount(mLightNumber);// 设置被点击的灯的编号
                        FrameLayout.LayoutParams newLp = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);
                        FrameLayout newFrameLayout = new FrameLayout(ct);
                        newFrameLayout.setLayoutParams(newLp);
                        newFrameLayout.addView(touchView);
                        mFrameLayout.addView(newFrameLayout);
                        touchView.setContainer(mFrameLayout, newFrameLayout);

                    }

                });
    }

    /**
     * 加载场景背景图
     *
     * @param path 场景img_url
     */
    private void displaySceneBg(String path) {
        screePath=path;
        imageLoader.displayImage(path, mSceneBgIv, options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        pd2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        pd2.setVisibility(View.GONE);
                        Toast.makeText(DiyActivity.this, failReason.getCause() + "请重试！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        pd2.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        pd2.setVisibility(View.GONE);
                    }
                });
    }

    private void initImageLoader() {
        options = new DisplayImageOptions.Builder()
                // 设置图片下载期间显示的图片
                .showImageOnLoading(R.mipmap.bg_default)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.mipmap.bg_default)
                // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnFail(R.mipmap.bg_default)
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(false)
                //设置图片的质量
                .bitmapConfig(Bitmap.Config.RGB_565)
                // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                // 是否考虑JPEG图像EXIF参数（旋转，翻转）
//                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                // 图片加载好后渐入的动画时间
                // .displayer(new FadeInBitmapDisplayer(100))
                .build(); // 构建完成

        // 得到ImageLoader的实例(使用的单例模式)
        imageLoader = ImageLoader.getInstance();
    }

    /**
     * 拍照获取相片
     **/
    private void takePhoto() {
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        photoName = format.format(date);
        cameraPath = FileUtil.getOwnFilesDir(this, Constant.CAMERA_PATH);

        Uri imageUri = Uri.fromFile(new File(cameraPath, photoName + ".jpg"));
        System.out.println("imageUri" + imageUri.toString());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机
        // 指定照片保存路径（SD卡）
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, PHOTO_WITH_CAMERA); // 用户点击了从相机获取
    }

    /**
     * 从相册获取图片
     **/
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*"); // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); // 使用Intent.ACTION_PICK这个Action则是直接打开系统图库

        startActivityForResult(intent, PHOTO_WITH_DATA); // 取得相片后返回到本画面
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) { // 返回成功
            switch (requestCode) {
                case PHOTO_WITH_CAMERA: {// 拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡

                        File imageFile = new File(cameraPath, photoName + ".jpg");

                        if (imageFile.exists()) {
                            String imagePath = "file://" + imageFile.toString();

                            displaySceneBg(imagePath);
                        } else {
                            Toast.makeText(this, "读取图片失败！", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(this, "没有SD卡", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case PHOTO_WITH_DATA: // 从图库中选择图片
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    displaySceneBg(originalUri.toString());
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void back(View v) {
        finish();
    }

    public void close(View v) {
        mDiyContainerRl.setVisibility(View.INVISIBLE);
        isFullScreen = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mFrameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private int mScreenWidth;
    private ProgressBar pd, pd2;
    private ImageView mSceneBgIv,mGocarIv;
    private FrameLayout mFrameLayout, mDiyGridViewContainer;
    private RelativeLayout mDiyContainerRl, mDiyMenuContainerRl, mOtherRl;
    private LinearLayout mDiyTabLl;
    public ImageView mProIv, mSceneIv, mOtherIv, mCameraIv, mAlbumIv, mShareIv;
    private ListView mListView;
    private DiyListViewAdapter mListViewAdapter;
    private GridView mGridView;
    private DiyGridViewAdapter mGridViewAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

    private void initView() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mFrameLayout = (FrameLayout) findViewById(R.id.sceneFrameLayout);
//        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();
//        flp.width = mScreenWidth;
//        flp.height = (int) (mScreenWidth / 4.0f * 3.0f);
//        mFrameLayout.setLayoutParams(flp);
        mFrameLayout.setOnClickListener(ct);

        pd = (ProgressBar) findViewById(R.id.pd);
        mSceneBgIv = (ImageView) findViewById(R.id.sceneBgIv);
        mGocarIv = (ImageView)findViewById(R.id.gocarIv);
        mDiyContainerRl = (RelativeLayout) findViewById(R.id.diyContainerRl);

        // 三分之一屏幕的RelativeLayout
        mDiyMenuContainerRl = (RelativeLayout) findViewById(R.id.diyMenuContainerRl);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mDiyMenuContainerRl.getLayoutParams();
        lp.width = (int) (mScreenWidth / 3.0f);
        mDiyMenuContainerRl.setLayoutParams(lp);

        mDiyTabLl = (LinearLayout) findViewById(R.id.diyTabLl);
        lp = (RelativeLayout.LayoutParams) mDiyTabLl.getLayoutParams();
        lp.height = (int) (80.0f / 1200.0f * mScreenWidth);
        mDiyTabLl.setLayoutParams(lp);

        mProIv = (ImageView) findViewById(R.id.diyProIv);
        mSceneIv = (ImageView) findViewById(R.id.diySceneIv);
        mOtherIv = (ImageView) findViewById(R.id.diyOtherIv);
        mCameraIv = (ImageView) findViewById(R.id.cameraIv);
        mAlbumIv = (ImageView) findViewById(R.id.albumIv);
        mShareIv = (ImageView) findViewById(R.id.diyShareIv);
        mProIv.setOnClickListener(this);
        mSceneIv.setOnClickListener(this);
        mOtherIv.setOnClickListener(this);
        mCameraIv.setOnClickListener(this);
        mAlbumIv.setOnClickListener(this);
        mShareIv.setOnClickListener(this);
        mGocarIv.setOnClickListener(this);
        apiService = HDRetrofit.create(HDApiService.class);

        // ListView
        mListView = (ListView) findViewById(R.id.listView);
        mListViewAdapter = new DiyListViewAdapter(ct);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
        mOtherRl = (RelativeLayout) findViewById(R.id.otherRl);

        // 包裹GridView的FrameLayout
        mDiyGridViewContainer = (FrameLayout) findViewById(R.id.diyGridViewContainer);
        lp = (RelativeLayout.LayoutParams) mDiyGridViewContainer.getLayoutParams();
        lp.width = (int) (mScreenWidth / 3f / 4f * 3f);
        mDiyGridViewContainer.setPadding((int) (8.0f / 1200.0f * mScreenWidth),
                (int) (8.0f / 1200.0f * mScreenWidth),
                (int) (8.0f / 1200.0f * mScreenWidth),
                (int) (8.0f / 1200.0f * mScreenWidth));
        mDiyGridViewContainer.setLayoutParams(lp);

        // GridView
        mGridView = (GridView) findViewById(R.id.diyGridView);
        mGridView.setVerticalSpacing((int) (8.0f / 1200.0f * mScreenWidth));
        mGridViewAdapter = new DiyGridViewAdapter(ct);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(this);

        pd2 = (ProgressBar) findViewById(R.id.pd2);

        mPullToRefreshLayout = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
        mPullToRefreshLayout.setOnRefreshListener(this);

    }

//    上传图片

    private  int TIME_OUT = 10*1000;   //超时时间
    private  String CHARSET = "utf-8"; //设置编码
    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @param RequestURL  请求的rul
     * @return  返回响应的内容
     */
    private String uploadFile(Bitmap file, String RequestURL, Map<String, String> param,String imageName){
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        // 显示进度框
        //      showProgressDialog();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file!=null){
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                String params = "";
                if (param != null && param.size() > 0) {
                    Iterator<String> it = param.keySet().iterator();
                    while (it.hasNext()) {
                        sb = null;
                        sb = new StringBuffer();
                        String key = it.next();
                        String value = param.get(key);
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                        sb.append(value).append(LINE_END);
                        params = sb.toString();
                        dos.write(params.getBytes());
                    }
                }
                sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"").append("file").append("\"")
                        .append(";filename=\"").append(imageName).append("\"\n");
                sb.append("Content-Type: image/png");
                sb.append(LINE_END).append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = ImageUtil.Bitmap2InputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1){
                    dos.write(bytes, 0, len);
                }


                is.close();
//                dos.write(file);
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);

                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */

                int res = conn.getResponseCode();
                System.out.println("res========="+res);
                if(res==200){
                    InputStream input =  conn.getInputStream();
                    StringBuffer sb1= new StringBuffer();
                    int ss ;
                    while((ss=input.read())!=-1){
                        sb1.append((char)ss);
                    }
                    result = sb1.toString();
                }
                else{
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
//



}
