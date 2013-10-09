package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import main.Engine;
import main.Functions;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField inputField;
	private Engine engine;

	private JScrollPane panelScroller;
	public ChatPanel chatPanel;

	private JCheckBox encryptBox;

	private String chatroom;

	// The only message to be preserved when the view is refreshed;
	public String entryMessage;

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
		panelScroller.setBounds(15, 15, 574, 360);
		contentPane.add(panelScroller);

		inputField = new JTextField();
		inputField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						if (!inputField.getText().equals("")) engine.send(inputField.getText(), encryptBox.isSelected());
						inputField.setText("");
						inputField.requestFocus();
				}
			}
		});
		inputField.setBounds(15, 394, 490, 28);
		contentPane.add(inputField);
		inputField.setColumns(10);

		encryptBox = new JCheckBox("Encrypt");
		encryptBox.setFont(new Font("AppleGothic", Font.PLAIN, 13));
		encryptBox.setBounds(507, 397, 82, 23);
		contentPane.add(encryptBox);
	}

	//Refreshes the Scoller
	public void verticalMax() {
		// Focuses
		panelScroller.grabFocus();
		// Scrolls the scrollPane to the bottom
		JScrollBar vertical = panelScroller.getVerticalScrollBar();
		vertical.setValue(vertical.getMinimum());
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
