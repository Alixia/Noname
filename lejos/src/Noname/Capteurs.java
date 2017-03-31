package Noname;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class Capteurs implements APICapteurs{
	
	EV3TouchSensor boutonPoussoir;
	EV3TouchSensor colorimetre;
	EV3TouchSensor ultrasons;

	public Capteurs(Port boutonPoussoir, Port colorimetre, Port ultrasons){
		this.boutonPoussoir = new EV3TouchSensor(boutonPoussoir);
		this.colorimetre = new EV3TouchSensor(colorimetre);
		this.ultrasons = new EV3TouchSensor(ultrasons);
	}
	
	@Override
	public boolean boutonEstPresse() {
		float[] sample = new float[1];
        boutonPoussoir.fetchSample(sample, 0);

        return sample[0] != 0;
	}

	@Override
	public float detecteObjet() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Couleur couleur() {
		// TODO Auto-generated method stub
		return null;
	}


}
