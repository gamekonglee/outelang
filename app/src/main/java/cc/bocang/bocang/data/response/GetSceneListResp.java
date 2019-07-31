package cc.bocang.bocang.data.response;

import java.util.List;

import cc.bocang.bocang.data.model.Scene;
import cc.bocang.bocang.data.model.SceneAllAttr;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GetSceneListResp {
    private boolean success;
    private List<SceneAllAttr> sceneAllAttrs;//所有属性列表
    private List<Scene> scenes;//商品列表

    @Override
    public String toString() {
        return "GetSceneListResp{" +
                "success=" + success +
                ", sceneAllAttrs=" + sceneAllAttrs +
                ", scenes=" + scenes +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SceneAllAttr> getSceneAllAttrs() {
        return sceneAllAttrs;
    }

    public void setSceneAllAttrs(List<SceneAllAttr> sceneAllAttrs) {
        this.sceneAllAttrs = sceneAllAttrs;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }
}
