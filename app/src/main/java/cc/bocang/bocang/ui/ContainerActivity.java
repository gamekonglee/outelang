package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.bocang.bocang.R;

public class ContainerActivity extends BaseActivity implements View.OnClickListener {

    private Button mBackBtn;
    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBackBtn = (Button) findViewById(R.id.topLeftBtn);
        mBackBtn.setOnClickListener(this);
        mTitleTv = (TextView) findViewById(R.id.titleTv);
        String title = getIntent().getStringExtra("title");
        mTitleTv.setText(title);
        String fm = getIntent().getStringExtra("fm");
        Fragment fragment = null;
        if (fm.equals(FmDistributor.class.getSimpleName())) {
            fragment = new FmDistributor();
            fragment.setArguments(new Bundle());
        } else if (fm.equals(FmCollect.class.getSimpleName())) {
            fragment = new FmCollect();
            fragment.setArguments(new Bundle());
        } else if (fm.equals(FmCompanyProfile.class.getSimpleName())) {
            fragment = new FmCompanyProfile();
            fragment.setArguments(new Bundle());
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();

        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            finish();
        }
    }
}
