package cc.bocang.bocang.data.response;


/**
 * Created by xpHuang on 2016/8/16.
 */
public class AddUserResp {
    private boolean success;
    private int errorCode;//0为失败，2为邀请码错误

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "AddUserResp{" +
                "success=" + success +
                ", errorCode=" + errorCode +
                '}';
    }
}
