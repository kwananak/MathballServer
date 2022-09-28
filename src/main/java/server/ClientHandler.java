package server;

import java.io.*;
import java.net.*;
import java.util.*;

import database.PlayerCard;
import database.Recorder;

public class ClientHandler extends Thread{
	
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private int clientID;
	private static ArrayList<String> inputs = new ArrayList<String>();
	private String storedIn = "";
	private GamesHandler gamesHandler;
	private PlayerCard playerCard;
	
	public ClientHandler(Socket clientSocket, int IDFromServer, GamesHandler gamesHandler) throws IOException {
		this.client = clientSocket;
		this.clientID = IDFromServer;
		this.gamesHandler = gamesHandler;
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
	}

	public void run() {
		System.out.println("ClientHandlerstarted" + clientID);
		try {
			Thread.sleep(1000);
			out.println("command:menu: ");
			while (true) {
				System.out.println("menu sent");
				String str = in.readLine();
				if (!str.equals("ready")) {
					playerCard = Recorder.checkForPlayerCard(new PlayerCard(str, "3"));
					out.println("command:menu:" + playerCard);
				} else if (str.equals("ready")) {
					break;
				}
			}
			gamesHandler.joinGame(this);
			while (true) {
				storedIn = in.readLine();
				inputs.add(storedIn);
				System.out.println("client " + clientID + " says: " + storedIn);
				if(storedIn==null) {
					in.close();
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
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
	
	public PlayerCard getPlayerCard() {
		return playerCard;
	}
	
}
