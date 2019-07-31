package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.bocang.bocang.R;

/**
 * 公司简介
 *
 * Created by bocang02 on 16/10/11.
 */

public class CompanyProfileActivity extends AppCompatActivity implements View.OnClickListener {
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
        Fragment fragment = new FmCompanyProfile();
        fragment.setArguments(new Bundle());
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            finish();
        }
    }
}
