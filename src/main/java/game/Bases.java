package game;

public class Bases {
	
	private Player[] home = new Player[2];
	private Player[] first = new Player[2];
	private Player[] second = new Player[2];
	private Player[] third = new Player[2];
	private Player[] mount = new Player[2];

	public void setFieldHome(Player[] fieldTeam) {
		mount[0] = fieldTeam[0];
		home[0] = fieldTeam[1];
		first[0] = fieldTeam[2];
		second[0] = fieldTeam[3];
		third[0] = fieldTeam[4];
	}
	
	public Player getPitcher() {
		return mount[0];
	}
	
	public void setHitter(Team t) {
			home[1] = t.getPlayers()[t.cycleBattingNumber()];
	}
	
	public Player getHitter() {
		return home[1];
	}
	
	public Player getPlayer(int base, int team){
		if (base == 0) {
			return home[team];
		} else if (base == 1) {
			return first[team];
		} else if (base == 2) {
			return second[team];
		} else if (base == 3) {
			return third[team];
		} else {
			return mount[team];
		}
	}
	
	public void removeRunner(int base){
		if (base == 1) {
			first[1] = null;
		} else if (base == 2) {
			second[1] = null;
		} else if (base == 3) {
			third[1] = null;
		}
	}
	
	public int cycleBases(int hit, int base) {
		int score = 0; 
		if (base == 0) {
			for (int i = 0; i < hit; i++) {
				if (third[1] != null) {score++;}
				third[1] = second[1];
				second[1] = first[1];
				first[1] = home[1];
				clearBatter();
			}
		} else {
			for (int i = 0; i < hit; i++) {
				if (third[1] != null) {score++;}
				third[1] = second[1];
				second[1] = first[1];
				first[1] = null;
			}
		}
		return score;
	}
	
	public void clearBatter() {
		home[1] = null;
	}
	
	public void clearBases() {
		home = new Player[2];
		first = new Player[2];
		second = new Player[2];
		third = new Player[2];
		mount = new Player[2];
	}
	
}
