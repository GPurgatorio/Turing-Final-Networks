import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GUILoggedClass extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private String username;
	private static DataOutputStream outToServer;
	private static BufferedReader inFromServer;
	private static Socket clientSocket;
	private Image createDocImg, inviteImg, showImg, listImg, editImg, logoutImg;
	private JButton createDocButton, inviteButton, editButton, listButton, showButton, logoutButton;
	private JLabel userLabel;
	
	public GUILoggedClass(DataOutputStream dos, BufferedReader ifs, Socket s, String usr) throws IOException {
		outToServer = dos;
		inFromServer = ifs;
		clientSocket = s;
		username = usr;
		clientUI();
	}

	public void clientUI() throws IOException {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		setLayout(null);
		setSize(450,450);

		createDocImg = ImageIO.read(new File("img/add.png"));
		inviteImg = ImageIO.read(new File("img/invite.png"));
		showImg = ImageIO.read(new File("img/show.png"));
		listImg = ImageIO.read(new File("img/list.png"));
		editImg = ImageIO.read(new File("img/edit.png"));
		logoutImg = ImageIO.read(new File("img/logout.png"));
		
		createDocImg = createDocImg.getScaledInstance(100, 100, Image.SCALE_DEFAULT);			
		inviteImg = inviteImg.getScaledInstance(100, 100, Image.SCALE_DEFAULT);			
		showImg = showImg.getScaledInstance(100, 100, Image.SCALE_DEFAULT);			
		listImg = listImg.getScaledInstance(100, 100, Image.SCALE_DEFAULT);			
		editImg = editImg.getScaledInstance(100, 100, Image.SCALE_DEFAULT);			
		logoutImg = logoutImg.getScaledInstance(100, 100, Image.SCALE_DEFAULT);
		
		createDocButton = new JButton();
		createDocButton.setIcon(new ImageIcon(createDocImg));
		inviteButton = new JButton();
		inviteButton.setIcon(new ImageIcon(inviteImg));
		showButton = new JButton();
		showButton.setIcon(new ImageIcon(showImg));
		listButton = new JButton();
		listButton.setIcon(new ImageIcon(listImg));
		editButton = new JButton();
		editButton.setIcon(new ImageIcon(editImg));
		logoutButton = new JButton();
		logoutButton.setIcon(new ImageIcon(logoutImg));
		
		createDocButton.setBounds(95,50,115,115);
		inviteButton.setBounds(225,50,115,115);
		showButton.setBounds(95,170,115,115);
		listButton.setBounds(225,170,115,115);
		editButton.setBounds(95,290,115,115);
		logoutButton.setBounds(225,290,115,115);
		
		createDocButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ae){
				try {
					outToServer.writeBytes("createDoc" + '\n');
					createDocRequest();
				} catch (IOException e) {
					e.printStackTrace();	//outToServer.writeBytes
				}
			}
		});
		
		inviteButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ae){
				try {
					outToServer.writeBytes("invite" + '\n');
					inviteRequest();
				} catch (IOException e) {
					e.printStackTrace();	//outToServer.writeBytes
				}
			}
		});
		
		showButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ae){
				try {
					outToServer.writeBytes("show" + '\n');
					showRequest();
				} catch (IOException e) {
					e.printStackTrace();	//outToServer.writeBytes
				}
			}
		});
		
		listButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ae){
				try {
					outToServer.writeBytes("list" + '\n');
					listRequest();
				} catch (IOException e) {
					e.printStackTrace();	//outToServer.writeBytes
				}
			}
		});
		
		editButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ae){
				try {
					outToServer.writeBytes("editDoc" + '\n');
					editRequest();
				} catch (IOException e) {
					e.printStackTrace();	//outToServer.writeBytes
				}
			}
		});
		
		logoutButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent ae){
				try {
					outToServer.writeBytes("logout" + '\n');
					logoutRequest();
				} catch (IOException e) {
					e.printStackTrace();	//outToServer.writeBytes
				}
			}
		});
		
		add(createDocButton);
		add(inviteButton);
		add(showButton);
		add(listButton);
		add(editButton);
		add(logoutButton);

			
		userLabel = new JLabel("Current user: " + username);
		userLabel.setBounds(20, 20, 200, 15);
		add(userLabel);
	}
	
	public void createDocRequest() throws IOException {
		
		JTextField docLabel = new JTextField();
		JTextField secLabel = new JTextField();
		
		Object[] struct = {
				"Nome Documento:", docLabel,
				"Numero Sezioni [1-9]:", secLabel
		};
		
		
		String res, docName = null;
		int sections; 
		
		do {
			try {
				
				int option = JOptionPane.showConfirmDialog(null, struct, "Creazione Documento", JOptionPane.OK_CANCEL_OPTION);
				
				if (option == JOptionPane.OK_OPTION) {
					docName = docLabel.getText();
					sections = Integer.parseInt(secLabel.getText());
				}
				
				else
					return;
			}
			catch (NumberFormatException e) {
				sections = -1;
			}
		} while (sections < 1 || sections > 9 || docName == null);
		
		outToServer.writeBytes(username + '\n');
		outToServer.writeBytes(docName + '\n');
		outToServer.writeByte(sections);
		
		res = inFromServer.readLine();
		
		switch(res) {
			case "SUCCESS":
				JOptionPane.showMessageDialog(null, "Documento " + docName + " creato con successo!");
				break;
			case "DOC_EXISTS":
				JOptionPane.showMessageDialog(null, "Un documento con nome " + docName + " risulta gi� presente. Cambia nome!");
				break;
			case "HACKER":
				JOptionPane.showMessageDialog(null, "Non risulti registrato.");
				break;
			default:
				JOptionPane.showMessageDialog(null, "Errore generico non specificato");
				System.err.println(res);
				break;
		}
		
	}
	
	public void inviteRequest() throws IOException {
		
		JTextField docLabel = new JTextField();
		JTextField usLabel = new JTextField();
		
		Object[] struct = {
				"Documento:", docLabel,
				"Utente:", usLabel
		};
		
		String res, user = null, docName = null; 
		
		do {
			
			int option = JOptionPane.showConfirmDialog(null, struct, "Invito a Documento", JOptionPane.OK_CANCEL_OPTION);
			
			if (option == JOptionPane.OK_OPTION) {
				docName = docLabel.getText();
				user = usLabel.getText();
			}
			
			else
				return;
		} while (docName == null || user == null);
		
		outToServer.writeBytes(username + '\n');
		outToServer.writeBytes(user + '\n');
		outToServer.writeBytes(docName + '\n');
		
		res = inFromServer.readLine();		
		
		switch(res) {
			case "SUCCESS":
				JOptionPane.showMessageDialog(null, "Utente " + user + " invitato con successo al documento " + docName + "!" );
				break;
			case "HACKER":
				JOptionPane.showMessageDialog(null, "Non risulti registrato.");
				break;
			case "UNKNWN_USR":
				JOptionPane.showMessageDialog(null, "L'utente " + user + " non risulta essere registrato.");
				break;
			case "UNKNWN_DOC":
				JOptionPane.showMessageDialog(null, "Il documento " + docName + " non risulta essere presente.");
				break;
			case "NOT_CREATOR":
				JOptionPane.showMessageDialog(null, "Non puoi invitare persone ad un documento di cui non sei il creatore.");
				break;
			case "EDITOR_ALRD":
				JOptionPane.showMessageDialog(null, "L'utente " + user + " � gi� Editor del documento " + docName + "!");
				break;
			default:
				JOptionPane.showMessageDialog(null, "Errore generico non specificato.");
				System.err.println(res);
				break;
		}
		
	}

	public void showRequest() throws IOException {
		
		JTextField docLabel = new JTextField();
		JTextField secLabel = new JTextField();
		
		Object[] struct = {
				"Nome Documento:", docLabel,
				"Sezione [opt]:", secLabel
		};
		
		String res, docName = null;
		int sections; 
		
		do {
			try {
				
				int option = JOptionPane.showConfirmDialog(null, struct, "Mostra [Sezione di] Documento", JOptionPane.OK_CANCEL_OPTION);
				
				if (option == JOptionPane.OK_OPTION) {
					docName = docLabel.getText();
					sections = Integer.parseInt(secLabel.getText());
				}
				
				else
					return;
			}
			catch (NumberFormatException e) {
				sections = -1;
			}
		} while (docName == null || sections < 0);
		
		outToServer.writeBytes(username + '\n');
		outToServer.writeBytes(docName + '\n');
		outToServer.writeByte(sections);
		
		res = inFromServer.readLine();		
		
		switch(res) {
			case "SUCCESS":
				JOptionPane.showMessageDialog(null, "Download del documento " + docName + " iniziato!" );
				break;
			//TODO
			default:
				JOptionPane.showMessageDialog(null, "Errore generico non specificato.");
				System.err.println(res);
				break;
		}
	}
	
	public void listRequest() throws IOException {
		
		outToServer.writeBytes(username + '\n');
		
		String res = inFromServer.readLine();
		
		switch(res) {
			case "SUCCESS":
				JOptionPane.showMessageDialog(null, "Lista Documenti: " );
				break;
				//TODO
			default:
				JOptionPane.showMessageDialog(null, "Errore generico non specificato.");
				System.err.println(res);
				break;
		}
		
	}

	public void editRequest() throws IOException {
		
		JTextField docLabel = new JTextField();
		JTextField secLabel = new JTextField();
		
		Object[] struct = {
				"Documento:", docLabel,
				"Sezione:", secLabel
		};
		
		String res, docName = null;
		int section; 
		
		do {
			try {
				
				int option = JOptionPane.showConfirmDialog(null, struct, "Modifica Sezione di Documento", JOptionPane.OK_CANCEL_OPTION);
				
				if (option == JOptionPane.OK_OPTION) {
					docName = docLabel.getText();
					section = Integer.parseInt(secLabel.getText());
				}
				
				else
					return;
			}
			catch (NumberFormatException e) {
				section = -1;
			}
		} while (docName == null || section < 0);
		
		outToServer.writeBytes(username + '\n');
		outToServer.writeBytes(docName + '\n');
		outToServer.writeByte(section);
		
		res = inFromServer.readLine();		
		
		switch(res) {
			case "SUCCESS":
				remove(userLabel);
				remove(createDocButton);
				remove(inviteButton);
				remove(showButton);
				remove(listButton);
				remove(editButton);
				remove(logoutButton);
				this.dispose();
				GUIEditClass w = new GUIEditClass(outToServer, inFromServer, clientSocket, username);
				username = "";
				w.getContentPane().setBackground(Color.LIGHT_GRAY);
				w.setLocation(400, 100);
				w.setVisible(true);
				break;
			//TODO
			default:
				JOptionPane.showMessageDialog(null, "Errore generico non specificato.");
				System.err.println(res);
				break;
		}
		
	}

	public void logoutRequest() throws IOException {
		
		outToServer.writeBytes(username + '\n');

		String res = inFromServer.readLine();
		
		if(!res.equals("ERROR")) {
			username = "";
			remove(userLabel);
			remove(createDocButton);
			remove(inviteButton);
			remove(showButton);
			remove(listButton);
			remove(editButton);
			remove(logoutButton);
			this.dispose();
			GUIClass w = new GUIClass(outToServer, inFromServer, clientSocket);
			w.getContentPane().setBackground(new java.awt.Color(4, 167, 210));	
			w.setLocation(400, 100);
			w.setVisible(true);
		}
		else {
			JOptionPane.showMessageDialog(null, "You can't disconnect. Don't ask me why tho");
		}
	}

}
