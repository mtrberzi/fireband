package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
  
  private int rollAffixLevel(){
    // Similar to the "ego generation level" in Angband.
    // Most of the time this comes out to itemQualityFactor,
    // but 1 time in 20 we "supercharge" and
    // use itemQualityFactor * (128 / 1d128) + 1 instead
    if(RNG.oneIn(20)){
      log.debug("Supercharged affix level");
      double superchargeFactor = 128.0 / (double)RNG.roll(128);
      return (int)Math.ceil(itemQualityFactor * superchargeFactor) + 1;
    }else{
      return itemQualityFactor;
    }
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
    WeaponType weaponType = RNG.randomEntry(allowedWeaponTypes);
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
    Weapon baseWeapon = RNG.randomEntry(baseWeapons);
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
    List<Affix> affixes = new ArrayList<Affix>();
    // TODO balance random perturbations and affixes
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
    
    List<Affix> compatibleAffixes = Affixes.instance.getAffixesByItemType(ItemType.TYPE_WEAPON);
    int affixLevel = rollAffixLevel();
    log.debug("Using affix level " + affixLevel);
    // Exclude affixes below this level
    Iterator<Affix> it = compatibleAffixes.iterator();
    while(it.hasNext()){
      Affix a = it.next();
      if(a.getMinimumLevel() < affixLevel){
        // Keep an out-of-depth affix one time in (out-of-depth factor + 1)
        if(!RNG.oneIn((affixLevel - a.getMinimumLevel()) + 1)){
          it.remove();
        }
      }
      // Additionally, on good or great objects, throw out any
      // affix that has any component effect that lowers the value of the item
      if(isGood || isGreat){
        for(Effect e : a.getEffects()){
          if(e.getItemValueFactor() < 0.0){
            it.remove();
            break;
          }
        }
      }
    }
    // if there is anything left...
    if(!compatibleAffixes.isEmpty()){
      // Great items get a few more attempts to receive an affix
      int affixAttempts = 10;
      if(isGreat){
        affixAttempts += 10;
      }
      for(int i = 0; i < affixAttempts; ++i){
        int affixDifficulty = (int)Math.ceil(Math.pow(10, affixes.size() + 1));
        if(RNG.oneIn(affixDifficulty)){
          Affix a = RNG.randomEntry(compatibleAffixes);
          compatibleAffixes.remove(a);
          affixes.add(a);
        }
        if(compatibleAffixes.isEmpty()) break;
      }
    }
    
    log.debug("To-hit bonus: " + toHitBonus);
    log.debug("To-damage bonus: " + toDamageBonus);
    log.debug("Damage dice: " + damageDice);
    if(!affixes.isEmpty()){
      log.debug("Affixes:");
      for(Affix a : affixes){
        log.debug("* " + a.getName());
      }
    }
    
    // now generate the weapon
    WeaponBuilder wBuilder = new WeaponBuilder(baseWeapon);
    wBuilder.setToHitBonus(toHitBonus);
    wBuilder.setToDamageBonus(toDamageBonus);
    wBuilder.setDamageDice(damageDice);
    for(Affix a : affixes){
      wBuilder.addAffix(a);
    }
    return wBuilder.build();
  }
}
