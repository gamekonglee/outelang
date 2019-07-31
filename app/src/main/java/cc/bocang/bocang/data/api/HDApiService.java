package cc.bocang.bocang.data.api;


import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by xpHuang on 2016/8/15.
 */
public interface HDApiService {
    /**
     * Call<ResponseBody> :Call是必须的,ResponseBody是Retrofit默认的返回数据类型,也就是String体
     */


    @FormUrlEncoded
    @POST("/Interface/add_user")
    Call<ResponseBody> addUser(@Field("type") int type, @Field("name") String name, @Field("address") String address,
                               @Field("phone") String phone, @Field("signid") String signid, @Field("invite_code") String invite_code);

    @FormUrlEncoded
    @POST("/Interface/get_user_info")
    Call<ResponseBody> getUserInfo(@Field("signid") String signid);

    @FormUrlEncoded
    @POST("/Interface/update_user_status")
    Call<ResponseBody> updateUserStatus(@Field("user_id") int user_id, @Field("status") int status);

    @FormUrlEncoded
    @POST("/Interface/get_fenxiao")
    Call<ResponseBody> getFenXiao(@Field("user_id") int user_id);

    @FormUrlEncoded
    @POST("/Interface/get_adv")
    Call<ResponseBody> getAd(@Field("id") int id);

    @FormUrlEncoded
    @POST("/Interface/get_goods_list")
    Call<ResponseBody> getGoodsList(@Field("c_id") String c_id, @Field("page") int page,@Field("okcat_id") int okcat_id,
                                    @Field("keywords") String keywords, @Field("type") String type,
                                    @Field("filter_attr") String filter_attr);

    @FormUrlEncoded
    @POST("/Interface/get_goods_list")
    Call<ResponseBody> getGoodsListItem(@Field("c_id") int c_id, @Field("page") int page );

    @FormUrlEncoded
    @POST("/Interface/get_goods_list")
    Call<ResponseBody> getSearchGoodsList(@Field("c_id") int c_id,@Field("page") int page,@Field("okcat_id") int okcat_id,
                                          @Field("keywords") String keywords);

    @FormUrlEncoded
    @POST("/Interface/get_goods_info")
    Call<ResponseBody> getGoodsInfo(@Field("id") int id);


    @FormUrlEncoded
    @POST("/Interface/get_scene_list")
    Call<ResponseBody> getSceneList(@Field("c_id") int c_id, @Field("page") int page,
                                    @Field("keywords") String keywords, @Field("type") String type,
                                    @Field("filter_attr") String filter_attr);
    //提交订单
    @FormUrlEncoded
    @POST("/index.php/Interface/upload_order")
    Call<ResponseBody> submitOrder(@Field("order") String order, @Field("product") String product);


    //设置倍数
    @FormUrlEncoded
    @POST("/Interface/upload_user_multiple")
    Call<ResponseBody> getMultiple(@Field("user_id") int userId, @Field("multiple") double multiple);
}
