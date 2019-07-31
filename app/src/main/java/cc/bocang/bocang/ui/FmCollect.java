package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lib.common.hxp.view.StatedFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.dao.CollectDao;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.global.Constant;

public class FmCollect extends StatedFragment implements AdapterView.OnItemClickListener {

    private List<Goods> goodses;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = initView(inflater, container);

        CollectDao dao = new CollectDao(getActivity());
        goodses = dao.getAll();
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mListView) {
            Intent intent = new Intent(getActivity(), ProDetailActivity.class);
            intent.putExtra("id", goodses.get(position).getId());
            startActivity(intent);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_lv_collect, null);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
                holder.priceTv = (TextView) convertView.findViewById(R.id.priceTv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.nameTv.setText(goodses.get(position).getName());
            holder.priceTv.setText("￥" + goodses.get(position).getShop_price());
            imageLoader.displayImage(Constant.PRODUCT_URL + goodses.get(position).getImg_url() + "!280X280.png", holder.imageView, options);

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTv;
            TextView priceTv;
        }
    }

    private ListView mListView;
    private MyAdapter mAdapter;

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_collect, container, false);
        mListView = (ListView) v.findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);
        return v;
    }
}
