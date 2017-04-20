package Noname.API;

import Noname.Outils.Couleur;

public interface APICapteurs {

	/*
	 * bouton poussoir
	 */
	public boolean boutonEstPresse();
	
	/*
	 * ultrason
	 */
	public float distanceVision();
	
	
}
