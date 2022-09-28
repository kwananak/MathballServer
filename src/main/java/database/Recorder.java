package database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Recorder {
	
	public static PlayerCard checkForPlayerCard(PlayerCard card) {
		try {
			String[] existingCard = new String(Files.readAllBytes(Paths.get("src/main/resources/playerBase/" + card.getName()))).split(",");
			existingCard[3] =  existingCard[3].trim();
			return new PlayerCard(existingCard[0], existingCard[1], existingCard[2], existingCard[3]);
		} catch (IOException e) {
			savePlayerCard(card);
			return card;
		}
	}
	
	public static void savePlayerCard(PlayerCard card) {
		try {
			FileWriter fw = new FileWriter("src/main/resources/playerBase/" + card.getName());		
			PrintWriter pw = new PrintWriter(fw);        
		    pw.println(card);
			pw.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
			
}
