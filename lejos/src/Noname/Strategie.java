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
	
	public void recupererPalet(Point position){
		moteurs.revenirDroit(200);
	}
	
}
