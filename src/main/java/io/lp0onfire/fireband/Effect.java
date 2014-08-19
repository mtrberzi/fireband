package io.lp0onfire.fireband;

public class Effect {

  // Modifiers for all items.
  private double itemWeightFactor = 0.0;
  public double getItemWeightFactor(){return itemWeightFactor;}
  private double itemValueFactor = 0.0;
  public double getItemValueFactor(){return itemValueFactor;}
  
  // Modifiers for weapons.
  private int weaponToHitBonus = 0;
  public int getWeaponToHitBonus(){return weaponToHitBonus;}
  private double weaponDamageFactor = 0.0;
  public double getWeaponDamageFactor(){return weaponDamageFactor;}
  private int weaponExtraDamageDice = 0;
  public int getWeaponExtraDamageDice(){return weaponExtraDamageDice;}
  
  // Modifiers for armour.
  private int armourClassBonus = 0;
  public int getArmourClassBonus(){return armourClassBonus;}
  private int armourCheckPenaltyReduction = 0;
  public int getArmourCheckPenaltyReduction(){return armourCheckPenaltyReduction;}
  private boolean armourCountsAsLight = false;
  public boolean getArmourCountsAsLight(){return armourCountsAsLight;}
  
  public Effect(double itemWeightFactor, double itemValueFactor,
      int weaponToHitBonus, double weaponDamageFactor,
      int weaponExtraDamageDice,
      int armourClassBonus, int armourCheckPenaltyReduction,
      boolean armourCountsAsLight){
    this.itemWeightFactor = itemWeightFactor;
    this.itemValueFactor = itemValueFactor;
    this.weaponToHitBonus = weaponToHitBonus;
    this.weaponDamageFactor = weaponDamageFactor;
    this.weaponExtraDamageDice = weaponExtraDamageDice;
    this.armourClassBonus = armourClassBonus;
    this.armourCheckPenaltyReduction = armourCheckPenaltyReduction;
    this.armourCountsAsLight = armourCountsAsLight;
  }
  
}
