package cc.bocang.bocang.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.common.hxp.view.ListViewForScrollView;

import java.util.ArrayList;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.dao.LogisticDao;
import cc.bocang.bocang.data.model.Logistics;
import cc.bocang.bocang.global.Constant;

/**
 * @author: Jun
 * @date : 2017/7/27 15:49
 * @description :
 */
public class UserAddressActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_add,topLeftBtn;
    private ProAdapter mProAdapter;
    private ListViewForScrollView order_sv;
    private int page = 1;
    private View mNullView;
    private View mNullNet;
    private Button mRefeshBtn;
    private TextView mNullNetTv;
    private TextView mNullViewTv;
    private ArrayList<Logistics> mLogisticList;
    private LogisticDao mLogisticDao;
    private Intent mIntent;
    public boolean isSelectLogistics=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address);
        initData();
        initView();
        initViewData();
    }

    private void initData() {
        Intent intent = getIntent();
        isSelectLogistics=intent.getBooleanExtra(Constant.isSELECTADDRESS,false);
    }

    private void initViewData() {
//        mLogisticList = (ArrayList<Logistics>) mLogisticDao.getAll();
//        mProAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLogisticList = (ArrayList<Logistics>) mLogisticDao.getAll();
        mProAdapter.notifyDataSetChanged();
    }

    private void initView() {
        btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        order_sv = (ListViewForScrollView) findViewById(R.id.order_sv);
        order_sv.setDivider(null);//去除listview的下划线
        mProAdapter = new ProAdapter();
        order_sv.setAdapter(mProAdapter);
        order_sv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //                if (mView.isSelectLogistics == false) {
                //                    mIntent = new Intent(mView, UserLogAddActivity.class);
                //                    mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                //                    mView.startActivityForResult(mIntent, Constance.FROMLOG);
                //                }else{
                //                    mIntent=new Intent();
                //                    mIntent.putExtra(Constance.logistics, mLogisticList.get(position));
                //                    mView.setResult(Constance.FROMLOG, mIntent);//告诉原来的Activity 将数据传递给它
                //                    mView.finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                //                }
            }
        });

        mNullView = findViewById(R.id.null_view);
        mNullNet = findViewById(R.id.null_net);
        mRefeshBtn = (Button) mNullNet.findViewById(R.id.refesh_btn);
        mNullNetTv = (TextView) mNullNet.findViewById(R.id.tv);
        mNullViewTv = (TextView) mNullView.findViewById(R.id.tv);
        mLogisticDao = new LogisticDao(this);
        topLeftBtn = (Button)findViewById(R.id.topLeftBtn);
        topLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                Intent intent=new Intent(this,UserAddressAddActivity.class);
                startActivity(intent);
            break;

        }
    }

    private class ProAdapter extends BaseAdapter {
        public ProAdapter() {
        }

        @Override
        public int getCount() {
            if (null == mLogisticList)
                return 0;
            return mLogisticList.size();
        }

        @Override
        public Logistics getItem(int position) {
            if (null == mLogisticList)
                return null;
            return mLogisticList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(UserAddressActivity.this, R.layout.item_user_address, null);

                holder = new ViewHolder();
                holder.consignee_tv = (TextView) convertView.findViewById(R.id.consignee_tv);
                holder.address_tv = (TextView) convertView.findViewById(R.id.address_tv);
                holder.phone_tv = (TextView) convertView.findViewById(R.id.phone_tv);
                holder.default_addr_tv = (TextView) convertView.findViewById(R.id.default_addr_tv);
                holder.delete_bt = (Button) convertView.findViewById(R.id.delete_bt);
                holder.edit_tv = (Button) convertView.findViewById(R.id.edit_tv);
                holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
                holder.edit_rl = (RelativeLayout) convertView.findViewById(R.id.edit_rl);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Logistics logistics = mLogisticList.get(position);
            holder.consignee_tv.setText(logistics.getName());
            holder.address_tv.setText(logistics.getAddress());
            holder.phone_tv.setText(logistics.getTel());
            holder.edit_rl.setVisibility(UserAddressActivity.this.isSelectLogistics == true ? View.GONE : View.VISIBLE);
            holder.delete_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(UserAddressActivity.this).setTitle(null).setMessage("是否删除该物流地址")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mLogisticDao.deleteOne(mLogisticList.get(position).getId());
                                    mLogisticList = (ArrayList<Logistics>) mLogisticDao.getAll();
                                    mProAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }
            });

            holder.edit_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntent = new Intent(UserAddressActivity.this, UserAddressAddActivity.class);
                    mIntent.putExtra(Constant.address, mLogisticList.get(position));
                    UserAddressActivity.this.startActivityForResult(mIntent, Constant.FROMLOG);
                }
            });
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(UserAddressActivity.this.isSelectLogistics){
                        Intent intent=new Intent();
                        intent.putExtra(Constant.address, mLogisticList.get(position));
                        UserAddressActivity.this.setResult(Constant.FROMADDRESS, intent);//告诉原来的Activity 将数据传递给它
                        UserAddressActivity.this.finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
                    }else{

                    }

                }
            });
            if(logistics.getIsDefault()==1){
                holder.default_addr_tv.setVisibility(View.VISIBLE);
            }else{
                holder.default_addr_tv.setVisibility(View.GONE);
            }

            return convertView;
        }

        class ViewHolder {
            TextView consignee_tv;
            TextView address_tv;
            TextView phone_tv;
            TextView default_addr_tv;
            Button delete_bt, edit_tv;
            LinearLayout ll;
            RelativeLayout edit_rl;
        }
    }
}
