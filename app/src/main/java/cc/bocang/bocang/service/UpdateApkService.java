package cc.bocang.bocang.service;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import com.lib.common.hxp.global.UserSp;

import cc.bocang.bocang.global.Constant;

/**
 * 检测安装更新文件的助手类
 * 
 * @author 
 * 
 */

public class UpdateApkService extends Service
{

	/** 安卓系统下载类 **/
	DownloadManager manager;

	/** 接收下载完的广播 **/
	DownloadCompleteReceiver receiver;

	/** 初始化下载器 **/
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initDownManager()
	{
		manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

		receiver = new DownloadCompleteReceiver();

		// 设置下载地址
		Request down = new Request(Uri.parse(Constant.APK_URL));

		// 设置允许使用的网络类型，这里是wifi才可以
		down.setAllowedNetworkTypes(Request.NETWORK_WIFI);

		// 下载时，通知栏显示途中
		down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);

		// 显示下载界面
		down.setVisibleInDownloadsUi(true);


		UserSp userSp = new UserSp(getApplicationContext());
		String serverAPKVersion = userSp.getString(userSp.getSP_SERVER_VERSION(), "");

		// 设置下载后文件存放的位置
		down.setDestinationInExternalFilesDir(this,
				Environment.DIRECTORY_DOWNLOADS, Constant.APK_NAME1+serverAPKVersion+ Constant.APK_NAME2);
		// 将下载请求放入队列
		manager.enqueue(down);

		// 注册下载广播
		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// 调用下载
		initDownManager();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{

		// 注销下载广播
		if (receiver != null)
			unregisterReceiver(receiver);

		super.onDestroy();
	}

	// 接受下载完成后的intent
	class DownloadCompleteReceiver extends BroadcastReceiver
	{

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onReceive(Context context, Intent intent)
		{

			// 判断是否下载完成的广播
			if (intent.getAction().equals(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE))
			{

				// 获取下载的文件id
				long downId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);

				// 自动安装apk
				installAPK(manager.getUriForDownloadedFile(downId));

				// 停止服务并关闭广播
				UpdateApkService.this.stopSelf();

			}
		}

		/**
		 * 安装apk文件
		 */
		private void installAPK(Uri apk)
		{
			// 通过Intent安装APK文件
			Intent intents = new Intent();

			intents.setAction("android.intent.action.VIEW");
//			intents.addCategory("android.intent.category.DEFAULT");
//			intents.setMsgType("application/vnd.android.package-archive");
			intents.setData(apk);
			intents.setDataAndType(apk,
					"application/vnd.android.package-archive");
			intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			android.os.Process.killProcess(android.os.Process.myPid());
			// 如果不加上这句的话在apk安装完成之后点击打开会崩溃

			startActivity(intents);
		}

	}
}