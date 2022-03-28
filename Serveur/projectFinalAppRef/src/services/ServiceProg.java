package services;


import bri.ClassDefoundedException;
import bri.Service;
import bri.ServiceRegistry;
import utilisateurs.User;
import bri.ValidationException;
import utilisateurs.UsersData;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URLClassLoader;


public class ServiceProg implements Runnable {

	private Socket client;
	private static UsersData utilisateurs;
	private User connected;

	static {
		utilisateurs = new UsersData();
	}

	public ServiceProg(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			auth(in, out);
			String service=null;
			String msg=null;
			do {
				if(msg!=null) {
					out.println(msg+"##Choisissez une action :##" +
							"- Nouveau service : 1##" +
							"- MAJ un service : 2##" +
							"- MAJ votre URL de ftp : 3##" +
							"- Arrêter un service : 4##" +
							"- Démarrer un service : 5##" +
							"- Désinstaller un service : 6##" +
							"- Se déconnecter : 7");
				}else {
					out.println("Choisissez une action :##" +
							"- Nouveau service : 1##" +
							"- MAJ un service : 2##" +
							"- MAJ votre URL de ftp : 3##" +
							"- Arrêter un service : 4##" +
							"- Démarrer un service : 5##" +
							"- Désinstaller un service : 6##" +
							"- Se déconnecter : 7");
				}
				service = in.readLine();
				switch(service) {
					case "1":
						chargerService(in,out);
						msg = "La classe a été chargée";
						break;
					case "2":
						majService(in,out);
						msg = "La classe a été mise à jour";
						break;
					case "3":
						out.println("Entrez votre nouvel URL de ftp (format ftp://@IP:PORT)");
						connected.setURL(in.readLine());
						msg = "Votre URL ftp a été modifiée";
						break;
					case "4":
						arreterService(out,in);
						msg = "La classe a été arrêtée";
						break;
					case "5":
						demarrerService(out,in);
						msg = "La classe a été démarrée";
						break;
					case "6":
						desinstallerService(in,out);
						msg = "La classe a été désinstallée";
						break;
					case "7":
						client.close();
						break;
					default:
						break;
				}
			}while(!service.equals("7"));
		} catch(IOException e){
			//Fin du service
		}
		//try {client.close();} catch (IOException e2) {}
	}

	private void auth(BufferedReader in, PrintWriter out) throws IOException {
		boolean connexion = false;
		String msg=null;
		while (!connexion) {
			out.println("Bonjour ! Avez-vous un compte ? (o/n)");
			switch (in.readLine()) {
				case "o":
					while (!connexion) {
						if(msg!=null) {
							out.println(msg+"##Connectez-vous comme-ci : 'LOGIN-MDP'");
						}else {
							out.println("Connectez-vous comme-ci : 'LOGIN-MDP'");
						}
						synchronized (utilisateurs) {
							try {
								connected = utilisateurs.connexion(in.readLine());
								connexion = true;
							} catch (Exception e) {
								msg="Login / MDP incorrect";
							}
						}
					}
					break;
				case "n":
					while (!connexion) {
						if(msg!=null) {
							out.println(msg+"##Inscrivez-vous comme-ci : 'LOGIN-MDP-URLFTP'" +
									"##Votre URLFTP doit être au format 'ftp://@ip:port'" +
									"##Assurez-vous que votre ftp soit accessible et que vous disposez d'un package au même nom que votre login");
						}else {
							out.println("Inscrivez-vous comme-ci : 'LOGIN-MDP-URLFTP'" +
									"##Votre URLFTP doit être au format 'ftp://@ip:port'" +
									"##Assurez-vous que votre ftp soit accessible et que vous disposez d'un package au même nom que votre login");
						}
						
						try {
							synchronized (utilisateurs) {
								String tmp = in.readLine();
								if (utilisateurs.userExists(tmp)) {
									msg="Ce login existe déjà !";
								} else {
									connected = utilisateurs.inscription(tmp);
									connexion = true;
								}
							}
						} catch (MalformedURLException e) {
							msg="URL incorrecte !";
						}
					}
					break;
			}
		}
	}

	private void chargerService(BufferedReader in, PrintWriter out) throws IOException {
		while(true) {
			out.println("Entre le nom de la classe à charger");
			String classeName = connected.getLogin()+"."+in.readLine();
			URLClassLoader urlcl = connected.getURLClassLoader();
			try {
				Class<? extends Service> c = urlcl.loadClass(classeName).asSubclass(Service.class);
				ServiceRegistry.addService(c);
				break;
			}catch(ClassDefoundedException e) {
				out.println(e.getMessage());
			} catch (ClassCastException e) {
				out.println("Votre classe "+classeName+" n'implémente pas l'interface de service BRi");
			} catch (ClassNotFoundException e) {
				out.println("Votre classe "+classeName+" n'a pas été trouvée");
			} catch (ValidationException e) {
				out.println(e.getMessage());
			}
		}
	}
	
	private void demarrerService(PrintWriter out,BufferedReader in) throws NumberFormatException, IOException {
		out.println(ServiceRegistry.toStringue()+"####Tapez le numéro du service à démarrer");
		int numService = Integer.parseInt(in.readLine());
		if(!ServiceRegistry.getServiceClass(numService).isDemarree()) {
			ServiceRegistry.getServiceClass(numService).setDemarree(true);
		}
		
	}

	private void arreterService(PrintWriter out,BufferedReader in) throws NumberFormatException, IOException {
		out.println(ServiceRegistry.toStringue()+"####Tapez le numéro du service à arrêter");
		int numService = Integer.parseInt(in.readLine());
		if(ServiceRegistry.getServiceClass(numService).isDemarree()) {
			ServiceRegistry.getServiceClass(numService).setDemarree(false);
		}
		
	}
	
	private void desinstallerService(BufferedReader in, PrintWriter out) throws IOException {
		out.println(ServiceRegistry.toStringue()+"##Tapez le num�ro de service à désinstaller :");
		int choix = Integer.parseInt(in.readLine());
		String cname = ServiceRegistry.getServiceClass(choix).getMaclasse().getName();
		connected.reloadURLClassLoader();
		ServiceRegistry.supprimerService(choix);
	}


	private void majService(BufferedReader in, PrintWriter out) throws IOException {
		out.println(ServiceRegistry.toStringue()+"##Tapez le num�ro de service à mettre à jour :");
		int choix = Integer.parseInt(in.readLine());
		try {
			String cname = ServiceRegistry.getServiceClass(choix).getMaclasse().getName();
			connected.reloadURLClassLoader();
			ServiceRegistry.majService(choix, connected.getURLClassLoader().loadClass(cname).asSubclass(Service.class));
		} catch (ClassNotFoundException e) {
			out.println("ERREUR : Classe non trouvée");
		} catch (ValidationException e) {
			out.println("ERREUR : Classe non conforme aux normes BRi");
		}
	}



	protected void finalize() throws Throwable {
		 client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
