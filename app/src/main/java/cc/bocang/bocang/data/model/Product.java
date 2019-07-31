package cc.bocang.bocang.data.model;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author Jun
 * @time 2016/10/19  17:41
 * @desc ${TODD}
 */
@JSONType(orders={"goods_id","goods_name","goods_price","number","msg","goodsPath"})
public class Product {
    private int goods_id;//产品的ID
    private String goods_name;//产品的名称
    private String goods_price;// 产品市场售价
    private String number;//产品数量
    private String msg;//产品备注
    private String goodsPath;//产品的URL名称



    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getGoodsPath() {
        return goodsPath;
    }

    public void setGoodsPath(String goodsPath) {
        this.goodsPath = goodsPath;
    }

    @Override
    public String toString() {
        return "Product{" +
                "goods_id=" + goods_id +
                ", goods_name='" + goods_name + '\'' +
                ", goods_price='" + goods_price + '\'' +
                ", number='" + number + '\'' +
                ", msg='" + msg + '\'' +
                ", goodsPath='" + goodsPath + '\'' +
                '}';
    }
}
