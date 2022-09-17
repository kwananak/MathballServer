package server;

import java.util.ArrayList;

import game.Game;

public class GamesHandler {

	private ArrayList<Game> games = new ArrayList<Game>();
	private int gameID = 0;
	
	public void joinGame(ClientHandler clientHandler) {
		if(games.size() == 0 || games.get(games.size()-1).getFull()) {
			games.add(new Game(gameID));
			gameID++;
			games.get(games.size()-1).start();
		}
		games.get(games.size()-1).addPlayer(clientHandler);
	}
		
}
