package com.project.jamffy.mobilesafe2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

/**
 * 下载的工具类：
 * 
 * @author tmac
 *
 */
public class DownLoadUtils {
	/**
	 * @param path
	 *            服务器文件路径
	 * @param filepath
	 *            本地文件路径
	 * @param pd
	 *            进度条对话框
	 * @return 本地文件对象,下载后的apk
	 * @throws Exception
	 */
	public static File getFile(String path, String filepath, ProgressDialog pd)
			throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		// 获取服务端文件总长度
		int max = conn.getContentLength();
		// 将进度条的最大值设置为要下载文件的总长度
		pd.setMax(max);

		if (conn.getResponseCode() == 200) {
			// 获取要下载的apk文件的输入流
			InputStream is = conn.getInputStream();
			File file = new File(filepath);
			FileOutputStream fos = new FileOutputStream(file);
			// 设置一个缓存区
			byte[] buffer = new byte[1024];
			int len = 0;
			int process = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				// 每读取一次输入流，就刷新一次下载进度
				process += len;
				pd.setProgress(process);
				// 设置睡眠时间，便于观察下载进度
				Thread.sleep(30);
			}
			// 刷新缓存数据到文件中
			fos.flush();
			fos.close();
			is.close();
			return file;

		}
		return null;
	}

	public static String getFilename(String urlpath) {
		return urlpath
				.substring(urlpath.lastIndexOf("/") + 1, urlpath.length());

	}
}
