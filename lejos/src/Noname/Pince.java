package Noname;

import lejos.robotics.chassis.Chassis;
import org.r2d2.vue.InputHandler;
import org.r2d2.vue.Screen;

import Noname.Outils.Constantes;
import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

public class Pince {
 		private EV3LargeRegulatedMotor pince; // Gestion de la pince
 		
	 	private int nbIterations; // Nombre de pas qu'il faut pour ouvrir/fermer la pince
	 	private boolean isOpen; // Permet de savoir si la pince est ouverte
	    private float vitessePince; // Vitesse d'ouverture et de fermeture de la pince
		
		// Constructeur par defaut
		public Pince(){
			initialisation();
			this.vitessePince = 150;
			this.pince.setSpeed(this.vitessePince);
		}
		
		// Constructeur avec prise en compte de vitesse de la pince vP
		public Pince(int vP){
			 initialisation();
			 this.vitessePince = vP;
			 this.pince.setSpeed(this.vitessePince);
		}
		
		// Initialisation et lancement de la calibration
		public void initialisation(){
			this.vitessePince = 150; // Vitesse agreable pour la calibration
			this.pince = new EV3LargeRegulatedMotor(Constantes.pince.port());
			this.pince.setSpeed(this.vitessePince);
			nbIterations = 0;
			calibration();
		}

		// Calibration de l'ouverture et fermeture de la pince
		public void calibration(){
			InputHandler ih = new InputHandler(new Screen());
			
			// Message de prevention
			System.out.println("Calibration de la pince");
			Button.ENTER.waitForPressAndRelease();
			Delay.msDelay(200); // Attente du realease du bouton 
			
			// Fermeture de la pince
			System.out.println("Appuyez sur OK lorsque la pince est fermee sur un palet");
			while(!ih.enterPressed()) // Tant que l'user n'appuie pas sur OK
				pince.backward(); // Fermer la pince d'un pas
			Delay.msDelay(200); // Attente du realease du bouton 
			
			System.out.println("Appuyez sur OK lorsque la pince est ouverte");
			while(!ih.enterPressed()){ // Tant que l'user n'appuie pas sur OK
				pince.forward(); // Ouvrir la pince d'un pas
				nbIterations++;
			}
			isOpen = true; // La pince est ouverte a la fin de la calibration
		}
		
		// Ouverture de la pince
		public void ouvrirPince(){
			if(!isOpen){ // Uniquement si elle est fermee
				for(int i=0;i<nbIterations;i++){
					pince.forward();
				}
			}
			isOpen = true;
		}
		
		// Fermeture de la pince 
		public void fermerPince(){
			if(isOpen){ // Uniquement si elle est ouverte
				for(int i=0;i<nbIterations;i++){
					pince.backward();
				}
			}
			isOpen = false;
		}
}
