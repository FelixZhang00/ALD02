package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.R;
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
import view.manager.TitleManger;
import view.sensor.ShakeListener;

public class PlaySSQ extends BaseUI {

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
		super.onClick(v);
	}

	private void randomRed() {
		redList.clear();
		Random random = new Random();
		while (redList.size() < 6) {
			int num = random.nextInt(33) + 1;
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
		while (blueList.size() < 1) {
			int num = random.nextInt(16) + 1;
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
	 * 清除资源（list变量等）
	 */
	private void clear() {
		redList.clear();
		blueList.clear();
		redAdapter.notifyDataSetChanged();
		blueAdapter.notifyDataSetChanged();
	}

}
