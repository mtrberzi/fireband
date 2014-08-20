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
    int threshold = 10 + getEffectiveItemQualityFactor();
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
  
  private boolean rollForCursedArmour(){
    if(guaranteedDecent) return false;
    return rollForGoodArmour();
  }
  
  private boolean rollForDreadfulArmour(){
    if(guaranteedDecent) return false;
    return rollForGreatArmour();
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
    boolean isCursed = false;
    boolean isDreadful = false;
    boolean isArtifact = false;
    
    isGood = rollForGoodArmour();
    if(isGood){
      isGreat = rollForGreatArmour();
      // TODO artifact roll
    }else{
      isCursed = rollForCursedArmour();
      if(isCursed){
        isDreadful = rollForDreadfulArmour();
      }
    }
    
    int armourClassBonus = 0;
    List<Affix> affixes = new ArrayList<Affix>();
    // TODO balance random perturbations and affixes
    if(isGood){
      if(isGreat){
        log.debug("Great armour");
        // Great armour receives an AC bonus of 1d5 + MB(5) + MB(10)
        armourClassBonus = RNG.roll(5)
            + RNG.mBonus(5, getEffectiveItemQualityFactor())
            + RNG.mBonus(10, getEffectiveItemQualityFactor());
      }else{
        log.debug("Good armour");
        // Good armour receives an AC bonus of 1d5 + MB(5)
        armourClassBonus = RNG.roll(5) 
            + RNG.mBonus(5, getEffectiveItemQualityFactor());
      }
    }else if(isCursed){
      if(isDreadful){
        log.debug("Dreadful armour");
        // Dreadful armour receives an AC penalty of 1d5 + MB(5) + MB(10)
        armourClassBonus = (RNG.roll(5)
            + RNG.mBonus(5, getEffectiveItemQualityFactor())
            + RNG.mBonus(10, getEffectiveItemQualityFactor())) * -1;
      }else{
        log.debug("Cursed armour");
        // Cursed armour receives an AC bonus of 1d5 + MB(5)
        armourClassBonus = (RNG.roll(5) 
            + RNG.mBonus(5, getEffectiveItemQualityFactor())) * -1;
      }
    }
    
    List<Affix> compatibleAffixes = Affixes.instance.getAffixesByItemType(ItemType.TYPE_ARMOUR);
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
    
    log.debug("AC bonus: " + armourClassBonus);
    if(!affixes.isEmpty()){
      log.debug("Affixes:");
      for(Affix a : affixes){
        log.debug("* " + a.getName());
      }
    }
    ArmourBuilder aBuilder = new ArmourBuilder(baseArmour);
    aBuilder.setArmourClassBonus(armourClassBonus);
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
