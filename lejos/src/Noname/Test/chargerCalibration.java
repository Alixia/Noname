package Noname.Test;

import java.io.IOException;

import Noname.Capteurs;
import Noname.Outils.InputHandler;
import Noname.Outils.Screen;

public class chargerCalibration {
	public static void main(String[] args) {
		Capteurs capteurs = new Capteurs();
		Screen screen = new Screen();
		InputHandler input = new InputHandler(screen);

		try {
			capteurs.chargerCalibration();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("appuyez sur un bouton pour tester la couleur");
		while(true){
			input.waitAny();
			System.out.println(capteurs.getCurrentColor().name());
		}
	}
}
