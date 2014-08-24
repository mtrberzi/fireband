package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ArmourGenerator {
  private static Logger log = LogManager.getLogger("ArmourGenerator");
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
  
  private boolean armourTypeRestrictions = false;
  private Set<ArmourType> allowedArmourTypes = new HashSet<ArmourType>();
  public void addAllowedArmourType(ArmourType type){
    armourTypeRestrictions = true;
    allowedArmourTypes.add(type);
  }
  
  public void allowOnlyBodyArmour(){
    armourTypeRestrictions = true;
    // TODO this list should not be hard-coded here
    allowedArmourTypes.clear();
    allowedArmourTypes.add(ArmourType.ARMOUR_BODY);
    allowedArmourTypes.add(ArmourType.ARMOUR_CLOAK);
    allowedArmourTypes.add(ArmourType.ARMOUR_HEAD);
    allowedArmourTypes.add(ArmourType.ARMOUR_HANDS);
    allowedArmourTypes.add(ArmourType.ARMOUR_FEET);
    allowedArmourTypes.add(ArmourType.ARMOUR_SHIELD);
  }
  
  public void disallowBodyArmour(){
    armourTypeRestrictions = true;
    // TODO this list should not be hard-coded here
    allowedArmourTypes.clear();
    allowedArmourTypes.add(ArmourType.ARMOUR_AMULET);
    allowedArmourTypes.add(ArmourType.ARMOUR_RING);
  }
  
  private boolean noHeavyArmour = false;
  public void disallowHeavyArmour(){
    noHeavyArmour = true;
  }
  private boolean onlyHeavyArmour = false;
  public void allowOnlyHeavyArmour(){
    onlyHeavyArmour = true;
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
  
  public ArmourGenerator(){}
  
  private int goodArmourPercentChance(){
    int threshold = 5 + 5*getEffectiveItemLevel();
    if(threshold > 75) threshold = 75;
    return threshold;
  }
  
  private boolean rollForGoodArmour(){
    if(guaranteedGood) return true;
    int threshold = goodArmourPercentChance();
    int roll = RNG.roll(100);
    return (roll <= threshold);
  }
  
  private int greatArmourPercentChance(){
    int threshold = goodArmourPercentChance() / 2;
    if(threshold > 20) threshold = 20;
    return threshold;
  }
  
  private boolean rollForGreatArmour(){
    if(guaranteedGreat) return true;
    int threshold = greatArmourPercentChance();
    int roll = RNG.roll(100);
    return (roll <= threshold);
  }
  
  private int rollEnhancementLevel(){
    // Non-artifact armour cannot have more than +10 of equivalent enhancement.
    int maximumLevel = (getEffectiveItemLevel()+4) / 3;
    if(maximumLevel > 10) maximumLevel = 10;
    double dist = RNG.normal(getEffectiveItemLevel()/2, 1.5);
    int level = (int)Math.round(dist);
    if(level < 0) level = 0;
    else if(level > maximumLevel) level = maximumLevel;
    return level;
  }
  
  private Armour generateBodyArmour(ArmourType type){
    List<Armour> baseArmourList = BaseArmour.instance.getBaseArmourByType(type);
    if(baseArmourList.isEmpty()){
      throw new IllegalStateException("no armour of type '"
          + type.toString() + "' defined");
    }
    if(onlyHeavyArmour && noHeavyArmour){
      throw new IllegalStateException("both heavy and non-heavy armour disallowed");
    }else if(onlyHeavyArmour){
      Iterator<Armour> it = baseArmourList.iterator();
      while(it.hasNext()){
        Armour a = it.next();
        if(!a.isHeavyArmour()){
          it.remove();
        }
      }
    }else if(noHeavyArmour){
      Iterator<Armour> it = baseArmourList.iterator();
      while(it.hasNext()){
        Armour a = it.next();
        if(a.isHeavyArmour()){
          it.remove();
        }
      }
    }
    if(baseArmourList.isEmpty()){
      throw new IllegalStateException("no possible base armour allowed");
    }
    Armour baseArmour = RNG.randomEntry(baseArmourList);
    
    boolean isGood = false;
    boolean isGreat = false;
    boolean isArtifact = false;
    
    isGood = rollForGoodArmour();
    if(isGood){
      isGreat = rollForGreatArmour();
      // TODO artifact roll
    }
    
    List<Affix> affixes = new ArrayList<Affix>();
    // TODO balance random perturbations and affixes
    if(isGood){
      if(isGreat){
        log.debug("Great armour");
      }else{
        log.debug("Good armour");
      }
    }else{
      log.debug("Standard armour");
    }
    
    List<Affix> compatibleAffixes = Affixes.instance.getAffixesByItemType(ItemType.TYPE_ARMOUR);
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
    
    log.debug("AC bonus: " + totalEnhancementFromModifiers);
    if(!affixes.isEmpty()){
      log.debug("Affixes:");
      for(Affix a : affixes){
        log.debug("* " + a.getName());
      }
    }
    ArmourBuilder aBuilder = new ArmourBuilder(baseArmour);
    aBuilder.setArmourClassBonus(totalEnhancementFromModifiers);
    for(Affix a : affixes){
      aBuilder.addAffix(a);
    }
    return aBuilder.build();
  }
  
  public Armour generateArmour(){
    List<ArmourType> armourTypes = new ArrayList<ArmourType>();
    if(armourTypeRestrictions){
      armourTypes = new ArrayList<ArmourType>(allowedArmourTypes);
    }else{
      for(ArmourType type : ArmourType.values()){
        armourTypes.add(type);
      }
    }
    if(armourTypes.isEmpty()){
      throw new IllegalStateException("all armour types disallowed");
    }
    ArmourType type = RNG.randomEntry(armourTypes);
    log.debug("Generating " + type.toString());
    if(type == ArmourType.ARMOUR_AMULET){
      // TODO
      throw new UnsupportedOperationException("amulets not supported");
    }else if(type == ArmourType.ARMOUR_RING){
      // TODO
      throw new UnsupportedOperationException("rings not supported");
    }else{
      // regular body armour
      return generateBodyArmour(type);
    }
  }
  
}
