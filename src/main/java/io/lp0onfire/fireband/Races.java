package io.lp0onfire.fireband;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class Races {
  private static Logger log = LogManager.getLogger("Races");
  public static final Races instance = new Races();
  private Races(){}
  
  private Map<String, Race> races = new HashMap<>();
  public Race getRaceByName(String name){
    return races.get(name);
  }
  
  private Race buildRace(JSONObject obj) throws JSONException{
    String name = obj.getString("name");
    int strMod = obj.getInt("strMod");
    int dexMod = obj.getInt("dexMod");
    int conMod = obj.getInt("conMod");
    int intMod = obj.getInt("intMod");
    int wisMod = obj.getInt("wisMod");
    int chaMod = obj.getInt("chaMod");
    int baseMovement = obj.getInt("movement");
    return new Race(name,
        strMod, dexMod, conMod, intMod, wisMod, chaMod,
        baseMovement);
  }
  
  public void loadRaces(String raceData) throws JSONException{
    JSONObject obj = new JSONObject(raceData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        Race r = buildRace(jObj);
        log.debug("Loaded race '" + key + "'");
        races.put(key, r);
      }
    }
    // print statistics
    log.info(Integer.toString(races.size()) + " total races");
  }
  
}
