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
	
	public Player[] getHome(){
		return home;
	}
	
	public Integer cycleBases(Integer hit) {
		int score = 0; 
		for (int i = 0; i < hit; i++) {
			if (third[1] != null) {score++;}
			third[1] = second[1];
			second[1] = first[1];
			first[1] = home[1];
			clearBatter();
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
