package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.receiver.MyAdmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SetupGuide4Activity extends Activity implements OnClickListener {
	private Button bt_pre;
	private Button bt_finish;
	private CheckBox cb_isprotecting;
	private SharedPreferences sp;
	private boolean isprotecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide4);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 当用户看到这个页面时，打算不再展示setupguide
		Editor editor = sp.edit();
		editor.putBoolean("nomoreshowguide", true);
		editor.commit();

		bt_pre = (Button) this.findViewById(R.id.bt_pre);
		bt_finish = (Button) this.findViewById(R.id.bt_setup_finish);
		cb_isprotecting = (CheckBox) this
				.findViewById(R.id.cb_setupguide4_isprotecting);
		// 初始化checkbox的状态
		isprotecting = sp.getBoolean("isprotecting", false);
		if (isprotecting) {
			cb_isprotecting.setChecked(true);
			cb_isprotecting.setText("保护已开启");
		} else {
			cb_isprotecting.setChecked(false);
			cb_isprotecting.setText("保护没有开启");
		}
		cb_isprotecting
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Editor editor = sp.edit();
							editor.putBoolean("isprotecting", true);
							editor.commit();
							cb_isprotecting.setText("保护已开启");
						} else {
							Editor editor = sp.edit();
							editor.putBoolean("isprotecting", false);
							editor.commit();
							cb_isprotecting.setText("保护没有开启");
						}
					}
				});

		bt_finish.setOnClickListener(this);
		bt_pre.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_pre:
			Intent intent3 = new Intent(SetupGuide4Activity.this,
					SetupGuide3Activity.class);
			// 一定要把当前activity从当前任务栈中移除
			finish();
			startActivity(intent3);
			// 设置activity切换时的动画效果
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		case R.id.bt_setup_finish:
			isprotecting = sp.getBoolean("isprotecting", false); //必须要重新获取一遍
			if (isprotecting) {
				finshSetup();
				finish();
				
				Toast.makeText(getApplicationContext(), "恭喜你，完成了设置", 0).show();
			} else { // 如果没有勾选checkbox，弹出对话框
				AlertDialog.Builder builder = new Builder(
						SetupGuide4Activity.this);
				builder.setTitle("提醒");
				builder.setMessage("强烈建议开启手机防盗，是否开启手机防盗");
				builder.setNegativeButton("否",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});
				builder.setPositiveButton("是",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finshSetup();
								cb_isprotecting.setChecked(true);
								finish();
							}
						});
				builder.create().show();
				return;
			}
			break;

		default:
			break;
		}
	}

	private void finshSetup() {
		Editor editor = sp.edit();
		editor.putBoolean("isprotecting", true);
		editor.commit();
		//用以启动授权该应用程序为超级管理员的activity
		DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

		ComponentName mAdminName = new ComponentName(this, MyAdmin.class);

		if (!manager.isAdminActive(mAdminName)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			startActivity(intent);
		}

	}
}
