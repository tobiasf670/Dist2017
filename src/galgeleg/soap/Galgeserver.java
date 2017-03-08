package galgeleg.soap;

import java.rmi.Naming;

import javax.xml.ws.Endpoint;

import brugerautorisation.server.Brugerdatabase;
import brugerautorisation.transport.soap.BrugeradminImpl;

 
 

public class Galgeserver {
	
	public static void main(String[] arg) throws Exception
	{
		// Enten: Kør programmet 'rmiregistry' fra mappen med .class-filerne, eller:
		// start i server-JVM

		GalgeISOAP k = new GalgelogikImpl();
		Endpoint.publish("http://[::]:9901/galgeSOAP", k);
		
		System.out.println("Kontotjeneste registreret.");
	}

}