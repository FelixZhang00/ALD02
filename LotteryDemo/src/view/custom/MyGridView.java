package view.custom;

import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.util.DensityUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MyGridView extends GridView {
	// TODO
	// ①手指按下
	// 显示放大的号码
	// ②手指滑动
	// 更新：号码内容+显示位置
	// ③手指抬起
	// 修改手指下面的球的背景

	private PopupWindow pop;
	private TextView ball;

	private OnActionUpListener onActionUpListener;
	
	public void setOnActionUpListener(OnActionUpListener onActionUpListener) {
		this.onActionUpListener = onActionUpListener;
	}

	public MyGridView(Context context) {
		super(context);

	}

	/**
	 * 如果在布局文件中要使用此控件，需要此构造方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		View view = View.inflate(getContext(), R.layout.il_gridview_item_pop,
				null);
		ball = (TextView) view.findViewById(R.id.ii_pretextView);
		pop = new PopupWindow(getContext());
		pop.setContentView(view);
		pop.setBackgroundDrawable(null);

		/**
		 * Set to -1 for the default animation, 0 for no animation, or a
		 * resource identifier for an explicit animation.
		 */
		pop.setAnimationStyle(0);
		// 给pop设置宽高,需要用dip为单位，这样就可以根据手机屏幕的大小来调整pop的大小
		pop.setHeight(DensityUtil.dip2px(getContext(), 55));
		pop.setWidth(DensityUtil.dip2px(getContext(), 53));

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		// 由父类GridView提供的方法，可以根据坐标得到item的索引位置
		int position = pointToPosition(x, y);
		// 如果没有点击item，则向上返回touch事件
		if (position == INVALID_POSITION) {
			hidePop();
			return false;
		}
		TextView child = (TextView) this.getChildAt(position);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 当手指按下的时候，接管ScrollView滑动
			// this.getParent();//获取到LinearLayout
			 this.getParent().getParent()
			 .requestDisallowInterceptTouchEvent(true);
			showPop(child);
			break;

		case MotionEvent.ACTION_MOVE:
			updatePop(child);
			break;
		case MotionEvent.ACTION_UP:
			// 当手指抬起的时候，放行，ScrollView滑动
			this.getParent().getParent()
			.requestDisallowInterceptTouchEvent(false);
			hidePop();
			//给用户的监听传递参数
			if (onActionUpListener!=null) {
				onActionUpListener.onActionUp(child,position);
			}
			break;
		default:
			hidePop();
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void updatePop(TextView anchor) {
		int xoff = -(pop.getWidth() - anchor.getWidth()) / 2;
		int yoff = -(pop.getHeight() + anchor.getHeight());
		ball.setText(anchor.getText());
		// width the new width, can be -1 to ignore
		// height the new height, can be -1 to ignore
		pop.update(anchor, xoff, yoff, -1, -1);

	}

	private void hidePop() {
		if (pop.isShowing()) {
			pop.dismiss();
		}
	}

	private void showPop(TextView anchor) {
		int xoff = -(pop.getWidth() - anchor.getWidth()) / 2;
		int yoff = -(pop.getHeight() + anchor.getHeight());
		ball.setText(anchor.getText());
		pop.showAsDropDown(anchor, xoff, yoff);
	}
	
	/**
	 * 设置一个手指抬起的监听
	 * @author tmac
	 *
	 */
	public interface OnActionUpListener{
		void onActionUp(TextView child, int position);
	}

}
