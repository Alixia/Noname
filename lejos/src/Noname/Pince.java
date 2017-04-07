package Noname;

import org.r2d2.vue.InputHandler;
import org.r2d2.vue.Screen;

import lejos.hardware.Button;
import lejos.utility.Delay;

public class Pince {
		public Moteurs m;
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
			Delay.msDelay(1000);
			InputHandler ih = new InputHandler(new Screen());
			while(!ih.enterPressed()){
				m.fermer(1);
			}
			Delay.msDelay(1000);
			System.out.println("Appuyez sur entree lorsque la pince est ouverte");
			while(!ih.enterPressed()){
				m.ouvrir(1);
				nbIterations++;
			}
		}
}
