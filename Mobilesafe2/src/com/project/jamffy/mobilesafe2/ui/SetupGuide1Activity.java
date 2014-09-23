package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetupGuide1Activity extends Activity implements OnClickListener {

	private Button bt_next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide1);
		bt_next = (Button) this.findViewById(R.id.bt_sepupguide_next);
		bt_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_sepupguide_next:
			Intent intent = new Intent(SetupGuide1Activity.this,
					SetupGuide2Activity.class);
			//一定要把当前activity从当前任务栈中移除
			finish();
			startActivity(intent);
			//设置activity切换时的动画效果
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;

		default:
			break;
		}
	}

}
