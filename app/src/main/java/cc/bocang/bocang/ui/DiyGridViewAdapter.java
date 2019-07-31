package cc.bocang.bocang.ui;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.global.Constant;

public class DiyGridViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> paths;
    private List<String> names;
    private int show;
    private int mScreenWidth, mScreenHeight;
    private Context mContext;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private int ids;
    private List<Scene> scenes=new ArrayList<>();

    public void setNames(List<String> names) {
        this.names = names;
    }

    public void setShow(int show) {
        this.show = show;
    }


    public void setIds(int position) {
        this.ids = position;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public DiyGridViewAdapter(Context context) {
        mContext = context;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mInflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                // .displayer(new FadeInBitmapDisplayer(100))// 图片加载好后渐入的动画时间
                .build(); // 构建完成

        // 得到ImageLoader的实例(使用的单例模式)
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        if (null == paths)
            return 0;

        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        if (null == paths)
            return null;

        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        String preUrl = "";
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gv_diy, null);

            viewHolder = new ViewHolder();
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.iv.getLayoutParams();
        int w = (int) ((mScreenWidth / 3.0f - 80.0f / 1200.0f * mScreenWidth - 2 * 8.0f / 1200.0f * mScreenWidth));
        int h = 0;
        if (show == 1) {//显示产品
            h = w;
            preUrl = Constant.PRODUCT_URL;
        } else if (show == 2) {//显示场景
            h = (int) (w / 4.0f * 3.0f);
            int id= Integer.parseInt(scenes.get(position).getId());
            if(id>1551){
                preUrl=Constant.SCENE_URL_2;
            }else {
                preUrl = Constant.SCENE_URL;
            }
        }
        lp.width = w;
        lp.height = h;
        viewHolder.iv.setLayoutParams(lp);

        if (!names.isEmpty()) {
            viewHolder.tv.setText(names.get(position));
            viewHolder.tv.setTextColor(Color.parseColor("#ffffff"));
        }

        imageLoader.displayImage(preUrl + paths.get(position) + "!280X280.png", viewHolder.iv, options);

        return convertView;
    }

    public void setScene(List<Scene> scenes) {
        this.scenes=scenes;
    }

    class ViewHolder {
        ImageView iv;
        TextView tv;
    }
}
