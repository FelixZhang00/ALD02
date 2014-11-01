package jamffy.example.lotterydemo.test;

import jamffy.example.lotterydemo.net.NetUtils;
import android.test.AndroidTestCase;

public class NetTest extends AndroidTestCase {

	public  void testNetType(){
		NetUtils.checkNet(getContext());
	} 
}
