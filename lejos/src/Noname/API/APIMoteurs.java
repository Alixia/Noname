package Noname.API;

public interface APIMoteurs {

	/**
	 * modifie la vitesse linaire du robot
	 * @param v la vitesse
	 */
	public void setVitesse(float v);
	
	/**
	 * permet de faire reculer le robot
	 */
	public void reculer();
	/**
	 * permet de faire avancer le robot
	 */
	public void avancer();
	/**
	 * permet d'arrêter le robot
	 */
	public void arreter();
	/**
	 * permet de se tourner le robot de degres° à une vitesse de vitesse
	 * @param degres angle pour tourner 
	 * @param aGauche true si on veut tourner à gauche
	 * @param vitesse vitesse angulaire
	 */
	public void tourner(double degres, boolean aGauche, double vitesse);
	
	
	/**
	 * permet de faire un demi tour, tourne de 180 degres
	 * 
	 */
	public void demiTour();
	
	
	/**
	 * 
	 * @return vrai si le robot est en mouvement
	 */
	public boolean bouge();
	
	/**
	 * 
	 * @return l'angle du robot
	 */
	public double getAngle();
	
	/**
	 *  permet de mettre à jour l'angle du robot
	 * @param angle
	 */
	public void setAngle(double angle);
	
	
	/**
	 * permet de faire tourner le robot jusqu'à l'angle de départ, 0 si face est à true 180 si non
	 * @param face
	 * @param vitesse
	 */
	public void revenirAngleInitial(boolean face, float vitesse);
	
	/**
	 * renvoi l'angle pour que le robot soit de face ou non (parametre) selon l'axe de départ
	 * @param face
	 * @return angle jusqu'à position initiale
	 */
	public double angleInitial(boolean face);
	
}
