package Noname;

import java.awt.Point;

import Noname.Outils.MachineEtat;

public class Strategie {

	private Capteurs capteur;
	private Moteurs moteurs;
	private Pince pince;
	
	private MachineEtat etat;
	private int[][] tabPallet;
	private int[][] tabRobot;
	private int indiceRobot;
	private int x = 1;
	private int y = 2;
	
	
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

	public void dirigerVersPalet(Point positionRobot, Point positionPalet) {
		moteurs.arreter();
		if (positionPalet.y == positionRobot.y) {
			moteurs.revenirAngleInitial(true, 120);
			if (positionRobot.x < positionPalet.x) {
				moteurs.tourner(90, false, 120);
			} else {
				moteurs.tourner(-90, false, 120);
			}
		} else {
			boolean face;
			if (positionRobot.y < positionPalet.y) {
				moteurs.revenirAngleInitial(true, 200);
				face = true;
			} else {
				moteurs.revenirAngleInitial(false, 200);
				face = false;
			}
			double tangenteTeta = Math.abs(positionPalet.x - positionRobot.x)
					/ Math.abs(positionPalet.y - positionRobot.y);
			double teta = Math.atan(tangenteTeta);

			if (positionRobot.x < positionPalet.x) {
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
	
	public Point detecterPlusProchePallet(){
		int indicePalletPlusProche = 0;
		double distancePrec = 5000;
		for(int i = 0; i < tabPallet.length; i++){
			if((tabPallet[i][y]>50) && (tabPallet[i][y]<250)){
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
	
	private void rentrerALaMaison() {
		// TODO Auto-generated method stub
		
	}

	private boolean allerChercherPallet(Point pallet) {
		// TODO Auto-generated method stub
		return false;
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
