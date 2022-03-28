package bri;

public class ClasseDemarree {
	private Class<? extends Service> maclasse;
	private boolean demarree;
	
	public ClasseDemarree(Class<? extends Service> maclasse, boolean demarree) {
		this.maclasse = maclasse;
		this.demarree = demarree;
	}
	
	public Class<? extends Service> getMaclasse() {
		return maclasse;
	}


	public boolean isDemarree() {
		return demarree;
	}


	public void setDemarree(boolean demarree) {
		this.demarree = demarree;
	}
	
	
	
}
