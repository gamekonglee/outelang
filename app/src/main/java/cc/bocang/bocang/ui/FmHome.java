package cc.bocang.bocang.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.lib.common.hxp.view.PullToRefreshLayout;
import com.lib.common.hxp.view.StatedFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.Ad;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.model.GoodsAttr;
import cc.bocang.bocang.data.parser.ParseGetAdResp;
import cc.bocang.bocang.data.parser.ParseGetGoodsListResp;
import cc.bocang.bocang.data.response.GetAdResp;
import cc.bocang.bocang.data.response.GetGoodsListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.IntentKeys;
import cc.bocang.bocang.utils.ConvertUtil;
import cc.bocang.bocang.utils.ResUtil;
import cc.bocang.bocang.utils.StringUtil;
import cc.bocang.bocang.utils.UniversalUtil;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FmHome extends StatedFragment implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private final String TAG = FmHome.class.getSimpleName();

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constant.isDebug)
            Log.i(TAG, "onCreate...");
    }
    private HDApiService apiService;
    private int page = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Constant.isDebug)
            Log.i(TAG, "onCreateView...");
        View view = initView(inflater, container);
        mNetwork=new Network();
        initList();

        apiService = HDRetrofit.create(HDApiService.class);
        callAd(apiService);
        page = 1;
        callGoodsList(apiService, IndexActivity.mCId, page, null, "is_best", null);

        mTypeTv.performClick();

        return view;
    }

    private void initList() {
        typeList.clear();
        spaceList.clear();
        styleList.clear();
        typeResList.clear();
        spaceResList.clear();
        styleResList.clear();

//        typeList.add("现代家居灯");
//        typeList.add("现代水晶灯");
//        typeList.add("金色水晶灯");
//        typeList.add("欧式灯");
//        typeList.add("中式艺术灯");
//        typeList.add("商业照明灯");
        typeList.add("后现代装饰灯");
        typeList.add("现代家居灯");
        typeList.add("现代艺术灯");
        typeList.add("轻奢水晶灯");
        typeList.add("欧式蜡烛灯");
        typeList.add("中式艺术灯");
        typeList.add("商业照明");
        typeList.add("更多");

        spaceList.add("客厅");
        spaceList.add("餐厅");
        spaceList.add("卧室");
        spaceList.add("书房");
        spaceList.add("卫生间");
        spaceList.add("阳台");
        spaceList.add("玄关/过道");
        spaceList.add("更多");

        styleList.add("现代简约");
        styleList.add("中式");
        styleList.add("新中式");
        styleList.add("欧式");
        styleList.add("美式");
        styleList.add("田园");
        styleList.add("新古典");
        styleList.add("更多");

        for (int i = 0; i < 8; i++) {
            if (i == 7) {
                typeResList.add(ResUtil.getResMipmap(getActivity(), "type_" + i));
                spaceResList.add(ResUtil.getResMipmap(getActivity(), "type_" + i));
                styleResList.add(ResUtil.getResMipmap(getActivity(), "type_" + i));
            } else {
                typeResList.add(ResUtil.getResMipmap(getActivity(), "type_" + i));
                spaceResList.add(ResUtil.getResMipmap(getActivity(), "space_" + i));
                styleResList.add(ResUtil.getResMipmap(getActivity(), "style_" + i));
            }
        }

        //默认选中类型
        nameList = typeList;
        imageResList = typeResList;
        mItemAdapter.notifyDataSetChanged();
    }

    private List<String> nameList;
    private List<Integer> imageResList;

    //下级选项名称列表
    private List<String> typeList = new ArrayList<>();
    private List<String> spaceList = new ArrayList<>();
    private List<String> styleList = new ArrayList<>();
    //对应的按钮图片
    private List<Integer> typeResList = new ArrayList<>();
    private List<Integer> spaceResList = new ArrayList<>();
    private List<Integer> styleResList = new ArrayList<>();
    //对应的筛选值列表
    private List<Integer> typeGoodsIdList = new ArrayList<>();
    private List<Integer> spaceGoodsIdList = new ArrayList<>();
    private List<Integer> styleGoodsIdList = new ArrayList<>();

    private List<Goods> goodses;
    private Network mNetwork;

    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr) {
        //重新获取，先清空列表
        typeGoodsIdList.clear();
        spaceGoodsIdList.clear();
        styleGoodsIdList.clear();
        mNetwork.sendGoodsList(c_id, page, "1", keywords, type, filter_attr, FmHome.this.getActivity(), new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        List<GoodsAllAttr> goodsAllAttrs = resp.getGoodsAllAttrs();
                        for (int i = 0; i < goodsAllAttrs.size(); i++) {
                            GoodsAllAttr goodsAllAttr = goodsAllAttrs.get(i);
                            if (i == 0) {//类型
                                String attrName = goodsAllAttr.getAttrName();
                                mTypeTv.setText(attrName);
                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
                                for (int j = 0; j < goodsAttrs.size(); j++) {
                                    if (j != 0) {//过滤掉“全部”
//                                        if (!goodsAttrs.get(j).getAttr_value().equals("全部")) {//过滤掉“全部”
                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
                                        typeGoodsIdList.add(goodsAttr.getGoods_id());
                                    }
                                }
                            } else if (i == 1) {//空间
                                String attrName = goodsAllAttr.getAttrName();
                                mSpaceTv.setText(attrName);
                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
                                for (int j = 0; j < goodsAttrs.size(); j++) {
                                    if (j != 0) {//过滤掉“全部”
                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
                                        spaceGoodsIdList.add(goodsAttr.getGoods_id());
                                    }
                                }
                            } else if (i == 2) {//风格
                                String attrName = goodsAllAttr.getAttrName();
                                mStyleTv.setText(attrName);
                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
                                for (int j = 0; j < goodsAttrs.size(); j++) {
                                    if (j != 0) {//过滤掉“全部”
                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
                                        styleGoodsIdList.add(goodsAttr.getGoods_id());
                                    }
                                }
                            }
                        }

                        List<Goods> goodsList = resp.getGoodses();
                        if (1 == page)
                            goodses = goodsList;
                        else if (null != goodses){
                            goodses.addAll(goodsList);
                            if(goodsList.isEmpty())
                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mProAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    if (null != getActivity() && !getActivity().isFinishing())
                        e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                FmHome.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });
    }

//    private void callGoodsList(HDApiService apiService, String c_id, final int page, String keywords, String type, String filter_attr) {
//        //重新获取，先清空列表
//        typeGoodsIdList.clear();
//        spaceGoodsIdList.clear();
//        styleGoodsIdList.clear();
//        Call<ResponseBody> call = apiService.getGoodsList(c_id, page,1, null, type, filter_attr);
//        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
//            @Override
//            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
//                try {
//                    String json = response.body().string();
//                    Log.i(TAG, json);
//                    GetGoodsListResp resp = ParseGetGoodsListResp.parse(json);
//                    if (null != resp && resp.isSuccess()) {
//                        List<GoodsAllAttr> goodsAllAttrs = resp.getGoodsAllAttrs();
//                        for (int i = 0; i < goodsAllAttrs.size(); i++) {
//                            GoodsAllAttr goodsAllAttr = goodsAllAttrs.get(i);
//                            if (i == 0) {//类型
//                                String attrName = goodsAllAttr.getAttrName();
//                                mTypeTv.setText(attrName);
//                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
//                                for (int j = 0; j < goodsAttrs.size(); j++) {
//                                    if (j != 0) {//过滤掉“全部”
//                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
//                                        typeGoodsIdList.add(goodsAttr.getGoods_id());
//                                    }
//                                }
//                            } else if (i == 1) {//空间
//                                String attrName = goodsAllAttr.getAttrName();
//                                mSpaceTv.setText(attrName);
//                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
//                                for (int j = 0; j < goodsAttrs.size(); j++) {
//                                    if (j != 0) {//过滤掉“全部”
//                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
//                                        spaceGoodsIdList.add(goodsAttr.getGoods_id());
//                                    }
//                                }
//                            } else if (i == 2) {//风格
//                                String attrName = goodsAllAttr.getAttrName();
//                                mStyleTv.setText(attrName);
//                                List<GoodsAttr> goodsAttrs = goodsAllAttr.getGoodsAttrs();
//                                for (int j = 0; j < goodsAttrs.size(); j++) {
//                                    if (j != 0) {//过滤掉“全部”
//                                        GoodsAttr goodsAttr = goodsAttrs.get(j);
//                                        styleGoodsIdList.add(goodsAttr.getGoods_id());
//                                    }
//                                }
//                            }
//                        }
//
//                        List<Goods> goodsList = resp.getGoodses();
//                        if (1 == page)
//                            goodses = goodsList;
//                        else if (null != goodses){
//                            goodses.addAll(goodsList);
//                            if(goodsList.isEmpty())
//                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
//                        }
//                        mProAdapter.notifyDataSetChanged();
//                    }
//                } catch (Exception e) {
//                    if (null != getActivity() && !getActivity().isFinishing())
////                    Toast.makeText(getActivity(), "数据异常...", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                if (null == getActivity() || getActivity().isFinishing())
//                    return;
//                FmHome.this.page--;
//                if (null != mPullToRefreshLayout) {
//                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
////                if (null != getActivity() && !getActivity().isFinishing())
////                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /**
     * 广告背景
     */
    private void callAd(HDApiService apiService) {
        Call<ResponseBody> call = apiService.getAd(Constant.AD_PARAM);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                try {
                    String json = response.body().string();
                    Log.i(TAG, "广告：" + json);
                    GetAdResp resp = ParseGetAdResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        List<Ad> ads = resp.getBeans();
                        List<String> imgUrl = new ArrayList<>();
                        for (Ad ad : ads) {
                            imgUrl.add(ad.getPath());
                        }
                        List<String> paths = StringUtil.preToStringArray(Constant.AD_URL, imgUrl);

                        mConvenientBanner.setPages(
                                new CBViewHolderCreator<NetworkImageHolderView>() {
                                    @Override
                                    public NetworkImageHolderView createHolder() {
                                        return new NetworkImageHolderView();
                                    }
                                }, paths);
                    }
                } catch (Exception e) {
//                    Toast.makeText(getActivity(), "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
//                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
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
                    // 点击事件
                    if (Constant.isDebug)
                        Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private float mCurrentCheckedRadioLeft;//当前被选中的Button距离左侧的距离
    private TextView mCheckedTv;//当前被选中的tab

    @Override
    public void onClick(View v) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = null;
        reSetTextColor();
        if (v == mTypeTv) {
            mCheckedTv = mTypeTv;
            mTypeTv.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, 0f, -20f, -20f);
            nameList = typeList;
            imageResList = typeResList;
            mItemAdapter.notifyDataSetChanged();
        } else if (v == mSpaceTv) {
            mCheckedTv = mSpaceTv;
            mSpaceTv.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, mScreenWidth / 3f, -20f, -20f);
            nameList = spaceList;
            imageResList = spaceResList;
            mItemAdapter.notifyDataSetChanged();
        } else if (v == mStyleTv) {
            mCheckedTv = mStyleTv;
            mStyleTv.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, mScreenWidth * 2f / 3f, -20f, -20f);
            nameList = styleList;
            imageResList = styleResList;
            mItemAdapter.notifyDataSetChanged();
        }
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillBefore(false);
        animationSet.setFillAfter(true);
        animationSet.setDuration(100);
        mLineIv.startAnimation(animationSet);

        mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft(v);//更新当前横条距离左边的距离
    }

    private void reSetTextColor() {
        if(IntentKeys.ISSPECIALSHOw){
            mTypeTv.setTextColor(Color.WHITE);
            mSpaceTv.setTextColor(Color.WHITE);
            mStyleTv.setTextColor(Color.WHITE);
        }else{
            mTypeTv.setTextColor(Color.GRAY);
            mSpaceTv.setTextColor(Color.GRAY);
            mStyleTv.setTextColor(Color.GRAY);
        }
    }

    /**
     * 获得当前被选中的Button距离左侧的距离
     */
    private float getCurrentCheckedRadioLeft(View v) {
        if (v == mTypeTv) {
            return 0f;
        } else if (v == mSpaceTv) {
            return mScreenWidth / 3f;
        } else if (v == mStyleTv) {
            return mScreenWidth * 2f / 3f;
        }
        return 0f;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mItemGridView) {
            if (Constant.isDebug)
                Toast.makeText(getActivity(), ((TextView) view.findViewById(R.id.textView)).getText(), Toast.LENGTH_SHORT).show();
            if (mCheckedTv == mTypeTv) {
                ((IndexActivity) getActivity()).titlePos = 0;
                ((IndexActivity) getActivity()).itemPos =  position == 7 ? 0 : position + 1;
                ((IndexActivity) getActivity()).goodsId =  position == 7 ? 0 : typeGoodsIdList.get(position);
            } else if (mCheckedTv == mSpaceTv) {
                ((IndexActivity) getActivity()).titlePos = 1;
                ((IndexActivity) getActivity()).itemPos = position >= spaceGoodsIdList.size() - 1 || position == 7 ? 0 : position + 1;
                ((IndexActivity) getActivity()).goodsId = position >= spaceGoodsIdList.size() - 1 || position == 7 ? 0 : spaceGoodsIdList.get(position);
            } else if (mCheckedTv == mStyleTv) {
                ((IndexActivity) getActivity()).titlePos = 2;
                ((IndexActivity) getActivity()).itemPos = position >= styleGoodsIdList.size() - 1 || position == 7 ? 0 : position + 1;
                ((IndexActivity) getActivity()).goodsId = position >= styleGoodsIdList.size() - 1 || position == 7 ? 0 : styleGoodsIdList.get(position);
            }
            ((IndexActivity) getActivity()).isClickFmHomeItem = true;
//            ((IndexActivity) getActivity()).fragmentsUpdateFlag[1] = true;//FmProduct重新onCreate（界面会有短暂的延迟）
            ((IndexActivity) getActivity()).mFragmentPagerAdapter.notifyDataSetChanged();
            ((IndexActivity) getActivity()).mViewPager.setCurrentItem(1, true);
        } else if (parent == mProGridView) {
            if (Constant.isDebug)
                Toast.makeText(getActivity(), "点击产品：" + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ProDetailActivity.class);
            intent.putExtra("id", goodses.get(position).getId());
            intent.putExtra("imgurl",goodses.get(position).getImg_url());
            intent.putExtra("goodsname",goodses.get(position).getName());

            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
    {
        callAd(apiService);
        page = 1;
        callGoodsList(apiService, IndexActivity.mCId, page, null, "is_best", null);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout)
    {
        callGoodsList(apiService, IndexActivity.mCId, ++page, null, "is_best", null);
    }

    private class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (null == nameList)
                return 0;
            return nameList.size();
        }

        @Override
        public String getItem(int position) {
            if (null == nameList)
                return null;
            return nameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_gridview_fm_home, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(nameList.get(position));
            holder.imageView.setImageResource(imageResList.get(position));

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

    private class ProAdapter extends BaseAdapter {
        private DisplayImageOptions options;
        private ImageLoader imageLoader;

        public ProAdapter() {
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
                convertView = View.inflate(getActivity(), R.layout.item_gridview_fm_product, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(getActivity(), 45.8f)) / 2;
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


    @Override
    public void onResume() {
        super.onResume();
        // 开始自动翻页
        mConvenientBanner.startTurning(UniversalUtil.randomA2B(3000, 5000));
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止翻页
        mConvenientBanner.stopTurning();
    }


    private GridView mItemGridView, mProGridView;
    private ItemAdapter mItemAdapter;
    private ProAdapter mProAdapter;
    private ConvenientBanner mConvenientBanner;
    private int mScreenWidth;
    private TextView mTypeTv, mSpaceTv, mStyleTv;
    private ImageView mLineIv;
    private PullToRefreshLayout mPullToRefreshLayout;

    private View initView(LayoutInflater inflater, ViewGroup container) {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        View v = inflater.inflate(R.layout.fm_home, container, false);

        ScrollView sv = (ScrollView) v.findViewById(R.id.scrollView);
        sv.smoothScrollTo(0, 0);

        mConvenientBanner = (ConvenientBanner) v.findViewById(R.id.convenientBanner);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mConvenientBanner.getLayoutParams();
        rlp.width = mScreenWidth;
        rlp.height = (int) (mScreenWidth * (360f / 640f));
        mConvenientBanner.setLayoutParams(rlp);

        RelativeLayout gridViewRl = (RelativeLayout) v.findViewById(R.id.gridViewRl);
        rlp = (RelativeLayout.LayoutParams) gridViewRl.getLayoutParams();
        rlp.height = (int) (mScreenWidth / 4f * 2f + 30);
        gridViewRl.setLayoutParams(rlp);

        mItemGridView = (GridView) v.findViewById(R.id.itemGridView);
        mItemGridView.setOnItemClickListener(this);
        mItemAdapter = new ItemAdapter();
        mItemGridView.setAdapter(mItemAdapter);

        mProGridView = (GridView) v.findViewById(R.id.priductGridView);
        mProGridView.setOnItemClickListener(this);
        mProAdapter = new ProAdapter();
        mProGridView.setAdapter(mProAdapter);

        mTypeTv = (TextView) v.findViewById(R.id.typeTv);
        mSpaceTv = (TextView) v.findViewById(R.id.spaceTv);
        mStyleTv = (TextView) v.findViewById(R.id.styleTv);
        mTypeTv.setOnClickListener(this);
        mSpaceTv.setOnClickListener(this);
        mStyleTv.setOnClickListener(this);
        mLineIv = (ImageView) v.findViewById(R.id.lineIv);
        rlp = (RelativeLayout.LayoutParams) mLineIv.getLayoutParams();
        rlp.width = (int) (mScreenWidth / 3f);
        mLineIv.setLayoutParams(rlp);

        mPullToRefreshLayout = ((PullToRefreshLayout) v.findViewById(R.id.refresh_view));
        mPullToRefreshLayout.setOnRefreshListener(this);
        return v;
    }
}
