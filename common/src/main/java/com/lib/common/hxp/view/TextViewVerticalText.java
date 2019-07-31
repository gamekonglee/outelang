package com.lib.common.hxp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class TextViewVerticalText extends TextView {

	public TextViewVerticalText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TextViewVerticalText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		if ("".equals(text) || text == null || text.length() == 0) {
			return;
		}
		int m = text.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < m; i++) {
			CharSequence index = text.toString().subSequence(i, i + 1);
			if (i == 0)
				sb.append(index);
			else
				sb.append("\n" + index);
		}
		super.setText(sb, type);
	}

}
