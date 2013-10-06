package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import main.Engine;
import main.Functions;

public class WaitingFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Engine engine;
	private WaitingFrame instance;

	private JLabel mainLabel;

	public WaitingFrame(Engine e){
		instance = this;
		engine = e;
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 319, 186);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		mainLabel = new JLabel("WAITING");
		mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainLabel.setFont(new Font("AppleGothic", Font.BOLD | Font.ITALIC, 20));
		mainLabel.setBounds(23, 19, 273, 62);
		contentPane.add(mainLabel);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(23, 74, 273, 53);
		contentPane.add(progressBar);
	}
	
	public void go() throws NoSuchAlgorithmException, InvalidKeySpecException{
		mainLabel.setText("Initializing RSA Generator");
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair kp = keyGen.genKeyPair();

		KeyFactory fact = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
				RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
				RSAPrivateKeySpec.class);

		mainLabel.setText("Generating RSA Key Pair");
		engine.pubMod = pub.getModulus();
		engine.privExp = priv.getPrivateExponent();
		engine.pubExp = pub.getPublicExponent();
		System.out.println("Modulus: " + engine.pubMod.toString());
		System.out.println("Public Exponent: " + engine.pubExp);
		System.out.println("Private Exponent: " + engine.privExp);

		mainLabel.setText("Finishing RSA");

		mainLabel.setText("Sending Data to Server");
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("MODULUS", engine.pubMod.toString()));
		al.add(new BasicNameValuePair("EXPONENT", engine.pubExp.toString()));
		al.add(new BasicNameValuePair("NAME", engine.name));
		System.out.println(Functions.Post(al, engine.URL + "/RSA"));

		mainLabel.setText("Waiting for other client to join");
	}

	public void done() {
		// Creates a MainFrame and closes the window
		MainFrame mf = new MainFrame(engine);
		engine.setMainFrame(mf);
		System.out.println("Closing window");
		instance.dispose();
	}
	// Other client has joined
	public void joined() {
		mainLabel.setText("Gettings keys from client");

		boolean received = false;
		String mod = null;
		String exp = null;
		
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("MODULUS", "REQUEST"));
		al.add(new BasicNameValuePair("NAME", engine.name));
		
		ArrayList<NameValuePair> al2 = new ArrayList<NameValuePair>();
		al2.add(new BasicNameValuePair("EXPONENT", "REQUEST"));
		al2.add(new BasicNameValuePair("NAME", engine.name));
		
		while (!received) {	
			mod = Functions.Get(al, engine.URL + "/RSA");
			exp = Functions.Get(al2, engine.URL + "/RSA");

			if (!mod.contains("NOT_READY") && !exp.contains("NOT_READY")) {
				received = true;
				System.out.println("REQUEST SUCCEEDED!");
			} else {
				System.out.println("REQUEST FAILED!");
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mainLabel.setText("Received keys");
		
		System.out.println("OTHER CLIENT'S DATA - MOD: " + mod + " || EXP: " + exp);
		engine.otherPubMod = new BigInteger(mod);
		engine.otherPubExp = new BigInteger(exp);
		
		done();
	}
}
