package Noname.API;

public interface APIPince {
	/* Il faut savoir que le constructeur prend un booleen
	 * qui represente si la pince est ouverte ou non
	 * (envoyer true si elle est ouverte, false sinon).
	 * Si la pince est fermee, il va tenter de l'ouvrir.
	 */
	
	/**
	 * effectue la calibration pour l'ouverture et la fermeture des pinces
	 */
	public void calibration();
	
	/**
	 * ouvrre les pinces
	 */
	public void ouvrirPince();
	
	/**
	 * ferme les pinces
	 */
	public void fermerPince();
	
}
