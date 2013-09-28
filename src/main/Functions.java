package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Functions {


	public static String Get(ArrayList<NameValuePair> headerList, String URL) {
		HttpClient hc = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(URL);
		
		headerList.add(new BasicNameValuePair("Client", "true"));

		for (NameValuePair nvp : headerList) {
			httpGet.setHeader(nvp.getName(), nvp.getValue());
		}

		try {
			HttpResponse response = hc.execute(httpGet);
			String res = EntityUtils.toString(response.getEntity());
			return res;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String Post(ArrayList<NameValuePair> postParameters, String URL) {
		HttpClient hc = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(URL);

		postParameters.add(new BasicNameValuePair("Client", "true"));
		
		HttpResponse response = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
			response = hc.execute(httpPost);
			String res = EntityUtils.toString(response.getEntity());
			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	//GETs, but returns the Response instead
	public static HttpResponse GetResponse(ArrayList<NameValuePair> headerList, String URL) {
		HttpClient hc = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(URL);
		
		headerList.add(new BasicNameValuePair("Client", "true"));

		for (NameValuePair nvp : headerList) {
			httpGet.setHeader(nvp.getName(), nvp.getValue());
		}

		try {
			HttpResponse response = hc.execute(httpGet);
			return response;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getTime(DateFormat df){
		Date now = Calendar.getInstance().getTime();
		return df.format(now);
	}
}
