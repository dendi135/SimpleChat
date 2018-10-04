package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.*;

public class Server extends JFrame{

	static final int PORT =  3443; 
	
	private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	
	private JFrame jWindow;
	private JButton jbClickMe;
	
	public Server() {
		
		jWindow = new JFrame();
		jWindow.setSize(300, 100);
		jWindow.setTitle("SimpleChat Server");
		jWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jWindow.setVisible(true);
		
		
		JLabel jTextInfo = new JLabel("Server started!");
		jTextInfo.setVisible(true);
		jWindow.add(jTextInfo, BorderLayout.NORTH);
		jWindow.add(new JLabel("=)"), BorderLayout.SOUTH);
		
		Socket clientSocket = null;
		ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(PORT);
				System.out.println("Server started!");
				while(true) {	
					clientSocket = serverSocket.accept();
					ClientHandler client = new ClientHandler(clientSocket, this);
					clients.add(client);
					new Thread(client).start();
				}
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				try {
					clientSocket.close();
					System.out.println("Server closed");
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	
	}
	
	public void sendMessageToAllClients(String msg) {
		for(ClientHandler o : clients) {
			o.sendMsg(msg);
		}
	}
	
	public void removeClient(ClientHandler client) {
		clients.remove(client);
	}
	
}
