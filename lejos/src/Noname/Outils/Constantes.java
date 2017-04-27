package Noname.Outils;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public enum Constantes {

	roueDroite(MotorPort.C), roueGauche(MotorPort.B), pince(MotorPort.A),
	colorimetre(LocalEV3.get().getPort("S1")), boutonPoussoir(LocalEV3.get().getPort("S2")), ultrasons(LocalEV3.get().getPort("S4")),
	demiTour(180), quartTour(90),
	distance_centre(62.525f), diametre_roues(56);

	private Port namePort;
	private int degres;
	private float centimetre;
	
	Constantes(Port name) {
		this.namePort = name;
	}
	
	Constantes(int degres){
		this.degres = degres;
	}
	
	Constantes(float centimetre){
		this.centimetre = centimetre;
	}
	
	public int degres(){
		return degres;
	}
	
	public float centimetre(){
		return centimetre;
	}
	
	public Port port(){
		return namePort;
	}
}
