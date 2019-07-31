package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import cc.bocang.bocang.R;
import cc.bocang.bocang.broadcast.Broad;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.dao.CartDao;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.parser.ParseGetGoodsInfoResp;
import cc.bocang.bocang.data.response.GetGoodsInfoResp;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by bocang02 on 16/10/13.
 */

public class ScannerWebActivity extends BaseActivity {
    private final String TAG = ScannerWebActivity.class.getSimpleName();
    private WebView mWebView;
    private String tempID;
    private HDApiService apiService;
    private Goods goods;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_scanner_web);
        mWebView = (WebView) findViewById(R.id.scannerWebView);
        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));

        String tempURL = getIntent().getStringExtra("tempURL");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(tempURL);

        Log.e("0........", tempURL);

        String [] sub_url_array = tempURL.split("[/ : . - _ # %]");
        int sid;
        for(sid=0;sid<sub_url_array.length;sid++) {
            if(sub_url_array[sid].equals("")) continue;
//            System.out.println(sub_url_array[sid]);

            tempID = sub_url_array[sid];
        }

//        Log.e("1........", sub_url_array.toString());

        Log.e("2........", tempID);

        apiService = HDRetrofit.create(HDApiService.class);
        callGoodsInfo(apiService, Integer.valueOf(tempID).intValue());

        Button button = (Button) findViewById(R.id.scannerShoppingButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartDao dao = new CartDao(ScannerWebActivity.this);
                if (-1 != dao.replaceOne(goods)) {
                    Toast.makeText(ScannerWebActivity.this, "已添加到购物车", Toast.LENGTH_LONG).show();
                    Broad.sendLocalBroadcast(ScannerWebActivity.this, Broad.CART_CHANGE_ACTION, null);//发送广播
                }
            }
        });
    }


    private void callGoodsInfo(HDApiService apiService, int id) {
        Call<ResponseBody> call = apiService.getGoodsInfo(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    GetGoodsInfoResp resp = ParseGetGoodsInfoResp.parse(json);
                    if (null != resp && resp.isSuccess()) {

                        goods = resp.getGoods();

                    }
                } catch (Exception e) {
                    Toast.makeText(ScannerWebActivity.this, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(ScannerWebActivity.this, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
