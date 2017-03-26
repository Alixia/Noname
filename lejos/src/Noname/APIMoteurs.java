package Noname;

public interface APIMoteurs {

	public void setVitesse(float v);
	public void setVitesseG(float v);
	public void setVitesseD(float v);
	
	public void reculer();
	public void reculerG();
	public void reculerD();
	public void avancer();
	public void avancerG();
	public void avancerD();
	public void tournerDroite();
	public void tournerGauche();

	public void fermer();
	public void ouvrir();	
	public void actionFermer();
	public void actionOuvrir();
	
}
