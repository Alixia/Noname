package Noname;

import org.r2d2.utils.R2D2Constants;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public enum Constantes {

	roueDroite(MotorPort.C), roueGauche(MotorPort.B), pince(MotorPort.A),
	colorimetre(LocalEV3.get().getPort("S1")), boutonPoussoir(LocalEV3.get().getPort("S2")), ultrasons(LocalEV3.get().getPort("S4"));

	private String name;
	private Port namePort;

	Constantes(String name) {
		this.name = name;
	}
	
	Constantes(Port name) {
		this.namePort = name;
	}

	public String toString() {
		return name;
	}
	
	public Port port(){
		return namePort;
	}
}
