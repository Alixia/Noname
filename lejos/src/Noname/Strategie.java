package Noname;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;

import Noname.API.APIStrategie;
import Noname.Outils.Couleur;
import Noname.Outils.MachineEtat;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class Strategie implements APIStrategie {

	private Capteurs capteur;
	private Moteurs moteurs;
	private Pince pince;

	private MachineEtat etat;
	private int[][] tabPallet;
	private int[][] tabRobot;
	private int[][] newTabPallet;
	private int[][] newTabRobot;
	private int indiceRobot;
	private int indiceAdverse;
	private int x = 1;
	private int y = 2;
	private double margeErreur = 5;
	private int yCageAdverse;
	private int yCage;
	private int yMin;
	private int yMax;
	public Camera cam;
	private Thread ThreadCam;
	private boolean aGauche;

	public Strategie(Capteurs ca, Moteurs m, Pince p, boolean aGauche) {
		this.capteur = ca;
		this.moteurs = m;
		this.pince = p;
		etat = MachineEtat.NOPALLET;
		cam = new Camera();
		ThreadCam = new Thread(cam);
		this.aGauche = aGauche;
		intialisation();
	}

	public void calibration() {
			// Si le robot commence la partie a gauche
		if (aGauche) {
			moteurs.setAngle(0);
			indiceRobot = 0;
			indiceAdverse = 1;
		} else{ // Robot commence a droite
			moteurs.setAngle(180);
			indiceRobot = 1;
			indiceAdverse = 0;
		}
		// Le but est du cote du robot adverse au debut
		yCage = tabRobot[indiceAdverse][y];
		yCageAdverse = tabRobot[indiceRobot][y];
		// Enregistrement des lignes blanches
		yMax = (yCage > yCageAdverse) ? yCage : yCageAdverse;
		yMin = (yCage > yCageAdverse) ? yCageAdverse : yCage;
	}

	public void intialisation() {
		try {
			capteur.chargerCalibration();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		lancerCam();
		
		newTabPallet = cam.getPalets();
		newTabRobot = cam.getRobots();
		
		tabPallet = cam.getPalets();
		tabRobot = cam.getRobots();
		calibration();
	}

	// Permet de ramener le premier palet dans les cages
	public void ramenerPremierPalet() {
		miseAJour(1);
		// Position du robot
		Point pos = new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]);
		// Decalage du robot pour esquiver les palets
		int delta = (tabRobot[indiceAdverse][x] > tabRobot[indiceRobot][x]) ? -25 : 25;
		// Destination vers les cages
		Point dest = new Point(tabRobot[indiceRobot][x] + delta, tabRobot[indiceRobot][y]);
		seDirigerVers(pos, dest);
		moteurs.avancer();
		Delay.msDelay(1200);
		moteurs.arreter();
		miseAJour(1);
		pos.move(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]);
		dest.move(tabRobot[indiceRobot][x], yCage);
		seDirigerVers(pos, dest);
		// Tant qu'on a pas traverse la ligne blanche
		boolean enCours = true;
		while (enCours) {
			moteurs.avancer();
			while (enCours && ((Math.abs(tabRobot[indiceAdverse][x] - tabRobot[indiceRobot][x]) > 20)
					|| (Math.abs(tabRobot[indiceAdverse][y] - tabRobot[indiceRobot][y]) > 40))) {
				Delay.msDelay(10);
				if (capteur.getCurrentColor().equals(Couleur.blanc)) {
					enCours = false;
				}
				miseAJour(1);
			}
			moteurs.arreter();
		}
		pince.ouvrirPince();
		moteurs.reculer();
		Delay.msDelay(500);
		moteurs.arreter();
	}

	public void seDirigerVers(Point positionRobot, Point destination) {
		moteurs.arreter();
		if (destination.y == positionRobot.y) {
			double angleAFaire = 0;
			angleAFaire += moteurs.angleInitial(true);
			if (positionRobot.x < destination.x) {
				moteurs.tourner(90 + angleAFaire, false, 120);
			} else {
				moteurs.tourner(-90 + angleAFaire, false, 120);
			}
		} else {
			double angleAFaire = 0;
			boolean face;
			if (positionRobot.y < destination.y) {
				angleAFaire += moteurs.angleInitial(true);
				face = true;
			} else {
				angleAFaire += moteurs.angleInitial(false);
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

	private boolean pointsEgaux(Point p1, Point p2) {
		return ((p1.x <= p2.x + margeErreur) && (p1.x >= p2.x + margeErreur));
	}

	public Point detecterPlusProchePallet() {
		int indicePalletPlusProche = 0;
		boolean estTrouve = false;
		double distancePrec = 5000;
		for (int i = 0; i < tabPallet.length; i++) {
			// System.out.println("1 taille tabPallet[i][y] : " +tabPallet[i][y]
			// + " yMin : " + yMin);
			// System.out.println("1 taille yMax " +yMax );
			if ((tabPallet[i][y] > yMin + 15) && (tabPallet[i][y] < yMax - 15)) {
				double distanceEnCours = Math.sqrt(Math.pow(tabPallet[i][x] - tabRobot[indiceRobot][x], 2)
						+ Math.pow(tabPallet[i][y] - tabRobot[indiceRobot][y], 2));
				if (distancePrec > distanceEnCours) {
					indicePalletPlusProche = i;
					distancePrec = distanceEnCours;
					estTrouve = true;
				}
			}
		}

		Point plusProche = new Point(-1, -1);

		if (estTrouve) {
			plusProche = new Point(tabPallet[indicePalletPlusProche][x], tabPallet[indicePalletPlusProche][y]);
		}
		System.out.println("Je vais au point : (" + tabPallet[indicePalletPlusProche][x] + ";"+ tabPallet[indicePalletPlusProche][y]+")");
		return plusProche;
	}

	public boolean allerChercherPallet(Point pallet) {
		// avance jusqu'au pallet et le prend en pince
		seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), pallet);
		moteurs.avancer();
		miseAJour(0);

		// prendre en compte les erreurs potentielles du aux angles
		// prendre en compte les obstacle
		int i = 1;
		while (!capteur.boutonEstPresse()) {
			Delay.msDelay(200);
			miseAJour(i);
			i = (i + 1) % 11;
			if(i == 0){ //l'angle a été mis a jour
				seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), pallet);
				moteurs.avancer();
			}
		}

		if (capteur.boutonEstPresse()) {
			moteurs.arreter();
			pince.fermerPince();
			return true;
		}
		moteurs.arreter();
		return false;
	}

	// met a jour les tableau palet et robot et l'angle
	public void miseAJour(int nbIter) {
		int[][] newTabPallet2 = cam.getPalets();
		int[][] newTabRobot2 = cam.getRobots();
		// afficherTableaux();

		if (nbIter == 0) {
			newTabPallet = cam.getPalets();
			newTabRobot = cam.getRobots();
		}
		if(nbIter == 10){
			double teta = 0;
			if (newTabRobot[indiceRobot][y] == newTabRobot2[indiceRobot][y]) {
				if (newTabRobot[indiceRobot][x] > newTabRobot2[indiceRobot][x]) {
					teta = 90;
				} else
					teta = 270;
			} else {
				double tangenteTeta = 0;
				tangenteTeta = Math.abs((newTabRobot[indiceRobot][x] - newTabRobot2[indiceRobot][x])
							/ (newTabRobot[indiceRobot][y] - newTabRobot2[indiceRobot][y]));
				teta = Math.atan(tangenteTeta);
				if(newTabRobot[indiceRobot][y] > newTabRobot2[indiceRobot][y]){
					if(newTabRobot[indiceRobot][x] > newTabRobot2[indiceRobot][x]){
						teta = 180 - teta;
					}else{
						teta = 180 + teta;
					}
				}else{
					if(newTabRobot[indiceRobot][x] > newTabRobot2[indiceRobot][x]){
						teta = teta;
					}else{
						teta = 360 - teta;
					}
				}
				
			}
			if(Math.abs(moteurs.getAngle()) > Math.abs(teta - 5) ){
				moteurs.setAngle(teta);
			}
			
		}

		System.arraycopy(newTabPallet2, 0, tabPallet, 0, tabPallet.length);
		System.arraycopy(newTabRobot2, 0, tabRobot, 0, tabRobot.length);
	}

	public void rentrerALaMaison() {
		// prendre en compte les erreurs potentielles du aux angles
		// prendre en compte les obstacle
		miseAJour(0);
		int i = 1;
		boolean mauvaisCamp = false;
		do{
			seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), new Point(100, yCage));
			moteurs.avancer();
			while (!capteur.getCurrentColor().equals(Couleur.blanc)) {
				Delay.msDelay(100);
				miseAJour(i);
				i = (i + 1) % 11;
				if(i == 0){ //l'angle a été mis a jour
					seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), new Point(100, yCage));
					moteurs.avancer();
				}
			}
			if(!(tabRobot[indiceRobot][y] > (yCage - 50) && tabRobot[indiceRobot][y] < (yCage + 50))){
				mauvaisCamp = true;
				moteurs.arreter();
				moteurs.demiTour();
			}
		}while(mauvaisCamp);
		moteurs.arreter();
		pince.ouvrirPince();
		moteurs.reculer();
		Delay.msDelay(500);
		moteurs.arreter();

		miseAJour(1);
	}

	public void lancerCam() {
		ThreadCam.start();
	}

	public void afficherTableaux() {

		for (int i = 0; i < tabPallet.length; i++) {
			System.out.println(tabPallet[i][0] + ":" + tabPallet[i][1] + " / " + tabPallet[i][2]);
			// Button.waitForAnyPress();
		}
		for (int i = 0; i < tabRobot.length; i++) {
			System.out.println(tabRobot[i][0] + ":" + tabRobot[i][1] + " / " + tabRobot[i][2]);
			// Button.waitForAnyPress();
		}

	}

	public void premiereEtape() {
		Point p = detecterPlusProchePallet();
		System.out.println(p);
		allerChercherPallet(p);
		ramenerPremierPalet();
	}

	public void run() {
		Point pallet;
		premiereEtape();
		boolean boucle = true;
		while (boucle) {
			switch (etat) {
			case NOPALLET:
				pallet = detecterPlusProchePallet();
				System.out.println("Je suis au point : (" + tabRobot[indiceRobot][x] + ";"+ tabRobot[indiceRobot][y]+")");
				System.out.println("Angle : " + moteurs.getAngle());
				// il n'y a plus de pallet à aller chercher
				if (pallet.x == -1) {
					boucle = false;
					break;
				}
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

	public void stop() {
		ThreadCam.stop();
	}
}
