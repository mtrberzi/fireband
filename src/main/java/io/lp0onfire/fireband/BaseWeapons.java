package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseWeapons {
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
  
  public void loadWeapons(String weaponData) throws JSONException{
    JSONObject obj = new JSONObject(weaponData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        Weapon w = Weapon.buildFromJSON(jObj);
        baseWeapons.add(w);
      }
    }
  }
}
