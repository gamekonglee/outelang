package cc.bocang.bocang.data.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.data.model.SceneAllAttr;
import cc.bocang.bocang.data.model.SceneAttr;
import cc.bocang.bocang.data.response.GetSceneListResp;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class ParseGetSceneListResp {
    /**
     * @param json
     * @return 成功返回XXXResp，错误返回null或XXXResp.success==false
     */
    public static GetSceneListResp parse(String json) {
        if (null == json) {
            return null;
        }
        try {
            GetSceneListResp resp = new GetSceneListResp();

            List<SceneAllAttr> sceneAllAttrs = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(json);

            JSONArray allAttrArray = jsonObject.optJSONArray("all_attr_list");
            for (int i = 0; i < allAttrArray.length(); i++) {

                SceneAllAttr sceneAllAttr = new SceneAllAttr();
                List<SceneAttr> sceneAttrs = new ArrayList<>();

                JSONObject allAttrObj = allAttrArray.optJSONObject(i);
                String attrName = allAttrObj.optString("filter_attr_name");

                sceneAllAttr.setAttrName(attrName);

                JSONArray attrArray = allAttrObj.optJSONArray("attr_list");
                for (int j = 0; j < attrArray.length(); j++) {
                    SceneAttr sceneAttr = new SceneAttr();
                    JSONObject attrObj = attrArray.optJSONObject(j);
                    String attr_value = attrObj.optString("attr_value");
                    String url = attrObj.optString("url");
                    String scene_id = attrObj.optString("scene_id");
                    String selected = attrObj.optString("selected");

                    sceneAttr.setAttr_value(attr_value);
                    sceneAttr.setUrl(url);
                    if ("".equals(scene_id))
                        sceneAttr.setScene_id(0);
                    else
                        sceneAttr.setScene_id(Integer.parseInt(scene_id));
                    sceneAttr.setSelected(selected);

                    sceneAttrs.add(sceneAttr);
                }

                sceneAllAttr.setSceneAttrs(sceneAttrs);

                sceneAllAttrs.add(sceneAllAttr);
            }

            List<Scene> scenes = new ArrayList<>();

            JSONArray sceneArray = jsonObject.optJSONArray("scenelist");
            for (int i = 0; i < sceneArray.length(); i++) {
                Scene scene = new Scene();
                JSONObject sceneObj = sceneArray.optJSONObject(i);
                String id = sceneObj.optString("id");
                String name = sceneObj.optString("name");
                String path = sceneObj.optString("path");
                String sort = sceneObj.optString("sort");
                String is_best = sceneObj.optString("is_best");
                String click = sceneObj.optString("click");
                scene.setId(id);
                scene.setName(name);
                scene.setPath(path);
                scene.setSort(sort);
                scene.setIs_best(is_best);
                scene.setClick(click);
                scenes.add(scene);
            }

            resp.setSuccess(true);
            resp.setSceneAllAttrs(sceneAllAttrs);
            resp.setScenes(scenes);

            return resp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
