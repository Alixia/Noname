package Noname;

import lejos.utility.Delay;

public class PinceTest {

	public static void OuvrirPince(Moteurs m){
		
		Moteurs m = new Moteurs(Constantes.roueDroite, Constantes.roueGauche);
		
        for (int i = 0; i < 2; i++){
        	m.actionOuvrir();
            Delay.msDelay(1000);
        }
	}
	
	public static void FermerPince(Moteurs m){
		
		Moteurs m = new Moteurs(Constantes.roueDroite, Constantes.roueGauche);
		
        for (int i = 0; i < 2; i++){
        	m.actionFermer();
            Delay.msDelay(1000);
        }
	}

	public static void main(String[] args) {
		Moteurs m = new Moteurs(Constantes.pince, true, 1.0);
        OuvrirPince(m);
        FermerPince(m);
	}

}
