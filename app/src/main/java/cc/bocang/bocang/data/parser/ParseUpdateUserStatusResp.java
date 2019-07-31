package cc.bocang.bocang.data.parser;


import org.json.JSONObject;
import cc.bocang.bocang.data.response.UpdateUserStatusResp;

/**
 * Created by xpHuang on 2016/8/16.
 */
public class ParseUpdateUserStatusResp {
    /**
     *
     * @param json
     * @return 成功返回UpdateUserStatusResp，错误返回null或Resp.success==false
     */
    public static UpdateUserStatusResp parse(String json) {
        if (null == json) {
            return null;
        }
        try {
            UpdateUserStatusResp resp = new UpdateUserStatusResp();
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.optString("result");
            if ("success".equals(result)) {
                resp.setSuccess(true);
            } else if ("error".equals(result)) {
                resp.setSuccess(false);
            }

            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
