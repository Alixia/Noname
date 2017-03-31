package Noname;

import lejos.utility.Delay;

public class PinceTest {

	public static void OuvrirPince(Moteurs m){
		
        for (int i = 0; i < 1; i++){
        	m.ouvrir();
        }
	}
	
	public static void FermerPince(Moteurs m){
		
        for (int i = 0; i < 1; i++){
        	m.fermer();
        }
	}

	public static void main(String[] args) {
		Moteurs m = new Moteurs(Constantes.pince, true, 150);
        FermerPince(m);
        OuvrirPince(m);
	}

}
