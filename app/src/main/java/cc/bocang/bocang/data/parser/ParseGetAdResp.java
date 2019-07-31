package cc.bocang.bocang.data.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.data.model.Ad;
import cc.bocang.bocang.data.response.GetAdResp;

/**
 * Created by xpHuang on 2016/8/16.
 */
public class ParseGetAdResp {
    /**
     *
     * @param json
     * @return 成功返回GetAdResp，错误返回null或Resp.success==false
     */
    public static GetAdResp parse(String json) {
        if (null == json) {
            return null;
        }
        GetAdResp resp = new GetAdResp();

        try {
            List<Ad> beans = new ArrayList<>();

            JSONArray dataArray = new JSONArray(json);
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObj = dataArray.getJSONObject(i);
                String id = dataObj.optString("id");
                String name = dataObj.optString("name");
                String cat_id = dataObj.optString("cat_id");
                String type_id = dataObj.optString("type_id");
                String is_use = dataObj.optString("is_use");
                String sort = dataObj.optString("sort");
                String path = dataObj.optString("path");

                Ad bean = new Ad();
                bean.setId(id);
                bean.setName(name);
                bean.setCat_id(cat_id);
                bean.setType_id(type_id);
                bean.setIs_use(is_use);
                bean.setSort(sort);
                bean.setPath(path);

                beans.add(bean);
            }
            resp.setSuccess(true);
            resp.setBeans(beans);

            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            resp.setSuccess(false);
            return resp;
        }
    }
}
