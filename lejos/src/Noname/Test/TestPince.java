package Noname.Test;

import Noname.Pince;
import lejos.utility.Delay;

public class TestPince {

	public static void main(String[] args) {
		Pince P = new Pince(500);
        P.calibration();
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Delay.msDelay(1000);
        for(int i=0;i<10;i++){
	        P.fermerPince();
	        Delay.msDelay(1000);
	        P.ouvrirPince();
	        Delay.msDelay(1000);
		}
       
	}

}
