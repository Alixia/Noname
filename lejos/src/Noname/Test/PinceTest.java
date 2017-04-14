package Noname.Test;

import Noname.Pince;
import lejos.utility.Delay;

public class PinceTest {

	public static void main(String[] args) {
		Pince P = new Pince(500);
        P.calibration();

        Delay.msDelay(1000);
        P.fermerPince();
        Delay.msDelay(1000);
        P.ouvrirPince();
       
	}

}
