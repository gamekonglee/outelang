package cc.bocang.bocang.data.model;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GoodsAttr {
    private String attr_value;//类型的名称
    private String url;//类型的链接
    private int goods_id;//该类型的筛选值（作为获取产品的参数）
    private String selected;//类型的选择次数

    @Override
    public String toString() {
        return "GoodsAttr{" +
                "attr_value='" + attr_value + '\'' +
                ", url='" + url + '\'' +
                ", goods_id=" + goods_id +
                ", selected='" + selected + '\'' +
                '}';
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
