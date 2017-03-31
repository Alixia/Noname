package Noname;

public interface APICapteurs {

	/*
	 * bouton poussoir
	 */
	public boolean boutonEstPresse();
	
	/*
	 * ultrason
	 */
	public float distanceVision();
	
	/*
	 * colorimetre
	 */
	public Couleur couleur();
	
}
