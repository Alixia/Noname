package Noname;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public enum Constantes {

	roueDroite(MotorPort.C), roueGauche(MotorPort.B), pince(MotorPort.A),
	colorimetre("1"), boutonPoussoir("2"), ultrasons("4");

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
