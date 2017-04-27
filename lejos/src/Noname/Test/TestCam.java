package Noname.Test;

import Noname.Capteurs;
import Noname.Moteurs;
import Noname.Pince;
import Noname.Strategie;
import Noname.Outils.InputHandler;
import Noname.Outils.Screen;

public class TestCam {

	public static void main(String[] args) {
		Moteurs m = new Moteurs();
		Pince p = new Pince(true);
		Capteurs c = new Capteurs();
		Strategie s = new Strategie(c, m, p);
		InputHandler ih = new InputHandler(new Screen());
		s.lancerCam();
		
		s.cam.affichePalets();
		s.cam.afficheRobots();
		ih.waitAny();
		s.cam.affichePalets();
		s.cam.afficheRobots();
		ih.waitAny();
		s.cam.affichePalets();
		s.cam.afficheRobots();
		ih.waitAny();
		s.cam.affichePalets();
		s.cam.afficheRobots();
		ih.waitAny();
	}

}
