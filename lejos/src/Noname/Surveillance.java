package Noname;

public class Surveillance {

	public double distance;
	public int index;
	public int indexCollision;
	public int mesure;
	public boolean estSurveille;
	public int posX;
	public int posY;
	
	public Surveillance() {
		distance = 0;
		index = -1;
		estSurveille = false;
		indexCollision = -1;
		mesure = 0;
		posX = -1;
		posY = -1;
		
	}
	

	@Override
	public String toString() {
		String buff = "";
		buff += " index:" + index;
		buff += " surv? " + estSurveille;
		buff += " dist: " + distance;
		buff += " mesures: " + mesure;
		buff += " collision avec " + indexCollision;
		buff += " pos: "+  posX + " / " + posY;
		buff += "\n";
		return buff;
	}
	
}
