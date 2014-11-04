package jamffy.example.lotterydemo;

import jamffy.example.lotterydemo.util.FadeUtil;
import view.FirstUI;
import view.SecondUI;
import view.manager.BaseUI;
import view.manager.FooterManger;
import view.manager.MiddleManager;

import view.manager.TitleManger;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 刚开始的测试界面
 * 
 * @author tmac
 *
 */
public class MainActivityTest extends Activity {

	private RelativeLayout middle; // 中间的最外层布局，先占位
	private boolean curr_view = true;// 当前界面
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// changeUI();
			// 轮流切换一二界面
			// if (curr_view) {
			// changeUI(new SecondUI(MainActivityTest.this));
			// curr_view = false;
			// } else {
			// changeUI(new FirstUI(MainActivityTest.this));
			// curr_view = true;
			// }
			// handler.sendEmptyMessageDelayed(0, 2000);
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		TitleManger titleManger = TitleManger.getInstance();
		titleManger.init(this);
		// titleManger.showUnLoginTitle();

		FooterManger footerManger = FooterManger.getInstance();
		footerManger.init(this);
		// footerManger.showCommonBottom();

		middle = (RelativeLayout) findViewById(R.id.middle);
		MiddleManager.getInstance().setMiddle(middle);

		// 建立观察者与被观察者之间的联系
		MiddleManager.getInstance().addObserver(TitleManger.getInstance());
		MiddleManager.getInstance().addObserver(FooterManger.getInstance());

		// loadFirstUI();
		// 让第一个界面和其他界面都通过changeUI（）来加载，方便管理
		MiddleManager.getInstance().changeUI(FirstUI.class);

		// 过2s启动第二个界面
		// handler.sendEmptyMessageDelayed(0, 2000);
	}

	private View child1;

	private void loadFirstUI() {
		FirstUI firstUI = new FirstUI(this);
		child1 = firstUI.getChild();
		middle.addView(child1);
	}

	/**
	 * @deprecated 这个方法显然不合理，如果有几十个界面就得写几十个加载的方法。
	 */
	private void loadSecendUI() {
		SecondUI secondUI = new SecondUI(this);
		View child = secondUI.getChild();
		middle.addView(child);
		// FadeUtil.fadeIn(child, 2000, 2000);
	}

	/**
	 * 切换界面
	 * 
	 * @param ui
	 *            具体的界面实现类
	 */
	private void changeUI(BaseUI ui) {
		middle.removeAllViews();
		View child = ui.getChild();
		middle.addView(child);
		child.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.view_tran_change));
	}

	/**
	 * 切换界面
	 * 
	 * @deprecated 这个方法显然不合理，如果有几十个界面就得写几十个加载的方法。
	 */
	private void changeUI() {
		FadeUtil.fadeOut(child1, 0, 2000);
		// middle.removeAllViews();

		loadSecendUI();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			boolean result = MiddleManager.getInstance().goBack();
			if (!result) {
				Toast.makeText(getApplicationContext(), "是否退出", 0).show();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
