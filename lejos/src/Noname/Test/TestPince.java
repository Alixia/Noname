package Noname.Test;

import Noname.Pince;
import lejos.utility.Delay;

public class TestPince {

	public static void main(String[] args) {
		Pince P = new Pince(true);
        P.calibration();
        Delay.msDelay(1000);
        for(int i=0;i<10;i++){
	        P.fermerPince();
	        P.fermerPince();
	        P.ouvrirPince();
	        P.ouvrirPince();
		}
       
	}

}
