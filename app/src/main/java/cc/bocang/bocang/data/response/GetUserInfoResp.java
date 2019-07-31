package cc.bocang.bocang.data.response;

import cc.bocang.bocang.data.model.UserInfo;

/**
 * Created by xpHuang on 2016/8/16.
 */
public class GetUserInfoResp {
    private boolean success;
    private UserInfo bean;
    private int errorCode;//0为不可用，2为未注册


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserInfo getBean() {
        return bean;
    }

    public void setBean(UserInfo bean) {
        this.bean = bean;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "GetUserInfoResp{" +
                "success=" + success +
                ", bean=" + bean +
                ", errorCode=" + errorCode +
                '}';
    }
}
