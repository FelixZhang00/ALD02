package com.project.jamffy.mobilesafe2.adapter;

import com.project.jamffy.mobilesafe2.R;

import android.content.Context;
import android.content.SharedPreferences;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {

	// 布局填充器
	private LayoutInflater inflater;
	// 接受MainActivity传来的上下文对象
	private Context context;
	private String TAG = "MainAdapter";
	private static TextView tv_name;
	private static ImageView iv_icon;
	private SharedPreferences sp;

	// 将9个item的每一个图片对应的id都存入数组中
	private final static int[] icon = { R.drawable.widget01,
			R.drawable.widget02, R.drawable.widget03, R.drawable.widget04,
			R.drawable.widget05, R.drawable.widget06, R.drawable.widget07,
			R.drawable.widget08, R.drawable.widget09 };
	// 将item的每一标题都存入数组中
	private final static String[] names = { "手机防盗", "通信卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "系统优化", "高级工具", "设置中心" };

	public MainAdapter(Context context) {
		this.context = context;
		// 获取系统中的布局填充器
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Logger.i(TAG, position + "");
		
		View view = inflater.inflate(R.layout.mainscreen_item, null);
		tv_name = (TextView) view.findViewById(R.id.tv_mainscreen_item_name);
		iv_icon = (ImageView) view.findViewById(R.id.iv_mainscreen_item_icon);
		tv_name.setText(names[position]);
		iv_icon.setImageResource(icon[position]);
		if (position==0) {
			String name=sp.getString("lost_name", null);
			if(name!=null){
				tv_name.setText(name);
			}
		}
		return view;
	}

}
