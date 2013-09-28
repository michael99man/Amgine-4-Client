package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message {

	public String date;
	public String time;

	public String message;
	public String sender;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy");
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm:ss");

	// Near identical to the Message class in the Servlet

	// To be used for messages sent by client
	public Message(String msg, String u) {
		sender = u;
		message = msg;
		time = Functions.getTime(TIME_FORMAT);
		date = Functions.getTime(DATE_FORMAT);
	}

	// To be used by messages received by client
	public Message(String msg, String u, String date, String time) {
		sender = u;
		message = msg;
		this.date = date;
		this.time = time;
	}

	// To be used by MainFrame
	public String format() {
		String msg = "";

		msg += "\t";

		// If it's the same date
		if (DATE_FORMAT.format(Calendar.getInstance().getTime())
				.contains(date)) {
			msg += time;
			// If it's not
		} else {
			msg += "(" + date + ") - " + time;
		}

		msg += ": ";
		msg += message;
		return msg;
	}
}
