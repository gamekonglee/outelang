package cc.bocang.bocang.utils.net;

import android.app.Activity;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import cc.bocang.bocang.R;

/**
 * @author : Jun
 * NoHttp 请求
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    private Activity mActivity;
    /**
     * Request
     */
    private Request<?> mRequest;
    /**
     * 结果回调
     */
    private HttpListener callBack;
    private boolean isResult;

    /**
     * @param activity     context用来实例化dialog
     * @param request      请求对象
     * @param httpCallback 回调对象
     * @param canCancel    是否允许用户取消请求
     * @param isLoading    是否显示dialog
     * @param isResult     是否判断参数
     */
    public HttpResponseListener(Activity activity, Request<?> request, HttpListener httpCallback,
                                boolean canCancel, boolean isLoading, boolean isResult) {
        this.mActivity = activity;
        this.mRequest = request;
        if (activity != null && isLoading) {
            //            mWaitDialog = new WaitDialog(activity);
            //            mWaitDialog.setCancelable(canCancel);
            //            mWaitDialog.setOnCancelListener(mRequest.cancel());
        }
        this.callBack = httpCallback;
        this.isResult = isResult;
    }


    @Override
    public void onStart(int what) {

    }

    /**
     * 成功回调
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callBack != null) {
            String val = (String) response.get();
            callBack.onSuccessListener(what, val);
        }
    }

    /**
     * 失败回调
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        int error = 0;
        Exception exception = response.getException();
        if (exception instanceof NetworkError) {// 网络不好
            error = 1;
            Toast.makeText(mActivity, R.string.error_please_check_network,Toast.LENGTH_SHORT).show();
        } else if (exception instanceof TimeoutError) {// 请求超时
            error = 1;
            Toast.makeText(mActivity, R.string.error_timeout,Toast.LENGTH_SHORT).show();
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            error = 1;
            Toast.makeText(mActivity, R.string.error_not_found_server, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof URLError) {// URL是错的
            error = 1;
            Toast.makeText(mActivity, R.string.error_url_error, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof NotFoundCacheError) {
            error = 1;
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            Toast.makeText(mActivity, R.string.error_not_found_cache, Toast.LENGTH_SHORT).show();
        } else {
            error = 1;
            Toast.makeText(mActivity, R.string.error_unknow,Toast.LENGTH_SHORT).show();
        }
        Logger.e("错误：" + exception.getMessage());

        if (error == 0){
            JSONObject ans=new JSONObject();
            ans.put("result","数据异常");
            callBack.onFailureListener(what, "");
            return;
        }

        if (callBack != null) {
            String val = (String) response.get();
//            JSONObject ans = JSONObject.parseObject(val);
            callBack.onFailureListener(what, val);
        }

    }

    @Override
    public void onFinish(int what) {

    }
}
