package jamffy.example.lotterydemo;

import view.manager.TitleManger;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		TitleManger titleManger = TitleManger.getInstance();
		titleManger.init(this);

	}

}
