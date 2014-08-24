package io.lp0onfire.fireband;

import org.json.JSONException;
import org.json.JSONObject;

public class EffectBuilder {
  private double itemWeightFactor = 0.0;
  public void setItemWeightFactor(double d){itemWeightFactor = d;}
  
  private int weaponToHitBonus = 0;
  public void setWeaponToHitBonus(int i){weaponToHitBonus = i;}
  private double weaponDamageFactor = 0.0;
  public void setWeaponDamageFactor(double d){weaponDamageFactor = d;}
  private int weaponExtraDamageDice = 0;
  public void setWeaponExtraDamageDice(int i){weaponExtraDamageDice = i;}
  
  private int armourClassBonus = 0;
  public void setArmourClassBonus(int i){armourClassBonus = i;}
  private int armourCheckPenaltyReduction = 0;
  public void setArmourCheckPenaltyReduction(int i){
    armourCheckPenaltyReduction = i;
    }
  private boolean armourCountsAsLight = false;
  public void setArmourCountsAsLight(boolean b){armourCountsAsLight = b;}
  
  public EffectBuilder(){}
  
  public EffectBuilder(JSONObject obj) throws JSONException{
    if(obj.has("weightFactor")){
      setItemWeightFactor(obj.getDouble("weightFactor"));
    }
    if(obj.has("toHitBonus")){
      setWeaponToHitBonus(obj.getInt("toHitBonus"));
    }
    if(obj.has("damageFactor")){
      setWeaponDamageFactor(obj.getDouble("damageFactor"));
    }
    if(obj.has("extraDamageDice")){
      setWeaponExtraDamageDice(obj.getInt("extraDamageDice"));
    }
    if(obj.has("acBonus")){
      setArmourClassBonus(obj.getInt("acBonus"));
    }
    if(obj.has("acPenaltyReduction")){
      setArmourCheckPenaltyReduction(obj.getInt("acPenaltyReduction"));
    }
    if(obj.has("lightArmour")){
      setArmourCountsAsLight(obj.getBoolean("lightArmour"));
    }
  }
  
  public Effect build(){
    return new Effect(itemWeightFactor,
        weaponToHitBonus, weaponDamageFactor, weaponExtraDamageDice,
        armourClassBonus, armourCheckPenaltyReduction,
        armourCountsAsLight);
  }
}
