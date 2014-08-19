package io.lp0onfire.fireband;

import java.util.List;

public class Weapon extends Item {
  
  private WeaponType weaponType;
  public WeaponType getWeaponType(){return weaponType;}
  
  private int damageDice = 0;
  public int getDamageDice(){return damageDice;}
  private int damageDieSize = 0;
  public int getDamageDieSize(){return damageDieSize;}
  private DamageType damageType;
  public DamageType getDamageType(){return damageType;}
  
  private int criticalThreshold = 20;
  public int getCriticalThreshold(){return criticalThreshold;}
  private int criticalMultiplier = 1;
  public int getCriticalMultiplier(){return criticalMultiplier;}
  
  private int toHitBonus = 0;
  public int getToHitBonus(){return toHitBonus;}
  private int toDamageBonus = 0;
  public int getToDamageBonus(){return toDamageBonus;}
  
  private boolean isSimple = false;
  public boolean isSimpleWeapon(){return isSimple;}
  
  private int range = 1;
  public int getRange(){return range;}
  
  public Weapon(String name, int baseWeight, int baseValue,
      WeaponType weaponType,
      int damageDice, int damageDieSize, DamageType damageType,
      int criticalThreshold, int criticalMultiplier,
      List<Affix> affixes,
      int toHitBonus, int toDamageBonus, int range,
      boolean isSimple) {
    super(ItemType.TYPE_WEAPON, name, baseWeight, baseValue, affixes);
    this.weaponType = weaponType;
    this.damageDice = damageDice;
    this.damageDieSize = damageDieSize;
    this.damageType = damageType;
    this.criticalThreshold = criticalThreshold;
    this.criticalMultiplier = criticalMultiplier;
    this.toHitBonus = toHitBonus;
    this.toDamageBonus = toDamageBonus;
    this.range = range;
    this.isSimple = isSimple;
  }
  
}
