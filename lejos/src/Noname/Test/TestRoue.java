package Noname.Test;

import java.awt.Point;

import Noname.*;
import lejos.utility.Delay;

public class TestRoue {

	public static void tournerEnCarreeG(){
		
		Moteurs m = new Moteurs();
		for(int i = 0; i < 4; i++){
			m.avancer();
			Delay.msDelay(1000);
			m.tourner(90, true, 150);
		}
        
	}
	
	public static void testPalet(){
		Moteurs m = new Moteurs();
		Pince p = new Pince();
		Capteurs c = new Capteurs();
		Strategie s = new Strategie(c, m, p);
		s.AvancerVersPalet(new Point(0,0), new Point(5,5));
	}

	public static void main(String[] args) {
		//tournerEnCarreeG();
		testPalet();
	}

}
