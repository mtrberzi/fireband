package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.List;

public class WeaponBuilder {
  private String name = "SORD.....";
  private int baseWeight = 0;
  private int baseValue = 0;
  private WeaponType type = WeaponType.WEAPON_EDGED;
  private int damageDice = 0;
  public void setDamageDice(int n){
    damageDice = n;
  }
  private int damageDieSize = 0;
  public void setDamageDieSize(int n){
    damageDieSize = n;
  }
  private DamageType damageType = DamageType.DAMAGE_SLASHING;
  private int criticalThreshold = 20;
  private int criticalMultiplier = 1;
  private List<Affix> affixes = new ArrayList<Affix>();
  private int toHitBonus = 0;
  public void setToHitBonus(int n){
    toHitBonus = n;
  }
  private int toDamageBonus = 0;
  public void setToDamageBonus(int n){
    toDamageBonus = n;
  }
  private int range = 1;
  
  public WeaponBuilder(Weapon base){
    // copy everything from a template base weapon
    name = base.getName();
    baseWeight = base.getBaseWeight();
    baseValue = base.getBaseValue();
    type = base.getWeaponType();
    damageDice = base.getDamageDice();
    damageDieSize = base.getDamageDieSize();
    damageType = base.getDamageType();
    criticalThreshold = base.getCriticalThreshold();
    criticalMultiplier = base.getCriticalMultiplier();
    affixes = new ArrayList<Affix>(base.getAffixes());
    toHitBonus = base.getToHitBonus();
    toDamageBonus = base.getToDamageBonus();
    range = base.getRange();
  }
  
  public Weapon build(){
    return new Weapon(name, baseWeight, baseValue,
        type, damageDice, damageDieSize,
        damageType, criticalThreshold, criticalMultiplier,
        affixes, toHitBonus, toDamageBonus, range);
  }
}
