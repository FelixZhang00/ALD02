package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.utils.MD5Encoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LostProtectdActivity extends Activity implements OnClickListener {
	private SharedPreferences sp;
	private String TAG = "LostProtectdActivity";
	private Dialog dialog;
	private EditText et_pwd;
	private EditText et_confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		// 判断用户是否设置了密码
		if (isPWDsetup()) {
			Logger.i(TAG, "设置了密码，");
			showNomalEntryDialog();
		} else {
			Logger.i(TAG, "没有设置密码，显示第一次登录的对话框");
			showFirstEntryDialog();
		}
	}

	/**
	 * 正常登录的对话框
	 */
	private void showNomalEntryDialog() {
		dialog = new Dialog(this, R.style.MyDialog);
		// dialog.setContentView(R.layout.first_entry_dialog);

		View view = View.inflate(this, R.layout.normal_entry_dialog, null);
		et_pwd = (EditText) view.findViewById(R.id.et_normal_entry_pwd);
		Button bt_normal_pwd = (Button) view
				.findViewById(R.id.bt_normal_dialog_ok);
		Button bt_normal_cancel = (Button) view
				.findViewById(R.id.bt_normal_dialog_pwd_cancel);
		dialog.setCancelable(false); // 不能用返回建取消该登录框，但要退回到前一个activity
		bt_normal_cancel.setOnClickListener(this);
		bt_normal_pwd.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
	}

	/**
	 * 第一次进入程序的对话框
	 */
	private void showFirstEntryDialog() {
		dialog = new Dialog(this, R.style.MyDialog);
		// dialog.setContentView(R.layout.first_entry_dialog);

		View view = View.inflate(this, R.layout.first_entry_dialog, null);
		et_pwd = (EditText) view.findViewById(R.id.et_first_entry_pwd);
		et_confirm = (EditText) view.findViewById(R.id.et_first_entry_confirm);
		Button bt_pwd = (Button) view.findViewById(R.id.bt_first_dialog_ok);
		Button bt_cancel = (Button) view
				.findViewById(R.id.bt_first_dialog_pwd_cancel);
		bt_cancel.setOnClickListener(this);
		bt_pwd.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
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

	/*
	 * 有关button的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_first_dialog_ok:
			String pwd = et_pwd.getText().toString().trim();
			String pwd_confirm = et_confirm.getText().toString().trim();
			if ("".equals(pwd)) {
				Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
				return;
			} else {
				if ("".equals(pwd_confirm)) {
					Toast.makeText(getApplicationContext(), "请确认密码", 0).show();
					return;
				} else if (pwd.equals(pwd_confirm)) {
					Editor editor = sp.edit();
					editor.putString("password", MD5Encoder.encode(pwd));
					editor.commit();
					Toast.makeText(getApplicationContext(), "密码设置成功", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "两次密码不相同", 0)
							.show();
					return;
				}
			}
			dialog.dismiss();
			//进入设置设置向导
			Logger.i(TAG, "激活设置向导界面");
			finish();
			Intent intent1 = new Intent(LostProtectdActivity.this,
					SetupGuide1Activity.class);
			startActivity(intent1);
			break;
		case R.id.bt_first_dialog_pwd_cancel:
			dialog.dismiss();
			finish(); //结束当前activity，应该会进入主activity
			break;

		case R.id.bt_normal_dialog_ok:
			String pwd_normal = et_pwd.getText().toString().trim();
			if ("".equals(pwd_normal)) {
				Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
				return;
			} else {
				String realpwd = sp.getString("password", "'");
				if (realpwd.equals(MD5Encoder.encode(pwd_normal))) {
					if (isSetup()) { // true
						Logger.i(TAG, "加载手机防盗主界面");
						finish();
						Intent intent = new Intent(LostProtectdActivity.this,
								LostProtectedSetActivity.class);
						startActivity(intent);
					} else {
						Logger.i(TAG, "激活设置向导界面");
						finish();
						Intent intent = new Intent(LostProtectdActivity.this,
								SetupGuide1Activity.class);
						startActivity(intent);
					}
				} else {
					Toast.makeText(getApplicationContext(), "密码错误", 0).show();
					et_pwd.setText(""); // 清空错误密码
					return;
				}
			}
			dialog.dismiss();
			break;
		case R.id.bt_normal_dialog_pwd_cancel:
			dialog.dismiss();
			finish(); // 取消对话框后不能停留在当前的activity上，要返回
			break;

		default:
			dialog.dismiss();
			break;
		}
	}

	private boolean isSetup() {
		return sp.getBoolean("nomoreshowguide", false);
	}
}
