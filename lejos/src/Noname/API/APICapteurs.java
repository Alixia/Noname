package Noname.API;

import java.io.FileNotFoundException;
import java.io.IOException;

import Noname.Outils.Couleur;

public interface APICapteurs {

	/**
	 * Savoir si le bouton poussoir est pressé
	 * @return true si le bouton poussoir est pressé
	 */
	public boolean boutonEstPresse();
	
	/**
	 * permet de savoir si on objet en devant le robot et de connaitre sa distance
	 * @return la distance de l'objet au robot
	 */
	public float distanceVision();
	
	/**
	 * permet de mettre a jour une couleur donne en parametre
	 * @param couleur
	 */
	public void calibrerCouleur(Couleur couleur);
	
	/**
	 * permet de savoir sur quel couleur le robot est positionné
	 * @return la couleur en dessous du robot
	 */
	public Couleur getCurrentColor();
	
	/**
	 * allume la lumière pour la detection des couleurs
	 */
	public void lightOn();
	
	/**
	 * allume la lumière pour la detection des couleurs
	 */
	public void lightOff();
	
	/**
	 * ermet de modifier le tableau des couleurs
	 * @param couleurs
	 */
	public void setCalibration(float[][] couleurs);
	
	/**
	 * 
	 * @return le tableau correspondant aux couleurs
	 */
	public float[][] getCalibration();
	
	/**
	 * effectue la calibration des couleurs
	 */
	public void calibration();
	
	/**
	 * Sauvegarde la calibration dans un fichier
	 * @throws IOException
	 */
	public void sauvegarderCalibration() throws IOException;
	
	/**
	 * Charge la calibration du fichier de configuration si elle existe
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void chargerCalibration() throws FileNotFoundException, IOException, ClassNotFoundException;
}
