package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupGuide3Activity extends Activity implements OnClickListener {
	private Button bt_next;
	private Button bt_previous;
	private Button bt_select;
	private EditText et_number;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide3);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		bt_next = (Button) this.findViewById(R.id.bt_next);
		bt_previous = (Button) this.findViewById(R.id.bt_previous);
		bt_select = (Button) this.findViewById(R.id.bt_setupguide3_select);
		et_number = (EditText) this.findViewById(R.id.et_setupguide3_number);
		// 终止activity后edittext中的号码仍可保留
		String number = sp.getString("contactnumber", null);
		if(number!=null){
			et_number.setText(number);
		}
		
		bt_next.setOnClickListener(this);
		bt_previous.setOnClickListener(this);
		bt_select.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_next:
			String number = et_number.getText().toString().trim();
			if (number.equals("")) {
				Toast.makeText(getApplicationContext(), "请输入安全号码", 0).show();
				return;
			} else {
				Editor editor = sp.edit();
				editor.putString("safenumber", number);
				editor.commit();
				Toast.makeText(getApplicationContext(), "安全号码设置完成", 0).show();
			}
			Intent intent3 = new Intent(SetupGuide3Activity.this,
					SetupGuide4Activity.class);
			// 一定要把当前activity从当前任务栈中移除
			finish();
			startActivity(intent3);
			// 设置activity切换时的动画效果
			overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
			break;
		case R.id.bt_previous:
			Intent intent2 = new Intent(SetupGuide3Activity.this,
					SetupGuide2Activity.class);
			// 一定要把当前activity从当前任务栈中移除
			finish();
			startActivity(intent2);
			// 设置activity切换时的动画效果
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		case R.id.bt_setupguide3_select:
			Intent intent = new Intent(this, SelectContactActivity.class);
			// 激活一个带返回值的界面
			startActivityForResult(intent, 0);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			String number = data.getStringExtra("number");
			et_number.setText(number);
			Editor editor = sp.edit();
			editor.putString("contactnumber", number);
			editor.commit();
		}
	}

}
