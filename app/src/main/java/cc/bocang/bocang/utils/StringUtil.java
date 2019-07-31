package cc.bocang.bocang.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpHuang on 2016/8/21.
 */
public class StringUtil {

    /**
     * 为一个字符串列表的所有元素加上相同的前缀
     *
     * @param str  前缀
     * @param strs 字符串列表
     * @return List<String>
     */
    public static List<String> preToStringArray(String str, List<String> strs) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < strs.size(); i++) {
            String temp = str + strs.get(i);
            list.add(temp);
        }
        return list;
    }

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof String) {
            return ((String) o).trim().isEmpty() ? true : false;
        } else {
            return false;
        }
    }
}
