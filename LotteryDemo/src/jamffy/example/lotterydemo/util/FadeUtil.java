package jamffy.example.lotterydemo.util;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 动画淡入淡出
 * 
 * @author tmac
 *
 */
public class FadeUtil {
	// 当前正在展示的淡出，动画的执行时间
	// 在这个执行过程中，第二界面处于等待状态
	// 第二个界面淡入，动画的执行时间

	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			View view = (View) msg.obj;
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
			super.handleMessage(msg);
		}

	};

	/**
	 * 动画淡入
	 * 
	 * @param child
	 *            执行动画的界面
	 * @param delay
	 *            延迟(保证界面淡出后才执行)
	 * @param duration
	 *            动画时间长度
	 */
	public static void fadeIn(View child, int delay, int duration) {
		AlphaAnimation alph = new AlphaAnimation(0, 1);
		alph.setStartOffset(delay);
		alph.setDuration(duration);
		child.setAnimation(alph);

	}

	/**
	 * 动画淡出 并移除此界面，否则会出现回光返照现象
	 * 
	 * @param child
	 * @param delay
	 *            一般置为0
	 * @param duration
	 */
	public static void fadeOut(final View child, int delay, int duration) {
		AlphaAnimation alph = new AlphaAnimation(1, 0);
		alph.setStartOffset(delay);
		alph.setDuration(duration);

		// 动画执行完成之后，做删除view的操作
		// 因为动画是在子线程中执行的，不能直接在child.setAnimation(alph) 后remove掉child
		// 在动画执行之前，先设置好监听到动画的某个阶段该作什么处理
		alph.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 交给handler处理：让父view删除子view
				Message msg = Message.obtain();
				msg.obj = child;
				handler.sendMessage(msg);
			}
		});
		child.setAnimation(alph);

	}

}
