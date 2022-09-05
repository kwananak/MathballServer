package game;

import java.util.*;

import server.ClientHandler;

public class Bases {
	
	private ArrayList<ClientHandler> home = new ArrayList<ClientHandler>();
	private ArrayList<ClientHandler> first = new ArrayList<ClientHandler>();
	private ArrayList<ClientHandler> second = new ArrayList<ClientHandler>();
	private ArrayList<ClientHandler> third = new ArrayList<ClientHandler>();
	private ArrayList<ClientHandler> mount = new ArrayList<ClientHandler>();
	
	private ArrayList<ClientHandler> batOrderEvens;	
	private ArrayList<ClientHandler> batOrderOdds;

	public Bases (ArrayList<ClientHandler> a, ArrayList<ClientHandler> b) {
		batOrderEvens = new ArrayList<ClientHandler>(a);
		batOrderOdds = new ArrayList<ClientHandler>(b);
	}
	
	public void setFieldHome(ArrayList<ClientHandler> p) {
		switch (p.size()) {
			case 1:
				mount.add(p.get(0));
				home.add(p.get(0));
				first.add(p.get(0));
				second.add(p.get(0));
				third.add(p.get(0));
				break;
			case 2: 
				mount.add(p.get(0));
				home.add(p.get(1));
				first.add(p.get(0));
				second.add(p.get(1));
				third.add(p.get(0));
				break;
			case 3: 
				mount.add(p.get(0));
				home.add(p.get(1));
				first.add(p.get(2));
				second.add(p.get(2));
				third.add(p.get(2));
				break;
			case 4: 
				mount.add(p.get(0));
				home.add(p.get(1));
				first.add(p.get(2));
				second.add(p.get(3));
				third.add(p.get(3));
				break;
			case 5: 
				mount.add(p.get(0));
				home.add(p.get(1));
				first.add(p.get(2));
				second.add(p.get(3));
				third.add(p.get(4));
				break;
		}
	}
	
	public ClientHandler getPitcher() {
		return mount.get(0);
	}
	
	public void setHitter(boolean top) {
		if (top) {
			home.add(batOrderEvens.get(0));
			ArrayList<ClientHandler> newOrder = new ArrayList<ClientHandler>();
			newOrder.addAll(batOrderEvens);			
			newOrder.add(newOrder.get(0));
			newOrder.remove(0);
			batOrderEvens = newOrder;
		} else {
			home.add(batOrderOdds.get(0));
			ArrayList<ClientHandler> newOrder = new ArrayList<ClientHandler>();
			newOrder.addAll(batOrderOdds);
			newOrder.add(newOrder.get(0));
			newOrder.remove(0);
			batOrderOdds = newOrder;
		}
	}
	
	public ClientHandler getHitter() {
		return home.get(1);
	}
	
	public ArrayList<ClientHandler> getHome(){
		return home;
	}
	
	public Integer cycleBases(Integer hit) {
		int score = 0; 
		for (int i = 0; i < hit; i++) {
			if (third.size() == 2) {
				score++;
				third.remove(1);
			}
			if (second.size() == 2) {
				third.add(second.get(1));
				second.remove(1);
			}
			if (first.size() == 2) {
				second.add(first.get(1));
				first.remove(1);
			}
			if (home.size() == 2) {
				first.add(home.get(1));
				clearBatter();
			}
		}
		return score;
	}
	
	public void clearBatter() {
		home.remove(1);
	}
	
	public void clearBases() {
		home.clear();
		first.clear();
		second.clear();
		third.clear();
		mount.clear();
	}
	
}
