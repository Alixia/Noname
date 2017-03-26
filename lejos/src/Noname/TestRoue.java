package Noname;

import lejos.utility.Delay;

public class TestRoue {

	public static void tournerEnCarreeG(){
		
		Moteurs m = new Moteurs(Constantes.roueDroite, Constantes.roueGauche);
		
        for (int i = 0; i < 5; i++)
        {
            Delay.msDelay(1000);
        }
	}

	public static void main(String[] args) {
		tournerEnCarreeG();
	}

}
