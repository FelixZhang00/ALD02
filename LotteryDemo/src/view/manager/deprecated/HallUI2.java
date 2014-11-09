package view.manager.deprecated;

import java.util.List;

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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import view.manager.BaseUI;

/**
 * 购彩大厅界面 以listvie的形式显示
 * 
 * @author tmac
 *
 */
public class HallUI2 extends BaseUI {

	// 第一步：加载layout（布局参数设置）
	// 第二步：初始化layout中控件
	// 第三步：设置监听

	private TextView ssqIssue;
	private ImageView ssqBet;
	// 用于显示彩票列表的list
	private ListView categoryListView;
	private categoryAdapter adapter;

	public HallUI2(Context context) {
		super(context);
		init();
	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_HALL;
	}

	public void init() {
		showInMiddle = (LinearLayout) View.inflate(getContext(),
				R.layout.il_hall2, null);
		categoryListView = (ListView) findViewById(R.id.ii_hall_lottery_list);
		adapter = new categoryAdapter();

		categoryListView.setAdapter(adapter);
		// getCurrentIssusInfo();

	}

	@Override
	public void onResume() {
		getCurrentIssusInfo();
		super.onResume();
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
	 * 保存需要更新的控件
	 * 
	 * @deprecated
	 */
	// private List<View> needUpdate;

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

		// TODO 更新界面
		// 方式一：
		// adapter.notifyDataSetChanged();// 所有的item更新

		// 方式二：更新需要更新内容（没有必要刷新所有的信息）
		// 获取到需要更新控件的应用
		// TextView view = (TextView) needUpdate.get(0);
		// view.setText(text);

		// 方式三：不想维护needUpdate，如何获取需要更新的控件的引用
		// 将所有的item添加到ListView ，是不是有方式可以获取到ListView的孩子
		// categoryList.findViewById(R.id.ii_hall_lottery_summary);

		// tag The tag to search for, using "tag.equals(getTag())".
		// 简化：只更新第一个条目
		TextView textView=(TextView) categoryListView.findViewWithTag(0);
		if (textView!=null) {
			textView.setText(text);
		}

	}

	// 资源信息
	private int[] logoResIds = new int[] { R.drawable.id_ssq, R.drawable.id_3d,
			R.drawable.id_qlc };
	private int[] titleResIds = new int[] { R.string.is_hall_ssq_title,
			R.string.is_hall_3d_title, R.string.is_hall_qlc_title };

	private class categoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// 简化假设只有3个item
			return 3;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			if (convertView==null) {
				holder=new ViewHolder();
				convertView=View.inflate(getContext(), R.layout.il_hall_lottery_item, null);
				
				holder.logo = (ImageView) convertView.findViewById(R.id.ii_hall_lottery_logo);
				holder.title = (TextView) convertView.findViewById(R.id.ii_hall_lottery_title);
				holder.summary = (TextView) convertView.findViewById(R.id.ii_hall_lottery_summary);
				// needUpdate.add(holder.summary);
				holder.bet = (ImageView) convertView.findViewById(R.id.ii_hall_lottery_bet);
				
				
				convertView.setTag(holder);
			} else {
				holder=(ViewHolder) convertView.getTag();
				
			}
			holder.logo.setImageResource(logoResIds[position]);
			holder.title.setText(titleResIds[position]);
			holder.summary.setTag(position);
			
			return convertView;
		}

		class ViewHolder {
			ImageView logo;
			TextView title;
			TextView summary;
			ImageView bet;
		}

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
