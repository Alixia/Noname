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
	    final int vitesseDefaut = 250;
		
		// Constructeur par defaut
		public Pince(){
			this.vitessePince = this.vitesseDefaut;
			initialisation();
		}
		
		// Constructeur avec prise en compte de vitesse de la pince vP
		public Pince(int vP){
			 this.vitessePince = vP;
			 initialisation();
		}
		
		// Initialisation et lancement de la calibration
		public void initialisation(){
			this.pince = new EV3LargeRegulatedMotor(Constantes.pince.port());
			this.pince.setSpeed(this.vitessePince);
			nbIterations = 0;
		}

		// Calibration de l'ouverture et fermeture de la pince
		public void calibration(){
			InputHandler ih = new InputHandler(new Screen());
			
			//this.pince.setSpeed(this.vitesseDefaut); // Vitesse agreable pour la calibration
			nbIterations = 0; // Nombre de pas qu'il faut pour ouvrir/fermer
			
			// Message de prevention
			System.out.println("Calibration de la pince");
			Button.ENTER.waitForPressAndRelease();
			Delay.msDelay(1000); // Attente du realease du bouton 
			
			// Fermeture de la pince
			System.out.println("Appuyez sur OK lorsque la pince est fermee sur un palet");
			while(!ih.enterPressed()){ // Tant que l'user n'appuie pas sur OK
				pince.backward(); // Fermer la pince d'un pas
			}
			pince.stop();
			Delay.msDelay(1000); // Attente du realease du bouton 

			System.out.println("Appuyez sur OK lorsque la pince est ouverte");
			while(!ih.enterPressed()){ // Tant que l'user n'appuie pas sur OK
				pince.forward(); // Ouvrir la pince d'un pas
				nbIterations++;
			}
			pince.stop();
			Delay.msDelay(1000); // Attente du realease du bouton 

			isOpen = true; // La pince est ouverte a la fin de la calibration
			
			// 
			//this.nbIterations /= (this.vitessePince/this.vitesseDefaut);
			
			//this.pince.setSpeed(this.vitessePince); // Retablissement de la vitesse voulue
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
