package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class WeaponGenerator {
  private static Logger log = LogManager.getLogger("WeaponGenerator");
  // Generation parameters
  private int itemQualityFactor = 0;
  public void setItemQualityFactor(int iq){
    this.itemQualityFactor = iq;
  }
  public int getEffectiveItemQualityFactor(){
    int eq = itemQualityFactor;
    // "Guaranteed good" generation boosts this by +10
    if(guaranteedGood){
      eq += 10;
    }
    return eq;
  }
  
  private Set<WeaponType> disallowedWeaponTypes = new HashSet<WeaponType>();
  public void disallowWeaponType(WeaponType type){
    disallowedWeaponTypes.add(type);
  }
  
  private boolean noSimpleWeapons = false;
  public void disallowSimpleWeapons(){
    noSimpleWeapons = true;
  }
  private boolean onlySimpleWeapons = false;
  public void allowOnlySimpleWeapons(){
    onlySimpleWeapons = true;
  }
  
  private boolean guaranteedDecent = false;
  // Guarantees that the item is average or better.
  public void guaranteeDecentObject(){
    this.guaranteedDecent = true;
  }
  
  private boolean guaranteedGood = false;
  public void guaranteeGoodObject(){
    this.guaranteedGood = true;
  }
  
  private boolean guaranteedGreat = false;
  public void guaranteeGreatObject(){
    this.guaranteedGreat = true;
    // great implies good
    this.guaranteedGood = true;
  }
  
  public WeaponGenerator(){}
  
  private int goodWeaponPercentChance(){
    int threshold = 10 + getEffectiveItemQualityFactor();
    if(threshold > 75) threshold = 75;
    return threshold;
  }
  
  private boolean rollForGoodWeapon(){
    if(guaranteedGood) return true;
    int threshold = goodWeaponPercentChance();
    int roll = RNG.roll(100);
    return (roll <= threshold);
  }
  
  private int greatWeaponPercentChance(){
    int threshold = goodWeaponPercentChance() / 2;
    if(threshold > 20) threshold = 20;
    return threshold;
  }
  
  private boolean rollForGreatWeapon(){
    if(guaranteedGreat) return true;
    int threshold = greatWeaponPercentChance();
    int roll = RNG.roll(100);
    return (roll <= threshold);
  }
  
  private boolean rollForCursedWeapon(){
    if(guaranteedDecent) return false;
    return rollForGoodWeapon();
  }
  
  private boolean rollForDreadfulWeapon(){
    if(guaranteedDecent) return false;
    return rollForGreatWeapon();
  }
  
  public Weapon generateWeapon(){
    List<WeaponType> allowedWeaponTypes = new ArrayList<WeaponType>();
    for(WeaponType type : WeaponType.values()){
      allowedWeaponTypes.add(type);
    }
    for(WeaponType type : disallowedWeaponTypes){
      log.debug("Disallowing " + type.toString());
      allowedWeaponTypes.remove(type);
    }
    if(allowedWeaponTypes.isEmpty()){
      throw new IllegalStateException("all weapon types disallowed");
    }
    WeaponType weaponType = allowedWeaponTypes.get(
        RNG.roll(allowedWeaponTypes.size()) - 1
        );
    log.debug("Generating " + weaponType.toString());
    List<Weapon> baseWeapons = BaseWeapons.instance.getBaseWeaponsByType(weaponType);
    if(baseWeapons.isEmpty()){
      throw new IllegalStateException("no weapons of type '"
          + weaponType.toString() + "' defined");
    }
    // exclude by simplicity
    List<Weapon> wrongSimpleWeapons = new ArrayList<Weapon>();
    if(noSimpleWeapons && onlySimpleWeapons){
      throw new IllegalStateException("both simple and non-simple weapons disallowed");
    }else if(noSimpleWeapons){
      log.debug("Excluding simple weapons");
      wrongSimpleWeapons = BaseWeapons.instance.getBaseWeaponsBySimple(true);
    }else if(onlySimpleWeapons){
      log.debug("Excluding non-simple weapons");
      wrongSimpleWeapons = BaseWeapons.instance.getBaseWeaponsBySimple(false);
    }
    for(Weapon w : wrongSimpleWeapons){
      baseWeapons.remove(w);
    }
    // final check to see if there are any weapons left
    if(baseWeapons.isEmpty()){
      throw new IllegalStateException("no possible base weapons allowed");
    }
    Weapon baseWeapon = baseWeapons.get(
        RNG.roll(baseWeapons.size()) - 1
        );
    log.debug("Base weapon is " + baseWeapon.getDisplayName());
    
    boolean isGood = false;
    boolean isGreat = false;
    boolean isCursed = false;
    boolean isDreadful = false;
    boolean isArtifact = false;
    
    isGood = rollForGoodWeapon();
    if(isGood){
      isGreat = rollForGreatWeapon();
      // TODO artifact roll
    }else{
      isCursed = rollForCursedWeapon();
      if(isCursed){
        isDreadful = rollForDreadfulWeapon();
      }
    }
    
    int toHitBonus = 0;
    int toDamageBonus = 0;
    int damageDice = baseWeapon.getDamageDice();
    if(isGood){
      if(isGreat){
        log.debug("Great weapon");
        // Great weapons get +(1d5 + mBonus(5) + mBonus(10)) to hit and to damage
        toHitBonus = RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor())
            + RNG.mBonus(10, getEffectiveItemQualityFactor());
        toDamageBonus = RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor())
            + RNG.mBonus(10, getEffectiveItemQualityFactor());
        // Try boosting the damage dice (1 in 10*X*Y for an XdY weapon)
        // This can happen until the roll fails or until X=9
        while(damageDice < 9){
          if(RNG.oneIn(10 * damageDice * baseWeapon.getDamageDieSize())){
            ++damageDice;
          }else{
            break;
          }
        }
        // TODO affixes
      }else{
        log.debug("Good weapon");
        // Good weapons get +(1d5 + mBonus(5)) to hit and to damage
        toHitBonus = RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor());
        toDamageBonus = RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor());
      }
    }else if (isCursed){
      if(isDreadful){
        log.debug("Dreadful weapon");
        // Dreadful weapons get -(1d5 + mBonus(5) + mBonus(10)) to hit and to damage
        toHitBonus = (RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor())
            + RNG.mBonus(10, getEffectiveItemQualityFactor())) * -1;
        toDamageBonus = (RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor())
            + RNG.mBonus(10, getEffectiveItemQualityFactor())) * -1;
      }else{
        log.debug("Cursed weapon");
        // Cursed weapons get -(1d5 + mBonus(5)) to hit and to damage
        toHitBonus = (RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor())) * -1;
        toDamageBonus = (RNG.roll(5) + RNG.mBonus(5, getEffectiveItemQualityFactor())) * -1;
      }
    }
    log.debug("To-hit bonus: " + toHitBonus);
    log.debug("To-damage bonus: " + toDamageBonus);
    log.debug("Damage dice: " + damageDice);
    // now generate the weapon
    WeaponBuilder wBuilder = new WeaponBuilder(baseWeapon);
    wBuilder.setToHitBonus(toHitBonus);
    wBuilder.setToDamageBonus(toDamageBonus);
    wBuilder.setDamageDice(damageDice);
    return wBuilder.build();
  }
}
