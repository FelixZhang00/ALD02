package com.project.jamffy.mobilesafe2.ui;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.R.id;
import com.project.jamffy.mobilesafe2.db.dao.AvirusDao;
import com.project.jamffy.mobilesafe2.utils.MD5Encoder;
import com.project.jamffy.mobilesafe2.utils.TextFormatUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AntiVirusActivity extends Activity {

	protected static final String TAG = "AntiVirusActivity";
	private ProgressBar mPb;
	private LinearLayout mLL;
	private Button mBtn_clean;
	private Button mBtn_kill;
	private ScrollView mSlv;
	private TextView mTv_status;
	private TextView mTv_result;
	private ImageView mIv_scan;

	private PackageManager mPm;
	private AvirusDao dao;
	private MyAsyncTask mAcTask;
	// Store the packname if its pack infect virus.
	private List<String> mVirusPacknames;

	// To insure onClick not clash, I set up two flag.Before execute onClick
	// method , you should check up these flags.
	private boolean flag_sk = true; // false: cleaning now.
	private boolean flag_cv = false; // true: complete the searchKill.
	private boolean flag_already_onclick = false;
	private boolean flag_doInback; // ctrol whether the method doInbackground()
									// can go.

	private RotateAnimation ra;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anti_virus);
		mPb = (ProgressBar) this.findViewById(R.id.pb_antivirus);
		mTv_status = (TextView) this.findViewById(R.id.tv_antivirus_status);
		mTv_result = (TextView) this.findViewById(R.id.tv_antivirus_result);
		mIv_scan = (ImageView) this.findViewById(R.id.iv_antivirus_scan);
		mLL = (LinearLayout) this.findViewById(R.id.ll_antivirus);
		mBtn_clean = (Button) this.findViewById(R.id.btn_antivirus_clean);
		mBtn_kill = (Button) this.findViewById(R.id.btn_antivirus_kill);
		mSlv = (ScrollView) this.findViewById(R.id.slv_antivirus);

		mPm = getPackageManager();
		mVirusPacknames = new ArrayList<String>();
		flag_doInback = true;
		// mAcTask = new MyAsyncTask();

		ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		ra.setDuration(1000);

		ra.setRepeatCount(Animation.INFINITE);
		ra.setRepeatMode(Animation.RESTART);

	}

	/**
	 * 
	 * init some flags just like the activity begins in case of accident.
	 */
	private void initFlags() {
		flag_cv = false;
		flag_already_onclick = false;
		flag_sk = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dao != null) {
			dao.closeDb();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		/*
		 * if the activity isn't seen by user, just put off the searchKill
		 * method.
		 */
		flag_doInback = false;
		if (mAcTask != null) {
			if (mAcTask.getStatus().equals("RUNNING")) {
				mAcTask.cancel(true);
				Logger.i(TAG, "Status:" + mAcTask.getStatus());
			}
			Logger.i(TAG, "Status:" + mAcTask.getStatus());
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		initFlags();
		flag_doInback = true;
		if (mVirusPacknames.size() > 0) {
			mLL.removeAllViews();

			// int[] nums = new int[mVirusPacknames.size()];
			// int j = 0;

			List<String> removePackNames = new ArrayList<String>();
			for (int i = 0; i < mVirusPacknames.size(); i++) {
				String packname = mVirusPacknames.get(i);
				if (checkPackage(packname)) {
					initLinearLayoutWithVirus(packname);
				} else {
					removePackNames.add(packname);
				}

				// else {
				// nums[j++] = i;
				// }

			}

			// remove location from list<string> doesn't work.
			// for (int i = 0; i < nums.length; i++) {
			// mVirusPacknames.remove(nums[i]);
			// }

			// So I remove string from list<string>.
			for (String rpn : removePackNames) {
				mVirusPacknames.remove(rpn);
			}

			changeTextStatus();
		}
	}

	/**
	 * repalce the num of virus checked up.
	 */
	private void changeTextStatus() {

		mTv_result.setVisibility(View.VISIBLE);
		mTv_result.setTextSize(18); // the default unit is sp.
		int num = mVirusPacknames.size();
		// Using Html , you can show different text color in a textview.
		String htText = "扫描结束, 共发现" + "<font color='red'>" + num + "</font>"
				+ "个病毒.";
		mTv_result.setText(Html.fromHtml(htText));
	}

	/**
	 * check the package exist.
	 * 
	 * @param packName
	 * @return
	 */
	private boolean checkPackage(String packName) {
		if (packName == null || "".equals(packName)) {
			return false;
		}

		try {
			mPm.getPackageInfo(packName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * AsyncTask is a abstract class , if you want to oncreate and destory a
	 * asynctask instance,you must extends parent and create your own class.
	 * 
	 * @author tmac
	 *
	 */
	private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
		private List<PackageInfo> packageInfos;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Put the exactly place, or you will never new it.
			dao = new AvirusDao();
			mVirusPacknames.clear();
			packageInfos = getPackageManager().getInstalledPackages(
					PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_SIGNATURES);
			mPb.setMax(packageInfos.size());
			mTv_status.setText("正在查杀...");
			mTv_result.setVisibility(View.GONE);
			// If mLL doesn't have childview,it's nothing wrong.
			mLL.removeAllViews();

		}

		@Override
		protected Void doInBackground(Void... params) {
			int values = 0;
			if (flag_doInback) {

				for (PackageInfo info : packageInfos) {
					Signature[] signatures = info.signatures;
					String sign = signatures[0].toCharsString();
					// we can see the length of sign is so long,it's not
					// md5.
					String packname = info.packageName;

					// if
					// ("f1c38ab7fba3a0e2bb8c1d6f7688fd96".equals(MD5Encoder
					// .encode(sign))) {
					// Logger.i(TAG, "before dao mVirusPacknames：" + packname);
					// }

					if (dao.isVirus(sign)) {
						mVirusPacknames.add(packname);
						Logger.i(TAG, "mVirusPacknames：" + packname);
						/*
						 * Just for experiment,I add virus db a md5 which
						 * myvirus app signature it's
						 * f1c38ab7fba3a0e2bb8c1d6f7688fd96.
						 */
					}
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					publishProgress(++values);
				}

			}
			dao.closeDb();
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int progress = values[0];
			mPb.setProgress(progress);
			TextView tv = new TextView(AntiVirusActivity.this);
			int color = getResources().getColor(R.color.mytextcolor_black);
			tv.setTextColor(color);
			String packnane = packageInfos.get(progress - 1).packageName;
			tv.setText(packnane);
			mLL.addView(tv);
			// When LinearLayout add a new textview, scrollview move down 20
			// px
			mSlv.scrollBy(0, 20);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mTv_status.setText("查杀完毕.");
			mLL.removeAllViews();
			changeTextStatus();
			if (mVirusPacknames.size() > 0) {
				for (String packname : mVirusPacknames) {
					initLinearLayoutWithVirus(packname);
				}
			}
			flag_cv = true;
			flag_already_onclick = false;

			mIv_scan.clearAnimation();

		}
	}

	/**
	 * Let the onClick be called only when it's available.
	 * 
	 * @param view
	 */
	public void btnSearchKillOnclick(View view) {
		if (!flag_sk) {
			Toast.makeText(getApplicationContext(), "正在清理...", 0).show();
			return;
		}
		if (flag_already_onclick) {
			return;
		}
		flag_already_onclick = true;

		// control the rotate animation
		ra.reset();
		mIv_scan.startAnimation(ra);
		mAcTask = new MyAsyncTask();
		mAcTask.execute();

		// new Thread() {
		//
		// @Override
		// public void run() {
		// super.run();
		//
		// List<PackageInfo> packageInfos = getPackageManager()
		// .getInstalledPackages(
		// PackageManager.GET_UNINSTALLED_PACKAGES
		// | PackageManager.GET_SIGNATURES);
		// Message msg = Message.obtain();
		// for (PackageInfo info : packageInfos) {
		// Signature[] signatures = info.signatures;
		// String sign = signatures[0].toCharsString();
		// // System.out.println(sign);
		// // we can see the length of sign is so long,it's not md5.
		//
		// String packname = info.packageName;
		// msg.obj = packname;
		// handler.sendMessage(msg);
		// if (dao.isVirus(sign)) {
		// // TODO there is a virus.
		//
		// } else {
		//
		// }
		//
		// }
		//
		// }
		//
		// }.start();

	}

	/**
	 * After searchKill , show all app with virus in the LinearLayout.
	 * 
	 * @param packname
	 */
	protected void initLinearLayoutWithVirus(final String packname) {
		View childView = View.inflate(getApplicationContext(),
				R.layout.antivirus_item, null);

		childView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DEFAULT);
				intent.setData(Uri.parse("package:" + packname));
				startActivity(intent);
			}
		});

		ImageView iv = (ImageView) childView
				.findViewById(R.id.iv_antivirus_item_icon);
		TextView tvName = (TextView) childView
				.findViewById(R.id.tv_antivirus_item_name);

		String appName = getAppName(packname);
		Drawable drawable = getAppIcon(packname);
		tvName.setText(appName);
		iv.setImageDrawable(drawable);
		mLL.addView(childView);
	}

	protected Drawable getAppIcon(String packname) {
		try {
			PackageInfo info = mPm.getPackageInfo(packname, 0);
			return info.applicationInfo.loadIcon(mPm);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return getResources().getDrawable(R.id.icon);
		}
	}

	protected String getAppName(String packname) {
		try {
			PackageInfo info = mPm.getPackageInfo(packname, 0);
			return info.applicationInfo.loadLabel(mPm).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return packname;
		}
	}

	public void btnCleanVirusOnclick(View view) {
		if (!flag_cv) {
			Toast.makeText(getApplicationContext(), "先查杀才能清理", 0).show();
			return;
		}
		// If user begin CleanVirus, we let serchKill cann't go.
		flag_sk = false;

		if (mVirusPacknames.size() > 0) {
			for (String packname : mVirusPacknames) {

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DEFAULT);
				intent.setData(Uri.parse("package:" + packname));
				startActivity(intent);
			}
		} else {
			Toast.makeText(getApplicationContext(), "您的手机没有病毒,不需要清理.", 0)
					.show();
		}

		flag_sk = true;

	}

}
