package io.lp0onfire.fireband;

public class Race {
  private final String name;
  public String getName(){return name;}
  
  private final int strengthModifier;
  public int getStrengthModifier(){return strengthModifier;}
  private final int dexterityModifier;
  public int getDexterityModifier(){return dexterityModifier;}
  private final int constitutionModifier;
  public int getConstitutionModifier(){return constitutionModifier;}
  private final int intelligenceModifier;
  public int getIntelligenceModifier(){return intelligenceModifier;}
  private final int wisdomModifier;
  public int getWisdomModifier(){return wisdomModifier;}
  private final int charismaModifier;
  public int getCharismaModifier(){return charismaModifier;}
  
  // TODO size (?)
  
  // TODO creature type and subtype(s)
  
  private final int baseMovement;
  public int getBaseMovement(){return baseMovement;}
  
  // TODO languages
  
  // TODO senses
  
  // TODO traits
  
  // TODO skill bonuses (?)
  
  // TODO bonus feats
  
  // TODO spell-like abilities (?)
  
  public Race(String name,
      int strMod, int dexMod, int conMod, int intMod, int wisMod, int chaMod,
      int baseMovement){
    this.name = name;
    this.strengthModifier = strMod;
    this.dexterityModifier = dexMod;
    this.constitutionModifier = conMod;
    this.intelligenceModifier = intMod;
    this.wisdomModifier = wisMod;
    this.charismaModifier = chaMod;
    this.baseMovement = baseMovement;
  }
  
}
