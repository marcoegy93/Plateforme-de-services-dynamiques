package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partag�e en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = Collections.synchronizedList(new Vector<ClasseDemarree>());
	}
	private static List<ClasseDemarree> servicesClasses;

	
	public static void addService(Class<? extends Service> classe) throws ValidationException, ClassDefoundedException {
		validation(classe);
		ClasseDemarree c = new ClasseDemarree(classe,true);
		for(int i=0;i<servicesClasses.size();i++) {
			if(servicesClasses.get(i).getMaclasse().getName()==classe.getName()) {
				throw new ClassDefoundedException("Classe déjà chargée auparavant");
			}	
		}
		servicesClasses.add(c);
	}

	private static void validation(Class<? extends Service> classe) throws ValidationException {
		boolean extendsService = false;
		Constructor<? extends Service> c = null;
		try { 
			c = classe.getConstructor(java.net.Socket.class); 
		} catch (NoSuchMethodException e) {
			throw new ValidationException("Il faut un constructeur avec Socket");
		}
		int modifiers = c.getModifiers();
		int modifier = classe.getModifiers();
		Field[] fields = classe.getDeclaredFields();
		if (!Modifier.isPublic(modifiers)) 
			throw new ValidationException("Le constructeur (Socket) doit être public");
		if (Modifier.isAbstract(modifier)) 
			throw new ValidationException("La classe ne peut pas être abstraite");
		if (!Modifier.isPublic(modifier)) 
			throw new ValidationException("La classe doit être publique");
		if (c.getExceptionTypes().length != 0)
			throw new ValidationException("Le constructeur (Socket) ne doit pas lever d'exception");
		for (Field field : fields){
			int modifierss = field.getModifiers();
			if (field.getType().getSimpleName().equals("Socket") && (!Modifier.isPrivate(modifierss) || Modifier.isFinal(modifierss))) 
				throw new ValidationException("l'attribut de type Socket doit être private et final");
		}
	}
	

	public static void supprimerService(int choix) {
		servicesClasses.remove(choix-1);
	}

	public static void majService(int choix, Class<? extends Service> c) throws ValidationException {
		validation(c);
		servicesClasses.set(choix - 1, new ClasseDemarree(c,true));
	}
		
	public static ClasseDemarree getServiceClass(int numService) {
		return servicesClasses.get(numService-1);
	}
	
		public static String toStringue() {
			String result = "Activités pr�sentes :##";
			for(int i = 0; i < servicesClasses.size(); i++)
				result += (i+1 + " : " + servicesClasses.get(i).getMaclasse().getSimpleName()+" (démarrée: " +servicesClasses.get(i).isDemarree()+ ",nom du programmeur qui l'a codé: "+servicesClasses.get(i).getMaclasse().getPackageName()+")##");
			return result;
		}
}
