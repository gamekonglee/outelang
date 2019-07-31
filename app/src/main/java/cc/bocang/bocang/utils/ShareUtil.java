package cc.bocang.bocang.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.util.HashMap;

import cc.bocang.bocang.R;
import cc.bocang.bocang.listener.IShareCallBack;
import cc.bocang.bocang.ui.BaseActivity;

/**
 * @author: Jun
 * @date : 2017/3/15 15:54
 * @description :
 */
public class ShareUtil {
//    /**
//     * 分享操作
//     */
//    public static void showShare(final Activity activity, String title, final String path, final String imagePath) {
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        ShareSDK.initSDK(activity);
//        HashMap<String, Object> wechat = new HashMap<String, Object>();
//        wechat.put("Id", "2");
//        wechat.put("SortId", "2");
//        wechat.put("AppId", "wx0861c0e57e017933");
//        wechat.put("AppSecret", "9cd9f8f883a1a50be696535b44dbec80");
//        wechat.put("BypassApproval", "false");
//        wechat.put("Enable", "true");
//        ShareSDK.setPlatformDevInfo(Wechat.NAME, wechat);
//        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, wechat);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(title);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(path);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(title);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(path);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(title);
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(UIUtils.getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(path);
//        //图片地址
//        //        mImgUrl= Constant.PRODUCT_URL+mImgUrl+ "!400X400.png";
//        //        Log.v("520it","'分享:"+mImgUrl);
//        //        Log.v("520it","产品地址:"+Constant.SHAREPLAN+"id="+id));
//        oks.setImageUrl(imagePath);
//
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            @Override
//            public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
//                if ("QZone".equals(platform.getName())) {
//                    paramsToShare.setTitle(null);
//                    paramsToShare.setTitleUrl(null);
//                }
//                if ("SinaWeibo".equals(platform.getName())) {
//                    paramsToShare.setUrl(null);
//                    paramsToShare.setText("分享文本 " + path);
//                }
//                if ("Wechat".equals(platform.getName())) {
//                    ImageView img = new ImageView(activity);
//                    ImageLoader.getInstance().displayImage(imagePath, img);
//                    paramsToShare.setImageData(img.getDrawingCache());
//                }
//                if ("WechatMoments".equals(platform.getName())) {
//                    ImageView img = new ImageView(activity);
//                    ImageLoader.getInstance().displayImage(imagePath, img);
//                    paramsToShare.setImageData(img.getDrawingCache());
//                }
//
//            }
//        });
//        // 启动分享GUI
//        oks.show(activity);
//    }
//
//    /**
//     * 分享操作
//     */
//    public static void showShareType(final BaseActivity activity, String title, final String path, final String imagePath, final IShareCallBack shareCallBack) {
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        ShareSDK.initSDK(activity);
//
//        Platform.ShareParams sp = new Platform.ShareParams();
//        sp.setTitle("测试分享的标题");
//        sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
//        sp.setText("测试分享的文本");
//        sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");
//        sp.setSite("发布分享的网站名称");
//        sp.setSiteUrl("发布分享网站的地址");
//
//        Platform qzone = ShareSDK.getPlatform(QQ.NAME);
//
//        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
//        qzone.setPlatformActionListener(new PlatformActionListener() {
//            public void onError(Platform arg0, int arg1, Throwable arg2) {
//                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
//                //                activity.hideLoading();
//                shareCallBack.onShareCallBackListener(false);
//                Toast.makeText(activity,"分享失败!",Toast.LENGTH_LONG).show();
//            }
//
//            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//                //分享成功的回调
//                shareCallBack.onShareCallBackListener(true);
//            }
//
//            public void onCancel(Platform arg0, int arg1) {
//                //取消分享的回调
//                shareCallBack.onShareCallBackListener(false);
//            }
//        });
//        // 执行图文分享
//        qzone.share(sp);
//    }
//
//    /**
//     * 分享操作
//     */
//    public static void showShareType02(final BaseActivity activity, String title, final String path, final String imagePath, int imageOrLink, String typeShare,boolean isLocal,Bitmap bitmap) {
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        ShareSDK.initSDK(activity);
//        HashMap<String, Object> wechat = new HashMap<String, Object>();
//        wechat.put("AppId", "wx0861c0e57e017933");
//        wechat.put("AppSecret", "9cd9f8f883a1a50be696535b44dbec80");
//        wechat.put("BypassApproval", "false");
//        wechat.put("Enable", "true");
//        ShareSDK.setPlatformDevInfo(Wechat.NAME, wechat);
//        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, wechat);
//        ShareSDK.setPlatformDevInfo(WechatFavorite.NAME, wechat);
//
//        Platform.ShareParams sp = new Platform.ShareParams();
//        if(isLocal){
//            if (imageOrLink == 0) {
//                sp.setTitle(title);
//                sp.setTitleUrl(path); // 标题的超链接
//                sp.setText(title);
//                sp.setImagePath(imagePath);
//            } else {
//                sp.setImagePath(imagePath);
//            }
//        }else{
//            if (imageOrLink == 0) {
//                sp.setTitle(title);
//                sp.setTitleUrl(path); // 标题的超链接
//                sp.setText(title);
//                sp.setImageUrl(imagePath);
//            } else {
//                sp.setImageUrl(imagePath);
//            }
//        }
//
//
//        if (typeShare.equals(Wechat.NAME) || typeShare.equals(WechatMoments.NAME)) {
//            if(imageOrLink==0){
//                sp.setShareType(Platform.SHARE_TEXT);
//            }
//            sp.setShareType(Platform.SHARE_IMAGE);
//        }
//
//        Platform platform = ShareSDK.getPlatform(typeShare);
//
//        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
//        platform.setPlatformActionListener(new PlatformActionListener() {
//            public void onError(Platform arg0, int arg1, Throwable arg2) {
//                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
//            }
//
//            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//                //分享成功的回调
//            }
//
//            public void onCancel(Platform arg0, int arg1) {
//                //取消分享的回调
//            }
//        });
//        // 执行图文分享
//        platform.share(sp);
//    }
//
//    /**
//     * 分享操作
//     */
//    public static void showShareType03(final BaseActivity activity, String title, final String path, final String imagePath, int imageOrLink, String typeShare,boolean isFile,String filePath) {
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        ShareSDK.initSDK(activity);
//        HashMap<String, Object> wechat = new HashMap<String, Object>();
//        wechat.put("Id", "2");
//        wechat.put("SortId", "2");
//        wechat.put("AppId", "wx0d5873a8c4a78126");
//        wechat.put("AppSecret", "9e375d469e4c2390e3a172ea6aaff3e0");
//        wechat.put("BypassApproval", "false");
//        wechat.put("Enable", "true");
//        ShareSDK.setPlatformDevInfo(Wechat.NAME, wechat);
//        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, wechat);
//        ShareSDK.setPlatformDevInfo(WechatFavorite.NAME, wechat);
//
//        Platform.ShareParams sp = new Platform.ShareParams();
//        if(isFile){
//            sp.setTitle(title);
//            sp.setImageUrl(imagePath);
//            sp.setText(title);
//            sp.setFilePath(filePath);
//            sp.setShareType(Platform.SHARE_FILE);
//
//        } else{
//            if (imageOrLink == 0) {
//                sp.setTitle(title);
//                sp.setTitleUrl(path); // 标题的超链接
//                sp.setText(title);
//                sp.setImagePath(imagePath);
//            } else {
//                sp.setImagePath(imagePath);
//            }
//            sp.setShareType(Platform.SHARE_IMAGE);
//        }
//
//
//
//        Platform platform = ShareSDK.getPlatform(typeShare);
//
//        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
//        platform.setPlatformActionListener(new PlatformActionListener() {
//            public void onError(Platform arg0, int arg1, Throwable arg2) {
//                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
//            }
//
//            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//                //分享成功的回调
//            }
//
//            public void onCancel(Platform arg0, int arg1) {
//                //取消分享的回调
//            }
//        });
//        // 执行图文分享
//        platform.share(sp);
//    }
//
//    /**
//     * 分享操作
//     */
//    public static void showShare01(final Activity activity, String title, final String path, final String imagePath) {
//        if (TextUtils.isEmpty(path)) {
//            return;
//        }
//        ShareSDK.initSDK(activity);
//        HashMap<String, Object> wechat = new HashMap<String, Object>();
//        wechat.put("Id", "2");
//        wechat.put("SortId", "2");
//        wechat.put("AppId", "wx0d5873a8c4a78126");
//        wechat.put("AppSecret", "9e375d469e4c2390e3a172ea6aaff3e0");
//        wechat.put("BypassApproval", "false");
//        wechat.put("Enable", "true");
//        ShareSDK.setPlatformDevInfo(Wechat.NAME, wechat);
//        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, wechat);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(title);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(path);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(title);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(path);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(title);
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(UIUtils.getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(path);
//        //图片地址
//        //        mImgUrl= Constant.PRODUCT_URL+mImgUrl+ "!400X400.png";
//        //        Log.v("520it","'分享:"+mImgUrl);
//        //        Log.v("520it","产品地址:"+Constant.SHAREPLAN+"id="+id));
//        oks.setImageUrl(imagePath);
//
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            @Override
//            public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
//                //                if ("QZone".equals(platform.getName())) {
//                //                    paramsToShare.setTitle(null);
//                //                    paramsToShare.setTitleUrl(null);
//                //                }
//                //                if ("SinaWeibo".equals(platform.getName())) {
//                //                    paramsToShare.setUrl(null);
//                //                    paramsToShare.setText("分享文本 " + path);
//                //                }
//                if ("Wechat".equals(platform.getName())) {
//
//                    paramsToShare.setText(null);
//                    paramsToShare.setTitle(null);
//                    paramsToShare.setTitleUrl("");
//                    paramsToShare.setImageUrl(imagePath);
//                    //                    ImageView img = new ImageView(activity);
//                    //                    ImageLoader.getInstance().displayImage(imagePath, img);
//                    //                    paramsToShare.setImageData(img.getDrawingCache());
//                }
//                if ("WechatMoments".equals(platform.getName())) {
//                    ImageView img = new ImageView(activity);
//                    ImageLoader.getInstance().displayImage(imagePath, img);
//                    paramsToShare.setImageData(img.getDrawingCache());
//                }
//                if (platform.getName().equalsIgnoreCase(QQ.NAME)) {
//                    paramsToShare.setText(null);
//                    paramsToShare.setTitle(null);
//                    paramsToShare.setTitleUrl("");
//                    paramsToShare.setImageUrl(imagePath);
//                }
//
//                platform.setPlatformActionListener(new PlatformActionListener() {
//                    @Override
//                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                        activity.finish();
//                    }
//
//                    @Override
//                    public void onError(Platform platform, int i, Throwable throwable) {
//                        activity.finish();
//                    }
//
//                    @Override
//                    public void onCancel(Platform platform, int i) {
//
//                    }
//                });
//
//            }
//
//
//        });
//        // 启动分享GUI
//        oks.show(activity);
//    }
    /**
     * 分享操作
     */
    public  static void shareWx(final Activity activity, String title, final String path){
        IWXAPI api= WXAPIFactory.createWXAPI(activity,"wx0d5873a8c4a78126",true);
        WXWebpageObject wxWebpageObject=new WXWebpageObject();
        wxWebpageObject.webpageUrl=path;
        WXMediaMessage wxMediaMessage=new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.title=title;
        Bitmap thumb= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.ic_launcher);
        wxMediaMessage.thumbData=ImageUtil.bitmap2Bytes(ImageUtil.createThumbBitmap(thumb,50,50),32);
//        wxMediaMessage.thumbData=ImageUtil.getBitmapByte(thumb);
        SendMessageToWX.Req req=new SendMessageToWX.Req();
        req.transaction="urlpage";
        req.message=wxMediaMessage;
        req.scene=SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }
    public static void sharePyq(FragmentActivity activity, String title, String path) {
        IWXAPI api=WXAPIFactory.createWXAPI(activity,"wx0d5873a8c4a78126",true);
        WXWebpageObject wxWebpageObject=new WXWebpageObject();
        wxWebpageObject.webpageUrl=path;
        WXMediaMessage wxMediaMessage=new WXMediaMessage(wxWebpageObject);
        wxMediaMessage.title=title;
        Bitmap thumb= BitmapFactory.decodeResource(activity.getResources(),R.mipmap.ic_launcher);
        wxMediaMessage.thumbData=ImageUtil.bitmap2Bytes(ImageUtil.createThumbBitmap(thumb,50,50),32);
//        wxMediaMessage.thumbData=ImageUtil.getBitmapByte(thumb);
        SendMessageToWX.Req req=new SendMessageToWX.Req();
        req.transaction="urlpage";
        req.message=wxMediaMessage;
        req.scene=SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }
    public static void shareQQ(FragmentActivity activity, String title, String apkUrl, String shareimage) {
        Tencent mTencent=Tencent.createInstance("1105698263",activity);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareimage);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, apkUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  UIUtils.getString(R.string.app_name));
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        mTencent.shareToQQ(activity, params, null);
    }
}
