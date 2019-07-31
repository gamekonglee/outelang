package cc.bocang.bocang.data.response;

import java.util.List;

import cc.bocang.bocang.data.model.Distributor;

/**
 * Created by xpHuang on 2016/8/17.
 */
public class GetDistributorResp {
    private boolean success;
    private List<Distributor> beans;

    @Override
    public String toString() {
        return "GetDistributorResp{" +
                "success=" + success +
                ", beans=" + beans +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Distributor> getBeans() {
        return beans;
    }

    public void setBeans(List<Distributor> beans) {
        this.beans = beans;
    }
}
