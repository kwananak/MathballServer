// Tous droits r?serv?s, copyright 2022 Dominic Daoust

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
	
	private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	private static GamesHandler gamesHandler = new GamesHandler();
	private static Logger log = new Logger("src/main/resources/logs/log.txt");
	private static int clientID = 0;
	
	public static void main(String[] args) throws IOException {
		try (ServerSocket ss = new ServerSocket(5565)) {			
			log.printLog("serverStarted");			
			while(true) {
				clients.add(new ClientHandler(ss.accept(), clientID, gamesHandler));
				log.printLog("accepted connection " + clients.get(clients.size()-1));
				clients.get(clients.size()-1).start();
				clientID++;
			}
		}
	}
	
}