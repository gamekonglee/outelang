package cc.bocang.bocang.data.parser;

import org.json.JSONObject;

import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.data.response.GetUserInfoResp;

/**
 * Created by xpHuang on 2016/8/16.
 */
public class ParseGetUserInfoResp {
    /**
     * @param json
     * @return 成功返回XXXResp，错误返回null或Resp.success==false
     */
    public static GetUserInfoResp parse(String json) {
        if (null == json) {
            return null;
        }
        try {
            GetUserInfoResp resp = new GetUserInfoResp();
            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.optString("result");
            if ("success".equals(result)) {
                String data = jsonObject.optString("data");
                JSONObject dataObj = new JSONObject(data);

                String id = dataObj.optString("id");
                String name = dataObj.optString("name");
                String address = dataObj.optString("address");
                String phone = dataObj.optString("phone");
                String signid = dataObj.optString("signid");
                String add_time = dataObj.optString("add_time");
                String invite_code = dataObj.optString("invite_code");
                String pid = dataObj.optString("pid");
                String is_use = dataObj.optString("is_use");
                String zhu_phone = dataObj.optString("zhu_phone");
                String multiple = dataObj.optString("multiple");

                UserInfo bean = new UserInfo();
                bean.setId(Integer.parseInt(id));
                bean.setName(name);
                bean.setAddress(address);
                bean.setPhone(phone);
                bean.setSignid(signid);
                bean.setAdd_time(add_time);
                bean.setInvite_code(invite_code);
                bean.setPid(pid);
                bean.setIs_use(Integer.parseInt(is_use));
                bean.setZhu_phone(zhu_phone);
                bean.setMultiple(multiple);

                resp.setSuccess(true);
                resp.setBean(bean);

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
