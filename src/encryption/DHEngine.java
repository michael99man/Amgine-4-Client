package encryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import main.Functions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class DHEngine {
	// Engine for Diffie Hellman

	public static final String URL = "bleh";
	public static final String NAME = "eurekae";

	public int mod;
	public int base;

	public int prikey;

	private static Random rand = new Random();

	public DHEngine() {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("Init", "true"));
		postParameters.add(new BasicNameValuePair("Name", NAME));

		try {
			System.out.println(Functions.Post(postParameters, URL));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void generate() {
		prikey = rand.nextInt(10000);
		System.out.println("------------------");
		System.out.println("NEW PRIKEY=" + prikey);
	}

	public void go() {
		// Requests then sends

		// While the server is not ready
		while (!request()) {
			try {
				Thread.sleep(rand.nextInt(300));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("BASE: " + base);
		System.out.println("MOD: " + mod);

		postInt();
	}

	// Returns the other client's value modPow your own
	public int GetValue() {

		ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
		headerList.add(new BasicNameValuePair("Name", NAME));
		headerList.add(new BasicNameValuePair("GetNumber", "Value"));

		boolean done = false;
		String temp = null;
		while (!done) {
			temp = Functions.Get(headerList, URL);
			if (temp.equalsIgnoreCase("VALUE_NOT_READY")) {
				// System.out.println("SERVLET NOT READY");

				try {
					// To prevent jams
					Thread.sleep(rand.nextInt(500));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				done = true;
			}
		}

		String res = temp.replaceAll(" ", "");
		res = res.replaceAll("\n", "");
		int v = Integer.parseInt(res);
		System.out.println("GOT VALUE: " + v);

		v = modPow(v, mod);

		return (v);
	}

	public void postInt() {
		int value = modPow(base, mod);
		System.out.println("SENT: " + value);

		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("Value", String.valueOf(value)));
		param.add(new BasicNameValuePair("Name", NAME));
		Functions.Post(param, URL);
	}

	// Takes something to the power of your own prikey
	public int modPow(int intBase, int mod) {

		BigInteger base = new BigInteger(String.valueOf(intBase));
		BigInteger modulus = new BigInteger(String.valueOf(mod));

		BigInteger number = base.pow(prikey).mod(modulus);

		System.out.println(base + " ^ " + prikey + " = " + number + " (mod "
				+ mod + ")");
		return number.intValue();
	}

	// To request for Mod and Base
	public boolean request() {
		ArrayList<NameValuePair> param1 = new ArrayList<NameValuePair>();
		ArrayList<NameValuePair> param2 = new ArrayList<NameValuePair>();
		param1.add(new BasicNameValuePair("Request", "mod"));
		param1.add(new BasicNameValuePair("Name", NAME));

		param2.add(new BasicNameValuePair("Request", "base"));
		param2.add(new BasicNameValuePair("Name", NAME));

		String r1 = Functions.Get(param1, URL);
		String r2 = Functions.Get(param2, URL);

		// System.out.println("RESPONSE 1: " + r1 + " || RESPONSE 2: " + r2);

		if (r1.equalsIgnoreCase("NOT_READY")
				&& r2.equalsIgnoreCase("NOT_READY")) {
			return false;
		} else {
			try {
				mod = Integer.parseInt(r1);
				base = Integer.parseInt(r2);
			} catch (NumberFormatException e) {
				System.out.println("Aiya!");
				return false;
			}
			return true;
		}
	}
}
