package cc.bocang.bocang.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class Goods implements Serializable{
    private int id;//产品的ID
    private String name;//产品的名称
    private String img_url;//产品的URL名称
    private String market_price;// 产品市场售价
    private float shop_price;//产品的app售价
    private String goods_desc;//HTML，产品的详情页
    private String sort;//排序
    private String add_time;//添加时间
    private String is_best;//是否推荐产品（精品），1为是，0为不是
    private String is_new;//是否最新产品（新品），1为是，0为不是
    private String is_hot;//是否热卖产品（热品），1为是，0为不是
    private String is_on_sale;//是否上架销售，1为是，0为不是
    private String goods_number;//产品数量
    private String class_id;//分类ID
    private String goods_type;//商品类型
    private String click;//点击次数
    private double agio;//折扣

    public double getAgio() {
        return agio;
    }

    public void setAgio(double agio) {
        this.agio = agio;
    }

    private List<GoodsPro> goodsPros;//产品的属性
    private List<GoodsSpe> goodsSpes;//产品的规格
    private List<GoodsGallery> goodsGalleries;//产品的产品的浏览图
    public boolean delete;//购物车里的产品是否处于选中状态
    public int buyCount;

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", img_url='" + img_url + '\'' +
                ", market_price='" + market_price + '\'' +
                ", shop_price=" + shop_price +
                ", goods_desc='" + goods_desc + '\'' +
                ", sort='" + sort + '\'' +
                ", add_time='" + add_time + '\'' +
                ", is_best='" + is_best + '\'' +
                ", is_new='" + is_new + '\'' +
                ", is_hot='" + is_hot + '\'' +
                ", is_on_sale='" + is_on_sale + '\'' +
                ", goods_number='" + goods_number + '\'' +
                ", class_id='" + class_id + '\'' +
                ", goods_type='" + goods_type + '\'' +
                ", click='" + click + '\'' +
                ", goodsPros=" + goodsPros +
                ", goodsSpes=" + goodsSpes +
                ", goodsGalleries=" + goodsGalleries +
                ", delete=" + delete +
                ", buyCount=" + buyCount +
                '}';
    }

    public String getGoods_desc() {
        return goods_desc;
    }

    public void setGoods_desc(String goods_desc) {
        this.goods_desc = goods_desc;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getIs_on_sale() {
        return is_on_sale;
    }

    public void setIs_on_sale(String is_on_sale) {
        this.is_on_sale = is_on_sale;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public List<GoodsSpe> getGoodsSpes() {
        return goodsSpes;
    }

    public void setGoodsSpes(List<GoodsSpe> goodsSpes) {
        this.goodsSpes = goodsSpes;
    }

    public List<GoodsGallery> getGoodsGalleries() {
        return goodsGalleries;
    }

    public void setGoodsGalleries(List<GoodsGallery> goodsGalleries) {
        this.goodsGalleries = goodsGalleries;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public float getShop_price() {
        return shop_price;
    }

    public void setShop_price(float shop_price) {
        this.shop_price = shop_price;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIs_best() {
        return is_best;
    }

    public void setIs_best(String is_best) {
        this.is_best = is_best;
    }

    public String getIs_new() {
        return is_new;
    }

    public void setIs_new(String is_new) {
        this.is_new = is_new;
    }

    public String getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(String is_hot) {
        this.is_hot = is_hot;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }

    public String getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(String goods_number) {
        this.goods_number = goods_number;
    }

    public List<GoodsPro> getGoodsPros() {
        return goodsPros;
    }

    public void setGoodsPros(List<GoodsPro> goodsPros) {
        this.goodsPros = goodsPros;
    }
}
