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
  private long experience = 0;
  public long getExperience(){return experience;}
  
  // Equipment and inventory.
  private static final int MAX_INVENTORY_SIZE = 25;
  private List<Item> inventory = new ArrayList<Item>();
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
}
