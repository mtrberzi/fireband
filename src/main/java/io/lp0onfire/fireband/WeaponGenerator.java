package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeaponGenerator {
  // Generation parameters
  private int itemQualityFactor = 0;
  public void setItemQualityFactor(int iq){
    this.itemQualityFactor = iq;
  }
  private Set<WeaponType> disallowedWeaponTypes = new HashSet<WeaponType>();
  private void disallowWeaponType(WeaponType type){
    disallowedWeaponTypes.add(type);
  }
  
  public WeaponGenerator(){}
  
  private int goodWeaponPercentChance(){
    int threshold = 10 + itemQualityFactor;
    if(threshold > 75) threshold = 75;
    return threshold;
  }
  
  private boolean rollForGoodWeapon(){
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
    int threshold = greatWeaponPercentChance();
    int roll = RNG.roll(100);
    return (roll <= threshold);
  }
  
  private boolean rollForCursedWeapon(){
    return rollForGoodWeapon();
  }
  
  private boolean rollForDreadfulWeapon(){
    return rollForGreatWeapon();
  }
  
  public Weapon generateWeapon(){
    List<WeaponType> allowedWeaponTypes = new ArrayList<WeaponType>();
    for(WeaponType type : WeaponType.values()){
      allowedWeaponTypes.add(type);
    }
    for(WeaponType type : disallowedWeaponTypes){
      allowedWeaponTypes.remove(type);
    }
    if(allowedWeaponTypes.isEmpty()){
      throw new IllegalStateException("all weapon types disallowed");
    }
    WeaponType weaponType = allowedWeaponTypes.get(
        RNG.roll(allowedWeaponTypes.size()) - 1
        );
    List<Weapon> baseWeapons = BaseWeapons.instance.getBaseWeaponsByType(weaponType);
    Weapon baseWeapon = baseWeapons.get(
        RNG.roll(baseWeapons.size()) - 1
        );
    
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
    
    boolean isNormal = !(isGood || isGreat || isCursed || isDreadful);
    int toHitBonus = 0;
    int toDamageBonus = 0;
    int damageDice = baseWeapon.getDamageDice();
    if(isGood){
      if(isGreat){
        // Great weapons get +(1d5 + mBonus(5) + mBonus(10)) to hit and to damage
        toHitBonus = RNG.roll(5) + RNG.mBonus(5, itemQualityFactor)
            + RNG.mBonus(10, itemQualityFactor);
        toDamageBonus = RNG.roll(5) + RNG.mBonus(5, itemQualityFactor)
            + RNG.mBonus(10, itemQualityFactor);
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
        // Good weapons get +(1d5 + mBonus(5)) to hit and to damage
        toHitBonus = RNG.roll(5) + RNG.mBonus(5, itemQualityFactor);
        toDamageBonus = RNG.roll(5) + RNG.mBonus(5, itemQualityFactor);
      }
    }else if (isCursed){
      if(isDreadful){
        // Dreadful weapons get -(1d5 + mBonus(5) + mBonus(10)) to hit and to damage
        toHitBonus = (RNG.roll(5) + RNG.mBonus(5, itemQualityFactor)
            + RNG.mBonus(10, itemQualityFactor)) * -1;
        toDamageBonus = (RNG.roll(5) + RNG.mBonus(5, itemQualityFactor)
            + RNG.mBonus(10, itemQualityFactor)) * -1;
      }else{
        // Cursed weapons get -(1d5 + mBonus(5)) to hit and to damage
        toHitBonus = (RNG.roll(5) + RNG.mBonus(5, itemQualityFactor)) * -1;
        toDamageBonus = (RNG.roll(5) + RNG.mBonus(5, itemQualityFactor)) * -1;
      }
    }
    // now generate the weapon
    WeaponBuilder wBuilder = new WeaponBuilder(baseWeapon);
    wBuilder.setToHitBonus(toHitBonus);
    wBuilder.setToDamageBonus(toDamageBonus);
    wBuilder.setDamageDice(damageDice);
    return wBuilder.build();
  }
}
