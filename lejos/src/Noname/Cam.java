package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

public class Cam implements Runnable {

	private int[][] palets;
	private int[][] robots;
	private boolean[][] tabColisions;

	private int colision;
	private int distColision;

	public Cam() {
		palets = new int[9][3];
		robots = new int[2][3];
		colision = 0;
		distColision = 15;

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
		String msg = "0;50;100\n1;50;150\n2;50;200\n3;100;50\n4;100;100\n5;100;150\n6;100;200\n7;100;250\n8;150;100\n9;150;150\n10;150;200";
		String[] buff = msg.split("\n");

		int irobot = 0;
		int ipalet = 0;

		for (int i = 0; i < buff.length; i++) {
			String[] coord = buff[i].split(";");
			int x = Integer.parseInt(coord[1]);
			int y = Integer.parseInt(coord[2]);
			int index = Integer.parseInt(coord[0]);

			if (y == 50 || y == 250) {
				robots[irobot][0] = index;
				robots[irobot][1] = x;
				robots[irobot][2] = y;
				irobot++;
			} else {
				palets[ipalet][0] = index;
				palets[ipalet][1] = x;
				palets[ipalet][2] = y;
				ipalet++;
			}

			// System.out.println(Integer.toString(index) + ":" +
			// Integer.toString(x) + " / " + Integer.toString(y));
		}
	}

	public void MajCoords(String msg) {
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
		System.out.println("nbre donnees reçues: " + buff.length);
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
					System.out.println("palet colision[" + j + "][" + i + "] : current = " + current);
					System.out.println("newx=" + newx + " newy=" + newy);
					System.out.println("i: " + index + " recu: " + x + "/" + y + " - palets: i:" + palets[j][0] + " "
							+ palets[j][1] + "/" + palets[j][2]);
					System.out.println(affichePalets());

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
					System.out.println("robot colision[" + j + "][" + i + "] : current = " + current);
					System.out.println(x + " - " + robots[j][1] + " - " + y + " - " + robots[j][2]);
					System.out.println("newx=" + newx + " newy=" + newy);
					System.out.println("i: " + index + " recu: " + x + "/" + y + " - robots: i:" + robots[j][0] + " "
							+ robots[j][1] + "/" + robots[j][2]);
					System.out.println(afficheRobots());
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
				synchronized (robots) {
					robots[indexMax][0] = index;
					robots[indexMax][1] = x;
					robots[indexMax][2] = y;
				}
				brobots[indexMax] = true;

			} else {
				synchronized (palets) {
					palets[indexMax][0] = index;
					palets[indexMax][1] = x;
					palets[indexMax][2] = y;
				}
				bpalets[indexMax] = true;

			}

			// System.out.println();

		}

		if (colision > 11) {
			System.out.println("COLISION! " + colision);
			System.out.println("palets:\n" + affichePalets());
			System.out.println("robots:\n" + afficheRobots());
			System.out.println("robots:\n" + afficheColisions());

			pressAnyKeyToContinue();
		}

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
		for (int j = 0; j < 1000; j++) {
			Cam c = new Cam();

			String msg = "0;50;100\n1;50;150\n2;50;200\n3;100;50\n4;100;100\n5;100;150\n6;100;200\n7;100;250\n8;150;100\n9;150;150\n10;150;200";
			// System.out.println(c.affichePalets());
			// System.out.println(c.afficheRobots());

			for (int i = 0; i < 10; i++) {
				msg = c.changeMsg(msg);

				c.MajCoords(msg);

			}

			if (c.getRobots()[0][0] == 3 && c.getRobots()[1][0] == 7) {
				ok++;
			} else {
				ko++;
			}
			// System.out.println(msg);
			// System.out.println(c.affichePalets());
			System.out.println(c.afficheRobots());

		}

		System.out.println("ok=" + ok + " ko= " + ko);
	}

	@Override
	public void run() {
		Cam c = new Cam();
		int port = 8888;
		try {
			// Create a socket to listen on the port.
			DatagramSocket dsocket = new DatagramSocket(port);

			// Create a buffer to read datagrams into. If a
			// packet is larger than this buffer, the
			// excess will simply be discarded!
			byte[] buffer = new byte[2048];

			// Create a packet to receive data into the buffer
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			while (true) {
				dsocket.receive(packet);

				// Convert the contents to a string, and display them
				// System.out.println("avant");
				String msg = new String(buffer, 0, packet.getLength());
				// System.out.println("apres");
				// System.out.println(packet.getAddress().getHostName() + ": "
				// + msg);
				c.MajCoords(msg);
				Thread.sleep(1000);

			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
