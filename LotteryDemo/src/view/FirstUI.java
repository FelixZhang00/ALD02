package view;

import jamffy.example.lotterydemo.ConstantValues;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import view.manager.BaseUI;

public class FirstUI extends BaseUI {

	public FirstUI(Context context) {
		super(context);
	}


	public View getChild() {
		TextView textView = new TextView(getContext());
		LayoutParams params = textView.getLayoutParams();
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		textView.setLayoutParams(params);
		textView.setBackgroundColor(Color.GREEN);
		textView.setText("第一个界面");
		return textView;

	}

	@Override
	public int getID() {
		return ConstantValues.VIEW_FIRST;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		
	}

}
