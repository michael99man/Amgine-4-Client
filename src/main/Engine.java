package main;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Engine {

	public static final String BASEURL = "http://localhost:8080/Amgine_4/";
	public static String URL;
	
	public String name;
	private String chatroom;
	
	public Engine(String n) {
		name = n;
	}

	public void send(String s){
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("SEND_MESSAGE", s));
		al.add(new BasicNameValuePair("NAME", name));
		System.out.println(Functions.Post(al, URL));
	}
	
	// Creates chatroom
	public void create(String id) {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("CREATECHATR", id));
		al.add(new BasicNameValuePair("NAME", name));
		String s = Functions.Post(al, BASEURL);
		System.out.println(s);
		
		URL = s;
		//Gets Chatroom URL
		chatroom = id;
	}

	//Joins chatroom
	public void join(String id){
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("JOINCHATR", id));
		al.add(new BasicNameValuePair("NAME", name));
		String s = Functions.Get(al, BASEURL);
		System.out.println(s);
		
		URL = s;
		//Gets Chatroom URL
		chatroom = id;
	}

	public void startThread() {
		PullThread pullThread = new PullThread(URL, this);
		new Thread(pullThread).start();
	}
	
	
	//Received message from thread
	public void push(String mesg){
		
		
	}
}
