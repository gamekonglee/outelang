package cc.bocang.bocang.data.model;

/**
 * Created by xpHuang on 2016/8/17.
 */
public class Ad {
    private String id;//id值
    private String name;//名称
    private String cat_id;//产品分类ID值
    private String type_id;//类型值，1为平板首页背景图，2为平板产品列表页广告图，3为手机首页广告图，4为平板启动页，5为品牌背景图
    private String is_use;//是否可用，1为可用，0为不可用
    private String sort;//排序
    private String path;//图片的路径名

    @Override
    public String toString() {
        return "Ad{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cat_id='" + cat_id + '\'' +
                ", type_id='" + type_id + '\'' +
                ", is_use='" + is_use + '\'' +
                ", sort='" + sort + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getIs_use() {
        return is_use;
    }

    public void setIs_use(String is_use) {
        this.is_use = is_use;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
