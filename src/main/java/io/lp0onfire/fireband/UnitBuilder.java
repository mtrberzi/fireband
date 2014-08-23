package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.List;

public class UnitBuilder {

  private String name = "Stranger";
  public void setName(String name){this.name = name;}
  private Pronoun pronoun = Pronoun.PRONOUN_NEUTRAL;
  public void setPronoun(Pronoun pronoun){this.pronoun = pronoun;}
  private Race race; // TODO reasonable default
  public void setRace(Race r){this.race = r;}
  private UnitClass unitClass; // TODO reasonable default
  public void setUnitClass(UnitClass c){this.unitClass = c;}
  
  private int baseStrength = 10;
  public void setStrength(int a){this.baseStrength = a;}
  private int baseDexterity = 10;
  public void setDexterity(int a){this.baseDexterity = a;}
  private int baseConstitution = 10;
  public void setConstitution(int a){this.baseConstitution = a;}
  private int baseIntelligence = 10;
  public void setIntelligence(int a){this.baseIntelligence = a;}
  private int baseWisdom = 10;
  public void setWisdom(int a){this.baseWisdom = a;}
  private int baseCharisma = 10;
  public void setCharisma(int a){this.baseCharisma = a;}
  
  private int level = 1; 
  public void setLevel(int a){this.level = a;}
  private long experience = 0;
  public void setExperience(int a){this.experience = a;}
  
  private List<Item> inventory = new ArrayList<Item>();
  public void addInventoryItem(Item i){this.inventory.add(i);}
  private Item equippedWeapon = null;
  private Item equippedArmour_amulet = null;
  private Item equippedArmour_body = null;
  private Item equippedArmour_cloak = null;
  private Item equippedArmour_feet = null;
  private Item equippedArmour_hands = null;
  private Item equippedArmour_head = null;
  private Item equippedArmour_ringL = null;
  private Item equippedArmour_ringR = null;
  private Item equippedArmour_shield = null;
  public void equip(Item i){
    if(!i.canBeWielded() && !i.canBeWorn()){
      throw new IllegalArgumentException("cannot equip '" + i.getDisplayName() + "'");
    }
    switch(i.getType()) {
    case TYPE_ARMOUR:
      Armour a = (Armour)i;
      switch(a.getArmourType()){
      case ARMOUR_AMULET:
        equippedArmour_amulet = i;
        break;
      case ARMOUR_BODY:
        equippedArmour_body = i;
        break;
      case ARMOUR_CLOAK:
        equippedArmour_cloak = i;
        break;
      case ARMOUR_FEET:
        equippedArmour_feet = i;
        break;
      case ARMOUR_HANDS:
        equippedArmour_hands = i;
        break;
      case ARMOUR_HEAD:
        equippedArmour_head = i;
        break;
      case ARMOUR_RING:
        if(equippedArmour_ringR == null){
          equippedArmour_ringR = i;
        }else{
          equippedArmour_ringL = i;
        }
        break;
      case ARMOUR_SHIELD:
        equippedArmour_shield = i;
        break;
      }
      break;
    case TYPE_WEAPON:
      equippedWeapon = i;
      break;
    default:
      throw new IllegalArgumentException("cannot equip '" + i.getDisplayName() + "'"); 
    }
  }
  
  public UnitBuilder(){
    
  }
  
  public Unit build(){
    Unit u = new Unit(name, pronoun, race, unitClass,
        level, experience,
        baseStrength, baseDexterity, baseConstitution,
        baseIntelligence, baseWisdom, baseCharisma);
    // go through inventory
    for(Item i : inventory){
      u.acquire(i);
    }
    // go through equipment
    if(equippedWeapon != null) u.getEquippedWith(equippedWeapon);
    if(equippedArmour_amulet != null) u.getEquippedWith(equippedArmour_amulet);
    if(equippedArmour_body != null) u.getEquippedWith(equippedArmour_body);
    if(equippedArmour_cloak != null) u.getEquippedWith(equippedArmour_cloak);
    if(equippedArmour_feet != null) u.getEquippedWith(equippedArmour_feet);
    if(equippedArmour_hands != null) u.getEquippedWith(equippedArmour_hands);
    if(equippedArmour_head != null) u.getEquippedWith(equippedArmour_head);
    if(equippedArmour_ringR != null) u.getEquippedWith(equippedArmour_ringR);
    if(equippedArmour_ringL != null) u.getEquippedWith(equippedArmour_ringL);
    if(equippedArmour_shield != null) u.getEquippedWith(equippedArmour_shield);
    return u;
  }
  
}
