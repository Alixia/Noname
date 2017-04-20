package Noname;

import java.awt.Point;

import Noname.Outils.Couleur;
import Noname.Outils.MachineEtat;
import lejos.utility.Delay;

public class Strategie {

	private Capteurs capteur;
	private Moteurs moteurs;
	private Pince pince;
	
	private MachineEtat etat;
	private int[][] tabPallet;
	private int[][] tabRobot;
	private int indiceRobot;
	private int indiceAdverse;
	private int x = 1;
	private int y = 2;
	private double margeErreur = 5;
	
	
	public Strategie(Capteurs ca, Moteurs m, Pince p) {
		this.capteur = ca;
		this.moteurs = m;
		this.pince = p;
		etat = MachineEtat.NOPALLET;
	}

	public void intialisation() {
		pince.calibration();
		// capteur.calibration();
		moteurs.calibration();
	}

	public void seDirigerVers(Point positionRobot, Point destination) {
		moteurs.arreter();
		if (destination.y == positionRobot.y) {
			moteurs.revenirAngleInitial(true, 120);
			if (positionRobot.x < destination.x) {
				moteurs.tourner(90, false, 120);
			} else {
				moteurs.tourner(-90, false, 120);
			}
		} else {
			boolean face;
			if (positionRobot.y < destination.y) {
				moteurs.revenirAngleInitial(true, 200);
				face = true;
			} else {
				moteurs.revenirAngleInitial(false, 200);
				face = false;
			}
			double tangenteTeta = Math.abs(destination.x - positionRobot.x)
					/ Math.abs(destination.y - positionRobot.y);
			double teta = Math.atan(tangenteTeta);

			if (positionRobot.x < destination.x) {
				if (face) {
					moteurs.tourner(1 * Math.toDegrees(teta), false, 120);
				} else {
					moteurs.tourner(-1 * Math.toDegrees(teta), false, 120);
				}

			} else {
				if (face) {
					moteurs.tourner(-1 * Math.toDegrees(teta), false, 120);
				} else {
					moteurs.tourner(1 * Math.toDegrees(teta), false, 120);
				}
			}
		}
	}
	
	public boolean pointsEgaux(Point p1, Point p2){
		
		return ((p1.x <= p2.x + margeErreur) && (p1.x >= p2.x + margeErreur));
	}
	
	public Point detecterPlusProchePallet(){
		int indicePalletPlusProche = 0;
		double distancePrec = 5000;
		for(int i = 0; i < tabPallet.length; i++){
			if((tabPallet[i][y]>50) && (tabPallet[i][y]<250) && !pointsEgaux(new Point(tabPallet[i][x], tabPallet[i][y]), new Point(tabRobot[indiceAdverse][x],tabRobot[indiceAdverse][y]))){
				double distanceEnCours =  Math.sqrt(Math.pow(tabPallet[i][x]-tabRobot[indiceRobot][x], 2) + Math.pow(tabPallet[i][y]-tabRobot[indiceRobot][y], 2));
				if( distancePrec > distanceEnCours ){
					indicePalletPlusProche = i;
					distancePrec = distanceEnCours;
				}
			}
		}
		Point plusProche = new Point(tabPallet[indicePalletPlusProche][x], tabPallet[indicePalletPlusProche][y]);
		return plusProche;
	}


	private boolean allerChercherPallet(Point pallet) {
		//avance jusqu'au pallet et le prend en pince
		seDirigerVers(new Point(tabRobot[indiceRobot][x],  tabRobot[indiceRobot][y]), pallet);
		moteurs.avancer();
		
		//prendre en compte les erreurs potentielles du aux angles
		//prendre en compte les obstacle
		while(!capteur.boutonEstPresse() && !pointsEgaux(new Point(tabRobot[indiceAdverse][x],  tabRobot[indiceAdverse][y]), pallet)){
			Delay.msDelay(200);
			mettreAJourTab();
		}
		if(capteur.boutonEstPresse()){
			pince.fermerPince();
			return true;
		}
		
		return false;
	}


	//met a jour les tableau palet et robot
	private void mettreAJourTab() {
		// TODO Auto-generated method stub
		
	}
	
	private void rentrerALaMaison() {
		seDirigerVers(new Point(tabRobot[indiceRobot][x],  tabRobot[indiceRobot][y]), new Point(50, 100));
		moteurs.avancer();
		
		//prendre en compte les erreurs potentielles du aux angles
		//prendre en compte les obstacle
		while(!capteur.getCurrentColor().equals(Couleur.blanc)){
			Delay.msDelay(200);
		}
		pince.ouvrirPince();
		moteurs.reculer();
		Delay.msDelay(200);
		mettreAJourTab();
	
	}
	

	public void run() {
		Point pallet;
		while (true) {
			switch (etat) {
			case NOPALLET:
				pallet = detecterPlusProchePallet();
				if (allerChercherPallet(pallet)) {
					etat = MachineEtat.PALLET;
				} else {
					
				}
			break;
			case PALLET:
				rentrerALaMaison();
				etat = MachineEtat.NOPALLET;
			}
		}
	}

}
