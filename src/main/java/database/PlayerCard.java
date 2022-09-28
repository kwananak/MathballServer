package database;

import lombok.Data;

@Data
public class PlayerCard {
		
	private String name;
	private String number;
	private String gamesPlayed;
	private String gamesWon;
	
	public PlayerCard(String name, String number) {
		this.name = name;
		this.number = number;
		this.gamesPlayed = "0";
		this.gamesWon = "0";
	}
	
	public PlayerCard(String name, String number, String gamesPlayed, String gamesWon) {
		this.name = name;
		this.number = number;
		this.gamesPlayed = gamesPlayed;
		this.gamesWon = gamesWon;
	}
	
	public String toString() {
		return name + "," + number + "," + gamesPlayed + "," + gamesWon;
	}
	
	public void victory() {
		gamesWon = Integer.toString(Integer.valueOf(gamesWon) + 1);
		gameFinished();
	}
	
	public void gameFinished() {
		gamesPlayed = Integer.toString(Integer.valueOf(gamesPlayed) + 1);
	}
	
}
