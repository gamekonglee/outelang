package cc.bocang.bocang.data.response;

import cc.bocang.bocang.data.model.Goods;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GetGoodsInfoResp {
    private boolean success;
    private Goods goods;//商品

    @Override
    public String toString() {
        return "GetGoodsInfoResp{" +
                "success=" + success +
                ", goods=" + goods +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }
}
