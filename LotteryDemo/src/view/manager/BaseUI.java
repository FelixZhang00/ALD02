package view.manager;

import jamffy.example.lotterydemo.net.NetUtils;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.util.PromptManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public abstract class BaseUI implements View.OnClickListener {

	private Context context;
	// 实现在中间的容器
	protected ViewGroup showInMiddle;

	public BaseUI(Context context) {
		super();
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 需要子类为showInMiddle设置资源
	 */
	public abstract void init();

	/**
	 * 获取要在中间容器显示的界面
	 * 
	 * @return
	 */
	public View getChild() {

		if (showInMiddle.getLayoutParams() == null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			showInMiddle.setLayoutParams(params);
		}
		return showInMiddle;
	};

	/**
	 * 子类实现控件的监听
	 */
	public abstract void setListener();

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public View findViewById(int id) {
		return showInMiddle.findViewById(id);
	}

	/**
	 * 要求每一个子类提供表示自己的ID
	 * 
	 * @return
	 */
	public abstract int getID();

	/**
	 * 查询网络数据时的异步工具。<br>
	 * AsyncTask中抽象方法留给子类去做.
	 * 
	 * @author tmac
	 *
	 * @param <Params>
	 *            泛型化参数，让子类来决定
	 */
	protected abstract class MyHttpTask<Params> extends
			AsyncTask<Params, Void, Message> {

		/**
		 * 帮助子类实现时判断网络是否连通，不连通则弹出对话框
		 * 
		 * @param params
		 * @return
		 */
		public final AsyncTask<Params, Void, Message> executeProxy(
				Params... params) {
			if (NetUtils.checkNet(getContext())) {
				return super.execute(params);
			} else {
				PromptManager.showNoNetWork(getContext());
			}
			return null;
		}
	}

}
