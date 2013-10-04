package gui;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import main.Engine;
import main.Functions;
import main.Message;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField inputField;
	private Engine engine;
	private JTextArea textArea;
	private JTextArea keyField;

	private JScrollPane panelScroller;
	public ChatPanel chatPanel;

	private JCheckBox encryptBox;

	private String chatroom;

	// Name of the last sender
	private String lastSender = "";

	// The only message to be preserved when the view is refreshed;
	public String entryMessage;

	private JTextField amountField;

	public JProgressBar progressBar;

	public MainFrame(Engine e) {

		// Shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread(this)));

		engine = e;
		chatroom = e.chatroom;

		setTitle("CHATROOM : " + chatroom);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 100, 600, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		entryMessage = Functions.getTime(new SimpleDateFormat("(HH:mm:ss)"))
				+ ": Welcome to chatroom \"" + chatroom + "\", " + engine.name;

		panelScroller = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// contentPane.add(textArea);
		chatPanel = new ChatPanel(engine, this);
		chatPanel.setPreferredSize(new Dimension(450, 360));

		panelScroller.setViewportView(chatPanel);
		panelScroller.setBounds(15, 15, 460, 360);
		contentPane.add(panelScroller);

		inputField = new JTextField();
		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					if (engine.canSend()) {
						send(inputField.getText());
						inputField.setText("");
						inputField.requestFocus();
					}
				}
			}
		});
		inputField.setBounds(15, 394, 490, 28);
		contentPane.add(inputField);
		inputField.setColumns(10);

		keyField = new JTextArea();
		keyField.setEditable(false);
		keyField.setFont(new Font("AppleGothic", Font.ITALIC, 12));
		keyField.setBounds(488, 60, 101, 270);
		contentPane.add(keyField);

		amountField = new JFormattedTextField();
		amountField.setFont(new Font("AppleGothic", Font.BOLD, 15));
		amountField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					if (amountField.getText().length() > 0) {
						if (amountField.getText().length() > 0) {
							progressBar.setIndeterminate(true);
							progressBar.setMaximum(Integer.parseInt(amountField
									.getText()));
							progressBar.setString("Waiting...");
							engine.sendRequest(Integer.parseInt(amountField
									.getText()));
							amountField.setText("");
						}
					}
				}
			}
		});
		amountField.setBounds(488, 362, 101, 28);
		amountField.setColumns(2);
		contentPane.add(amountField);

		JLabel KeyLabel = new JLabel("Keys");
		KeyLabel.setFont(new Font("AppleGothic", Font.BOLD | Font.ITALIC, 30));
		KeyLabel.setBounds(495, 16, 89, 42);
		contentPane.add(KeyLabel);

		encryptBox = new JCheckBox("Encrypt");
		encryptBox.setFont(new Font("AppleGothic", Font.PLAIN, 13));
		encryptBox.setBounds(507, 397, 82, 23);
		contentPane.add(encryptBox);

		progressBar = new JProgressBar();
		progressBar.setFont(new Font("AppleGothic", Font.PLAIN, 14));
		progressBar.setBounds(487, 335, 101, 20);
		progressBar.setStringPainted(true);
		progressBar.setString("Ready");
		contentPane.add(progressBar);
	}

	// Sends the plaintext
	private void send(String s) {
		s = Functions.clearIllegal(s);

		Message m = new Message(s, engine.name);
		if (encryptBox.isSelected()) {
			// Encrypt

			// Terminates if we don't have enough keys
			if (s.length() > engine.keyList.size()) {
				textArea.append("\n");
				textArea.append("NOT ENOUGH KEYS TO ENCRYPT (Need "
						+ (s.length() - engine.keyList.size()) + " more keys)");
				textArea.append("\n");
				textArea.append("\n");
				return;
			}

			int l = s.length();
			int[] key = new int[l];

			engine.printKeys();
			for (int i = 0; i < l; i++) {
				key[i] = engine.keyList.get(0);
				engine.keyList.poll();
			}
			System.out.println("Removed first " + l + "items of KeyList");
			engine.printKeys();

			System.out.println("Encrypting " + s + " with: ");
			for (int i : key) {
				System.out.print(i);
				System.out.print(",");
			}
			System.out.println();

			String cipherText = Functions.encrypt(s, key);
			System.out.println(s + " --> " + cipherText);

			// Manually does this
			m.cipherText = cipherText;
			m.encrypted = true;
			engine.send(cipherText, true);
		} else {
			engine.send(s, false);
		}
		engine.messageList.add(m);
		chatPanel.addMessage(m);
		updateKeys();
	}

	// Refreshes the view
	public void updateKeys() {
		keyField.setText("Keys: (" + engine.keyList.size() + ")");
		keyField.append("\n");
		for (int i : engine.keyList) {
			keyField.append(String.valueOf(i));
			keyField.append("\n");
		}
	}

	public void verticalMax() {
		// Focuses
		panelScroller.grabFocus();
		// Scrolls the scrollPane to the bottom
		JScrollBar vertical = panelScroller.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

	public void focus() {
		// Gives the inputField focus again
		inputField.requestFocusInWindow();
	}
	
	//Called when window is closing
	public void exit(){
		System.out.println("Application is quitting!");
		engine.leaveChatroom();
	}
}
