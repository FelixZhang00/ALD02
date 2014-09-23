package com.project.jamffy.mobilesafe2.ui;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.TrafficInfo;
import com.project.jamffy.mobilesafe2.engine.TrafficInfoProvider;
import com.project.jamffy.mobilesafe2.utils.ImageUtil;
import com.project.jamffy.mobilesafe2.utils.TextFormatUtil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer.TrackInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TrafficInfoActivity extends Activity {

	private Timer timer;
	private TimerTask task;
	private ListView lv;
	private LinearLayout ll_load;
	private List<TrafficInfo> trafficInfos;
	private TrafficInfoProvider provider;
	private TrafficAdapter adapter;
	private ViewHolder holder;
	private final static String TAG = "TrafficInfoActivity";
	protected static final int LOAD_OK = 11;
	protected static final int ITEM_CHANGED = 12;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_OK:
				ll_load.setVisibility(View.INVISIBLE);
				adapter = new TrafficAdapter(TrafficInfoActivity.this);
				lv.setAdapter(adapter);
				break;
			case ITEM_CHANGED:
				if (adapter!=null) {
					adapter.notifyDataSetChanged();					
				}
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_info);
		lv = (ListView) this.findViewById(R.id.lv_traffic_manager);
		ll_load = (LinearLayout) this.findViewById(R.id.ll_ti_loading);
		Logger.i(TAG, "onCreate");
		initListViewUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				msg.what = ITEM_CHANGED;
				handler.sendMessage(msg);
			}
		};
		timer.schedule(task, 1000, 2000);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		timer.cancel();
		timer=null;
		task=null;
	}

	private void initListViewUI() {
		ll_load.setVisibility(View.VISIBLE);
		new Thread() { // 用于查找所有app的耗时操作

			@Override
			public void run() {
				super.run();
				try {
					Thread.sleep(500); // 在加载的时间内，按钮点了也不会报错
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				provider = new TrafficInfoProvider(TrafficInfoActivity.this);
				trafficInfos = provider.getTrafficInfos();
				Message msg = Message.obtain();
				msg.what = LOAD_OK;
				handler.sendMessage(msg);
			}
		}.start();
	}

	private class TrafficAdapter extends BaseAdapter {
		private Context context;
		private PackageManager pm;

		public TrafficAdapter(Context context) {
			this.context = context;
			pm = getPackageManager();
		}

		@Override
		public int getCount() {
			return trafficInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return trafficInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = View.inflate(TrafficInfoActivity.this,
						R.layout.traffic_info_item2, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) view
						.findViewById(R.id.iv_traffic_item_icon);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_traffic_item_appname);
				holder.tv_rx = (TextView) view
						.findViewById(R.id.tv_traffic_item_re);
				holder.tv_tx = (TextView) view
						.findViewById(R.id.tv_traffic_item_te);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			TrafficInfo trafficInfo = trafficInfos.get(position);
			Drawable drawable = trafficInfo.getIcon();
			Bitmap bitmap = ImageUtil.getResizeBitmap(TrafficInfoActivity.this,
					(BitmapDrawable) drawable);
			holder.iv.setImageBitmap(bitmap);
			holder.tv_name.setText(trafficInfo.getAppname());

			// String mobiletotal =
			// TextFormatUtil.byteFormater(trafficInfos.get(
			// position).getMobiletotal());
			// holder.tv_mobile.setText(mobiletotal);
			// String wifitotal = TextFormatUtil.byteFormater(trafficInfos.get(
			// position).getWifitotal());
			// holder.tv_wifi.setText(wifitotal);

			String packname = trafficInfo.getPackname();
			try {
				PackageInfo packageInfo = pm.getPackageInfo(packname, 0);
				int uid = packageInfo.applicationInfo.uid;
				String rx = TextFormatUtil.byteFormater(TrafficStats
						.getUidRxBytes(uid));
				String tx = TextFormatUtil.byteFormater(TrafficStats
						.getUidTxBytes(uid));
				holder.tv_rx.setText(rx);
				holder.tv_tx.setText(tx);

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return view;
		}
	}

	private static class ViewHolder {
		ImageView iv;
		TextView tv_name;
		TextView tv_rx;
		TextView tv_tx;
	}

}
