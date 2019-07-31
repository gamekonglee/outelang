package cc.bocang.bocang.data.model;

import java.io.Serializable;

/**
 * Created by xpHuang on 2016/8/18.
 */
public class GoodsPro  implements Serializable {
    private String name;// 产品的属性的名称
    private String value;// 产品的属性的值

    @Override
    public String toString() {
        return "GoodsPro{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
