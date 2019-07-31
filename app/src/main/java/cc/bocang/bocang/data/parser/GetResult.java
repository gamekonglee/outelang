package cc.bocang.bocang.data.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import cc.bocang.bocang.data.model.Result;

/**
 * @author Jun
 * @time 2016/10/19  17:19
 * @desc ${TODD}
 */
public class GetResult {

    private static Result bean;

    public static Result parse(String json) {
        if (null == json) {
            return null;
        }

        try {
            bean = new Result();

            JSONArray dataArray = new JSONArray(json);
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObj = dataArray.getJSONObject(i);
                String result = dataObj.optString("result");
                Result bean=new Result();
                bean.setResult(result);
            }
            return bean;

        } catch (Exception e) {
            e.printStackTrace();
            return bean;
        }
    }
}
