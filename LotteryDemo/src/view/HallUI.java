package view;

import org.apache.commons.lang3.StringUtils;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.engine.BaseEngine;
import jamffy.example.lotterydemo.engine.CommInfoEngine;
import jamffy.example.lotterydemo.net.protocal.Element;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.Oelement;
import jamffy.example.lotterydemo.net.protocal.element.CurrentIssueElement;
import jamffy.example.lotterydemo.util.BeanFactory;
import jamffy.example.lotterydemo.util.PromptManager;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import view.manager.BaseUI;

/**
 * 购彩大厅界面
 * 
 * @author tmac
 *
 */
public class HallUI extends BaseUI {

	// 第一步：加载layout（布局参数设置）
	// 第二步：初始化layout中控件
	// 第三步：设置监听

	private TextView ssqIssue;
	private ImageView ssqBet;

	public HallUI(Context context) {
		super(context);
		init();
	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_HALL;
	}

	public void init() {
		showInMiddle = (LinearLayout) View.inflate(getContext(),
				R.layout.il_hall1, null);

		ssqIssue = (TextView) findViewById(R.id.ii_hall_ssq_summary);
		ssqBet = (ImageView) findViewById(R.id.ii_hall_ssq_bet);

		getCurrentIssusInfo();
	}

	@Override
	public void setListener() {
		ssqBet.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
	}

	/**
	 * 获取当前销售期信息
	 */
	private void getCurrentIssusInfo() {
		new MyHttpTask<Integer>() {

			/**
			 * 获取xml数据
			 */
			@Override
			protected Message doInBackground(Integer... params) {
				CommInfoEngine engine = BeanFactory
						.getImp(CommInfoEngine.class);
				return engine.getCurrentIssueInfo(params[0]);
			}

			/**
			 * 判断获取的数据
			 */
			@Override
			protected void onPostExecute(Message result) {
				if (result != null) {
					Oelement oelement = result.getBody().getOelement();
					if (ConstantValues.SUCCESS.equals(oelement.getErrorcode())) {
						// 如果验证正确就显示数据到界面
						changeNotice(result.getBody().getElements().get(0));
					} else {
						PromptManager.showToast(getContext(),
								oelement.getErrormsg());
					}

				} else {
					System.out.println("服务器忙，请稍后再试...");
					PromptManager.showToast(getContext(), "服务器忙，请稍后再试...");
				}
				super.onPostExecute(result);
			}

		}.executeProxy(ConstantValues.SSQ);
	}

	/**
	 * 更新彩票信息等提示的界面
	 * 
	 * @param element
	 */
	private void changeNotice(Element element) {
		CurrentIssueElement currentIssueElement = (CurrentIssueElement) element;
		String issue = currentIssueElement.getIssue();
		String lasttime = getLasttime(currentIssueElement.getLasttime());
		// 第ISSUE期 还有TIME停售
		String text = getContext().getResources().getString(
				R.string.is_hall_common_summary);
		text = StringUtils.replaceEach(text, new String[] { "ISSUE", "TIME" },
				new String[] { issue, lasttime });
		ssqIssue.setText(text);
	}

	/**
	 * 将秒时间转换成日时分格式
	 * 
	 * @param lasttime
	 * @return
	 */
	public String getLasttime(String lasttime) {
		StringBuffer result = new StringBuffer();
		if (StringUtils.isNumericSpace(lasttime)) {
			int time = Integer.parseInt(lasttime);
			int day = time / (24 * 60 * 60);
			result.append(day).append("天");
			if (day > 0) {
				time = time - day * 24 * 60 * 60;
			}
			int hour = time / 3600;
			result.append(hour).append("时");
			if (hour > 0) {
				time = time - hour * 60 * 60;
			}
			int minute = time / 60;
			result.append(minute).append("分");
		}
		return result.toString();
	}

}
