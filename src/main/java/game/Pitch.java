package game;

import java.util.Random;

public class Pitch {

		public int plate, field, a, b, strength;
		
		public Pitch(int i) {
			Random rand = new Random();
			if (i == 0) {
				a = rand.nextInt(5) + 3;
				b = rand.nextInt(5) + 3;
				plate = 0;
				field = 0;
				strength = 1; 
			} else if (i == 4) {
				a = rand.nextInt(5) + 8;
				b = rand.nextInt(5) + 8;
				plate = 0;
				field = 0;
				strength = 2;
			} else {
				a = rand.nextInt(8) + 4;
				b = rand.nextInt(8) + 4;
				plate = i;
				field = i;
				strength = 1;
			}
		}
			
}
