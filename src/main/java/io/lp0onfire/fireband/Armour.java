package io.lp0onfire.fireband;

import java.util.List;

public class Armour extends Item {

  private ArmourType armourType;
  public ArmourType getArmourType(){return armourType;}
  
  // optional items
  
  private boolean isHeavy = false;
  public boolean isHeavyArmour(){return isHeavy;}
  
  private int baseArmourClass = 0;
  public int getBaseArmourClass(){return baseArmourClass;}
  
  private int armourClassBonus = 0;
  public int getArmourClassBonus(){return armourClassBonus;}
  
  private int armourCheckPenalty = 0;
  public int getArmourCheckPenalty(){return armourCheckPenalty;}
  
  public Armour(String name, int baseWeight, int baseValue,
      ArmourType armourType, boolean isHeavy,
      List<Affix> affixes,
      int baseArmourClass, int armourClassBonus, 
      int armourCheckPenalty){
    super(ItemType.TYPE_ARMOUR, name, baseWeight, baseValue, affixes);
    this.armourType = armourType;
    this.isHeavy = isHeavy;
    this.armourClassBonus = armourClassBonus;
    this.armourCheckPenalty = armourCheckPenalty;
  }
  
}
