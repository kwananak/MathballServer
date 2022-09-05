package logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
	File log;
	
	public Logger(String str) {
		log = new File(str);
	}
	
	public void printLog(String str) {
		try {
			FileWriter fw = new FileWriter(log, true);		
			PrintWriter pw = new PrintWriter(fw);
				  
		    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		    LocalDateTime logTime = LocalDateTime.now();
		    String formattedTime = logTime.format(timeFormatter);
		    	        
		    pw.println(formattedTime + " " + str);
		    System.out.println(str);
			pw.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
	
}