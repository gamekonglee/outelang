package cc.bocang.bocang.data.response;

/**
 * Created by xpHuang on 2016/8/17.
 */
public class UpdateUserStatusResp {
    private boolean success;

    @Override
    public String toString() {
        return "UpdateUserStatusResp{" +
                "success=" + success +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
