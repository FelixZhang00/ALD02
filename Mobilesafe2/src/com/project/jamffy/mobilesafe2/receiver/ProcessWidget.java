package com.project.jamffy.mobilesafe2.receiver;

import com.project.jamffy.mobilesafe2.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class ProcessWidget extends AppWidgetProvider {

	private final static String TAG = "ProcessWidget";
	private Intent intent;

	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);
		Logger.i(TAG, "onReceive");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Logger.i(TAG, "onEnabled");
		intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Logger.i(TAG, "onDisabled");
		intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);

	}
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		Logger.i(TAG, "onUpdate");

	}
	
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Logger.i(TAG, "onUpdate");
	}





}
