package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class ArmourBuilder {
  private String name = "Decoy";
  private int baseWeight = 0;
  private int baseValue = 0;
  private List<Affix> affixes = new ArrayList<Affix>();
  public void addAffix(Affix a){
    // TODO check for duplicate affixes
    affixes.add(a);
  }
  private ArmourType type = ArmourType.ARMOUR_BODY;
  private boolean isHeavy = false;
  private int baseArmourClass = 0;
  private int armourClassBonus = 0;
  public void setArmourClassBonus(int n){this.armourClassBonus = n;}
  private int armourCheckPenalty = 0;
  
  public ArmourBuilder(Armour base){
    // copy everything from a template
    name = base.getName();
    baseWeight = base.getBaseWeight();
    baseValue = base.getBaseValue();
    affixes = new ArrayList<Affix>(base.getAffixes());
    type = base.getArmourType();
    isHeavy = base.isHeavyArmour();
    armourClassBonus = base.getArmourClassBonus();
    armourCheckPenalty = base.getArmourCheckPenalty();
  }
  
  public ArmourBuilder(JSONObject obj) throws JSONException{
    name = obj.getString("name");
    baseWeight = obj.getInt("weight");
    baseValue = obj.getInt("value");
    baseArmourClass = obj.getInt("ac");
    type = ArmourType.valueOf(obj.getString("type"));
    if(type == null){
      throw new IllegalArgumentException("unknown armour type '" + obj.getString("type") + "'");
    }
    
    // optional
    affixes = new ArrayList<Affix>(); // TODO
    isHeavy = obj.optBoolean("heavy");
    armourClassBonus = obj.optInt("acBonus");
    armourCheckPenalty = obj.optInt("penalty");
  }
  
  public Armour build(){
    return new Armour(name, baseWeight, baseValue,
        type, isHeavy, affixes, baseArmourClass,
        armourClassBonus, armourCheckPenalty);
  }
  
}
