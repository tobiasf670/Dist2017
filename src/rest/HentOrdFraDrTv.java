/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;



import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Tobias
 */
public class HentOrdFraDrTv {
    
    public String [] getOrdDr(){
        
        String [] ordFraDR = null;
      
        
        Client client = ClientBuilder.newClient();
     Response res = client.target("http://www.dr.dk/mu-online/api/1.3/list/view/mostviewed?channel=drtv&channeltype=TV&limit=3&offset=0")
              .request(MediaType.APPLICATION_JSON).get();
     
    
 String svar = res.readEntity(String.class);
  try {
    //Parse svar som et JSON-objekt
    JSONObject json = new JSONObject(svar);
  
     JSONArray k = json.getJSONArray("Items");
     
     Response ord = client.target("http://www.dr.dk/mu-online/api/1.3/programcard/"+k.getJSONObject(0).getString("Urn"))
             .request(MediaType.APPLICATION_JSON).get();
     String ordSvar = ord.readEntity(String.class);
     json = new JSONObject(ordSvar);
     String ordFraDRRest = json.getString("Description");    
     ordFraDRRest= ordFraDRRest.replaceAll("<.+?>", " ").toLowerCase().replaceAll("[^a-zæøå]", " ");
     ordFraDR = ordFraDRRest.split(" ");
     


  } catch (Exception e) {
    e.printStackTrace();
  }
        return ordFraDR;
    
    

    }
    
    public String [] getOrdApache(){
        
        String [] ordFraDR = null;
      
        
        Client client = ClientBuilder.newClient();
     Response res = client.target("http://ec2-35-165-42-120.us-west-2.compute.amazonaws.com:8080/restservicetest/webapi/scores")
              .request(MediaType.APPLICATION_JSON).get();
     
    
 String svar = res.readEntity(String.class);
  try {
    //Parse svar som et JSON-objekt
    JSONObject json = new JSONObject(svar);
  
     JSONArray k = json.getJSONArray("Highscore");
     
     Response ord = client.target("http://www.dr.dk/mu-online/api/1.3/programcard/"+k.getJSONObject(0).getString("Urn"))
             .request(MediaType.APPLICATION_JSON).get();
     String ordSvar = ord.readEntity(String.class);
     json = new JSONObject(ordSvar);
     String ordFraDRRest = json.getString("name");    
     
     ordFraDR = ordFraDRRest.split(" ");
     


  } catch (Exception e) {
    e.printStackTrace();
  }
        return ordFraDR;
    
    

    }
    
    public static void main(String[] args) 
    {
      HentOrdFraDrTv tv = new HentOrdFraDrTv();
      
      tv.getOrdApache();
    }
}