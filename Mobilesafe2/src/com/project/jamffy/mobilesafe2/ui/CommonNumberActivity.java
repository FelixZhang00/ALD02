package com.project.jamffy.mobilesafe2.ui;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.db.dao.CommNumDao;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CommonNumberActivity extends Activity implements OnChildClickListener {

	private ExpandableListView elv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_num);

		elv = (ExpandableListView) this.findViewById(R.id.elv_commnumber);
		elv.setAdapter(new CommNumberAdapter());
		//为分组中的每个孩子注册一个监听器
		elv.setOnChildClickListener(this);
		
	}

	private class CommNumberAdapter extends BaseExpandableListAdapter {
		// 存储组名
		private List<String> groupNames;

		// 将子孩子的所有信息一次性从数据库中获取出来，这样可以避免重复查询数据库内存缓存集合。key：分组的位置
		// value：分组里面所有子孩子的信息
		private Map<Integer, List<String>> childrenCache;

		public CommNumberAdapter() {
			childrenCache = new HashMap<Integer, List<String>>();
		}

		@Override
		public int getGroupCount() {
			return CommNumDao.getGroupCount();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return CommNumDao.getChildrenCount(groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				tv = new TextView(getApplicationContext());
			} else {
				tv = (TextView) convertView;
			}
			tv.setTextSize(28);
			if (groupNames == null) {
				groupNames = CommNumDao.getGroupNames();
				tv.setText("      " + groupNames.get(groupPosition));
			} else {
				tv.setText("      " + groupNames.get(groupPosition));
			}

			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				tv = new TextView(getApplicationContext());
			} else {
				tv = (TextView) convertView;
			}
			tv.setTextSize(20);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			String result = null;
			if (childrenCache.containsKey(groupPosition)) {
				result = childrenCache.get(groupPosition).get(childPosition);
			} else {
				List<String> results = CommNumDao
						.getChildNameByPosition(groupPosition);
				childrenCache.put(groupPosition, results);
				result = results.get(childPosition);
			}
			tv.setText(result);
			return tv;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// 返回值如果为true，则表示每个分组的子孩子都可以响应到点击事件，否则，不可以响应
			return true;
		}

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		TextView tv=(TextView) v;
		String[] strs=tv.getText().toString().split("\n");
		String number=strs[1];
		String name=strs[0];
		//启用隐式意图激活dial拨号器
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		//传递额外的数据
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
		Toast.makeText(getApplicationContext(), "马上拨打 "+name+" 的电话", 0).show();
		return false;
	}

}
