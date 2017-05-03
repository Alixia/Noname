package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Set;

public class Cam implements Runnable {
	// GESTION DES COLLISIONS
	private boolean[][] tabCollisions; // Tableau gerant les collisions
	private Set<Integer> collisionsRobot1; // Collisions du robot 1
	private Set<Integer> collisionsRobot2;
	private Surveillance[] surveillance; // Surveille les collisions
	
	// GESTION DE LA CAMERA
	private DatagramSocket dSocket;
	private DatagramPacket dPacket;
	private byte[] buffer;
	
	// GESTION DES ELTS SUR TERRAIN
	private int[][] tabElements; // Contient tous les robots et palets
	private int[] indiceRobots; // Contient l'indice des robots
	private int[] indicePalets; // Contient l'indice des palets
	private int nbPalets;
	private int nbRobots;
	private int nbTot;
	
	// GESTION DU TABLEAU D'ELEMENTS
	final private int nbDim = 3;
		// Les dimensions sont :
	final private int INDICE = 0;
	final private int X = 1;
	final private int Y = 2;
	
	// VAR. GLOBALES
	final private int nbMesures = 15;
	final private int distanceColision = 30;

	// Constructeur
	public Cam(int nbPal, int nbRob) {
		this.nbPalets = nbPal;
		this.nbRobots = nbRob;
		this.nbTot = nbPalets + nbRobots;
		this.tabElements = new int[nbTot][nbDim];
		this.indiceRobots = new int[nbRobots];
		this.indicePalets = new int[nbPalets];
		// Initialisation des indices des elements
		for (int i = 0; i < nbTot; i++) {
			if (i < nbPalets){ // palets
				indicePalets[i] = i;
			} else{ // robots
				indiceRobots[i - nbPalets] = i;
			}
		}
		collisionsRobot1 = new HashSet<>();
		collisionsRobot2 = new HashSet<>();
		surveillance = new Surveillance[nbPalets + nbRobots];
		for (int i = 0; i < surveillance.length; i++) {
			surveillance[i] = new Surveillance();
		}
		initialisation();
	}

	// Initialisation de la camera et des positions
	private void initialisation() {
		int port = 8888;
		try {
			// Create a socket to listen on the port.
			dSocket = new DatagramSocket(port);

			// Create a buffer to read datagrams into. If a packet is larger
			// than this buffer, the excess will simply be discarded!
			buffer = new byte[2048];

			// Create a packet to receive data into the buffer
			dPacket = new DatagramPacket(buffer, buffer.length);
			dSocket.receive(dPacket);

			String msg = new String(buffer, 0, dPacket.getLength());
			dPacket.setLength(buffer.length);

			String[] buff = msg.split("\n");

			// Indice 0 contient yMax, 1 : yMin
			tabElements[indiceRobots[0]][Y] = 1000;
			tabElements[indiceRobots[1]][Y] = -100;

			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				// Coordonnees actuelles des robots
				int currentX = Integer.parseInt(coord[X]);
				int currentY = Integer.parseInt(coord[Y]);
				if (currentY < tabElements[indiceRobots[indiceRobots[0]]][Y]) {
					tabElements[indiceRobots[0]][X] = currentX;
					tabElements[indiceRobots[0]][Y] = currentY;
					tabElements[indiceRobots[0]][INDICE] = i;
				}
				if (currentY > tabElements[indiceRobots[indiceRobots[1]]][Y]) {
					tabElements[indiceRobots[1]][X] = currentX;
					tabElements[indiceRobots[1]][Y] = currentY;
					tabElements[indiceRobots[1]][INDICE] = i;
				}
			}
			// Permet de remplir position et indices des palets
			int indicePalet = 0;
			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				int currentX = Integer.parseInt(coord[X]);
				int currentY = Integer.parseInt(coord[Y]);
				if (i != tabElements[indiceRobots[0]][INDICE]
						&& i != tabElements[indiceRobots[1]][INDICE]) {
					tabElements[indicePalet][INDICE] = i;
					tabElements[indicePalet][X] = currentX;
					tabElements[indicePalet][Y] = currentY;
					indicePalet++;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// Permet de mettre a jour les coordonnees 
	public void MAJCoords(String msg) {
		// System.out.println("MajCoords");
		boolean[] bElements = new boolean[nbTot];
		tabCollisions = new boolean[nbTot][nbTot];
		// Initialise toutes les collisions a faux
		for (int i = 0; i < nbTot; i++) {
			bElements[i] = false;
			for (int j = 0; j < nbTot; j++) {
				tabCollisions[i][j] = false;
			}
		}

		String[] buff = msg.split("\n");

		// System.out.println("nbre donnees reçues: " + buff.length);
		for (int currentElt = 0; currentElt < buff.length; currentElt++) {
			String[] coord = buff[currentElt].split(";");
			int currentX = Integer.parseInt(coord[X]);
			int currentY = Integer.parseInt(coord[Y]);

			double minDistance = 500;
			int indexMinDistance = 0;

			double currentDistance;

			// Test de tous les elements
			for (int i = 0; i < nbTot; i++) {
				int diffX = currentX - tabElements[i][X];
				int diffY = currentY - tabElements[i][Y];
				currentDistance = Math.sqrt(diffX * diffX + diffY * diffY);
				// System.out.println("currentP = " + current );
				// Trouver la plus courte distance
				if (currentDistance <= distanceColision) {
					System.out.println("COLLISION" + currentElt + " " + i + " current= " + currentDistance);
					tabCollisions[i][currentElt] = true;
				} //else System.out.println("palets pas colision: "+current);
				if (minDistance > currentDistance && bElements[i] == false) {
					minDistance = currentDistance;
					indexMinDistance = i;
				}
			}

			int messageNumero = 1;
			System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
			// Si cet index est sous surveillance
			if (surveillance[indexMinDistance].estSurveille) {
				System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
				// Si le compteur n'est pas fini
				if (surveillance[indexMinDistance].mesure > 0) {
					System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
					surveillance[indexMinDistance].mesure--;
					int diffX = (currentX - tabElements[indexMinDistance][X]);
					int diffY = (currentY - tabElements[indexMinDistance][Y]);
					surveillance[indexMinDistance].distance += Math.floor(Math.sqrt(diffX * diffX + diffY * diffY));
				} else { // Gestion des objets en collision
					messageNumero +=10;
					int indexCollision = surveillance[indexMinDistance].indexCollision;
					System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
					if (surveillance[indexMinDistance].distance > surveillance[indexCollision].distance) {
						if (indexMinDistance < nbPalets) { // Palet
							System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
							System.out.println("(p)ROBOT < PALET " + "ic=" + indexCollision + "im=" + indexMinDistance);
							tabElements[indexCollision - nbPalets][INDICE] = currentElt;
							tabElements[indexCollision - nbPalets][X] = currentX;
							tabElements[indexCollision - nbPalets][Y] = currentY;

							bElements[indexCollision - nbPalets] = true;
							surveillance[indexCollision].estSurveille = false;
						} else { // Robot
							System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
							System.out.println("ROBOT > PALET ");
							tabElements[indexMinDistance][INDICE] = currentElt;
							tabElements[indexMinDistance][X] = currentX;
							tabElements[indexMinDistance][Y] = currentY;

							bElements[indexMinDistance] = true;
							surveillance[indexMinDistance].estSurveille = false;
						}
					} else if (surveillance[indexMinDistance].distance < surveillance[indexCollision].distance) {
						messageNumero +=100;
						if (indexMinDistance < nbPalets) { // Palet
							System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
							System.out.println("ROBOT > PALET ");
							tabElements[indexMinDistance][INDICE] = currentElt;
							tabElements[indexMinDistance][X] = currentX;
							tabElements[indexMinDistance][Y] = currentY;

							bElements[indexMinDistance] = true;
							surveillance[indexMinDistance].estSurveille = false;
						} else { // Robot
							System.out.println(messageNumero++ + " : surveille = " + surveillance[indexMinDistance].toString());
							System.out.println("(p)ROBOT < PALET " + "ic=" + indexCollision + "im=" + indexMinDistance);
							tabElements[indexCollision][INDICE] = currentElt;
							tabElements[indexCollision][X] = currentX;
							tabElements[indexCollision][Y] = currentY;

							bElements[indexCollision] = true;
							surveillance[indexMinDistance].estSurveille = false;
						}
					} else {
						System.out.println(1000 + " : surveille = " + surveillance[indexMinDistance].toString());
						surveillance[indexMinDistance].mesure++;
					}

				}
			} else {
				System.out.println(1001 + " : surveille = " + surveillance[indexMinDistance].toString());
				tabElements[indexMinDistance][INDICE] = currentElt;
				tabElements[indexMinDistance][X] = currentX;
				tabElements[indexMinDistance][Y] = currentY;
				bElements[indexMinDistance] = true;
			}
		}
		// Test s'il y a des collisions
		for (int i = 0; i < tabCollisions.length; i++) {
			for(int indexRobot = 0; indexRobot < nbRobots;indexRobot++){
				if (tabCollisions[indiceRobots[indexRobot]][i]) {
					for (int j = 0; j < tabCollisions.length; j++) {
						// Collision entre le robot indice indexRobot et le palet j
						if (tabCollisions[j][i] && j != indiceRobots[indexRobot]) {
							if(indexRobot==0)
								collisionsRobot1.add(j);
							else
								collisionsRobot2.add(j);
						}
					}
				}
			}
		}

		// Donner les memes coord au robot en collision avec palet
		Set<Integer> collisionSet;
		for(int indexRobot = 0; indexRobot<nbRobots;indexRobot++){
			if(indexRobot == 0)
				collisionSet = collisionsRobot1;
			else
				collisionSet = collisionsRobot2;
			for (Integer palets : collisionSet) {
				tabElements[palets][INDICE] = tabElements[indiceRobots[indexRobot]][INDICE];
				tabElements[palets][X] = tabElements[indiceRobots[indexRobot]][X];
				tabElements[palets][Y] = tabElements[indiceRobots[indexRobot]][Y];
			}
		}
		// Test sur les non collisions
		// Surveiller les distances sur les 2 objets
		// La plus grande distance sera consideree comme robot
		// Le robot ne surveille qu'un seul palet,
		// Les autres restent consideres comme palets
		Set<Integer> currentSet;
		for(int indexRobot = 0; indexRobot<nbRobots;indexRobot++){
			if(indexRobot == 0)
				currentSet = collisionsRobot1;
			else
				currentSet = collisionsRobot2;
			for (Integer palet : currentSet) {
				if (!currentSet.contains(palet)) {
					System.out.println("CurrentSet = " + indexRobot + " : " + currentSet.toString());
					// Surveillance du robot
					surveillance[indiceRobots[indexRobot]].index = indiceRobots[indexRobot];
					surveillance[indiceRobots[indexRobot]].estSurveille = true;
					surveillance[indiceRobots[indexRobot]].distance = 0;
					surveillance[indiceRobots[indexRobot]].mesure = nbMesures;
					surveillance[indiceRobots[indexRobot]].indexCollision = palet;
					surveillance[indiceRobots[indexRobot]].posX = tabElements[indiceRobots[indexRobot]][X];
					surveillance[indiceRobots[indexRobot]].posY = tabElements[indiceRobots[indexRobot]][Y];
					// Surveillance du palet
					surveillance[palet].index = palet;
					surveillance[palet].estSurveille = true;
					surveillance[palet].distance = 0;
					surveillance[palet].mesure = nbMesures;
					surveillance[palet].indexCollision = indiceRobots[indexRobot];
					surveillance[palet].posX = tabElements[palet][X];
					surveillance[palet].posY = tabElements[palet][Y];
					// Retire le palet en collision
					currentSet.remove(palet);
				}
			}
			System.out.println(10000 + " : surveille = " + surveillance[indexRobot].toString());
		}
	}

	public int[][] getElements() {
		return tabElements;
	}

	public String afficheCollisions() {
		String buffer = "";
		int lignes = tabCollisions.length;
		int colonnes = tabCollisions[0].length;
		for (int i = 0; i < lignes; i++) {
			for (int j = 0; j < colonnes; j++) {
				if (tabCollisions[i][j]) {
					buffer += "1 ";
				} else {
					buffer += "0 ";
				}
			}
			buffer += "\n";
		}
		return buffer;
	}

	public String afficheElements() {
		String buff = "";
		for (int i = 0; i < tabElements.length; i++) {
			buff += tabElements[i][INDICE] + ":" + tabElements[i][X] + " / " + tabElements[i][Y] + "\n";
		}
		return buff;
	}

	public String afficheSurveillance() {
		String buff = "";
		for (int i = 0; i < surveillance.length; i++) {
			buff += "i:" + i;
			buff += " index:" + surveillance[i].index;
			buff += " surv? " + surveillance[i].estSurveille;
			buff += " dist: " + surveillance[i].distance;
			buff += " mesures: " + surveillance[i].mesure;
			buff += " collision avec " + surveillance[i].indexCollision;
			buff += " pos: " + surveillance[i].posX + " / " + surveillance[i].posY;
			buff += "\n";
		}
		return buff;
	}

	public static void main(String[] args) {
		Cam c = new Cam(9, 2);
		Thread t = new Thread(c);
		t.run();
	}

	@Override
	public void run() {
		int iter = 0;
		try {
			// Create a socket to listen on the port.
			// DatagramSocket dsocket = new DatagramSocket(port);

			// Create a buffer to read datagrams into. If a
			// packet is larger than this buffer, the
			// excess will simply be discarded!
			// byte[] buffer = new byte[2048];

			// Create a packet to receive data into the buffer
			// DatagramPacket packet = new DatagramPacket(buffer,
			// buffer.length);
			while (true) {
				dSocket.receive(dPacket);

				// Convert the contents to a string, and display them
				String msg = new String(buffer, 0, dPacket.getLength());
				System.out.println("------------debut " + iter + "--------------");
				// System.out.println(msg);

				MAJCoords(msg);
				System.out.println(afficheElements());
				System.out.println(afficheSurveillance());
				System.out.println(afficheCollisions());
				System.out.println("---------------fin------------------");
				dPacket.setLength(buffer.length);
				// Thread.sleep(500);
				iter++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}