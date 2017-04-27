package Noname.Test;

import java.awt.Point;

import Noname.Capteurs;
import Noname.Moteurs;
import Noname.Pince;
import Noname.Strategie;
import Noname.Outils.Couleur;
import lejos.utility.Delay;

public class TestStrategie {
	
	final private static Point E = new Point(5,0);
	final private static Point NE = new Point(5,5);
	final private static Point N = new Point(0,5);
	final private static Point NO = new Point(-5,5);
	final private static Point O = new Point(-5,0);
	final private static Point SO = new Point(-5,-5);
	final private static Point S = new Point(0,-5);
	final private static Point SE = new Point(5,-5);
	final private static Point init = new Point(0,0);
	
	final private static Point[] tabOrientation = {E, NE, N, NO, O, SO, S, SE}; 
	
	public static void tourSensAntiHoraire(Strategie s){
		
		for(int i = 0; i < tabOrientation.length; i++){
			System.out.println(tabOrientation[i].toString());
			Delay.msDelay(1000);
			s.seDirigerVers(new Point(0,0), tabOrientation[i]);
		}
	}
	
	public static void tourSensHoraire(Strategie s){
		
		for(int i = tabOrientation.length-1; i >=0; i--){
			System.out.println(tabOrientation[i].toString());
			Delay.msDelay(1000);
			s.seDirigerVers(init, tabOrientation[i]);
		}
		
	}
	
	public static void getPalet(Moteurs m, Pince p, Capteurs c, Strategie s){
		//c.calibration();
		Point dest =  new Point(-2,3);
		s.seDirigerVers(init, dest);
		m.avancer();

		while(!c.boutonEstPresse()){
			Delay.msDelay(1000);
		}
		m.arreter();
		s.seDirigerVers(dest, init);
	}
	
	public static void avanceJusqueLigneBlanche(Moteurs m, Pince p, Capteurs c, Strategie s){
		c.calibration();
		p.calibration();
		
		p.fermerPince();
		m.avancer();

		while(!c.getCurrentColor().equals(Couleur.blanc)){
			Delay.msDelay(20);
		}
		m.arreter();
		p.ouvrirPince();
	}
	
	public static void premiereEtape(Moteurs m, Pince p, Capteurs c, Strategie s){
		s.allerChercherPallet(s.detecterPlusProchePallet());
		s.ramenerPremierPalet();
	}
	
	public static void main(String[] args) {

		Moteurs m = new Moteurs();
		Pince p = new Pince(true);
		Capteurs c = new Capteurs();
		Strategie s = new Strategie(c, m, p);
		premiereEtape(m, p, c, s);
	}
}
