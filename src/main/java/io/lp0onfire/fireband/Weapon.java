package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Weapon extends Item {

  // mandatory items
  
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

  // optional items
  private List<Affix> affixes;
  public List<Affix> getAffixes(){return affixes;}
  
  private int toHitBonus = 0;
  public int getToHitBonus(){return toHitBonus;}
  private int toDamageBonus = 0;
  public int getToDamageBonus(){return toDamageBonus;}
  
  private int range = 1;
  public int getRange(){return range;}
  
  public Weapon(String name, int baseWeight, int baseValue,
      WeaponType weaponType,
      int damageDice, int damageDieSize, DamageType damageType,
      int criticalThreshold, int criticalMultiplier,
      Collection<Affix> affixes,
      int toHitBonus, int toDamageBonus, int range) {
    super(ItemType.TYPE_WEAPON, name, baseWeight, baseValue);
    this.damageDice = damageDice;
    this.damageDieSize = damageDieSize;
    this.damageType = damageType;
    this.criticalThreshold = criticalThreshold;
    this.criticalMultiplier = criticalMultiplier;
    this.affixes = new ArrayList<Affix>(affixes);
    this.toHitBonus = toHitBonus;
    this.toDamageBonus = toDamageBonus;
    this.range = range;
  }
  
}
