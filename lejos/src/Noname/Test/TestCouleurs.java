package Noname.Test;
 

import Noname.Capteurs;
import Noname.Outils.Couleur;
import Noname.Outils.InputHandler;
import Noname.Outils.Screen;

public class TestCouleurs {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Capteurs capteurs = new Capteurs();
		//Screen screen = new Screen();
		//InputHandler input = new InputHandler(screen);

		capteurs.calibration();
		/*for (Couleur c : Couleur.values()) {
			System.out.println("couleur = " + c.name());
			input.waitAny();
			capteurs.calibrerCouleur(c);
			System.out.println("tableur calibrationnage" + tableauString(capteurs.getCalibration()));
		}*/
		// the end

	}

/*	private static String tableauString(float[][] tab) {
		String buffer = "";
		for (int i = 0; i < Couleur.values().length; i++) {
			buffer += " " + tab[i][0];
		}
		return buffer;
	}
*/
}