package cc.bocang.bocang.utils;

import android.content.Context;

public class ResUtil {
	/**
	 * 通过资源类型和名称获取资源
	 * 
	 * @param ct
	 *            上下文
	 * @param resourceType
	 *            资源类型（drawable、layout...）
	 * @param fileName
	 *            资源名称
	 * @return int 资源在R文件里的id
	 */
	public static int getResourceByName(Context ct, String resourceType,
			String fileName) {
		return ct.getResources().getIdentifier(fileName, resourceType,
				ct.getPackageName());
	}

	/**
	 * 通过资源名称获取id资源
	 * @param ct 
	 * @param fileName 控件id
	 * @return int
	 */
	public static int getResId(Context ct, String fileName) {
		return ct.getResources().getIdentifier(fileName, "id",
				ct.getPackageName());
	}
	/**
	 * 通过资源名称获取layout资源
	 * @param ct
	 * @param fileName 布局文件名称
	 * @return int
	 */
	public static int getResLayout(Context ct, String fileName) {
		return ct.getResources().getIdentifier(fileName, "layout",
				ct.getPackageName());
	}
	/**
	 * 通过资源名称获取drawable资源
	 * @param ct
	 * @param fileName drawable图片文件名称
	 * @return int
	 */
	public static int getResDrawable(Context ct, String fileName) {
		return ct.getResources().getIdentifier(fileName, "drawable",
				ct.getPackageName());
	}
	/**
	 * 通过资源名称获取mipmap资源
	 * @param ct
	 * @param fileName mipmap图片文件名称
	 * @return int
	 */
	public static int getResMipmap(Context ct, String fileName) {
		return ct.getResources().getIdentifier(fileName, "mipmap",
				ct.getPackageName());
	}
}
