package cc.bocang.bocang.data.model;

/**
 * @author Jun
 * @time 2016/10/19  17:15
 * @desc ${TODD}
 */
public class Result {
    String result;
    String code;
    String content;
    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "result='" + result + '\'' +
                '}';
    }
}
