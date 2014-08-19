package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseArmour {
  private static Logger log = LogManager.getLogger("BaseArmour");
  public final static BaseArmour instance = new BaseArmour();
  private BaseArmour(){}
  
  private List<Armour> baseArmour = new ArrayList<Armour>();
  public List<Armour> getBaseArmour(){
    return baseArmour;
  }
  public List<Armour> getBaseArmourByType(ArmourType type){
    List<Armour> results = new ArrayList<Armour>();
    for(Armour a : baseArmour){
      if(a.getArmourType().equals(type)){
        results.add(a);
      }
    }
    return results;
  }
  public List<Armour> getBaseWeaponsByHeaviness(boolean isHeavy){
    List<Armour> results = new ArrayList<Armour>();
    for(Armour a : baseArmour){
      if(a.isHeavyArmour() == isHeavy){
        results.add(a);
      }
    }
    return results;
  }
  
  public void loadArmour(String armourData) throws JSONException{
    JSONObject obj = new JSONObject(armourData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        ArmourBuilder aBuild = new ArmourBuilder(jObj);
        Armour a  = aBuild.build();
        log.debug("Loaded base armour '" + key + "'");
        baseArmour.add(a);
      }
    }
    // print statistics
    log.info(Integer.toString(baseArmour.size()) + " total armour items");
    for(ArmourType type : ArmourType.values()){
      List<Armour> armourByType = getBaseArmourByType(type);
      log.info(Integer.toString(armourByType.size()) + " "
          + type.toString() + " armour");
    }
  }
}
