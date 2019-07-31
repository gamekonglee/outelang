package cc.bocang.bocang.data.response;

import java.util.List;

import cc.bocang.bocang.data.model.Ad;

/**
 * Created by xpHuang on 2016/8/17.
 */
public class GetAdResp {
    private boolean success;
    private List<Ad> beans;

    @Override
    public String toString() {
        return "GetAdResp{" +
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

    public List<Ad> getBeans() {
        return beans;
    }

    public void setBeans(List<Ad> beans) {
        this.beans = beans;
    }
}
