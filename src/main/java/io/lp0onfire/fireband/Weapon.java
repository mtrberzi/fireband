package io.lp0onfire.fireband;

import java.util.List;

public class Weapon extends Item {
  
  private WeaponType weaponType;
  public WeaponType getWeaponType(){return weaponType;}
  
  private String description = "Use it to attack your foes.";
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
    // describe bonus/penalty to hit
    if(getEffectiveToHitBonus() > 0){
      sb.append("It grants a +").append(getEffectiveToHitBonus())
        .append(" bonus to hit opponents.").append(br);
    }else if(getEffectiveToHitBonus() < 0){
      sb.append("It grants a -").append(getEffectiveToHitBonus()*-1)
        .append(" penalty to hit opponents.").append(br);
    }
    // describe damage
    sb.append("On a successful hit, this weapon deals (");
    // damage dice
    sb.append(getEffectiveDamageDice()).append("d")
      .append(getDamageDieSize());
    if(getToDamageBonus() > 0){
      sb.append(" + ").append(getToDamageBonus());
    }else if(getToDamageBonus() < 0){
      sb.append(" - ").append(getToDamageBonus() * -1);
    }
    if(getEffectiveDamageFactor() == 1.0){
      sb.append(") damage");
    }else{
      sb.append(") * ").append(String.format("%.2f damage", getEffectiveDamageFactor()));
    }
    sb.append(".").append(br);
    sb.append(String.format("This means an average hit deals about %.1f damage.", getExpectedDamage()))
      .append(br);
    // describe range
    if(getEffectiveRange() > 1){
      sb.append("It is a ranged weapon and can be used to attack from up to ")
        .append(getEffectiveRange()).append(" squares away.").append(br);
    }
    return sb.toString();
  }
  
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
  
  public int getEffectiveDamageDice(){
    int base = getDamageDice();
    for(Affix a : getAffixes()){
      for(Effect e : a.getEffects()){
        base += e.getWeaponExtraDamageDice();
      }
    }
    if(base < 1) base = 1;
    return base;
  }
  
  public int getEffectiveToHitBonus(){
    int base = getToHitBonus();
    for(Affix a : getAffixes()){
      for(Effect e : a.getEffects()){
        base += e.getWeaponToHitBonus();
      }
    }
    return base;
  }
  public double getEffectiveDamageFactor(){
    double base = 1.0;
    for(Affix a : getAffixes()){
      for(Effect e : a.getEffects()){
        base += e.getWeaponDamageFactor();
      }
    }
    if(base < 0.0) base = 0.0;
    return base;
  }
  
  public int getEffectiveRange(){
    int base = getRange();
    // TODO affix range bonuses/penalties
    if(base < 1) base = 1;
    return base;
  }
  
  public double getExpectedDamage(){
    // TODO extra damage from affix brands, etc.
    double baseRoll = RNG.expectedValue(getEffectiveDamageDice(), getDamageDieSize());
    baseRoll += getToDamageBonus();
    baseRoll *= getEffectiveDamageFactor();
    if(baseRoll < 0.0) return 0.0;
    return baseRoll;
  }
  
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
