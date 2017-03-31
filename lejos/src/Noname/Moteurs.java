package Noname;

import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Moteurs implements APIMoteurs {

    private EV3LargeRegulatedMotor roueDroite;
    private EV3LargeRegulatedMotor roueGauche;
    
    private static float maxVitesseGauche = 200;
    private static float maxVitesseDroit = 200;
    private float vitesseDroit;
    private float vitesseGauche;
    
    private EV3LargeRegulatedMotor pince;
    private boolean estOuvert;
    private float vitessePince;
    
    
    /*
     * Constructeur pour manipuler les roues
     */
    public Moteurs(Constantes roueDroite, Constantes roueGauche){
    	this.roueDroite = new EV3LargeRegulatedMotor(roueDroite.port());
    	this.roueGauche = new EV3LargeRegulatedMotor(roueGauche.port());
    	this.vitesseDroit = maxVitesseDroit;
    	this.vitesseGauche = maxVitesseGauche;
    	this.roueDroite.setSpeed(vitesseDroit);
    	this.roueGauche.setSpeed(vitesseGauche);
    }
    
    /*
     * Constructeur pour manipuler les pinces
     */
    public Moteurs(Constantes pince, boolean estOuvert, float vP){
    	this.pince = new EV3LargeRegulatedMotor(pince.port());
    	this.vitessePince = vitessePince;
    	this.pince.setSpeed(this.vitessePince);
    	this.estOuvert = estOuvert;
    }
    
    /*
     * Constructeur pour manipuler les roues et les pinces
     */
    public Moteurs(Constantes roueDroite, Constantes roueGauche, Constantes pince, boolean estOuvert, float vP){
    	this.roueDroite = new EV3LargeRegulatedMotor(roueDroite.port());
    	this.roueGauche = new EV3LargeRegulatedMotor(roueGauche.port());
    	this.vitesseDroit = maxVitesseDroit;
    	this.vitesseGauche = maxVitesseGauche;
    	this.roueDroite.setSpeed(vitesseDroit);
    	this.roueGauche.setSpeed(vitesseGauche);
    	this.pince = new EV3LargeRegulatedMotor(pince.port());
    	this.vitessePince = vP;
    	this.pince.setSpeed(vitessePince);
    	this.estOuvert = estOuvert;
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
		if(estOuvert){
			 for (int i = 0; i < 1; i++){
				 	actionFermer();
		            Delay.msDelay(1000);
		     }
			 estOuvert = !estOuvert;
		}
	}

	@Override
	public void ouvrir() {
		if(!estOuvert){
			 for (int i = 0; i < 1; i++){
				 	actionOuvrir();
		            Delay.msDelay(1000);
		     }
			 estOuvert = !estOuvert;
		}		
	}
	
	@Override
	public void actionFermer() {
		pince.backward();		
	}

	@Override
	public void actionOuvrir() {
		pince.backward();		
	}
	

}
