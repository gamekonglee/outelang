package cc.bocang.bocang.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.lib.common.hxp.global.UserSp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.broadcast.Broad;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.dao.CartDao;
import cc.bocang.bocang.data.dao.CollectDao;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsGallery;
import cc.bocang.bocang.data.model.GoodsPro;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.data.parser.ParseGetGoodsInfoResp;
import cc.bocang.bocang.data.response.GetGoodsInfoResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.utils.ShareUtil;
import cc.bocang.bocang.utils.StringUtil;
import cc.bocang.bocang.utils.UIUtils;
import it.sephiroth.android.library.picasso.Picasso;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static cc.bocang.bocang.R.id.webView;

public class ProDetailActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = ProDetailActivity.class.getSimpleName();
    private ProDetailActivity ct = this;
    private HDApiService apiService;

    private Goods goods;
    private boolean collected;
    private int id = -1;
    private String mImgUrl;
    private UserInfo mInfo;
    private String mGoodsname;
    public int IMAGE = 0X001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_detail);
        initView();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        mImgUrl = intent.getStringExtra("imgurl");
        mGoodsname = intent.getStringExtra("goodsname");
        CollectDao dao = new CollectDao(ct);
        collected = dao.isExist(id);
        if (collected)
            mCollectIv.setImageResource(R.mipmap.ic_collect_press);
        else
            mCollectIv.setImageResource(R.mipmap.ic_collect_normal);

        apiService = HDRetrofit.create(HDApiService.class);
        callGoodsInfo(apiService, id);
        mInfo = ((MyApplication) getApplication()).mUserInfo;
        //沉浸式状态栏
        setColor(this, getResources().getColor(R.color.colorPrimary));
    }


    @Override
    public void onClick(View view) {
        if (view == mCallLl) {
            UserSp userSp = new UserSp(ct);
            String phoneNumber = userSp.getString(userSp.getSP_ZHU_PHONE(), null);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);

        } else if (view == mCollectLl) {
            if (collected) {
                collected = false;
                CollectDao dao = new CollectDao(ct);
                dao.deleteOne(id);
                mCollectIv.setImageResource(R.mipmap.ic_collect_normal);
            } else {
                collected = true;
                CollectDao dao = new CollectDao(ct);
                dao.replaceOne(goods);
                mCollectIv.setImageResource(R.mipmap.ic_collect_press);
            }

        } else if (view == mToCartBtn) {
            CartDao dao = new CartDao(ct);
            if (-1 != dao.replaceOne(goods)) {
                Toast.makeText(ct, "已添加到购物车", Toast.LENGTH_LONG).show();
                Broad.sendLocalBroadcast(ct, Broad.CART_CHANGE_ACTION, null);//发送广播
            }
        } else if (view == mToDiyBtn) {
            Intent intent = new Intent(ct, DiyActivity.class);
            intent.putExtra("from", "goods");
            intent.putExtra("goods", goods);
            startActivity(intent);
        } else if (view == mShareBtn) {//分享按钮
            if(mInfo==null){
                showShare(id+"", "");
            }else {
                showShare(id+"", mInfo.getPhone());
            }

        }
    }


//    /**
//     * 分享操作
//     */
//    private void showShare(final int id, final String phone) {
//        if (id == -1) {
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
//        oks.setTitle(mGoodsname);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(Constant.SHAREPRODUCT + "id=" + id + "&phone=" + phone);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(mGoodsname);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(Constant.SHAREPRODUCT + "id=" + id + "&phone=" + phone);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(mGoodsname);
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(Constant.SHAREPRODUCT + "id=" + id + "&phone=" + phone);
//        //图片地址
//        mImgUrl = Constant.PRODUCT_URL + mImgUrl + "!400X400.png";
//        Log.v("520it", "'分享:" + mImgUrl);
//        Log.v("520it", "产品地址:" + Constant.SHAREPRODUCT + "id=" + id + "&phone=" + phone);
//        oks.setImageUrl(mImgUrl);
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
//                    paramsToShare.setText("分享文本 " + Constant.SHAREPRODUCT + "id=" + id + "&phone=" + phone);
//                }
//                if ("Wechat".equals(platform.getName())) {
//                    ImageView img = new ImageView(ProDetailActivity.this);
//                    Picasso.with(ProDetailActivity.this).load(mImgUrl).into(img);
//                    //                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ssdk_logo);
//                    paramsToShare.setImageData(img.getDrawingCache());
//
//
//                }
//                if ("WechatMoments".equals(platform.getName())) {
//
//                    ImageView img = new ImageView(ProDetailActivity.this);
//                    Picasso.with(ProDetailActivity.this).load(mImgUrl).into(img);
//                    //                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ssdk_logo);
//                    paramsToShare.setImageData(img.getDrawingCache());
//                }
//
//            }
//        });
//
//        // 启动分享GUI
//        oks.show(this);
//    }
    /**
     * 分享操作
     */
    private void showShare(final String id,final String phone) {

        if(TextUtils.isEmpty(id)){
            return;
        }
        final Dialog dialog= UIUtils.showBottomInDialog(this,R.layout.share_dialog,UIUtils.dip2PX(150));
        TextView tv_cancel= (TextView) dialog.findViewById(R.id.tv_cancel);
        LinearLayout ll_wx= (LinearLayout) dialog.findViewById(R.id.ll_wx);
        LinearLayout ll_pyq= (LinearLayout) dialog.findViewById(R.id.ll_pyq);
        LinearLayout ll_qq= (LinearLayout) dialog.findViewById(R.id.ll_qq);
//        final String mGoodsname="来自"+getString(R.string.app_name)+"的分享";
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final String url=Constant.SHAREPRODUCT + "id=" + id + "&phone=" + phone;
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWx(ProDetailActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.sharePyq(ProDetailActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mImgUrl.contains("!400")) mImgUrl = Constant.PRODUCT_URL + mImgUrl + "!400X400.png";
                ShareUtil.shareQQ(ProDetailActivity.this, mGoodsname, url,mImgUrl);
                dialog.dismiss();
            }
        });
    }
    private void callGoodsInfo(HDApiService apiService, int id) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getGoodsInfo(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetGoodsInfoResp resp = ParseGetGoodsInfoResp.parse(json);
                    if (null != resp && resp.isSuccess()) {

                        goods = resp.getGoods();

                        setGallery();
                        setOthers();
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
                //                Toast.makeText(ct, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOthers() {
        mProNameTv.setText(goods.getName());
//        Log.v("520it", "mInfo.getMultiple()" + mInfo.getMultiple());
//        Log.v("520it", "mInfo.getInvite_code()" + mInfo.getInvite_code());
        if (mInfo!=null&&!TextUtils.isEmpty(mInfo.getMultiple()) && !mInfo.getMultiple().equals("null") && !TextUtils.isEmpty(mInfo.getInvite_code())) {

            mProPriceTv.setText("￥" + goods.getShop_price() * Double.parseDouble((mInfo.getMultiple())));
        } else {
            mProPriceTv.setText("￥" + goods.getShop_price());
        }
        mTextView.setText("【规格参数】");
        List<String> data = new ArrayList<>();
        for (GoodsPro goodsPro : goods.getGoodsPros()) {
            if (!TextUtils.isEmpty(goodsPro.getValue())) {
                StringBuffer sb = new StringBuffer();
                sb.append(goodsPro.getName());
                sb.append("：");
                sb.append(goodsPro.getValue());
                data.add(sb.toString());
            }

        }
        //        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_productdetail);
        ProductAdapter adapter = new ProductAdapter(this);
        //        adapter.addAll(data);
        mListView.setAdapter(adapter);

        adapter.setData(data);
        adapter.notifyDataSetChanged();

        String html = goods.getGoods_desc();
        html = html.replace("<img src=\"", "<img src=\"" + Constant.BASE_URL);
        html = "<meta name=\"viewport\" content=\"width=device-width\">" + html;
        mWebView.loadData(html, "text/html; charset=UTF-8", null);//这种写法可以正确解析中文
    }

    class ProductAdapter extends BaseAdapter {
        List<String> mdata;
        private LayoutInflater mInflater;
        private Context mContext;

        public ProductAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        public void setData(List<String> data) {
            this.mdata = data;
        }

        @Override
        public int getCount() {
            return mdata != null ? mdata.size() : 0;
        }

        @Override
        public String getItem(int position) {
            if (null == mdata)
                return null;
            return mdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_productdetail, null);

                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.tv_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(mdata.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView tv;
        }
    }


    private void setGallery() {
        List<GoodsGallery> goodsGalleryList = goods.getGoodsGalleries();
        List<String> imgUrl = new ArrayList<>();
        if (goodsGalleryList.isEmpty()) {
            imgUrl.add(goods.getImg_url());
        } else {
            for (GoodsGallery gallery : goodsGalleryList) {
                imgUrl.add(gallery.getImg_url());
            }
        }
        List<String> paths = StringUtil.preToStringArray(Constant.PRODUCT_URL, imgUrl);

        mConvenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, paths);
    }

    class NetworkImageHolderView implements CBPageAdapter.Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            // 你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            imageView.setImageResource(R.mipmap.bg_default);
            ImageLoader.getInstance().displayImage(data, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.isDebug)
                        Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void goBack(View v) {
        finish();
    }

    private ProgressBar pd;
    private ConvenientBanner mConvenientBanner;
    private TextView mProNameTv, mProPriceTv, mTextView;
    private ListView mListView;
    private WebView mWebView;
    private LinearLayout mCallLl, mCollectLl;
    private ImageView mCollectIv;
    private Button mToCartBtn, mToDiyBtn, mShareBtn;

    private void initView() {
        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
        sv.smoothScrollTo(0, 0);

        pd = (ProgressBar) findViewById(R.id.pd);

        mCallLl = (LinearLayout) findViewById(R.id.callLl);
        mCollectLl = (LinearLayout) findViewById(R.id.collectLl);
        mCollectIv = (ImageView) findViewById(R.id.collectIv);
        mToCartBtn = (Button) findViewById(R.id.toCartBtn);
        mToDiyBtn = (Button) findViewById(R.id.toDiyBtn);
        mShareBtn = (Button) findViewById(R.id.sharebtn);
        mCallLl.setOnClickListener(this);
        mCollectLl.setOnClickListener(this);
        mToCartBtn.setOnClickListener(this);
        mToDiyBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);

        mConvenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        rlp.width = mScreenWidth;
        rlp.height = mScreenWidth;
        mConvenientBanner.setLayoutParams(rlp);

        mProNameTv = (TextView) findViewById(R.id.proNameTv);
        mProPriceTv = (TextView) findViewById(R.id.proPriceTv);

        mTextView = (TextView) findViewById(R.id.textView);
        mListView = (ListView) findViewById(R.id.listView);

        mWebView = (WebView) findViewById(webView);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
    }
}
