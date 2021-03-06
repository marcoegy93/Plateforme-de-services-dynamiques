package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import bri.Service;
import utilisateurs.User;
import utilisateurs.UsersData;


public class ServiceChat implements Service{
    private static UsersData utilisateurs;
	private User connected;
    private static Map<String,List<String>>  messages;
    private Socket client;

    static {
        messages = new HashMap<String,List<String>>();
    }

    public ServiceChat(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            auth(in, out);
			out.println("Vous êtes connecté ! ##Voulez-vous envoyer un message ou lire vos messages  reçus ? E pour envoyer, L pour lire, Q pour quitter");
			while(true) {
				
	            String reponse = in.readLine();
	            switch(reponse) {
	            	case "E":
	            		out.println("SERVICE CHAT##Rentrez donc le pseudo du destinataire ?");
	            		String pseudo = in.readLine();
	            		out.println("Quel message voulez-vous envoyer ?");
	            		String message = in.readLine();
	            		synchronized(messages) {
	            			envoieMessage(pseudo, message);
	            			out.println("Message envoyé !##Voulez-vous envoyer un message ou lire vos messages  reçus ? E pour envoyer, L pour lire, Q pour quitter");
	            		}
	            		
	            		break;
	            		//messages.put(pseudo, null);
	            		//messages.add(new Message(message,pseudo));
	            	case "L":
	            		synchronized(messages) {
	            			String s = lectureMessages(connected);
	            			out.println(s+ "Voulez-vous envoyer un message ou lire vos messages  reçus ? E pour envoyer, L pour lire, Q pour quitter" );
	            		}
		            	
		            	break;
	            	case "Q":
	            		this.client.close();
	            		break;
	            	default:
	            		out.println("Veuillez ressaisir le bon caractère ");
	            		
	            }
			}
        } catch (IOException e) {}
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

	private String lectureMessages(User utilisateurConnect) {
		synchronized(messages) {
			String s = "Voici la liste de vos messages reçus :##";
			for (Map.Entry mapentry : messages.entrySet()){
				if(mapentry.getKey().toString().equals(utilisateurConnect.getLogin())) {
					for(int i=0;i<messages.get(utilisateurConnect.getLogin()).size();i++) {
						s+= "message " + (i+1) + " : " + messages.get(utilisateurConnect.getLogin()).get(i) + "##";
					}
				}
			}
			return s;
		}
	}

	private void envoieMessage(String pseudo, String message) {
		synchronized(messages) {
			if(!messages.containsKey(pseudo)) {
				messages.put(pseudo, new ArrayList<String>(Arrays.asList(message)));
				//System.out.println(messages.get(pseudo));
			}else {
				messages.get(pseudo).add(message);
			}
		}
	}

    protected void finalize() throws Throwable {
        client.close();
    }
}

