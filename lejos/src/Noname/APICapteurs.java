package Noname;

public interface APICapteurs {

	/*
	 * bouton poussoir
	 */
	public boolean boutonEstPresse();
	
	/*
	 * ultrason
	 */
	public float detecteObjet();
	
	/*
	 * colorimetre
	 */
	public Couleur couleur();
	
}
