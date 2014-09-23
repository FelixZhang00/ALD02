package com.project.jamffy.mobilesafe2.ui;

import java.io.File;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.UpdateInfo;
import com.project.jamffy.mobilesafe2.engine.DownLoadFileTask;
import com.project.jamffy.mobilesafe2.engine.UpdateInfoService;
import com.project.jamffy.mobilesafe2.service.AddressService;
import com.project.jamffy.mobilesafe2.service.LockAppService;
import com.project.jamffy.mobilesafe2.utils.AssetCopyUtil;
import com.project.jamffy.mobilesafe2.utils.DownLoadUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	private static final int GET_INFO_SUCCESS = 10;
	private static final int SERVER_ERROR = 11;
	private static final int SERVER_URL_ERROR = 12;
	private static final int PROTOCOL_ERROR = 13;
	private static final int IO_ERROR = 14;
	private static final int XML_PARSE_ERROR = 15;
	private static final int DOWNLOAD_SUCCESS = 16;
	private static final int DOWNLOAD_ERROR = 17;
	protected static final String TAG = "SplashActivity";
	private long startTime;
	private long endTime;

	private TextView tv_splash_version;
	private LinearLayout ll_splash_main;
	private String versiontext;
	private ProgressDialog pd;

	private SharedPreferences sp;
	private String download_file_name = null;
	private UpdateInfo info;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case XML_PARSE_ERROR:
				Toast.makeText(getApplicationContext(), "xml解析错误", 1).show();
				loadMainUI();
				break;
			case IO_ERROR:
				Toast.makeText(getApplicationContext(), "I/O错误", 1).show();
				loadMainUI();
				break;
			case PROTOCOL_ERROR:
				Toast.makeText(getApplicationContext(), "协议不支持", 1).show();
				loadMainUI();
				break;
			case SERVER_URL_ERROR:
				Toast.makeText(getApplicationContext(), "服务器路径不正确", 1).show();
				loadMainUI();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器内部异常", 1).show();
				loadMainUI();
				break;
			case GET_INFO_SUCCESS:
				// 判断服务器版本号和客户端的版本号是否相同
				if (isNeedUpdate(versiontext)) {
					Logger.i(TAG, "弹出升级对话框");
					showUpdateDialog();
				}
				break;
			case DOWNLOAD_SUCCESS:
				Logger.i(TAG, "文件下载成功");
				File file = (File) msg.obj;
				installApk(file);
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "下载数据异常", 1).show();
				loadMainUI();
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		initService();
		tv_splash_version = (TextView) this
				.findViewById(R.id.tv_splash_version);
		ll_splash_main = (LinearLayout) this.findViewById(R.id.ll_splash_main);
		versiontext = getVersion();

		// 让当前的activity延时一秒钟 检查更新
		new Thread() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!isAutoUpdate()) { // 如果用户取消了自动更新就直接进入主界面
					loadMainUI();
				} else {
					msg.what = GET_INFO_SUCCESS;
					handler.sendMessage(msg);
				}
			}

		}.start();

		// Set up a worker thread to copy a db file.
		new Thread() {

			@Override
			public void run() {
				super.run();
				File file = new File(getFilesDir(), "antivirus.db");
				if (file.exists() && file.length() > 0) {

				} else {
					AssetCopyUtil.copyFile2(getApplicationContext(), "antivirus.db",
							file.getAbsolutePath(), null);
				}
			}

		}.start();

		tv_splash_version.setText(versiontext);
		// 设置动画效果
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		ll_splash_main.startAnimation(aa);

		// 完成窗体的全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	/**
	 * 初始化各种服务
	 */
	private void initService() {
		// 控制来电号码归属地显示
		// 控制程序锁服务
		Intent serviceIntent = new Intent(this, AddressService.class);
		Intent lockserviceInten = new Intent(this, LockAppService.class);
		boolean isaddressopen = sp.getBoolean("isaddressopen", true);
		boolean islockopen = sp.getBoolean("islockopen", false);
		if (isaddressopen) {
			startService(serviceIntent);
		} else {
			stopService(serviceIntent); // 如果服务没有开启，stop也不会报错
		}
		if (islockopen) {
			startService(lockserviceInten);
		} else {
			stopService(lockserviceInten);
		}
	}

	/**
	 * 检测用户是否取消了自动更新
	 * 
	 * @return
	 */
	private boolean isAutoUpdate() {
		SharedPreferences sp = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		boolean autoupdate = sp.getBoolean("autoupdate", true);
		if (autoupdate) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 安装一个apk文件
	 * 
	 * @param file
	 *            要安装的完整文件名
	 */
	protected void installApk(File file) {
		// 隐式意图
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");// 设置意图的动作
		intent.addCategory("android.intent.category.DEFAULT");// 为意图添加额外的数据
		// intent.setType("application/vnd.android.package-archive");
		// intent.setData(Uri.fromFile(file));
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");// 设置意图的数据与类型
		finish(); // 关闭当前activity
		startActivity(intent);// 激活该意图
	}

	/**
	 * 升级对话框
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.icon5);
		builder.setTitle("升级提醒");
		builder.setMessage(info.getDescription());
		// 创建进度条
		pd = new ProgressDialog(SplashActivity.this);
		pd.setMessage("正在下载...");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCancelable(false); // 同样进度条也不能取消
		builder.setCancelable(false); // 让用户不能取消对话框
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Logger.i(TAG, "下载apk文件" + info.getApkurl());
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					pd.show();
					DownLoadFileThread dlft = new DownLoadFileThread(info
							.getApkurl());
					// 开启子线程下载apk
					new Thread(dlft).start();
				} else {
					Toast.makeText(getApplicationContext(), "sd卡不可用", 1).show();
					loadMainUI();
				}
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Logger.i(TAG, "用户取消升级，进入主界面");
				loadMainUI();
			}
		});
		builder.create().show();
	}

	/**
	 * @param versiontext
	 *            当前客户端的版本号信息
	 * @return 是否需要更新
	 */
	private boolean isNeedUpdate(String versiontext) {

		UpdateInfoService service = new UpdateInfoService(this);
		try {
			info = service.getUpdateInfo(R.string.updateurl);
			// info = new UpdateInfo("2.0", "检查是否在获取R.string.updateurl出现异常",
			// "http://192.168.0.100:8080/update.xml");
			String version = info.getVersion();
			// Logger.i(TAG, version);
			if (version.equals(versiontext)) {
				Logger.i(TAG, "版本号相同，无需升级");
				return false;
			} else {
				Logger.i(TAG, "版本号不同,需要升级");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "获取更新信息异常", 0).show();
			Logger.i(TAG, "获取更新信息异常，进入主界面");
			loadMainUI();
			return false;
		}

	}

	/**
	 * 获取当前应用程序的版本号
	 * 
	 * @return
	 */
	private String getVersion() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(getPackageName(),
					0);
			return info.versionName;
		} catch (NameNotFoundException e) {

			e.printStackTrace();
			return "版本号未知";
		}

	}

	private void loadMainUI() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish(); // 把当前activity从任务栈里移除
	}

	private class DownLoadFileThread implements Runnable {
		private String filepath; // 本地文件路径
		private String path; // 服务器文件路径

		public DownLoadFileThread(String path) {
			this.path = path;
		}

		@Override
		public void run() {
			// 获取最新的apk文件
			String filename = DownLoadFileTask.getFilename(path);
			// 在sd卡上创建一个文件
			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			try {
				// 得到下载后的apk文件
				file = DownLoadFileTask.getFile(path, file.getAbsolutePath(),
						pd);
				// 向主线程发送下载成功的消息
				Message msg = Message.obtain();
				msg.what = DOWNLOAD_SUCCESS;
				msg.obj = file;
				handler.sendMessage(msg);
				pd.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
				// 向主线程发送下载失败的消息
				Message msg = Message.obtain();
				msg.what = DOWNLOAD_ERROR;
				handler.sendMessage(msg);
				pd.dismiss();
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
