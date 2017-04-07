package Noname.Test;

import Noname.Moteurs;
import lejos.utility.Delay;

public class TestRoue {

	public static void tournerEnCarreeG(){
		
		Moteurs m = new Moteurs(true, 50);
		for(int i = 0; i < 4; i++){
			m.avancer();
			Delay.msDelay(1000);
			m.tourner(90, true, 150);
		}
        
	}

	public static void main(String[] args) {
		tournerEnCarreeG();
		
	}

}
