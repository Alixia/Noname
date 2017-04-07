package Noname;

public class TestBoutonPoussoir {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Capteurs capteurs = new Capteurs();
		Screen screen = new Screen();
		InputHandler input = new InputHandler(screen);

		while(true){
			System.out.println("appuyez sur le bouton poussoir");
			while(!capteurs.boutonEstPresse());
			System.out.println("ouiiiiiiiiii!");
		}
		// the end

	}
}
