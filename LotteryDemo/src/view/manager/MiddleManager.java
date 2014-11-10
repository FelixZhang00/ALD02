package view.manager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.util.FadeUtil;
import jamffy.example.lotterydemo.util.MemoryManager;
import jamffy.example.lotterydemo.util.PromptManager;
import jamffy.example.lotterydemo.util.SoftMap;
import view.HallUI;
import view.PlaySSQ;
import view.manager.deprecated.SecondUI;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class MiddleManager extends Observable {

	private static final String TAG = "MiddleManager";
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

	private BaseUI currUI;

	public BaseUI getCurrUI() {
		return currUI;
	}

	/**
	 * 有频繁创建对象的问题
	 * 
	 * @param ui
	 * @deprecated
	 */
	public void changeUI(BaseUI ui) {
		middle.removeAllViews();
		View child = ui.getChild();
		middle.addView(child);
		// child.startAnimation(AnimationUtils.loadAnimation(getContext(),
		// R.anim.view_tran_change));
		FadeUtil.fadeIn(child, 0, 300);
	}

	/**
	 * 如果中间容器对象创建过了，就在此登个记. 如果手机内存空间不够了，还需要其他的解决方法
	 */
	private static Map<String, BaseUI> viewMap;

	static {
		// 16M，如果不足<16M(模拟器)
		// 32M，真机
		if (MemoryManager.hasAcailMemory()) {
			viewMap = new HashMap<String, BaseUI>();
		} else {
			viewMap = new SoftMap<String, BaseUI>();
		}
	}

	/**
	 * 通过字节码标示，避免了重复创建对象
	 * 
	 * @param targetClazz
	 *            BaseUI的子类的字节码
	 */
	public void changeUI(Class<? extends BaseUI> targetClazz) {
		// 判断需要创建的UI与当前的UI是否一致
		if (currUI != null && currUI.getClass() == targetClazz) {
			return;
		}
		String viewName = targetClazz.getSimpleName();
		BaseUI targetUI = null;
		// 判断：是否创建了——曾经创建过的界面需要存储
		// 创建了，重用
		if (viewMap.containsKey(viewName)) {
			targetUI = viewMap.get(viewName);
		} else {
			try {
				// 否则，创建
				// 通过某种构造器（主要指参数区别）来创建实例
				Constructor<? extends BaseUI> constructor = targetClazz
						.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());

				// 登记
				viewMap.put(viewName, targetUI);
			} catch (Exception e) {
				throw new RuntimeException("constructor newInstance error!");
			}
		}
		Log.i(TAG, targetUI.toString());

		// 界面pause前调用BaseUI的onPause（），具体的实现让子类来做
		if (currUI != null) {
			currUI.onPause();
		}

		middle.removeAllViews();
		View child = targetUI.getChild();
		middle.addView(child);
		FadeUtil.fadeIn(child, 0, 300);

		// 界面Resume后，让子类实现具体的Resume（）方法
		targetUI.onResume();
		currUI = targetUI;
		// 把当前界面放到栈顶
		historyView.addFirst(viewName);

		changeTitleAndFooter();
	}

	/**
	 * @param targetClazz
	 * @param bundle
	 *            传递的数据
	 */
	public void changeUI(Class<? extends BaseUI> targetClazz, Bundle bundle) {
		// 判断需要创建的UI与当前的UI是否一致
		if (currUI != null && currUI.getClass() == targetClazz) {
			return;
		}
		String viewName = targetClazz.getSimpleName();
		BaseUI targetUI = null;
		// 判断：是否创建了——曾经创建过的界面需要存储
		// 创建了，重用
		if (viewMap.containsKey(viewName)) {
			targetUI = viewMap.get(viewName);
		} else {
			try {
				// 否则，创建
				// 通过某种构造器（主要指参数区别）来创建实例
				Constructor<? extends BaseUI> constructor = targetClazz
						.getConstructor(Context.class);
				targetUI = constructor.newInstance(getContext());

				// 登记
				viewMap.put(viewName, targetUI);
			} catch (Exception e) {
				throw new RuntimeException("constructor newInstance error!");
			}
		}
		if (targetUI != null) {
			targetUI.setBundle(bundle);
		}

		// 界面pause前调用BaseUI的onPause（），具体的实现让子类来做
		if (currUI != null) {
			currUI.onPause();
		}

		middle.removeAllViews();
		View child = targetUI.getChild();
		middle.addView(child);
		FadeUtil.fadeIn(child, 0, 300);

		// 界面Resume后，让子类实现具体的Resume（）方法
		targetUI.onResume();
		currUI = targetUI;
		// 把当前界面放到栈顶
		historyView.addFirst(viewName);

		changeTitleAndFooter();
	}

	/**
	 * 当中间容器改变时，title、footer也跟着改变----联动。 需要找一个合适的对应关系：
	 * 比如①当前中间UI类的字节码（缺点：必须实例化此类）显然不合理。 ②要就每一个中间UI类提供一个表示自己的ID。
	 */
	private void changeTitleAndFooter() {
		// 耦合度太高。
		// switch (currUI.getID()) {
		// case ConstantValues.VIEW_FIRST:
		// TitleManger.getInstance().showUnLoginTitle();
		// FooterManger.getInstance().showCommonBottom();
		// break;
		//
		// case ConstantValues.VIEW_SECOND:
		// TitleManger.getInstance().showCommonTitle();
		// FooterManger.getInstance().showGameBottom();
		// break;
		//
		// default:
		// break;
		// }

		// MiddleManager负责通知即可
		setChanged();
		notifyObservers(currUI.getID());

	}

	/**
	 * 存放历史界面，序号为viewMap中的key。 主界面也包含在此栈中
	 */
	private LinkedList<String> historyView = new LinkedList<String>();

	/**
	 * 
	 * @return Return <code>true</code> ,成功将当前UI移除栈 or <code>false</code>
	 *         :当前UI为主UI，不能再退了
	 */
	public boolean goBack() {
		// 此时historyView 至少包含FirstUI
		if (historyView.size() == 1) {
			return false;
		}
		historyView.removeFirst();
		String key = historyView.getFirst();
		BaseUI targetUI = viewMap.get(key);

		if (targetUI != null) {
			currUI.onPause();
			middle.removeAllViews();
			View child = targetUI.getChild();
			middle.addView(child);
			targetUI.onResume();
			currUI = targetUI;
			FadeUtil.fadeIn(child, 0, 300);
			changeTitleAndFooter();

		} else {
			this.changeUI(HallUI.class);
			PromptManager.showToast(getContext(), "应用在低内存下运行");
		}

		return true;
	}

	public void clear() {
		historyView.clear();
	}

}
