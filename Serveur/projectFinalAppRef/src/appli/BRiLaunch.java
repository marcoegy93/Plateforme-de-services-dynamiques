package appli;

import bri.*;
import services.ServiceAma;
import services.ServiceProg;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMA= 4000;
	
	public static void main(String[] args) throws Exception {
		System.out.println("**** Lancement du Serveur BRi ****");
		System.out.println("Lancement du serveur Programmeurs sur le port " + PORT_PROG);
		System.out.println("Lancement du serveur Amateur sur le port " + PORT_AMA);

		new ServeurBRi(PORT_PROG, ServiceProg.class).lancer();
		new ServeurBRi(PORT_AMA, ServiceAma.class).lancer();
	}
}
