package Noname;

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
	public void tourner(float degres, boolean aGauche, double vitesse);

	// Methodes pour la pince
	public void fermer();
	public void ouvrir();	
	public void actionFermer();
	public void actionOuvrir();
	
}
