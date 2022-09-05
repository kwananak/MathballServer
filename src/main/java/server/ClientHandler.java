package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread{
	
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private int clientID;
	private static ArrayList<String> inputs = new ArrayList<String>();
	private String storedIn = "";
	Server server;
	
	public ClientHandler(Socket clientSocket, int IDFromServer) throws IOException {
		this.client = clientSocket;
		this.clientID = IDFromServer;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
	}

	public void run() {
		System.out.println("ClientHandlerstarted" + clientID);
		Receiver receiver = new Receiver();
		receiver.start();			
	}

	public class Receiver extends Thread{
		
		public void run() {
			System.out.println("Receiver client " + clientID + "  started");
			try {
				while (true) {
					storedIn = in.readLine();
					inputs.add(storedIn);
					System.out.println("client " + clientID + " says: " + storedIn);
					if(storedIn==null) {
						in.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}				
	}
	
	public void sender(String msg) {
		out.println(msg);	
	}
	
	public void clearStoredIn() {
		storedIn = "";
	}
	
	public String getStoredIn() {
		return storedIn;
	}
	
	public String getClientID() {
		return Integer.toString(clientID);
	}
	
}
