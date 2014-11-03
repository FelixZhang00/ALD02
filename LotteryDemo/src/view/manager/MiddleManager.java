package view.manager;

import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.util.FadeUtil;
import view.SecondUI;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class MiddleManager {

	private static MiddleManager middleManager = new MiddleManager();

	private MiddleManager() {
		super();
	}

	public static MiddleManager getInstance() {
		return middleManager;
	}

	private RelativeLayout middle;

	public void setMiddle(RelativeLayout middle) {
		this.middle = middle;
	}

	public Context getContext() {
		return middle.getContext();
	}

	public void changeUI(BaseUI ui) {
		middle.removeAllViews();
		View child = ui.getChild();
		middle.addView(child);
		// child.startAnimation(AnimationUtils.loadAnimation(getContext(),
		// R.anim.view_tran_change));
		FadeUtil.fadeIn(child, 0, 1000);
	}

}
