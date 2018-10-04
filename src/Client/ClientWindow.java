package Client;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientWindow extends JFrame{
	
	private static final String SERVER_HOST = "localhost";
	private static final int SERVER_PORT = 3443;
	
	private Socket clientSocket;
	private Scanner inMessage;
	private PrintWriter outMessage;
	
	private JTextField jtfMessage;
	private JTextField jtfName;
	private JTextArea jtaTextAreaMessage;
	
	private String clientname = "";

	public String getClientname() {
		return clientname;
	}

	public ClientWindow() {
		try {
			//Підключення до сервера
			clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
			inMessage = new Scanner(clientSocket.getInputStream());
			outMessage= new PrintWriter(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Створення вікна на SWING
		setBounds(600, 300, 600, 500);
		setTitle("SimpleChat Client");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jtaTextAreaMessage = new JTextArea();
		jtaTextAreaMessage.setEditable(false);
		jtaTextAreaMessage.setLineWrap(true);
		JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
		add(jsp, BorderLayout.CENTER);
			
		JLabel jlNumberOfClients= new JLabel("Number of users in chat: ");
		add(jlNumberOfClients, BorderLayout.NORTH);
		JPanel bottomPanel = new JPanel(new BorderLayout());
		add(bottomPanel, BorderLayout.SOUTH);
		JButton jbSendMessage = new JButton("Send");
		bottomPanel.add(jbSendMessage, BorderLayout.EAST);
		jtfMessage = new JTextField("Input your message: ");
		bottomPanel.add(jtfMessage, BorderLayout.CENTER);
		jtfName = new JTextField("Input your name: ");
		bottomPanel.add(jtfName, BorderLayout.WEST);
		JPanel jpUserList = new JPanel(new BorderLayout());
		add(jpUserList, BorderLayout.NORTH);
		
		//Дія для кнопки повідомлення
		jbSendMessage.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
					clientname = jtfName.getText();
					sendMsg();
					//Фокус на полі повідомлення
					jtfMessage.grabFocus();
				}
				
			}
		});
		
		//При фокусі поле очищається
		jtfMessage.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jtfMessage.setText("");
			}
		});
		
		jtfName.addFocusListener(new FocusAdapter() {
			@Override
		      public void focusGained(FocusEvent e) {
		        jtfName.setText("");
		      }
		});
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					while(true) {
						if(inMessage.hasNext()) {
							String inMes = inMessage.nextLine();
							String clientsInChat = "Users in chat - ";
							if(inMes.indexOf(clientsInChat) == 0) {
								jlNumberOfClients.setText(inMes);
							} else {
								//Вивід повідомлення і перехід на нову лінію
								jtaTextAreaMessage.append(inMes);
								jtaTextAreaMessage.append("\n");
							}
						}
					}
				} catch(Exception e) {
				}
				
			}
		}).start();
		
		//Вихід з клієнту
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					if(!clientname.isEmpty() && clientname != "Input your name: ") {
						outMessage.println(clientname + " has left");
					} else {
						outMessage.println("Anonymous has left");
					}
					outMessage.println("##session##end##");
					outMessage.flush();
					outMessage.close();
					inMessage.close();
					clientSocket.close();
				} catch(IOException ex) {
				}
			}
		});
		
		//Малюємо вікно
		setVisible(true);
	}
	
	public void sendMsg() {
		String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
		//JUST SEND IT!!!
		outMessage.println(messageStr);
		outMessage.flush();
		jtfMessage.setText("");
	}
	
}
