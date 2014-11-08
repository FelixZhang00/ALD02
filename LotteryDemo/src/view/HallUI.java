package view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.GlobalParams;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.style.BulletSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import view.manager.BaseUI;
import view.manager.MiddleManager;

/**
 * 购彩大厅界面<br>
 * 以viewpage的形式显示
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
	// 用于显示彩票列表的list
	private ListView categoryListView;
	private categoryAdapter adapter;

	private ViewPager viewPager;
	private PagerTabStrip strip;
	private ImageView underLine; // 选项卡下面的下划线
	private MyPagerAdapter pageAdapter;
	private List<View> pagers;

	private TextView fcTitle;// 福彩
	private TextView tcTitle;// 体彩
	private TextView gpcTitle;// 高频彩

	private Bundle ssqBundle;
	
	public HallUI(Context context) {
		super(context);
	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_HALL;
	}

	public void init() {
		showInMiddle = (LinearLayout) View.inflate(getContext(),
				R.layout.il_hall, null);
		// 从另外一个布局文件中加载
		categoryListView = (ListView) View.inflate(getContext(),
				R.layout.hall_lottery_list, null);

		categoryListView.setFadingEdgeLength(0);// 删除黑边（上下）
		adapter = new categoryAdapter();

		categoryListView.setAdapter(adapter);

		viewPager = (ViewPager) findViewById(R.id.viewpages);
		pagers = new ArrayList<View>();
		initPages();
		pageAdapter = new MyPagerAdapter();
		viewPager.setAdapter(pageAdapter);

		initScrip();
	}

	/**
	 * 初始化选项卡的下划线，把它放到选项卡的中间位置
	 */
	private void initScrip() {
		underLine = (ImageView) findViewById(R.id.ii_category_selector);

		fcTitle = (TextView) findViewById(R.id.ii_category_fc);
		tcTitle = (TextView) findViewById(R.id.ii_category_tc);
		gpcTitle = (TextView) findViewById(R.id.ii_category_gpc);
		fcTitle.setTextColor(Color.RED);
		// imagview的width设置成match_parent后，underLine.getWidth();就是父控件的width了，
		// 而不是图片的真实宽度
		// underLine.getWidth();
		Bitmap bitmap = BitmapFactory.decodeResource(getContext()
				.getResources(), R.drawable.id_category_selector);

		int offset = (GlobalParams.metrics.widthPixels / 3 - bitmap.getWidth()) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		underLine.setImageMatrix(matrix);

	}

	private void initPages() {
		pagers.add(categoryListView);
		TextView item = new TextView(getContext());
		item.setText("体彩");
		pagers.add(item);

		item = new TextView(getContext());
		item.setText("高频彩");
		pagers.add(item);

	}

	@Override
	public void onResume() {
		getCurrentIssusInfo();
		super.onResume();
	}

	/**
	 * 上一次选项卡的位置
	 */
	private int lastPosition = 0;

	@Override
	public void setListener() {
		fcTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(0);

			}
		});

		tcTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(1);

			}
		});

		gpcTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(2);

			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO 当page转变时，自定义的下划线图片也跟着过来

				// fromXDelta toXDelta:相对于图片初始位置需要增加的量
				int tempDis = GlobalParams.metrics.widthPixels / 3;
				TranslateAnimation anim = new TranslateAnimation(lastPosition
						* tempDis, position * tempDis, 0, 0);
				anim.setDuration(300);
				// 因为是动画，并不是真的把图片移走了。所以需要用下面的设置使图片停在终点位置。
				// 并且图片的真实位置不会因为动画而改变
				anim.setFillAfter(true);
				underLine.startAnimation(anim);
				lastPosition = position;

				// 选中的page对应的选项卡为红色
				fcTitle.setTextColor(Color.BLACK);
				tcTitle.setTextColor(Color.BLACK);
				gpcTitle.setTextColor(Color.BLACK);

				switch (position) {
				case 0:
					fcTitle.setTextColor(Color.RED);
					break;
				case 1:
					tcTitle.setTextColor(Color.RED);
					break;
				case 2:
					gpcTitle.setTextColor(Color.RED);
					break;
				}

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});
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
		TextView textView = (TextView) categoryListView.findViewWithTag(0);
		if (textView != null) {
			textView.setText(text);
		}

		// 向BaseUI设置bundle信息
		 ssqBundle = new Bundle();
		ssqBundle.putString("ssqissue", issue);
		
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getContext(),
						R.layout.il_hall_lottery_item, null);

				holder.logo = (ImageView) convertView
						.findViewById(R.id.ii_hall_lottery_logo);
				holder.title = (TextView) convertView
						.findViewById(R.id.ii_hall_lottery_title);
				holder.summary = (TextView) convertView
						.findViewById(R.id.ii_hall_lottery_summary);
				// needUpdate.add(holder.summary);
				holder.bet = (ImageView) convertView
						.findViewById(R.id.ii_hall_lottery_bet);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			holder.logo.setImageResource(logoResIds[position]);
			holder.title.setText(titleResIds[position]);
			holder.summary.setTag(position);
			holder.bet.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (position == 0) {
						MiddleManager.getInstance().changeUI(PlaySSQ.class,ssqBundle);
					}
				}
			});
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

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagers.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(pagers.get(position));
			// super.destroyItem(container, position, object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = pagers.get(position);
			container.addView(view);
			return view;
			// return super.instantiateItem(container, position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

}
