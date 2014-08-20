package io.lp0onfire.fireband;

import java.util.List;

public class Armour extends Item {

  private ArmourType armourType;
  public ArmourType getArmourType(){return armourType;}
  
  private String description = "Protective gear for battle.";
  public String getDescription(){
    return description;
  }
  
  public String getFullDescription(){
    StringBuilder sb = new StringBuilder();
    String br = System.getProperty("line.separator");
    // describe the weight and value
    sb.append(String.format("It weighs %d.%d pounds and is worth %d gold piece%s.",
        getEffectiveWeight() / 10, getEffectiveWeight() % 10,
        getEffectiveValue(),
        getEffectiveValue() == 1 ? "" : "s")).append(br);
    // describe armour class
    if(getEffectiveArmourClassBonus() == 0){
      sb.append(String.format("It provides a total Armour Class of %d.",
          getEffectiveArmourClass()));
    }else{
      sb.append("It provides a base protection of ").append(getBaseArmourClass());
      if(getEffectiveArmourClassBonus() > 0){
        sb.append(String.format(" and an additional +%d enhancement bonus ",
            getEffectiveArmourClassBonus()));
      }else{
        sb.append(String.format(" but with a %d penalty ",
            getEffectiveArmourClassBonus()));
      }
      sb.append("for a total Armour Class of ")
      .append(getEffectiveArmourClass()).append(".").append(br);
    }
    return sb.toString();
  }
  
  // optional items
  
  private boolean isHeavy = false;
  public boolean isHeavyArmour(){return isHeavy;}
  
  private int baseArmourClass = 0;
  public int getBaseArmourClass(){return baseArmourClass;}
  
  private int armourClassBonus = 0;
  public int getArmourClassBonus(){return armourClassBonus;}
  
  private int armourCheckPenalty = 0;
  public int getArmourCheckPenalty(){return armourCheckPenalty;}
  
  public int getEffectiveArmourClass(){
    return getBaseArmourClass() + getEffectiveArmourClassBonus();
  }
  
  public int getEffectiveArmourClassBonus(){
    int base = getArmourClassBonus();
    for(Affix a : getAffixes()){
      for(Effect e : a.getEffects()){
        base += e.getArmourClassBonus();
      }
    }
    return base;
  }
  
  public int getEffectiveArmourCheckPenalty(){
    int base = armourCheckPenalty;
    for(Affix a : getAffixes()){
      for(Effect e : a.getEffects()){
        base += e.getArmourCheckPenaltyReduction();
      }
    }
    if(base > 0) base = 0;
    return base;
  }
  
  public boolean isEffectivelyHeavyArmour(){
    if(!isHeavyArmour()) return false;
    // otherwise, we normally return true, except...
    for(Affix a : getAffixes()){
      for(Effect e : a.getEffects()){
        if(e.getArmourCountsAsLight()){
          return false;
        }
      }
    }
    // not cancelled by an affix
    return true;
  }
  
  public Armour(String name, int baseWeight, int baseValue,
      ArmourType armourType, boolean isHeavy,
      List<Affix> affixes,
      int baseArmourClass, int armourClassBonus, 
      int armourCheckPenalty){
    super(ItemType.TYPE_ARMOUR, name, baseWeight, baseValue, affixes);
    this.armourType = armourType;
    this.isHeavy = isHeavy;
    this.baseArmourClass = baseArmourClass;
    this.armourClassBonus = armourClassBonus;
    this.armourCheckPenalty = armourCheckPenalty;
  }
  
}
