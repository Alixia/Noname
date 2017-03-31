package Noname;

import lejos.hardware.port.Port;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

import org.r2d2.utils.R2D2Constants;

import lejos.hardware.ev3.LocalEV3;
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
    
    
    private MovePilot pilot;
	private Wheel left;
	private Wheel right;
	private Chassis chassis;
    
    
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
    	
    	left      = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(LocalEV3.get().getPort(R2D2Constants.LEFT_WHEEL)), 56).offset(-1*R2D2Constants.DISTANCE_TO_CENTER);
		right     = WheeledChassis.modelWheel(new EV3LargeRegulatedMotor(LocalEV3.get().getPort(R2D2Constants.RIGHT_WHEEL)), 56).offset(R2D2Constants.DISTANCE_TO_CENTER);
		chassis   = new WheeledChassis(new Wheel[]{left, right},  WheeledChassis.TYPE_DIFFERENTIAL);
		pilot     = new MovePilot(chassis);
    }
    
    /*
     * Constructeur pour manipuler les pinces
     */
    public Moteurs(Constantes pince, boolean estOuvert, float vP){
    	this.pince = new EV3LargeRegulatedMotor(pince.port());
    	this.vitessePince = vP;
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
			 for (int i = 0; i < 5; i++){
				 	actionFermer();
		            Delay.msDelay(1000);
		     }
			 estOuvert = !estOuvert;
		}
	}

	@Override
	public void ouvrir() {
		if(!estOuvert){
			 for (int i = 0; i < 5; i++){
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
		pince.forward();		
	}
	
	public void rotate(int i, boolean left, double speed) {
		pilot.setAngularSpeed(speed);
		rotate(i, left, true);
	}
	
	public void rotate(float i, boolean left, boolean correction) {
		if(correction)
			i = i - (i * R2D2Constants.PR_ANGLE_CORRECTION);
		if(left){
			pilot.rotate(i*-1, true);
		}else{
			pilot.rotate(i, true);	
		}
	}

}
