package server;

import java.net.*;
import java.io.*;
import java.util.*;
import game.Game;
import logger.Logger;

public class Server {
	
	private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	private static ArrayList<Game> games = new ArrayList<Game>();
	static Logger log = new Logger("src/main/resources/logs/log.txt");
	
	public static void main(String[] args) throws IOException {
		try (ServerSocket ss = new ServerSocket(5565)) {
			int clientID = 0;
			int gameID = 0;
			log.printLog("serverStarted");
			
			
			while(true) {
				clients.add(new ClientHandler(ss.accept(), clientID));
				log.printLog("accepted connection " + clients.get(clients.size()-1));
				new Thread(clients.get(clients.size()-1)).start();
				clientID++;
				if(clients.size() == 1 || games.get(games.size()-1).getFull()) {
					games.add(new Game(gameID));
					log.printLog("game " + gameID + " started");
					gameID++;
					games.get(games.size()-1).start();
				}
				games.get(games.size()-1).addPlayer(clients.get(clients.size()-1));
			}
		}
	}
	
}