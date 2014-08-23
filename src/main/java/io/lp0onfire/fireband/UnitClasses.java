package io.lp0onfire.fireband;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class UnitClasses {
  private static Logger log = LogManager.getLogger("UnitClasses");
  public static final UnitClasses instance = new UnitClasses();
  private UnitClasses(){}
  
  private Map<String, UnitClass> unitClasses = new HashMap<>();
  public UnitClass getUnitClassByName(String name){
    return unitClasses.get(name);
  }
  
  private UnitClass buildUnitClass(JSONObject obj) throws JSONException{
    String name = obj.getString("name");
    BaseAttackBonusType babType = BaseAttackBonusType.valueOf(
        obj.getString("baseAttackBonus"));
    SavingThrowType fortSave = SavingThrowType.valueOf(
        obj.getString("fortitudeSave"));
    SavingThrowType reflSave = SavingThrowType.valueOf(
        obj.getString("reflexSave"));
    SavingThrowType willSave = SavingThrowType.valueOf(
        obj.getString("willSave"));
    int hitDieSize = obj.getInt("hitDie");
    return new UnitClass(name, babType, fortSave, reflSave, willSave,
        hitDieSize);
  }
  
  public void loadUnitClasses(String unitClassData) throws JSONException{
    JSONObject obj = new JSONObject(unitClassData);
    Iterator<?> keys = obj.keys();
    while(keys.hasNext()){
      String key = (String)keys.next();
      if(obj.get(key) instanceof JSONObject){
        JSONObject jObj = (JSONObject) obj.get(key);
        UnitClass c = buildUnitClass(jObj);
        log.debug("Loaded unit class '" + key + "'");
        unitClasses.put(key, c);
      }
    }
    // print statistics
    log.info(Integer.toString(unitClasses.size()) + " total unit classes");
  }
  
}
