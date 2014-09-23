package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.service.IService;
import com.project.jamffy.mobilesafe2.service.LockAppService;
import com.project.jamffy.mobilesafe2.utils.MD5Encoder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockDoorActivity extends Activity implements OnClickListener {

	private EditText ed_pwd;
	private SharedPreferences sp;
	private ImageButton bt_confirm;
	private String TAG = "LockDoorActivity";
	private IService iService;
	private String packname;
	private MyConn conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lockdoor);
		conn=new MyConn();
		Intent LserviceIntent = new Intent(this, LockAppService.class);
		bindService(LserviceIntent, conn, BIND_AUTO_CREATE);
		ed_pwd = (EditText) this.findViewById(R.id.et_lockdoor);
		bt_confirm = (ImageButton) this.findViewById(R.id.bt_lockdoor);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		identUI();
		bt_confirm.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.i(TAG, "onResume");

	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.i(TAG, "onPause");
		// 当activity不可见时就将其关闭
		finish();
	}

	/**
	 * 实例化 lockdoor 的icon和appname
	 */
	private void identUI() {
		ImageView iv = (ImageView) this.findViewById(R.id.iv_lockdoor_icon);
		TextView tv = (TextView) this.findViewById(R.id.tv_lockdoor_name);
		Intent intent = getIntent();
		packname = intent
				.getStringExtra("com.project.jamffy.mobilesafe2.service.packname");
		Logger.i(TAG, packname);

		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packname, 0); // 根据包名得到APP的name、icon
			iv.setImageDrawable(info.loadIcon(pm));
			tv.setText(info.loadLabel(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_lockdoor:
			openDoor();
			break;
		default:
			break;
		}

	}

	/**
	 * 验证密码，打开密码锁
	 */
	private void openDoor() {
		String realPwd = sp.getString("password", null);
		String pwd = ed_pwd.getText().toString().trim();
		if (pwd.equals("")) {
			// 动画效果
			Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			ed_pwd.startAnimation(shake);
			return;
		}
		if (MD5Encoder.encode(pwd).equals(realPwd)) {
			// 把当前app包名存到 TempUnlocks 列表中
			iService.callUnLockApps(packname);
			// 关闭当前lockdoor
			finish();
		} else {
			// 动画效果
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			ed_pwd.startAnimation(shake);
			ed_pwd.setText("");
			Toast.makeText(getApplicationContext(), "密码输入错误", 0).show();
		}
	}

	public class MyConn implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
//			System.out.println("onServiceConnected");
			iService = (IService) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
//			System.out.println("onServiceDisconnected");
		}

	}

	// 屏蔽回退键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true; // to prevent this event from being propagated further
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.i(TAG, "onDestroy");
		unbindService(conn);
		Logger.i(TAG, "unbindService");
	}

}
