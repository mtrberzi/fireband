package io.lp0onfire.fireband;

public class UnitClass {
  private final String name;
  public String getName(){return name;}
  
  private final BaseAttackBonusType baseAttackBonus;
  public int getNumberOfAttacks(int level){
    switch(baseAttackBonus){
    case BAB_FAST: 
      return level/5;
    case BAB_NORMAL: // TODO this only works for level <= 20
      if(level >= 15) return 3;
      else if(level >= 8) return 2;
      else return 1;
    case BAB_SLOW:
      // TODO this only works for level <= 20
      if(level >= 12) return 2;
      else return 1;
    default:
      return 0;  
    }
  }
  public int getBaseAttackBonus(int level, int attack){
    if(attack > getNumberOfAttacks(level)) return 0;
    if(attack > 1) return getBaseAttackBonus(level, attack-1) - 5;
    switch(baseAttackBonus){
    case BAB_FAST:
      return level;
    case BAB_NORMAL:
      // The progression is 0 1 2 3, 3 4 5 6, 6 7 8 9, ...
      int base = 3 * ((level-1)/4);
      int offset = (level-1)%4;
      return base + offset;
    case BAB_SLOW:
      // The progression is 0 1, 1 2, 2 3, 3 4, ...
      return level/2;
    default:
      return 0;
    }
  }
  
  private final SavingThrowType fortitudeSave;
  private final SavingThrowType reflexSave;
  private final SavingThrowType willSave;
  private int getSave(int level, SavingThrowType type){
    switch(type){
    case SAVE_STRONG:
      // The progression is 2 3, 3 4, 4 5, ...
      int base = 2 + (level-1)/2;
      int offset = (level-1)%2;
      return base + offset;
    case SAVE_WEAK:
      // The progression is 0 0, 1 1 1, 2 2 2, ...
      return level/3;
    default: return 0;
    }
  }
  public int getFortitudeSave(int level){return getSave(level, fortitudeSave);}
  public int getReflexSave(int level){return getSave(level, reflexSave);}
  public int getWillSave(int level){return getSave(level, willSave);}
  
  private final int hitDieSize;
  public int getHitDieSize(){return hitDieSize;}
  
  // TODO weapon proficiency (feats/innate)
  
  public UnitClass(String name, BaseAttackBonusType baseAttackBonus,
      SavingThrowType fortitudeSave,
      SavingThrowType reflexSave,
      SavingThrowType willSave,
      int hitDieSize){
    this.name = name;
    this.baseAttackBonus = baseAttackBonus;
    this.fortitudeSave = fortitudeSave;
    this.reflexSave = reflexSave;
    this.willSave = willSave;
    this.hitDieSize = hitDieSize;
  }
}
