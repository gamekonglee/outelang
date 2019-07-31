package cc.bocang.bocang.data.model;

/**
 * Created by xpHuang on 2016/8/16.
 */
public class UserInfo {
    private int id;//用户ID
    private String name;//用户名称
    private String address;//地址
    private String phone;//手机号码
    private String signid;//唯一标识
    private String add_time;//注册时间
    private String invite_code;//邀请码
    private String pid;//父级用户ID
    private int is_use;//是否可用，1为可用，0为不可用
    private String zhu_phone;//注册的手机号码
    private String multiple;


    public String getMultiple() {
        return multiple;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", signid='" + signid + '\'' +
                ", add_time='" + add_time + '\'' +
                ", invite_code='" + invite_code + '\'' +
                ", pid='" + pid + '\'' +
                ", is_use='" + is_use + '\'' +
                ", zhu_phone='" + zhu_phone + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSignid() {
        return signid;
    }

    public void setSignid(String signid) {
        this.signid = signid;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getIs_use() {
        return is_use;
    }

    public void setIs_use(int is_use) {
        this.is_use = is_use;
    }

    public String getZhu_phone() {
        return zhu_phone;
    }

    public void setZhu_phone(String zhu_phone) {
        this.zhu_phone = zhu_phone;
    }
}
