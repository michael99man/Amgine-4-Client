package main;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class PullThread implements Runnable {

	private String URL;
	private Engine parent;
	
	public PullThread(String url, Engine e){
		URL = url;
		parent = e;
	}
	
	@Override
	public void run() {
		while (true){
			ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
			al.add(new BasicNameValuePair("PULL", "true"));
			al.add(new BasicNameValuePair("NAME", parent.name));
			String r = Functions.Get(al, URL);
			
			//Nothing new
			if (r.equals("NOTHING_NEW")){
				//System.out.println("NOTHING TO PULL!");
			} else {
				System.out.println(r);
				String mesg = r.substring(r.indexOf("Message: ") + "Message: ".length(), r.indexOf("Sender: ") - 2);
				String sender = r.substring(r.indexOf("Sender: ") + "Sender: ".length(), r.indexOf("Time: ") - 2);
				String time = r.substring(r.indexOf("Time: ") + "Time: ".length(), r.length() - 1);
				
				
				System.out.println("RECEIVED MESSAGE!!!");
				System.out.println(mesg);
				System.out.println(sender);
				System.out.println(time);
			}
				
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
