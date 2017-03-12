package galgeleg.soap;

import java.io.IOException;
import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;

import brugerautorisation.data.Bruger;
import org.json.JSONArray;
import org.json.JSONObject;
@WebService
public interface GalgeISOAP {
	
	@WebMethod ArrayList<String> getBrugteBogstaver();
	@WebMethod String getSynligtOrd();
	@WebMethod String getOrdet();
	@WebMethod int getAntalForkerteBogstaver();
	@WebMethod boolean erSidsteBogstavKorrekt();
	@WebMethod boolean erSpilletVundet();
	@WebMethod boolean erSpilletTabt();
	@WebMethod boolean erSpilletSlut();
	
	@WebMethod void nulstil();
	@WebMethod void opdaterSynligtOrd();
	@WebMethod void gætBogstav(String s);
	@WebMethod void logStatus();
	@WebMethod String hentUrl(String s) throws Exception; 
	@WebMethod void hentOrdFraDr() throws Exception;
	@WebMethod Bruger hentBruger(String user, String pass) throws Exception;
    @WebMethod public void hentOrdFraDrRest();
    @WebMethod String getHighscore()throws Exception;
}
