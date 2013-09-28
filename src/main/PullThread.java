package main;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import encryption.DHEngine;

public class PullThread implements Runnable {

	private String URL;
	private Engine parent;

	public PullThread(String url, Engine e) {
		URL = url;
		parent = e;
	}

	@Override
	public void run() {
		while (true) {
			if (parent.dhMode){
				return;
			}
			
			ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
			al.add(new BasicNameValuePair("PULL", "true"));
			al.add(new BasicNameValuePair("NAME", parent.name));
			String r = Functions.Get(al, URL);

			// Nothing new
			if (r.equals("NOTHING_NEW"))  {
				//Nothing new...
			} else if (r.contains("DHKE_READY")){
				//Detects that servlet has returned a message to initiate DHKE

				System.out.println("PARSING: " + r);
				
				
				int length = Integer.parseInt(r.substring(r.indexOf("(") + 1, r.indexOf(")")));
				parent.main(length);
			} else {
				System.out.println("SPLIT: \n" + r);
				String[] messages = r.split("\n");

				for (String m : messages) {
					// Parse each message in returned string
					System.out.println("PARSING: " + m);
					String mesg = m.substring(m.indexOf("(Message: ")
							+ "(Message: ".length(), m.indexOf("(Sender: ") - 1);
					String sender = m.substring(m.indexOf("(Sender: ")
							+ "(Sender: ".length(), m.indexOf("(Date: ") - 1);
					String date = m.substring(
							m.indexOf("(Date: ") + "(Date: ".length(),
							m.indexOf("(Time:") - 1);
					String time = m.substring(
							m.indexOf("(Time: ") + "(Time: ".length(),
							m.length() - 1);
					
					System.out.println("RECEIVED MESSAGE - " + time + " - "
							+ sender + ": \"" + mesg + "\"");

					//This message will always be encrypted
					Message msg = new Message(mesg, sender, date, time);
					//The chance that this will equal the ciphertext is so small it ain't even funny
					if (!mesg.contains("has joined the chatroom!")){
						parent.push(msg, true);
					} else {
						parent.push(msg, false);
					}
					
					
				}
			}
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
