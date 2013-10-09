package main;

import gui.MainFrame;
import gui.WaitingFrame;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Engine {

	// The url of the MainServlet
	//public static final String BASEURL = "http://localhost:8080/Amgine_4/";
	 public static final String BASEURL = "https://amgine4-michael99man.rhcloud.com/";

	// The url of the chatroom
	public String URL;

	// Name of the client, to be used in info processing
	public String name;

	// Name of the chatroom
	public String chatroom;

	private MainFrame parent;

	// The plaintext you want to send. Stored when encryption array is not ready
	public String tempMessage = "";

	// This list holds all keys ever generated
	public LinkedList<Integer> keyList = new LinkedList<Integer>();

	// Holds all messages
	public LinkedList<Message> messageList = new LinkedList<Message>();

	// My public modulus
	public BigInteger pubMod;

	// My public exponent
	public BigInteger pubExp;

	// My private exponent
	public BigInteger privExp;

	// Other client's public modulus
	public BigInteger otherPubMod;

	// Other client's public exponent
	public BigInteger otherPubExp;

	public void setMainFrame(MainFrame mf) {
		parent = mf;
	}

	public Engine(String n) {
		name = n;
	}

	public void formKey(String s) {
		sendRequest(s.length());
		Message m = new Message(s, name);
		messageList.add(m);

		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("SEND_MESSAGE", s));
		al.add(new BasicNameValuePair("NAME", name));
		System.out.println(Functions.Post(al, URL));
	}

	// Tells Servlet that client wants to send a message of length x
	public void sendRequest(int x) {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("AMOUNT", String.valueOf(x)));
		al.add(new BasicNameValuePair("NAME", name));
		System.out.println(Functions.Post(al, URL));
	}

	// Creates chatroom
	// Returns true if successful
	public boolean create(String id) {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("CREATECHATR", id));
		al.add(new BasicNameValuePair("NAME", name));
		String s = Functions.Post(al, BASEURL);
		// If room already exists
		if (s.equals("ERROR")) {
			System.out.println("ERROR! Room already exists");
			return false;
		}
		System.out.println(s);

		URL = s;
		// Gets Chatroom URL
		chatroom = id;
		return true;
	}

	// Joins chatroom
	// Returns true if successful
	public boolean join(String id) {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("JOINCHATR", id));
		al.add(new BasicNameValuePair("NAME", name));
		String s = Functions.Get(al, BASEURL);

		if (s.equals("ERROR")) {
			System.out.println("ERROR! Room doesn't exist!");
			return false;
		}
		System.out.println(s);

		URL = s;
		// Gets Chatroom URL
		chatroom = id;
		return true;
	}

	public void startThread(WaitingFrame wf, boolean create) {
		PullThread pullThread = new PullThread(URL, this, wf, create);
		new Thread(pullThread).start();
	}

	// Received message from thread
	public void push(Message mesg) {
		if (mesg.encrypted) {
			// Message is encrypted!
			mesg.decrypt(privExp, pubMod);
		}
		messageList.add(mesg);
		parent.chatPanel.addMessage(mesg);
	}

	public void printKeys() {
		System.out.println("KEYS: ");
		// Prints all the keys
		String s = "[";
		for (int i : keyList) {
			s += i;
			s += ",";
		}
		s = s.substring(0, s.length() - 1);
		s += "]";
		System.out.println(s);
	}

	public void leaveChatroom() {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("LEAVE_CHATROOM", "TRUE"));
		al.add(new BasicNameValuePair("NAME", name));
		System.out.println(Functions.Post(al, URL));
	}

	// Sends the plaintext
	public void send(String s, boolean encrypt) {
		s = Functions.clearIllegal(s);

		Message m = new Message(s, name);

		m.encrypted = encrypt;
		if (encrypt) {
			m.cipherText = Functions.encrypt(s, otherPubExp, otherPubMod);
		}
		messageList.add(m);
		parent.chatPanel.addMessage(m);

		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("SEND_MESSAGE", encrypt ? m.cipherText
				: m.message));
		al.add(new BasicNameValuePair("NAME", name));
		al.add(new BasicNameValuePair("ENCRYPTED", encrypt ? "TRUE" : "FALSE"));
		System.out.println(Functions.Post(al, URL));
	}
}
