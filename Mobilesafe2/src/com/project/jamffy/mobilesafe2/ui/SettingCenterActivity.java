package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.service.AddressService;
import com.project.jamffy.mobilesafe2.service.LockAppService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingCenterActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {
	private Intent serviceIntent;
	private Intent lock_serviceIntent;
	private SharedPreferences sp;

	private TextView tv_autoupdate_status;
	private CheckBox cb_autoupdate;

	private TextView tv_adressservice_status;
	private CheckBox cb_adressservice;

	private TextView tv_lockservice_status;
	private CheckBox cb_lockservice;

	private LinearLayout ll_select_bg;
	private LinearLayout ll_change_site;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_center);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		tv_autoupdate_status = (TextView) this
				.findViewById(R.id.tv_setting_autoupdate_status);
		cb_autoupdate = (CheckBox) this
				.findViewById(R.id.cb_setting_autoupdate);

		tv_adressservice_status = (TextView) this
				.findViewById(R.id.tv_sc_adressservice_status);
		cb_adressservice = (CheckBox) this
				.findViewById(R.id.cb_sc_adressservice);

		tv_lockservice_status = (TextView) this
				.findViewById(R.id.tv_sc_lockservice_status);
		cb_lockservice = (CheckBox) this.findViewById(R.id.cb_sc_lockservice);
		ll_select_bg = (LinearLayout) this.findViewById(R.id.ll_sc_select_bg);
		ll_change_site = (LinearLayout) this
				.findViewById(R.id.ll_sc_change_site);
		checkAutoUpdate();
		checkAddressOpen();
		checkLockOpen();
		cb_autoupdate.setOnCheckedChangeListener(this);
		cb_adressservice.setOnCheckedChangeListener(this);
		cb_lockservice.setOnCheckedChangeListener(this);
		ll_select_bg.setOnClickListener(this);
		ll_change_site.setOnClickListener(this);
	}

	/**
	 * 检查开启号码服务
	 */
	private void checkAddressOpen() {
		serviceIntent = new Intent(this, AddressService.class);
		boolean isaddressopen = sp.getBoolean("isaddressopen", true);
		if (isaddressopen) {
			tv_adressservice_status.setText("号码归属地服务已开启");
			tv_adressservice_status.setTextColor(SettingCenterActivity.this
					.getResources().getColor(R.color.mytextcolor_black));
			startService(serviceIntent);
		} else {
			tv_adressservice_status.setText("号码归属地服务已关闭");
			tv_adressservice_status.setTextColor(SettingCenterActivity.this
					.getResources().getColor(R.color.mytextcolor_red));
			stopService(serviceIntent);
		}
		cb_adressservice.setChecked(isaddressopen);
	}

	/**
	 * 检查自动更新
	 */
	private void checkAutoUpdate() {
		boolean autoupdate = sp.getBoolean("autoupdate", true);
		if (autoupdate) {
			tv_autoupdate_status.setText("自动更新已开启");
			tv_autoupdate_status.setTextColor(SettingCenterActivity.this
					.getResources().getColor(R.color.mytextcolor_black));

		} else {
			tv_autoupdate_status.setText("自动更新已关闭");
			tv_autoupdate_status.setTextColor(SettingCenterActivity.this
					.getResources().getColor(R.color.mytextcolor_red));
		}
		cb_autoupdate.setChecked(autoupdate);

	}

	/**
	 * 检查程序锁服务
	 */
	private void checkLockOpen() {
		lock_serviceIntent = new Intent(this, LockAppService.class);
		boolean lockopen = sp.getBoolean("islockopen", false);
		if (lockopen) {
			tv_lockservice_status.setText("程序锁服务已开启");
			tv_lockservice_status.setTextColor(SettingCenterActivity.this
					.getResources().getColor(R.color.mytextcolor_black));
			startService(lock_serviceIntent);
		} else {
			tv_lockservice_status.setText("程序锁服务已关闭");
			tv_lockservice_status.setTextColor(SettingCenterActivity.this
					.getResources().getColor(R.color.mytextcolor_red));
			stopService(lock_serviceIntent);
		}
		cb_lockservice.setChecked(lockopen);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Editor editor = sp.edit();
		switch (buttonView.getId()) {
		case R.id.cb_setting_autoupdate:
			editor.putBoolean("autoupdate", isChecked);
			editor.commit();
			if (isChecked) {
				tv_autoupdate_status.setText("自动更新已开启");
				tv_autoupdate_status.setTextColor(SettingCenterActivity.this
						.getResources().getColor(R.color.mytextcolor_black));
			} else {
				tv_autoupdate_status.setText("自动更新已关闭");
				tv_autoupdate_status.setTextColor(SettingCenterActivity.this
						.getResources().getColor(R.color.mytextcolor_red));
			}
			break;
		case R.id.cb_sc_adressservice:
			editor.putBoolean("isaddressopen", isChecked);
			editor.commit();
			if (isChecked) {
				tv_adressservice_status.setText("号码归属地服务已开启");
				tv_adressservice_status.setTextColor(SettingCenterActivity.this
						.getResources().getColor(R.color.mytextcolor_black));
				startService(serviceIntent);
			} else {
				tv_adressservice_status.setText("号码归属地服务已关闭");
				tv_adressservice_status.setTextColor(SettingCenterActivity.this
						.getResources().getColor(R.color.mytextcolor_red));
				stopService(serviceIntent);
			}
			break;
		case R.id.cb_sc_lockservice:
			editor.putBoolean("islockopen", isChecked);
			editor.commit();
			if (isChecked) {
				tv_lockservice_status.setText("程序锁服务已开启");
				tv_lockservice_status.setTextColor(SettingCenterActivity.this
						.getResources().getColor(R.color.mytextcolor_black));
				// todo 开启服务
				startService(lock_serviceIntent);
			} else {
				tv_lockservice_status.setText("程序锁服务已关闭");
				tv_lockservice_status.setTextColor(SettingCenterActivity.this
						.getResources().getColor(R.color.mytextcolor_red));
				// todo 关闭服务
				stopService(lock_serviceIntent);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_sc_select_bg:
			showBGseleterDialog();
			break;
		case R.id.ll_sc_change_site:
			Intent changesiteIntent = new Intent(SettingCenterActivity.this,
					DragViewActivity.class);
			startActivity(changesiteIntent);
			break;
		default:
			break;
		}

	}

	/**
	 * 弹出选择颜色的对话框
	 */
	private void showBGseleterDialog() {
		String[] items = new String[] { "半透明", "活力橙", "苹果绿" };
		AlertDialog.Builder builder = new Builder(SettingCenterActivity.this);
		builder.setTitle("归属地提示显示风格");
		long bg_select_already = sp.getLong("adress_toast_bg", 0);
		builder.setSingleChoiceItems(items, (int) bg_select_already,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putLong("adress_toast_bg", which);
						editor.commit();
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

}
