//package cc.bocang.bocang.view;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.ColorDrawable;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import cc.bocang.bocang.R;
//import cc.bocang.bocang.listener.IShareProductListener;
//import cc.bocang.bocang.ui.BaseActivity;
//import cc.bocang.bocang.utils.ShareUtil;
//
//
///**
// * @author: Jun
// * @date : 2017/2/16 15:12
// * @description :
// */
//public class ShareProductPopWindow extends BasePopwindown implements View.OnClickListener {
//    private IShareProductListener mListener;
//    private RelativeLayout pop_rl;
//    private LinearLayout wechat_ll,wechatmoments_ll,share_qq_ll,save_ll;
//    public int mShareType=1;
//    public String mSharePath="";
//    public String mShareImgPath="";
//    public String mShareTitle="";
//    public String typeShare="";
//    public BaseActivity mActivity;
//    public boolean mIsLocal=false;
//    public Bitmap mBitmap;
//
//
//    public void setListener(IShareProductListener listener) {
//        mListener = listener;
//    }
//
//    public ShareProductPopWindow(Context context) {
//        super(context);
//        initViewData();
//
//    }
//
//    private void initViewData() {
//
//    }
//
//    @Override
//    protected void initView(Context context) {
//        View contentView = View.inflate(mContext, R.layout.pop_product_share, null);
//        pop_rl = (RelativeLayout) contentView.findViewById(R.id.pop_rl);
//        pop_rl.setOnClickListener(this);
//        wechat_ll = (LinearLayout) contentView.findViewById(R.id.wechat_ll);
//        wechat_ll.setOnClickListener(this);
//        wechatmoments_ll = (LinearLayout) contentView.findViewById(R.id.wechatmoments_ll);
//        wechatmoments_ll.setOnClickListener(this);
//        share_qq_ll = (LinearLayout) contentView.findViewById(R.id.share_qq_ll);
//        share_qq_ll.setOnClickListener(this);
//        initUI(contentView);
//    }
//
//    private void initUI(View contentView) {
//
//        mPopupWindow = new PopupWindow(contentView,-1, -1);
//        // 1.让mPopupWindow内部的控件获取焦点
//        mPopupWindow.setFocusable(true);
//        // 2.mPopupWindow内部获取焦点后 外部的所有控件就失去了焦点
//        mPopupWindow.setOutsideTouchable(true);
//        //只有加载背景图还有效果
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
//        // 3.如果不马上显示PopupWindow 一般建议刷新界面
//        mPopupWindow.update();
//        mPopupWindow.setAnimationStyle(R.style.AnimBottom);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.pop_rl:
//                onDismiss();
//            break;
//            case R.id.wechat_ll://分享微信
//                typeShare= Wechat.NAME;
//                getShareData();
//                onDismiss();
//            break;
//            case R.id.wechatmoments_ll://分享朋友圈
//                typeShare= WechatMoments.NAME;
//                getShareData();
//                onDismiss();
//            break;
//            case R.id.share_qq_ll://分享QQ
//                typeShare= QQ.NAME;
//                getShareData();
//                onDismiss();
//            break;
//
//        }
//    }
//
//
//    /**
//     * 分享数据
//     */
//    public void getShareData() {
//        Toast.makeText(this.mContext, "正在分享..", Toast.LENGTH_LONG).show();
//        if(mIsLocal){
//            ShareUtil.showShareType02(mActivity, mShareTitle, mSharePath, mShareImgPath, this.mShareType, this.typeShare, true, mBitmap);
//        }else{
//            ShareUtil.showShareType02(mActivity, mShareTitle, mSharePath, mShareImgPath, this.mShareType, this.typeShare,false,null);
//        }
//
//    }
//}
