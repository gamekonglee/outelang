package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
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
import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.data.model.SceneAllAttr;
import cc.bocang.bocang.data.parser.ParseGetSceneListResp;
import cc.bocang.bocang.data.response.GetSceneListResp;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.utils.ConvertUtil;
import cc.bocang.bocang.utils.net.HttpListener;
import cc.bocang.bocang.utils.net.Network;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FmScene extends StatedFragment implements OnFilterDoneListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private final String TAG = FmScene.class.getSimpleName();

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
    }
    private Network mNetwork;
    private HDApiService apiService;
    private Integer[] sceneIds = new Integer[]{0, 0, 0};
    private String fitterStr;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Constant.isDebug)
            Log.i(TAG, "onCreateView....");

        View view = initView(inflater, container);
        mNetwork=new Network();
        fitterStr = sceneIds[0] + "." + sceneIds[1] + "." + sceneIds[2];
        apiService = HDRetrofit.create(HDApiService.class);
        callSceneList(apiService, 0, page, null, null, fitterStr, true);

        return view;
    }

    private List<SceneAllAttr> sceneAllAttrs;
    private List<Scene> scenes;

    private void callSceneList(HDApiService apiService, int c_id, final int page, String keywords, String type, String filter_attr, final boolean initFilterDropDownView) {
        pd.setVisibility(View.VISIBLE);
        mNetwork.sendSceneList(c_id+"", page, keywords, type, filter_attr, this.getActivity(), new HttpListener() {
            @Override
            public void onSuccessListener(int what, String response) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response;
                    Log.i(TAG, json);
                    GetSceneListResp resp = ParseGetSceneListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        sceneAllAttrs = resp.getSceneAllAttrs();
                        if (initFilterDropDownView)//重复setMenuAdapter会报错
                            initFilterDropDownView(sceneAllAttrs);

                        List<Scene> sceneList = resp.getScenes();
                        if (1 == page)
                            scenes = sceneList;
                        else if (null != scenes) {
                            scenes.addAll(sceneList);
                            if (sceneList.isEmpty())
                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //Toast.makeText(getActivity(), "数据异常9...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureListener(int what, String ans) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                FmScene.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }
        });

    }


    private void callSceneList02(HDApiService apiService, int c_id, final int page, String keywords, String type, String filter_attr, final boolean initFilterDropDownView) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getSceneList(c_id, page, keywords, type, filter_attr);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetSceneListResp resp = ParseGetSceneListResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        sceneAllAttrs = resp.getSceneAllAttrs();
                        if (initFilterDropDownView)//重复setMenuAdapter会报错
                            initFilterDropDownView(sceneAllAttrs);

                        List<Scene> sceneList = resp.getScenes();
                        if (1 == page)
                            scenes = sceneList;
                        else if (null != scenes) {
                            scenes.addAll(sceneList);
                            if (sceneList.isEmpty())
                                Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //                    Toast.makeText(getActivity(), "数据异常9...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                FmScene.this.page--;
                if (null != mPullToRefreshLayout) {
                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                //                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Integer> itemPosList = new ArrayList<>();//有选中值的itemPos列表，长度为3

    private void initFilterDropDownView(List<SceneAllAttr> sceneAllAttrs) {
        if (itemPosList.size() < sceneAllAttrs.size()) {
            itemPosList.add(0);
            itemPosList.add(0);
            itemPosList.add(0);
        }
        SceneDropMenuAdapter dropMenuAdapter = new SceneDropMenuAdapter(getActivity(), sceneAllAttrs, itemPosList, this);
        dropDownMenu.setMenuAdapter(dropMenuAdapter);
    }

    @Override
    public void onFilterDone(int titlePos, int itemPos, String itemStr) {
        dropDownMenu.close();
        if (0 == itemPos)
            itemStr = sceneAllAttrs.get(titlePos).getAttrName();
        dropDownMenu.setPositionIndicatorText(titlePos, itemStr);

        if (titlePos < itemPosList.size())
            itemPosList.remove(titlePos);
        itemPosList.add(titlePos, itemPos);

                int goodsId = sceneAllAttrs.get(titlePos).getSceneAttrs().get(itemPos).getScene_id();
        sceneIds[titlePos] = goodsId;
        fitterStr = sceneIds[0] + "." + sceneIds[1] + "." + sceneIds[2];

        callSceneList(apiService, 0, page, null, null, fitterStr, false);



        if (Constant.isDebug)
            Toast.makeText(getActivity(), itemStr + " titlePos：" + titlePos + "  itemPos：" + itemPos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mGridView) {
            if (Constant.isDebug)
                Toast.makeText(getActivity(), "点击：" + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), DiyActivity.class);
            intent.putExtra("from", "scene");
            intent.putExtra("path", scenes.get(position).getPath());
            int ids= Integer.parseInt(scenes.get(position).getId());
            intent.putExtra("url",ids>1551?Constant.SCENE_URL_2:Constant.SCENE_URL);
            startActivity(intent);

        }
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        callSceneList(apiService, 0, page, null, null, fitterStr, false);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        callSceneList(apiService, 0, ++page, null, null, fitterStr, false);
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
            if (null == scenes)
                return 0;
            return scenes.size();
        }

        @Override
        public Scene getItem(int position) {
            if (null == scenes)
                return null;
            return scenes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_gridview_fm_scene, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                RelativeLayout.LayoutParams lLp = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
                float h = (mScreenWidth - ConvertUtil.dp2px(getActivity(), 45f)) / 2f * (3f / 4f);
                lLp.height = (int) h;
                holder.imageView.setLayoutParams(lLp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(scenes.get(position).getName());
            int id= Integer.parseInt(scenes.get(position).getId());
            if(id>1551){
                imageLoader.displayImage(Constant.SCENE_URL_2 + scenes.get(position).getPath() + "!400X400.png", holder.imageView, options);
            }else {
                imageLoader.displayImage(Constant.SCENE_URL + scenes.get(position).getPath() + "!400X400.png", holder.imageView, options);
            }


            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

    static DropDownMenu dropDownMenu;
    private int mScreenWidth;
    private ProgressBar pd;
    private GridView mGridView;
    private MyAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_product, container, false);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        pd = (ProgressBar) v.findViewById(R.id.pd);
        dropDownMenu = (DropDownMenu) v.findViewById(R.id.dropDownMenu);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mGridView.setAdapter(mAdapter);
        mPullToRefreshLayout = ((PullToRefreshLayout) v.findViewById(R.id.mFilterContentView));
        mPullToRefreshLayout.setOnRefreshListener(this);
        return v;
    }
}
