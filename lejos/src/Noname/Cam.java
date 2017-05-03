package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Set;

public class Cam implements Runnable {


	private int[][] palets;
	int[][] robots;
	private boolean[][] tabColisions;

	private int distColision;
	private Set<Integer> collisionsRobot1;
	private Set<Integer> collisionsRobot2;
	Surveillance[] surveillance;

	private DatagramSocket dsocket;
	private DatagramPacket packet;
	private byte[] buffer;

	public Cam() {
		palets = new int[9][3];
		robots = new int[2][3];
		distColision = 30;
		collisionsRobot1 = new HashSet<>();
		collisionsRobot2 = new HashSet<>();
		surveillance = new Surveillance[11];
		
		for (int i = 0; i < surveillance.length; i++) {
			surveillance[i] = new Surveillance();
		}
		init();
	}

	public Cam(int nbPalets, int nbRobots) {
		palets = new int[nbPalets][3];
		robots = new int[nbRobots][3];
		distColision = 15;

		init();
	}


	private void init() {
		int port = 8888;

		try {
			// Create a socket to listen on the port.
			dsocket = new DatagramSocket(port);

			// Create a buffer to read datagrams into. If a
			// packet is larger than this buffer, the
			// excess will simply be discarded!
			buffer = new byte[2048];

			// Create a packet to receive data into the buffer
			packet = new DatagramPacket(buffer, buffer.length);

			dsocket.receive(packet);

			String msg = new String(buffer, 0, packet.getLength());
			packet.setLength(buffer.length);

			String[] buff = msg.split("\n");
			int ipalet = 0;

			int indexMin = 0;
			int indexMax = 0;
			int ymin = 500;
			int xmin = 500;

			int ymax = 0;
			int xmax = 0;

			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				int x = Integer.parseInt(coord[1]);
				int y = Integer.parseInt(coord[2]);

				if (y < ymin) {
					ymin = y;
					xmin = x;
					indexMin = i;
				}

				if (y > ymax) {
					ymax = y;
					xmax = x;
					indexMax = i;
				}

			}

			robots[0][0] = indexMin;
			robots[0][1] = xmin;
			robots[0][2] = ymin;

			robots[1][0] = indexMax;
			robots[1][1] = xmax;
			robots[1][2] = ymax;

			for (int i = 0; i < buff.length; i++) {
				String[] coord = buff[i].split(";");
				int x = Integer.parseInt(coord[1]);
				int y = Integer.parseInt(coord[2]);
				int index = i;

				if (index != robots[0][0] && index != robots[1][0]) {
					palets[ipalet][0] = index;
					palets[ipalet][1] = x;
					palets[ipalet][2] = y;
					ipalet++;
				}

				// System.out.println(Integer.toString(index) + ":" +
				// Integer.toString(x) + " / " + Integer.toString(y));
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void MajCoords(String msg) {
		// System.out.println("MajCoords");

		boolean[] bpalets = new boolean[9];
		boolean[] brobots = new boolean[2];

		tabColisions = new boolean[11][11];

		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				tabColisions[i][j] = false;
			}
		}

		for (int i = 0; i < bpalets.length; i++) {
			bpalets[i] = false;
		}
		for (int i = 0; i < brobots.length; i++) {
			brobots[i] = false;
		}

		String[] buff = msg.split("\n");
		int debugIndexMax = 0;

		// System.out.println("nbre donnees reçues: " + buff.length);
		for (int i = 0; i < buff.length; i++) {

			String[] coord = buff[i].split(";");
			int x = Integer.parseInt(coord[1]);
			int y = Integer.parseInt(coord[2]);
			int index = i;

			double max = 500;
			int indexMax = 0;
			double current;
			boolean estRobot = false;
			for (int j = 0; j < palets.length; j++) {
				int newx = x - palets[j][1];
				int newy = y - palets[j][2];

				current = Math.sqrt(newx * newx + newy * newy);
				// System.out.println("currentP = " + current );

				if (current <= distColision) {
					System.out.println("COLLISIONNNNNNNN  PALET" + i + " " + j);
					tabColisions[j][i] = true;
				} else {
					// System.out.println("palets pas colision: "+current);
				}

				if (max > current && bpalets[j] == false) {
					max = current;
					indexMax = j;
				}
			}
			for (int j = 0; j < robots.length; j++) {
				int newx = x - robots[j][1];
				int newy = y - robots[j][2];

				current = Math.sqrt(newx * newx + newy * newy);
				// System.out.println("currentR = " + current );

				if (current <= distColision) {
					System.out.println("COLLISIONNNNNNNN  ROBOT" + i + " " + j + " current= " + current);

					tabColisions[palets.length + j][i] = true;
				} else {
					//System.out.println("robots pas colision ROBOT" + i + " " + j + " current= " + current);
				}

				if (max >= current && brobots[j] == false) {
					max = current;
					indexMax = j;
					estRobot = true;
				}
			}

			
			debugIndexMax = indexMax;
			if (estRobot) {
				// si cet index est sous surveillance
				debugIndexMax = indexMax + 9;
				System.out.println("1 estRobot: surveille = " + surveillance[indexMax + 9].toString());
				if (surveillance[indexMax + 9].estSurveille) {
					// si on est pas au bout de cette surveillance, on actualise
					// les distances
					System.out.println("2 estRobot: surveille = " + surveillance[indexMax + 9].toString());

					if (surveillance[indexMax + 9].mesure > 0) {
						System.out.println("3 estRobot: surveille = " + surveillance[indexMax + 9].toString());

						surveillance[indexMax + 9].mesure--;
						surveillance[indexMax + 9].distance += Math
								.floor(Math.sqrt((x - robots[indexMax][1]) * (x - robots[indexMax][1])
										+ (y - robots[indexMax][2]) * (y - robots[indexMax][2])));
						System.out.println("3.5 estRobot: surveille = " + surveillance[indexMax + 9].toString());

					} else {
						// mettre les objets qui étaient en collision dans le
						// bon tableau
						int indexCollision = surveillance[indexMax + 9].indexCollision;
						System.out.println("4 estRobot: surveille = " + surveillance[indexMax + 9].toString());

						// un palet est entré avec un robot qui été déjà en
						// collision
						/*
						if (indexCollision != surveillance[indexCollision][3]) {
							System.out.println("JE SUIS UN PALET (dans robot)");
							palets[indexMax][0] = index;
							palets[indexMax][1] = x;
							palets[indexMax][2] = y;

							bpalets[indexMax] = true;
							surveillance[indexMax + 9][0] = 0;

						} else */if (surveillance[indexMax + 9].distance > surveillance[indexCollision].distance) {
							System.out.println("ROBOT > PALET ");
							System.out.println("5 estRobot: surveille = " + surveillance[indexMax + 9].toString());

							robots[indexMax][0] = index;
							robots[indexMax][1] = x;
							robots[indexMax][2] = y;

							brobots[indexMax] = true;
							surveillance[indexMax + 9].estSurveille = false;
						} else if(surveillance[indexMax + 9].distance < surveillance[indexCollision].distance) {
							System.out.println("(r)ROBOT < PALET " + "ic=" + indexCollision + "im=" + indexMax);
							System.out.println("6 estRobot: surveille = " + surveillance[indexMax + 9].toString());

							palets[indexCollision][0] = index;
							palets[indexCollision][1] = x;
							palets[indexCollision][2] = y;

							bpalets[indexCollision] = true;
							surveillance[indexMax + 9].estSurveille = false;
						}else{
							System.out.println("7 estRobot: surveille = " + surveillance[indexMax + 9].toString());

							surveillance[indexMax + 9].mesure++;
						}
						
					}
				} else {
					robots[indexMax][0] = index;
					robots[indexMax][1] = x;
					robots[indexMax][2] = y;

					brobots[indexMax] = true;
				}

			} else {
				// si cet index est sous surveillance
				if (surveillance[indexMax].estSurveille) {
					// si on est pas au bout de cette surveillance, on actualise
					// les distances
					if (surveillance[indexMax].mesure > 0) {
						surveillance[indexMax].mesure--;
						surveillance[indexMax].distance += Math
								.floor(Math.sqrt((x - palets[indexMax][1]) * (x - palets[indexMax][1])
										+ (y - palets[indexMax][2]) * (y - palets[indexMax][2])));
					} else {
						// mettre les objets qui étaient en collision dans le
						// bon tableau
						int indexCollision = surveillance[indexMax].indexCollision;

						// un palet est entré avec un robot qui été déjà en
						// collision
						/*
						if (indexCollision != surveillance[indexCollision][3]) {
						//if (indexCollision != surveillance[indexCollision +9][3]) { // pas sur de l'indice...

							System.out.println("JE SUIS UN PALET (dans palet)");
							palets[indexMax][0] = index;
							palets[indexMax][1] = x;
							palets[indexMax][2] = y;

							bpalets[indexMax] = true;
							surveillance[indexMax][0] = 0;

						} else */
						System.out.println("ROBOT < PALET ??? ");

						
						if (surveillance[indexMax].distance > surveillance[indexCollision].distance) {
							System.out.println("(p)ROBOT < PALET " + "ic=" + indexCollision + "im=" + indexMax);
							robots[indexCollision -9][0] = index;
							robots[indexCollision -9][1] = x;
							robots[indexCollision -9][2] = y;

							brobots[indexCollision -9] = true;
							surveillance[indexCollision].estSurveille = false;

						} else if  (surveillance[indexMax].distance < surveillance[indexCollision].distance){
							System.out.println("ROBOT > PALET ");
							palets[indexMax][0] = index;
							palets[indexMax][1] = x;
							palets[indexMax][2] = y;

							bpalets[indexMax] = true;
							surveillance[indexMax].estSurveille = false;
						}else{
							surveillance[indexMax].mesure++;
						}

					}
				} else {
					palets[indexMax][0] = index;
					palets[indexMax][1] = x;
					palets[indexMax][2] = y;

					bpalets[indexMax] = true;
				}

			}

			// System.out.println();

		}
		System.out.println("8 estRobot: surveille = " + surveillance[debugIndexMax].toString());

		int robot1 = 9;
		int robot2 = 10;
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
		System.out.println("9 estRobot: surveille = " + surveillance[debugIndexMax].toString());

		// donner le même x/y au palet en collision avec un robot
		for (Integer pal : collisionsRobot1) {
			palets[pal][0] = robots[0][0];
			palets[pal][1] = robots[0][1];
			palets[pal][2] = robots[0][2];
		}

		for (Integer pal : collisionsRobot2) {
			palets[pal][0] = robots[1][0];
			palets[pal][1] = robots[1][1];
			palets[pal][2] = robots[1][2];
		}

		int nbMesures = 15;
		for (Integer pal : newcollisionsRobot1) {
			if (!collisionsRobot1.contains(pal)) {
				// on sort de collision!
				// surveiller les distances parcourues par les 2 objets
				// celui qui a parcouru le plus de distance en 5 (a ajuster)
				// mesures sera considéré comme le robot

				// le robot ne surveille qu'un seul palet, les palets qui
				// entrent en collision avec lui alors qu'il est déjà en
				// collision sont traités comme des palets.
				//if (surveillance[robot1][0] == 0) {
					surveillance[robot1].index = robot1;
					surveillance[robot1].estSurveille = true;
					surveillance[robot1].distance = 0;
					surveillance[robot1].mesure = nbMesures;
					surveillance[robot1].indexCollision = pal;
					surveillance[robot1].posX = robots[0][1];
					surveillance[robot1].posY = robots[0][2];
					
				//}
				surveillance[pal].index = pal;
				surveillance[pal].estSurveille = true;
				surveillance[pal].distance = 0;
				surveillance[pal].mesure = nbMesures;
				surveillance[pal].indexCollision = robot1;
				surveillance[pal].posX = palets[pal][1];
				surveillance[pal].posY = palets[pal][2];
				// retirer le palet de la liste des collisions avec le robot
				collisionsRobot1.remove(pal);
			}
		}
		System.out.println("10 estRobot: surveille = " + surveillance[debugIndexMax].toString());

		for (Integer pal : newcollisionsRobot2) {
			if (!collisionsRobot2.contains(pal)) {
				// on sort de collision!
				// surveiller les distances parcourues par les 2 objets
				// celui qui a parcouru le plus de distance en 5 (a ajuster)
				// mesures sera considéré comme le robot
				// le robot ne surveille qu'un seul palet, les palets qui
				// entrent en collision avec lui alors qu'il est déjà en
				// collision sont traités comme des palets.
				//if (surveillance[robot2][0] == 0) {
				System.out.println("newcollisionsRobot2 = " + newcollisionsRobot2.toString());
				System.out.println("collisionsRobot2 = " + collisionsRobot2.toString());

					surveillance[robot2].index = robot2;
					surveillance[robot2].estSurveille = true;
					surveillance[robot2].distance = 0;
					surveillance[robot2].mesure = nbMesures;
					surveillance[robot2].indexCollision = pal;
					surveillance[robot2].posX = robots[1][1];
					surveillance[robot2].posY = robots[1][2];
					
				//}
				surveillance[pal].index = pal;
				surveillance[pal].estSurveille = true;
				surveillance[pal].distance = 0;
				surveillance[pal].mesure = nbMesures;
				surveillance[pal].indexCollision = robot2;
				surveillance[pal].posX = palets[pal][1];
				surveillance[pal].posY = palets[pal][2];
				// retirer le palet de la liste des collisions avec le robot
				collisionsRobot2.remove(pal);
			}
		}
		System.out.println("11 estRobot: surveille = " + surveillance[debugIndexMax].toString());

	}



	public int[][] getPalets() {
		return palets;
	}

	public int[][] getRobots() {
		return robots;
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

	public String affichePalets() {
		String buff = "";
		for (int i = 0; i < palets.length; i++) {
			buff += palets[i][0] + ":" + palets[i][1] + " / " + palets[i][2] + "\n";
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
			buff += " pos: "+  surveillance[i].posX + " / " + surveillance[i].posY;
			buff += "\n";
		}
		return buff;
	}

	
	public String afficheRobots() {
		String buff = "";
		for (int i = 0; i < robots.length; i++) {
			buff += robots[i][0] + ":" + robots[i][1] + " / " + robots[i][2] + "\n";
		}
		return buff;

	}

	public static void main(String[] args) {

		Cam c = new Cam();
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
				dsocket.receive(packet);

				// Convert the contents to a string, and display them
				String msg = new String(buffer, 0, packet.getLength());
				System.out.println("------------debut " + iter + "--------------");
				// System.out.println(msg);

				MajCoords(msg);
				System.out.println(affichePalets());
				System.out.println(afficheRobots());
				System.out.println(afficheSurveillance());
				System.out.println(afficheColisions());
				System.out.println("---------------fin------------------");
				packet.setLength(buffer.length);
				// Thread.sleep(500);
				iter++;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
