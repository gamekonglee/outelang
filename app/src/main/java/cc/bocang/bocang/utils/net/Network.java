package cc.bocang.bocang.utils.net;

import android.app.Activity;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.Request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cc.bocang.bocang.global.NetWorkConst;
import cc.bocang.bocang.utils.AppUtils;

/**
 * @author: Jun
 * @date : 2017/5/17 11:39
 * @description :
 */
public class Network {
    /**
     * 用来标记取消
     */
    private static Object object = new Object();

    public void sendGoodsList( String c_id, final int page,String okcat_id, String keywords, String type, String filter_attr,Activity view, HttpListener callBack) {
        Map<String, Object> params=new HashMap<>();
        params.put("c_id", c_id);
        params.put("page", page);
        params.put("okcat_id", okcat_id);
        params.put("keywords", keywords);
        params.put("type", type);
        params.put("filter_attr", filter_attr);
        sendRequest(NetWorkConst.PRODUCTLIST_URL, params, 1, RequestMethod.POST, NetWorkConst.WITH_PRODUCTLIST_URL, true, view, callBack);
    }

    /**
     * 场景列表
     */
    public void sendSceneList(String c_id, final int page, String keywords, String type, String filter_attr,Activity view, HttpListener callBack) {
        Map<String, Object> params=new HashMap<>();
        params.put("c_id", c_id);
        params.put("page", page);
        params.put("keywords", keywords);
        params.put("type", type);
        params.put("filter_attr", filter_attr);
        sendRequest(NetWorkConst.SCENETLIST_URL, params, 2, RequestMethod.POST, NetWorkConst.WITH_SCENELIST_URL, true, view, callBack);
    }

    /**
     *  NOHTTP请求
     * @param url         网络地址
     * @param data      请求参数
     * @param cancheType  缓存类型 0：不考虑缓存，直接请求网络 1：请求网络，请求失败返回上次缓存的数据 2：没有缓存在请求网络
     * @param requestType 请求方式  RequestMethod.POST  RequestMethod.GET。。
     * @param what        请求标签
     * @param isResult    判断是否判断请求放回来的参数
     * @param view        activity
     * @param callBack    回调接口
     */
    private void sendRequest(String url,Map<String, Object> data, int cancheType, RequestMethod requestType,int what,boolean isResult, Activity view, HttpListener callBack) {
        Request<String> request = null;
        request = NoHttp.createStringRequest(url, requestType);

        //传递参数
        Iterator<Map.Entry<String, Object>> itr = data.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
            if (!AppUtils.isEmpty(entry)) {
                if (AppUtils.isEmpty(entry.getValue())) {
                    request.add(entry.getKey(), "");
                } else {
                    request.add(entry.getKey(), entry.getValue().toString());
                }

            }
        }
        request.setCancelSign(object);

        if (cancheType==1) {
            request.setCacheKey(url+data.toString());//
            // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
            request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);//请求失败返回上次缓存的数据
            // 添加到请求队列
            CallServer.getInstance().add(view, what, request, callBack, false, true,isResult);
        } else if (cancheType==0){//不考虑缓存，直接请求网络
            // 添加到请求队列
            CallServer.getInstance().add(view, what, request, callBack, true, true,isResult);
        }else if(cancheType==2){
            request.setCacheKey(url+data.toString());//
            // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
            request.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);//没有缓存在请求网络
            // 添加到请求队列
            CallServer.getInstance().add(view, what, request, callBack, false, true,isResult);
        }
    }


}
