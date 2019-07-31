package cc.bocang.bocang.data.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cc.bocang.bocang.data.model.Distributor;
import cc.bocang.bocang.data.response.GetDistributorResp;

/**
 * Created by xpHuang on 2016/8/16.
 */
public class ParseGetDistributorResp {
    /**
     *
     * @param json
     * @return 成功返回GetDistributorResp，错误返回nullResp.success==false
     */
    public static GetDistributorResp parse(String json) {
        if (null == json) {
            return null;
        }
        try {
            GetDistributorResp resp = new GetDistributorResp();

            JSONObject jsonObject = new JSONObject(json);
            String result = jsonObject.optString("result");
            if ("error".equals(result)) {
                resp.setSuccess(false);

            } else {
                List<Distributor> beans = new ArrayList<>();

                JSONArray dataArray = new JSONArray(result);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject dataObj = dataArray.getJSONObject(i);

                    String id = dataObj.optString("id");
                    String name = dataObj.optString("name");
                    String address = dataObj.optString("address");
                    String phone = dataObj.optString("phone");
                    String signid = dataObj.optString("signid");
                    String add_time = dataObj.optString("add_time");
                    String invite_code = dataObj.optString("invite_code");
                    String pid = dataObj.optString("pid");
                    String is_use = dataObj.optString("is_use");

                    Distributor bean = new Distributor();
                    bean.setId(Integer.parseInt(id));
                    bean.setName(name);
                    bean.setAddress(address);
                    bean.setPhone(phone);
                    bean.setSignid(signid);
                    bean.setAdd_time(Long.parseLong(add_time));
                    bean.setInvite_code(invite_code);
                    bean.setPid(pid);
                    bean.setIs_use(is_use);

                    beans.add(bean);
                }
                resp.setSuccess(true);
                resp.setBeans(beans);

            }
            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
