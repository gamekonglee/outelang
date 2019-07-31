package cc.bocang.bocang.global;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import cc.bocang.bocang.data.model.UserInfo;
import cc.bocang.bocang.ui.MainActivity;

public class MyApplication extends Application {
	public static int mLightIndex;
    private final String TAG = MyApplication.class.getSimpleName();
	private static MyApplication instance;
	public UserInfo mUserInfo;


	@Override
	public void onCreate() {
		Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHander());
		Log.i(TAG, "==============================Application onCreate==========================");
		super.onCreate();
		instance = this;

		//初始化网络图片缓存库
		initImageLoader();
		initNoHttp();
	}

	//初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

	public static MyApplication getInstance() {
		return instance;
	}

	/**
	 * 配置NoHttp
	 */
	private void initNoHttp() {
		NoHttp.initialize(this);
		NoHttp.initialize(this, new NoHttp.Config()
				// 设置全局连接超时时间，单位毫秒
				.setConnectTimeout(30 * 1000)
						// 设置全局服务器响应超时时间，单位毫秒
				.setReadTimeout(30 * 1000)
				.setCacheStore(new DBCacheStore(this))//配置缓存，控制开关
				.setCookieStore(new DBCookieStore(this))//配置Cookie保存的位置，默认保存在数据库
				.setNetworkExecutor(new OkHttpNetworkExecutor())//配置网络层
		);
//		Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
//		Logger.setTag("NoHttpSample");// 设置NoHttp打印Log的tag。
	}

	private class MyExceptionHander implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// Logger.i("MobileSafeApplication", "发生了异常,但是被哥捕获了..");
			//            LogUtils.d("MobileSafeApplication","发生了异常,但是被哥捕获了..");
			//并不能把异常消灭掉,只是在应用程序关掉前,来一个留遗嘱的事件
			//获取手机硬件信息
			try {
				Field[] fields = Build.class.getDeclaredFields();
				StringBuffer sb = new StringBuffer();
				for(Field field:fields){
					String value = field.get(null).toString();
					String name  = field.getName();
					sb.append(name);
					sb.append(":");
					sb.append(value);
					sb.append("\n");
				}
				File file=new File(getFilesDir(),"error.log");
				FileOutputStream out = new FileOutputStream(file);
				StringWriter wr = new StringWriter();
				PrintWriter err = new PrintWriter(wr);
				//获取错误信息
				ex.printStackTrace(err);
				String errorlog = wr.toString();
				sb.append(errorlog);
				out.write(sb.toString().getBytes());
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//杀死页面进程
			//            android.os.Process.killProcess(android.os.Process.myPid());
			restartApp();
		}
	}

	public void restartApp(){
		Intent intent = new Intent(getApplicationContext(),MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
	}

}
