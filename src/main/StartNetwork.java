package main;

import game.GameData;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import network.Client;
import network.Server;

import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartNetwork {
	
	public StartNetwork() {
		//panels etc...
		JFrame networkWindow = new JFrame("Netzwerkeinstellungen");
		JPanel base = new JPanel();
		JPanel buttonsStart = new JPanel();
		final JButton buttonConnectGame = new JButton("Verbinde zum Spiel");
		final JButton buttonStartServer = new JButton("Server starten");
		final JButton buttonEditIP = new JButton("IP aendern");
		final JTextPane paneIP = new JTextPane();
		
		base.setLayout(new FlowLayout());
		buttonsStart.setLayout(new FlowLayout());
		
		buttonsStart.add(buttonConnectGame);
		buttonsStart.add(buttonStartServer);
		
		base.add(buttonsStart);
		base.add(paneIP);
		base.add(buttonEditIP);

		//editing JTextPane
		StyleContext.NamedStyle styleCenter = StyleContext.getDefaultStyleContext().new NamedStyle();
		StyleConstants.setAlignment(styleCenter, StyleConstants.ALIGN_CENTER);
		paneIP.setText("127.0.0.1");
		paneIP.setEditable(false);
		paneIP.setLogicalStyle(styleCenter);
		
		//changing ip
		ActionListener alChange = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if(paneIP.isEditable()) {
					paneIP.setEditable(false);
					buttonEditIP.setText("IP aendern");
				}
				else {
					paneIP.setEditable(true);
					buttonEditIP.setText("IP bestaetigen");
				}
			}
		};
		
		//start server
		ActionListener alServer = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
			    int port = 12345;
			    String ip = "127.0.0.1";
			    GameData.server = new Server(ip, port);
			    new Thread(GameData.server).start();
			}
		};
		
		//connect to game
		ActionListener alConnect = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
			    int port = 12345;
			    String ip = "127.0.0.1";
			    GameData.client = new Client(ip, port);
			    new Thread(GameData.client).start();
			    GameData.client.send("AUTHENTICATION_REQUEST");
			    //While not authenticated wait TODO add timeout
			    while (!GameData.client.isAuthenticated()) {
			        try {
                        Thread.sleep(10);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
			    }
			    System.out.println("Successfully connected to server");
			}
		};
		
		//adding actionlisteners
		buttonEditIP.addActionListener(alChange);
		buttonStartServer.addActionListener(alServer);
		buttonConnectGame.addActionListener(alConnect);
		
		
		//setting up sizes to match everything
		buttonConnectGame.setPreferredSize(new Dimension(180, 70));
		buttonStartServer.setPreferredSize(new Dimension(180, 70));
		paneIP.setPreferredSize(new Dimension(350, 35));
		buttonEditIP.setPreferredSize(new Dimension(350, 35));
		networkWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		networkWindow.setSize(450, 215);
		networkWindow.setResizable(false);
		
		//gets dimensions from screen and calculates center
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension center = new Dimension((int)screenSize.getWidth()/2 - networkWindow.getWidth()/2, (int)screenSize.getHeight()/2 - networkWindow.getHeight()/2);
		
		networkWindow.setLocation((int)center.getWidth(), (int)center.getHeight());
		networkWindow.add(base);
		networkWindow.setVisible(true);
	}

}
