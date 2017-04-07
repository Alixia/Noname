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

import Noname.API.APIMoteurs;
import Noname.Outils.Constantes;
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
    private float vitessePince;
    
    
    /*
     * Constructeur pour manipuler les roues et les pinces
     */
    public Moteurs(float vP){
    	
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

	public boolean bouge(){
		return true;
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
