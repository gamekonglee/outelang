package cc.bocang.bocang.data.model;

/**
 * @author Jun
 * @time 2016/10/24  11:23
 * @desc 产品分类
 */
public class GoodsClass {
    private String delimiter;
    private int id;
    private String level;
    private String name;
    private String pid;
    private String sort;
    private String  status;

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GoodsClass{" +
                "delimiter='" + delimiter + '\'' +
                ", id='" + id + '\'' +
                ", level='" + level + '\'' +
                ", name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", sort='" + sort + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
