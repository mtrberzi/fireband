package io.lp0onfire.fireband;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class Features {
  private static Logger log = LogManager.getLogger("Features");
  public static final Features instance = new Features();
  private Features(){}
  
  private Map<String, Feature> features = new HashMap<>();
  public Feature getFeatureByName(String name){
    return features.get(name);
  }
  
  private Feature buildFeature(JSONObject obj) throws JSONException{
    String name = obj.getString("name");
    return new Feature(name);
  }
  
  public void loadFeatures(String featureData) throws JSONException{
    JSONObject obj = new JSONObject(featureData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        Feature f = buildFeature(jObj);
        log.debug("Loaded feature '" + key + "'");
        features.put(key, f);
      }
    }
    // print statistics
    log.info(Integer.toString(features.size()) + " total features");
  }
}
