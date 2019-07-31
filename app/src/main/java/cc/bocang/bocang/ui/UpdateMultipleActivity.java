package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.ResponseBody;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.model.Result;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.global.MyApplication;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * @author Jun
 * @time 2016/10/25  11:54
 * @desc ${TODD}
 */
public class UpdateMultipleActivity extends BaseActivity implements View.OnClickListener {
    private Button submitBt,topLeftBtn;
    private TextView multipleEt;
    private HDApiService apiService;
    private UserInfo mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mInfo = ((MyApplication)getApplication()).mUserInfo;
        apiService = HDRetrofit.create(HDApiService.class);
        setContentView(R.layout.activity_updatemultiple);
        submitBt = (Button)findViewById(R.id.submitBt);
        multipleEt = (TextView)findViewById(R.id.multipleEt);
        topLeftBtn = (Button)findViewById(R.id.topLeftBtn);
        submitBt.setOnClickListener(this);
        topLeftBtn.setOnClickListener(this);
        if(!TextUtils.isEmpty(mInfo.getMultiple()) && !mInfo.getMultiple().equals("null")){
            multipleEt.setText(mInfo.getMultiple());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitBt:
                //确定按钮
                String multipleValue=multipleEt.getText().toString();
                if(TextUtils.isEmpty(multipleValue) ||multipleValue=="0.0"){
                    tip("请输入倍数!");
                    return;
                }
                //TODO
//                Log.v("520it",":"+mInfo.getId()+"");
//                Log.v("520it",":"+Integer.parseInt(multipleValue)+"");
                submitMultiple(apiService,mInfo.getId(),Double.parseDouble(multipleValue));
            break;
            case R.id.topLeftBtn:
                finish();
            break;
        }
    }


    /**
     * 提交订单
     * @param apiService
     * @param userId   用户ID
     * @param multiple 倍数
     */
    private void submitMultiple(HDApiService apiService, int userId, double multiple){

        Call<ResponseBody> call = apiService.getMultiple(userId,multiple);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == UpdateMultipleActivity.this || UpdateMultipleActivity.this.isFinishing())
                    return;

                try {
                    String json = response.body().string();
                    Log.v("520it","result.getResult()="+json);
                    Result result= JSON.parseObject(json,Result.class);
                    Log.v("520it","result.getResult()="+result.getResult());
                    if(result.getResult().equals("0")){
                        tip("设置失败!");
                    }else{
                        tip("设置成功!");
                    }
                } catch (Exception e) {
                    if (null != UpdateMultipleActivity.this && ! UpdateMultipleActivity.this.isFinishing())
                        tip("设置失败!");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null ==  UpdateMultipleActivity.this ||  UpdateMultipleActivity.this.isFinishing())
                    return;
                tip("设置失败");
            }
        });
    }
}
