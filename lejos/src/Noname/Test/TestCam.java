package Noname.Test;

import Noname.Capteurs;
import Noname.Moteurs;
import Noname.Pince;
import Noname.Strategie;
import lejos.hardware.Button;


public class TestCam {

	public static void main(String[] args) {
		Moteurs m = new Moteurs();
		Pince p = new Pince(true);
		Capteurs c = new Capteurs();
		Strategie s = new Strategie(c, m, p);
		s.lancerCam();

		while (!Button.LEFT.isDown()) {
			s.afficherTableaux();
			
			Button.waitForAnyPress();
			
		}
		
	}

}
