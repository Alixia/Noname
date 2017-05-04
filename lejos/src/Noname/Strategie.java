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

	public Strategie(Capteurs ca, Moteurs m, Pince p) {
		this.capteur = ca;
		this.moteurs = m;
		this.pince = p;
		etat = MachineEtat.NOPALLET;
		cam = new Camera();
		ThreadCam = new Thread(cam);
		intialisation();
	}

	public void calibration() {
		System.out.println("Calibration du terrain");
		// Tant que ce n'est ni LEFT ni RIGHT, redemander
		boolean reAsk = true;
		do {
			System.out.println("Le robot est a gauche ou a droite de la camera ?");
			Button.waitForAnyPress();
			// Si le robot commence la partie a gauche
			if (Button.LEFT.isDown()) {
				moteurs.setAngle(0);
				indiceRobot = 0;
				indiceAdverse = 1;
				reAsk = false;
			} else if (Button.RIGHT.isDown()) { // Robot commence a droite
				moteurs.setAngle(180);
				indiceRobot = 1;
				indiceAdverse = 0;
				reAsk = false;
			}
		} while (reAsk);
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

		tabPallet = cam.getPalets();
		tabRobot = cam.getRobots();
		calibration();
	}

	// Permet de ramener le premier palet dans les cages
	public void ramenerPremierPalet() {
		miseAJour();
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
		miseAJour();
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
				miseAJour();
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
		System.out.println("Je vais au point : ()" + tabPallet[indicePalletPlusProche][x] + ";"+ tabPallet[indicePalletPlusProche][y]+")");
		return plusProche;
	}

	public boolean allerChercherPallet(Point pallet) {
		// avance jusqu'au pallet et le prend en pince
		seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), pallet);
		moteurs.avancer();
		miseAJour();

		// prendre en compte les erreurs potentielles du aux angles
		// prendre en compte les obstacle
		while (!capteur
				.boutonEstPresse() /*
									 * && !pointsEgaux(new
									 * Point(tabRobot[indiceAdverse][x],
									 * tabRobot[indiceAdverse][y]), pallet)
									 */) {
			Delay.msDelay(200);
			miseAJour();
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
	public void miseAJour() {
		int[][] newTabPallet = cam.getPalets();
		int[][] newTabRobot = cam.getRobots();
		// afficherTableaux();

		if ((Math.abs(newTabPallet[indiceRobot][x] - tabPallet[indiceRobot][x]) < 10)
				|| (Math.abs(newTabPallet[indiceRobot][y] - tabPallet[indiceRobot][y]) < 10)) {
			double teta = 0;
			if (newTabPallet[indiceRobot][y] == tabPallet[indiceRobot][y]) {
				if (newTabPallet[indiceRobot][x] > tabPallet[indiceRobot][x]) {
					teta = 90;
				} else
					teta = 120;
			} else {
				double tangenteTeta = 0;
				if ((newTabRobot[indiceRobot][y] - tabRobot[indiceRobot][y]) != 0)
					tangenteTeta = Math.abs((newTabRobot[indiceRobot][x] - tabRobot[indiceRobot][x])
							/ (newTabRobot[indiceRobot][y] - tabRobot[indiceRobot][y]));
				teta = Math.atan(tangenteTeta);
			}
			// moteurs.setAngle(teta);
			System.arraycopy(newTabPallet, 0, tabPallet, 0, tabPallet.length);
			System.arraycopy(newTabRobot, 0, tabRobot, 0, tabRobot.length);
		}
	}

	public void rentrerALaMaison() {
		seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), new Point(100, yCage));
		moteurs.avancer();

		// prendre en compte les erreurs potentielles du aux angles
		// prendre en compte les obstacle
		while (!capteur.getCurrentColor().equals(Couleur.blanc)) {
			Delay.msDelay(100);
		}
		moteurs.arreter();
		pince.ouvrirPince();
		moteurs.reculer();
		Delay.msDelay(500);
		moteurs.arreter();

		miseAJour();
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
				// il n'y a plus de pallet � aller chercher
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
