package Noname;

import java.util.Random;

public class Cam {

	private int[][] palets;
	private int[][] robots;

	public Cam() {
		palets = new int[9][3];
		robots = new int[2][3];

		init();
	}

	private String changeMsg(String msg) {
		// String msg =
		// "0;100;100\n1;200;100\n2;300;100\n3;0;200\n4;200;100\n5;200;200\n6;200;300\n7;400;200\n8;300;100\n9;300;200\n10;300;300";
		String[] buff = msg.split("\n");
		String Nextmsg = "";

		int amplitude = 10;
		Random r = new Random();

		for (int i = 0; i < buff.length; i++) {
			String[] coord = buff[i].split(";");
			int x = Integer.parseInt(coord[1]);
			int y = Integer.parseInt(coord[2]);
			int index = Integer.parseInt(coord[0]);
			if (index == 3 || index == 7) {
				x += r.nextInt(2 * amplitude) - amplitude;
				y += r.nextInt(2 * amplitude) - amplitude;
			}

			Nextmsg += index + ";" + x + ";" + y + "\n";
		}

		return Nextmsg;
	}

	private void init() {
		// index;x;y robot1
		String msg = "0;100;100\n1;200;100\n2;300;100\n3;0;200\n4;200;100\n5;200;200\n6;200;300\n7;400;200\n8;300;100\n9;300;200\n10;300;300";
		String[] buff = msg.split("\n");

		int irobot = 0;
		int ipalet = 0;

		for (int i = 0; i < buff.length; i++) {
			String[] coord = buff[i].split(";");
			int x = Integer.parseInt(coord[1]);
			int y = Integer.parseInt(coord[2]);
			int index = Integer.parseInt(coord[0]);

			if (x == 0 || x == 400) {
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

		for (int i = 0; i < bpalets.length; i++) {
			bpalets[i] = false;
		}
		for (int i = 0; i < brobots.length; i++) {
			brobots[i] = false;
		}

		String[] buff = msg.split("\n");

		int irobot = 0;
		int ipalet = 0;

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
				if (max >= current && brobots[j] == false) {
					max = current;
					indexMax = j;
					estRobot = true;
				}
			}

			if (estRobot) {
				robots[indexMax][0] = index;
				robots[indexMax][1] = x;
				robots[indexMax][2] = y;
				brobots[indexMax] = true;

			} else {
				palets[indexMax][0] = index;
				palets[indexMax][1] = x;
				palets[indexMax][2] = y;
				bpalets[indexMax] = true;

			}

			// System.out.println();

		}

	}

	public int[][] getPalets() {
		return palets;
	}

	public int[][] getRobots() {
		return robots;
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

			String msg = "0;100;100\n1;200;100\n2;300;100\n3;0;200\n4;200;100\n5;200;200\n6;200;300\n7;400;200\n8;300;100\n9;300;200\n10;300;300";
			// System.out.println(c.affichePalets());
			// System.out.println(c.afficheRobots());

			for (int i = 0; i < 1000; i++) {
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

}
