package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.engine.NumberAddressService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryNumberActivity extends Activity implements OnClickListener {
	private EditText et_query;
	private TextView tv_query_number_address;
	private Button bt_from_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_number);
		tv_query_number_address = (TextView) this
				.findViewById(R.id.tv_query_number_address);
		et_query = (EditText) this.findViewById(R.id.et_query_number);
		bt_from_contact = (Button) this
				.findViewById(R.id.bt_querynumber_from_contact);
		bt_from_contact.setOnClickListener(this);
	}

	public void query(View view) {
		// 判断号码是否为空
		String number = et_query.getText().toString().trim();
		if (TextUtils.isEmpty(number)) { // 如果没有在edittext中输出，则产生抖动
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_query.startAnimation(shake);
		} else {
			// 打开数据库 查询号码归属地
			String address = NumberAddressService.getAddress(number);
			tv_query_number_address.setText("归属地信息：" + address);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_querynumber_from_contact:
			Intent intent = new Intent(QueryNumberActivity.this,
					SelectContactActivity.class);
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
			String querynumber = data.getExtras().getString("number");
			et_query.setText(querynumber);
		}
	}

}
