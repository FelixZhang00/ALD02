package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.bean.ShoppingCart;
import jamffy.example.lotterydemo.bean.Ticket;
import jamffy.example.lotterydemo.engine.CommInfoEngine;
import jamffy.example.lotterydemo.net.NetUtils;
import jamffy.example.lotterydemo.net.protocal.Element;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.Oelement;
import jamffy.example.lotterydemo.net.protocal.element.CurrentIssueElement;
import jamffy.example.lotterydemo.util.BeanFactory;
import jamffy.example.lotterydemo.util.PromptManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import view.adapter.PoolAdapter;
import view.custom.MyGridView;
import view.custom.MyGridView.OnActionUpListener;
import view.manager.BaseUI;
import view.manager.FooterManger;
import view.manager.MiddleManager;
import view.manager.PlayGame;
import view.manager.TitleManger;
import view.sensor.ShakeListener;

public class PlaySSQ extends BaseUI implements PlayGame {

	// 机选
	private Button randomRed;
	private Button randomBlue;
	// 选号容器
	private MyGridView redContainer;
	private GridView blueContainer;

	private PoolAdapter redAdapter;
	private PoolAdapter blueAdapter;

	// 被选中的号码集合
	private List<Integer> redList;
	private List<Integer> blueList;

	/**
	 * 传感器管理工具
	 */
	private SensorManager manager;
	private ShakeListener listener;

	public PlaySSQ(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (ViewGroup) View.inflate(getContext(),
				R.layout.il_playssq, null);

		redContainer = (MyGridView) findViewById(R.id.ii_ssq_red_number_container);
		blueContainer = (GridView) findViewById(R.id.ii_ssq_blue_number_container);
		randomRed = (Button) findViewById(R.id.ii_ssq_random_red);
		randomBlue = (Button) findViewById(R.id.ii_ssq_random_blue);

		redList = new ArrayList<Integer>();
		blueList = new ArrayList<Integer>();

		redAdapter = new PoolAdapter(getContext(), ConstantValues.RED_POOL_NUM,
				redList, R.drawable.id_redball);
		blueAdapter = new PoolAdapter(getContext(),
				ConstantValues.BLUE_POOL_NUM, blueList, R.drawable.id_blueball);
		redContainer.setAdapter(redAdapter);
		blueContainer.setAdapter(blueAdapter);

		manager = (SensorManager) getContext().getSystemService(
				Context.SENSOR_SERVICE);
	}

	@Override
	public void setListener() {
		blueContainer.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!blueList.contains(position + 1)) {
					view.setBackgroundResource(R.drawable.id_blueball);
					view.startAnimation(AnimationUtils.loadAnimation(
							getContext(), R.anim.ball_shake));
					blueList.add(position + 1);
				} else {
					view.setBackgroundResource(R.drawable.id_defalut_ball);
					// 数组越界异常。list.size才2，而我选中的球号是6
					// blueList.remove(position + 1);
					// 需要强转，把选中的号作为对象传入
					blueList.remove((Object) (position + 1));
				}
				changeBottomNotice();
			}

		});

		// 当手机抬起时，为选号球设置颜色，并添加到list中
		redContainer.setOnActionUpListener(new OnActionUpListener() {

			@Override
			public void onActionUp(TextView child, int position) {
				if (!redList.contains(position + 1)) {
					child.setBackgroundResource(R.drawable.id_redball);
					redList.add(position + 1);
				} else {
					child.setBackgroundResource(R.drawable.id_defalut_ball);
					redList.remove((Object) (position + 1));
				}
				changeBottomNotice();
			}
		});

		randomRed.setOnClickListener(this);
		randomBlue.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ii_ssq_random_blue:
			randomBlue();
			break;
		case R.id.ii_ssq_random_red:
			randomRed();
			break;
		default:
			break;
		}
		changeBottomNotice();
		super.onClick(v);
	}

	private void randomRed() {
		redList.clear();
		// 产生1~33 6个随机数
		Random random = new Random();
		while (redList.size() < ConstantValues.SSQ_EACH_RED) {
			int num = random.nextInt(ConstantValues.RED_POOL_NUM) + 1;
			if (redList.contains(num)) {
				continue;
			}
			redList.add(num);
		}
		redAdapter.notifyDataSetChanged();
	}

	private void randomBlue() {
		blueList.clear();
		// 产生1~16 1个随机数
		Random random = new Random();
		while (blueList.size() < ConstantValues.SSQ_EACH_BLUE) {
			int num = random.nextInt(ConstantValues.BLUE_POOL_NUM) + 1;
			blueList.add(num);
		}
		// 让对应的球变色
		blueAdapter.notifyDataSetChanged();
	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_SSQ;
	}

	@Override
	public void onResume() {

		listener = new ShakeListener(getContext()) {

			@Override
			public void doAfterShake() {
				randomBlue();
				randomRed();
				changeBottomNotice();
			}
		};

		// 将传感器工具注册成 加速器
		manager.registerListener(listener,
				manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		changTitleContent();
		super.onResume();
	}

	/**
	 * 修改双色球界面的标题内容
	 */
	private void changTitleContent() {
		Bundle bundle = getBundle();
		String text = "双色球选号";
		if (bundle != null) {
			if (bundle.get("ssqissue") != null) {
				text = "双色球第" + bundle.get("ssqissue") + "期";
			}
		}
		TitleManger.getInstance().changTitleContent(text);

	}

	@Override
	public void onPause() {
		clear();
		manager.unregisterListener(listener);
		super.onPause();
	}

	/**
	 * 修改底部导航的信息
	 */
	private void changeBottomNotice() {
		String text = getContext().getString(R.string.is_need_to_chose);
		String redball = getContext().getString(R.string.is_ball_red);
		String blueball = getContext().getString(R.string.is_ball_blue);
		if (redList.size() < ConstantValues.SSQ_EACH_RED) {
			text = StringUtils.replace(text, "NUM", ""
					+ (ConstantValues.SSQ_EACH_RED - redList.size()))
					+ redball;
		} else {
			if (blueList.size() < ConstantValues.SSQ_EACH_BLUE) {
				text = StringUtils.replace(text, "NUM", ""
						+ (ConstantValues.SSQ_EACH_BLUE - blueList.size()))
						+ blueball;
			} else {
				// 显示投注信息
				text = getContext().getString(R.string.is_play_bottom_title);
				int stakeNum = calStakeNum();
				text = StringUtils.replaceEach(text, new String[] { "NUM",
						"MONEY" }, new String[] { stakeNum + "",
						"" + stakeNum * 2 });
			}
		}

		FooterManger.getInstance().changeGameBottomNotice(text);
	}

	/**
	 * 计算彩票的注数
	 */
	private int calStakeNum() {
		int redNum = (int) (factorial(redList.size()) / ((factorial(ConstantValues.SSQ_EACH_RED)) * (factorial(redList
				.size() - ConstantValues.SSQ_EACH_RED))));
		int blueNum = (int) (factorial(blueList.size()) / ((factorial(ConstantValues.SSQ_EACH_BLUE)) * (factorial(blueList
				.size() - ConstantValues.SSQ_EACH_BLUE))));
		return redNum * blueNum;
	}

	/**
	 * 阶乘
	 * 
	 * @param num
	 * @return
	 */
	private long factorial(int num) {
		if (num > 1) {
			return num * factorial(num - 1);
		} else if (num == 1 || num == 0) {
			return 1;
		} else {
			// 抛出参数异常
			throw new IllegalArgumentException("num must >=0");
		}
	}

	/**
	 * 清除资源（list变量等）
	 */
	@Override
	public void clear() {
		redList.clear();
		blueList.clear();
		changeBottomNotice();
		changTitleContent();
		redAdapter.notifyDataSetChanged();
		blueAdapter.notifyDataSetChanged();
	}

	@Override
	public void done() {
		// ①判断：用户是否选择了一注投注
		if (isAtLeastSelectOne()) {
			// 一个购物车中，只能放置一个彩种，当前期的投注信息
			// ②判断：是否获取到了当前销售期的信息
			if (getBundle() != null && getBundle().get("ssqissue") != null) {
				// ③封装用户的投注信息：红球、蓝球、注数
				Ticket ticket = new Ticket();
				StringBuffer redbuffer = new StringBuffer();
				for (Integer item : redList) {
					redbuffer.append(" ").append(item.toString());
				}
				ticket.setRedNum(redbuffer.substring(1));

				StringBuffer bluebuffer = new StringBuffer();
				for (Integer item : blueList) {
					bluebuffer.append(" ").append(item.toString());
				}
				ticket.setBlueNum(bluebuffer.substring(1));

				ticket.setNum(calStakeNum());
				// ④创建彩票购物车，将投注信息添加到购物车中
				ShoppingCart.getInstance().getTickets().add(ticket);

				// ⑤设置彩种的标示，设置彩种期次
				ShoppingCart.getInstance().setLotteryid(ConstantValues.SSQ);
				ShoppingCart.getInstance().setIssue(
						getBundle().getString("ssqissue"));
				// ⑥界面跳转——购物车展示
				MiddleManager.getInstance().changeUI(ShoppingUI.class,
						getBundle());
			} else {
				// 重新获取
				getCurrentIssusInfo();
			}

		} else {
			// 提示：需要选择一注
			PromptManager.showToast(getContext(), "需要选择一注");
		}

	}

	/**
	 * 判断用户是否至少选了一注
	 * 
	 * @return
	 */
	private boolean isAtLeastSelectOne() {
		if (redList.size() >= ConstantValues.SSQ_EACH_RED
				&& blueList.size() >= ConstantValues.SSQ_EACH_BLUE) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前销售期信息
	 */
	private void getCurrentIssusInfo() {
		new MyHttpTask<Integer>() {

			@Override
			protected void onPreExecute() {
				if (NetUtils.checkNet(getContext())) {
					PromptManager.showProgressDialog(getContext());
				} else {
					PromptManager.showNoNetWork(getContext());
				}
				super.onPreExecute();
			}

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
				PromptManager.closeProgressDialog();
				if (result != null) {
					Oelement oelement = result.getBody().getOelement();
					if (ConstantValues.SUCCESS.equals(oelement.getErrorcode())) {

						CurrentIssueElement element = (CurrentIssueElement) result
								.getBody().getElements().get(0);
						String issue = element.getIssue();

						// 向BaseUI设置bundle信息
						Bundle bundle = new Bundle();
						bundle.putString("ssqissue", issue);
						setBundle(bundle);
						changTitleContent();
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

}
