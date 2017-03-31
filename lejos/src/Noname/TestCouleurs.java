package Noname;

public class TestCouleurs {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Capteurs capteurs = new Capteurs();

		for (Couleur c : Couleur.values()) {
			capteurs.calibrerCouleur(Couleur.rouge);
			System.out.println("couleur = " + c.name());
			System.out.println("tableur calibrationnage" + tableauString(capteurs.getCalibration()));

		}
		// the end

	}

	private static String tableauString(float[][] tab) {
		String buffer = "";
		for (int i = 0; i < Couleur.values().length; i++) {
			buffer += " " + tab[i];
		}
		return buffer;
	}

}
