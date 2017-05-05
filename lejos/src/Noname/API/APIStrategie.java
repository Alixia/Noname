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
	 * Permet de detecter le palet le plus proche du robot
	 * @return les coordonnees du palet le plus proche de notre robot
	 */
	public Point detecterPlusProchePallet();
	
	/**
	 * Avance jusqu'au palet et le prend en pince
	 * @param pallet
	 * @return vrai si le bouton poussoir est presse, faux sinon
	 */
	public boolean allerChercherPallet(Point pallet);
	
	/**
	 * 
	 *  
	 */
	/**
	 *  Met a jour les tableau palet et robot et l'angle
	 * @param nbIter permet de savoir combien de fois on l'appel (utile pour la gestion de l'angle)
	 */
	public void miseAJour(int nbIter);
	
	/**
	 * Ramene le palet dans les cages
	 */
	public void rentrerALaMaison();
	
	/**
	 * Lance la camera sur un thread
	 */
	public void lancerCam();
	
	/**
	 * Permet d'afficher les tableaux de coordonnees des palets
	 */
	public void afficherTableaux();
	
	/**
	 * Machine a etats permettant de gerer le chemin, la gestion de la pince de l'acquisition d'un palet dans les cages
	 */
	public void run();
	
}
