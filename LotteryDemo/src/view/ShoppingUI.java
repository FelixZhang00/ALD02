package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.GlobalParams;
import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.bean.ShoppingCart;
import jamffy.example.lotterydemo.bean.Ticket;
import jamffy.example.lotterydemo.util.PromptManager;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import view.manager.BaseUI;
import view.manager.MiddleManager;

/**
 * 购物车界面
 * 
 * @author tmac
 *
 */
public class ShoppingUI extends BaseUI {

	private Button addOptional;// 添加自选
	private Button addRandom;// 添加机选

	private ListView shoppingList;// 用户选择信息列表

	private ImageButton shoppingListClear;// 清空购物车
	private TextView notice;// 提示信息
	private Button buy;// 购买

	private ShoppingAdapter adapter;

	public ShoppingUI(Context context) {
		super(context);

	}

	@Override
	public void init() {
		showInMiddle = (ViewGroup) View.inflate(getContext(),
				R.layout.il_shopping, null);

		addOptional = (Button) findViewById(R.id.ii_add_optional);
		addRandom = (Button) findViewById(R.id.ii_add_random);
		shoppingListClear = (ImageButton) findViewById(R.id.ii_shopping_list_clear);
		notice = (TextView) findViewById(R.id.ii_shopping_lottery_notice);
		buy = (Button) findViewById(R.id.ii_lottery_shopping_buy);
		shoppingList = (ListView) findViewById(R.id.ii_shopping_list);

		adapter = new ShoppingAdapter();
		shoppingList.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		changeNotice();
		super.onResume();
	}

	@Override
	public void setListener() {
		addOptional.setOnClickListener(this);
		addRandom.setOnClickListener(this);
		shoppingListClear.setOnClickListener(this);
		buy.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ii_add_optional:
			// 添加自选
			MiddleManager.getInstance().goBack();
			break;
		case R.id.ii_add_random:
			// 添加机选
			addRandom();
			changeNotice();
			break;
		case R.id.ii_shopping_list_clear:
			ShoppingCart.getInstance().getTickets().clear();
			adapter.notifyDataSetChanged();
			changeNotice();
			break;
		case R.id.ii_lottery_shopping_buy:
			buy();
			break;

		}
		super.onClick(v);
	}

	/**
	 * 添加机选1注
	 */
	private void addRandom() {
		// 产生1~33 6个随机数
		Random random = new Random();
		List<Integer> redList = new ArrayList<Integer>();
		while (redList.size() < ConstantValues.SSQ_EACH_RED) {
			int num = random.nextInt(ConstantValues.RED_POOL_NUM) + 1;
			if (redList.contains(num)) {
				continue;
			}
			redList.add(num);
		}
		List<Integer> blueList = new ArrayList<Integer>();
		// 产生1~16 1个随机数
		while (blueList.size() < ConstantValues.SSQ_EACH_BLUE) {
			int num = random.nextInt(ConstantValues.BLUE_POOL_NUM) + 1;
			blueList.add(num);
		}
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

		ticket.setNum(1);
		ShoppingCart.getInstance().getTickets().add(ticket);
		adapter.notifyDataSetChanged();

	}

	private void changeNotice() {

		String text = getContext().getString(R.string.is_shopping_list_notice);
		Integer lotterynumber = ShoppingCart.getInstance().getLotterynumber();
		Integer lotteryvalue = ShoppingCart.getInstance().getLotteryvalue();
		text = StringUtils.replaceEach(
				text,
				new String[] { "NUM", "MONEY" },
				new String[] { lotterynumber.toString(),
						lotteryvalue.toString() });
		notice.setText(Html.fromHtml(text));
	}

	/**
	 * 点击 购买 按钮后处理的逻辑
	 */
	private void buy() {
		// ①检查是否够一注
		if (ShoppingCart.getInstance().getTickets().size() > 0) {
			// ②检查是否登录
			if (GlobalParams.isLogined) {
				// ③查看余额
				if (GlobalParams.USER_BALANCE >= ShoppingCart.getInstance()
						.getLotteryvalue()) {
					// ④跳转到追期和倍投的设置界面
					MiddleManager.getInstance().changeUI(PreBetUI.class,
							getBundle());
				} else {
					// 提示用户：充值去；界面跳转：用户充值界面
					PromptManager.showToast(getContext(), "充值去");
				}
			} else {
				// 进入登录界面
				PromptManager.showToast(getContext(), "请先登录");
				MiddleManager.getInstance()
						.changeUI(LoginUI.class, getBundle());
			}

		} else {
			PromptManager.showToast(getContext(), "至少选一注");
		}
		// ④
	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_SHOPPING;
	}

	private class ShoppingAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return ShoppingCart.getInstance().getTickets().size();
		}

		@Override
		public Object getItem(int position) {
			return ShoppingCart.getInstance().getTickets().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getContext(),
						R.layout.il_shopping_row, null);

				holder.delete = (ImageButton) convertView
						.findViewById(R.id.ii_shopping_item_delete);
				holder.redNum = (TextView) convertView
						.findViewById(R.id.ii_shopping_item_reds);
				holder.blueNum = (TextView) convertView
						.findViewById(R.id.ii_shopping_item_blues);
				holder.num = (TextView) convertView
						.findViewById(R.id.ii_shopping_item_money);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Ticket ticket = ShoppingCart.getInstance().getTickets()
					.get(position);
			holder.redNum.setText(ticket.getRedNum());
			holder.blueNum.setText(ticket.getBlueNum());
			holder.num.setText(ticket.getNum() + "注");

			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 添加删除的动画效果
					// convertView.startAnimation(AnimationUtils.loadAnimation(
					// getContext(), R.anim.item_left_delet));
					ShoppingCart.getInstance().getTickets().remove(position);
					notifyDataSetChanged();
					changeNotice();
				}
			});

			return convertView;
		}

		class ViewHolder {
			ImageButton delete;
			TextView redNum;
			TextView blueNum;
			TextView num;
		}

	}

}
