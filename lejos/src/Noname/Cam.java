package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import lejos.hardware.Button;

public class Cam implements Runnable {

	private int[][] palets;
	private int[][] robots;
	private boolean[][] tabColisions;
	private Set<Integer> collisionsRobot1;
	private Set<Integer> collisionsRobot2;
	int[][] surveillance;

	private int colision;
	private int distColision;

	private DatagramSocket dsocket;
	private DatagramPacket packet;
	private byte[] buffer;

	public Cam() {
		palets = new int[9][3];
		robots = new int[2][3];
		colision = 0;
		distColision = 15;
		collisionsRobot1 = new HashSet<>();
		collisionsRobot2 = new HashSet<>();
		surveillance = new int[11][4];
		for(int i=0;i<surveillance.length;i++){
			surveillance[i][0] = 0; // 1 si surveillé; 0 si non surveillé
			surveillance[i][1] = 0; // distance parcourue pendant la surveillance
			surveillance[i][2] = 0; // nombre de mesures de distance enregistrées.
			surveillance[i][3] = 0; // index de l'objet avec lequel il est en collision.
			
		}

		init();
	}

	private String changeMsg(String msg) {
		// String msg =
		// String msg =
		// "0;50;100\n1;50;150\n2;50;200\n3;100;50\n4;100;100\n5;100;150\n6;100;200\n7;100;250\n8;150;100\n9;150;150\n10;150;200";
		String[] buff = msg.split("\n");
		String Nextmsg = "";

		int amplitude = 10;
		Random r = new Random();

		for (int i = 0; i < buff.length; i++) {
			String[] coord = buff[i].split(";");
			int x = Integer.parseInt(coord[1]);
			int y = Integer.parseInt(coord[2]);
			int index = Integer.parseInt(coord[0]);
			if (index == 3) {
				x += r.nextInt(2 * amplitude) - amplitude;
				y += 10;
			}

			Nextmsg += index + ";" + x + ";" + y + "\n";
		}

		return Nextmsg;
	}

	private void init() {
		// index;x;y robot1
		// String msg =
		// "0;50;100\n1;50;150\n2;50;200\n3;100;50\n4;100;100\n5;100;150\n6;100;200\n7;100;250\n8;150;100\n9;150;150\n10;150;200";
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
			int irobot = 0;
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
				int index = Integer.parseInt(coord[0]);

				if (y < ymin) {
					ymin = y;
					xmin = x;
					indexMin = index;
				}

				if (y > ymax) {
					ymax = y;
					xmax = x;
					indexMax = index;
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
				int index = Integer.parseInt(coord[0]);

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

		System.out.println(affichePalets());
		System.out.println(afficheRobots());

	}

	public void MajCoords(String msg) {
		// System.out.println("MajCoords");

		boolean[] bpalets = new boolean[9];
		boolean[] brobots = new boolean[2];

		tabColisions = new boolean[11][11];

		colision = 0;
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

		int irobot = 0;
		int ipalet = 0;
		// System.out.println("nbre donnees reçues: " + buff.length);
		for (int i = 0; i < buff.length; i++) {

			String[] coord = buff[i].split(";");
			int x = Integer.parseInt(coord[1]);
			int y = Integer.parseInt(coord[2]);
			int index = Integer.parseInt(coord[0]);

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
					// System.out.println("palet colision[" + j + "][" + i + "]
					// : current = " + current);
					// System.out.println("newx=" + newx + " newy=" + newy);
					// System.out.println("i: " + index + " recu: " + x + "/" +
					// y + " - palets: i:" + palets[j][0] + " "
					// + palets[j][1] + "/" + palets[j][2]);
					// System.out.println(affichePalets());

					colision++;
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
					// System.out.println("robot colision[" + j + "][" + i + "]
					// : current = " + current);
					// System.out.println(x + " - " + robots[j][1] + " - " + y +
					// " - " + robots[j][2]);
					// System.out.println("newx=" + newx + " newy=" + newy);
					// System.out.println("i: " + index + " recu: " + x + "/" +
					// y + " - robots: i:" + robots[j][0] + " "
					// + robots[j][1] + "/" + robots[j][2]);
					// System.out.println(afficheRobots());
					colision++;
					tabColisions[palets.length + j][i] = true;
				} else {
					// System.out.println("robots pas colision: "+current);
				}

				if (max >= current && brobots[j] == false) {
					max = current;
					indexMax = j;
					estRobot = true;
				}
			}

			if (estRobot) {
				// System.out.println("Actualise Robot" + "" );
				if(surveillance[indexMax + 9][0] == 1){ // si cet index est sous surveillance
					if(surveillance[indexMax + 9][2] > 0){ // si on est pas au bout de cette surveillance, on actualise les distances
						surveillance[indexMax + 9][1] += Math.sqrt((x - robots[indexMax][1]) * (x - robots[indexMax][1]) + (y - robots[indexMax][2]) * (y - robots[indexMax][2]));
					}else{
						// mettre les objets qui étaient en collision dans le bon tableau
						int indexCollision = surveillance[indexMax + 9][3];
						if(surveillance[indexMax + 9][1] > surveillance[indexCollision][1]){
							robots[indexMax][0] = index;
							robots[indexMax][1] = x;
							robots[indexMax][2] = y;

							brobots[indexMax] = true;
						}else{
							palets[indexMax][0] = index;
							palets[indexMax][1] = x;
							palets[indexMax][2] = y;

							bpalets[indexMax] = true;
						}
						
						// arreter la surveillance?
						
					}
				}


			} else {
				// System.out.println("Actualise Palet");

				palets[indexMax][0] = index;
				palets[indexMax][1] = x;
				palets[indexMax][2] = y;

				bpalets[indexMax] = true;

			}

			// System.out.println();

		}

		int robot1 = 9;
		int robot2 = 10;
		Set<Integer> newcollisionsRobot1 = new HashSet<>();
		Set<Integer> newcollisionsRobot2 = new HashSet<>();
		for (int i = 0; i < tabColisions.length; i++) {
			if (tabColisions[robot1][i]) {
				for (int j = 0; j < tabColisions.length; j++) {
					if (tabColisions[j][i]) {
						// colision robot1 et j
						newcollisionsRobot1.add(j);
					}
				}
			}

			if (tabColisions[robot2][i]) {
				for (int j = 0; j < tabColisions.length; j++) {
					if (tabColisions[j][i]) {
						// colision robot2 et j
						newcollisionsRobot2.add(j);
					}
				}
			}

		}
		
		// donner le même x/y au palet en collision avec un robot
		for(Integer pal : collisionsRobot1){
			palets[pal][0] = robots[0][0];
			palets[pal][1] = robots[0][1];
			palets[pal][2] = robots[0][2];
		}
		
		for(Integer pal : collisionsRobot2){
			palets[pal][0] = robots[1][0];
			palets[pal][1] = robots[1][1];
			palets[pal][2] = robots[1][2];
		}
		
		
		for(Integer pal : newcollisionsRobot1){
			if(!collisionsRobot1.contains(pal)){
				// on sort de collision!
				// surveiller les distances parcourues par les 2 objets				
				// celui qui a parcouru le plus de distance en 5 (a ajuster) mesures sera considéré comme le robot

				surveillance[robot1][0] = 1;
				surveillance[robot1][1] = 0;
				surveillance[robot1][2] = 5;
				surveillance[robot1][2] = pal;
				
				
				surveillance[pal][0] = 1;
				surveillance[pal][1] = 0;
				surveillance[pal][2] = 5;
				surveillance[pal][2] = robot1;
				
				// retirer le palet de la liste des collisions avec le robot
				collisionsRobot1.remove(pal);
			}
		}
		
		
		for(Integer pal : newcollisionsRobot2){
			if(!collisionsRobot2.contains(pal)){
				// on sort de collision!
				// surveiller les distances parcourues par les 2 objets				
				// celui qui a parcouru le plus de distance en 5 (a ajuster) mesures sera considéré comme le robot

				surveillance[robot2][0] = 1;
				surveillance[robot2][1] = 0;
				surveillance[robot2][2] = 5;
				
				surveillance[pal][0] = 1;
				surveillance[pal][1] = 0;
				surveillance[pal][2] = 5;
				
				// retirer le palet de la liste des collisions avec le robot
				collisionsRobot2.remove(pal);
			}
		}
		

	}

	private void remplirTableauCollision() {

	}

	private void pressAnyKeyToContinue() {
		// System.out.println("Press any key to continue...");
		try {
			System.in.read();
		} catch (Exception e) {
		}
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

	public String afficheRobots() {
		String buff = "";
		for (int i = 0; i < robots.length; i++) {
			buff += robots[i][0] + ":" + robots[i][1] + " / " + robots[i][2] + "\n";
		}
		return buff;

	}

	public static void main(String[] args) {

		int ok = 0;
		int ko = 0;
		Cam c = new Cam();
		Thread t = new Thread(c);
		t.run();

	}

	@Override
	public void run() {

		int port = 8888;
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
				System.out.println("------------debut--------------");
				System.out.println(msg);
				// System.out.println(packet.getAddress().getHostName() + ": "
				// + msg);
				MajCoords(msg);
				System.out.println(affichePalets());
				System.out.println(afficheRobots());
				System.out.println("---------------fin------------------");
				packet.setLength(buffer.length);
				// Thread.sleep(500);

			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
