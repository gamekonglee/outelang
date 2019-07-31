package cc.bocang.bocang.data.api;

import cc.bocang.bocang.global.Constant;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by xpHuang on 2016/8/15.
 */
public class HDRetrofit {

    public static <T> T create(final Class<T> cls) {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//用于Json数据的转换,非必须
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//用于返回Rxjava调用,非必须
                .build();
        return retrofit.create(cls);
    }
}
