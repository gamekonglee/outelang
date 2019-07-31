package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.common.hxp.global.UserSp;
import com.lib.common.hxp.view.EaseSwitchButton;
import com.lib.common.hxp.view.StatedFragment;
import com.squareup.okhttp.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.List;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.Distributor;
import cc.bocang.bocang.data.parser.ParseGetDistributorResp;
import cc.bocang.bocang.data.parser.ParseUpdateUserStatusResp;
import cc.bocang.bocang.data.response.GetDistributorResp;
import cc.bocang.bocang.data.response.UpdateUserStatusResp;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FmDistributor extends StatedFragment implements AdapterView.OnItemClickListener {
    private final String TAG = FmDistributor.class.getSimpleName();
    private HDApiService apiService;

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
        UserSp userSp = new UserSp(getActivity());
        int userId = userSp.getInt(userSp.getSP_USERID(), 0);
        apiService = HDRetrofit.create(HDApiService.class);
        callDistributors(apiService, userId);

        return view;
    }

    private List<Distributor> distributors;

    private void callDistributors(HDApiService apiService, int user_id) {
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.getFenXiao(user_id);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetDistributorResp resp = ParseGetDistributorResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        distributors = resp.getBeans();
                        mListView.setAdapter(mAdapter);
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
                pd.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void callUpdateStatus(HDApiService apiService, int user_id, int status) {
        Call<ResponseBody> call = apiService.updateUserStatus(user_id, status);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == getActivity() || getActivity().isFinishing())
                    return;
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    UpdateUserStatusResp resp = ParseUpdateUserStatusResp.parse(json);
                    if (null != resp && resp.isSuccess()) {

                    }else{
                        Toast.makeText(getActivity(), "操作失败！", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mListView && position != 0) {
            EaseSwitchButton switchBtn = (EaseSwitchButton) view.findViewById(R.id.switchBtn);
            if (switchBtn.isSwitchOpen()) {
                switchBtn.closeSwitch();
                distributors.get(position - 1).setIs_use("0");
                callUpdateStatus(apiService, distributors.get(position -1).getId(), 0);
            } else {
                switchBtn.openSwitch();
                distributors.get(position - 1).setIs_use("1");
                callUpdateStatus(apiService, distributors.get(position -1).getId(), 1);
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (null == distributors)
                return 1;
            return distributors.size() + 1;
        }

        @Override
        public Distributor getItem(int position) {
            if (0 == position)
                return null;
            return distributors.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_lv_distributor, null);

                holder = new ViewHolder();
                holder.numTv = (TextView) convertView.findViewById(R.id.numTv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
                holder.phoneTv = (TextView) convertView.findViewById(R.id.phoneTv);
                holder.dateTv = (TextView) convertView.findViewById(R.id.dateTv);
                holder.checkedTv = (TextView) convertView.findViewById(R.id.checkedTv);
                holder.switchBtn = (EaseSwitchButton) convertView.findViewById(R.id.switchBtn);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                convertView.setBackgroundColor(0x33666666);
                holder.numTv.setText("编号");
                holder.numTv.setTextColor(0xFF000000);
                holder.nameTv.setText("用户名");
                holder.nameTv.setTextColor(0xFF000000);
                holder.phoneTv.setText("电话");
                holder.phoneTv.setTextColor(0xFF000000);
                holder.dateTv.setText("日期");
                holder.dateTv.setTextColor(0xFF000000);
                holder.checkedTv.setText("审核");
                holder.checkedTv.setTextColor(0xFF000000);
                holder.checkedTv.setVisibility(View.VISIBLE);
                holder.switchBtn.setVisibility(View.GONE);
            } else {
                convertView.setBackgroundColor(0x00000000);
                Distributor distributor = getItem(position);
                holder.numTv.setText((getCount() - position) + "");
                holder.numTv.setTextColor(0xFF666666);
                holder.nameTv.setText(distributor.getName());
                holder.nameTv.setTextColor(0xFF666666);
                holder.phoneTv.setText(distributor.getPhone());
                holder.phoneTv.setTextColor(0xFF666666);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                holder.dateTv.setText(sdf.format(distributor.getAdd_time() * 1000));
                holder.dateTv.setTextColor(0xFF666666);
                holder.checkedTv.setVisibility(View.GONE);
                holder.switchBtn.setVisibility(View.VISIBLE);
                if ("1".equals(distributor.getIs_use())) {
                    holder.switchBtn.openSwitch();
                } else {
                    holder.switchBtn.closeSwitch();
                }
            }

            return convertView;
        }

        class ViewHolder {
            TextView numTv;
            TextView nameTv;
            TextView phoneTv;
            TextView dateTv;
            TextView checkedTv;
            EaseSwitchButton switchBtn;
        }
    }

    private ProgressBar pd;
    private ListView mListView;
    private MyAdapter mAdapter;

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_distributor, container, false);

        pd = (ProgressBar) v.findViewById(R.id.pd);
        mListView = (ListView) v.findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        return v;
    }
}
