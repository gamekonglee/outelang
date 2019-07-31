package cc.bocang.bocang.data.model;

/**
 * 版本更新
 *
 * @author HeYan
 * @time 2016/7/1 12:55
 */
public class AppVersion {
    private String id;
    private String name;
    private String version;
    private String publishedAt;
    private String des;
    private String forcedUpdate;
    private String type;
    private String url;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getForcedUpdate() {
        return forcedUpdate;
    }

    public void setForcedUpdate(String forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
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

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
