package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.squareup.okhttp.ResponseBody;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.parser.ParseGetSceneListResp;
import cc.bocang.bocang.data.response.GetSceneListResp;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private EditText mEtContent;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEtContent.setText("");
                mProgressBar.setVisibility(View.VISIBLE);
                HDApiService apiService = HDRetrofit.create(HDApiService.class);
//                Call<ResponseBody> call = apiService.getUserInfo("ce97c8f7e1733564b4f20046d5d24831");
//                Call<ResponseBody> call = apiService.updateUserStatus(511, 1);
//                Call<ResponseBody> call = apiService.getFenXiao(489);
//                Call<ResponseBody> call = apiService.getAd(1);
//                Call<ResponseBody> call = apiService.getGoodsList(0, 1, null, null, null);
//                Call<ResponseBody> call = apiService.getGoodsInfo(20);
                Call<ResponseBody> call = apiService.getSceneList(0, 1, null, null, null);
                call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
                    @Override
                    public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            String json = response.body().string();
                            Log.i(TAG, json);
                            GetSceneListResp resp = ParseGetSceneListResp.parse(json);
                            json = resp.isSuccess() + "\n" + resp.getSceneAllAttrs().toString() + "\n" + resp.getScenes().toString();
                            mEtContent.setText(json);
                            Log.i(TAG, json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i(TAG, t.getMessage());
                    }
                });
            }
        });
    }
}
