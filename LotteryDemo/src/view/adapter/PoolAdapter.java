package view.adapter;

import java.text.DecimalFormat;
import java.util.List;

import jamffy.example.lotterydemo.R;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 选号填充器
 * 
 * @author tmac
 *
 */
public class PoolAdapter extends BaseAdapter {

	private Context context;

	// 选号球的个数
	private int numPool;

	/**
	 * 被选中球的的列表
	 */
	private List<Integer> selectNums;

	/**
	 * 设置球的图片资源id
	 */
	private int ballSelectResId;

	public PoolAdapter(Context context, int numPool, List<Integer> selectNums,
			int ballSelectResId) {
		super();
		this.context = context;
		this.numPool = numPool;
		this.selectNums = selectNums;
		this.ballSelectResId = ballSelectResId;
	}

	public PoolAdapter(Context context, int numPool) {
		super();
		this.context = context;
		this.numPool = numPool;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return numPool;
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
		TextView ball = new TextView(context);
		DecimalFormat format = new DecimalFormat("00");
		ball.setText(format.format(position + 1));
		ball.setGravity(Gravity.CENTER);
		if (selectNums.contains(position+1)) {
			ball.setBackgroundResource(ballSelectResId);
		}else{
			ball.setBackgroundResource(R.drawable.id_defalut_ball);			
		}
		return ball;
	}

}
