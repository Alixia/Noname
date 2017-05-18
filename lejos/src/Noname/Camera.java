package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Camera implements Runnable {
	private final boolean DEBUG = true;

	
	// GESTION DES COLLISIONS
	private boolean[][] tabCollisions; // Tableau gerant les collisions
	private Set<Integer> collisionsRobot1; // Collisions du robot 1
	private Set<Integer> collisionsRobot2;
	private Set<Surveillance>[] surveillance; // Surveille les collisions

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

	// gestion du set robot
	private boolean setrobot = false;
	private int setRobotX = -1;
	private int setRobotY = -1;
	public int numRobot = 0;

	// GESTION DU TABLEAU D'ELEMENTS
	final private int nbDim = 3;
	// Les dimensions sont : ---------------- c'est quoi ce commentaire de merde
	final private int INDICE = 0;
	final private int X = 1;
	final private int Y = 2;

	// VAR. GLOBALES
	final private int nbMesures = 5;

	final private int MOUVEMENTMAXPALET = 4;
	final private int MIN_DISTANCE_SWAP_ROBOT_PALET = 100;

	final private int nbPal = 9;
	final private int nbRob = 1;

	// Constructeur
	public Camera() {
		this.nbPalets = nbPal;
		this.nbRobots = nbRob;
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
		surveillance = new HashSet[nbPalets + nbRobots];
		for (int i = 0; i < surveillance.length; i++) {
			surveillance[i] = new HashSet<Surveillance>();
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
			if(nbRobots >= 2)
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
				if (nbRobots >= 2 && currentY > tabElements[indiceRobots[1]][Y]) {
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
				if ((nbRobots == 1 && i != tabElements[indiceRobots[0]][INDICE]) ||(nbRobots >= 2 && i != tabElements[indiceRobots[0]][INDICE] && i != tabElements[indiceRobots[1]][INDICE])) {
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

	public int[][] getPalets() {
		int[][] tabPalets = new int[nbPalets][nbDim];
		for (int i = 0; i < indicePalets.length; i++) {
			tabPalets[i][INDICE] = tabElements[indicePalets[i]][INDICE];
			tabPalets[i][X] = tabElements[indicePalets[i]][X];
			tabPalets[i][Y] = tabElements[indicePalets[i]][Y];
		}
		return tabPalets;
	}

	public int[][] getRobots() {
		int[][] tabRobots = new int[nbRobots][nbDim];
		for (int i = 0; i < indiceRobots.length; i++) {
			tabRobots[i][INDICE] = tabElements[indiceRobots[i]][INDICE];
			tabRobots[i][X] = tabElements[indiceRobots[i]][X];
			tabRobots[i][Y] = tabElements[indiceRobots[i]][Y];
		}
		return tabRobots;
	}

	public void setRobot(int x, int y, int nr) {
		setrobot = false;
		setRobotY = y;
		setRobotX = x;
		numRobot = nr;
	}

	public void afficheCollisions() {
		if(!DEBUG){return;}
		
		String buff = "";
		int lignes = tabCollisions.length;
		int colonnes = tabCollisions[0].length;
		for (int i = 0; i < lignes; i++) {
			for (int j = 0; j < colonnes; j++) {
				if (tabCollisions[i][j]) {
					buff += "1 ";
				} else {
					buff += "0 ";
				}
			}
			buff += "\n";
		}
		System.out.println(buff);
	}

	public void afficheElements() {
		if(!DEBUG){return;}

		String buff = "";
		for (int i = 0; i < indicePalets.length; i++) {
			buff += tabElements[indicePalets[i]][INDICE] + ":" + tabElements[indicePalets[i]][X] + " / "
					+ tabElements[indicePalets[i]][Y] + "\n";
		}
		buff += "\n";
		for (int i = 0; i < indiceRobots.length; i++) {
			buff += tabElements[indiceRobots[i]][INDICE] + ":" + tabElements[indiceRobots[i]][X] + " / "
					+ tabElements[indiceRobots[i]][Y] + "\n";
		}
		System.out.println(buff);
	}

	public void afficheSurveillance() {
		if(!DEBUG){return;}

		String buff = "";
		for (int i = 0; i < surveillance.length; i++) {
			for(Surveillance s : surveillance[i]){
				buff += s.toString();
			}
		}
		System.out.println(buff);
	}
	
	public void afficheTexte(String texte, int iter) {
		if(!DEBUG){return;}
		System.out.println("------- " + texte + " " + iter + "---------");
	}
	
	public static void main(String[] args) {
		Camera c = new Camera();
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
				afficheTexte("debut", iter);
				MAJCoords(msg);
				afficheElements();
				afficheSurveillance();
				afficheTexte("fin", iter);

				dPacket.setLength(buffer.length);
				// Thread.sleep(500);
				iter++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean estRobot(int index) {
		return index >= nbPalets;
	}
	
	private boolean sontTousRobots(int[] elements){
		boolean sontTousRobots = true;
		for(int e : elements){
			if(!estRobot(e))
				sontTousRobots = false;
		}
		return sontTousRobots;
	}

	private void affichetruc(String s, int t){
		if(!DEBUG){return;}
		
		System.out.println(s + " " + t);
	}
	
	private void swap(int a, int b){
		if (a != b){
			int echangeX = tabElements[a][X];
			int echangeY = tabElements[a][Y];
			tabElements[a][X] = tabElements[b][X];
			tabElements[a][Y] = tabElements[b][Y];
			tabElements[b][X] = echangeX;
			tabElements[b][Y] = echangeY;
		}
	}
	
	private void MAJCoords(String msg) {
		affichetruc("271 debut majcoords",0);	
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
			buffer[currentElt][X] = Integer.parseInt(coord[X]);
			buffer[currentElt][Y] = Integer.parseInt(coord[Y]);
			buffer[currentElt][INDICE] = 0;
		}

		int nbProbRobots = 0;
		int indProbRobot[] = new int[nbTot];
		for (int i = 0; i < nbTot; i++) {

			double minDistance = 500;
			int indexMinDistance = 0;

			//match des positions precedentes avec la position actuelle la plus proche
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
			if (minDistance > MOUVEMENTMAXPALET) {
				indProbRobot[nbProbRobots] = i;
				nbProbRobots++;
			}
			tabElements[i][X] = buffer[indexMinDistance][X];
			tabElements[i][Y] = buffer[indexMinDistance][Y];
			buffer[indexMinDistance][INDICE]++;

		}
		affichetruc("317 debut probRobots",0);
		
		

		//swap des prob (elements qui ont bougé) robots avec les robots si necessaire
		for(int r : indiceRobots){
			boolean contains = false;
			double minDistance = 500;
			int indexMinDistance = -1;
			for(int i = 0; i<nbProbRobots; i++){
				int e = indProbRobot[i];
				if(e == r){
					contains = true;
					break;
				}
				int diffX = tabElements[e][X] - tabElements[r][X];
				int diffY = tabElements[e][X] - tabElements[r][Y];
				double currentDistance = Math.sqrt(diffX * diffX + diffY * diffY);
				// Trouver la plus courte distance
				if (minDistance > currentDistance && !estRobot(e)) {
					minDistance = currentDistance;
					indexMinDistance = e;
				}
			}
			if(!contains && indexMinDistance != -1 && minDistance <= MIN_DISTANCE_SWAP_ROBOT_PALET){
				swap(indexMinDistance, r);
			}
		}
		
		
		
		//si de nouveaux point apparaissent, c'est qu'on est sorti d'une colision
		affichetruc("462 debut gestion des colisions",0);
		for (int currentElt = 0; currentElt < buffer.length; currentElt++) {
			if (buffer[currentElt][INDICE] == 0) {
				ArrayList<Integer> collison = new ArrayList<>();
				double minDistance = 500;
				int indSwap = -1;
				for(int e1 = 0; e1 < tabElements.length -1; e1++){
					for(int e2 = e1 + 1; e2 < tabElements.length; e2++){
						int diffX = buffer[currentElt][X] - tabElements[e1][X];
						int diffY = buffer[currentElt][Y] - tabElements[e1][Y];
						double currentDistance = Math.sqrt(diffX * diffX + diffY * diffY);
						if(tabElements[e1][X] == tabElements[e2][X] && tabElements[e1][Y] == tabElements[e2][Y] && currentDistance < minDistance)
							indSwap = estRobot(e1) ? e2 : e1;
					}
				}System.out.println("aaaaaaaaaaaaaaaaaaa");
				tabElements[indSwap][X] = buffer[currentElt][X];
				tabElements[indSwap][Y] = buffer[currentElt][Y];
			}
		}


		

		// si le thread principal veux indiquer la position reel du robot
		affichetruc("606 debut setrobot",0);
		if (setrobot && ((setRobotX != -1) || (setRobotY != -1))){
			double minDistance = 500;
			int indexMinDistance = 0;

			for (int currentElt = 0; currentElt < tabElements.length; currentElt++) {
				
				int diffX = setRobotX - tabElements[currentElt][X];
				int diffY = setRobotY - tabElements[currentElt][Y];
				
				if(setRobotX == -1){
					diffX = 0;
				}
				if(setRobotY == -1){
					diffY = 0;
				}
				double currentDistance = Math.sqrt(diffX * diffX + diffY * diffY);
				// Trouver la plus courte distance
				if (minDistance > currentDistance) {
					minDistance = currentDistance;
					indexMinDistance = currentElt;
				}
			}
			if (indiceRobots[numRobot] == indexMinDistance) {
				setrobot = false;
				setRobotX = -1;
				setRobotY = -1;
			} else {
				if (surveillance[indiceRobots[numRobot]].isEmpty() && surveillance[indexMinDistance].isEmpty()) {

					int echangeX = tabElements[indiceRobots[numRobot]][X];
					int echangeY = tabElements[indiceRobots[numRobot]][Y];
					tabElements[indiceRobots[numRobot]][X] = tabElements[indexMinDistance][X];
					tabElements[indiceRobots[numRobot]][Y] = tabElements[indexMinDistance][Y];
					tabElements[indexMinDistance][X] = echangeX;
					tabElements[indexMinDistance][Y] = echangeY;

					setrobot = false;
					setRobotX = -1;
					setRobotY = -1;
				}
			}
		}
	}
}