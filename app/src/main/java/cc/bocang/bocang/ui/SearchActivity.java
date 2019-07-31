package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.parser.ParseGetGoodsListResp;
import cc.bocang.bocang.data.response.GetGoodsListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.utils.ConvertUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Jun
 * @time 2016/10/18  11:52
 * @desc 搜索产品界面
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private Button mTopLeftBtn;
    private EditText mEtSsearch;
    private Button mTopRightBtn;
    private GridView mProductGridView;
    private int page = 1;
    private List<GoodsAllAttr> goodsAllAttrs;
    private List<Goods> goodses;
    private PullToRefreshLayout mPullToRefreshLayout;
    private HDApiService mApiService;
    private String mKeywords;
    private MyAdapter  mAdapter;
    private int mScreenWidth;
    private ProgressBar pd;
    private int  mOkcatId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));
    }

    private void initData() {
        Intent intent=getIntent();
        mOkcatId=intent.getIntExtra("okcat_id",0);
    }

    /**
     * 异步获取数据
     * @param apiService Retrofit对象
     * @param c_id  用户编号
     * @param page   页数
     * @param keywords  关键字
     * @param initFilterDropDownView
     */
    private void callGoodsList(HDApiService apiService, int c_id, final int page, String keywords, final boolean initFilterDropDownView) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getSearchGoodsList(c_id, page,mOkcatId, keywords);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                pd.setVisibility(View.GONE);
               //下拉刷新数据
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response.body().string();
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        goodsAllAttrs = resp.getGoodsAllAttrs();
                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                tip("没有更多内容了");
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    tip("数据异常...");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                pd.setVisibility(View.GONE);
                SearchActivity.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                tip("无法连接服务器...");
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setContentView(R.layout.activity_search);
        mTopLeftBtn = (Button)findViewById(R.id.topLeftBtn);
        mEtSsearch = (EditText)findViewById(R.id.et_search);
        mTopRightBtn = (Button)findViewById(R.id.topRightBtn);
        mProductGridView = (GridView) findViewById(R.id.priductGridView);
        mPullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.mFilterContentView);
        pd = (ProgressBar)findViewById(R.id.pd);
        mApiService = HDRetrofit.create(HDApiService.class);
        //获取到屏幕宽度
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mAdapter = new MyAdapter();
        //绑定适配器
        mProductGridView.setAdapter(mAdapter);
        mTopRightBtn.setOnClickListener(this);
        mTopLeftBtn.setOnClickListener(this);
        mPullToRefreshLayout.setOnRefreshListener(this);
        mProductGridView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topLeftBtn://返回
                finish();
                break;
            case R.id.topRightBtn://查询
                mKeywords=mEtSsearch.getText().toString();
                if(TextUtils.isEmpty(mKeywords)){
                    tip("请输入搜索的产品");
                    return;
                }
                callGoodsList(mApiService, 0, page, mKeywords, true);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        callGoodsList(mApiService, 0, page, mKeywords, false);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        callGoodsList(mApiService, 0, ++page, mKeywords, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mProductGridView) {
            if (Constant.isDebug)
                tip("点击：" + position);
            Intent intent = new Intent(SearchActivity.this, ProDetailActivity.class);
            intent.putExtra("id", goodses.get(position).getId());
            startActivity(intent);

        }
    }

    /**
     * 适配器类
     */
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
            if (null == goodses)
                return 0;
            return goodses.size();
        }

        @Override
        public Object getItem(int position) {
            if (null == goodses)
                return null;
            return goodses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(SearchActivity.this, R.layout.item_gridview_fm_product, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(SearchActivity.this, 45.8f)) / 2;
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(goodses.get(position).getName());

            imageLoader.displayImage(Constant.PRODUCT_URL + goodses.get(position).getImg_url() + "!400X400.png", holder.imageView, options);

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }
}
