package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.Choice;
import javax.swing.JButton;

import main.Engine;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

//JOB: To receive user input to create/join a chatroom
public class StartWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private static JTextField nameField;
	private static JTextField idField;
	private static Choice choice;

	private JFrame instance = this;
	
	private JFrame logFrame = new JFrame("Event Log");
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartWindow frame = new StartWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//Opens the response log
	public void openWindow(){
		logFrame.setBounds(100,100,400,250);
		logFrame.setVisible(true);
		logFrame.setTitle("Event Log");
		//Do more stuff here
		//Show errors, etc
		
		//TODO: Implement commands
			//e.g. show open chatrooms
		//TODO: Password protected chatrooms
	}
	
	public StartWindow() {
		setTitle("AMGINE 4");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 297, 207);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(Color.WHITE);

		choice = new Choice();
		choice.setBounds(202, 20, 75, 35);
		choice.addItem("Create");
		choice.addItem("Join");
		choice.setBackground(Color.WHITE);
		contentPane.add(choice);
		
		int height = 40;
		int width = 180;

		nameField = new JTextField();
		nameField.setFont(new Font("AppleGothic", Font.BOLD, 18));
		nameField.setBounds(18, 14, width, height);

		TitledBorder title = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK), "Name");
		title.setTitleFont((new Font("AppleGothic", Font.ITALIC, 14)));
		title.setTitleJustification(TitledBorder.LEFT);
		nameField.setBorder(title);
		contentPane.add(nameField);

		idField = new JTextField();
		idField.setFont(new Font("AppleGothic", Font.BOLD, 18));
		idField.setBounds(18, 65, width, height);
		TitledBorder title2 = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK), "Chatroom ID");
		title2.setTitleFont((new Font("AppleGothic", Font.ITALIC, 14)));
		title2.setTitleJustification(TitledBorder.LEFT);
		idField.setBorder(title2);
		contentPane.add(idField);

		JButton StartButton = new JButton("Start");
		StartButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String name = nameField.getText();
				String id = idField.getText();

				if (name.equals("") || id.equals("")) {
					System.out.println("Invalid input");
					return;
				}

				Engine e = new Engine(name);
				boolean success = false;
				if (choice.getSelectedItem().equals("Create")) {
					success = e.create(id);
				} else {
					success = e.join(id);
				}

				if (success) {
					try {
						WaitingFrame wf = new WaitingFrame(e);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						wf.go();
						//If client was told to join a chatroom
						if (choice.getSelectedItem().equals("Join")){
							wf.joined();
						}
						//Starts listening
						//Call only after chatroom has been created!
						e.startThread(wf, choice.getSelectedItem().equals("Create"));
					} catch (NoSuchAlgorithmException e1) {
						e1.printStackTrace();
					} catch (InvalidKeySpecException e1) {
						e1.printStackTrace();
					}
					
					System.out.println("Closing window");
					instance.dispose();
				}
			}
		});
		StartButton
				.setFont(new Font("AppleGothic", Font.BOLD | Font.ITALIC, 30));
		StartButton.setBounds(18, 130, 258, 40);
		contentPane.add(StartButton);

		JButton shellButton = new JButton("?");
		shellButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				openWindow();
			}
		});
		shellButton.setFont(new Font("AppleGothic", Font.BOLD, 27));
		shellButton.setBounds(202, 70, 75, 38);
		contentPane.add(shellButton);
	}
}
