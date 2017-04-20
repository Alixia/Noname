package Noname.Test;

import java.awt.Point;

import Noname.*;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class TestRoue {
	
	public static void tournerEnCarreeG(){
		
		Moteurs m = new Moteurs();
		for(int i = 0; i < 4; i++){
			m.avancer();
			Delay.msDelay(3000);
			m.tourner(90, true, 150);
		}
        
	}
	
	

	public static void main(String[] args) {
		tournerEnCarreeG();
		
	}

}
