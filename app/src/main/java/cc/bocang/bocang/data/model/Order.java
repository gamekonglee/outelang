package cc.bocang.bocang.data.model;

import com.alibaba.fastjson.annotation.JSONType;


/**
 * @author Jun
 * @time 2016/10/19  17:29
 * @desc ${TODD}
 */
@JSONType(orders={"user_id","orser_sum","order_name","order_phone","delivery_time","order_address"})
public class Order {
    private String user_id;//注册用户id
    private String orser_sum;//订单总价
    private String order_name;//客户名称
    private String order_phone;// 客户电话
    private String delivery_time;//交货时间
    private String order_address;//客户地址

    public Order(String user_id, String orser_sum, String order_name, String order_phone, String delivery_time, String order_address) {
        this.user_id = user_id;
        this.orser_sum = orser_sum;
        this.order_name = order_name;
        this.order_phone = order_phone;
        this.delivery_time = delivery_time;
        this.order_address = order_address;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrser_sum() {
        return orser_sum;
    }

    public void setOrser_sum(String orser_sum) {
        this.orser_sum = orser_sum;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getOrder_phone() {
        return order_phone;
    }

    public void setOrder_phone(String order_phone) {
        this.order_phone = order_phone;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getOrder_address() {
        return order_address;
    }

    public void setOrder_address(String order_address) {
        this.order_address = order_address;
    }

    @Override
    public String toString() {
        return "Order{" +
                "user_id=" + user_id +
                ", orser_sum=" + orser_sum +
                ", order_name='" + order_name + '\'' +
                ", order_phone='" + order_phone + '\'' +
                ", delivery_time='" + delivery_time + '\'' +
                ", order_address='" + order_address + '\'' +
                '}';
    }
}
