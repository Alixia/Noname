package Noname;

import java.util.ArrayList;

public class Surveillance {

	public int index1;
	public int index2;
	public int mesure;
	public int pos1X;
	public int pos1Y;	
	public int pos2X;
	public int pos2Y;
	
	
	public Surveillance() {
		index1 = -1;
		index2 = -1;
		mesure = -1;
		pos1X = -1;
		pos1Y = -1;
		pos2X = -1;
		pos2Y = -1;
		
	}
	

	@Override
	public String toString() {
		String buff = "";
		buff += " mesures: " + mesure;
		buff += " collision " + index1 + " avec " + index2;
		buff += " pos1: "+  pos1X + " / " + pos1Y;
		buff += " pos2: "+  pos2X + " / " + pos2Y;
		buff += "\n";
		return buff;
	}
	
}
