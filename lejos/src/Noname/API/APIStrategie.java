package Noname.API;

import java.awt.Point;

public interface APIStrategie {

	/**
	 * permet de gerer la calibration, notamment enregistre si on est à droite ou à gauche
	 */
	public void calibration();
	
	/**
	 * permet d'initialiser tout les attributs utiles en fonctin des calibrations
	 */
	public void intialisation();
	
	/**
	 * permet de faire la premiere action en esquivant les autres palets et le robot
	 */
	public void ramenerPremierPalet();
	
	/**
	 * a partir d'une position d'un robot oriente celui-ci en direction du point destination
	 * @param positionRobot
	 * @param destination
	 */
	public void seDirigerVers(Point positionRobot, Point destination);
	
	/**
	 * permet 
	 * @return le palet le plus proche du robot
	 */
	public Point detecterPlusProchePallet();
	
	public boolean allerChercherPallet(Point pallet);
	
	public void miseAJour();
	
	public void rentrerALaMaison();
	
	public void lancerCam();
	
	public void afficherTableaux();
	
	public void run();
	
}
