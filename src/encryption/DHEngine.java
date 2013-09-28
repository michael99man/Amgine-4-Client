package encryption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import main.Engine;
import main.Functions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class DHEngine {
	// Engine for Diffie Hellman

	public int mod;
	public int base;

	public int prikey;

	public Engine parent;

	private static Random rand = new Random();
	
	
	private String URL;
	
	private int[] keyList;
	
	// Will either be called from the sendRequest() method OR the PullThread
	//x is the amount of times to perform DHKE
	public DHEngine(Engine e, int x) {
		parent = e;
		keyList = new int[x];
		URL = e.URL + "/DHKE";
		
		//Always call last in constructor!
		Main(x);
	}

	public void generate() {
		prikey = rand.nextInt(10000);
		System.out.println("-----------------------------------");
		System.out.println("NEW PRIKEY=" + prikey);
	}

	// Main function
	public void Main(int x) {
		for (int i = 1; i <= x; i++) {
			generate();

			// Requests then sends own value
			while (!request()) {
				// While the server is not ready...
				try {
					Thread.sleep(rand.nextInt(200));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("BASE: " + base);
			System.out.println("MOD: " + mod);

			// Posts your number
			postInt();

			int value = GetValue();
			System.out.println("FINISHED DHKE " + i + " time(s): " + value);
			keyList[i-1] = value;
			
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		
		System.out.println("FINISHED EVERYTHING!!");
		mod = 0;
		base = 0;
		parent.addKey(keyList);
	}

	// Returns the other client's value (modPow their secret key)
	public int GetValue() {
		ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
		headerList.add(new BasicNameValuePair("NAME", parent.name));
		headerList.add(new BasicNameValuePair("GETVALUE", "Value"));

		boolean done = false;
		String temp = null;
		while (!done) {
			temp = Functions.Get(headerList, URL);
			if (temp.equalsIgnoreCase("VALUE_NOT_READY")) {
				// System.out.println("SERVLET NOT READY");
				try {
					// To prevent jams
					Thread.sleep(rand.nextInt(200));
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
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("POSTVALUE", String.valueOf(value)));
		param.add(new BasicNameValuePair("NAME", parent.name));
		Functions.Post(param, URL);
		System.out.println("SENT: " + value);
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
		if (reqMod() && reqBase() || mod!=0 && base!=0){
			return true;
		}
		return false;
	}
	
	private boolean reqMod(){
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("REQUEST", "MOD"));
		param.add(new BasicNameValuePair("NAME", parent.name));

		String r = Functions.Get(param, URL);


		if (r.equalsIgnoreCase("NOT_READY")) {
			return false;
		} else {
			try {
				mod = Integer.parseInt(r);
			} catch (NumberFormatException e) {
				System.out.println("AIYA MOD: " + r);
				return false;
			}
			return true;
		}
	}
	
	private boolean reqBase(){
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("REQUEST", "BASE"));
		param.add(new BasicNameValuePair("NAME", parent.name));

		String r = Functions.Get(param, URL);

		// System.out.println("RESPONSE 1: " + r1 + " || RESPONSE : " + r);

		if (r.equalsIgnoreCase("NOT_READY")) {
			return false;
		} else {
			try {
				base = Integer.parseInt(r);
			} catch (NumberFormatException e) {
				System.out.println("AIYA BASE: " + r);
				return false;
			}
			return true;
		}
	}

}
