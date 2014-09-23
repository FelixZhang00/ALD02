package com.project.jamffy.mobilesafe2.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.SmsInfo;
import com.project.jamffy.mobilesafe2.engine.SmsInfoEngine;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.util.Xml;
import android.widget.Toast;

public class BackupSmsService extends Service {
	protected static final String TAG = "BackupSmsService";
	private SmsInfoEngine smsInfoEngine;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		smsInfoEngine = new SmsInfoEngine(this);
		new Thread() {
			@Override
			public void run() {
				super.run();
				List<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
				smsInfos = smsInfoEngine.getAllSms();
				//获取文件地址
				String path = getResources().getString(R.string.SMSBackup);
				String filepath = Environment.getExternalStorageDirectory()
						.getPath() + path;
				Logger.i(TAG, filepath); // 上面的方法能正确的获取path
				//先要创建文件夹
				File file=new File(filepath);
				if (!file.exists()) {
					file.mkdirs();
				}
				//有了文件夹，就可以指定里面的文件了，没有会自动创建
				File file2=new File(file, "/message.xml");
				
				XmlSerializer serializer = Xml.newSerializer(); // 创建一个序列化器
				try {
					FileOutputStream os = new FileOutputStream(file2);
					serializer.setOutput(os, "utf-8");
					serializer.startDocument("utf-8", true);
					serializer.startTag(null, "smss");
					serializer.startTag(null, "count");
					serializer.text(smsInfos.size() + "");
					serializer.endTag(null, "count");
					for (SmsInfo smsInfo : smsInfos) {
						serializer.startTag(null, "sms");
						serializer.startTag(null, "id");
						serializer.text(smsInfo.getId() + "");
						serializer.endTag(null, "id");

						serializer.startTag(null, "address");
						serializer.text(smsInfo.getAddress() + "");
						serializer.endTag(null, "address");

						serializer.startTag(null, "body");
						serializer.text(smsInfo.getBody() + "");
						serializer.endTag(null, "body");

						serializer.startTag(null, "read");
						serializer.text(smsInfo.getRead() + "");
						serializer.endTag(null, "read");

						serializer.startTag(null, "type");
						serializer.text(smsInfo.getType() + "");
						serializer.endTag(null, "type");

						serializer.endTag(null, "sms");
					}
					serializer.endTag(null, "smss");
					serializer.endDocument();
					os.flush(); // 把文件缓存区的数据写出来
					os.close();
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "备份成功", 0).show();
					Looper.loop();
				} catch (Exception e) {
					e.printStackTrace();
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "备份失败", 0).show();
					Looper.loop();
				}
			}

		}.start();

	}
}
