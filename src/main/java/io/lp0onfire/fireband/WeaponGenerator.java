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
  private int itemLevel = 0;
  public void setItemLevel(int il){
    this.itemLevel = il;
  }
  public int getEffectiveItemLevel(){
    int eq = itemLevel;
    // "Guaranteed good" generation boosts this by +2 levels
    if(guaranteedGood){
      eq += 2;
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
    int threshold = 5 + 5*getEffectiveItemLevel();
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
    if(threshold > 45) threshold = 45;
    return threshold;
  }
  
  private boolean rollForGreatWeapon(){
    if(guaranteedGreat) return true;
    int threshold = greatWeaponPercentChance();
    int roll = RNG.roll(100);
    return (roll <= threshold);
  }
  
  private int rollEnhancementLevel(){
    // Non-artifact weapons cannot have more than +10 of equivalent enhancement.
    int maximumLevel = (getEffectiveItemLevel()+4) / 3;
    if(maximumLevel > 10) maximumLevel = 10;
    double dist = RNG.normal(getEffectiveItemLevel()/2, 1.5);
    int level = (int)Math.round(dist);
    if(level < 0) level = 0;
    else if(level > maximumLevel) level = maximumLevel;
    return level;
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
    boolean isArtifact = false;
    
    isGood = rollForGoodWeapon();
    if(isGood){
      isGreat = rollForGreatWeapon();
      // TODO artifact roll
    }
    
    List<Affix> affixes = new ArrayList<Affix>();
    if(isGood){
      if(isGreat){
        log.debug("Great weapon");
      }else{
        log.debug("Good weapon");
      }
    }else{
      log.debug("Standard weapon");
    }
    
    List<Affix> compatibleAffixes = Affixes.instance.getAffixesByItemType(ItemType.TYPE_WEAPON);
    int targetEnhancementLevel;
    if(isGood){
      targetEnhancementLevel = rollEnhancementLevel();
    }else{
      targetEnhancementLevel = 0;
    }
    log.debug("Target enhancement level is " + targetEnhancementLevel);
    
    int totalEnhancementFromAffixes = 0;
    int totalEnhancementFromModifiers = 0; // max +5
    
    // roll for modifiers first
    double dist = RNG.normal((getEffectiveItemLevel()+3)/4, 1.0);
    totalEnhancementFromModifiers = (int)Math.round(dist);
    if(totalEnhancementFromModifiers < 0) 
      totalEnhancementFromModifiers = 0;
    else if(totalEnhancementFromModifiers > targetEnhancementLevel)
      totalEnhancementFromModifiers = targetEnhancementLevel;
    
    log.debug("Enhancement from modifiers is " + totalEnhancementFromModifiers);
    
    if(targetEnhancementLevel - totalEnhancementFromModifiers > 0){
      // Exclude affixes below this level
      Iterator<Affix> it = compatibleAffixes.iterator();
      while(it.hasNext()){
        Affix a = it.next();
        // On great objects or anything we insist be "decent", throw out any
        // affix that lowers the enhancement bonus of the item
        if(isGreat || guaranteedDecent){
          if(a.getEquivalentEnhancementBonus() < 0){
            it.remove();
            continue;
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
          Affix a = RNG.randomEntry(compatibleAffixes);
          // we can only exceed the target enhancement level on great objects;
          // even then the maximum is still +10
          if(isGreat){
            if(totalEnhancementFromAffixes 
                + totalEnhancementFromModifiers
                + a.getEquivalentEnhancementBonus() > 10){
              continue;
            }
          }else{
            if(totalEnhancementFromAffixes 
                + totalEnhancementFromModifiers
                + a.getEquivalentEnhancementBonus() > targetEnhancementLevel){
              continue;
            }
          }
          int affixDifficulty = (int)Math.ceil(Math.pow(2, a.getEquivalentEnhancementBonus() + 1));
          if(RNG.oneIn(affixDifficulty)){
            compatibleAffixes.remove(a);
            affixes.add(a);
            totalEnhancementFromAffixes += a.getEquivalentEnhancementBonus();
          }
          if(compatibleAffixes.isEmpty()) break;
        }
      }
    }
    
    log.debug("Enhancement from affixes is " + totalEnhancementFromAffixes);
    log.debug("Total actual enhancement bonus is "
        + (totalEnhancementFromAffixes+totalEnhancementFromModifiers));
    
    if(!affixes.isEmpty()){
      log.debug("Affixes:");
      for(Affix a : affixes){
        log.debug("* " + a.getName());
      }
    }
    
    // now generate the weapon
    WeaponBuilder wBuilder = new WeaponBuilder(baseWeapon);
    wBuilder.setEnhancementBonus(totalEnhancementFromModifiers);
    for(Affix a : affixes){
      wBuilder.addAffix(a);
    }
    return wBuilder.build();
  }
}
