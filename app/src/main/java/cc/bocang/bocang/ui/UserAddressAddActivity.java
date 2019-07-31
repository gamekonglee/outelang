package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.baiiu.filter.util.CommonUtil;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.dao.LogisticDao;
import cc.bocang.bocang.data.model.Logistics;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.utils.StringUtil;

/**
 * @author: Jun
 * @date : 2017/7/27 16:00
 * @description :
 */
public class UserAddressAddActivity extends BaseActivity implements View.OnClickListener {
    private Button btnSave,topLeftBtn;
    private TextView user_addr_editName,user_addr_editPhone,user_detail_addr;
    private CheckBox select_cb;
    public Logistics mLogistics;
    private Intent mIntent;
    private String  mId="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address_add);
        initData();
        initView();
       initViewData();
    }

    private void initViewData() {
        if(StringUtil.isEmpty(mLogistics))return;
        user_addr_editName.setText(mLogistics.getName());
        user_detail_addr.setText(mLogistics.getAddress());
        user_addr_editPhone.setText(mLogistics.getTel());
        if(mLogistics.getIsDefault()==0){
            select_cb.setChecked(false);
        }else{
            select_cb.setChecked(true);
        }
        mId=mLogistics.getId()+"";
    }

    private void initView() {
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        topLeftBtn = (Button) findViewById(R.id.topLeftBtn);
        topLeftBtn.setOnClickListener(this);
        user_addr_editName = (TextView)findViewById(R.id.user_addr_editName);
        user_addr_editPhone = (TextView)findViewById(R.id.user_addr_editPhone);
        user_detail_addr = (TextView)findViewById(R.id.user_detail_addr);
        select_cb = (CheckBox)findViewById(R.id.select_cb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave://保存
                sendAddLogistic();
                break;
            case R.id.topLeftBtn://返回
                finish();
                break;
        }
    }

    /**
     * 保存物流
     */
    public void sendAddLogistic() {
        String name=user_addr_editName.getText().toString();
        String phone=user_addr_editPhone.getText().toString();
        String address=user_detail_addr.getText().toString();
        if (StringUtil.isEmpty(name)) {
            Toast.makeText(this,"收货人名称不能为空!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(phone)) {
            Toast.makeText(this,"收货人电话不能为空!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(address)) {
            Toast.makeText(this,"收货地址不能为空!",Toast.LENGTH_SHORT).show();
            return;
        }

        // 做个正则验证手机号
        if (!CommonUtil.isMobileNO(phone)) {
            Toast.makeText(this,"请输入正确的手机号码!",Toast.LENGTH_SHORT).show();
            return;
        }



        LogisticDao dao=new LogisticDao(this);

        if(select_cb.isChecked()){
            dao.UpdateDefault("");

        }

        Logistics logistics=new Logistics();
        logistics.setName(name);
        logistics.setTel(phone);
        logistics.setAddress(address);
        logistics.setIsDefault(select_cb.isChecked()?1:0);
        long isSave = dao.replaceOne(logistics);
        if(isSave==-1){
            Toast.makeText(this,"保存失败!",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"保存成功!",Toast.LENGTH_SHORT).show();
            if(!StringUtil.isEmpty(mId)){
                dao.deleteOne(Integer.parseInt(mId));
            }



            mIntent = new Intent();
            setResult(Constant.FROMLOG, mIntent);//告诉原来的Activity 将数据传递给它
            finish();//一定要调用该方法 关闭新的AC 此时 老是AC才能获取到Itent里面的值
        }


    }

    protected void initData() {
        mIntent=getIntent();
        mLogistics= (Logistics) mIntent.getSerializableExtra(Constant.address);

    }



}
