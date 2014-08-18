package io.lp0onfire.fireband;

public class Affix {
  private String name;
  public String getName(){return name;}
  
  // true: prefix (appear before item name)
  // false: suffix (appear after item name)
  private boolean prefix = true;
  public boolean isPrefix(){return prefix;}
  
  // TODO refactor this out into a general "Effect" class
  
  // Modifiers for all items.
  private double itemWeightFactor = 0.0;
  private double itemValueFactor = 0.0;
  
  // Modifiers for weapons.
  private int weaponToHitBonus = 0;
  private double weaponDamageFactor = 0.0;
  
  // Modifiers for armour.
  private int armourACBonus = 0;
  private int armourToHitBonus = 0;
  
}
