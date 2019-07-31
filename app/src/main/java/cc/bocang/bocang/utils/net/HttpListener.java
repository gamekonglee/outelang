package cc.bocang.bocang.utils.net;

/**
 * @author : Jun
 * <p>接受回调结果</p>
 */
public interface HttpListener {

    /**
     * 请求成功,ans必定有值
     *
     * @param ans
     */
    void onSuccessListener(int what, String ans);

    /**
     * 请求失败,ans可能有值,可能为null
     *
     * @param ans
     */
    void onFailureListener(int what, String ans);

}
