package com.project.jamffy.mobilesafe2.ui.view;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.R.id;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {

	/**
	 * @param context  
	 * @param text toast文本
	 * @param iconid 图片资源id
	 */
	public static void showToast(Context context, String text, int iconid) {
		View view = View.inflate(context, R.layout.mytoast, null);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_mytoast);
		TextView tv = (TextView) view.findViewById(R.id.tv_mytoast);
		tv.setText(text);
		iv.setImageResource(iconid);

		Toast toast = new Toast(context);
		toast.setDuration(0);
		toast.setView(view);

		toast.show();
	}
}
