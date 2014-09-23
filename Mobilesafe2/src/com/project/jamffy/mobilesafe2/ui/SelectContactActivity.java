package com.project.jamffy.mobilesafe2.ui;

import java.util.List;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.ContactInfo;
import com.project.jamffy.mobilesafe2.engine.ContactInfoService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactActivity extends Activity {
	private ListView lv;
	private List<ContactInfo> infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_contact);
		ContactInfoService service = new ContactInfoService(this);
		infos = service.getContactInfo(); // 终于拿到了数据集
		lv = (ListView) this.findViewById(R.id.lv_select_contact);
		lv.setAdapter(new SelectContactAdapter());
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String number=infos.get(position).getPhone();
				Intent intent=new Intent();
				intent.putExtra("number", number);
				setResult(0, intent);
				finish();
			}
		});
	}

	private class SelectContactAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactInfo info=infos.get(position);			
			// 因为布局简单，直接用代码生成布局
			LinearLayout layout = new LinearLayout(SelectContactActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);
			TextView tv_name=new TextView(SelectContactActivity.this);
			TextView tv_phone=new TextView(SelectContactActivity.this);
			tv_name.setText("姓名："+info.getName());
			tv_phone.setText("联系人电话："+info.getPhone());
			layout.addView(tv_name);
			layout.addView(tv_phone);
			return layout;
		}

	}

}
