package game;

public class Team {

	private Player[] players = new Player[5];
	private int numberOfRealPlayers = 0;
	private int battingNumber = 0;
	private int realPlayersBattingNumber = 0;
	
	
	public int getNumberOfRealPlayers() {
		return numberOfRealPlayers;
	}

	public Player[] getPlayers() {
		return players;
	}		
	
	public int cycleBattingNumber() {
		int numberToReturn = battingNumber;
		battingNumber++;
		if (battingNumber > 4) {battingNumber = 0;}
		return numberToReturn;
	}
	
	public int cycleRealPlayersBattingNumber() {
		int numberToReturn = realPlayersBattingNumber;
		realPlayersBattingNumber++;
		if (realPlayersBattingNumber >= numberOfRealPlayers) {realPlayersBattingNumber = 0;}
		return numberToReturn;
	}
	
	public void setRealPlayerSlot(Player player) {
		players[numberOfRealPlayers] = player;
		numberOfRealPlayers++;
	}
	
	public void fillWithBots() {
		for (int i = numberOfRealPlayers; i < 5; i++) {
			players[i] = new BotPlayer();
		}
	}
}
