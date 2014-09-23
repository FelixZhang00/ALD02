package com.project.jamffy.mobilesafe2.ui;

import java.io.File;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.R.bool;
import com.project.jamffy.mobilesafe2.engine.DownLoadFileTask;
import com.project.jamffy.mobilesafe2.engine.SmsInfoEngine;
import com.project.jamffy.mobilesafe2.service.AddressService;
import com.project.jamffy.mobilesafe2.service.BackupSmsService;
import com.project.jamffy.mobilesafe2.utils.AssetCopyUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.AlteredCharSequence;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AToolsActivity extends Activity implements OnClickListener {
	private LinearLayout ll_query;
	private ProgressDialog pd;

	private LinearLayout ll_sms_backup;
	private LinearLayout ll_sms_restore;
	private LinearLayout ll_applock;
	private LinearLayout ll_com_search;
	private SmsInfoEngine smsInfoEngine;
	private final static int ERROR = 10;
	private final static int SUCCESS = 11;
	private final static int COPY_COMMON_NUMBER_SUCCESS = 12;
	private final static int COPY_COMMON_NUMBER_FAILED = 13;
	private SharedPreferences sp;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ERROR:
				Toast.makeText(getApplicationContext(), "下载数据库失败", 0).show();
				break;
			case SUCCESS:
				Intent queryIntent = new Intent(AToolsActivity.this,
						QueryNumberActivity.class);
				startActivity(queryIntent);
				Toast.makeText(getApplicationContext(), "下载数据库成功", 0).show();

				break;
			case COPY_COMMON_NUMBER_SUCCESS:
				// 进入公共号码的显示界面
				Intent comSearchIntent = new Intent(AToolsActivity.this,
						CommonNumberActivity.class);
				startActivity(comSearchIntent);
				Toast.makeText(getApplicationContext(), "复制数据库成功", 0).show();
				break;
			case COPY_COMMON_NUMBER_FAILED:
				Toast.makeText(getApplicationContext(), "复制数据库失败", 0).show();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.atools);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		pd = new ProgressDialog(this);
		// 设置进度条显示的风格
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		ll_query = (LinearLayout) this.findViewById(R.id.ll_atools_query);

		ll_sms_backup = (LinearLayout) this
				.findViewById(R.id.ll_atools_sms_backup);
		ll_sms_restore = (LinearLayout) this
				.findViewById(R.id.ll_atools_sms_restore);
		ll_applock = (LinearLayout) this.findViewById(R.id.ll_atools_applock);
		ll_com_search = (LinearLayout) this
				.findViewById(R.id.ll_atools_common_number);
		ll_query.setOnClickListener(this);
		ll_sms_backup.setOnClickListener(this);
		ll_sms_restore.setOnClickListener(this);
		ll_applock.setOnClickListener(this);
		ll_com_search.setOnClickListener(this);

	}

	/**
	 * 启动常用号码查询功能前的准备工作
	 */
	protected void loadCommNumUI() {
		// 判读数据库是否已经拷贝到系统目录（ data/data/包名/files/commonnum.db）
		final File commNumFile = new File(getFilesDir(), "commonnum.db");
		if (commNumFile.exists() && commNumFile.length() > 0) {
			// 进入公共号码的显示界面
			Intent comSearchIntent = new Intent(AToolsActivity.this,
					CommonNumberActivity.class);
			startActivity(comSearchIntent);
		} else {
			// 数据库的拷贝.
			pd.setMessage("正在复制数据库...");
			pd.setCancelable(false);
			pd.show();
			new Thread() {
				@Override
				public void run() {
					super.run();
					// 将数据库拷贝到手机系统中
					AssetCopyUtil acu = new AssetCopyUtil(
							getApplicationContext());
					boolean result = acu.copyFile("commonnum.db", commNumFile,
							pd);
					Message msg = Message.obtain();
					if (result) {
						// 复制成功
						pd.dismiss();
						msg.what = COPY_COMMON_NUMBER_SUCCESS;
						handler.sendMessage(msg);
					} else {
						msg.what = COPY_COMMON_NUMBER_FAILED;
						handler.sendMessage(msg);
					}
				}

			}.start();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_atools_query:
			loadQueryUI();
			break;

		case R.id.ll_atools_sms_backup:
			smsInfoEngine = new SmsInfoEngine(this);
			long counts = smsInfoEngine.getCounts();
			showsmsbackupDialog(counts + "");
			break;

		case R.id.ll_atools_sms_restore:
			smsRestore();
			break;
		case R.id.ll_atools_applock:
			// 判断是否设置了密码
			if (isPWDsetup()) {
				Intent applockIntent = new Intent(AToolsActivity.this,
						AppLockActivity.class);
				startActivity(applockIntent);
			} else {
				Toast.makeText(getApplicationContext(), "请先设置密码", 0).show();
				Intent setPwdIntent = new Intent(AToolsActivity.this,
						LostProtectdActivity.class);
				startActivity(setPwdIntent);
			}
			break;
		case R.id.ll_atools_common_number:
			loadCommNumUI();
			break;
		default:
			break;
		}
	}

	/**
	 * 检查SharedPreferences 里是否有密码的设置
	 * 
	 * @return
	 */
	private boolean isPWDsetup() {
		String pwd = sp.getString("password", null);
		if (pwd == null) {
			return false;
		} else {
			if ("".equals(pwd)) {
				return false;
			} else {
				return true;
			}

		}

	}

	/**
	 * 进入号码归属地查询功能前的准备工作
	 */
	private void loadQueryUI() {
		// 判断来电归属地数据库是否存在
		if (isDBexist()) {
			Intent queryIntent = new Intent(AToolsActivity.this,
					QueryNumberActivity.class);
			startActivity(queryIntent);
		} else {
			// 提示用于下载数据库
			pd.setMessage("正在下载数据库...");
			pd.setCancelable(false);
			pd.show();
			// 下载数据库
			new Thread() {

				@Override
				public void run() {
					String path = getResources()
							.getString(R.string.adressdburl);
					String filepath = "/sdcard/mysafemobileaddress.db";
					try {
						DownLoadFileTask.getFile(path, filepath, pd);
						pd.dismiss();
						Message message = Message.obtain();
						message.what = SUCCESS;
						handler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
						pd.dismiss();
						Message message = Message.obtain();
						message.what = ERROR;
						handler.sendMessage(message);
					}
				}

			}.start();
		}
	}

	/**
	 * 判断来电归属地数据库是否存在
	 * 
	 * @return
	 */
	public boolean isDBexist() {
		File file = new File("/sdcard/mysafemobileaddress.db");// 获得文件句柄
		return file.exists();
	}

	/**
	 * 短信备份
	 */
	private void smsRestore() {
		final ProgressDialog pd = new ProgressDialog(AToolsActivity.this);
		pd.setTitle("正在还原短信...");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCancelable(false); // 为了避免中断还原产生的冗余数据，不让用户终止服务
		pd.show();
		smsInfoEngine = new SmsInfoEngine(this);
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					if (!smsInfoEngine.restoreSms(pd)) {
						Looper.prepare();
						Toast.makeText(getApplicationContext(),
								"还没有备份短信或者不在SD卡中", 0).show();
						Looper.loop();
					} else {
						pd.dismiss();
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "还原成功", 0)
								.show();
						Looper.loop();
					}
				} catch (Exception e) {
					e.printStackTrace();
					pd.dismiss();
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "还原失败", 0).show();
					Looper.loop();

				}
			}

		}.start();
	}

	private void showsmsbackupDialog(String smsCount) {
		AlertDialog.Builder builder = new Builder(AToolsActivity.this);
		builder.setTitle("真的要备份？");
		builder.setMessage("当前短信数为：" + smsCount);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent backupIntent = new Intent(AToolsActivity.this,
						BackupSmsService.class);
				startService(backupIntent);

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();

	}

}
