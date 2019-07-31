package cc.bocang.bocang.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.okhttp.ResponseBody;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.dao.CartDao;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.Product;
import cc.bocang.bocang.data.model.Result;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.IntentKeys;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.utils.ShareUtil;
import cc.bocang.bocang.utils.UIUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Jun
 * @time 2016/10/19  11:28
 * @desc 提交订单
 */
public class OrderActivity extends BaseActivity implements View.OnClickListener {
    private double mShopCarTotalPrice;
    private ArrayList<Goods> mShopCarBeans;
    private HDApiService mApiService;
    private MyAdapter mAdapter;
    private UserInfo mInfo;
    private MyAdapter myAdapter;
    private int mShopCarTotalCount;
    private HDApiService apiService;
    private List<Product> mProducts;
    private String mOrderDiscount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        //沉浸式状态栏
        setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        Serializable serializableExtra = intent.getSerializableExtra(IntentKeys.SHOPCARDATAS);
        mShopCarTotalPrice = intent.getDoubleExtra(IntentKeys.SHOPCARTOTALPRICE, 0);//订单总金额
        mShopCarTotalCount = intent.getIntExtra(IntentKeys.SHOPCARTOTALCOUNT, 0);//订单总数量
        mOrderDiscount = TextUtils.isEmpty(intent.getStringExtra(IntentKeys.SHOPORDERDISCOUNT)) ? "100" :
                intent.getStringExtra(IntentKeys.SHOPORDERDISCOUNT);//订单总折扣
        if (serializableExtra != null || mShopCarTotalCount >= 0) {
            mShopCarBeans = (ArrayList<Goods>) serializableExtra;
            Log.v("520it", "mShopCarBeans" + mShopCarBeans.toString() + "");
        } else {
            finish();
        }
        getReceiverAdress();
        mMoneytv.setText(mShopCarTotalPrice + "");
        mProductnumtv.setText(mShopCarTotalCount + "");
        if (!TextUtils.isEmpty(mOrderDiscount))
            mSumAge.setText(mOrderDiscount + "%");
    }

    /**
     * 显示收货地址信息
     */
    private void getReceiverAdress() {
        //获取到用户数据
        mInfo = ((MyApplication) getApplication()).mUserInfo;
        if (mInfo == null)
            return;
        Log.v("520", mInfo.toString());
        mConsigneetv.setText("收货人：" + mInfo.getName());
        mPhonetv.setText("  电话：" + mInfo.getPhone());
        mAdresstv.setText(mInfo.getAddress());
    }

    /**
     * 提交订单
     *
     * @param apiService
     * @param order
     * @param product
     */
    private void submitOrder(HDApiService apiService, String order, String product) {
        Call<ResponseBody> call = apiService.submitOrder(order, product);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == OrderActivity.this || OrderActivity.this.isFinishing())
                    return;

                try {
                    String json = response.body().string();
                    //                    Result result= GetResult.parse(json);
                    Log.v("520it", "result.getResult()=" + json);
                    Result result = JSON.parseObject(json, Result.class);
                    Log.v("520it", "result.getResult()=" + result.getResult());
                    if (result.getResult().equals("0")) {
                        tip("订单提交失败!");
                    } else {
                        showShare(result.getResult(),"");
                        //清除购物车数据
                        deleteCarShop();

                    }
                } catch (Exception e) {
                    if (null != OrderActivity.this && !OrderActivity.this.isFinishing())
                        tip("订单提交失败!");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == OrderActivity.this || OrderActivity.this.isFinishing())
                    return;
                tip("订单提交失败");
            }
        });
    }

    /**
     * 发送消息清除购物车
     */
    private void deleteCarShop() {
        CartDao dao = new CartDao(OrderActivity.this);
        boolean isfinsh = false;
        for (int i = 0; i < mShopCarBeans.size(); i++) {
            if (-1 != dao.deleteOne(mShopCarBeans.get(i).getId())) {
                isfinsh = true;
            } else {
                isfinsh = false;
            }
        }
        if (isfinsh == true) {
            EventBus.getDefault().post(IntentKeys.ORDERFINISH);
        }
    }

    private TextView mConsigneetv, mProductnumtv, mSumAge, mAdresstv, mPhonetv, mShare_tv, mMoneytv;
    private Button mTopLeftBtn;
    private ListView mListView;

    /**
     * 初始化控件
     */
    private void initView() {
        setContentView(R.layout.activity_order);
        mConsigneetv = (TextView) findViewById(R.id.consigneetv);
        mPhonetv = (TextView) findViewById(R.id.phonetv);
        mListView = (ListView) findViewById(R.id.listView);
        mAdresstv = (TextView) findViewById(R.id.adresstv);
        mMoneytv = (TextView) findViewById(R.id.money_tv);
        mProductnumtv = (TextView) findViewById(R.id.productnum_tv);
        mTopLeftBtn = (Button) findViewById(R.id.topLeftBtn);
        mTopLeftBtn.setOnClickListener(this);
        mShare_tv = (TextView) findViewById(R.id.share_tv);
        mShare_tv.setOnClickListener(this);
        apiService = HDRetrofit.create(HDApiService.class);
        myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);
        mSumAge = (TextView) findViewById(R.id.sumAge);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topLeftBtn:
                finish();
                break;
            case R.id.share_tv:
                getSubmitOrder();
                break;
        }
    }
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
        final String url=Constant.SHAREICON+id;
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWx(OrderActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.sharePyq(OrderActivity.this, mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareQQ(OrderActivity.this, mGoodsname, url,sceenpath);
                dialog.dismiss();
            }
        });
    }

    private void getSubmitOrder() {
        //TODO提交订单分享订单
        //订单信息
        String orderJson = "";
        if (IntentKeys.ISAGIO) {
            orderJson = "{   \"order_name\" : \"" + mInfo.getName() + "\",   \"order_sum" +
                    "\" : \"" + mShopCarTotalPrice + "\",   \"order_phone\" : \"" + mInfo.getPhone() + "\",   \"delivery_time\" : \"\"" +
                    ",   \"user_id\" : \"" + mInfo.getId() + "\",   \"order_address\" : \"" +
                    mInfo.getAddress() + "\",   \"order_discount\" : \"" + mOrderDiscount + "\" }";

        } else {
            orderJson = "{   \"order_name\" : \"" + mInfo.getName() + "\",   \"order_sum" +
                    "\" : \"" + mShopCarTotalPrice + "\",   \"order_phone\" : \"" + mInfo.getPhone() + "\",   \"delivery_time\" : \"\"" +
                    ",   \"user_id\" : \"" + mInfo.getId() + "\",   \"order_address\" : \"" + mInfo.getAddress() + "\"}";
        }

        StringBuffer productJson = new StringBuffer();
        productJson.append("[\n");


        if (IntentKeys.ISAGIO) {
            //订单商品信息
            for (int i = 0; i < mShopCarBeans.size(); i++) {
                productJson.append("  \"{\\n  \\\"msg\\\" : \\\"\\\",");
                productJson.append("\\n  \\\"goodsPath\\\" : \\\"" + Constant.PRODUCT_URL + mShopCarBeans.get(i).getImg_url() + "\\\",");
                productJson.append("\\n  \\\"goods_id\\\" : \\\"" + mShopCarBeans.get(i).getId() + "\\\",");
                productJson.append("\\n  \\\"goods_name\\\" : \\\"" + mShopCarBeans.get(i).getName() + "\\\",");
                if (mShopCarBeans.get(i).getAgio() == 0) {
                    productJson.append("\\n  \\\"discount\\\" : \\\"" + 100 + "\\\",");
                } else {
                    productJson.append("\\n  \\\"discount\\\" : \\\"" + mShopCarBeans.get(i).getAgio() * 100 + "\\\",");
                }

                if (!TextUtils.isEmpty(mInfo.getMultiple()) && !mInfo.getMultiple().equals("null") && !TextUtils.isEmpty(mInfo.getInvite_code())) {
                    productJson.append("\\n  \\\"goods_price\\\" : \\\"" +
                            mShopCarBeans.get(i).getShop_price() * Double.parseDouble(mInfo.getMultiple()) + "\\\",");
                } else {
                    productJson.append("\\n  \\\"goods_price\\\" : \\\"" +
                            mShopCarBeans.get(i).getShop_price() + "\\\",");
                }
                productJson.append("\\n  \\\"number\\\" : \\\"" + mShopCarBeans.get(i).getBuyCount() + "\\\"");
                if (i == mShopCarBeans.size() - 1) {
                    productJson.append("\\n}\"\n");
                } else {
                    productJson.append("\\n}\",\n");
                }

            }
        } else {
            //订单商品信息
            for (int i = 0; i < mShopCarBeans.size(); i++) {
                productJson.append("  \"{\\n  \\\"msg\\\" : \\\"\\\",");
                productJson.append("\\n  \\\"goodsPath\\\" : \\\"" + Constant.PRODUCT_URL + mShopCarBeans.get(i).getImg_url() + "\\\",");
                productJson.append("\\n  \\\"goods_id\\\" : \\\"" + mShopCarBeans.get(i).getId() + "\\\",");
                productJson.append("\\n  \\\"goods_name\\\" : \\\"" + mShopCarBeans.get(i).getName() + "\\\",");
                productJson.append("\\n  \\\"goods_price\\\" : \\\"" +
                        mShopCarBeans.get(i).getShop_price() + "\\\",");
                productJson.append("\\n  \\\"number\\\" : \\\"" + mShopCarBeans.get(i).getBuyCount() + "\\\"");
                if (i == mShopCarBeans.size() - 1) {
                    productJson.append("\\n}\"\n");
                } else {
                    productJson.append("\\n}\",\n");
                }

            }
        }

        productJson.append("]");
        Log.v("520", "xx:" + productJson.toString());
        submitOrder(apiService, orderJson, productJson.toString());
    }

    private class MyAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        public MyAdapter() {
            options = new DisplayImageOptions.Builder()
                    // 设置图片下载期间显示的图片
                    .showImageOnLoading(R.mipmap.bg_default)
                            // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageForEmptyUri(R.mipmap.bg_default)
                            // 设置图片加载或解码过程中发生错误显示的图片
                            // .showImageOnFail(R.drawable.ic_error)
                            // 设置下载的图片是否缓存在内存中
                    .cacheInMemory(true)
                            // 设置下载的图片是否缓存在SD卡中
                    .cacheOnDisk(true)
                            // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                            // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                            // .displayer(new FadeInBitmapDisplayer(100))//
                            // 图片加载好后渐入的动画时间
                    .build(); // 构建完成

            // 得到ImageLoader的实例(使用的单例模式)
            imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            if (null == mShopCarBeans)
                return 0;
            return mShopCarBeans.size();
        }

        @Override
        public Goods getItem(int position) {
            if (null == mShopCarBeans)
                return null;
            return mShopCarBeans.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(OrderActivity.this, R.layout.item_order, null);

                holder = new ViewHolder();
                holder.productIv = (ImageView) convertView.findViewById(R.id.product_iv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.pricTv = (TextView) convertView.findViewById(R.id.price_tv);
                holder.countTv = (TextView) convertView.findViewById(R.id.count_tv);
                holder.agiotv = (TextView) convertView.findViewById(R.id.agiotv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //绑定数据
            imageLoader.displayImage(Constant.PRODUCT_URL + mShopCarBeans.get(position).getImg_url() +
                    "!280X280.png", holder.productIv, options);
            holder.nameTv.setText(mShopCarBeans.get(position).getName());
            if (!TextUtils.isEmpty(mInfo.getMultiple()) && !mInfo.getMultiple().equals("null") && !TextUtils.isEmpty(mInfo.getInvite_code())) {
                holder.pricTv.setText("" + mShopCarBeans.get(position).getBuyCount() * mShopCarBeans.get(position).getShop_price() * Double.parseDouble(mInfo.getMultiple()));
            } else {
                holder.pricTv.setText("" + mShopCarBeans.get(position).getBuyCount() * mShopCarBeans.get(position).getShop_price());
            }
            holder.countTv.setText("X" + mShopCarBeans.get(position).getBuyCount() + "");
            if (mShopCarBeans.get(position).getAgio() == 0) {
                holder.agiotv.setText("100%");

            } else {
                holder.agiotv.setText(mShopCarBeans.get(position).getAgio() * 100 + "%");
            }
            return convertView;
        }

        class ViewHolder {
            ImageView productIv;
            TextView nameTv;
            TextView pricTv;
            TextView countTv;
            TextView agiotv, agioName;
        }
    }

}
