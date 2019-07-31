package cc.bocang.bocang.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cc.bocang.bocang.R;

public class DiyListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> data;
    private int selection;// 当前选中位置
    private int mScreenWidth;
    private Context mContext;

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public DiyListViewAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public int getCount() {
        if (null == data)
            return 0;

        return data.size();
    }

    @Override
    public String getItem(int position) {
        if (null == data)
            return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_lv_diy, null);

            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setMinHeight((int) (120.0f / 1200.0f * mScreenWidth));
        holder.tv.setText(getItem(position));
        // 处理选中和未选中的品牌背景
        if (selection == position) {
            holder.tv.setBackgroundColor(Color.parseColor("#66000000"));
        } else {
            holder.tv.setBackgroundColor(Color.parseColor("#000000"));
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }
}
