package view.manager.deprecated;

import jamffy.example.lotterydemo.ConstantValues;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import view.manager.BaseUI;

public class SecondUI extends BaseUI {

	private TextView textView;

	public SecondUI(Context context) {
		super(context);
		init();
	}

	/**
	 * 避免重复创建控件
	 */
	@Override
	public void init() {
		textView = new TextView(getContext());
		LayoutParams params = textView.getLayoutParams();
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		textView.setLayoutParams(params);
		textView.setBackgroundColor(Color.YELLOW);
		textView.setText("第二个界面");
	}

	@Override
	public View getChild() {
		return textView;

	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_SECOND;
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		
	}

}
