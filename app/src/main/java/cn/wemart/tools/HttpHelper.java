package cn.wemart.tools;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.text.TextUtils;

public class HttpHelper {
	private HttpParams httpParams;
	private HttpClient httpClient;
	private String cookies;

	public HttpHelper() {
		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）

		this.httpParams = new BasicHttpParams();
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小

		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
		// 设置重定向，缺省为 true

		HttpClientParams.setRedirecting(httpParams, true);
		// 设置 user agent

		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);
		// 创建一个 HttpClient 实例

		// 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient

		// 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient

		this.httpClient = new DefaultHttpClient(httpParams);
	}

	public String executeGet(String url, List<NameValuePair> params)
			throws ClientProtocolException, IOException {
		String result = "";

		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				if (i > 0)
					url += "&";
				else {
					url += "?";
				}
				url += params.get(i).getName() + "="
						+ URLEncoder.encode(params.get(i).getValue(), "UTF-8");
			}
		}

		HttpGet httpRequest = new HttpGet(url);
		setRequestCookies(httpRequest);

		HttpResponse response = httpClient.execute(httpRequest);
		/* 若状态码为200 ok */
		if (response.getStatusLine().getStatusCode() == 200) {
			appendCookies(response);
			/* 读返回数据 */
			result = EntityUtils.toString(response.getEntity());
		}

		return result;
	}

	public String executePost(String url, List<NameValuePair> params)
			throws ClientProtocolException, IOException {
		String result = "";
		HttpPost httpRequest = new HttpPost(url);
		setRequestCookies(httpRequest);
		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpClient.execute(httpRequest);
		/* 若状态码为200 ok */
		if (response.getStatusLine().getStatusCode() == 200) {
			appendCookies(response);
			/* 读返回数据 */
			result = EntityUtils.toString(response.getEntity());
		}

		return result;
	}

	public String executePut(String url, List<NameValuePair> params)
			throws ClientProtocolException, IOException {
		String result = "";
		HttpPut httpRequest = new HttpPut(url);
		setRequestCookies(httpRequest);
		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpClient.execute(httpRequest);
		/* 若状态码为200 ok */
		if (response.getStatusLine().getStatusCode() == 200) {
			appendCookies(response);
			/* 读返回数据 */
			result = EntityUtils.toString(response.getEntity());
		}

		return result;
	}

	public String executeDelete(String url, JSONObject jobject)
			throws ClientProtocolException, IOException {
		String result = "";
		HttpDeleteWithBody httpRequest = new HttpDeleteWithBody(url);
		// httpRequest.

		setRequestCookies(httpRequest);
		httpRequest.addHeader("para", jobject.toString());
		// httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = httpClient.execute(httpRequest);
		/* 若状态码为200 ok */
		if (response.getStatusLine().getStatusCode() == 200) {
			appendCookies(response);
			/* 读返回数据 */
			result = EntityUtils.toString(response.getEntity());
		}

		return result;
	}

	/**
	 * 
	 * 设置请求的Cookie头信息
	 * 
	 * 
	 * 
	 * @param reqMsg
	 */
	private void setRequestCookies(HttpMessage reqMsg) {
		if (!TextUtils.isEmpty(cookies)) {
			reqMsg.setHeader("Cookie", cookies);
		}
	}

	/**
	 * 
	 * 把新的Cookie头信息附加到旧的Cookie后面 用于下次Http请求发送
	 * 
	 * 
	 * 
	 * @param resMsg
	 */
	private void appendCookies(HttpMessage resMsg) {
		Header setCookieHeader = resMsg.getFirstHeader("Set-Cookie");
		if (setCookieHeader != null
				&& TextUtils.isEmpty(setCookieHeader.getValue())) {
			String setCookie = setCookieHeader.getValue();
			if (TextUtils.isEmpty(cookies)) {
				cookies = setCookie;
			} else {
				cookies = cookies + "; " + setCookie;
			}
		}
	}

}
