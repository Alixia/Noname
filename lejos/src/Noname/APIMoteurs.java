package Noname;

public interface APIMoteurs {

	public void setVitesse(float v);
	
	public void reculer();
	public void avancer();
	public void tourner(float i, boolean aGauche, double vitesse);

	// Methodes pour la pince
	public void fermer();
	public void ouvrir();	
	public void actionFermer();
	public void actionOuvrir();
	
}
