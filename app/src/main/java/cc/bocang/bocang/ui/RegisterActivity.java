package cc.bocang.bocang.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.squareup.okhttp.ResponseBody;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.HDApiService;
import cc.bocang.bocang.data.api.HDRetrofit;
import cc.bocang.bocang.data.parser.ParseAddUserResp;
import cc.bocang.bocang.data.response.AddUserResp;
import cc.bocang.bocang.utils.SignIdUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private final String TAG = RegisterActivity.class.getSimpleName();
    private RegisterActivity ct = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mRegisterBtn) {
            String distributor = mDistributorEt.getText().toString().trim();
            String address = mAddressEt.getText().toString().trim();
            String phone = mPhoneEt.getText().toString().trim();
            String inviteCode = mInviteCodeEt.getText().toString().trim();
            if (TextUtils.isEmpty(distributor)) {
                Toast.makeText(ct, "经销商名称不能为空", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(address)) {
                Toast.makeText(ct, "经销商地址不能为空", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(phone)) {
                Toast.makeText(ct, "手机号码不能为空", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(inviteCode)) {
                Toast.makeText(ct, "邀请码不能为空", Toast.LENGTH_LONG).show();
            } else {
                register(distributor, address, phone, inviteCode);
            }
        }
    }

    private void register(String distributor, String address, String phone, String inviteCode) {
        HDApiService apiService = HDRetrofit.create(HDApiService.class);
        pd.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = apiService.addUser(1, distributor, address, phone, SignIdUtil.getSignId(ct), inviteCode);
        call.enqueue(new Callback<ResponseBody>() {//开启异步网络请求
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                try {
                    String json = response.body().string();
                    Log.i(TAG, json);
                    AddUserResp resp = ParseAddUserResp.parse(json);
                    if (null != resp && resp.isSuccess()) {
                        showDialog();
                    } else if (null != resp && !resp.isSuccess()) {
                        if (resp.getErrorCode() == 2)
                            Toast.makeText(ct, "邀请码错误", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(ct, "注册失败：" + resp.getErrorCode(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ct, "数据异常...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (null == ct || ct.isFinishing())
                    return;
                pd.setVisibility(View.GONE);
                Toast.makeText(ct, "无法连接服务器...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog() {
        final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(ct);
        dialog.withTitle("提示：")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("\n注册成功，请通知管理员审核\n")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor(getResources().getColor(R.color.colorPrimary))
                .isCancelableOnTouchOutside(false)
                .isCancelable(false)
                .withDuration(700)
                .withEffect(Effectstype.Fadein)
                .withButton1Text("返回").
                setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ct.finish();
                    }
                })
                .show();
    }

    private ProgressBar pd;
    private Button mRegisterBtn;
    private EditText mDistributorEt, mAddressEt, mPhoneEt, mInviteCodeEt;

    private void initView() {
        pd = (ProgressBar) findViewById(R.id.pd);
        mDistributorEt = (EditText) findViewById(R.id.distributorEt);
        mAddressEt = (EditText) findViewById(R.id.addressEv);
        mPhoneEt = (EditText) findViewById(R.id.phoneEv);
        mInviteCodeEt = (EditText) findViewById(R.id.inviteCodeEv);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mRegisterBtn.setOnClickListener(this);
    }
}
