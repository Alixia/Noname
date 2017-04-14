package Noname;

import java.awt.Point;

public class Strategie {

	private Capteurs capteur;
	private Moteurs moteurs;
	private Pince pince;
	
	public Strategie (Capteurs ca, Moteurs m, Pince p){
		this.capteur = ca;
		this.moteurs = m;
		this.pince = p;
	}
	
	public void intialisaton(){
		pince.calibration();
		//capteur.calibration();
		moteurs.calibration();
	}
	
	//position du robot a calculer!!!!
	public void recupererPalet(Point positionRobot, Point positionPalet){
		if(positionPalet.x == positionRobot.x){
			if(positionPalet.y > positionRobot.y){
				if(moteurs.angle() >= 85 && moteurs.angle() <= 95){
					moteurs.demiTour();
				}
			}
		}else{
			if(positionPalet.x < positionRobot.x){
				moteurs.revenirAngleInitial(false, 200);
			}else{
				moteurs.revenirAngleInitial(true, 200);
			}
			double tangenteTeta = Math.abs(positionPalet.y - positionRobot.y)/Math.abs(positionPalet.x-positionRobot.x);
			double teta = Math.atan(tangenteTeta);
			moteurs.tourner(Math.toDegrees(teta), false, 120);
		}
		moteurs.avancer();
	}
	
}
