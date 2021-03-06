package Noname;

import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import Noname.API.APIMoteurs;
import Noname.Outils.Constantes;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Moteurs implements APIMoteurs, MoveListener {

	//gestion des roues
    private EV3LargeRegulatedMotor rDroite;
    private EV3LargeRegulatedMotor rGauche;
    private MovePilot pilot;
 	private Wheel roueDroite;
 	private Wheel roueGauche;
 	private Chassis chassis;
 	private boolean avance;
    
 	//gestion des vitesses
    private static float maxVitesseRoue = 400;
    private float vitesseRoues;
    
    //gestion de l'angle
    private double angle;
    
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
	
	//angle compris entre 0 et 360
	public void tourner(double i, boolean aGauche, double vitesse) {
		pilot.setAngularSpeed(vitesse);
		if(aGauche){
			pilot.rotate(i*-1);
			this.angle = angle + i ;
		}else{
			pilot.rotate(i);
			this.angle = angle - i;
		}
		while(angle >= 360){
			this.angle -= 360;
		}
		while(angle < 0){
			this.angle += 360;
		}
	}
	
	public void demiTour(){
		tourner(180, true, 120);
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

	public double getAngle(){
		return angle;
	}
	
	public void setAngle(double angle){
		this.angle = angle;
	}
	
	
	public void revenirAngleInitial(boolean face, float vitesse){
		if(face){
			if(angle > 180){
				tourner(360-angle, false, vitesse);
			}else{
				tourner(-angle, false, vitesse);
			}
		}else{
			tourner(180-angle, false, vitesse);
		}
		
	}
	
	public double angleInitial(boolean face){
		if(face){
			if(angle > 180){
				return 360-angle;
			}else{
				return -angle;
			}
		}else{
			return 180-angle;
		}
		
	}

	
}
