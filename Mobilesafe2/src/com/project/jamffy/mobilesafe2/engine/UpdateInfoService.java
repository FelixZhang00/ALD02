package com.project.jamffy.mobilesafe2.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import com.project.jamffy.mobilesafe2.utils.Logger;


import com.project.jamffy.mobilesafe2.domain.UpdateInfo;

public class UpdateInfoService {
	private Context context;
	private String TAG="UpdateInfoService";
	public UpdateInfoService(Context context) {
		this.context = context;
	}


	/**
	 * @param urlid 服务器路径string对应的id
	 *            
	 * @return 更新信息
	 * @throws Exception 
	 */
	public UpdateInfo getUpdateInfo(int urlid) throws Exception {
		String path= context.getResources().getString(urlid);
		Logger.i(TAG, path);
		URL url=new URL(path);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");
		InputStream is=conn.getInputStream();
		return UpdateInfoParser.getUpdateInfo(is);
	}
}
