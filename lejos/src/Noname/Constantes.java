package Noname;

public enum Constantes {

	roueDroite("C"), roueGauche("B"), pince("A"),
	colorimetre("1"), boutonPoussoir("2"), ultrasons("4");

	private String name;

	Constantes(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
