package Noname;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import lejos.hardware.Button;

public class Camera {
	public static void main(String args[]) {

		try {
			int port = 8888;

			// Create a socket to listen on the port.
			DatagramSocket dsocket = new DatagramSocket(port);

			// Create a buffer to read datagrams into. If a
			// packet is larger than this buffer, the
			// excess will simply be discarded!
			byte[] buffer = new byte[2048];

			// Create a packet to receive data into the buffer
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			boolean rester = true;
			int nbIteration = 100;
			// Now loop forever, waiting to receive packets and printing them.
			while (rester) {
				// Wait to receive a datagram
				dsocket.receive(packet);

				// Convert the contents to a string, and display them
				//System.out.println("avant");
				String msg = new String(buffer, 0, packet.getLength());
				//System.out.println("apres");
				// System.out.println(packet.getAddress().getHostName() + ": "
				// + msg);

				String[] palets = msg.split("\n");

				for (int i = 0; i < palets.length; i++) {
					String[] coord = palets[i].split(";");
					int x = Integer.parseInt(coord[1]);
					int y = Integer.parseInt(coord[2]);
					int index = Integer.parseInt(coord[0]);

					System.out.println(Integer.toString(index) + ":" + Integer.toString(x) + " / " + Integer.toString(y));
				Thread.sleep(1000);
				}

				// Reset the length of the packet before reusing it.
				packet.setLength(buffer.length);
				
				nbIteration--;
				if(nbIteration <= 0 ){
					rester = false;
				}
			}
			
			while(true){
				
			}

		} catch (Exception e) {
			System.out.println(e);
			while(true){
				
			}
		}
	}
}
