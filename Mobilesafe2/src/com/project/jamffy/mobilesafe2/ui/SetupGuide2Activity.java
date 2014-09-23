package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SetupGuide2Activity extends Activity implements OnClickListener {
	private Button bt_next;
	private Button bt_previous;
	private Button bt_bind;
	private CheckBox cb_bind;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide2);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);

		bt_bind = (Button) this.findViewById(R.id.bt_setupguide2_bind);
		bt_next = (Button) this.findViewById(R.id.bt_next);
		bt_previous = (Button) this.findViewById(R.id.bt_previous);
		cb_bind = (CheckBox) this.findViewById(R.id.cb_setupguide2_bind);

		bt_bind.setOnClickListener(this);
		bt_next.setOnClickListener(this);
		bt_previous.setOnClickListener(this);
		// 首先初始化checkbox
		String sim = sp.getString("sim", null);
		if (sim != null) {
			cb_bind.setText("已经绑定");
			cb_bind.setChecked(true);
		} else {
			cb_bind.setText("没有绑定");
			cb_bind.setChecked(false);
			resetSimInfo();
		}
		cb_bind.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cb_bind.setText("已经绑定");
					setSimInfo();
					Toast.makeText(getApplicationContext(), "绑定成功", 0).show();
				} else {
					cb_bind.setText("没有绑定");
					resetSimInfo();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_next:
			Intent intent3 = new Intent(SetupGuide2Activity.this,
					SetupGuide3Activity.class);
			// 一定要把当前activity从当前任务栈中移除
			finish();
			startActivity(intent3);
			// 设置activity切换时的动画效果
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		case R.id.bt_previous:
			Intent intent1 = new Intent(SetupGuide2Activity.this,
					SetupGuide1Activity.class);
			// 一定要把当前activity从当前任务栈中移除
			finish();
			startActivity(intent1);
			// 设置activity切换时的动画效果
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		case R.id.bt_setupguide2_bind:
			setSimInfo();
			if (cb_bind.isChecked()) {
				Toast.makeText(getApplicationContext(), "已经绑定了", 0).show();
			} else {
				cb_bind.setText("已经绑定");
				cb_bind.setChecked(true);
				Toast.makeText(getApplicationContext(), "绑定成功", 0).show();
			}
			break;
		default:
			break;
		}
	}

	private void setSimInfo() {
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String simserial = manager.getSimSerialNumber();
		Editor editor = sp.edit();
		editor.putString("sim", simserial);
		editor.commit();
	}

	private void resetSimInfo() {
		Editor editor = sp.edit();
		editor.putString("sim", null);
		editor.commit();
	}
}
