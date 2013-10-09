package main;

import gui.WaitingFrame;

import java.awt.Toolkit;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class PullThread implements Runnable {

	private String URL;
	private Engine parent;
	private WaitingFrame waitingFrame;

	// Client is only waiting if it created the servlet; If it didn't, it would
	// join automatically

	public PullThread(String url, Engine e, WaitingFrame wf, boolean create) {
		URL = url;
		parent = e;
		waitingFrame = wf;
	}

	@Override
	public void run() {
		while (true) {
			ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
			al.add(new BasicNameValuePair("PULL", "true"));
			al.add(new BasicNameValuePair("NAME", parent.name));
			String r = Functions.Get(al, URL);

			// Nothing new
			if (r.equals("NOTHING_NEW")) {
				// Nothing new...
			} else {
				String[] messages = r.split("\n");

				for (String m : messages) {
					// Parse each message in returned string
					System.out.println("PARSING: " + m);
					String mesg = m.substring(m.indexOf("(Message: ")
							+ "(Message: ".length(),
							m.indexOf("(Encrypted: ") - 1);
					String encrypt = m.substring(m.indexOf("(Encrypted: ")
							+ "(Encrypted: ".length(),
							m.indexOf("(Sender: ") - 1);
					boolean encrypted = encrypt.equals("TRUE");
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
					// This message will always be encrypted
					Message msg = new Message(mesg, sender, date, time,
							encrypted);
					Toolkit.getDefaultToolkit().beep();

					String s = " has joined the chatroom!";
					if (msg.message.contains(s) && msg.sender.equals("Server")) {
						//Space is to prevent the false negative of this client's name being a superset of the other client's name
						//e.g. Found when using names "Bobby" and "Bob"
						if (msg.message.contains(parent.name + " ")) {
							// Do nothing
						} else {
							System.out.println("Other client has joined!");
							waitingFrame.joined();
						}
						parent.messageList.add(msg);
					} else {
						parent.push(msg);
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
