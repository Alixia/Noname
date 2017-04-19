package Noname.Test;

import java.awt.Point;

import Noname.*;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class TestRoue {

	public static Point E = new Point(5,0);
	public static Point NE = new Point(5,5);
	public static Point N = new Point(0,5);
	public static Point NO = new Point(-5,5);
	public static Point O = new Point(-5,0);
	public static Point SO = new Point(-5,-5);
	public static Point S = new Point(0,-5);
	public static Point SE = new Point(5,-5);
	
	public static Point[] tabOrientation = {E, NE, N, NO, O, SO, S, SE}; 
	
	public static void tournerEnCarreeG(){
		
		Moteurs m = new Moteurs();
		for(int i = 0; i < 4; i++){
			m.avancer();
			Delay.msDelay(1000);
			m.tourner(90, true, 150);
		}
        
	}
	
	public static void tourSensAntiHoraire(Strategie s){
		Point init = new Point(0,0);
		
		for(int i = 0; i < tabOrientation.length; i++){
			System.out.println(tabOrientation[i].toString());
			Delay.msDelay(1000);
			s.dirigerVersPalet(new Point(0,0), tabOrientation[i]);
		}
	}
	
	public static void tourSensHoraire(Strategie s){
		Point init = new Point(0,0);
		
		for(int i = tabOrientation.length-1; i >=0; i--){
			System.out.println(tabOrientation[i].toString());
			Delay.msDelay(1000);
			s.dirigerVersPalet(init, tabOrientation[i]);
		}
		
	}

	public static void main(String[] args) {
		//tournerEnCarreeG();

		Moteurs m = new Moteurs();
		Pince p = new Pince();
		Capteurs c = new Capteurs();
		Strategie s = new Strategie(c, m, p);

		System.out.println("sens horaire");
		tourSensHoraire(s);
		Delay.msDelay(000);
		System.out.println("sens anti-horaire");
		tourSensAntiHoraire(s);
		
	}

}
