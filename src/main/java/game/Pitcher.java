package game;

import server.ClientHandler;

public abstract class Pitcher {
	
	private static String getAnswerFromMount(Player player) {		
		ClientHandler ch = player.getClientHandler();
		ch.sender("command:pitch:choose a pitch");
		while (true) {
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			if(!ch.getStoredIn().equals("")) {
				String choice= ch.getStoredIn();
				ch.clearStoredIn();
				ch.sender("command:umpire: ");
				return choice;
			}
		}
	}
	
	public static Pitch getPitch(Player player) {		
		int choice = Integer.valueOf(getAnswerFromMount(player));
		Pitch pitch = new Pitch(choice);;		
		return pitch; 
	}
	
}
