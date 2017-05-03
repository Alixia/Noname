package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Set;

public class Cam implements Runnable {
	private boolean[][] tabColisions; // Tableau gerant les collisions

	private Set<Integer> collisionsRobot1;
	private Set<Integer> collisionsRobot2;
	
	private Surveillance[] surveillance;

	private DatagramSocket dSocket;
	private DatagramPacket dPacket;
	private byte[] buffer;

	private int[][] tabElements; // Contient tous les robots et palets
	private int[] indiceRobots; // Contient l'indice des robots
	private int[] indicePalets; // Contient l'indice des palets
	
	private int nbPalets;
	private int nbRobots;
	private int nbTot;

	// GESTION DU TABLEAU D'ELEMENTS
	final private int nbDim = 3;
	final private int INDICE = 0;
	final private int X = 1;
	final private int Y = 2;

	final private int nbMesures = 15;
	final private int distanceColision = 15;

	// Constructeur
	public Cam(int nbPal, int nbRob) {
		this.nbPalets = nbPal;
		this.nbRobots = nbRob;
		this.nbTot = nbPalets + nbRobots;
		this.tabElements = new int[nbTot][nbDim];
		this.indiceRobots = new int[nbRobots];
		this.indicePalets = new int[nbPalets];
		for (int i = 0; i < nbTot; i++) {
			if (i < nbPalets)
				indicePalets[i] = i;
			else
				indiceRobots[i - nbPalets] = i;
		}
		collisionsRobot1 = new HashSet<>();
		collisionsRobot2 = new HashSet<>();
		surveillance = new Surveillance[nbPalets + nbRobots];
		for (int i = 0; i < surveillance.length; i++) {
			surveillance[i] = new Surveillance();
		}
		initialisation();
	}

	private void initialisation() {
		int port = 8888;
		try {
			// Create a socket to listen on the port.
			dSocket = new DatagramSocket(port);

			// Create a buffer to read datagrams into. If a
			// packet is larger than this buffer, the
			// excess will simply be discarded!
			buffer = new byte[2048];

			// Create a packet to receive data into the buffer
			dPacket = new DatagramPacket(buffer, buffer.length);
			dSocket.receive(dPacket);
			
			String msg = new String(buffer, 0, dPacket.getLength());
			dPacket.setLength(buffer.length);

			String[] buff = msg.split("\n");

			int indiceRobotYMin = 0;
			int indiceRobotYMax = 1;
			tabElements[indiceRobots[indiceRobotYMin]][Y] = 500;
			tabElements[indiceRobots[indiceRobotYMax]][Y] = 0;

			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				int currentX = Integer.parseInt(coord[X]);
				int currentY = Integer.parseInt(coord[Y]);
				if (currentY < tabElements[indiceRobots[indiceRobotYMin]][Y]) {
					tabElements[indiceRobots[indiceRobotYMin]][X] = currentX;
					tabElements[indiceRobots[indiceRobotYMin]][Y] = currentY;
					tabElements[indiceRobots[indiceRobotYMin]][INDICE] = i;
				}
				if (currentY > tabElements[indiceRobots[indiceRobotYMax]][Y]) {
					tabElements[indiceRobots[indiceRobotYMax]][X] = currentX;
					tabElements[indiceRobots[indiceRobotYMax]][Y] = currentY;
					tabElements[indiceRobots[indiceRobotYMax]][INDICE] = i;
				}
			}

			int indicePalet = 0;
			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				int currentX = Integer.parseInt(coord[X]);
				int currentY = Integer.parseInt(coord[Y]);
				int index = i;

				if (index != tabElements[indiceRobots[indiceRobotYMin]][INDICE]
						&& index != tabElements[indiceRobots[indiceRobotYMax]][INDICE]) {
					tabElements[indicePalet][INDICE] = index;
					tabElements[indicePalet][X] = currentX;
					tabElements[indicePalet][Y] = currentY;
					indicePalet++;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void MAJCoords(String msg) {
		// System.out.println("MajCoords");

		boolean[] bElements = new boolean[nbTot];

		tabColisions = new boolean[nbTot][nbTot];

		for (int i = 0; i < nbTot; i++) {
			bElements[i] = false;
			for (int j = 0; j < nbTot; j++) {
				tabColisions[i][j] = false;
			}
		}

		String[] buff = msg.split("\n");

		// System.out.println("nbre donnees reçues: " + buff.length);
		for (int currentElt = 0; currentElt < buff.length; currentElt++) {
			String[] coord = buff[currentElt].split(";");
			int currentX = Integer.parseInt(coord[X]);
			int currentY = Integer.parseInt(coord[Y]);
			int index = currentElt;

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
					System.out.println("COLLISION" + currentElt + " " + i);
					tabColisions[i][currentElt] = true;
				} else {
					// System.out.println("palets pas colision: "+current);
				}

				if (minDistance > currentDistance && bElements[i] == false) {
					minDistance = currentDistance;
					indexMinDistance = i;
				}
			}
			// si cet index est sous surveillance
			if (surveillance[indexMinDistance].estSurveille) {
				// si on est pas au bout de cette surveillance, on actualise les
				// distances
				if (surveillance[indexMinDistance].mesure > 0) {
					surveillance[indexMinDistance].mesure--;
					int diffX = (currentX - tabElements[indexMinDistance][X]);
					int diffY = (currentY - tabElements[indexMinDistance][Y]);
					surveillance[indexMinDistance].distance += Math.floor(Math.sqrt(diffX * diffX + diffY * diffY));
				} else {
					// mettre les objets qui étaient en collision dans le bon
					// tableau
					int indexCollision = surveillance[indexMinDistance].indexCollision;
					// un palet est entré avec un robot qui été déjà en
					// collision
					System.out.println("ROBOT < PALET ? ");
					if (surveillance[indexMinDistance].distance > surveillance[indexCollision].distance) {
						if (indexMinDistance < nbPalets) {
							System.out.println("(p)ROBOT < PALET " + "ic=" + indexCollision + "im=" + indexMinDistance);
							tabElements[indexCollision - nbRobots][INDICE] = index;
							tabElements[indexCollision - nbRobots][X] = currentX;
							tabElements[indexCollision - nbRobots][Y] = currentY;

							bElements[indexCollision - nbRobots] = true;
							surveillance[indexCollision].estSurveille = false;
						} else {
							System.out.println("ROBOT > PALET ");
							tabElements[indexMinDistance][INDICE] = index;
							tabElements[indexMinDistance][X] = currentX;
							tabElements[indexMinDistance][Y] = currentY;

							bElements[indexMinDistance] = true;
							surveillance[indexMinDistance + 9].estSurveille = false;
						}

					} else if (surveillance[indexMinDistance].distance < surveillance[indexCollision].distance) {
						if (indexMinDistance < nbPalets) {
							System.out.println("ROBOT > PALET ");
							tabElements[indexMinDistance][INDICE] = index;
							tabElements[indexMinDistance][X] = currentX;
							tabElements[indexMinDistance][Y] = currentY;

							bElements[indexMinDistance] = true;
							surveillance[indexMinDistance].estSurveille = false;
						} else {
							System.out.println("(p)ROBOT < PALET " + "ic=" + indexCollision + "im=" + indexMinDistance);
							tabElements[indexCollision][INDICE] = index;
							tabElements[indexCollision][X] = currentX;
							tabElements[indexCollision][Y] = currentY;

							bElements[indexCollision] = true;
							surveillance[indexCollision].estSurveille = false;
						}
					} else {
						surveillance[indexMinDistance].mesure++;
					}

				}
			} else {
				tabElements[indexMinDistance][INDICE] = index;
				tabElements[indexMinDistance][X] = currentX;
				tabElements[indexMinDistance][Y] = currentY;
				bElements[indexMinDistance] = true;
			}
		}

		int robot1 = this.indiceRobots[0];
		int robot2 = this.indiceRobots[1];
		Set<Integer> newcollisionsRobot1 = new HashSet<>();
		Set<Integer> newcollisionsRobot2 = new HashSet<>();
		for (int i = 0; i < tabColisions.length; i++) {
			if (tabColisions[robot1][i]) {
				for (int j = 0; j < tabColisions.length; j++) {
					if (tabColisions[j][i] && j != robot1) {
						// colision robot1 et j
						newcollisionsRobot1.add(j);
					}
				}
			}

			if (tabColisions[robot2][i]) {
				for (int j = 0; j < tabColisions.length; j++) {
					if (tabColisions[j][i] && j != robot2) {
						// colision robot2 et j
						newcollisionsRobot2.add(j);
					}
				}
			}
		}

		// donner le même x/y au palet en collision avec un robot
		for (Integer palets : collisionsRobot1) {
			tabElements[palets][INDICE] = tabElements[indiceRobots[0]][INDICE];
			tabElements[palets][X] = tabElements[indiceRobots[0]][X];
			tabElements[palets][Y] = tabElements[indiceRobots[0]][Y];
		}

		for (Integer palets : collisionsRobot2) {
			tabElements[palets][INDICE] = tabElements[indiceRobots[1]][INDICE];
			tabElements[palets][X] = tabElements[indiceRobots[1]][X];
			tabElements[palets][Y] = tabElements[indiceRobots[1]][Y];
		}

		for (Integer palet : newcollisionsRobot1) {
			if (!collisionsRobot1.contains(palet)) {
				// on sort de collision!
				// surveiller les distances parcourues par les 2 objets
				// celui qui a parcouru le plus de distance en 5 (a ajuster)
				// mesures sera considéré comme le robot

				// le robot ne surveille qu'un seul palet, les palets qui
				// entrent en collision avec lui alors qu'il est déjà en
				// collision sont traités comme des palets.

				surveillance[robot1].estSurveille = true;
				surveillance[robot1].distance = 0;
				surveillance[robot1].mesure = nbMesures;
				surveillance[robot1].indexCollision = palet;
				surveillance[robot1].posX = tabElements[robot1][X];
				surveillance[robot1].posY = tabElements[robot1][Y];

				surveillance[palet].estSurveille = true;
				surveillance[palet].distance = 0;
				surveillance[palet].mesure = nbMesures;
				surveillance[palet].indexCollision = robot1;
				surveillance[palet].posX = tabElements[palet][X];
				surveillance[palet].posY = tabElements[palet][Y];
				// retirer le palet de la liste des collisions avec le robot
				collisionsRobot1.remove(palet);
			}
		}

		for (Integer pal : newcollisionsRobot2) {
			if (!collisionsRobot2.contains(pal)) {
				// on sort de collision!
				// surveiller les distances parcourues par les 2 objets
				// celui qui a parcouru le plus de distance en 5 (a ajuster)
				// mesures sera considéré comme le robot
				// le robot ne surveille qu'un seul palet, les palets qui
				// entrent en collision avec lui alors qu'il est déjà en
				// collision sont traités comme des palets.
				// if (surveillance[robot2][0] == 0) {
				surveillance[robot2].estSurveille = true;
				surveillance[robot2].distance = 0;
				surveillance[robot2].mesure = nbMesures;
				surveillance[robot2].indexCollision = pal;
				surveillance[robot2].posX = tabElements[robot2][X];
				surveillance[robot2].posY = tabElements[robot2][Y];
				// }
				surveillance[pal].estSurveille = true;
				surveillance[pal].distance = 0;
				surveillance[pal].mesure = nbMesures;
				surveillance[pal].indexCollision = robot2;
				surveillance[pal].posX = tabElements[pal][X];
				surveillance[pal].posY = tabElements[pal][Y];
				// retirer le palet de la liste des collisions avec le robot
				collisionsRobot2.remove(pal);
			}
		}
	}

	public int[][] getElements() {
		return tabElements;
	}

	public String afficheColisions() {
		String buffer = "";
		int lignes = tabColisions.length;
		int colonnes = tabColisions[0].length;
		for (int i = 0; i < lignes; i++) {
			for (int j = 0; j < colonnes; j++) {
				if (tabColisions[i][j]) {
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
				System.out.println(afficheColisions());
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