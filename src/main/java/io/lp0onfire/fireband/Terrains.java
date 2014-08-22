package io.lp0onfire.fireband;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class Terrains {
  private static Logger log = LogManager.getLogger("Terrains");
  public static final Terrains instance = new Terrains();
  private Terrains(){}
  
  private Map<String, Terrain> terrains = new HashMap<>();
  public Terrain getTerrainByName(String name){
    return terrains.get(name);
  }
  
  private Terrain buildTerrain(JSONObject obj) throws JSONException{
    String name = obj.getString("name");
    int moveCost = obj.getInt("moveCost");
    String symbolString = obj.getString("symbol");
    char symbol = symbolString.charAt(0);
    return new Terrain(name, moveCost, symbol);
  }
  
  public void loadTerrains(String featureData) throws JSONException{
    JSONObject obj = new JSONObject(featureData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        Terrain t = buildTerrain(jObj);
        log.debug("Loaded terrain '" + key + "'");
        terrains.put(key, t);
      }
    }
    // print statistics
    log.info(Integer.toString(terrains.size()) + " total terrains");
  }
}
