package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseWeapons {
  private static Logger log = LogManager.getLogger("BaseWeapons");
  public final static BaseWeapons instance = new BaseWeapons();
  private BaseWeapons(){}
  
  private List<Weapon> baseWeapons = new ArrayList<Weapon>();
  public List<Weapon> getBaseWeapons(){
    return baseWeapons;
  }
  public List<Weapon> getBaseWeaponsByType(WeaponType type){
    List<Weapon> results = new ArrayList<Weapon>();
    for(Weapon w : baseWeapons){
      if(w.getType().equals(type)){
        results.add(w);
      }
    }
    return results;
  }
  public List<Weapon> getBaseWeaponsBySimple(boolean isSimple){
    List<Weapon> results = new ArrayList<Weapon>();
    for(Weapon w : baseWeapons){
      if(w.isSimpleWeapon() == isSimple){
        results.add(w);
      }
    }
    return results;
  }
  
  public void loadWeapons(String weaponData) throws JSONException{
    JSONObject obj = new JSONObject(weaponData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        WeaponBuilder wBuild = new WeaponBuilder(jObj);
        Weapon w = wBuild.build();
        log.debug("Loaded base weapon '" + key + "'");
        baseWeapons.add(w);
      }
    }
  }
}
