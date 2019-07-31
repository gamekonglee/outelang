package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
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
 * @author Administrator
 * @version $Rev$
 * @time 2016/11/9 13:43
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class NewProductActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, OnFilterDoneListener, View.OnClickListener {
    static DropDownMenu dropDownMenu;
    private HDApiService apiService;
    private List<Integer> itemPosList = new ArrayList<>();//有选中值的itemPos列表，长度为3
    private Integer[] goodsIds = new Integer[]{0, 0, 0};
    private String fitterStr;
    private int page = 1;
    private int mScreenWidth;
    private ProgressBar pd;
    private GridView mGridView;
    private MyAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;
    private Button mTopLeftBtn,mTopRightBtn,mCollectBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setContentView(R.layout.activity_new_product);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        pd = (ProgressBar) findViewById(R.id.pd);
        dropDownMenu = (DropDownMenu) findViewById(R.id.dropDownMenu);
        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mGridView.setAdapter(mAdapter);
        mPullToRefreshLayout = ((PullToRefreshLayout) findViewById(R.id.mFilterContentView));
        mPullToRefreshLayout.setOnRefreshListener(NewProductActivity.this);
        mTopLeftBtn = (Button)findViewById(R.id.topLeftBtn);
        mTopRightBtn = (Button)findViewById(R.id.topRightBtn);
        mCollectBtn = (Button)findViewById(R.id.collectBtn);
        mTopLeftBtn.setOnClickListener(this);
        mTopRightBtn.setOnClickListener(this);
        mCollectBtn.setOnClickListener(this);



        int titlePos = IndexActivity.titlePos;
        int goodsId = IndexActivity.goodsId;
        if (IndexActivity.isClickFmHomeItem) {//如果是通过点击首页跳转过来的
            goodsIds[titlePos] = goodsId;
            IndexActivity.isClickFmHomeItem = false;
        }
        fitterStr = goodsIds[0] + "." + goodsIds[1] + "." + goodsIds[2];
        Log.v("520","fitterStr"+fitterStr);
        apiService = HDRetrofit.create(HDApiService.class);
        page = 1;
        callGoodsList(apiService, IndexActivity.mCId, page, null, null, fitterStr, true);
    }
    private List<GoodsAllAttr> goodsAllAttrs;
    private List<Goods> goodses;

    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr, final boolean initFilterDropDownView) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getGoodsList(c_id, page,1, keywords, type, filter_attr);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == NewProductActivity.this || NewProductActivity.this.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response.body().string();
                    Log.i("520it", json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        goodsAllAttrs = resp.getGoodsAllAttrs();
                        if (initFilterDropDownView)//重复setMenuAdapter会报错
                            initFilterDropDownView(goodsAllAttrs);
                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses) {
                            goodses.addAll(goodsList);
                            if (goodsList.isEmpty())
                                Toast.makeText(NewProductActivity.this, "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
//                    Toast.makeText(NewProductActivity.this, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null ==NewProductActivity.this || NewProductActivity.this.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                NewProductActivity.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
//                Toast.makeText(NewProductActivity.this, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFilterDropDownView(List<GoodsAllAttr> goodsAllAttrs) {
        int titlePos = IndexActivity.titlePos;
        int itemPos = IndexActivity.itemPos;
        if (itemPosList.size() < goodsAllAttrs.size()) {
            itemPosList.add(0);
            itemPosList.add(0);
            itemPosList.add(0);
        }
        if (titlePos < itemPosList.size())
            itemPosList.remove(titlePos);
        itemPosList.add(titlePos, itemPos);
        ProDropMenuAdapter dropMenuAdapter = new ProDropMenuAdapter(NewProductActivity.this, goodsAllAttrs, itemPosList, this);
        dropDownMenu.setMenuAdapter(dropMenuAdapter);
    }

    @Override
    public void onFilterDone(int titlePos, int itemPos, String itemStr) {
        dropDownMenu.close();
        if (0 == itemPos)
            itemStr = goodsAllAttrs.get(titlePos).getAttrName();
        dropDownMenu.setPositionIndicatorText(titlePos, itemStr);

        if (titlePos < itemPosList.size())
            itemPosList.remove(titlePos);
        itemPosList.add(titlePos, itemPos);

        int goodsId = goodsAllAttrs.get(titlePos).getGoodsAttrs().get(itemPos).getGoods_id();
        goodsIds[titlePos] = goodsId;
        fitterStr = goodsIds[0] + "." + goodsIds[1] + "." + goodsIds[2];
        page = 1;
        callGoodsList(apiService,IndexActivity.mCId, page, null, null, fitterStr, false);

        if (Constant.isDebug)
            Toast.makeText(NewProductActivity.this, itemStr + " titlePos：" + titlePos + "  itemPos：" + itemPos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mGridView) {
            if (Constant.isDebug)
                Toast.makeText(NewProductActivity.this, "点击：" + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewProductActivity.this, ProDetailActivity.class);
            intent.putExtra("id", goodses.get(position).getId());
            intent.putExtra("imgurl",goodses.get(position).getImg_url());
            intent.putExtra("goodsname",goodses.get(position).getName());
            startActivity(intent);

        }
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        callGoodsList(apiService, IndexActivity.mCId, page, null, null, fitterStr, false);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        callGoodsList(apiService, IndexActivity.mCId, ++page, null, null, fitterStr, false);
    }

    private void getSeachProduct() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("title", "产品搜索");
        intent.putExtra("okcat_id",1);
        intent.putExtra("fm", FmCollect.class.getSimpleName());
        this.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topLeftBtn:
                finish();
            break;
            case R.id.topRightBtn:
                getSeachProduct();
            break;
            case R.id.collectBtn:
                Intent intent = new Intent(this, ContainerActivity.class);
                intent.putExtra("title", "我的收藏");
                intent.putExtra("fm", FmCollect.class.getSimpleName());
                this.startActivity(intent);
            break;
        }

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
            if (null == goodses)
                return 0;
            return goodses.size();
        }

        @Override
        public Goods getItem(int position) {
            if (null == goodses)
                return null;
            return goodses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(NewProductActivity.this, R.layout.item_gridview_fm_product, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(NewProductActivity.this, 45.8f)) / 2;
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
