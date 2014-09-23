package com.project.jamffy.mobilesafe2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.WindowManager;

public class ImageUtil {

	/**
	 * 返回一个宽度和高度都为48个像素的bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap getResizeBitmap(Context context,
			BitmapDrawable drawable) {
		Bitmap bitmap = drawable.getBitmap();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int height = display.getHeight();
		int width = display.getWidth();
		if (height < 480 || width < 320) {
			return bitmap.createScaledBitmap(bitmap, 32, 32, false);
		} else {
			return bitmap.createScaledBitmap(bitmap, 48, 48, false);
		}

	}
}
