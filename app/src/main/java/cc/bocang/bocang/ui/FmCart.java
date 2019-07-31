package cc.bocang.bocang.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.common.hxp.view.StatedFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenu;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuCreator;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuItem;
import com.yjn.swipelistview.swipelistviewlibrary.widget.SwipeMenuListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.broadcast.Broad;
import cc.bocang.bocang.data.dao.CartDao;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.IntentKeys;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.listener.INumberInputListener;
import cc.bocang.bocang.view.NumberInputView;

public class FmCart extends StatedFragment implements AdapterView.OnItemClickListener {

    private UserInfo mInfo;
//    private boolean ifAllCheck=false;

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
        //注册消息发送
        EventBus.getDefault().register(this);
        return view;
    }

    //在主线程执行
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(Integer action){
        if(action==IntentKeys.ORDERFINISH){
            Broad.sendLocalBroadcast(getActivity(), Broad.CART_CHANGE_ACTION, null);//发送广播
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mListView) {
            Intent intent = new Intent(getActivity(), ProDetailActivity.class);
            intent.putExtra("id", goodses.get(position).getId());
            startActivity(intent);
        }
    }

    private List<Integer> buyNum=new ArrayList<>();

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

        public void setBuyCount(int position ,int num){
            buyNum.set(position,num);
        }

        public void getBuyCount(){
            for(int i=0;i<goodses.size();i++){
                buyNum.add(goodses.get(i).getBuyCount());
            }
        }

        @Override
        public int getCount() {
            if (null == goodses)
                return 0;
            return goodses.size();
        }

        /**
         * 合计数量
         * @return
         */
        public int getTotalCount() {
            int count = 0;
            for (int i = 0; i < goodses.size(); i++) {
                //判断该商品是不是选择状态
                if (goodses.get(i).delete == true) {
                    count += goodses.get(i).getBuyCount();
                }
            }
            return count;
        }

        /**
         * 选择状态为true的数据
         * @return
         */
        public ArrayList<Goods> getShopCarChecked(){
            ArrayList<Goods> beans=new ArrayList<>();
            for(int i=0;i<goodses.size();i++){
                if(goodses.get(i).delete==true){
                    beans.add(goodses.get(i));
                }
            }
            return beans;
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
                convertView = View.inflate(getActivity(), R.layout.item_lv_cart, null);

                holder = new ViewHolder();
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
                holder.numTv = (TextView) convertView.findViewById(R.id.numTv);
                holder.priceTv = (TextView) convertView.findViewById(R.id.priceTv);
                holder.InputView = (NumberInputView) convertView.findViewById(R.id.number_input_et);
                holder.agioTv = (EditText) convertView.findViewById(R.id.agioTv);
                holder.agioitemiv = (LinearLayout) convertView.findViewById(R.id.agioitemiv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.agioitemiv.setVisibility(IntentKeys.ISAGIO?View.VISIBLE:View.GONE);
            holder.InputView.setMax(10000);//设置数量的最大值
            holder.nameTv.setText(goodses.get(position).getName());
//            holder.agioTv.setText(goodses.get(position).getAgio()+"");
           String multiple= mInfo.getMultiple();
            if(!TextUtils.isEmpty(multiple) && !multiple.equals("null")){
                holder.priceTv.setText("￥" + goodses.get(position).getShop_price()*Double.parseDouble(mInfo.getMultiple()));
            }else{
                holder.priceTv.setText("￥" + goodses.get(position).getShop_price());
            }
            imageLoader.displayImage(Constant.PRODUCT_URL +goodses.get(position).getImg_url() + "!280X280.png", holder.imageView, options);
            //输入数量的监听事件
            holder.InputView.setListener(new INumberInputListener() {
                @Override
                public void onTextChange(int index) {

//                    holder.priceTv.setText("￥" +goodses.get(position).getShop_price() * Integer.parseInt(holder.numTv.getText().toString()));
                    goodses.get(position).setBuyCount(Integer.parseInt(holder.numTv.getText().toString()));
                    setBuyCount(position,Integer.parseInt(holder.numTv.getText().toString()));
                    //合计金额
                    Log.v("520it","触发一");
                    money_tv.setText("￥"+getMoney()+"");
                    Log.v("520it","触发一1:"+money_tv.getText());
////                    //合计数量
//                    settle_tv.setText("合计("+getTotalCount()+")");
                }
            });



            holder.checkBox.setChecked(mCheckedAll);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        goodses.get(position).delete = true;
                    } else {
                        mNotify = false;
                        mAllCB.setChecked(false);
                        mNotify = true;
                        goodses.get(position).delete = false;
                    }
                    goodses.get(position).setBuyCount(Integer.parseInt(holder.numTv.getText().toString()));
                    setBuyCount(position,Integer.parseInt(holder.numTv.getText().toString()));
                    money_tv.setText("￥"+getMoney());
                    if(position==goodses.size()-1){
                        money_tv.setText("￥"+getMoney());
                    }
                }
            });

           //折扣改变触发事件
            holder.agioTv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String name=holder.nameTv.getText().toString();
                    if(name.equals(goodses.get(position).getName())){
                        if(!TextUtils.isEmpty(holder.agioTv.getText().toString())){
                            goodses.get(position).setAgio(Double.parseDouble(holder.agioTv.getText().toString())*0.01);
                            money_tv.setText("￥"+getMoney()+"");
                            Log.v("520it","触发三");
                            Log.v("520it","触发san:"+money_tv.getText());
                        }

                    }


                }
            });

            return convertView;
        }

        private double  getMoney() {
            double money=0;
            for(int i =0;i<goodses.size();i++){

                //判断该商品是不是选择状态
                if(goodses.get(i).delete==true){
                    if(goodses.get(i).getAgio()==0){
                        if(!TextUtils.isEmpty(mInfo.getMultiple())&& !mInfo.getMultiple().equals("null")){
                            money+=goodses.get(i).getShop_price() * goodses.get(i).getBuyCount()*1*Double.parseDouble(mInfo.getMultiple());
                        }else{
                            money+=goodses.get(i).getShop_price() * goodses.get(i).getBuyCount()*1;
                        }
                    }else{
                        if(!TextUtils.isEmpty(mInfo.getMultiple())&& !mInfo.getMultiple().equals("null")){
                            Log.v("520it",goodses.get(i).getAgio()+":agio");
                            money+=goodses.get(i).getShop_price() * goodses.get(i).getBuyCount()*goodses.get(i).getAgio()*Double.parseDouble(mInfo.getMultiple());
                        }else{
                            Log.v("520it",goodses.get(i).getAgio()+":agio");
                            money+=goodses.get(i).getShop_price() * goodses.get(i).getBuyCount()*goodses.get(i).getAgio();
                        }
                    }
                }
            }
            String sumAgio = sumAgioTv.getText().toString();
            money=TextUtils.isEmpty(sumAgio)?money*1:money*Double.parseDouble(sumAgio)*0.01;
            BigDecimal b = new BigDecimal(money);
            return  b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        class ViewHolder {
            CheckBox checkBox;
            ImageView imageView;
            TextView nameTv;
            TextView numTv;
            TextView priceTv;
            NumberInputView InputView;
            EditText agioTv;
            LinearLayout agioitemiv;
        }
    }

    private SwipeMenuListView mListView;
    private MyAdapter mAdapter;
    private CheckBox mAllCB;
    private boolean mCheckedAll;
    private boolean mNotify = true;
    private List<Goods> goodses;
    private TextView money_tv;
    private TextView settle_tv;
    private EditText sumAgioTv;
    private LinearLayout agiolv;


    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_cart, container, false);
        goodses = ((IndexActivity) getActivity()).goodses;
        //清空数据
        mListView = (SwipeMenuListView) v.findViewById(R.id.listView);
        money_tv = (TextView) v.findViewById(R.id.money_tv);
        sumAgioTv = (EditText) v.findViewById(R.id.sumAgioTv);
        settle_tv =(TextView) v.findViewById(R.id.settle_tv);
        agiolv = (LinearLayout)v.findViewById(R.id.agiolv);
        mAllCB = (CheckBox) v.findViewById(R.id.checkAll);
        agiolv.setVisibility(IntentKeys.ISAGIO==true?View.VISIBLE:View.GONE);
//        mListView.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.getBuyCount();//购买数量


        for(int i=0;i<goodses.size();i++){
            goodses.get(i).setBuyCount(1);
            goodses.get(i).setAgio(0);
        }

//
        mInfo = ((MyApplication) getActivity().getApplication()).mUserInfo;
        final SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());

                deleteItem.setBackground(new ColorDrawable(Color.parseColor("#fe3c3a")));
                deleteItem.setWidth(dp2px(80));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(20);
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    CartDao dao = new CartDao(getActivity());
                    if (-1 != dao.deleteOne(goodses.get(position).getId())) {
                        Broad.sendLocalBroadcast(getActivity(), Broad.CART_CHANGE_ACTION, null);//发送广播
                        mAdapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
//
        sumAgioTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                money_tv.setText("￥"+ mAdapter.getMoney()+"");
                Log.v("520it","触发:"+money_tv.getText());
            }
        });


        mAllCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckedAll = true;
                    mAdapter.notifyDataSetChanged();
                } else {
                    mCheckedAll = false;
                    if (mNotify) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        settle_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否选择了商品
                if(mAdapter.getTotalCount()<=0){
                    Toast.makeText(getActivity(),"请选择要购买的商品",Toast.LENGTH_SHORT).show();
                    return;
                }
                //跳转到提交订单
                Intent intent=new Intent(getActivity(),OrderActivity.class);
                intent.putExtra(IntentKeys.SHOPCARDATAS, mAdapter.getShopCarChecked());
                double money=mAdapter.getMoney();
                intent.putExtra(IntentKeys.SHOPCARTOTALPRICE,money);
                intent.putExtra(IntentKeys.SHOPCARTOTALCOUNT,mAdapter.getTotalCount());
                String sumAgio = sumAgioTv.getText().toString();
                intent.putExtra(IntentKeys.SHOPORDERDISCOUNT,sumAgio);
                startActivity(intent);
            }
        });
        return v;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                this.getResources().getDisplayMetrics());
    }
}
