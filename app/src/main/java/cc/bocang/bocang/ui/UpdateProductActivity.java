package cc.bocang.bocang.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.model.Result;
import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.global.Constant;
import cc.bocang.bocang.global.MyApplication;
import cc.bocang.bocang.utils.FileUtil;
import cc.bocang.bocang.utils.ImageUtil;
import cc.bocang.bocang.utils.LoadingDailog;

/**
 * @author Jun
 * @time 2016/10/24  14:47
 * @desc ${TODD}
 */
public class UpdateProductActivity extends BaseActivity implements View.OnClickListener {
    private Button mSubmitBt,mTopLeftBtn;
    private ImageView mImageIv;
    private EditText mProductTypeEt,mRemarkEt;
    private final int PHOTO_WITH_DATA = 1; // 从SD卡中得到图片
    private final int PHOTO_WITH_CAMERA = 2;// 拍摄照片
    private String photoName;
    private File cameraPath;
    private  int TIME_OUT = 10*1000;   //超时时间
    private  String CHARSET = "utf-8"; //设置编码
    private UserInfo mInfo;
    private String imageURL="";
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private LoadingDailog mLodingDailog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateproduct);
        mInfo = ((MyApplication)getApplication()).mUserInfo;
        initView();
        initImageLoader();
        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));

    }

    /**
     * 初始化控件
     */
    private void initView() {
        mSubmitBt = (Button)findViewById(R.id.submitBt);
        mImageIv = (ImageView)findViewById(R.id.imageIv);
        mProductTypeEt = (EditText)findViewById(R.id.productTypeEt);
        mRemarkEt = (EditText)findViewById(R.id.remarkEt);
        mTopLeftBtn = (Button)findViewById(R.id.topLeftBtn);
        mImageIv.setOnClickListener(this);
        mSubmitBt.setOnClickListener(this);
        mTopLeftBtn.setOnClickListener(this);
        mLodingDailog=new LoadingDailog(this, R.style.CustomDialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitBt://提交
                submitImage();
                break;
            case R.id.imageIv://选择
                openImage();
                break;
            case R.id.topLeftBtn:
                finish();
                break;
        }
    }

    /**
     * 选择图片
     */
    private void openImage() {
        final String [] items={"拍照","从相册选择"};
        final boolean[] checkedItem={false,false,false,false};
        new AlertDialog.Builder(UpdateProductActivity.this)
                .setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which){
                            case  0:
                                takePhoto();
                                break;
                            case  1:
                                pickPhoto();
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 提交按钮
     */
    private void submitImage() {
        String fileName=mProductTypeEt.getText().toString();
        String remark= mRemarkEt.getText().toString();
        if(TextUtils.isEmpty(fileName)){
            tip("产品型号不能为空!");
            return;
        }

        if(TextUtils.isEmpty(imageURL)){
            tip("请选择图片!");
            return;
        }

        //TODO
        final String url =Constant.UPLOADSAMPLE;//地址
        final Map<String, String> params = new HashMap<String, String>();

        params.put("user_id",mInfo.getId() + "");
        params.put("name",fileName);
        params.put("content",remark);
        mLodingDailog.show();
        getUpdateImage(url, params, fileName,imageURL);
    }

    /**
     * 拍照获取相片
     **/
    private void takePhoto() {
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        photoName = format.format(date);
        cameraPath = FileUtil.getOwnFilesDir(this, Constant.CAMERA_PATH);

        Uri imageUri = Uri.fromFile(new File(cameraPath, photoName + ".jpg"));
        System.out.println("imageUri" + imageUri.toString());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机
        // 指定照片保存路径（SD卡）
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, PHOTO_WITH_CAMERA); // 用户点击了从相机获取
    }

    /**
     * 从相册获取图片
     **/
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*"); // 开启Pictures画面Type设定为image
        intent.setAction(Intent.ACTION_GET_CONTENT); // 使用Intent.ACTION_PICK这个Action则是直接打开系统图库
        startActivityForResult(intent, PHOTO_WITH_DATA); // 取得相片后返回到本画面
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { // 返回成功
            switch (requestCode) {
                case PHOTO_WITH_CAMERA: {// 拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡

                        File imageFile = new File(cameraPath, photoName + ".jpg");

                        if (imageFile.exists()) {
                            imageURL="file://" + imageFile.toString();
                            //TODO
                            Log.v("520it",imageURL);
                            displaySceneBg(imageURL);
                        } else {
                            Toast.makeText(this, "读取图片失败！", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(this, "没有SD卡", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case PHOTO_WITH_DATA: // 从图库中选择图片
                    // 照片的原始资源地址
                    //TODO
                    imageURL=data.getData().toString();
                    Log.v("520it",imageURL);
                    displaySceneBg(imageURL);
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传图片
     * @param url
     * @param params
     * @param fileName
     * @param imagePath
     */
    private void getUpdateImage(final String url, final Map<String, String> params, String fileName, final String imagePath) {

        final String imageName= fileName+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+".png";
        new  Thread(new Runnable() {
            @Override
            public void run() {
                final String resultJson=uploadFile(mImageIv.getDrawable(),url,params,imageName);
                Log.v("520it","resultJson"+resultJson);
                final Result result= JSON.parseObject(resultJson,Result.class);
                //分享的操作
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(TextUtils.isEmpty(result.getResult())||result.getResult()=="0"){
                            return;
                        }
                        mLodingDailog.dismiss();
                        tip("上传成功!");
                        mImageIv.setImageResource(R.mipmap.jia);
                        mRemarkEt.setText("");
                        mProductTypeEt.setText("");
                    }
                });
            }
        }).start();
    }

    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @param RequestURL  请求的rul
     * @return  返回响应的内容
     */
    private String uploadFile(Drawable file, String RequestURL, Map<String, String> param, String imageName){
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型
        // 显示进度框
        //      showProgressDialog();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file!=null){
                Log.v("520it","触发到");
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();

                String params = "";
                if (param != null && param.size() > 0) {
                    Iterator<String> it = param.keySet().iterator();
                    while (it.hasNext()) {
                        sb = null;
                        sb = new StringBuffer();
                        String key = it.next();
                        String value = param.get(key);
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                        sb.append(value).append(LINE_END);
                        params = sb.toString();
                        dos.write(params.getBytes());
                    }
                }
                sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"").append("file").append("\"")
                        .append(";filename=\"").append(imageName).append("\"\n");
                sb.append("Content-Type: image/png");
                sb.append(LINE_END).append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = ImageUtil.Drawable2InputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1){
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */

                int res = conn.getResponseCode();
                System.out.println("res========="+res);
                if(res==200){
                    InputStream input =  conn.getInputStream();
                    StringBuffer sb1= new StringBuffer();
                    int ss ;
                    while((ss=input.read())!=-1){
                        sb1.append((char)ss);
                    }
                    result = sb1.toString();
                }
                else{
                }
            }else{
                Log.v("520it","触发不到");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    private void displaySceneBg(String path) {
        imageLoader.displayImage(path, mImageIv, options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });
    }

    private void initImageLoader() {
        options = new DisplayImageOptions.Builder()
                // 设置图片下载期间显示的图片
                .showImageOnLoading(R.mipmap.bg_default)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.mipmap.bg_default)
                // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnFail(R.mipmap.bg_default)
                // 设置下载的图片是否缓存在内存中
                .cacheInMemory(false)
                //设置图片的质量
                .bitmapConfig(Bitmap.Config.RGB_565)
                // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                //                .considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片可以放大（要填满ImageView必须配置memoryCacheExtraOptions大于Imageview）
                // 图片加载好后渐入的动画时间
                // .displayer(new FadeInBitmapDisplayer(100))
                .build(); // 构建完成

        // 得到ImageLoader的实例(使用的单例模式)
        imageLoader = ImageLoader.getInstance();
    }



}
