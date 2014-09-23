package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DragViewActivity extends Activity implements OnTouchListener,
		OnClickListener {
	protected static final String TAG = "DragViewActivity";
	private TextView tv_drag; // 要移动的view
	private TextView tv_dragview_declare; // 提示框
	private int windowHeight; // 屏幕的高度
	private int windowWidth;
	private SharedPreferences sp;
	// 记录起始触摸点的坐标
	private int startx;
	private int starty;
	private long firstclicktime; // 记录第一次点击时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drag_view);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		windowHeight = getWindowManager().getDefaultDisplay().getHeight();
		windowWidth = getWindowManager().getDefaultDisplay().getWidth();
		tv_dragview_declare = (TextView) this
				.findViewById(R.id.tv_dragview_declare);
		tv_drag = (TextView) this.findViewById(R.id.tv_drag);
		tv_drag.setOnTouchListener(this);
		tv_drag.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocateAllView();
	}

	private void LocateAllView() {
//		LayoutParams params0 = (LayoutParams) tv_drag.getLayoutParams();  //同样无法获取控件的长度
//		int xr = params0.rightMargin;  
//		int xl = params0.leftMargin;     
		
		int width = tv_drag.getRight() - tv_drag.getLeft(); // 得到控件的长宽   失败了 都是0
		int height = tv_drag.getBottom() - tv_drag.getTop();
		Logger.i(TAG, "windowHeight：" + windowHeight);
		int center_l = windowWidth / 2 - width / 2; // 找到控件的中央位置
		int center_t = windowHeight / 2 - height / 2;
		int center_b = windowHeight / 2 + height / 2;

		// 初始化上次view的位置
		int lastx = sp.getInt("lastx", center_l);
		int lasty = sp.getInt("lasty", center_t);
		int lastb = sp.getInt("lastb", center_b);
		Logger.i(TAG, "x=" + lastx);
		Logger.i(TAG, "y=" + lasty);
		Logger.i(TAG, "b=" + lastb);
		LayoutParams params = (LayoutParams) tv_drag.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		tv_drag.setLayoutParams(params);

		LayoutParams params2 = (LayoutParams) tv_dragview_declare
				.getLayoutParams();
		params2.leftMargin = 22;
		params2.rightMargin = 22;

		if (lastb < windowHeight / 2) { // 将提示框移到下方
			params2.topMargin = windowHeight / 2 + 20;
			params2.bottomMargin = params2.topMargin + 50;
			tv_dragview_declare.setLayoutParams(params2);
			Logger.i(TAG, "重新启动activity后，提示放在了下方");
		} else { // 将提示框移到上方
			params2.topMargin = 20;
			params2.bottomMargin = 50;
			tv_dragview_declare.setLayoutParams(params2);
			Logger.i(TAG, "重新启动activity后，提示放在了上方");
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 第一次触摸
			Logger.i(TAG, "第一次触摸");
			startx = (int) event.getRawX(); // 获取触摸点的坐标
			starty = (int) event.getRawY();

			break;
		case MotionEvent.ACTION_MOVE: // 移动
			int x = (int) event.getRawX(); // 获取移动的触摸点当前的坐标
			int y = (int) event.getRawY();

			int dx = x - startx;
			int dy = y - starty;
			// 计算出被拖动的view的上下左右距离 窗体的距离
			int l = tv_drag.getLeft();
			int t = tv_drag.getTop();
			int r = tv_drag.getRight();
			int b = tv_drag.getBottom();
			LocatTextView(b);

			int newl = l + dx;
			int newt = t + dy;
			int newr = r + dx; // 不是相减
			int newb = b + dy;
			// 通过对移动刚结束的View距离手机屏幕的四个边框的大小的判断，来避免View被移出屏幕
			if (newl < 0 || newt < 0 || newr > windowWidth
					|| newb > windowHeight) {
				break; // 提前退出当前的switch
			}

			// 将移动后的view在窗体中重新显示
			tv_drag.layout(newl, newt, newr, newb);
			// 立即更新触摸点位置，以便下次继续移动
			startx = (int) event.getRawX();
			starty = (int) event.getRawY();
			Logger.i(TAG, "移动");
			break;
		case MotionEvent.ACTION_UP: // 触摸停止
			Logger.i(TAG, "松手");
			// 记录下来最后拖动条的位置
			int lastx = tv_drag.getLeft();
			int lasty = tv_drag.getTop();
			int lastb = tv_drag.getBottom();
			Editor editor = sp.edit();
			editor.putInt("lastx", lastx);
			editor.putInt("lasty", lasty);
			editor.putInt("lastb", lastb);
			editor.commit();
			break;

		}
		// true 会消费调当前的触摸事件，那么后面的移动和离开事件会被响应到
		// false 不会消费当前的触摸事件，那么后面的移动和离开事件都不会被响应到
		return false;
	}

	/**
	 * 放置提示框
	 * 
	 * @param y
	 *            触摸点距离窗体上方的距离
	 */
	private void LocatTextView(int y) {
		int dragview_declare_l = tv_dragview_declare.getLeft();
		int dragview_declare_r = tv_dragview_declare.getRight();
		if (y < windowHeight / 2) { // 将提示框移到下方
			int dragview_declare_t = windowHeight / 2 + 20;
			int dragview_declare_b = dragview_declare_t + 50; // 50是默认的提示框的长度

			tv_dragview_declare.layout(dragview_declare_l, dragview_declare_t,
					dragview_declare_r, dragview_declare_b);
		} else { // 将提示框移到上方
			int dragview_declare_t = 20;
			int dragview_declare_b = dragview_declare_t + 50;
			tv_dragview_declare.layout(dragview_declare_l, dragview_declare_t,
					dragview_declare_r, dragview_declare_b);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.tv_drag:
		// Logger.i(TAG, "点击一次");
		// // 实现双击
		// // if (firstclicktime > 0) { // 表示不是第一次点击了
		// // 把控件放到中间，并上传位置,并且给提示框放置位置
		// int width = tv_drag.getRight() - tv_drag.getLeft(); // 得到控件的长宽
		// int height = tv_drag.getBottom() - tv_drag.getTop();
		// int center_l = windowWidth / 2 - width / 2;
		// int center_r = windowWidth / 2 + width / 2;
		// int center_t = windowHeight / 2 - height / 2;
		// int center_b = windowHeight / 2 + height / 2;
		// tv_drag.layout(center_l, center_t, center_r, center_b);
		//
		// LocatTextView(center_b); // 选择提示框的位置
		// Editor editor = sp.edit();
		// editor.putInt("lastx", center_l);
		// editor.putInt("lasty", center_t);
		// editor.putInt("lastb", center_b);
		// editor.commit();
		// // }
		// firstclicktime=System.currentTimeMillis();
		//
		// break;

		default:
			break;
		}

	}

}
