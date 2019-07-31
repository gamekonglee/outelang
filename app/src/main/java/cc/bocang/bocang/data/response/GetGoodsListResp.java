package cc.bocang.bocang.data.response;

import java.util.List;

import cc.bocang.bocang.data.model.GoodsAllAttr;
import cc.bocang.bocang.data.model.Goods;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GetGoodsListResp {
    private boolean success;
    private List<GoodsAllAttr> goodsAllAttrs;//所有属性列表
    private List<Goods> goodses;//商品列表

    @Override
    public String toString() {
        return "GetGoodsListResp{" +
                "success=" + success +
                ", goodsAllAttrs=" + goodsAllAttrs +
                ", goodses=" + goodses +
                '}';
    }

    public List<Goods> getGoodses() {
        return goodses;
    }

    public void setGoodses(List<Goods> goodses) {
        this.goodses = goodses;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<GoodsAllAttr> getGoodsAllAttrs() {
        return goodsAllAttrs;
    }

    public void setGoodsAllAttrs(List<GoodsAllAttr> goodsAllAttrs) {
        this.goodsAllAttrs = goodsAllAttrs;
    }
}
