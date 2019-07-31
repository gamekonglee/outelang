package cc.bocang.bocang.data.model;

import java.util.List;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class SceneAllAttr {
    private String attrName;//属性名称：类型、空间、风格等
    private List<SceneAttr> sceneAttrs;//该属性下拉列表的所有值

    public String getAttrName() {
        return attrName;
    }

    @Override
    public String toString() {
        return "SceneAllAttr{" +
                "attrName='" + attrName + '\'' +
                ", sceneAttrs=" + sceneAttrs +
                '}';
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public List<SceneAttr> getSceneAttrs() {
        return sceneAttrs;
    }

    public void setSceneAttrs(List<SceneAttr> sceneAttrs) {
        this.sceneAttrs = sceneAttrs;
    }
}
