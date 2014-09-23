package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LostProtectedSetActivity extends Activity implements
		OnClickListener {
	private SharedPreferences sp;
	private String TAG = "LostProtectdActivity";
	// 手机防盗主界面的控件
	private TextView tv_lostprotect_number;
	private TextView tv_reentry_setupguide;
	private CheckBox cb_isprotecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		setContentView(R.layout.lostprotect);
		showLostProtectSurface();
	}

	/**
	 * 显示手机防盗主界面
	 */
	private void showLostProtectSurface() {

		// 获取手机防盗主界面的控件
		tv_lostprotect_number = (TextView) this
				.findViewById(R.id.tv_lostprotect_number);
		tv_reentry_setupguide = (TextView) this
				.findViewById(R.id.tv_reentry_setupguide);
		cb_isprotecting = (CheckBox) this.findViewById(R.id.cb_isprotecting);
		// 控件初始化
		String number = sp.getString("safenumber", "");
		tv_lostprotect_number.setText("安全手机号码为:" + number);
		tv_reentry_setupguide.setOnClickListener(this);
		// 初始化checkbox的状态
		boolean isprotecting = sp.getBoolean("isprotecting", false);
		if (isprotecting) {
			cb_isprotecting.setChecked(true);
			cb_isprotecting.setText("防盗保护已开启");
		} else {
			cb_isprotecting.setChecked(false);
			cb_isprotecting.setText("防盗保护没有开启");
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
							cb_isprotecting.setText("防盗保护已开启");
						} else {

							// 如果用户没有开启手机防护，弹出对话框强烈建议其打开
							AlertDialog.Builder builder = new Builder(
									LostProtectedSetActivity.this);
							builder.setTitle("温馨提示");
							builder.setMessage("手机防盗极大地提高了手机的安全性，您确定将其关闭？！");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Editor editor = sp.edit();
											editor.putBoolean("isprotecting",
													false);
											editor.commit();
											cb_isprotecting.setText("防盗保护没有开启");
										}
									});
							builder.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											cb_isprotecting.setChecked(true);
											Editor editor = sp.edit();
											editor.putBoolean("isprotecting",
													true);
											editor.commit();
											cb_isprotecting.setText("防盗保护已开启");
										}
									});
							builder.create().show();
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_reentry_setupguide:
			Logger.i(TAG, "重新激活设置向导界面");
			Intent intent = new Intent(LostProtectedSetActivity.this,
					SetupGuide1Activity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
