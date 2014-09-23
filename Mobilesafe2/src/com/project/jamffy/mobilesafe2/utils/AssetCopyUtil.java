package com.project.jamffy.mobilesafe2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.widget.ProgressBar;

/**
 * 资产文件拷贝的工具类
 * 
 * @author tmac
 *
 */
public class AssetCopyUtil {
	private Context context;

	public AssetCopyUtil(Context context) {
		this.context = context;
	}

	/**
	 * 拷贝资产目录下的文件
	 * 
	 * @param srcfilename
	 *            源文件的名称
	 * @param file
	 *            目标文件的对象
	 * @param pd
	 *            进度条对话框
	 * @return 是否拷贝成功
	 */
	public boolean copyFile(String srcfilename, File file, ProgressDialog pd) {
		try {
			// 获取到资产目录的管理器。因为数据库存放在该目录下
			AssetManager asm = context.getAssets();
			// 打开资产目录下的资源文件，获取一个输入流对象
			InputStream is = asm.open(srcfilename);
			// 获取到该文件的字节数
			int max = is.available();
			// 设置进度条显示的最大进度
			if (pd != null) {
				pd.setMax(max);
			}
			// 创建一个输出流文件，用于接收输入流
			FileOutputStream fos = new FileOutputStream(file);
			// 创建一个缓存区
			byte[] buffers = new byte[1024];
			int len = 0;
			// 进度条的最开始的位置应该为0
			int process = 0;
			while ((len = is.read(buffers)) != -1) {
				fos.write(buffers, 0, len);
				// 让进度条不断的动态显示当前的拷贝进度
				process += len;
				if (pd != null) {
					pd.setProgress(process);
				}
			}
			// 刷新缓冲区，关流
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Just fo show off the knowledge.
	 * 
	 * @param filename
	 *            the file'name in asset dir.
	 * @param destfilename
	 *            copy the file above into where.
	 * @param pd
	 *            you can set null.
	 * @return
	 */
	public static File copyFile2(Context context, String filename,
			String destfilename, ProgressDialog pd) {

		try {
			InputStream is = context.getAssets().open(filename);
			int max = is.available();
			if (pd != null) {
				pd.setMax(max);
			}
			File file = new File(destfilename);
			OutputStream os = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			int total = 0;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
				total++;
				if (pd != null) {
					pd.setProgress(total);
				}
			}
			os.flush();
			os.close();
			is.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
