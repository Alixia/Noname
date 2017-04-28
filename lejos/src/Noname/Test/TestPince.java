package Noname.Test;

import Noname.Pince;
import lejos.utility.Delay;

public class TestPince {

	// Test de la pince basique
	public static void main(String[] args) {
		Pince P = new Pince(true);
		// Test de la calibration des pinces sur le palet
        P.calibration();
		Delay.msDelay(1000);
		// Dans ce test, nous essayons d'ouvrir et de fermer les pinces plusieurs fois
		// d'affilee, puisque le programme est cense gerer ce genre de situations
		// afin de ne pas se retrouver dans une situation non voulue
		for (int i = 0; i < 10; i++) {
			P.fermerPince();
			P.fermerPince();
			P.ouvrirPince();
			P.ouvrirPince();
		}
       
	}

}
