package view.manager;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.R;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 管理界面的标题栏 找上面的控件，提供多种显示样式
 * 
 * @author tmac
 *
 */
public class FooterManger implements Observer {
	protected static final String TAG = "FooterManger";
	// 单例设计模式
	private static FooterManger footerManger;

	private FooterManger() {
		super();
	}

	public static FooterManger getInstance() {
		if (footerManger == null) {
			footerManger = new FooterManger();
		}
		return footerManger;
	}

	/*********************************************************************************************/
	/******************* 第二步：初始化各个导航容器及相关控件设置监听 *********************************/

	/********** 底部菜单容器 **********/
	private RelativeLayout bottomMenuContainer;
	/************ 底部导航 ************/
	private LinearLayout commonBottom;// 购彩通用导航
	private LinearLayout playBottom;// 购彩

	/***************** 导航按钮 ******************/

	/************ 购彩导航底部按钮及提示信息 ************/
	private ImageButton cleanButton;
	private ImageButton addButton;

	private TextView playBottomNotice;

	/************ 通用导航底部按钮 ************/
	private ImageButton homeButton;
	private ImageButton hallButton;
	private ImageButton rechargeButton;
	private ImageButton myselfButton;

	public void init(Activity activity) {
		bottomMenuContainer = (RelativeLayout) activity
				.findViewById(R.id.footer);
		commonBottom = (LinearLayout) activity
				.findViewById(R.id.ii_bottom_common);
		playBottom = (LinearLayout) activity.findViewById(R.id.ii_bottom_game);

		playBottomNotice = (TextView) activity
				.findViewById(R.id.ii_bottom_game_choose_notice);
		cleanButton = (ImageButton) activity
				.findViewById(R.id.ii_bottom_game_choose_clean);
		addButton = (ImageButton) activity
				.findViewById(R.id.ii_bottom_game_choose_ok);

		// 设置监听
		setListener();
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		// 清空按钮
		cleanButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.i(TAG, "点击清空按钮");

			}
		});
		// 选好按钮
		addButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.i(TAG, "点击选好按钮");

			}
		});
	}

	/*********************************************************************************************/
	/****************** 第三步：控制各个导航容器的显示和隐藏 *****************************************/
	/**
	 * 转换到通用导航
	 */
	public void showCommonBottom() {
		if (bottomMenuContainer.getVisibility() == View.GONE
				|| bottomMenuContainer.getVisibility() == View.INVISIBLE) {
			bottomMenuContainer.setVisibility(View.VISIBLE);
		}
		commonBottom.setVisibility(View.VISIBLE);
		playBottom.setVisibility(View.INVISIBLE);
	}

	/**
	 * 转换到购彩
	 */
	public void showGameBottom() {
		if (bottomMenuContainer.getVisibility() == View.GONE
				|| bottomMenuContainer.getVisibility() == View.INVISIBLE) {
			bottomMenuContainer.setVisibility(View.VISIBLE);
		}
		commonBottom.setVisibility(View.INVISIBLE);
		playBottom.setVisibility(View.VISIBLE);
	}

	/**
	 * 改变底部导航容器显示情况
	 */
	public void changeBottomVisiblity(int type) {
		if (bottomMenuContainer.getVisibility() != type)
			bottomMenuContainer.setVisibility(type);
	}

	/*********************************************************************************************/
	/*********************** 第四步：控制玩法导航内容显示 ********************************************/
	/**
	 * 设置玩法底部提示信息
	 * 
	 * @param notice
	 */
	public void changeGameBottomNotice(String notice) {
		playBottomNotice.setText(notice);
	}

	/*********************************************************************************************/

	@Override
	public void update(Observable observable, Object data) {
		if (observable instanceof MiddleManager) {

			if (data != null && StringUtils.isNumeric(data.toString())) {
				int id = Integer.parseInt(data.toString());
				switch (id) {
				case ConstantValues.VIEW_FIRST:
					showCommonBottom();
					break;
				case ConstantValues.VIEW_SECOND:
					showGameBottom();
					break;
				case ConstantValues.VIEW_HALL:
					showCommonBottom();
					break;
				default:
					break;
				}
			}

		}
	}

}
