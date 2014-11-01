package jamffy.example.lotterydemo.net;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.GlobalParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientUtil {
	private HttpClient httpClient;
	private HttpGet httpGet;
	private HttpPost httpPost;

	public HttpClientUtil() {
		httpClient = new DefaultHttpClient();
		// 判断是否需要设置代理信息
		if (StringUtils.isNotBlank(GlobalParams.PROXY_IP)) {
			// 设置代理信息
			HttpHost host = new HttpHost(GlobalParams.PROXY_IP,
					GlobalParams.PROXY_PORT);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					host);
		}
	}

	/**
	 * 向指定的url发送xml数据
	 * 
	 * @param uri
	 * @param xml
	 * @return InputStream
	 */
	public InputStream sendXml(String uri, String xml) {
		httpPost = new HttpPost(uri);

		StringEntity entity = null;
		try {
			entity = new StringEntity(xml, ConstantValues.ENCODING);
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);

			// 判断返回的状态码是否为成功的标志
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取响应中的实体部分
				return response.getEntity().getContent();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
}
