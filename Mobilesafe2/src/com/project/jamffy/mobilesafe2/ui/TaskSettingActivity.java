package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.R.id;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class TaskSettingActivity extends Activity {

	private TextView tv_showsy;
	private CheckBox cb_showsy;
	private TextView tv_clean;
	private CheckBox cb_clean;
	private SharedPreferences sp;
	private boolean is_show;
	private boolean is_clean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_setting_center);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		is_show = sp.getBoolean("is_show_system_pss_ts", true);
		is_clean = sp.getBoolean("is_clean_pss_ts", false);
		initView();
		CheckBoxChecked();
	}

	private void CheckBoxChecked() {
		
		final Editor editor=sp.edit();
		cb_showsy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
					is_show=isChecked;
					initCheckBox();
					editor.putBoolean("is_show_system_pss_ts", isChecked);
					editor.commit();
					setResult(RESULT_OK);
			}
		});

		cb_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				is_clean=isChecked;
				initCheckBox();
				editor.putBoolean("is_clean_pss_ts", isChecked);
				editor.commit();
				
			}
		});
	}

	/**
	 * 找到布局中的控件 并设置状态
	 */
	private void initView() {
		tv_showsy = (TextView) this
				.findViewById(R.id.tv_task_setting_showsy_status);
		tv_clean = (TextView) this.findViewById(R.id.tv_ts_sfclean_status);
		cb_clean = (CheckBox) this.findViewById(R.id.cb_ts_sfclean);
		cb_showsy = (CheckBox) this.findViewById(R.id.cb_task_setting_showsy);
		initCheckBox();

	}

	private void initCheckBox() {
		if (is_show) {
			tv_showsy.setText("已显示");
			tv_showsy.setTextColor(getResources().getColor(
					R.color.mytextcolor_black));
			cb_showsy.setChecked(is_show);
		} else {
			tv_showsy.setText("未显示");
			tv_showsy.setTextColor(getResources().getColor(
					R.color.mytextcolor_red));
			cb_showsy.setChecked(is_show);
		}

		if (is_clean) {
			tv_clean.setText("已开启");
			tv_clean.setTextColor(getResources().getColor(
					R.color.mytextcolor_black));
			cb_clean.setChecked(is_clean);
		} else {
			tv_clean.setText("未开启");
			tv_clean.setTextColor(getResources().getColor(
					R.color.mytextcolor_red));
			cb_clean.setChecked(is_clean);
		}
	}

}
