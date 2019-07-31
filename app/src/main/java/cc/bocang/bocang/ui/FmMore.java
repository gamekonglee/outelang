package cc.bocang.bocang.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.lib.common.hxp.view.StatedFragment;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.api.OtherApi;
import cc.bocang.bocang.data.model.Result;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.utils.ShareUtil;
import cc.bocang.bocang.utils.UIUtils;

public class FmMore extends StatedFragment implements OnClickListener {

    private RelativeLayout mUpdateMultiple;
    private RelativeLayout bocang_rl,addressRl;
    private UserInfo mInfo;

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = initView(inflater, container);


        return view;
    }

    @Override
    public void onClick(View v) {
        if (mCompanyrofilePRl == v) {
            //公司简介
            if (Constant.Companyrofile != null) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra("title", "公司简介");
                intent.putExtra("fm", FmCompanyProfile.class.getSimpleName());
                getActivity().startActivity(intent);
            }

        } else if (mOfficialWebsiteRl == v) {
            //官网
            if (Constant.OfficialWebsite != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constant.OfficialWebsite));
                startActivity(intent);
            }

        } else if (mOneContactRl == v) {
            //一键联系
            if (Constant.OneContactPhone != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Constant.OneContactPhone));
                startActivity(intent);
            }

        } else if (mUseruideGRl == v) {
            Intent intent = new Intent(getActivity(), HelpActivity.class);
            getActivity().startActivity(intent);

        } else if (mShareRl == v) {
            showShare();
            //分享

        } else if (mDistributorRl == v) {
            //经销商管理
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra("title", "经销商管理");
            intent.putExtra("fm", FmDistributor.class.getSimpleName());
            getActivity().startActivity(intent);

        } else if (mUpdateProductRl == v) {
            //产品上样
            Log.v("520it","产品上样");
            Intent intent = new Intent(getActivity(), UpdateProductActivity.class);
            getActivity().startActivity(intent);



        } else if (mUpdateMultiple == v) {
            //设置倍数
            Intent intent = new Intent(getActivity(), UpdateMultipleActivity.class);
            getActivity().startActivity(intent);

        }
        else if (mSetUpProductRl == v) {

            showInstallDialog();
        }
        else if (mTwoBarCodeRl == v) {
            //二维码
            Intent intent = new Intent(getActivity(), TwoBarCodeActivity.class);
            getActivity().startActivity(intent);

        }
        else if (addressRl == v) {
            //收货地址管理
            Intent intent = new Intent(getActivity(), UserAddressActivity.class);
            getActivity().startActivity(intent);

        }
        else if (bocang_rl == v) {
            //一键联系
            if (Constant.TwoContactPhone != null) {
//                Intent intent = new Intent();
                //                intent.setAction(Intent.ACTION_CALL);
                //                intent.setData(Uri.parse("tel:" + Constant.OneContactPhone));
                //                startActivity(intent);
                setPhone(Constant.TwoContactPhone);
            }

        }
    }

    private void setPhone(final String phoneNumber){
        ActivityCompat.requestPermissions(FmMore.this.getActivity(),
                new String[]{Manifest.permission.CALL_PHONE},
                1);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FmMore.this.getActivity());
        dialogBuilder.withTitle("提示：")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("是否打电话给" + phoneNumber + "?")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor(FmMore.this.getActivity().getResources().getColor(R.color.colorPrimary))
                        // .withIcon(getResources().getDrawable(R.drawable.icon))
                .isCancelableOnTouchOutside(false)
                .isCancelable(false)
                .withEffect(Effectstype.Shake)
                .withButton1Text("取消")
                .withButton2Text("确定")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         PackageManager packageManager = FmMore.this.getActivity().getPackageManager();
                                         int permission = packageManager.checkPermission("android.permission.CALL_PHONE", "cc.bocang.bocang");
                                         if (PackageManager.PERMISSION_GRANTED != permission) {
                                             dialogBuilder.dismiss();
                                             return;
                                         } else {
                                             Intent intent = new Intent();
                                             intent.setAction(Intent.ACTION_CALL);
                                             intent.setData(Uri.parse("tel:" + phoneNumber));
                                             startActivity(intent);
                                             dialogBuilder.dismiss();
                                         }


                                     }
                                 }

                ).

                show();

    }

    private void showInstallDialog() {
        final EditText inputServer = new EditText(getActivity());
//        inputServer.setRawInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
//        inputServer.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        inputServer.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请输入密码查看新品专区!").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String urlPath=Constant.SETUPPROUCT+inputServer.getText().toString();
                        Log.v("520it","urlPath:"+urlPath);
                        String json = OtherApi.doGet(urlPath);
                        Log.v("520it","json:"+json);
                        final Result result= JSON.parseObject(json, Result.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result.getCode().equals("1")){
                                    Intent intent=new Intent(getActivity(),NewProductActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getActivity(), "密码错误，请重新输入!", Toast.LENGTH_LONG).show();
                                }


                            }
                        });
                    }
                }).start();

            }
        });
        builder.show();
    }


//    /**
//     * 分享操作
//     */
//    private void showShare() {
//        ShareSDK.initSDK(getActivity());
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(getString(R.string.app_name)+"配灯系统App");
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(Constant.SHAREAPP);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(getString(R.string.app_name)+"配灯系统App");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(Constant.SHAREAPP);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(getString(R.string.app_name)+"配灯系统App");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(Constant.SHAREAPP);
//
//        oks.setImageUrl(Constant.SHAREIMAGE);
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            @Override
//            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
//                if ("QZone".equals(platform.getName())) {
//                    paramsToShare.setTitle(null);
//                    paramsToShare.setTitleUrl(null);
//                }
//                if ("SinaWeibo".equals(platform.getName())) {
//                    paramsToShare.setUrl(null);
//                    paramsToShare.setText("分享文本 "+Constant.SHAREAPP);
//                }
//                if ("Wechat".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                    paramsToShare.setImageData(imageData);
//                }
//                if ("WechatMoments".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                    paramsToShare.setImageData(imageData);
//                }
//
//            }
//        });
//
//        // 启动分享GUI
//        oks.show(getActivity());
//    }
    /**
     * 分享操作
     */
    private void showShare() {

        final Dialog dialog= UIUtils.showBottomInDialog(getActivity(),R.layout.share_dialog,UIUtils.dip2PX(150));
        TextView tv_cancel= (TextView) dialog.findViewById(R.id.tv_cancel);
        LinearLayout ll_wx= (LinearLayout) dialog.findViewById(R.id.ll_wx);
        LinearLayout ll_pyq= (LinearLayout) dialog.findViewById(R.id.ll_pyq);
        LinearLayout ll_qq= (LinearLayout) dialog.findViewById(R.id.ll_qq);
        final String mGoodsname="来自"+getString(R.string.app_name)+"配灯的分享";
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final String url=Constant.SHAREAPP;
        ll_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareWx(getActivity(), mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.sharePyq(getActivity(), mGoodsname, url);
                dialog.dismiss();
            }
        });
        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareUtil.shareQQ(getActivity(), mGoodsname, url,"");
                dialog.dismiss();
            }
        });
    }

    private RelativeLayout mCompanyrofilePRl,mOfficialWebsiteRl,mOneContactRl,
            mUseruideGRl,mShareRl, mDistributorRl,mUpdateProductRl,mSetUpProductRl,mTwoBarCodeRl;

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fm_more, container, false);

        //公司简介
        mCompanyrofilePRl = (RelativeLayout) v.findViewById(R.id.companyrofilePRl);
        mCompanyrofilePRl.setOnClickListener(this);

        //官网
        mOfficialWebsiteRl = (RelativeLayout) v.findViewById(R.id.officialWebsiteRl);
        mOfficialWebsiteRl.setOnClickListener(this);

        //一键联系
        mOneContactRl = (RelativeLayout) v.findViewById(R.id.oneContactRl);
        mOneContactRl.setOnClickListener(this);
        mOneContactRl.setVisibility(View.GONE);

        //用户指南
        mUseruideGRl = (RelativeLayout) v.findViewById(R.id.useruideGRl);
        mUseruideGRl.setOnClickListener(this);

        //分享
        mShareRl = (RelativeLayout) v.findViewById(R.id.shareRl);
        mShareRl.setOnClickListener(this);

        //设置倍数
        mUpdateMultiple = (RelativeLayout) v.findViewById(R.id.updateMultiple);
        mUpdateMultiple.setOnClickListener(this);

        //经销商管理
        mDistributorRl = (RelativeLayout) v.findViewById(R.id.distributorRl);
        mDistributorRl.setOnClickListener(this);

        //产品上样
        mUpdateProductRl = (RelativeLayout) v.findViewById(R.id.updateProductRl);
        mUpdateProductRl.setOnClickListener(this);

        mInfo = ((MyApplication) getActivity().getApplication()).mUserInfo;

        if(mInfo.getInvite_code().equals("null")){
            mUpdateMultiple.setVisibility(View.GONE);
        }else{
            mUpdateMultiple.setVisibility(View.GONE);
        }

        mSetUpProductRl = (RelativeLayout) v.findViewById(R.id.setup_new_rl);
        mSetUpProductRl.setOnClickListener(this);

        mTwoBarCodeRl = (RelativeLayout) v.findViewById(R.id.two_bar_code_rl);
        mTwoBarCodeRl.setOnClickListener(this);

        bocang_rl = (RelativeLayout) v.findViewById(R.id.bocang_rl);
        bocang_rl.setOnClickListener(this);

        addressRl = (RelativeLayout) v.findViewById(R.id.addressRl);
        addressRl.setOnClickListener(this);


        return v;
    }
}
