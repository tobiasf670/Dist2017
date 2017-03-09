package galgeleg.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import javax.jws.WebService;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.HentOrdFraDrTv;

@WebService(endpointInterface = "galgeleg.soap.GalgeISOAP")
public class GalgelogikImpl implements GalgeISOAP {
  private ArrayList<String> muligeOrd = new ArrayList<String>();
  private String ordet;
  private ArrayList<String> brugteBogstaver = new ArrayList<String>();
  private String synligtOrd;
  private int antalForkerteBogstaver;
  private boolean sidsteBogstavVarKorrekt;
  private boolean spilletErVundet;
  private boolean spilletErTabt;
  private String currentUser;


  public ArrayList<String> getBrugteBogstaver() {
    return brugteBogstaver;
  }

  public String getSynligtOrd() {
    return synligtOrd;
  }

  public String getOrdet() {
    return ordet;
  }

  public int getAntalForkerteBogstaver() {
    return antalForkerteBogstaver;
  }

  public boolean erSidsteBogstavKorrekt() {
    return sidsteBogstavVarKorrekt;
  }

  public boolean erSpilletVundet() {
	  if(spilletErVundet){
		File file = new File("highscores.txt");
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.append(currentUser + " vandt spillet med " + getAntalForkerteBogstaver() + " forkerte bogstaver. \n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
    return spilletErVundet;
  }

  public boolean erSpilletTabt() {
    return spilletErTabt;
  }

  public boolean erSpilletSlut() {
    return spilletErTabt || spilletErVundet;
  }


  public GalgelogikImpl() throws java.rmi.RemoteException{
    muligeOrd.add("bil");
    muligeOrd.add("computer");
    muligeOrd.add("programmering");
    muligeOrd.add("motorvej");
    muligeOrd.add("busrute");
    muligeOrd.add("gangsti");
    muligeOrd.add("skovsnegl");
    muligeOrd.add("solsort");
    nulstil();
  }

  public void nulstil() {
    brugteBogstaver.clear();
    antalForkerteBogstaver = 0;
    spilletErVundet = false;
    spilletErTabt = false;
    ordet = muligeOrd.get(new Random().nextInt(muligeOrd.size()));
    opdaterSynligtOrd();
  }


  public void opdaterSynligtOrd() {
    synligtOrd = "";
    spilletErVundet = true;
    for (int n = 0; n < ordet.length(); n++) {
      String bogstav = ordet.substring(n, n + 1);
      if (brugteBogstaver.contains(bogstav)) {
        synligtOrd = synligtOrd + bogstav;
      } else {
        synligtOrd = synligtOrd + "*";
        spilletErVundet = false;
      }
    }
  }

  public void gætBogstav(String bogstav) {
    if (bogstav.length() != 1) return;
    System.out.println("Der gættes på bogstavet: " + bogstav);
    if (brugteBogstaver.contains(bogstav)) return;
    if (spilletErVundet || spilletErTabt) return;

    brugteBogstaver.add(bogstav);

    if (ordet.contains(bogstav)) {
      sidsteBogstavVarKorrekt = true;
      System.out.println("Bogstavet var korrekt: " + bogstav);
    } else {
      // Vi gættede på et bogstav der ikke var i ordet.
      sidsteBogstavVarKorrekt = false;
      System.out.println("Bogstavet var IKKE korrekt: " + bogstav);
      antalForkerteBogstaver = antalForkerteBogstaver + 1;
      if (antalForkerteBogstaver > 6) {
        spilletErTabt = true;
      }
    }
    opdaterSynligtOrd();
  }

  public void logStatus() {
    System.out.println("---------- ");
    System.out.println("- ordet (skult) = " + ordet);
    System.out.println("- synligtOrd = " + synligtOrd);
    System.out.println("- forkerteBogstaver = " + antalForkerteBogstaver);
    System.out.println("- brugeBogstaver = " + brugteBogstaver);
    if (spilletErTabt) System.out.println("- SPILLET ER TABT");
    if (spilletErVundet) System.out.println("- SPILLET ER VUNDET");
    System.out.println("---------- ");
  }


  public String hentUrl(String url) throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
    StringBuilder sb = new StringBuilder();
    String linje = br.readLine();
    while (linje != null) {
      sb.append(linje + "\n");
      linje = br.readLine();
    }
    return sb.toString();
  }

  public void hentOrdFraDr() throws Exception{
  String data = hentUrl("http://dr.dk");
    System.out.println("data = " + data);

    data = data.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
    System.out.println("data = " + data);
    muligeOrd.clear();
    muligeOrd.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

    System.out.println("muligeOrd = " + muligeOrd);
    nulstil();
  }
  
  
  
//  public void hentOrdFraDrRest(){
//     
//     Client client = ClientBuilder.newClient();
//     Response res = client.target("http://www.dr.dk/mu-online/api/1.3/list/view/mostviewed?channel=drtv&channeltype=TV&limit=3&offset=0")
//              .request(MediaType.APPLICATION_JSON).get();
//     
// String svar = res.readEntity(String.class);
// try {
//    //Parse svar som et JSON-objekt
//    JSONObject json = new JSONObject(svar);
//  
//     JSONArray k = json.getJSONArray("Items");
//     
//     Response ord = client.target("http://www.dr.dk/mu-online/api/1.3/programcard/"+k.getJSONObject(0).getString("Urn"))
//             .request(MediaType.APPLICATION_JSON).get();
//     String ordSvar = ord.readEntity(String.class);
//     json = new JSONObject(ordSvar);
//     String ordFraDRRest = json.getString("Description");    
//     ordFraDRRest= ordFraDRRest.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
//   String [] ordFraDR  = ordFraDRRest.split(" ");
//   
//   System.out.println("ORD ER NU HENTET");
//   
//     } catch (Exception e) {
//    e.printStackTrace();
//  }
//  }

@Override
public Bruger hentBruger(String user, String pass) throws Exception {
	Brugeradmin ba = (Brugeradmin) Naming.lookup("rmi://javabog.dk/brugeradmin");

    //ba.sendGlemtAdgangskodeEmail("jacno", "Dette er en test, husk at skifte kode");
		//ba.ændrAdgangskode("jacno", "kodenj4gvs", "xxx");
		Bruger b = ba.hentBruger(user, pass);
		
		if (b.brugernavn != null){
			currentUser = b.brugernavn;
		}
		return b;
}

    @Override
    public void hentOrdFraDrRest() { 

    	
    	
    	
    	
//    Client client = ClientBuilder.newClient();
//     Response res = client.target("http://www.dr.dk/mu-online/api/1.3/list/view/mostviewed?channel=drtv&channeltype=TV&limit=3&offset=0")
//              .request(MediaType.APPLICATION_JSON).get();
//     
// String svar = res.readEntity(String.class);
// try {
//    //Parse svar som et JSON-objekt
//    JSONObject json = new JSONObject(svar);
//  
//     JSONArray k = json.getJSONArray("Items");
//     
//     Response ord = client.target("http://www.dr.dk/mu-online/api/1.3/programcard/"+k.getJSONObject(0).getString("Urn"))
//             .request(MediaType.APPLICATION_JSON).get();
//     String ordSvar = ord.readEntity(String.class);
//     json = new JSONObject(ordSvar);
//     String ordFraDRRest = json.getString("Description");    
//     ordFraDRRest= ordFraDRRest.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
//   String [] ordFraDR  = ordFraDRRest.split(" ");
//   
//   System.out.println("ORD ER NU HENTET");
//   
//   
//        
//        muligeOrd.clear();
//        muligeOrd.addAll(new HashSet<String>(Arrays.asList(ordFraDR)));
//        nulstil();
//        
//          } catch (Exception e) {
//    e.printStackTrace();
//  }
        
        HentOrdFraDrTv ordDR = new HentOrdFraDrTv();
        muligeOrd.clear();
        muligeOrd.addAll(new HashSet<String>(Arrays.asList(ordDR.getOrdDr())));
        nulstil();
        }

	
    
    
    @Override
	public String getHighscore() {
		File file = new File("highscores.txt");
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
	}
}
