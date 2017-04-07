package Noname;

import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import lejos.utility.Delay;

import org.r2d2.utils.R2D2Constants;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Moteurs implements APIMoteurs, MoveListener {

    private EV3LargeRegulatedMotor rDroite;
    private EV3LargeRegulatedMotor rGauche;
    private MovePilot pilot;
 	private Wheel roueDroite;
 	private Wheel roueGauche;
 	private Chassis chassis;
    
    private static float maxVitesseRoue = 200;
    private float vitesseRoues;
    private boolean avance;
    
    private EV3LargeRegulatedMotor pince;
    private boolean estOuvert;
    private float vitessePince;
    
    
    /*
     * Constructeur pour manipuler les roues et les pinces
     */
<<<<<<< HEAD
    public Moteurs(float vP){
    	this.roueDroite = new EV3LargeRegulatedMotor(Constantes.roueDroite.port());
    	this.roueGauche = new EV3LargeRegulatedMotor(Constantes.roueGauche.port());
    	this.vitesseDroit = maxVitesseDroit;
    	this.vitesseGauche = maxVitesseGauche;
    	this.roueDroite.setSpeed(vitesseDroit);
    	this.roueGauche.setSpeed(vitesseGauche);
=======
    public Moteurs(boolean estOuvert, float vP){
    	
    	this.rDroite = new EV3LargeRegulatedMotor(Constantes.roueDroite.port());
    	this.rGauche = new EV3LargeRegulatedMotor(Constantes.roueGauche.port());
    	this.vitesseRoues = maxVitesseRoue;
    	avance = false;
    	this.rDroite.setSpeed(vitesseRoues);
    	this.rGauche.setSpeed(vitesseRoues);
    	
    	this.roueGauche = WheeledChassis.modelWheel(this.rGauche, 56).offset(-1*Constantes.distance_centre.centimetre());
    	this.roueDroite = WheeledChassis.modelWheel(this.rDroite, 56).offset(Constantes.distance_centre.centimetre());
    	this.chassis = new WheeledChassis(new Wheel[]{roueGauche, roueDroite},  WheeledChassis.TYPE_DIFFERENTIAL);
    	this.pilot = new MovePilot(chassis);
    	this.pilot.setLinearSpeed(maxVitesseRoue);
    	this.pilot.setAngularSpeed(maxVitesseRoue);
		pilot.addMoveListener(this);
    
>>>>>>> branch 'master' of git://github.com/Alixia/Noname.git
    	this.pince = new EV3LargeRegulatedMotor(Constantes.pince.port());
    	this.vitessePince = vP;
    	this.pince.setSpeed(vitessePince);
    }
    
	public void setVitesse(float v) {
		vitesseRoues = v;
		pilot.setLinearSpeed(v);
	}
	
	
	@Override
	public void reculer() {
		pilot.backward();
	}


	@Override
	public void avancer() {
		pilot.forward();
	}
	
	public void arreter() {
		pilot.stop();
	}
	
	public void tourner(float i, boolean aGauche, double vitesse) {
		pilot.setAngularSpeed(vitesse);
		if(aGauche){
			pilot.rotate(i*-1);
		}else{
			pilot.rotate(i);	
		}
	}

	public void ouvrir(int nbIterations) {
		for (int i = 0; i < nbIterations; i++)
			pince.forward();
	}
	
	public void fermer(int nbIterations) {
		for (int i = 0; i < nbIterations; i++)
			pince.backward();
	}

<<<<<<< HEAD
	
	public void rotate(int i, boolean left, double speed) {
		pilot.setAngularSpeed(speed);
		rotate(i, left, true);
=======
	@Override
	public void actionOuvrir() {
		pince.forward();		
	}

	public boolean bouge(){
		return true;
>>>>>>> branch 'master' of git://github.com/Alixia/Noname.git
	}
	
	@Override
	public void moveStarted(Move event, MoveProvider mp) {
		avance = true;
		
	}

	@Override
	public void moveStopped(Move event, MoveProvider mp) {
		// TODO Auto-generated method stub
		avance = false;
	}

	
}
