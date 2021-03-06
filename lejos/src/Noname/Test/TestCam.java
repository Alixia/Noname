package Noname.Test;

import Noname.Camera;
import Noname.Capteurs;
import Noname.Moteurs;
import Noname.Pince;
import Noname.Strategie;
import lejos.hardware.Button;


public class TestCam {

	
	public static void suivreObjets(){

		Moteurs m = new Moteurs();
		Pince p = new Pince(true);
		Capteurs c = new Capteurs();
		Strategie s = new Strategie(c, m, p, true);
		s.lancerCam();
		while (!Button.LEFT.isDown()) {
			s.afficherTableaux();
			Button.waitForAnyPress();
		}
		
	}
	

	public static void afficherCam() {
		Camera c = new Camera();
		Thread t = new Thread(c);
		t.run();
		
	}
	
	public static void main(String[] args) {
		
		//suivreObjets();
		afficherCam();
	}

}
