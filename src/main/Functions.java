package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
	// An array with 100 items (String[99]
	private static final String[] CONSTANT_ARRAY = {
			// The alphabet (Uppercase) 0 - 25
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y",
			"Z",
			// The alphabet (Lowercase) 26 - 51
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
			"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
			"z",
			// The numbers 52 - 61
			"0", "1", "2", "3", "4", "5", "6",
			"7",
			"8",
			"9",
			// The other characters in ASCII minus forward slash (in order of
			// their appearance) 62 - 92
			" ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",",
			"-", ".", "/", ":", ";", "<", "=", ">", "?", "@", "[", "]", "^",
			"_", "`", "{", "|", "~",
			// 7 other added characters 93 - 99
			"²", "Å", "³", "É", "Á", "¹", "À" };

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

	public static String Post(ArrayList<NameValuePair> postParameters,
			String URL) {
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

	// GETs, but returns the Response instead
	public static HttpResponse GetResponse(ArrayList<NameValuePair> headerList,
			String URL) {
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

	public static String getTime(DateFormat df) {
		Date now = Calendar.getInstance().getTime();
		return df.format(now);
	}

	public static String encrypt(String plainText, BigInteger exp,
			BigInteger mod) {
		System.out.println("________________________");
		String p = "";
		for (char c : plainText.toCharArray()) {
			int i = getLoc(String.valueOf(c));
			if (i < 10)
				p += "0";
			p += i;
		}
		System.out.println("Encrypting: " + plainText + " (" + p + ")");
		BigInteger c = new BigInteger(p).modPow(exp, mod);
		System.out.println(plainText + " --> " + c);
		return c.toString();
	}

	public static String decrypt(String cipherText, BigInteger exp,
			BigInteger mod) {
		System.out.println("________________________");
		System.out.println("Decrypting: " + cipherText);
		String str = new BigInteger(cipherText).modPow(exp, mod).toString();
		// If it's of an odd length, append a 0 to the number
		if (str.length() % 2 == 1) {
			str = "0" + str;
		}
		// Splits the string into digraphs, then converts each of them back into
		// their original strings
		String c = "";
		for (int j = 0; j < str.length(); j += 2) {
			c += CONSTANT_ARRAY[Integer.parseInt(str.substring(j, j + 2))];
		}
		System.out.println(cipherText + " --> " + c);
		return c;
	}

	// Returns the location of the given letter (0-25)
	public static int getLoc(String s) {
		int index = 0;
		for (String str : CONSTANT_ARRAY) {
			if (str.equals(s)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	// This is to be used as a replacement to String.split("") which has a
	// whitespace at [0]
	public static String[] split(String s) {
		char[] c = s.toCharArray();
		String[] str = new String[s.length()];

		for (int i = 0; i < s.length(); i++) {
			str[i] = String.valueOf(c[i]);
		}
		return str;
	}

	// Fixes illegal characters (only lets alphabetical letters through)
	public static String clearIllegal(String s) {
		String str = "";
		for (char c : s.toCharArray()) {
			if (getLoc(String.valueOf(c)) != -1) {
				str += String.valueOf(c);
			}
		}
		return str;
	}

}
