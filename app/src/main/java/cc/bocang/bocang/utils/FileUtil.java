package cc.bocang.bocang.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import static android.os.Environment.MEDIA_MOUNTED;
/**
 * Created by xpHuang on 2016/9/5.
 */
public class FileUtil {
    /**
     * 获取自定义sd卡上的文件目录
     *
     * @param context
     * @param cacheDir
     * @return 文件夹创建成功返回File or 文件夹创建失败返回null
     */
    public static File getOwnFilesDir(Context context, String cacheDir) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context
                .checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),
                    cacheDir);
        }
        if (appCacheDir == null
                || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context
                    .getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        }
        return appCacheDir;
    }
}
