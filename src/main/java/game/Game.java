package game;


import java.util.*;

import logger.Logger;
import server.ClientHandler;

public class Game extends Thread {
	
	private String swing;
	private int gameID;
	private boolean top = true;
	private int strikes, balls, scoreEvens, scoreOdds, maxInnings, outs = 0;
	private int inning = 1;
	private Team evens, odds;
	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> answers = new ArrayList<>();	
	private Bases bases = new Bases();
	private Pitch pitch;
	private Logger log;	
	private boolean full = false;
	
	public Game(int IDFromServer) {
		gameID = IDFromServer;
		log = new Logger("src/main/resources/logs/logGame" + Integer.toString(IDFromServer) + ".txt");
	}
	
	public void run() {
		Setuper.waitForPlayers(this);
		log.printLog("game " + Integer.toString(gameID) + " started with " + Integer.toString(players.size()) + " players");
		setTeams(Setuper.makeTeams(players));
		Setuper.setMaxInnings(this);
		while(inning <= maxInnings) {
			startInning();
			while(outs < 2) {
				startTurn();
				endTurn();
			}
			endInning();
		}
		endGame();
	}
		
	public void startInning() {
			String topStr;
			if(top) {
				topStr = "true";
			} else {
				topStr = "false";
			}
			massSend("command:jumbotron:" + strikes + "," + balls + "," + outs  + "," + inning + "," + scoreEvens + " - " + scoreOdds + "," + topStr + ",false");
			if (top) {
				bases.setFieldHome(odds.getPlayers());
				massSend("command:inningStart:top");
			} else {
				bases.setFieldHome(evens.getPlayers());
				massSend("command:inningStart:bot");
			}
	}
	
	public void getAnswersFromHandlers() {
		log.printLog("getAnswers game started");
		answers.clear();
		while (answers.size() < players.size()) {
			for (Player player : players) {
				ClientHandler ch = player.getClientHandler();
				if(!ch.getStoredIn().equals("")) {
					ArrayList<Integer> answer = new ArrayList<Integer>();
					answer.add(Integer.valueOf(ch.getClientID()));
					answer.add(Integer.valueOf(ch.getStoredIn()));
					answers.add(answer);
					ch.clearStoredIn();
					log.printLog("got " + answers.get(answers.size()-1).get(1) + " from player " + ch.getClientID());
				}
			}
		}
		log.printLog("closing getAnswers game " + gameID);
	}
	
	public void addPlayer(ClientHandler ch) {
		Player newGuy = new RealPlayer(ch);
		players.add(newGuy);
	}
	
	public boolean getFull() {
		return full;
	}
	
	private void upInning() {
		if(!top) {
			inning++;
		}
		top = !top;
	}
	
	public String getInning() {
		return Integer.toString(inning) + " " + Boolean.toString(top);
	}
	
	private void upScore(Integer i) {
		for (int j = 0; j < i; j++) {
			if(top) {
				scoreEvens++;
			} else {
				scoreOdds++;
			}
		}
	}
	
	public String getScore() {
		return Integer.toString(scoreEvens) + " " + Integer.toString(scoreOdds);
	}
	
	public void startTurn() {
		String topStr;
		if(top) {
			topStr = "true";
		} else {
			topStr = "false";
		}
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		massSend("command:turnStart:" + strikes + "," + balls + "," + outs  + "," + inning + "," + scoreEvens + " - " + scoreOdds + "," + topStr + ",false");
		if(top) {
			bases.setHitter(evens);
		} else {
			bases.setHitter(odds);
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		while (true) {
			massSend("command:startLoop:" + strikes + "," + balls + "," + outs  + "," + inning + "," + scoreEvens + " - " + scoreOdds + "," + topStr + ",false");
			
			pitch = Pitcher.getPitch(bases.getPitcher());
			sendPitch(pitch);
			
			try {Thread.sleep(500);} catch (InterruptedException e1) {e1.printStackTrace();}
			log.printLog("swing received : " + answers);
			swing = swingResult(pitch);
			log.printLog(swing);
			if (swing.equals("hit")) {
				break;
			} else if (swing.equals("strike")){
				strikes++;
				massSend("command:umpire:strike " + Integer.toString(strikes));
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}				
				if (strikes == 2) {
					outs++;
					break;
				}
			} else if (swing.equals("baseCatch")) {
				outs++;
				massSend("command:baseCatch:" + pitch.field);
				bases.removeRunner(pitch.plate);
				if (outs == 2) {
					break;
				}
			} else if (swing.equals("runnerHit")) {
				massSend("command:runnerHit:" + pitch.plate);
				bases.cycleBases(pitch.strength, pitch.plate);
			}
		}
	}

	private String swingResult(Pitch pitch) {
		if (pitch.plate == 0) {
			if (pitch.a * pitch.b == answers.get(0).get(1)) {
				if (answers.get(0).get(0) == 0) {
					massSend("command:umpire:right answer from catcher");
					return "strike";
				} else {
					massSend("command:umpire:right answer from batter");
					return "hit";
				}
			} else {
				if (answers.get(0).get(0) == 0) {
					massSend("command:umpire:wrong answer from catcher");
					return "hit";
				} else {
					massSend("command:umpire:wrong answer from batter");
					return "strike";
				}
			}
		} else {
			if (pitch.a * pitch.b == answers.get(0).get(1)) {
				if (answers.get(0).get(0) == 0) {
					massSend("command:umpire:right answer from base");
					return "baseCatch";
				} else {
					massSend("command:umpire:right answer from runner");
					return "runnerHit";
				}
			} else {
				if (answers.get(0).get(0) == 0) {
					massSend("command:umpire:wrong answer from base");
					return "runnerHit";
				} else {
					massSend("command:umpire:wrong answer from runner");
					return "baseCatch";
				}
			}
		}
	}
	
	private void sendPitch(Pitch pitch) {
		Player[] answerers = new Player[2];
		if (bases.getPlayer(pitch.plate, 0).getClientHandler() == null) {
			if (top) {
				answerers[0] = odds.getPlayers()[evens.cycleRealPlayersBattingNumber()];
			} else {
				answerers[0] = evens.getPlayers()[odds.cycleRealPlayersBattingNumber()];
			}
		} else {
			answerers[0] = bases.getPlayer(pitch.plate, 0);	
		}
		if (bases.getPlayer(pitch.field, 1).getClientHandler() == null) {
			if (top) {
				answerers[1] = evens.getPlayers()[evens.cycleRealPlayersBattingNumber()];
			} else {
				answerers[1] = odds.getPlayers()[odds.cycleRealPlayersBattingNumber()];
			}
		} else {
			answerers[1] = bases.getPlayer(pitch.field, 1);	
		}
		answerers[0].getClientHandler().sender("command:sender:" + pitch.a + "," + pitch.b);
		answerers[1].getClientHandler().sender("command:sender:" + pitch.a + "," + pitch.b);
		answers.clear();
		log.printLog("getAnswers game " + gameID + " started");
		
		while (answers.size() < 2) {
			for (int i = 0 ; i < 2 ; i++) {
				if(!answerers[i].getClientHandler().getStoredIn().equals("")) {
					ArrayList<Integer> answer = new ArrayList<Integer>();
					answer.add(i);
					answer.add(Integer.valueOf(answerers[i].getClientHandler().getStoredIn()));
					answers.add(answer);
					answerers[i].getClientHandler().clearStoredIn();
					log.printLog("got " + answers.get(answers.size()-1).get(1) + " from player " + answerers[i].getClientHandler().getClientID());
				}
			}
		}
	}
	
	private void endTurn() {
		strikes = 0;
		if (outs < 2){
			if (swing.equals("hit")) {
				upScore(bases.cycleBases(pitch.strength, pitch.plate));
				massSend("command:cycleBases:" + pitch.strength);
				try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}
			} else {
				bases.clearBatter();
				massSend("command:clearBatter");
				try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		log.printLog("outs " + outs + " ,score " + scoreEvens + " - " + scoreOdds);
	}
	
	private void endInning() {
		outs = 0;
		bases.clearBases();
		massSend("command:returnBench");
		upInning();
	}
	
	private void endGame() {
		log.printLog("final score " + scoreEvens + " - " + scoreOdds);
		massSend("command:umpire:final score " + scoreEvens + " - " + scoreOdds);
		//save stats
		//close game
	}
	
	public void massSend(String str) {
		for(Player player : players) {
			player.getClientHandler().sender(str);
		}
	}

	public void setTeams(Team[] teams) {
		this.evens = teams[0];
		this.odds = teams[1];
	}

	public void printLog(String string) {
		log.printLog(string);
	}
	
	public void setFull() {
		full = true;
	}
	
	public int getNumberOfPlayers() {
		return players.size();
	}

	public ArrayList<ArrayList<Integer>> getAnswers() {
		return answers;
	}

	public void setMaxInnings(int maxInnings) {
		this.maxInnings = maxInnings;
	}
	
}