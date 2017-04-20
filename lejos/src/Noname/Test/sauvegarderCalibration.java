package Noname.Test;

import java.io.IOException;

import Noname.Capteurs;

public class sauvegarderCalibration {

	public static void main(String[] args) {
		Capteurs capteurs = new Capteurs();
		
		capteurs.calibration();
		
		try {
			capteurs.sauvegarderCalibration();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
