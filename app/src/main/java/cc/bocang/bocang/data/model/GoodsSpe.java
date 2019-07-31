package cc.bocang.bocang.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GoodsSpe implements Serializable {
    private int id;//id值
    private String attr_type;//属性类型
    private String name;//名字
    private List<GoodsSpeValue> goodsSpeValues;//内容列表

    @Override
    public String toString() {
        return "GoodsSpe{" +
                "id='" + id + '\'' +
                ", attr_type='" + attr_type + '\'' +
                ", name='" + name + '\'' +
                ", goodsSpeValues=" + goodsSpeValues +
                '}';
    }

    public List<GoodsSpeValue> getGoodsSpeValues() {
        return goodsSpeValues;
    }

    public void setGoodsSpeValues(List<GoodsSpeValue> goodsSpeValues) {
        this.goodsSpeValues = goodsSpeValues;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAttr_type() {
        return attr_type;
    }

    public void setAttr_type(String attr_type) {
        this.attr_type = attr_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
