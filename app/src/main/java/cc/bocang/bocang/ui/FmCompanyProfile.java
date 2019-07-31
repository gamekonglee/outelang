package cc.bocang.bocang.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lib.common.hxp.view.StatedFragment;

import cc.bocang.bocang.R;
import cc.bocang.bocang.global.Constant;

/**
 * Created by bocang02 on 16/10/11.
 */

public class FmCompanyProfile extends StatedFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = initView(inflater, container);

        return view;
    }



    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_company_profile, container, false);

        TextView tv = (TextView) v.findViewById(R.id.companyrofileTextView);
        tv.setText(Constant.Companyrofile);

        return v;
    }
}
