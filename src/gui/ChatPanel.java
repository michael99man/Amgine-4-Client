package gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Engine;
import main.Message;

public class ChatPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private MainFrame parent;

	private static Font DEFAULT_FONT = new Font("AppleGothic", Font.PLAIN, 15);
	private static Font CRYPTO_FONT = new Font("AppleGothic", Font.ITALIC, 15);

	//Used to display the names of the senders
	private static Font NAME_FONT = new Font("AppleGothic", Font.BOLD, 18);
	
	private static Font TITLE_FONT = new Font("AppleGothic", Font.BOLD, 15);

	@SuppressWarnings("unused")
	private Engine engine;

	private int y = 40;

	private String lastSender = null;

	public ChatPanel(Engine e, MainFrame frame) {
		setLayout(null);
		engine = e;
		parent = frame;
		addLabel(parent.entryMessage, 10, 10, TITLE_FONT);
		repaint();
	}

	public void addMessage(Message m) {
		int x = 40;
		if (lastSender == null || !m.sender.equals(lastSender)) {
			y += 5;
			addLabel(m.sender, 20, y, NAME_FONT);
			y += 25;
		}
		
		// Formats the message
		addLabel(m.message, x, y,
				(m.encrypted ? CRYPTO_FONT : DEFAULT_FONT));

		y += 20;
		// Brings focus back to the textField
		parent.focus();
		lastSender = m.sender;
	}

	public void addLabel(String s, int x, int y, Font font) {
		// FontMetrics metrics = getGraphics().getFontMetrics(DEFAULT_FONT);
		// Creates a new label and adds it to the JPanel
		JLabel label = new JLabel(s);
		label.setFont(font);
		label.setBounds(x, y, label.getPreferredSize().width + 5,
				label.getPreferredSize().height);
		add(label);

		if (y > getHeight()) {
			resize(y - getHeight());
		}

		repaint();
		parent.verticalMax();
		repaint();
	}

	public void resize(int y) {
		setPreferredSize(new Dimension(getWidth(), getHeight() + y));
		System.out.println("RESIZED TO " + getWidth() + ", " + getHeight());
	}
}
