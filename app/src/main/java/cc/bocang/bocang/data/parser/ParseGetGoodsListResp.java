package cc.bocang.bocang.data.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.model.GoodsAttr;
import cc.bocang.bocang.data.model.Goods;
import cc.bocang.bocang.data.model.GoodsPro;
import cc.bocang.bocang.data.response.GetGoodsListResp;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class ParseGetGoodsListResp {

    private static int current;
    private static String currentName;

    /**
     * @param json
     * @return 成功返回XXXResp，错误返回null或XXXResp.success==false
     */
    public static GetGoodsListResp parse(String json) {
        if (null == json) {
            return null;
        }
        try {
            GetGoodsListResp resp = new GetGoodsListResp();
            List<GoodsAllAttr> goodsAllAttrs = new ArrayList<>();
            List<Goods> goodses = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);

            JSONArray allAttrArray = jsonObject.optJSONArray("all_attr_list");
            for (int i = 0; i < allAttrArray.length(); i++) {
                GoodsAllAttr goodsAllAttr = new GoodsAllAttr();
                List<GoodsAttr> goodsAttrs = new ArrayList<>();
                JSONObject allAttrObj = allAttrArray.optJSONObject(i);
                String attrName = allAttrObj.optString("filter_attr_name");
                goodsAllAttr.setAttrName(attrName);
                JSONArray attrArray = allAttrObj.optJSONArray("attr_list");
                for (int j = 0; j < attrArray.length(); j++) {
                    GoodsAttr goodsAttr = new GoodsAttr();
                    JSONObject attrObj = attrArray.optJSONObject(j);
                    String attr_value = attrObj.optString("attr_value");
                    String url = attrObj.optString("url");
                    String goods_id = attrObj.optString("goods_id");
                    String selected = attrObj.optString("selected");
                    goodsAttr.setAttr_value(attr_value);
                    goodsAttr.setUrl(url);
                    if ("".equals(goods_id))
                        goodsAttr.setGoods_id(0);
                    else
                        goodsAttr.setGoods_id(Integer.parseInt(goods_id));
                    goodsAttr.setSelected(selected);

                    goodsAttrs.add(goodsAttr);
                }

                goodsAllAttr.setGoodsAttrs(goodsAttrs);

                goodsAllAttrs.add(goodsAllAttr);
            }

            JSONArray goodsArray = jsonObject.optJSONArray("goodslist");
            if (null != goodsArray) {
                for (int i = 0; i < goodsArray.length(); i++) {
                    Goods goods = new Goods();
                    List<GoodsPro> goodsPros = new ArrayList<>();
                    JSONObject goodsObj = goodsArray.optJSONObject(i);
                    int id = goodsObj.optInt("id");
                    String name = goodsObj.optString("name");
                    String img_url = goodsObj.optString("img_url");
                    String shop_price = goodsObj.optString("shop_price");
                    String sort = goodsObj.optString("sort");
                    String is_best = goodsObj.optString("is_best");
                    String is_new = goodsObj.optString("is_new");
                    String is_hot = goodsObj.optString("is_hot");
                    String click = goodsObj.optString("click");
                    String market_price = goodsObj.optString("market_price");
                    String goods_number = goodsObj.optString("goods_number");
                    goods.setId(id);
                    goods.setName(name);
                    goods.setImg_url(img_url);
                    current = i;
                    currentName = name;
                    goods.setShop_price(Float.parseFloat(shop_price));
                    goods.setSort(sort);
                    goods.setIs_best(is_best);
                    goods.setIs_new(is_new);
                    goods.setIs_hot(is_hot);
                    goods.setClick(click);
                    goods.setMarket_price(market_price);
                    goods.setGoods_number(goods_number);
                    JSONArray goodsAttrArray = goodsObj.optJSONArray("attr");
                    for (int j = 0; j < goodsAttrArray.length(); j++) {
                        GoodsPro goodsPro = new GoodsPro();
                        JSONObject goodsAttrObj = goodsAttrArray.optJSONObject(j);
                        name = goodsAttrObj.optString("name");
                        String value = goodsAttrObj.optString("value");
                        goodsPro.setName(name);
                        goodsPro.setValue(value);

                        goodsPros.add(goodsPro);
                    }
                    goods.setGoodsPros(goodsPros);
                    goodses.add(goods);
                }
            }
            resp.setSuccess(true);
            resp.setGoodsAllAttrs(goodsAllAttrs);
            resp.setGoodses(goodses);

            return resp;

        } catch (Exception e) {
            Log.e("currentname",currentName);
            e.printStackTrace();
            return null;
        }
    }
}
