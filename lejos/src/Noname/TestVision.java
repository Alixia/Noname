package Noname;

public class TestVision {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Capteurs capteurs = new Capteurs();
		Screen screen = new Screen();
		InputHandler input = new InputHandler(screen);

		while(!capteurs.boutonEstPresse()){
			System.out.println(capteurs.distanceVision());
			input.waitAny();
		}
		// the end

	}
}
