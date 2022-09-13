package game;

import java.util.ArrayList;

public abstract class Setuper {
	
	public static void waitForPlayers(Game game) {
		game.printLog("waiting on players for 3 secs");
		int i = 0;		
		while (true) {
			i++;		
			if (game.getNumberOfPlayers() > 9 || (game.getNumberOfPlayers() > 1 && i > 3000)) {
				break;
			} else {
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}		
		game.setFull();
	}
	
	public static Team[] makeTeams(ArrayList<Player> players) {
		Team evens = new Team();
		Team odds = new Team();		
		for(int j = 0; j < players.size(); j++) {
			if (j%2 == 0) {
				evens.setRealPlayerSlot(players.get(j));
				players.get(j).getClientHandler().sender("command:team:true");
			} else {
				odds.setRealPlayerSlot(players.get(j));
				players.get(j).getClientHandler().sender("command:team:false");
			}
		}
		evens.fillWithBots();
		odds.fillWithBots();		
		Team[] teams = {evens, odds};
		return teams;
	}
	
	public static void setMaxInnings(Game game) {
		Integer numInns = 0;
		game.massSend("command:inningSender:how many innings?");
		game.getAnswersFromHandlers();
		for(ArrayList<Integer> i : game.getAnswers()) {
			numInns += i.get(1);
		}
		int maxInnings = numInns / game.getAnswers().size();
		game.setMaxInnings(maxInnings);
		game.printLog("maxInnings : " + Integer.toString(maxInnings));
		game.massSend("command:startGame: " + Integer.toString(maxInnings) + " innings");				
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
}
