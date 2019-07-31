package cc.bocang.bocang.data.model;

import java.util.List;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GoodsAllAttr {
    private String attrName;//属性名称：类型、空间、风格等
    private List<GoodsAttr> goodsAttrs;//该属性下拉列表的所有值

    @Override
    public String toString() {
        return "GoodsAllAttr{" +
                "attrName='" + attrName + '\'' +
                ", goodsAttrs=" + goodsAttrs +
                '}';
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public List<GoodsAttr> getGoodsAttrs() {
        return goodsAttrs;
    }

    public void setGoodsAttrs(List<GoodsAttr> goodsAttrs) {
        this.goodsAttrs = goodsAttrs;
    }
}
