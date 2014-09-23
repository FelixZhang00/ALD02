package com.project.jamffy.mobilesafe2.adapter;

import java.util.List;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.AppInfo;
import com.project.jamffy.mobilesafe2.engine.AppInfoProvider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CtrlAppAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<AppInfo> appInfos;

	private static TextView tv_name;
	private static TextView tv_size;
	private static ImageView im_icon;

	public CtrlAppAdapter(Context context, List<AppInfo> appInfos) {
		this.context = context;
		// 下面两种实例化的 inflater 的方法均可行
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflater = LayoutInflater.from(context);
		this.appInfos = appInfos;
	}

	@Override
	public int getCount() {
		return appInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return appInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// convertView 转化view对象，历史view对象的缓存
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppInfo info = appInfos.get(position);
		View view = null;
		if (convertView != null) { // 这样有优化listview 的作用
			view = convertView;
		} else {

			view = inflater.inflate(R.layout.ctrlapp_item, null);
		}
		im_icon = (ImageView) view.findViewById(R.id.iv_ctrlapp_item);
		tv_name = (TextView) view.findViewById(R.id.tv_ctrlapp_item_name);
		tv_size = (TextView) view.findViewById(R.id.tv_ctrlapp_item_size);

		// im_icon.setBackground(appInfos.get(position).getIcon());
		// im_icon.setImageResource(appInfos.get(position).getIcon());
		// im_icon.setBackground(background);
		
		im_icon.setImageDrawable(info.getIcon());
		tv_name.setText(info.getAppname());
		tv_size.setText(info.getAppsize());
		return view;
	}

}
