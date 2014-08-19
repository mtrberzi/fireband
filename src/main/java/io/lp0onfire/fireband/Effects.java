package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class Effects {
  private static Logger log = LogManager.getLogger("Effects");
  public final static Effects instance = new Effects();
  // Effects are most efficiently looked up by name.
  private Map<String, Effect> effects = new HashMap<String,Effect>();
  private Effects(){}
  
  public Effect getEffectByName(String name){
    return effects.get(name);
  }
  
  public void loadEffects(String effectData) throws JSONException{
    JSONObject obj = new JSONObject(effectData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        EffectBuilder eBuild = new EffectBuilder(jObj);
        Effect e = eBuild.build();
        log.debug("Loaded effect '" + key + "'");
        effects.put(key, e);
      }
    }
    // print statistics
    log.info(Integer.toString(effects.size()) + " total effects");
  }
}
