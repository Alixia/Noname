package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import Noname.API.APICamera;

public class Camera implements Runnable, APICamera {
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
	private int nbTot;

	// GESTION DU TABLEAU D'ELEMENTS
	final private int nbDim = 3;
	// Les dimensions sont : ---------------- c'est quoi ce commentaire de merde
	final private int INDICE = 0;
	final private int X = 1;
	final private int Y = 2;

	// VAR. GLOBALES
	final private int nbMesures = 15;
	final private int distanceColision = 30;

	final private int nbPalets = 9;
	final private int nbRobots = 2;

	// Constructeur
	public Camera() {
		this.nbTot = nbPalets + nbRobots;
		this.tabElements = new int[nbTot][nbDim];
		this.indiceRobots = new int[nbRobots];
		this.indicePalets = new int[nbPalets];
		// Initialisation des indices des elements
		for (int i = 0; i < nbTot; i++) {
			if (i < nbPalets) { // palets
				indicePalets[i] = i;
			} else { // robots
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
			tabElements[indiceRobots[0]][Y] = 500;
			tabElements[indiceRobots[1]][Y] = 0;

			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				// Coordonnees actuelles des robots
				int currentX = Integer.parseInt(coord[X]);
				int currentY = Integer.parseInt(coord[Y]);
				if (currentY < tabElements[indiceRobots[0]][Y]) {
					tabElements[indiceRobots[0]][X] = currentX;
					tabElements[indiceRobots[0]][Y] = currentY;
					tabElements[indiceRobots[0]][INDICE] = i;
				}
				if (currentY > tabElements[indiceRobots[1]][Y]) {
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
				if (i != tabElements[indiceRobots[0]][INDICE] && i != tabElements[indiceRobots[1]][INDICE]) {
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

	public int[][] getElements() {
		return tabElements;
	}
	
	public int[][] getPalets(){
		int[][] tabPalets = new int[nbPalets][nbDim];
		for(int i=0;i<nbPalets;i++){
			tabPalets[i][INDICE] = tabElements[i][INDICE];
			tabPalets[i][X] = tabElements[i][X];
			tabPalets[i][Y] = tabElements[i][Y];
		}
		return tabPalets;
	}
	
	public int[][] getRobots(){
		int[][] tabRobots = new int[nbRobots][nbDim];
		for(int i=nbPalets;i<nbRobots + nbPalets;i++){
			tabRobots[i-nbPalets][INDICE] = tabElements[i][INDICE];
			tabRobots[i-nbPalets][X] = tabElements[i][X];
			tabRobots[i-nbPalets][Y] = tabElements[i][Y];
		}
		return tabRobots;
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
		for (int i = 0; i < indicePalets.length; i++) {
			buff += tabElements[indicePalets[i]][INDICE] + ":" + tabElements[indicePalets[i]][X] + " / " + tabElements[indicePalets[i]][Y] + "\n";
		}
		buff += "\n";
		for (int i = 0; i < indiceRobots.length; i++) {
			buff += tabElements[indiceRobots[i]][INDICE] + ":" + tabElements[indiceRobots[i]][X] + " / " + tabElements[indiceRobots[i]][Y] + "\n";
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
		Cam2 c = new Cam2();
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

				MAJCoords(msg);
				dPacket.setLength(buffer.length);
				// Thread.sleep(500);
				iter++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	
	private boolean estRobot(int index){
		return index >= nbPalets;
	}
	
	public void MAJCoords(String msg) {
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
		int[][] buffer = new int[buff.length][3];
		for (int currentElt = 0; currentElt < buff.length; currentElt++) {
			String[] coord = buff[currentElt].split(";");
			buffer[currentElt][X]  = Integer.parseInt(coord[X]);
			buffer[currentElt][Y]  = Integer.parseInt(coord[Y]);
			buffer[currentElt][INDICE]  = 0;
		}

		for (int i = 0; i < nbTot; i++) {
			
			double minDistance = 500;
			int indexMinDistance = 0;
			
			for (int currentElt = 0; currentElt < buffer.length; currentElt++) {
				int diffX = buffer[currentElt][X] - tabElements[i][X];
				int diffY = buffer[currentElt][Y] - tabElements[i][Y];
				double currentDistance = Math.sqrt(diffX * diffX + diffY * diffY);
				// Trouver la plus courte distance
				if (minDistance > currentDistance) {
					minDistance = currentDistance;
					indexMinDistance = currentElt;
				}
			}
			tabElements[i][X] = buffer[indexMinDistance][X];
			tabElements[i][Y] = buffer[indexMinDistance][Y];
			buffer[indexMinDistance][INDICE]++;

		}
		
		
		for (int currentElt = 0; currentElt < buffer.length; currentElt++) {
			if(buffer[currentElt][INDICE] == 0){
				ArrayList<Integer> collison = new ArrayList<>();
				double minDistance = 500;
				for (int i = 0; i < nbTot; i++) {
					int diffX = buffer[currentElt][X] - tabElements[i][X];
					int diffY = buffer[currentElt][Y] - tabElements[i][Y];
					double currentDistance = Math.sqrt(diffX * diffX + diffY * diffY);
					if (minDistance > currentDistance) {
						minDistance = currentDistance;
						collison.clear();
					}
					if (minDistance == currentDistance){
						collison.add(i);
					}
				}
				tabElements[collison.get(0)][X] = buffer[currentElt][X];
				tabElements[collison.get(0)][Y] = buffer[currentElt][Y];
				
				surveillance[collison.get(0)].index = collison.get(0);				
				surveillance[collison.get(0)].indexCollision = collison.get(1);
				surveillance[collison.get(0)].estSurveille = true;				
				surveillance[collison.get(0)].distance = 0;
				surveillance[collison.get(0)].mesure = nbMesures;
				surveillance[collison.get(0)].posX = buffer[currentElt][X];
				surveillance[collison.get(0)].posY = buffer[currentElt][Y];
				
				
				surveillance[collison.get(1)].index = collison.get(1);				
				surveillance[collison.get(1)].indexCollision = collison.get(0);
				surveillance[collison.get(1)].estSurveille = true;				
				surveillance[collison.get(1)].distance = 0;
				surveillance[collison.get(1)].mesure = nbMesures;
				surveillance[collison.get(1)].posX = tabElements[collison.get(1)][X];
				surveillance[collison.get(1)].posY = tabElements[collison.get(1)][Y];	
				
				
				
			}
		}
		
		
		for(int indexMinDistance = 0 ; indexMinDistance < surveillance.length; indexMinDistance++){
			if (surveillance[indexMinDistance].estSurveille) {
				// Si le compteur n'est pas fini
				if (surveillance[indexMinDistance].mesure > 0) {
					surveillance[indexMinDistance].mesure--;
				} else { // Gestion des objets en collision
					
					int monIndex = surveillance[indexMinDistance].index;
					int posX = surveillance[monIndex].posX;
					int posY = surveillance[monIndex].posY;
					
					int diffX = posX - tabElements[monIndex][X];
					int diffY = posY - tabElements[monIndex][Y];
					surveillance[monIndex].distance += Math.floor(Math.sqrt(diffX * diffX + diffY * diffY));

					
					int indexCollision = surveillance[indexMinDistance].indexCollision;
					posX = surveillance[indexCollision].posX;
					posY = surveillance[indexCollision].posY;
					diffX = posX - tabElements[indexCollision][X];
					diffY = posY - tabElements[indexCollision][Y];
					surveillance[indexCollision].distance += Math.floor(Math.sqrt(diffX * diffX + diffY * diffY));
					
					if(surveillance[monIndex].distance == surveillance[indexCollision].distance){
						surveillance[monIndex].mesure++;
					}
					else if (((surveillance[monIndex].distance < surveillance[indexCollision].distance) == estRobot(monIndex))) {
					
							int tempX = tabElements[monIndex][X];
							int tempY = tabElements[monIndex][Y];
							tabElements[monIndex][X] = tabElements[indexCollision][X];
							tabElements[monIndex][Y] = tabElements[indexCollision][Y];
							tabElements[indexCollision][X] = tempX;
							tabElements[indexCollision][Y] = tempY;

							surveillance[monIndex].estSurveille = false;
							surveillance[indexCollision].estSurveille = false;
					}
					else{
						surveillance[monIndex].estSurveille = false;
						surveillance[indexCollision].estSurveille = false;
					}
				}
			}
		}
	}
}