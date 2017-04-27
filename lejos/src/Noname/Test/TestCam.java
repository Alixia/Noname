package Noname.Test;

import org.r2d2.vue.InputHandler;
import org.r2d2.vue.Screen;

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
