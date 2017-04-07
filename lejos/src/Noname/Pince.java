package Noname;

import lejos.hardware.Button;

public class Pince {
		Moteurs m;
	 	private int nbIterations;
		
		// Constructeur
		public Pince(){
			 m = new Moteurs(150);
			 nbIterations = 0;
		}
		
		// Constructeur
		public Pince(int vitessePince){
			 m = new Moteurs(vitessePince);
			 nbIterations = 0;
		}

		// Calibration 
		public void calibration(){
			System.out.println("Appuyez sur entree pour commencer la calibration,");
			System.out.println("Puis appuyez sur entree lorsque la pince est fermee");
			Button.ENTER.waitForPressAndRelease();
			while(!Button.ENTER.waitForPressAndRelease()){
				m.fermer(1);
			}
			System.out.println("Appuyez sur entree lorsque la pince est ouverte");
			while(!Button.ENTER.waitForPressAndRelease()){
				m.ouvrir(1);
				nbIterations++;
			}
		}
}
