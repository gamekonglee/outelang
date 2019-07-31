package cc.bocang.bocang.data.parser;

import org.json.JSONObject;

import cc.bocang.bocang.data.response.AddUserResp;

/**
 * Created by xpHuang on 2016/8/16.
 */
public class ParseAddUserResp {
    /**
     * @param json
     * @return 成功返回XXXResp，错误返回null或Resp.success==false
     */
    public static AddUserResp parse(String json) {
        if (null == json) {
            return null;
        }
        try {
            AddUserResp resp = new AddUserResp();
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.optString("result");
            if ("success".equals(result)) {

                resp.setSuccess(true);

            } else if ("error".equals(result)) {

                int errorCode = jsonObject.optInt("data");

                resp.setSuccess(false);
                resp.setErrorCode(errorCode);
            }

            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
