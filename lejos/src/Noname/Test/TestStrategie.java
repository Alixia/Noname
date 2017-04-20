package Noname.Test;

import java.awt.Point;

import Noname.Capteurs;
import Noname.Moteurs;
import Noname.Pince;
import Noname.Strategie;
import Noname.Outils.Couleur;
import lejos.utility.Delay;

public class TestStrategie {
	
	public static Point E = new Point(5,0);
	public static Point NE = new Point(5,5);
	public static Point N = new Point(0,5);
	public static Point NO = new Point(-5,5);
	public static Point O = new Point(-5,0);
	public static Point SO = new Point(-5,-5);
	public static Point S = new Point(0,-5);
	public static Point SE = new Point(5,-5);
	public static Point init = new Point(0,0);
	
	public static Point[] tabOrientation = {E, NE, N, NO, O, SO, S, SE}; 
	
	public static void tourSensAntiHoraire(Strategie s){
		
		for(int i = 0; i < tabOrientation.length; i++){
			System.out.println(tabOrientation[i].toString());
			Delay.msDelay(1000);
			s.dirigerVersPalet(new Point(0,0), tabOrientation[i]);
		}
	}
	
	public static void tourSensHoraire(Strategie s){
		
		for(int i = tabOrientation.length-1; i >=0; i--){
			System.out.println(tabOrientation[i].toString());
			Delay.msDelay(1000);
			s.dirigerVersPalet(init, tabOrientation[i]);
		}
		
	}
	
	public static void getPalet(Moteurs m, Pince p, Capteurs c, Strategie s){
		//c.calibration();
		Point dest =  new Point(-2,3);
		s.dirigerVersPalet(init, dest);
		m.avancer();

		while(!c.boutonEstPresse()){
			Delay.msDelay(1000);
		}
		m.arreter();
		s.dirigerVersPalet(dest, init);
	}
	
	public static void avanceJusqueLigneBlanche(Moteurs m, Pince p, Capteurs c, Strategie s){
		c.calibration();
		
		m.avancer();

		while(!c.getCurrentColor().equals(Couleur.blanc)){
			Delay.msDelay(20);
		}
		m.arreter();
	}
	
	public static void main(String[] args) {

		Moteurs m = new Moteurs();
		Pince p = new Pince(500);
		Capteurs c = new Capteurs();
		Strategie s = new Strategie(c, m, p);
		//getPalet(m,p,c,s);
		//tourSensHoraire(s);
		avanceJusqueLigneBlanche(m,p,c,s);
	}
}
