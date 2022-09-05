package game;

import server.ClientHandler;

public class RealPlayer implements Player {
	
	private ClientHandler clientHandler;
	
	public RealPlayer(ClientHandler ch) {
		clientHandler = ch;
	}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}
	
}
