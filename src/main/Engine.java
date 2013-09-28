package main;

import encryption.DHEngine;
import gui.MainFrame;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Engine {

	
	//The url of the MainServlet
	public static final String BASEURL = "http://localhost:8080/Amgine_4/";
	
	//The url of the chatroom
	public String URL;

	//Name of the client, to be used in info processing
	public String name;
	
	//Name of the chatroom
	public String chatroom;

	private MainFrame parent;
	
	//The plaintext you want to send. Stored when encryption array is not ready
	public String tempMessage;
	
	//While performing DHKE
	public boolean dhMode;
	
	//The DHKE Engine
	public DHEngine dhe;

	public void setMainFrame(MainFrame mf) {
		parent = mf;
	}

	public Engine(String n) {
		name = n;
	}
	
	//ONLY TO BE USED TO SEND AN ENCRYPTED STRING
	public void send(String s) {
		sendRequest(s.length());
		
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("SEND_MESSAGE", s));
		al.add(new BasicNameValuePair("NAME", name));
		System.out.println(Functions.Post(al, URL));
	}

	//Tells Servlet that client wants to send a message of length x
	public void sendRequest(int x){
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
	public void push(Message mesg) {
		parent.messageList.add(mesg);
		parent.update();
	}
}
