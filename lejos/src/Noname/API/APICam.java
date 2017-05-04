package Noname.API;

public interface APICam {

	/**
	 * mais à jour les tableaux palets et robot à partir des informations contenu dans msg
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
	 * affiche le tableau pour voir les éléments en collisions
	 * @return
	 */
	public String afficheCollisions();
	
	/**
	 * affiche le tableau avec les positions des palets et des robots
	 * @return
	 */
	public String afficheElements();
	
	/**
	 * affiche le tableau permettant de gèrer la surveillance (lorsque deux éléments ne sont plus en collision)
	 * ce tableau permet de savoir si un élément est un robot ou un palet
	 * @return
	 */
	public String afficheSurveillance();
	
	
	
}
