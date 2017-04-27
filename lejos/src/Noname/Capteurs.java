package Noname;
 

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import Noname.API.APICapteurs;
import Noname.Outils.Constantes;
import Noname.Outils.Couleur;
import Noname.Outils.InputHandler;
import Noname.Outils.Screen;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;

public class Capteurs implements APICapteurs{
	
	private EV3TouchSensor boutonPoussoir;
	private EV3ColorSensor colorimetre;
	private EV3UltrasonicSensor ultrasons;
	private float[][] couleurs;

	public Capteurs(){
		this.boutonPoussoir = new EV3TouchSensor(Constantes.boutonPoussoir.port());
		this.colorimetre = new EV3ColorSensor(Constantes.colorimetre.port());
		this.ultrasons = new EV3UltrasonicSensor(Constantes.ultrasons.port());
		couleurs = new float[Couleur.values().length][1];
	}
	
	
	public boolean boutonEstPresse() {
		float[] sample = new float[1];
        boutonPoussoir.fetchSample(sample, 0);

        return sample[0] != 0;
	}

	
	public float distanceVision() {
		float[] echantillon = new float[1];
		ultrasons.fetchSample(echantillon, 0);
		return echantillon[0];
	}

	
	public void calibrerCouleur(Couleur couleur){
		SampleProvider moyenne = new MeanFilter(colorimetre.getRGBMode(), 1);
		couleurs[couleur.ordinal()] = new float[moyenne.sampleSize()];
		moyenne.fetchSample(couleurs[couleur.ordinal()], 0);
	}
	public Couleur getCurrentColor(){
		SampleProvider moyenne = new MeanFilter(colorimetre.getRGBMode(), 1);
		float[]        echantillon  = new float[moyenne.sampleSize()];
		double         distance = Double.MAX_VALUE;
		Couleur        couleur   = Couleur.rouge;

		moyenne.fetchSample(echantillon, 0);

		for(Couleur c : Couleur.values()){
				double scalaire = scalaire(echantillon, couleurs[c.ordinal()]);
				if (scalaire < distance) {
					distance = scalaire;
					couleur = c;
			}
		}
		return couleur;
	}

	private double scalaire(float[] v1, float[] v2) {
		return Math.sqrt (Math.pow(v1[0] - v2[0], 2.0) +
				Math.pow(v1[1] - v2[1], 2.0) +
				Math.pow(v1[2] - v2[2], 2.0));
	}
	
	public void lightOn(){
		colorimetre.setFloodlight(Color.WHITE);
	}
	
	public void lightOff(){
		colorimetre.setFloodlight(false);
	}
	
	public void setCalibration(float[][] couleurs){
		this.couleurs = couleurs;
	}
	
	public float[][] getCalibration() {
		return couleurs;
	}
	
	public void calibration(){
		InputHandler ih = new InputHandler(new Screen());
		System.out.println("début de la calibration des couleurs");
		for(Couleur c : Couleur.values()){
			System.out.println("calbirer " + c.name());
			System.out.println("appuyez sur Entrer pour valider");
			ih.waitAny();
			calibrerCouleur(c);
		}
		System.out.println("calibration terminée");
	}

	/**
	 * Sauvegarde la calibration
	 * @throws IOException
	 */
	public void sauvegarderCalibration() throws IOException {
		File file = new File("calibration");
		if(file.exists()){
			file.delete();
		}
		file.createNewFile();
		ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream(file));
		str.writeObject(couleurs);
		str.flush();
		str.close();
	}
	
	/**
	 * Charge la calibration du fichier de configuration si elle existe
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void chargerCalibration() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File("calibration");
		if(file.exists()){
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			couleurs = (float[][])ois.readObject();
			ois.close();
		}
	}
}