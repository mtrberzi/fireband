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

public class Affixes {
  private static Logger log = LogManager.getLogger("Affixes");
  public final static Affixes instance = new Affixes();
  // Affixes are usually keyed to specific item types.
  private Map<ItemType, List<Affix>> affixes;
  private Affixes(){
    affixes = new HashMap<ItemType, List<Affix>>();
    for(ItemType type : ItemType.values()){
      affixes.put(type, new ArrayList<Affix>());
    }
  }
  
  public List<Affix> getAffixesByItemType(ItemType type){
    return affixes.get(type);
  }
  
  public void loadAffixes(String affixData) throws JSONException{
    JSONObject obj = new JSONObject(affixData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        AffixBuilder aBuild = new AffixBuilder(jObj);
        Affix a = aBuild.build();
        log.debug("Loaded affix '" + key + "'");
        for(ItemType type : a.getAllowedItemTypes()){
          affixes.get(type).add(a);
        }
      }
    }
    // print statistics
    int count = 0;
    for(ItemType type : ItemType.values()){
      List<Affix> affixesByType = getAffixesByItemType(type);
      log.info(Integer.toString(affixesByType.size()) + " "
          + type.toString() + " affixes");
      count += affixesByType.size();
    }
    log.info(Integer.toString(count) + " total affixes");
  }
}
