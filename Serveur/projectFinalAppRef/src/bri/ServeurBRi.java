package bri;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;


public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;
	private Class<? extends Runnable> service;
	
	public ServeurBRi(int port, Class<? extends Runnable> cl) {
		try {
			listen_socket = new ServerSocket(port);
			service = cl;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public void run() {
		try {
			while(true) {
				 new Thread(service.getConstructor(Socket.class).newInstance(listen_socket.accept())).start();
			}	
		}
		catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			try {this.listen_socket.close();} catch (IOException e1) {}
			System.err.println("Problème sur le port d'écoute :"+e);
		}
	}

	 // restituer les ressources --> finalize
	protected void finalize() throws Throwable {
		try {this.listen_socket.close();} catch (IOException e1) {}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();
	}
}
