package com.project.jamffy.mobilesafe2.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.project.jamffy.mobilesafe2.MyApplication;
import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.TaskInfo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class PssDetailsActivity extends Activity {
	private TextView tv_appname;
	private TextView tv_packname;
	private ScrollView sv;
	private static final String TAG="PssDetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_details);

		tv_appname = (TextView) this.findViewById(R.id.tv_task_details_appname);
		tv_packname = (TextView) this
				.findViewById(R.id.tv_task_details_packname);
		sv = (ScrollView) this.findViewById(R.id.sv_task_details);

		// 目的：获取 AppSecurityPermissions类中的getPermissionsView()方法，
		// 返回显示某个app的权限的view

		MyApplication myApp = (MyApplication) getApplication();
		TaskInfo taskInfo = myApp.taskInfo;
		String packname = taskInfo.getPackname();
		tv_packname.setText(packname);
		tv_appname.setText(taskInfo.getAppname());
		Logger.i(TAG, packname);
		Logger.i(TAG, taskInfo.getAppname());
		try {

			// 通过类加载器 传入类名 获得字节码
			Class clazz = getClass().getClassLoader().loadClass(
					"android.widget.AppSecurityPermissions");

			// 获得构造方法 参数就是AppSecurityPermissions的构造方法的参数的类
			Constructor constructor = clazz.getConstructor(new Class[] {
					Context.class, String.class });

			// 实例化构造方法，即传入真正的参数
			Object object = constructor.newInstance(new Object[] { this,
					packname });

			// 获取方法 参数：方法名、参数的类（没有参数就为空）
			Method method = clazz.getDeclaredMethod("getPermissionsView",
					new Class[] {});

			// 调用方法； 参数 ：类的实例、方法的参数 ；返回值要看原来的类了
			View view = (View) method.invoke(object, new Object[] {});
			sv.addView(view);
		} catch (Exception e) {
			e.printStackTrace();
		}

		myApp.taskInfo = null;

	}
}
