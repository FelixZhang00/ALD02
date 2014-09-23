package com.project.jamffy.mobilesafe2.ui;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.db.dao.AppLockDao;
import com.project.jamffy.mobilesafe2.domain.AppInfo;
import com.project.jamffy.mobilesafe2.engine.AppInfoProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity implements OnItemClickListener {

	protected static final int LOCK_IV = 10;
	protected static final int UNLOCK_IV = 11;
	protected static final int LOAD_OK = 12;
	private List<AppInfo> appInfos;
	private AppInfoProvider provider;
	private AppLockAdapter2 adapter;
	private List<String> packnames;
	private AppLockDao dao;
	
	private ImageView iv_item;
	private ListView lv;
	private LinearLayout ll_load;
	private ViewHolder holder;
	private String TAG = "AppLockActivity";

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_OK:
				ll_load.setVisibility(View.INVISIBLE);
				adapter = new AppLockAdapter2();
				lv.setAdapter(adapter);
				break;
			case LOCK_IV:
				iv_item.setImageResource(R.drawable.lock);
				// lv.setEnabled(true);
				break;
			case UNLOCK_IV:
				iv_item.setImageResource(R.drawable.unlock);
				// lv.setEnabled(true);
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applock);
		lv = (ListView) this.findViewById(R.id.lv_applock);
		ll_load = (LinearLayout) this.findViewById(R.id.ll_applock_load);
		dao = new AppLockDao(AppLockActivity.this);
		packnames = dao.getAllData();
		
		initListViewUI();
		lv.setOnItemClickListener(this);
	}

	private void initListViewUI() {
		ll_load.setVisibility(View.VISIBLE);
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					sleep(200); // 为了方便观察，发布时要注释掉
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = Message.obtain();
				provider = new AppInfoProvider(AppLockActivity.this);
				appInfos = null;
				appInfos = provider.getAllApps();
				msg.what = LOAD_OK;
				handler.sendMessage(msg);
			}

		}.start();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
				0.3f, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		ta.setDuration(300);
		// lv.setEnabled(false);
		Logger.i(TAG, "id:" + id + "\t position" + position);
		AppInfo appInfo = appInfos.get(position);
		String packname = appInfo.getPackname();
		iv_item = (ImageView) view.findViewById(R.id.iv_applock);
		final Message msg = Message.obtain();
		ContentValues values = new ContentValues();
		if (!dao.find(packname)) {
			// 点击之前，该包名不在数据库中 ,需要加锁

			Uri uri = Uri
					.parse("content://com.project.jamffy.mobilesafe2.provider.AppLocksProvider/insert");
			values.put("packname", packname);
			getContentResolver().insert(uri, values);
			values.clear();

			Logger.i(TAG, packname + "\t add");
			packnames.add(packname);
			new Thread() { // 为了 锁图标在移除屏幕后改变

				@Override
				public void run() {
					super.run();
					try {
						sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					msg.what = LOCK_IV;
					handler.sendMessage(msg);
				}

			}.start();

			// holder.iv_lock.setImageResource(R.drawable.lock);
			// 这样只能设置当前屏幕最下面的 ，应该用view参数确定为当前点中的item
			// adapter.notifyDataSetChanged(); //使用于把某一item删除的情况
		} else {
			// 点击之前，该包名已在数据库中 ,需要解锁
			// iv_applock.setImageResource(R.drawable.unlock); //不可能加载出来这个控件
			Logger.i(TAG, packname + "delete");
			Uri uri = Uri
					.parse("content://com.project.jamffy.mobilesafe2.provider.AppLocksProvider/delete");
			getContentResolver().delete(uri, null, new String[] { packname });

			// dao.delete(packname);
			packnames.remove(packname);
			new Thread() {

				@Override
				public void run() {
					super.run();
					try {
						sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					msg.what = UNLOCK_IV;

					handler.sendMessage(msg);

				}

			}.start();

			// holder.iv_lock.setImageResource(R.drawable.unlock);
			// adapter.notifyDataSetChanged();
		}
		view.startAnimation(ta);

	}

	private class AppLockAdapter2 extends BaseAdapter {

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo = appInfos.get(position);
			View view = null;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(AppLockActivity.this,
						R.layout.applock_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_applock_item_icon);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_applock_item_name);
				holder.iv_lock = (ImageView) view.findViewById(R.id.iv_applock);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getAppname());

			// 虽然影响性能，先用频繁查询数据库的方式
			// if (dao.find(appInfo.getPackname())) {
			// holder.iv_lock.setImageResource(R.drawable.lock);
			// } else {
			// holder.iv_lock.setImageResource(R.drawable.unlock);
			// }

			// 我创建了一个类成员变量：加锁名单 ，这样就不用访问数据库了
			if (!packnames.isEmpty()) {
				if (packnames.contains(appInfo.getPackname())) {
					holder.iv_lock.setImageResource(R.drawable.lock);
				} else {
					holder.iv_lock.setImageResource(R.drawable.unlock);
				}
			} else {
				Logger.i(TAG, "packnames为空");
			}

			return view;
		}

	}

	// 将Item中的控件使用static修饰，被static修饰的类的字节码在JVM中只会存在一份。iv_icon，tv_name在栈中也会只存在一份
	private static class ViewHolder {
		ImageView iv_icon;
		ImageView iv_lock;
		TextView tv_name;
	}

}
