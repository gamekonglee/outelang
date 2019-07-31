package cc.bocang.bocang.utils;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.baiiu.filter.util.CommonUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import cc.bocang.bocang.R;
import cc.bocang.bocang.data.model.AppVersion;
import cc.bocang.bocang.global.Constant;


/**
 * 版本更新工具类
 *
 * @author Clawolf
 * @since 2016-08-10
 */
public class UpAppUtils {

    private static String UP_SAVENAME = Constant.APK_NAME1;
    public static String UP_SAVEPATH = getSDPath() + "/Download/";// 保存apk的文件夹
    // 应用程序Context
    private Context mContext;
    private AppVersion newVersion;
    private String updateMsg = "发现新版本V";// 提示消息
    private String updateURL = Constant.APK_URL;
    private Boolean isTesting = false; //是否手动检测
    private Boolean isCanceled = false; //用户取消下载
    private Boolean isFinished = false; //用户完成下载


    public UpAppUtils(Context context, AppVersion ver) {
        this.mContext = context;
        this.newVersion = ver;
        contrastVersion();
    }

    public UpAppUtils(Context context, AppVersion ver, Boolean isTesting) {
        this.mContext = context;
        this.newVersion = ver;
        this.isTesting = isTesting;
        checkUpdateCompleted();
    }

    private void contrastVersion() {
        if (newVersion == null)
            return;
        String newVersionName = newVersion.getVersion();
        String cusVersionName = CommonUtil.localVersionName(mContext);
        if (!newVersionName.equals(cusVersionName)) {
            String[] str1 = newVersionName.split("\\.");
            String[] str2 = cusVersionName.split("\\.");
            for (int i = 0; i < 2; i++) {
                if (!str1[i].equals(str2[i])) {
                    if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                        checkUpdateCompleted();
                    } else {
                        showLatest();
                        return;
                    }

                }
            }
            updateURL = Constant.APK_URL;
        } else {
            showLatest();
            return;
        }

    }

    private void showUpAppDialog(String newVersionName) {
        //创建builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(updateMsg + newVersionName)             //标题
                .setIcon(R.mipmap.ic_launcher)     //icon
                .setCancelable(false)           //不响应back按钮
                .setMessage(newVersion.getDes())    //对话框显示内容
                .setPositiveButton(R.string.UMUpdateNow, new DialogInterface.OnClickListener() { //设置按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkUpdateCompleted();
                    }
                });
        if (!newVersion.getForcedUpdate().equals("1")) {
            builder.setNegativeButton(R.string.UMNotNow, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                    System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                }
            });
        }
        final AlertDialog dlg = builder.create();
        //创建Dialog对象
        dlg.show();
    }

    /**
     * 下载时的弹窗
     */
    public void checkUpdateCompleted() {
        final ProgressDialog pBar = new ProgressDialog(mContext);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle(R.string.down_waiting);
        pBar.setProgress(0);
        pBar.setMax(100);
        pBar.setCancelable(false);
        pBar.setButton(DialogInterface.BUTTON_POSITIVE, "后台下载", new DialogInterface.OnClickListener() { //设置按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pBar.dismiss();
            }
        });
        pBar.setButton(DialogInterface.BUTTON_NEGATIVE, "取消下载", new DialogInterface.OnClickListener() { //设置按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pBar.dismiss();
                cancelDownload();
//                android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
//                System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
            }
        });
        pBar.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(updateURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File ApkFile = new File(UP_SAVEPATH, String.format(UP_SAVENAME, newVersion.getVersion()));

                    if (ApkFile.exists()) {
                        ApkFile.delete();
                    }

                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {

                        int numRead = is.read(buf);
                        count += numRead;
                        pBar.setProgress((int) (((float) count / length) * 100));
                        if (numRead <= 0) {
                            isFinished = true;
                            pBar.dismiss();
                            installApk();
                        } else {
                            fos.write(buf, 0, numRead);
                        }
                    }
                    while (!isCanceled && !isFinished);// 点击取消或下载完成就停止下载.
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //获取跟目录
    public static String getSDPath() {
        File sdDir = Environment.getExternalStorageDirectory();
        return sdDir.toString();
    }

    //判断sd卡是否存在
    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public void cancelDownload() {
        Toast.makeText(mContext, R.string.download_cancel, Toast.LENGTH_LONG);
        isCanceled = true;
    }

    //安装apk
    public void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(UP_SAVEPATH, String.format(UP_SAVENAME, newVersion.getVersion()))),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    public void showLatest() {
        if (isTesting)
            Toast.makeText(mContext, R.string.UMLatestVersion, Toast.LENGTH_LONG);
    }

//    /**
//     * 下载视频到sd卡
//     */
//    public static void toLoadVideo(String FileUrl, final Activity view) {
//
//        final File filepath = new File(UP_SAVEPATH + File.separator + "otl" + File.separator + "data.zip");//仅创建路径的File对象
//        if (!filepath.exists()) {
//            filepath.mkdir();//如果路径不存在就先创建路径
//        }
//
//        if (filepath.exists()) {
//            filepath.delete();
//        }
//
//        final ProgressDialog pBar = new ProgressDialog(view);    //进度条，在下载的时候实时更新进度，提高用户友好度
//        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pBar.setIcon(R.drawable.ssdk_oks_ptr_ptr); //icon
//        pBar.setTitle(R.string.down_waiting);
//        pBar.setProgress(0);
//        pBar.setMax(100);
//        pBar.setCancelable(false);
//        pBar.setButton(DialogInterface.BUTTON_POSITIVE, "后台下载", new DialogInterface.OnClickListener() { //设置按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                pBar.dismiss();
//            }
//        });
//        pBar.setButton(DialogInterface.BUTTON_NEGATIVE, "取消下载", new DialogInterface.OnClickListener() { //设置按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                pBar.dismiss();
////                cancelDownload();
//                //下载失败，视频路径就要删除，否则下次进来有了路径就不再下载该视频
//                File file = new File(filepath.getPath());
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//        });
//        pBar.show();
//        /**
//         * 下载文件
//         * 第一个参数:文件的下载地址
//         * 第二个参数:文件的保存路径
//         * 第三个参数:是否支持断点续传
//         * 第四个参数:是否自动重命名
//         * 第五个参数:下载文件的响应函数
//         //         */
//        mUtils.download(FileUrl, filepath.getPath(), true, false, new RequestCallBack<File>() {
//                    //下载成功后调用此方法
//                    @Override
//                    public void onSuccess(ResponseInfo<File> responseInfo) {
//                        pBar.dismiss();
//                        MyToast.show(view, "下载成功!");
//
//
//                    }
//
//                    //下载失败后调用此方法
//                    @Override
//                    public void onFailure(HttpException e, String s) {
//                        //下载失败，视频路径就要删除，否则下次进来有了路径就不再下载该视频
//                        File file = new File(filepath.getPath());
//                        if (file.exists()) {
//                            file.delete();
//                        }
//                        pBar.dismiss();
//                        MyToast.show(view, "下载失败!");
//                    }
//
//                    //设置下载的进度
//                    @Override
//                    public void onLoading(long total, long current, boolean isUploading) {
//                        super.onLoading(total, current, isUploading);
//                        //设置进度条的总进度
//                        pBar.setMax((int) total);
//
//                        //在进度条中设置当前的下载进度
//                        pBar.setProgress((int) current);
//
//                    }
//                }
//        );
//
//    }


    public static void unzipSingleFileHereWithFileName(String zipPath, String name) throws IOException{
        File zipFile = new File(zipPath);
        File unzipFile = new File(zipFile.getParent() + "/" + name);
        ZipInputStream zipInStream = null;
        FileOutputStream unzipOutStream = null;
        try {
            zipInStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zipInStream.getNextEntry();
            if (!zipEntry.isDirectory()) {
                unzipOutStream = new FileOutputStream(unzipFile);
                byte[] buf = new byte[4096];
                int len = -1;
                while((len = zipInStream.read(buf)) != -1){
                    unzipOutStream.write(buf, 0, len);
                }
            }
        } finally {
            if(unzipOutStream != null){
                unzipOutStream.close();
            }


            if (zipInStream != null) {
                zipInStream.close();
            }
        }
    }


    private static void Unzip(String zipFile, String targetDir) {
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称

        try {
            BufferedOutputStream dest = null; //缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例


            while ((entry = zis.getNextEntry()) != null) {
                try {
                    Log.i("Unzip: ","="+ entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();


                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }


                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }


    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     * @throws Exception
     */
    public static int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
        //public static void upZipFile() throws Exception{
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d("upZipFile", "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finishssssssssssssssssssss");
        return 0;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param baseDir 指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName){
        String[] dirs=absFileName.split("/");
        File ret=new File(baseDir);
        String substr = null;
        if(dirs.length>1){
            for (int i = 0; i < dirs.length-1;i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret = new File(ret, substr);
            }
            Log.d("upZipFile", "1ret = "+ret);
            if(!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length-1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = "+substr);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ret=new File(ret, substr);
            Log.d("upZipFile", "2ret = "+ret);
            return ret;
        }
        return ret;
    }


}