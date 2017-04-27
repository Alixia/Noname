package Noname;

import Noname.API.APIPince;
import Noname.Outils.Constantes;
import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

public class Pince implements APIPince {
 		private EV3LargeRegulatedMotor pince; // Gestion de la pince
 		
	 	private boolean isOpen; // Permet de savoir si la pince est ouverte
	 	final private int vitessePince = 2000; // Vitesse d'ouverture et de fermeture de la pince
	    final private int tempsOuverture = 500; // Temps d'ouverture arbitraire fixe
	    final private int tempsFermeture = (int)(tempsOuverture*0.98); // Vitesse de la pince a la calibration
		
		// Constructeur par defaut
		public Pince(boolean estOuvert){
			this.pince = new EV3LargeRegulatedMotor(Constantes.pince.port());
			this.pince.setSpeed(vitessePince);
			if(!estOuvert)
				this.ouvrirPince();
			this.isOpen = true;
		}

		// Calibration de l'ouverture et fermeture de la pince
		public void calibration(){
			this.pince.setSpeed(400);
			
			// Message de prevention
			System.out.println("Calibration de la pince");
			System.out.println("Pinces ouvertes ? (Oui : RIGHT/ Non : LEFT)");
			
			// Tant que ce n'est ni LEFT ni RIGHT, redemander
			boolean reAsk = true;
			while(reAsk){
				Button.waitForAnyPress();
				if(Button.LEFT.isDown()){
					System.out.println("Appuyez sur OK");
					pince.forward(); // Ouvrir la pince
					Button.ENTER.waitForPressAndRelease();
					pince.stop();
					reAsk = false;
				}else if(Button.RIGHT.isDown()){
					reAsk = false;
				}else{
					System.out.println("Pinces ouvertes ? (Oui : RIGHT/ Non : LEFT)");
				}
			}
			
			// Fermeture de la pince
			System.out.println("Fermeture sur Palet");
			Button.ENTER.waitForPressAndRelease();
			System.out.println("Appuyez sur OK");
			pince.backward(); // Fermer la pince
			Button.ENTER.waitForPress();
			pince.stop();
			
			isOpen = false; // Ouverture a la fin de la calibration
			this.pince.setSpeed(vitessePince);
			ouvrirPince();
		}
		
		// Ouverture de la pince
		public void ouvrirPince(){
			if(!isOpen){ // Uniquement si elle est fermee
				pince.forward();
				Delay.msDelay(tempsOuverture); 
				pince.stop();
			}
			isOpen = true;
		}
		
		// Fermeture de la pince 
		public void fermerPince(){
			if(isOpen){ // Uniquement si elle est ouverte
				pince.backward();
				Delay.msDelay(tempsFermeture); 
				pince.stop();
			}
			isOpen = false;
		}
}
