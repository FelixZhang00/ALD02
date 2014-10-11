package jamffy.example.photodetective;

import android.app.Activity;
import android.media.ExifInterface;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			
			ExifInterface exif = new ExifInterface("sdcard/img.jpg");
			// System.out.println( exif.getAttribute("ImageDescription"));
			System.out.println(exif.getAttribute(ExifInterface.TAG_DATETIME));
			System.out.println(exif
					.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
			System.out
					.println(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));

			// exif.setAttribute("camera", "柯达相机");

			System.out.println(exif.getAttribute("camera"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("没有找到");
		}

	}

}
