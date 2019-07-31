package cc.bocang.bocang.data.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsGallery;
import cc.bocang.bocang.data.model.GoodsPro;
import cc.bocang.bocang.data.model.GoodsSpe;
import cc.bocang.bocang.data.model.GoodsSpeValue;
import cc.bocang.bocang.data.response.GetGoodsInfoResp;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class ParseGetGoodsInfoResp {
    /**
     * @param json
     * @return 成功返回GetGoodsInfoResp，错误返回null或Resp.success==false
     */
    public static GetGoodsInfoResp parse(String json) {
        if (null == json) {
            return null;
        }
        try {
            GetGoodsInfoResp resp = new GetGoodsInfoResp();
            JSONObject goodsObj = new JSONObject(json);
            int id = goodsObj.optInt("id");
            String name = goodsObj.optString("name");
            String img_url = goodsObj.optString("img_url");
            String market_price = goodsObj.optString("market_price");
            String shop_price = goodsObj.optString("shop_price");
            String goods_desc = goodsObj.optString("goods_desc");
            String sort = goodsObj.optString("sort");
            String add_time = goodsObj.optString("add_time");
            String is_best = goodsObj.optString("is_best");
            String is_new = goodsObj.optString("is_new");
            String is_hot = goodsObj.optString("is_hot");
            String is_on_sale = goodsObj.optString("is_on_sale");
            String goods_number = goodsObj.optString("goods_number");
            String class_id = goodsObj.optString("class_id");
            String goods_type = goodsObj.optString("goods_type");
            String click = goodsObj.optString("click");

            Goods goods = new Goods();
            goods.setId(id);
            goods.setName(name);
            goods.setImg_url(img_url);
            goods.setMarket_price(market_price);
            goods.setShop_price(Float.parseFloat(shop_price));
            goods.setGoods_desc(goods_desc);
            goods.setSort(sort);
            goods.setAdd_time(add_time);
            goods.setIs_best(is_best);
            goods.setIs_new(is_new);
            goods.setIs_hot(is_hot);
            goods.setIs_on_sale(is_on_sale);
            goods.setGoods_number(goods_number);
            goods.setClass_id(class_id);
            goods.setGoods_type(goods_type);
            goods.setClick(click);

            JSONObject attrObj = goodsObj.optJSONObject("attr");
            JSONArray proArray = attrObj.optJSONArray("pro");
            List<GoodsPro> goodsPros = new ArrayList<>();
            for (int i = 0; i < proArray.length(); i++) {
                JSONObject proObj = proArray.optJSONObject(i);
                GoodsPro pro = new GoodsPro();
                String proName = proObj.optString("name");
                String value = proObj.optString("value");
                pro.setName(proName);
                pro.setValue(value);

                goodsPros.add(pro);
            }

            JSONArray speArray = attrObj.optJSONArray("spe");
            List<GoodsSpe> goodsSpes = new ArrayList<>();
            for (int i = 0; i < speArray.length(); i++) {
                JSONObject speObj = speArray.optJSONObject(i);
                GoodsSpe spe = new GoodsSpe();
                id = speObj.optInt("id");
                String attr_type = speObj.optString("attr_type");
                String speName = speObj.optString("name");
                JSONArray valuesArray = speObj.optJSONArray("values");
                List<GoodsSpeValue> goodsSpeValues = new ArrayList<>();
                for(int j = 0; j < valuesArray.length(); j++){
                    JSONObject valuesObj = valuesArray.optJSONObject(j);
                    String label = valuesObj.optString("label");
                    String price = valuesObj.optString("price");
                    String goods_attr_id = valuesObj.optString("goods_attr_id");
                    GoodsSpeValue goodsSpeValue = new GoodsSpeValue();
                    goodsSpeValue.setLabel(label);
                    goodsSpeValue.setPrice(price);
                    goodsSpeValue.setGoods_attr_id(goods_attr_id);

                    goodsSpeValues.add(goodsSpeValue);
                }
                spe.setId(id);
                spe.setAttr_type(attr_type);
                spe.setName(speName);
                spe.setGoodsSpeValues(goodsSpeValues);

                goodsSpes.add(spe);
            }

            JSONArray galleryArray = goodsObj.optJSONArray("gallery");
            List<GoodsGallery> goodsGalleries = new ArrayList<>();
            for(int i = 0; i < galleryArray.length(); i++){
                JSONObject galleryObj = galleryArray.optJSONObject(i);
                id = galleryObj.optInt("id");
                String goods_id = galleryObj.optString("goods_id");
                img_url = galleryObj.optString("img_url");
                sort = galleryObj.optString("sort");
                String filetype = galleryObj.optString("filetype");
                String filesize = galleryObj.optString("filesize");
                String uploadtime = galleryObj.optString("uploadtime");
                GoodsGallery gallery = new GoodsGallery();
                gallery.setId(id);
                gallery.setGoods_id(goods_id);
                gallery.setImg_url(img_url);
                gallery.setSort(sort);
                gallery.setFiletype(filetype);
                gallery.setFilesize(filesize);
                gallery.setUploadtime(uploadtime);

                goodsGalleries.add(gallery);
            }

            goods.setGoodsPros(goodsPros);
            goods.setGoodsSpes(goodsSpes);
            goods.setGoodsGalleries(goodsGalleries);

            if(0 != id){//产品不为空
                resp.setSuccess(true);
                resp.setGoods(goods);
            }

            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
