package Noname.API;

public interface APICam {

	/**
	 * mais � jour les tableaux palets et robot � partir des informations contenu dans msg
	 * @param msg
	 */
	public void MAJCoords(String msg);
	
	/**
	 * renvoi un tableau avec la position des palets
	 * @return
	 */
	public int[][] getPalets();
	
	/**
	 * renvoi un tableau avec la position des robots
	 * @return
	 */
	public int[][] getRobots();
	
	/**
	 * affiche le tableau pour voir les �l�ments en collisions
	 * @return
	 */
	public String afficheCollisions();
	
	/**
	 * affiche le tableau avec les positions des palets et des robots
	 * @return
	 */
	public String afficheElements();
	
	/**
	 * affiche le tableau permettant de g�rer la surveillance (lorsque deux �l�ments ne sont plus en collision)
	 * ce tableau permet de savoir si un �l�ment est un robot ou un palet
	 * @return
	 */
	public String afficheSurveillance();
	
	
	
}
