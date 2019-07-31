package cc.bocang.bocang.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.baiiu.filter.adapter.MenuAdapter;
import com.baiiu.filter.adapter.SimpleTextAdapter;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.baiiu.filter.interfaces.OnFilterItemClickListener;
import com.baiiu.filter.typeview.SingleGridView;
import com.baiiu.filter.util.UIUtil;
import com.baiiu.filter.view.FilterCheckedTextView;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.model.GoodsAttr;

/**
 * 产品筛选条件
 */
public class ProDropMenuAdapter implements MenuAdapter {
    private final Context mContext;
    private OnFilterDoneListener onFilterDoneListener;
    private List<GoodsAllAttr> goodsAllAttrs;
    private List<Integer> itemPosList;

    public ProDropMenuAdapter(Context context, List<GoodsAllAttr> goodsAllAttrs, List<Integer> itemPosList, OnFilterDoneListener onFilterDoneListener) {
        this.mContext = context;
        this.goodsAllAttrs = goodsAllAttrs;
        this.onFilterDoneListener = onFilterDoneListener;
        this.itemPosList = itemPosList;
    }

    @Override
    public int getMenuCount() {
        return goodsAllAttrs.size();
    }

    @Override
    public String getMenuTitle(int position) {
        if (position < itemPosList.size()) {
            int itemPos = itemPosList.get(position);
            if (itemPos != 0) {
                return goodsAllAttrs.get(position).getGoodsAttrs().get(itemPos).getAttr_value();
            }
        }
        return goodsAllAttrs.get(position).getAttrName();
    }

    @Override
    public int getBottomMargin(int position) {

        return 0;
    }

    @Override
    public View getView(int position, FrameLayout parentContainer) {
        return createSingleGridView(position);
    }

    private View createSingleGridView(final int position) {
        SingleGridView<String> singleGridView = new SingleGridView<String>(mContext)
                .adapter(new SimpleTextAdapter<String>(null, mContext) {
                    @Override
                    public String provideText(String s) {
                        return s;
                    }

                    @Override
                    protected void initCheckedTextView(FilterCheckedTextView checkedTextView) {
                        checkedTextView.setPadding(0, UIUtil.dp(context, 3), 0, UIUtil.dp(context, 3));
                        checkedTextView.setGravity(Gravity.CENTER);
                        checkedTextView.setBackgroundResource(R.drawable.selector_filter_grid);
                    }
                })
                .onItemClick(new OnFilterItemClickListener<String>() {
                    @Override
                    public void onItemClick(int itemPos, String itemStr) {

                        if (onFilterDoneListener != null) {
                            onFilterDoneListener.onFilterDone(position, itemPos, itemStr);
                        }

                    }
                });

        List<String> list = new ArrayList<>();
        for (int i = 0; i < goodsAllAttrs.get(position).getGoodsAttrs().size(); ++i) {
            GoodsAttr goodsAttr = goodsAllAttrs.get(position).getGoodsAttrs().get(i);
            list.add(goodsAttr.getAttr_value());
        }
        int itemPos = 0;
        if (position < itemPosList.size())
            itemPos = itemPosList.get(position);
        singleGridView.setList(list, itemPos);

        return singleGridView;
    }
}
