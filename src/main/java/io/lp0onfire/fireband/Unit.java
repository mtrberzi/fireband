package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.List;

public class Unit {
  // Basic per-unit characteristics.
  private String name;
  public String getName(){return name;}
  private Pronoun pronoun;
  public Pronoun getPronoun(){return pronoun;}
  
  private int baseStrength = 10;
  public int getBaseStrength(){return baseStrength;}
  private int baseDexterity = 10;
  public int getBaseDexterity(){return baseDexterity;}
  private int baseConstitution = 10;
  public int getBaseConstitution(){return baseConstitution;}
  private int baseIntelligence = 10;
  public int getBaseIntelligence(){return baseIntelligence;}
  private int baseWisdom = 10;
  public int getBaseWisdom(){return baseWisdom;}
  private int baseCharisma = 10;
  public int getBaseCharisma(){return baseCharisma;}
  private int baseMovement = 6;
  public int getBaseMovement(){return baseMovement;}
  
  private int level = 1; 
  public int getLevel(){return level;}
  // Experience points are not cumulative; any positive quantity is
  // counted toward the next level, and is reset when a level is gained (or lost).
  private long experience = 0;
  public long getExperience(){return experience;}
  
  // Equipment and inventory.
  private static final int MAX_INVENTORY_SIZE = 25;
  private List<Item> inventory = new ArrayList<Item>();
  
  private void processGetItem(Item i){
    inventory.add(i);
  }
  
  // Silently add an item to inventory (used for setup).
  public void acquire(Item i){
    if(inventory.size() == MAX_INVENTORY_SIZE){
      throw new IllegalStateException("inventory is full");
    }
    processGetItem(i);
  }
  
  private Weapon equippedWeapon = null;
  private Item equippedArmour_amulet = null;
  private Item equippedArmour_body = null;
  private Item equippedArmour_cloak = null;
  private Item equippedArmour_feet = null;
  private Item equippedArmour_hands = null;
  private Item equippedArmour_head = null;
  private Item equippedArmour_ringL = null;
  private Item equippedArmour_ringR = null;
  private Item equippedArmour_shield = null;
  
  private void processEquipWeapon(Weapon w){
    if(w == null) return;
    if(equippedWeapon != null){
      // TODO attempt to remove equipped weapon first
    }
    equippedWeapon = w;
  }
  
  // Silently equip an item onto the unit (for setup).
  public void getEquippedWith(Item i){
    if(i.canBeWielded()){
      Weapon w = (Weapon)i;
      processEquipWeapon(w);
    }else if(i.canBeWorn()){
      // TODO processEquipArmour
    }else{
      throw new IllegalArgumentException("cannot equip '" + i.getDisplayName() + "'");
    }
  }
  
  public Unit(String name, Pronoun pronoun,
      int level, long experience,
      int baseStrength, int baseDexterity, int baseConstitution,
      int baseIntelligence, int baseWisdom, int baseCharisma,
      int baseMovement){
    this.name = name;
    this.pronoun = pronoun;
    this.level = level;
    this.experience = experience;
    this.baseStrength = baseStrength;
    this.baseDexterity = baseDexterity;
    this.baseConstitution = baseConstitution;
    this.baseIntelligence = baseIntelligence;
    this.baseWisdom = baseWisdom;
    this.baseCharisma = baseCharisma;
    this.baseMovement = baseMovement;
  }
}
