package Noname;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Moteur implements APIMoteur {

    private EV3LargeRegulatedMotor roueDroite;
    private EV3LargeRegulatedMotor roueGauche;
    
    private float vitesseDroit;
    private float vitesseGauche;
    
    
    public Moteur(EV3LargeRegulatedMotor roueDroite, EV3LargeRegulatedMotor roueGauche){
    	this.roueDroite = roueDroite;
    	this.roueGauche = roueGauche;
    }
    
	@Override
	public void setVitesse(float v) {
		setVitesseG(v);
		setVitesseD(v);		
	}
	public void setVitesseG(float v) {
		vitesseGauche = v;
		roueGauche.setSpeed(v);
	}
	
	public void setVitesseD(float v) {
		vitesseDroit = v;
		roueDroite.setSpeed(v);
	}
	
	
	@Override
	public void reculer() {
		reculerG();
		reculerD();
	}

	public void reculerG() {
		roueGauche.backward();
	}
	
	public void reculerD() {
		roueDroite.backward();		
	}

	@Override
	public void avancer() {
		avancerG();
		avancerD();
	}

	public void avancerG() {
		roueGauche.forward();
	}
	
	public void avancerD() {
		roueDroite.forward();		
	}
	@Override
	public void tournerDroite() {
		avancerG();
		reculerD();
	}

	@Override
	public void tournerGauche() {
		avancerD();
		reculerG();
		
	}

	@Override
	public void fermer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ouvrir() {
		// TODO Auto-generated method stub
		
	}

}
