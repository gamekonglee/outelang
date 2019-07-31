package cc.bocang.bocang.data.model;

/**
 * Created by xpHuang on 2016/8/17.
 */
public class Distributor {
    private int id;//用户ID
    private String name;//用户名
    private String address;//地址
    private String phone;//手机号码
    private String signid;//唯一标识
    private long add_time;//注册的时间戳
    private String invite_code;//邀请码
    private String pid;//父ID
    private String is_use;//是否为可用，1为可用，0为不可用

    @Override
    public String toString() {
        return "Distributor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", signid='" + signid + '\'' +
                ", add_time='" + add_time + '\'' +
                ", invite_code='" + invite_code + '\'' +
                ", pid='" + pid + '\'' +
                ", is_use='" + is_use + '\'' +
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

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
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

    public String getIs_use() {
        return is_use;
    }

    public void setIs_use(String is_use) {
        this.is_use = is_use;
    }
}
