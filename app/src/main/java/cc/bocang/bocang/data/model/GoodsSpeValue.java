package cc.bocang.bocang.data.model;

import java.io.Serializable;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GoodsSpeValue implements Serializable {
    private String label;//名字
    private String price;//产品的App售价
    private String goods_attr_id;//产品属性ID值

    @Override
    public String toString() {
        return "GoodsSpeValue{" +
                "label='" + label + '\'' +
                ", price='" + price + '\'' +
                ", goods_attr_id='" + goods_attr_id + '\'' +
                '}';
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoods_attr_id() {
        return goods_attr_id;
    }

    public void setGoods_attr_id(String goods_attr_id) {
        this.goods_attr_id = goods_attr_id;
    }
}
