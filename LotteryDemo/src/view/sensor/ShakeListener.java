package view.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;

/**
 * 手机摇晃监听
 * 
 * @author tmac
 *
 */
public abstract class ShakeListener implements SensorEventListener {

	// ①记录第一个数据：三个轴的加速度，为了屏蔽掉不同手机采样的时间间隔，记录第一个点的时间
	// ②当有新的传感器数据传递后，判断时间间隔（用当前时间与第一个采样时间进行比对，如果满足了时间间隔要求，认为是合格的第二个点，否则舍弃该数据包）
	// 进行增量的计算：获取到新的加速度值与第一个点上存储的进行差值运算，获取到一点和二点之间的增量
	// ③以此类推，获取到相邻两个点的增量，一次汇总
	// ④通过汇总值与设定好的阈值比对，如果大于等于，用户摇晃手机，否则继续记录当前的数据（加速度值和时间）

	private Context context; 
	private Vibrator vibrator;
	
	public ShakeListener(Context context) {
		super();
		this.context = context;
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	private float lastX;
	private float lastY;
	private float lastZ;

	// 规定的时间间隔
	private long duration = 100;

	private long lasttime = 0;
	// 经过测试都出来的阈值
	private float threshold = 200;

	private float total;

	// 加速器每次状态该表都会调用此方法，SensorEvent中传递了位置信息等。
	// TODO 判断是否是人为的手机摇晃,如果是则做更新相关的界面等的操作。
	@Override
	public void onSensorChanged(SensorEvent event) {
		// 第一次，设置参数
		if (lasttime == 0) {
			lastX = event.values[0];
			lastY = event.values[1];
			lastZ = event.values[2];
			lasttime = System.currentTimeMillis();
		} else {
			if ((System.currentTimeMillis() - lasttime) > duration) {
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];

				// 屏蔽掉微小的增量

				float dx = Math.abs(x - lastX);
				float dy = Math.abs(y - lastY);
				float dz = Math.abs(z - lastZ);

				if (dx < 1) {
					dx = 0;
				}
				if (dy < 1) {
					dy = 0;
				}
				if (dz < 1) {
					dz = 0;
				}

				// 极个别的手机，静止某个轴的增量大于1,10以上100以上
				// if(dx==0||dy==0||dz==0)
				// {
				// init();
				// }

				// 一点和二点总增量
				float shake = dx + dy + dz;

				if (shake == 0) {
					init();
				}

				total += shake;

				if (total > threshold) {
					init();					
					// 更新相关的界面或者其他操作，有具体的实现类来决定
					doAfterShake();
					//  震动提示用户
					vibrator.vibrate(200);
				} else {
					lasttime = System.currentTimeMillis();
					lastX = x;
					lastY = y;
					lastZ = z;
				}

			}
		}

	}

	/**
	 * 初始化所有参数
	 */
	private void init() {
		total = 0;
		lasttime = 0;

		lastX = 0;
		lastY = 0;
		lastZ = 0;

	}

	/**
	 * 当判断是合理的人为震动时，让子类（或者匿名类）做相关的操作
	 */
	public abstract void doAfterShake();

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
