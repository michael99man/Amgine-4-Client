package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
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
	private static final String[] ALPHA_ARRAY = { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z" };

	private static final CharsetEncoder asciiEncoder = Charset.forName(
			"US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1

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

	public static String encrypt(String plainText, int[] keyList) {
		System.out.println("Encrypting: " + plainText);
		int[] plaintext_ints = getIntArray(plainText.getBytes());

		int i = 0;
		byte[] ciphertext_bytes = new byte[plainText.length()];
		for (int p : plaintext_ints) {
			// Working in a mod 95 system, so subtract 32 from these values
			p += keyList[i] - 32;

			// ONLY USE STRING SUPPORTED CHARACTERS 32 (space) - 126 (~) ---
			// (mod 95)
			while (p >= 95) {
				p -= 95;
			}

			while (p < 0) {
				p += 95;
			}

			// Add 32 here
			ciphertext_bytes[i] = (byte) (p + 32);
			i++;
		}
		try {
			return new String(ciphertext_bytes, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;

		/**
		 * String[] strArray = split(tempMessage);
		 * 
		 * String cipherText = "";
		 * 
		 * for (int i = 0; i < strArray.length; i++) { int index =
		 * getLoc(strArray[i]) + keyList[i]; System.out.println(index); while
		 * (index > 25) { index -= 26; } cipherText += ALPHA_ARRAY[index]; }
		 **/
	}

	private static int[] getIntArray(byte[] bytes) {
		int[] tempArray = new int[bytes.length];
		for (int i = 0; i < tempArray.length; tempArray[i] = bytes[i++])
			;
		return tempArray;
	}

	public static String decrypt(String cipherText, int[] keyList) {
		System.out.println("Decrypting: " + cipherText);
		int[] plaintext_ints = getIntArray(cipherText.getBytes());

		int i = 0;
		byte[] ciphertext_bytes = new byte[cipherText.length()];
		for (int p : plaintext_ints) {
			// Working in a mod 95 system, so subtract 32 from these values
			p -= keyList[i];
			p -= 32;

			// ONLY USE STRING SUPPORTED CHARACTERS 32 (space) - 126 (~) ---

			// (mod 95)
			while (p >= 95) {
				p -= 95;
			}

			while (p < 0) {
				p += 95;
			}
			// Add 32 here
			ciphertext_bytes[i] = (byte) (p + 32);
			i++;
		}
		try {
			return new String(ciphertext_bytes, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
		/**
		 * System.out.println("Decrypting: " + tempMessage); String[] strArray =
		 * split(tempMessage);
		 * 
		 * String cipherText = "";
		 * 
		 * for (int i = 0; i < strArray.length; i++) { int index =
		 * getLoc(strArray[i]) - keyList[i]; index = index % 26;
		 * 
		 * while (index < 0) { index += 26; } cipherText += ALPHA_ARRAY[index];
		 * }
		 * 
		 * return cipherText;
		 **/
	}

	// Returns the location of the given letter (0-25)
	public static int getLoc(String s) {
		int index = 0;
		for (String str : ALPHA_ARRAY) {
			if (str.equalsIgnoreCase(s)) {
				return index;
			}
			index++;
		}
		return -1;
		// Invalid character (Used in clearIllegal())
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
			if (asciiEncoder.canEncode(String.valueOf(c))) {
				str += String.valueOf(c);
			}
		}
		return str;
	}

}
