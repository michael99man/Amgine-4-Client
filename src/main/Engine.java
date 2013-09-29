package main;

import encryption.DHEngine;
import gui.MainFrame;

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

	// While performing DHKE
	public boolean dhMode;

	// The DHKE Engine
	public DHEngine dhe;

	// This list holds all keys ever generated
	public LinkedList<int[]> keyList = new LinkedList<int[]>();

	public void setMainFrame(MainFrame mf) {
		parent = mf;
	}

	public Engine(String n) {
		name = n;
	}

	// ONLY TO BE USED TO SEND AN ENCRYPTED STRING
	public void send(String s) {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("SEND_MESSAGE", s));
		al.add(new BasicNameValuePair("NAME", name));
		System.out.println(Functions.Post(al, URL));
	}

	public void formKey(String s) {
		sendRequest(s.length());
		Message m = new Message(s, name);
		parent.messageList.add(m);

		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("SEND_MESSAGE", s));
		al.add(new BasicNameValuePair("NAME", name));
		System.out.println(Functions.Post(al, URL));
	}

	// Tells Servlet that client wants to send a message of length x
	public void sendRequest(int x) {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("MESSAGE_LENGTH", String.valueOf(x)));
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

	public void startThread() {
		PullThread pullThread = new PullThread(URL, this);
		new Thread(pullThread).start();
	}

	// Received message from thread
	public void push(Message mesg, boolean encrypted) {
		if (encrypted) {
			// Message is encrypted!
			int i = parent.messageList.size();

			// At this point, the keyList SHOULD BE the same length as the
			// messageList (Entry message (not encrypted) and newest key
			// (message has not been added))
			int[] key = keyList.get(i - 1);

			if (key.length != mesg.message.length()) {
				System.out.println("WTFFFFFF");
				return;
			}
			mesg.decrypt(key);
		}
		parent.messageList.add(mesg);
		parent.update();
	}

	public void addKey(int[] keyList) {
		this.keyList.add(keyList);
		// Tells client to start pulling again
		dhMode = false;

		// If you were to send the message, encrypt it and send
		if (!tempMessage.equals("")) {

			String cipherText = Functions.encrypt(tempMessage, keyList);
			send(cipherText);
			System.out.println("SENT: " + cipherText);
			tempMessage = "";
		}

		printKeys();
	}

	public void main(int length) {
		System.out.println("INITIATING DHKE: " + length);
		dhMode = true;
		dhe = new DHEngine(this, length);
	}

	public void printKeys() {
		System.out.println("KEYS: ");
		// Prints all the keys
		for (int[] ia : keyList) {
			String s = "[";
			for (int i : ia) {
				s += i;
				s += ",";
			}
			s = s.substring(0, s.length() - 1);
			s += "]";
			System.out.println(s);
			System.out.println();
		}
	}
}
