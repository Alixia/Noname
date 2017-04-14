package Noname;

import Noname.API.APICapteurs;
import Noname.Outils.Constantes;
import Noname.Outils.Couleur;
import lejos.hardware.port.Port;
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
	
	@Override
	public boolean boutonEstPresse() {
		float[] sample = new float[1];
        boutonPoussoir.fetchSample(sample, 0);

        return sample[0] != 0;
	}

	@Override
	public float distanceVision() {
		float[] echantillon = new float[1];
		ultrasons.fetchSample(echantillon, 0);
		return echantillon[0];
	}

	@Override
	public Couleur couleur() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void calibrerCouleur(Couleur couleur){
		SampleProvider moyenne = new MeanFilter(colorimetre.getRGBMode(), 1);
		couleurs[couleur.ordinal()] = new float[moyenne.sampleSize()];
		moyenne.fetchSample(couleurs[couleur.ordinal()], 0);
	}
	public int getCurrentColor(){
		SampleProvider moyenne = new MeanFilter(colorimetre.getRGBMode(), 1);
		float[]        echantillon  = new float[moyenne.sampleSize()];
		double         distance = Double.MAX_VALUE;
		int            couleur   = -1;

		moyenne.fetchSample(echantillon, 0);

		for(int i= 0; i< 16; i++){
			if(couleurs[i].length > 0){
				double scalaire = scalaire(echantillon, couleurs[i]);
				if (scalaire < distance) {
					distance = scalaire;
					couleur = i;
				}
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
}
