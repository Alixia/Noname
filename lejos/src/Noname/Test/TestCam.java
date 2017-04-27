package Noname.Test;

import org.r2d2.vue.InputHandler;
import org.r2d2.vue.Screen;

import Noname.Capteurs;
import Noname.Moteurs;
import Noname.Pince;
import Noname.Strategie;

public class TestCam {

	public static void main(String[] args) {
		Moteurs m = new Moteurs();
		Pince p = new Pince();
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
