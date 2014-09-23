package com.project.jamffy.mobilesafe2.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.project.jamffy.mobilesafe2.MyApplication;
import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.R.id;
import com.project.jamffy.mobilesafe2.domain.TaskInfo;
import com.project.jamffy.mobilesafe2.engine.TaskInfoProvider;
import com.project.jamffy.mobilesafe2.ui.view.MyToast;
import com.project.jamffy.mobilesafe2.utils.TextFormatUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CtrlTaskActivity extends Activity implements OnItemClickListener {

	private TextView tv_counts;
	private TextView tv_memory;
	private LinearLayout ll_load;
	private ActivityManager am;
	private ListView mLv_infos;
	private ViewHolder mHolder;
	private TaskInfoProvider provider;
	private UserListViewAdapter adapter;
	private List<TaskInfo> mTaskInfos;
	private List<TaskInfo> mUserInfos;
	private List<TaskInfo> mSystemInfos;
	private List<RunningAppProcessInfo> runningPsInfos;

	private boolean is_showsy;
	private long usedMemSize;
	private static final String TAG = "CtrlTaskActivity";
	private static final int LOAD_OK = 10;
	private SharedPreferences sp;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_OK:
				ll_load.setVisibility(View.INVISIBLE);
				adapter = new UserListViewAdapter(CtrlTaskActivity.this);
				mLv_infos.setAdapter(adapter);
				long totalMemSize = getAvailMem() + usedMemSize * 1024;
				String text = "剩余/总内存:"
						+ TextFormatUtil.byteFormater(getAvailMem()) + "/"
						+ TextFormatUtil.byteFormater(totalMemSize);
				tv_memory.setText(text);
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		setContentView(R.layout.ctrl_task);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		is_showsy = sp.getBoolean("is_show_system_pss_ts", true);
		mLv_infos = (ListView) this.findViewById(R.id.lv_ctrltask_user);
		tv_counts = (TextView) this.findViewById(R.id.tv_ctrltask_count);
		tv_memory = (TextView) this.findViewById(R.id.tv_ctrltask_memory);
		ll_load = (LinearLayout) this.findViewById(R.id.ll_ctrtask_load1);

		fillData();

		mLv_infos.setOnItemClickListener(this);
		mLv_infos.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 与 public Object getItem(int position) 方法对应
				Object obj = mLv_infos.getItemAtPosition(position);
				if (obj instanceof TaskInfo) {
					MyApplication myApp = (MyApplication) getApplication();
					Intent intent = new Intent(CtrlTaskActivity.this,
							PssDetailsActivity.class);
					myApp.taskInfo = (TaskInfo) obj;
					startActivity(intent);
				}
				return false;
			}

		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// 与 public Object getItem(int position) 方法对应
		Object obj = mLv_infos.getItemAtPosition(position);
		if (obj instanceof TaskInfo) {
			CheckBox cb = (CheckBox) view.findViewById(R.id.cb_ctrltask);
			TaskInfo taskInfo = (TaskInfo) obj;
			String packname = taskInfo.getPackname();
			if (packname.equals("system")
					|| packname.equals("android.process.media")
					|| packname.equals("com.project.jamffy.mobilesafe2")) {
				return;
			}

			if (taskInfo.isIschecked()) {
				taskInfo.setIschecked(false);
				cb.setChecked(false);
			} else {
				taskInfo.setIschecked(true);
				cb.setChecked(true);
			}
		} else { // 无法让listview中的textview不可点击
			view.setFocusable(false);
			view.setClickable(false);
			view.setEnabled(false);
		}

	}

	/**
	 * 填充 标题 和 listview 的数据
	 */
	private void fillData() {
		provider = new TaskInfoProvider(this);
		setTitleData();
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
				mTaskInfos = null;
				mTaskInfos = provider.getAllTaskInfo(runningPsInfos);
				usedMemSize = 0;
				for (TaskInfo taskInfo : mTaskInfos) {
					usedMemSize += taskInfo.getMemorysize(); // 一kb为单位
				}
				Message msg = Message.obtain();
				msg.what = LOAD_OK;
				handler.sendMessage(msg);
			}

		}.start();
	}

	private void setTitleData() {
		Logger.i(TAG, "setTitleData");
		tv_counts.setText("进程数目:" + getProgressCount());
		tv_memory.setText("剩余/总内存:"
				+ TextFormatUtil.byteFormater(getAvailMem()));
	}

	/**
	 * 获取当前系统的剩余的可用内存信息 byte long
	 */
	private long getAvailMem() {
		MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		Logger.i(TAG, "getAvailMem" + outInfo.availMem);
		return outInfo.availMem;

	}

	/**
	 * 获取当前正在运行的进程数目
	 * 
	 * @return 进程数目
	 */
	private int getProgressCount() {
		Logger.i(TAG, "getProgressCount");
		runningPsInfos = am.getRunningAppProcesses();
		Logger.i(TAG, "" + runningPsInfos.size());

		return runningPsInfos.size();

	}

	/**
	 * 杀死用户选中的进程
	 * 
	 * @param view
	 */
	public void killTask(View view) {
		int total = 0;
		int memorysize = 0;
		int checked = 0;
		for (TaskInfo taskInfo : mUserInfos) {
			if (taskInfo.isIschecked()) {
				memorysize += taskInfo.getMemorysize();
				checked++;
				am.killBackgroundProcesses(taskInfo.getPackname());
				total++;

			}
		}
		for (TaskInfo taskInfo : mSystemInfos) {
			if (taskInfo.isIschecked()) {
				memorysize += taskInfo.getMemorysize();
				checked++;
				am.killBackgroundProcesses(taskInfo.getPackname());
				total++;
			}
		}
		if (checked > 0) {
			// 通知用户杀死进程情况
			String size = TextFormatUtil.kbFormater(memorysize);
			// Toast.makeText(getApplicationContext(),
			// "杀死了" + total + "个进程，" + "释放了" + size + "内存空间", 0).show();
			MyToast.showToast(getApplicationContext(), "杀死了" + total + "个进程，"
					+ "释放了" + size + "内存空间", R.drawable.st_icon_cleandroid_cn);

			fillData();
		} else {
			Toast.makeText(getApplicationContext(), "请先选中进程", 0).show();
		}

	}

	/**
	 * 打开进程管理设置界面
	 * 
	 * @param view
	 */
	public void taskSetting(View view) {
		Intent taskSetingIntent = new Intent(CtrlTaskActivity.this,
				TaskSettingActivity.class);
		startActivityForResult(taskSetingIntent, 0);
	}

	private class UserListViewAdapter extends BaseAdapter {

		private Context context;

		/**
		 * 在构造方法里面完成了用户列表和系统程序列表的区分
		 */
		public UserListViewAdapter(Context context) {
			this.context = context;
			mUserInfos = new ArrayList<TaskInfo>();
			mSystemInfos = new ArrayList<TaskInfo>();
			for (TaskInfo taskInfo : mTaskInfos) {
				if (taskInfo.isSystemApp()) {
					mSystemInfos.add(taskInfo);
				} else {
					mUserInfos.add(taskInfo);
				}
			}
		}

		@Override
		public int getCount() {
			is_showsy = sp.getBoolean("is_show_system_pss_ts", true);
			if (is_showsy) {
				return runningPsInfos.size() + 2; // 2 为 在listview中的2个textview
			} else {
				return mUserInfos.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) { // 是 textview 则返回 常数
			if (position == 0) {
				return position;
			} else if (position <= mUserInfos.size()) {
				return mUserInfos.get(position - 1);
			} else if (position == mUserInfos.size() + 1) {
				return position;
			} else if (position <= mTaskInfos.size() + 1) {
				return mSystemInfos.get(position - mUserInfos.size() - 2);
			} else {
				return position;
			}

		}

		@Override
		public long getItemId(int position) { // textview 位置为-1
			if (position == 0) {
				return -1;
			} else if (position <= mUserInfos.size()) {
				return position - 1;
			} else if (position == mUserInfos.size() + 1) {
				return -1;
			} else if (position <= mTaskInfos.size() + 1) {
				return position - mUserInfos.size() - 2;
			} else {
				return -1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 把这些条目信息 做一下分类 系统进程和用户进程区分出来
			if (position == 0) {
				TextView tv_user = new TextView(CtrlTaskActivity.this);
				Drawable background = context.getResources().getDrawable(
						R.drawable.title_bg);
				tv_user.setBackgroundDrawable(background);
				tv_user.setText("用户进程:(" + mUserInfos.size() + ")");
				tv_user.setTextColor(0xff666666);
				return tv_user;
			} else if (position <= mUserInfos.size()) {
				int currentpositon = (position - 1);
				TaskInfo taskInfo = mUserInfos.get(currentpositon);
				return showItemView2(taskInfo);
			} else if (position == mUserInfos.size() + 1) {
				TextView tv_system = new TextView(CtrlTaskActivity.this);
				Drawable background = context.getResources().getDrawable(
						R.drawable.title_bg);
				tv_system.setBackgroundDrawable(background);
				tv_system.setText("系统进程:(" + mSystemInfos.size() + ")");
				tv_system.setTextColor(0xff666666);
				return tv_system;
			} else if (position <= mTaskInfos.size() + 1) {
				int currentpositon = position - mUserInfos.size() - 2;
				TaskInfo taskInfo = mSystemInfos.get(currentpositon);
				return showItemView2(taskInfo);
			} else {
				return null;
			}

		}

		/**
		 * 拖动能正常显示listview，缺点是没有用到缓存机制
		 * 
		 * @param taskInfo
		 * @return
		 */
		private View showItemView2(TaskInfo taskInfo) {
			View view = View.inflate(CtrlTaskActivity.this,
					R.layout.ctrltask_item, null);
			ViewHolder holder = new ViewHolder();
			holder.iv = (ImageView) view
					.findViewById(R.id.iv_ctrltask_item_icon);
			holder.tv_name = (TextView) view
					.findViewById(R.id.tv_ctrltask_item_name);
			holder.tv_size = (TextView) view
					.findViewById(R.id.tv_ctrltask_item_size);
			holder.cb = (CheckBox) view.findViewById(R.id.cb_ctrltask);
			holder.iv.setImageDrawable(taskInfo.getAppicon());
			holder.tv_name.setText("" + taskInfo.getAppname());
			holder.tv_size.setText("内存占用:"
					+ TextFormatUtil.kbFormater(taskInfo.getMemorysize()));

			// 判断是否为需要过滤的进程
			String packname = taskInfo.getPackname();
			if (packname.equals("system")
					|| packname.equals("android.process.media")
					|| packname.equals("com.project.jamffy.mobilesafe2")) {
				holder.cb.setVisibility(View.INVISIBLE);
			} else {
				holder.cb.setChecked(taskInfo.isIschecked());
			}
			return view;
		}

		/**
		 * @deprecated 拖动listview 就会报错
		 * @param position
		 * @param convertView
		 * @param taskInfos
		 * @return
		 */
		private View showItemView(int position, View convertView,
				List<TaskInfo> taskInfos) {
			View view = null;
			TaskInfo taskInfo = taskInfos.get(position);
			if (convertView != null) {
				view = convertView;
				mHolder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(CtrlTaskActivity.this,
						R.layout.ctrltask_item, null);
				mHolder = new ViewHolder();
				mHolder.iv = (ImageView) view
						.findViewById(R.id.iv_ctrltask_item_icon);
				mHolder.tv_name = (TextView) view
						.findViewById(R.id.tv_ctrltask_item_name);
				mHolder.tv_size = (TextView) view
						.findViewById(R.id.tv_ctrltask_item_size);
				mHolder.cb = (CheckBox) view.findViewById(R.id.cb_ctrltask);
				view.setTag(mHolder);
			}
			mHolder.iv.setImageDrawable(taskInfo.getAppicon());
			mHolder.tv_name.setText("" + taskInfo.getAppname());
			mHolder.tv_size.setText("内存占用:"
					+ TextFormatUtil.kbFormater(taskInfo.getMemorysize()));
			mHolder.cb.setChecked(taskInfo.isIschecked());
			return view;
		}

	}

	private static class ViewHolder {
		ImageView iv;
		TextView tv_name;
		TextView tv_size;
		CheckBox cb;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			fillData();
		}
	}

}
