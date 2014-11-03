package view.manager;

import android.content.Context;
import android.view.View;

public abstract class BaseUI {

	private Context context;

	public BaseUI(Context context) {
		super();
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 获取要在中间容器显示的界面
	 * @return
	 */
	public abstract View getChild();

}
