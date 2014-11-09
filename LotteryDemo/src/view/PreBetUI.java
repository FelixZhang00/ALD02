package view;

import org.apache.commons.lang3.StringUtils;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.GlobalParams;
import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.bean.ShoppingCart;
import jamffy.example.lotterydemo.bean.Ticket;
import jamffy.example.lotterydemo.bean.User;
import jamffy.example.lotterydemo.engine.UserEngine;
import jamffy.example.lotterydemo.net.NetUtils;
import jamffy.example.lotterydemo.net.protocal.Element;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.Oelement;
import jamffy.example.lotterydemo.net.protocal.element.BetElement;
import jamffy.example.lotterydemo.util.BeanFactory;
import jamffy.example.lotterydemo.util.PromptManager;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import view.manager.BaseUI;
import view.manager.MiddleManager;

/**
 * 倍数和追号界面
 * 
 * @author tmac
 *
 */
public class PreBetUI extends BaseUI {

	private TextView bettingNum;// 注数
	private TextView bettingMoney;// 金额

	private Button subAppnumbers;// 减少倍投
	private TextView appnumbersInfo;// 倍数
	private Button addAppnumbers;// 增加倍投

	private Button subIssueflagNum;// 减少追期
	private TextView issueflagNumInfo;// 追期
	private Button addIssueflagNum;// 增加追期

	private ImageButton lotteryPurchase;// 投注
	private ListView shoppingList;// 购物车展示

	private ShoppingAdapter adapter;

	public PreBetUI(Context context) {
		super(context);

	}

	@Override
	public void init() {
		showInMiddle = (ViewGroup) View.inflate(getContext(),
				R.layout.il_play_prefectbetting, null);

		bettingNum = (TextView) findViewById(R.id.ii_shopping_list_betting_num);
		bettingMoney = (TextView) findViewById(R.id.ii_shopping_list_betting_money);

		subAppnumbers = (Button) findViewById(R.id.ii_sub_appnumbers);
		appnumbersInfo = (TextView) findViewById(R.id.ii_appnumbers);
		addAppnumbers = (Button) findViewById(R.id.ii_add_appnumbers);

		subIssueflagNum = (Button) findViewById(R.id.ii_sub_issueflagNum);
		issueflagNumInfo = (TextView) findViewById(R.id.ii_issueflagNum);
		addIssueflagNum = (Button) findViewById(R.id.ii_add_issueflagNum);

		lotteryPurchase = (ImageButton) findViewById(R.id.ii_lottery_purchase);
		shoppingList = (ListView) findViewById(R.id.ii_lottery_shopping_list);

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
		// 倍数
		addAppnumbers.setOnClickListener(this);
		subAppnumbers.setOnClickListener(this);
		// 追期
		addIssueflagNum.setOnClickListener(this);
		subIssueflagNum.setOnClickListener(this);
		// 投注
		lotteryPurchase.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ii_add_appnumbers:
			// 增加倍数
			boolean result = ShoppingCart.getInstance().addAppnumbers(true);
			if (result) {
				changeNotice();
			} else {
				if (GlobalParams.USER_BALANCE < ShoppingCart.getInstance()
						.getLotteryvalue()) {
					PromptManager.showToast(getContext(), "超出余额");
				}
			}
			break;
		case R.id.ii_sub_appnumbers:
			// 减少倍数
			if (ShoppingCart.getInstance().addAppnumbers(false)) {
				changeNotice();
			}
			break;
		case R.id.ii_add_issueflagNum:
			// 增加追期
			if (ShoppingCart.getInstance().addIssuesnumbers(true)) {
				changeNotice();
			} else {
				if (GlobalParams.USER_BALANCE < ShoppingCart.getInstance()
						.getLotteryvalue()) {
					PromptManager.showToast(getContext(), "超出余额");
				}
			}
			break;
		case R.id.ii_sub_issueflagNum:
			// 减少追期
			if (ShoppingCart.getInstance().addIssuesnumbers(false)) {
				changeNotice();
			}
			break;
		case R.id.ii_lottery_purchase:
			if (GlobalParams.USER_BALANCE < ShoppingCart.getInstance()
					.getLotteryvalue()) {
				PromptManager.showToast(getContext(), "超出余额");
			} else {
				// 投注请求
				User user = new User();
				new MyHttpTask<User>() {

					@Override
					protected void onPreExecute() {
						if (NetUtils.checkNet(getContext())) {
							PromptManager.showProgressDialog(getContext());
						} else {
							PromptManager.showNoNetWork(getContext());
						}
						super.onPreExecute();
					}

					@Override
					protected Message doInBackground(User... params) {
						UserEngine engine = BeanFactory
								.getImp(UserEngine.class);
						return engine.bet(params[0]);
					}

					/**
					 * 判断获取的数据
					 */
					@Override
					protected void onPostExecute(Message result) {
						PromptManager.closeProgressDialog();
						if (result != null) {
							Oelement oelement = result.getBody().getOelement();
							if (ConstantValues.SUCCESS.equals(oelement
									.getErrorcode())) {
								BetElement element = (BetElement) result
										.getBody().getElements().get(0);
								GlobalParams.USER_BALANCE = Float
										.parseFloat(element.getActvalue());

								MiddleManager.getInstance().clear();
								MiddleManager.getInstance().changeUI(
										HallUI.class);
								ShoppingCart.getInstance().clear();
								PromptManager.showErrorDialog(getContext(),
										"投注成功！");
							} else {
								PromptManager.showToast(getContext(),
										oelement.getErrormsg());
							}
							MiddleManager.getInstance().goBack();
						} else {
							PromptManager.showToast(getContext(),
									"服务器忙，请稍后再试...");
						}
						super.onPostExecute(result);
					}

				}.executeProxy(user);
			}
			break;

		}
		super.onClick(v);
	}

	private void changeNotice() {
		String betNum = getContext().getString(
				R.string.is_shopping_list_betting_num);
		String betMon = getContext().getString(
				R.string.is_shopping_list_betting_money);
		Integer lotterynumber = ShoppingCart.getInstance().getLotterynumber();
		Integer lotteryvalue = ShoppingCart.getInstance().getLotteryvalue();

		betNum = StringUtils.replace(betNum, "NUM", lotterynumber.toString());
		betMon = StringUtils.replaceEach(betMon, new String[] { "MONEY1",
				"MONEY2" }, new String[] { lotteryvalue.toString(),
				GlobalParams.USER_BALANCE.toString() });
		bettingNum.setText(Html.fromHtml(betNum));
		bettingMoney.setText(Html.fromHtml(betMon));

		appnumbersInfo.setText("" + ShoppingCart.getInstance().getAppnumbers());
		issueflagNumInfo.setText(""
				+ ShoppingCart.getInstance().getIssuesnumbers());
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return ConstantValues.VIEW_PREBET;
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
						R.layout.il_play_prefectbetting_row, null);

				holder.redNum = (TextView) convertView
						.findViewById(R.id.ii_shopping_item_reds);
				holder.blueNum = (TextView) convertView
						.findViewById(R.id.ii_shopping_item_blues);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Ticket ticket = ShoppingCart.getInstance().getTickets()
					.get(position);
			holder.redNum.setText(ticket.getRedNum());
			holder.blueNum.setText(ticket.getBlueNum());
			return convertView;
		}

		class ViewHolder {
			TextView redNum;
			TextView blueNum;
		}

	}

}
