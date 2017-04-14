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
import lejos.hardware.Button;
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
    
    private float angle;
    
    /*
     * Constructeur pour manipuler les roues et les pinces
     */
    public Moteurs(){
    	
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
		
		this.angle = 0;
    }
    
    public void calibration(){
    	System.out.println("Calibration de l'angle du robot");
		Button.ENTER.waitForPressAndRelease();
		Delay.msDelay(200); // Attente du realease du bouton
		System.out.println("Appuyez sur le bouton quand le robot est � la position de base");
		Button.ENTER.waitForPressAndRelease();
		this.angle = 0;
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
			this.angle = angle - i ;
		}else{
			pilot.rotate(i);
			this.angle = angle + i ;
		}
	}

	public boolean bouge(){
		return avance;
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

	public float angle(){
		return angle;
	}
	
	public void revenirDroit(float vitesse){
		tourner(angle, true, vitesse);
	}

	
}
