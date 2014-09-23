package com.project.jamffy.mobilesafe2.ui;

import java.util.List;
import java.util.Observer;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.db.dao.BlackNumberDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.InputType;
import android.text.TextUtils;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 黑名单管理界面
 * 
 * @author tmac
 * @date 2014-8-15 每成功添加一条黑名单，就通话记录中的相应数据清除
 */
public class SignalGuardActivity extends Activity {
	private List<String> numbers;
	private BlackNumberDao dao;
	private MyListViewAdapter adapter;

	private ListView lv_signalguard_ctrl_blacknumber;
	private Button bt_add;
	private LinearLayout ll_text;
	private final static String TAG = "SignalGuardActivity";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ctrl_blacknumber);
		lv_signalguard_ctrl_blacknumber = (ListView) this
				.findViewById(R.id.lv_crtlblacknumber);
		// 给listview注册上下文菜单
		registerForContextMenu(lv_signalguard_ctrl_blacknumber);

		bt_add = (Button) this.findViewById(R.id.bt_ctrl_sg_add);
		ll_text = (LinearLayout) this
				.findViewById(R.id.ll_ctrlblacknumber_text);
		dao = new BlackNumberDao(SignalGuardActivity.this);
		numbers = dao.getAllData();
		adapter = new MyListViewAdapter();
		lv_signalguard_ctrl_blacknumber.setAdapter(adapter);
		// 刷新页面
		// adapter.notifyDataSetChanged();
		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(null);
			}
		});

	}

	// 当activity呈现在用户面前时调用
	@Override
	protected void onStart() {
		super.onStart();
		String beingblacknumber = getIntent()
				.getStringExtra("beingblacknumber");
		if (beingblacknumber != null) {
			showDialog(beingblacknumber);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.csg_blacknumber_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int id = (int) info.id;
		String number = numbers.get(id);
		switch (item.getItemId()) {
		case R.id.item_alter:
			// 更改黑名单
			Logger.i(TAG, "更改黑名单");
			updateNumber(number);
			return true;
		case R.id.item_delet:
			// 删除黑名单
			Logger.i(TAG, "删除黑名单");
			if (dao.delete(number)) {
				numbers = dao.getAllData();
				// 通知listview更新界面
				adapter.notifyDataSetChanged();
				Logger.i(TAG, "删除成功");
				Toast.makeText(getApplicationContext(), "删除成功", 0).show();
			} else {
				Logger.i(TAG, "删除失败");
				Toast.makeText(getApplicationContext(), "删除失败", 0).show();
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void updateNumber(final String oldnumber) {
		AlertDialog.Builder builder = new Builder(SignalGuardActivity.this);
		builder.setTitle("修改黑名单号码");
		final EditText et = new EditText(SignalGuardActivity.this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		builder.setView(et);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newnumber = et.getText().toString().trim();
				if (TextUtils.isEmpty(newnumber)) {
					Toast.makeText(getApplicationContext(), "号码不能为空", 1).show();
					return;
				} else {
					if (dao.update(oldnumber, newnumber)) {
						Toast.makeText(getApplicationContext(), "成功修改黑名单号码", 1)
								.show();
						numbers = dao.getAllData();
						// 让数据适配器通知listview更新数据
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(), "没有修改成功", 1)
								.show();
					}

				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		builder.create().show();

	}

	/**
	 * 创建并显示对话框
	 * 
	 * @param number
	 *            提前设置的号码
	 */
	private void showDialog(String number) {
		AlertDialog.Builder builder = new Builder(SignalGuardActivity.this);
		builder.setTitle("添加黑名单号码");
		final EditText et = new EditText(SignalGuardActivity.this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		et.setText(number);
		builder.setView(et);
		builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String number = et.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "号码不能为空", 1).show();
					return;
				} else {
					if (dao.add(number)) {
						Toast.makeText(getApplicationContext(), "成功添加黑名单号码", 1)
								.show();
						numbers = dao.getAllData();
						// 让数据适配器通知listview更新数据
						adapter.notifyDataSetChanged();
						// 每成功添加一条黑名单，就通话记录中的相应数据清除
						deleteCallLog(number);
					} else {
						Toast.makeText(getApplicationContext(), "该号码已经存在", 1)
								.show();
					}

				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		builder.create().show();
	}

	/**
	 * 删除黑名单号码
	 * 
	 * @param incomingNumber
	 */
	private void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null,
				"number=?", new String[] { incomingNumber }, null);
		while (cursor.moveToNext()) { // 查询到了呼叫记录
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?",
					new String[] { id });
		}
	}

	private class MyListViewAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			int count = numbers.size();
//			System.out.println("个数为：" + count);
			if (count == 0) {
				ll_text.setVisibility(View.VISIBLE);
			}else{
				ll_text.setVisibility(View.INVISIBLE);
			}
			return numbers.size();
		}

		@Override
		public Object getItem(int position) {
			return numbers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(SignalGuardActivity.this,
					R.layout.ctrl_blacknumber_item, null);
			TextView tv = (TextView) view.findViewById(R.id.tv_csg_number);
			tv.setText(numbers.get(position));
			return view;
		}

	}

}
