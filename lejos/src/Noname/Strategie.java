package Noname;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;

import Noname.Outils.Couleur;
import Noname.Outils.MachineEtat;
import lejos.hardware.Button;
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
	private Point cage;
	public Cam cam;
	private Thread ThreadCam;
	public Strategie(Capteurs ca, Moteurs m, Pince p) {
		this.capteur = ca;
		this.moteurs = m;
		this.pince = p;
		etat = MachineEtat.NOPALLET;

		cam = new Cam();
		ThreadCam = new Thread(cam);

		mettreAJourTab();
		indiceRobot = 0;
		indiceAdverse = 1;
		cage = new Point(0,0);
	}
	
	public void calibration(){
		System.out.println("Calibration du terrain");
		System.out.println("Le robot est a gauche ou a droit ? (bouton gauche et droit)");
		// Tant que ce n'est ni LEFT ni RIGHT, redemander
		boolean reAsk = true;
		while(reAsk){
			Button.waitForAnyPress();
			if(Button.LEFT.isDown()){
				moteurs.setangle(0);
				tabRobot[indiceRobot][x] = 106;
				tabRobot[indiceRobot][y] = 19;
				cage = new Point(106,275);
				
				tabRobot[indiceAdverse][x] = 106;
				tabRobot[indiceAdverse][y] = 275;
				reAsk = false;
			}else if(Button.RIGHT.isDown()){
				moteurs.setangle(180);
				tabRobot[indiceRobot][x] = 106;
				tabRobot[indiceRobot][y] = 275;
				cage = new Point(106,19);
				
				tabRobot[indiceAdverse][x] = 106;
				tabRobot[indiceAdverse][y] = 19;
				reAsk = false;
			}else{
				System.out.println("Le robot est a gauche ou a droit ? (bouton gauche et droit)");
			}
		} 
	}

	public void intialisation() {

		try {
			capteur.chargerCalibration();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		calibration();
	}

	public void seDirigerVers(Point positionRobot, Point destination) {
		moteurs.arreter();
		if (destination.y == positionRobot.y) {
			double angleAFaire = 0;
			angleAFaire += moteurs.angleInitial(true, 120);
			if (positionRobot.x < destination.x) {
				moteurs.tourner(90+angleAFaire, false, 120);
			} else {
				moteurs.tourner(-90+angleAFaire, false, 120);
			}
		} else {
			double angleAFaire = 0;
			boolean face;
			if (positionRobot.y < destination.y) {
				angleAFaire += moteurs.angleInitial(true, 200);
				face = true;
			} else {
				angleAFaire += moteurs.angleInitial(false, 200);
				face = false;
			}
			double tangenteTeta = Math.abs(destination.x - positionRobot.x) / Math.abs(destination.y - positionRobot.y);
			double teta = Math.atan(tangenteTeta);
			
			double anglePlus = 1 * Math.toDegrees(teta) + angleAFaire;
			double angleMoins = -1 * Math.toDegrees(teta) + angleAFaire;

			if (positionRobot.x < destination.x) {
				if (face) {
					moteurs.tourner(anglePlus, false, 120);
				} else {
					moteurs.tourner(angleMoins, false, 120);
				}

			} else {
				if (face) {
					moteurs.tourner(angleMoins, false, 120);
				} else {
					moteurs.tourner(anglePlus, false, 120);
				}
			}
		}
	}

	public boolean pointsEgaux(Point p1, Point p2) {

		return ((p1.x <= p2.x + margeErreur) && (p1.x >= p2.x + margeErreur));
	}

	public Point detecterPlusProchePallet() {
		int indicePalletPlusProche = 0;
		double distancePrec = 5000;
		for (int i = 0; i < tabPallet.length; i++) {
			if ((tabPallet[i][y] > 50) && (tabPallet[i][y] < 250)
					&& !pointsEgaux(new Point(tabPallet[i][x], tabPallet[i][y]),
							new Point(tabRobot[indiceAdverse][x], tabRobot[indiceAdverse][y]))) {
				double distanceEnCours = Math.sqrt(Math.pow(tabPallet[i][x] - tabRobot[indiceRobot][x], 2)
						+ Math.pow(tabPallet[i][y] - tabRobot[indiceRobot][y], 2));
				if (distancePrec > distanceEnCours) {
					indicePalletPlusProche = i;
					distancePrec = distanceEnCours;
				}
			}
		}
		Point plusProche = new Point(tabPallet[indicePalletPlusProche][x], tabPallet[indicePalletPlusProche][y]);
		return plusProche;
	}

	public boolean allerChercherPallet(Point pallet) {
		// avance jusqu'au pallet et le prend en pince
		seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), pallet);
		moteurs.avancer();

		// prendre en compte les erreurs potentielles du aux angles
		// prendre en compte les obstacle
		while (!capteur
				.boutonEstPresse() /*
									 * && !pointsEgaux(new
									 * Point(tabRobot[indiceAdverse][x],
									 * tabRobot[indiceAdverse][y]), pallet)
									 */) {
			Delay.msDelay(200);
			mettreAJourTab();
		}

		if (capteur.boutonEstPresse()) {
			moteurs.arreter();
			pince.fermerPince();
			return true;
		}
		moteurs.arreter();
		return false;
	}

	// met a jour les tableau palet et robot
	public void mettreAJourTab() {
		tabPallet = cam.getPalets();
		tabRobot = cam.getRobots();

	}

	public void rentrerALaMaison() {
		seDirigerVers(new Point(tabRobot[indiceRobot][x],  tabRobot[indiceRobot][y]), cage);
		moteurs.avancer();

		// prendre en compte les erreurs potentielles du aux angles
		// prendre en compte les obstacle
		while (!capteur.getCurrentColor().equals(Couleur.blanc)) {
			Delay.msDelay(200);
		}
		moteurs.arreter();
		pince.ouvrirPince();
		moteurs.reculer();
		Delay.msDelay(200);
		moteurs.arreter();

		mettreAJourTab();

	}

	public void lancerCam() {
		ThreadCam.start();
	}
	

	
	public void afficherTableaux(){
		
		for(int i = 0;i<tabPallet.length;i++){
			System.out.println( tabPallet[i][0] + ":" + tabPallet[i][1] + " / " + tabPallet[i][2]);
			//Button.waitForAnyPress();
		}
		for(int i = 0;i<tabRobot.length;i++){
			System.out.println( tabRobot[i][0] + ":" + tabRobot[i][1] + " / " + tabRobot[i][2]);
			//Button.waitForAnyPress();
		}
		
	}

	public void run() {
		Point pallet;
		lancerCam();
		while (true) {
			switch (etat) {
			case NOPALLET:
				pallet = detecterPlusProchePallet();
				if (allerChercherPallet(pallet)) {
					etat = MachineEtat.PALLET;
				}
				break;
			case PALLET:
				rentrerALaMaison();
				etat = MachineEtat.NOPALLET;
			}
		}
	}

}
