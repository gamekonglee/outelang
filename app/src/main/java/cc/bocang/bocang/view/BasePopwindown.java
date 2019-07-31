package cc.bocang.bocang.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * @author Jun
 * @time 2016/9/9  20:23
 * @desc  ${TODD}
 */
public abstract class BasePopwindown {
    public Context mContext;
    protected PopupWindow mPopupWindow;
    public BasePopwindown(Context context){
        this.mContext=context;
        initView(context);
    }

    /**
     * 显示悬浮窗口
     * @param anchorView
     */
    public void onShow(View anchorView){
//        mPopupWindow.showAsDropDown(anchorView);
        mPopupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, 0, 0);
    }



    /**
     * 关闭悬浮窗口
     */
    public void onDismiss(){
        mPopupWindow.dismiss();
    }


    protected abstract void initView(Context context);


    public void showPop(View contentView){
        mPopupWindow = new PopupWindow(contentView, -1, -1);
        // 1.让mPopupWindow内部的控件获取焦点
        mPopupWindow.setFocusable(true);
        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
        mPopupWindow.setOutsideTouchable(true);
        //只有加载背景图还有效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 3.如果不马上显示PopupWindow 一般建议刷新界面
        mPopupWindow.update();
    }
}
