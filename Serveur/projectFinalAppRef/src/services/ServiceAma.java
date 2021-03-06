package services;


import bri.ClassDefoundedException;
import bri.Service;
import bri.ServiceRegistry;
import bri.ValidationException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.*;


public class ServiceAma implements Runnable {
	
	private Socket client;
	
	public ServiceAma(Socket socket) {
		client = socket;
	}

	public void run() {
		try {BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			int choix;
			String msg = null;
			do {
				if(msg!=null) {
					out.println(msg+"##"+ServiceRegistry.toStringue()+"##Tapez le numéro du service :");
				}else {
					out.println(ServiceRegistry.toStringue()+"##Tapez le numéro du service :");
				}
				
			    choix = Integer.parseInt(in.readLine());
			    if(!ServiceRegistry.getServiceClass(choix).isDemarree()) {
			    	msg="classe non disponible( non démarrée par le programmeur)";
			    }else {
					Class<? extends Service> classe = ServiceRegistry.getServiceClass(choix).getMaclasse();
				    Constructor<? extends Service> cons = classe.getConstructor(Socket.class);
				    new Thread(cons.newInstance(this.client)).start();
			    }
			}while(!ServiceRegistry.getServiceClass(choix).isDemarree());

			}
		catch (Exception e) {
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
