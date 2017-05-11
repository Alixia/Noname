package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Kameha implements Runnable {
	// GESTION DES COLLISIONS

	// GESTION DE LA CAMERA
	private DatagramSocket dSocket;
	private DatagramPacket dPacket;
	private byte[] buffer;

	// GESTION DES ELTS SUR TERRAIN
	final private int nbRobots = 2;
	final private int indiceR1 = 0; // Contient l'indice du robot 1
	final private int indiceR2 = 1; // Contient l'indice du robot 2
	private ArrayList tabElements; // Contient tous les robots et palets

	// Constructeur
	public Camera() {
		this.tabElements = new ArrayList();
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

			tabElements.add(indiceR1, new Point(100,500));
			tabElements.add(indiceR2, new Point(100,0));
			
			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				// Coordonnees actuelles des robots
				int currentX = Integer.parseInt(coord[X]);
				int currentY = Integer.parseInt(coord[Y]);
				if (currentY < (int)tabElements.get(indiceR1).getY()) 
					tabElements.add(indiceR1, new Point(currentX,currentY));
				else if (currentY > (int)tabElements.get(indiceR2).getY())
					tabElements.add(indiceR2, new Point(currentX,currentY));
				else
					tabElements.add(new Point(currentX,currentY));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public int[][] getPalets() {
		int[][] tabPalets = new int[tabElements.length-nbRobots][3];
		for (int i = 2; i < tabElements.length; i++) {
			tabPalets[i][0] = i;
			tabPalets[i][1] = tabElements.get(i).getX();
			tabPalets[i][2] = tabElements.get(i).getY();
		}
		return tabPalets;
	}

	public int[][] getRobots(){
		int[][] tabRobots = new int[tabElements.length-nbRobots][3];
		for (int i = 0; i < nbRobots; i++) {
			tabRobots[i][0] = i;
			tabRobots[i][1] = tabElements.get(i).getX();
			tabRobots[i][2] = tabElements.get(i).getY();
		}
		return tabPalets;
	}

	public String afficheElements() {
		String buff = "Robots : \n";
		for (int i = 0; i < nbRobots; i++) 
			buff += i + ":" + tabElements.get(i).getX() + " / " + tabElements.get(i).getY() + "\n";
		buff += "Palets : \n";
		for (int i = nbRobots; i < tabElements.length; i++)
			buff += i + ":" + tabElements.get(i).getX() + " / " + tabElements.get(i).getY() + "\n";
		return buff;
	}

	public static void main(String[] args) {
		Kameha c = new Kameha();
		Thread t = new Thread(c);
		t.run();
	}

	@Override
	public void run() {
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
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean estRobot(int index) {
		return index <= nbRobots;
	}

	private void MAJCoords(String msg) {
		String[] buff = msg.split("\n");
		Point[] buffer = new Point[buff.length];
		for (int currentElt = 0; currentElt < buff.length; currentElt++) {
			String[] coord = buff[currentElt].split(";");
			buffer[currentElt] = new Point(Integer.parseInt(coord[X]),Integer.parseInt(coord[Y]));
		}
		int elementStillPresent[] = new int[tabElements.length];
		for (int i = 0; i < tabElements.length; i++) {
			elementStillPresent[i] = false;
			double minDistance = 500;
			int indexMinDistance = 0;
			for (int currentElt = 0; currentElt < buffer.length; currentElt++) {
				int diffX = buffer.get(currentElt).getX() - tabElements.get(i).getX();
				int diffY = buffer.get(currentElt).getY() - tabElements.get(i).getY();
				double currentDistance = Math.sqrt(diffX * diffX + diffY * diffY);
				// Trouver la plus courte distance
				if (minDistance > currentDistance) {
					minDistance = currentDistance;
					indexMinDistance = currentElt;
				}
			}
			if(minDistance > 4){
				int diffX = buffer.get(currentElt).getX() - tabElements.get(indiceR1).getX();
				int diffY = buffer.get(currentElt).getY() - tabElements.get(indiceR1).getY();
				double distanceRobot1 = Math.sqrt(diffX * diffX + diffY * diffY);
				diffX = buffer.get(currentElt).getX() - tabElements.get(indiceR2).getX();
				diffY = buffer.get(currentElt).getY() - tabElements.get(indiceR2).getY();
				double distanceRobot2 = Math.sqrt(diffX * diffX + diffY * diffY);
				
				int distanceCollision = 20;
				// Robots confondus
				if(distanceRobot1 <= distanceRobot2 + distanceCollision && distanceRobot1 >= distanceRobot2 - distanceCollision){
					tabElements.setLocation(indiceR1,buffer.get(indexMinDistance));
					tabElements.setLocation(indiceR2,buffer.get(indexMinDistance));
				} else if (distanceRobot1 < distanceRobot2){ // C'est probablement le robot 1
					tabElements.setLocation(indiceR1,buffer.get(indexMinDistance));
				} else { // C'est probablement le robot 2
					tabElements.setLocation(indiceR2,buffer.get(indexMinDistance));
				}
			} else {
				boolean elementFound = false;
				for(int i = 0 ; i < tabElements.length; i++){
					if(buffer.get(indexMinDistance).equals(tabElements.get(i))){
						elementFound = true;
						elementStillPresent[i] = true;
					}
				}
				if(!elementFound) // Ajouter les nouveaux palets
					tabElements.add(buffer.get(indexMinDistance));
			}
		}
		// Enlever les palets non disponibles
			for(int i=tabElement.length - 1; i >=nbRobots ;i--){
				if(elementStillPresent[i] = false)
					tabElement.remove(i);
			}
	}
}
