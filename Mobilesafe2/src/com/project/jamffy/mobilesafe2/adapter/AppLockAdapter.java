package com.project.jamffy.mobilesafe2.adapter;

import java.util.List;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.AppInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author tmac
 *@deprecated 用AppLockActivity中的内部类 AppLockAdapter2 更好
 */
public class AppLockAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Context context;
	private List<AppInfo> appInfos;

	public AppLockAdapter(Context context, List<AppInfo> appInfos) {
		super();
		this.context = context;
		this.appInfos = appInfos;
		inflater = LayoutInflater.from(context);
	}

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
		ViewHolder holder;
		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			view = inflater.inflate(R.layout.applock_item, null);
			holder = new ViewHolder();
			holder.iv_icon = (ImageView) view
					.findViewById(R.id.iv_applock_item_icon);
			holder.tv_name = (TextView) view
					.findViewById(R.id.tv_applock_item_name);
			view.setTag(holder);
		}
		holder.iv_icon.setImageDrawable(appInfo.getIcon());
		holder.tv_name.setText(appInfo.getAppname());

		return view;
	}

	// 将Item中的控件使用static修饰，被static修饰的类的字节码在JVM中只会存在一份。iv_icon，tv_name在栈中也会只存在一份
	private static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
	}

}
