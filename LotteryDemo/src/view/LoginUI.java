package view;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.GlobalParams;
import jamffy.example.lotterydemo.R;
import jamffy.example.lotterydemo.bean.User;
import jamffy.example.lotterydemo.engine.CommInfoEngine;
import jamffy.example.lotterydemo.engine.UserEngine;
import jamffy.example.lotterydemo.net.NetUtils;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.Oelement;
import jamffy.example.lotterydemo.net.protocal.element.BalanceElement;
import jamffy.example.lotterydemo.net.protocal.element.CurrentIssueElement;
import jamffy.example.lotterydemo.util.BeanFactory;
import jamffy.example.lotterydemo.util.PromptManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import view.manager.BaseUI;
import view.manager.MiddleManager;

public class LoginUI extends BaseUI {

	private EditText username;
	private ImageView clear;// 清空用户名
	private EditText password;
	private Button login;
	// 简单地存放用户名密码
	private SharedPreferences sp;

	public LoginUI(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (ViewGroup) View.inflate(getContext(),
				R.layout.il_user_login, null);
		sp = getContext()
				.getSharedPreferences("userinfo", Context.MODE_PRIVATE);

		username = (EditText) findViewById(R.id.ii_user_login_username);
		clear = (ImageView) findViewById(R.id.ii_clear);
		password = (EditText) findViewById(R.id.ii_user_login_password);
		login = (Button) findViewById(R.id.ii_user_login);

		if (username.getText() != null) {
			clear.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_LOGIN;
	}

	@Override
	public void setListener() {
		clear.setOnClickListener(this);
		login.setOnClickListener(this);

		username.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (username.getText().length() > 0) {
					clear.setVisibility(View.VISIBLE);
				} else {
					clear.setVisibility(View.INVISIBLE);
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ii_clear:
			// 清除用户名
			username.setText("");
			clear.setVisibility(View.INVISIBLE);
			break;
		case R.id.ii_user_login:
			login();
			break;
		}
		super.onClick(v);
	}

	/**
	 * 处理用户登录的相关逻辑
	 */
	private void login() {
		User user = new User();
		user.setUesrname(username.getText().toString());
		user.setPassword(password.getText().toString());

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

			/**
			 * 获取xml数据
			 */
			@Override
			protected Message doInBackground(User... params) {
				UserEngine engine = BeanFactory.getImp(UserEngine.class);
				Message loginMsg = engine.login(params[0]);

				// 检查是否登录成功
				if (loginMsg != null) {
					Oelement oelement = loginMsg.getBody().getOelement();
					if (ConstantValues.SUCCESS.equals(oelement.getErrorcode())) {
						// 登录成功了
						GlobalParams.isLogined = true;
						GlobalParams.USER_NAME = params[0].getUesrname();
						// 获取用户余额
						Message balanceMsg = engine.getBalance(params[0]);
						if (balanceMsg != null) {
							// 检查状态码
							oelement = balanceMsg.getBody().getOelement();
							if (ConstantValues.SUCCESS.equals(oelement
									.getErrorcode())) {
								BalanceElement be = (BalanceElement) balanceMsg
										.getBody().getElements().get(0);
								GlobalParams.USER_BALANCE = Float.parseFloat(be
										.getInvestvalues().toString());
								return balanceMsg;
							}

						}
					}
				}
				return null;
			}

			/**
			 * 判断获取的数据
			 */
			@Override
			protected void onPostExecute(Message result) {
				PromptManager.closeProgressDialog();
				if (result != null) {
					PromptManager.showToast(getContext(), "登录成功");
					MiddleManager.getInstance().goBack();
				} else {
					PromptManager.showToast(getContext(), "服务器忙，请稍后再试...");
				}
				super.onPostExecute(result);
			}

		}.executeProxy(user);
	}

}
