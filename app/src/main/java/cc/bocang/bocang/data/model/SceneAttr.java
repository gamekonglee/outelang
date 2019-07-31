package cc.bocang.bocang.data.model;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class SceneAttr {
    private String attr_value;//类型的名称
    private String url;//类型的链接
    private int scene_id;//该类型的筛选值（作为获取场景的参数）
    private String selected;//类型的选择次数

    @Override
    public String toString() {
        return "SceneAttr{" +
                "attr_value='" + attr_value + '\'' +
                ", url='" + url + '\'' +
                ", scene_id=" + scene_id +
                ", selected='" + selected + '\'' +
                '}';
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getScene_id() {
        return scene_id;
    }

    public void setScene_id(int scene_id) {
        this.scene_id = scene_id;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
