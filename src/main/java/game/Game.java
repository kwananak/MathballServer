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
	private Team evens = new Team();
	private Team odds = new Team();
	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> answers = new ArrayList<>();	
	private Bases bases = new Bases();
	private ArrayList<Integer> pitch = new ArrayList<>();
	private Logger log;	
	private boolean full = false;
	
	public Game(int IDFromServer) {
		gameID = IDFromServer;
		log = new Logger("src/main/resources/logs/logGame" + Integer.toString(IDFromServer) + ".txt");
	}
	
	public void run() {
		waitForPlayers();
		log.printLog("game " + Integer.toString(gameID) + " started with " + Integer.toString(players.size()) + " players");
		makeTeams();
		setMaxInnings();
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
	
	public void waitForPlayers() {
		log.printLog("waiting on players for 3 secs");
		int i = 0;
		
		while (true) {
			i++;		
			if (players.size() > 9 || (players.size() > 1 && i > 3000)) {
				break;
			} else {
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}		
		full = !full;
	}
	
	public void makeTeams() {
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
	
	public void setMaxInnings() {
		Integer numInns = 0;		
		massSend("command:umpire:How many Innings?");
		massSend("command:sender:How many Innings?");
		getAnswersFromHandlers();
		for(ArrayList<Integer> i : answers) {
			numInns += i.get(1);
		}
		maxInnings = numInns / answers.size();
		log.printLog("maxInnings : " + Integer.toString(maxInnings));
		massSend("command:umpire: " + Integer.toString(maxInnings) + " innings");				
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public String getAnswerFromMount(Player player) {		
		ClientHandler ch = player.getClientHandler();
		ch.sender("command:pitch:choose a pitch");
		while (true) {
			try {sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			if(!ch.getStoredIn().equals("")) {
				String choice= ch.getStoredIn();
				ch.clearStoredIn();
				ch.sender("command:umpire: ");
				return choice;
			}
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
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		massSend("command:turnStart:" + strikes + "," + balls + "," + outs  + "," + inning + "," + scoreEvens + " - " + scoreOdds + "," + topStr + ",false");
		if(top) {
			bases.setHitter(evens);
		} else {
			bases.setHitter(odds);
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		while (true) {
			massSend("command:startLoop:" + strikes + "," + balls + "," + outs  + "," + inning + "," + scoreEvens + " - " + scoreOdds + "," + topStr + ",false");			
			pitch = getPitch(getAnswerFromMount(bases.getPitcher()));
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			massSend("command:umpire:" + pitch.get(0) + " * " + pitch.get(1));
			if (bases.getHitter().getClientHandler() != null) {
				log.printLog("sending pitch " + pitch.toString() + " to player " + bases.getHitter().getClientHandler().getClientID() + " from player " + bases.getPitcher().getClientHandler().getClientID());
			} else {
				log.printLog("sending pitch " + pitch.toString() + " to bot from player " + bases.getPitcher().getClientHandler().getClientID());
			}
			sendPitch(pitch);
			log.printLog("swing received : " + answers);
			swing = swingResult(pitch);
			log.printLog(swing);
			if (swing == "hit") {
				break;
			} else {
				strikes++;
				massSend("command:umpire:strike " + Integer.toString(strikes));
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}				
				if (strikes == 2) {
					outs++;
					break;
				}
			}
		}
	}
	
	private String swingResult(ArrayList<Integer> pitch) {
		if (pitch.get(0) * pitch.get(1) == answers.get(0).get(1)) {
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
	}
		
	
	private ArrayList<Integer> getPitch(String str) {
		Random rand = new Random();
		ArrayList<Integer> intPitch = new ArrayList<Integer>();
		
		if (str.equals("0")) {
			intPitch.add(rand.nextInt(5) + 3);
			intPitch.add(rand.nextInt(5) + 3);
			intPitch.add(1);
		} else {
			intPitch.add(rand.nextInt(5) + 8);
			intPitch.add(rand.nextInt(5) + 8);
			intPitch.add(2);
		}
		
		return intPitch; 
	}
	
	private void sendPitch(ArrayList<Integer> pitch) {
		Player[] answerers = new Player[2];
		if (bases.getHome()[0].getClientHandler() == null) {
			if (top) {
				answerers[0] = odds.getPlayers()[evens.cycleRealPlayersBattingNumber()];
			} else {
				answerers[0] = evens.getPlayers()[odds.cycleRealPlayersBattingNumber()];
			}
		} else {
			answerers[0] = bases.getHome()[0];	
		}
		if (bases.getHome()[1].getClientHandler() == null) {
			if (top) {
				answerers[1] = evens.getPlayers()[evens.cycleRealPlayersBattingNumber()];
			} else {
				answerers[1] = odds.getPlayers()[odds.cycleRealPlayersBattingNumber()];
			}
		} else {
			answerers[1] = bases.getHome()[1];	
		}
		answerers[0].getClientHandler().sender("command:sender:" + pitch.get(0) + " * " + pitch.get(1));
		answerers[1].getClientHandler().sender("command:sender:" + pitch.get(0) + " * " + pitch.get(1));
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
				upScore(bases.cycleBases(pitch.get(2)));
				massSend("command:cycleBases:" + pitch.get(2));
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
	
}