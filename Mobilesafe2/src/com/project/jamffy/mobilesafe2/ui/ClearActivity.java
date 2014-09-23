package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 
 * 系统优化主界面 完成清理app的缓存数据及sd卡上垃圾数据的清理 只打印toast，功能实现在项目 myclearcache 和 myclearsd 中
 * 
 * @author tmac
 *
 */
public class ClearActivity extends Activity implements OnClickListener {

	private LinearLayout mll_clearcache;
	private LinearLayout mll_clearsd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.optmize);
		mll_clearcache = (LinearLayout) this
				.findViewById(R.id.ll_optimize_clearcache);
		mll_clearsd = (LinearLayout) this
				.findViewById(R.id.ll_optimize_clearsd);
		mll_clearcache.setOnClickListener(this);
		mll_clearsd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_optimize_clearcache:
			Intent clearcacheIntent = new Intent(ClearActivity.this,
					CleanCacheActivity2.class);
			startActivity(clearcacheIntent);
			break;
		case R.id.ll_optimize_clearsd:
			Toast.makeText(getApplicationContext(),
					"考虑到危险性,该功能在项目myclearsd中实现", 0).show();
			break;
		default:
			break;
		}
	}

}
