package galgeleg.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import brugerautorisation.data.Bruger;
import rest.HentOrdFraDrTv;





public class GalgelegKlient{
	
	static GalgeISOAP spil;


  public static void main(String[] args) throws Exception  {
      
      
	  System.out.println("Velkommen til Galgeleg");
	  Scanner scan = new Scanner(System.in);
	  System.out.println("Indtast Brugernavn");
	  String user = scan.next();
	  System.out.println("Indtast password!");
	  String pass = scan.next();
	  
	  boolean legitUser = false;
	  
	  URL url = new URL("http://ec2-35-165-42-120.us-west-2.compute.amazonaws.com:9901/galgeSOAP?wsdl");
         //URL url = new URL("http://localhost:9901/galgeSOAP?wsdl");
	  
	  QName qname = new QName("http://soap.galgeleg/", "GalgelogikImplService");
	  Service service = Service.create(url, qname);
		GalgeISOAP ba = service.getPort(GalgeISOAP.class);
		
		
		Bruger bruger = ba.hentBruger(user,  pass);
		if (bruger != null){
			legitUser = true;
		}
	
	  
	  if(legitUser){
		  System.out.println("Du er nu logget ind!");
		  
	  }
	  else{
		  System.out.println("Login fejlet");
		  
	  }
	 
	  
	  boolean igen = true;
	  
	  while (igen){
	  try {
		 igen = spilSpillet(ba);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  
	  }
	  
  }
  
  public static boolean spilSpillet(GalgeISOAP face) throws MalformedURLException, RemoteException, NotBoundException {
	  
	  spil = face;
        
	 
    
	  System.out.println("Nyt Spil Startet");
	  
	  Scanner scan = new Scanner(System.in);
	  spil.nulstil();
	    try {
	      spil.hentOrdFraDrRest();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    
	    while (!spil.erSpilletSlut()){
	    	System.out.println("Gæt et bogstav!");
	    	String bogstav = scan.next();
	    	if(bogstav.length() > 1){
	    		System.out.println("Du må kun gæte et bogstav ad gangen! Det første bogstav, du skrev bruges i stedet.");
	    		bogstav = bogstav.substring(0, 1);
	    		
	    	}
	    	
	    	
	    	spil.gætBogstav(bogstav);
	    	
	    	System.out.println("Foreløbigt ord: " + spil.getSynligtOrd());
	    	System.out.println("Brugte bogstaver: " + spil.getBrugteBogstaver());
	    	System.out.println("Du har " + (7 - spil.getAntalForkerteBogstaver()) + " gæt tilbage!");
		
	    	
	    }
	    System.out.println("Spillet er slut! \n Prøv igen Y/N?"); 
	    String nus = scan.next();
	    if (nus.equalsIgnoreCase("Y")){
	    	return true;
	    }
	    else {
	    	System.out.println("Tak for denne gang");
	    	return false;
	    	
	    }
	  
  }
  
}
