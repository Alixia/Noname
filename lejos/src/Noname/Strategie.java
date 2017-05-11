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
		int delta = (tabRobot[indiceAdverse][x] > tabRobot[indiceRobot][x]) ? 20 : -20;
		// Destination vers les cages
		moteurs.arreter();
		moteurs.tourner(delta, true, 120);
		moteurs.avancer();
		miseAJour(1);
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
		double teta = 0;
		double tangenteTeta = 0;
		if (positionRobot.y == destination.y) {
			if (positionRobot.x > destination.x) {
				teta = 90;
			} else
				teta = 270;
		} else {
			tangenteTeta = Math.abs((double)(positionRobot.x - destination.x)
						/ (double)(positionRobot.y - destination.y));
			teta = Math.toDegrees(Math.atan(tangenteTeta));
			if(positionRobot.y > destination.y){
				if(positionRobot.x > destination.x){
					teta = 180 - teta;
				}else{
					teta = 180 + teta;
				}
			}else{
				if(positionRobot.x > destination.x){
					teta = teta;
				}else{
					teta = 360 - teta;
				}
			}
		}
		
		double angleAFaire =  moteurs.getAngle();
		angleAFaire = teta - angleAFaire;
				
		if(angleAFaire >= 360){
			angleAFaire -= 360;
		}else if(angleAFaire < 0){
			angleAFaire += 360;
		}
		
		if(angleAFaire > 180){
			moteurs.tourner(360 - angleAFaire, false, 120);
		}else{
			moteurs.tourner(angleAFaire, true, 120);
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
		return plusProche;
	}

	public boolean allerChercherPallet(Point pallet) {
		// avance jusqu'au pallet et le prend en pince
		seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), pallet);
		moteurs.avancer();
		miseAJour(0);

		// prendre en compte les erreurs potentielles du aux angles
		// prendre en compte les obstacle
		int i = 0;
		while (!capteur.boutonEstPresse() && !capteur.getCurrentColor().equals(Couleur.blanc)) {
			Delay.msDelay(100);

			Point coord = capteur.getCoord(capteur.getCurrentColor());
			cam.setRobot(coord.x, coord.y, indiceRobot);
			
			i = (i + 1) % 10;
			miseAJour(i);
			if(i == 0){ //l'angle a été mis a jour
				if((Math.abs(tabRobot[indiceRobot][x] - pallet.x) < 30 ) &&( Math.abs(tabRobot[indiceRobot][y] - pallet.y) < 30)){
					System.out.println("modif angle! " + i);
					moteurs.arreter();
					Delay.msDelay(200);
					seDirigerVers(new Point(tabRobot[indiceRobot][x], tabRobot[indiceRobot][y]), pallet);
					moteurs.avancer();
				}
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
		if(nbIter == 9){
			double teta = 0;
			if (newTabRobot[indiceRobot][y] == newTabRobot2[indiceRobot][y]) {
				if (newTabRobot[indiceRobot][x] > newTabRobot2[indiceRobot][x]) {
					teta = 90;
				} else
					teta = 270;
			} else {
				double tangenteTeta = 0;
				tangenteTeta = Math.abs((double)(newTabRobot[indiceRobot][x] - newTabRobot2[indiceRobot][x])
							/ (double)(newTabRobot[indiceRobot][y] - newTabRobot2[indiceRobot][y]));
				teta = Math.toDegrees(Math.atan(tangenteTeta));
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
			moteurs.setAngle(teta);
			
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
				
				Point coord = capteur.getCoord(capteur.getCurrentColor());
				cam.setRobot(coord.x, coord.y, indiceRobot);
				
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
