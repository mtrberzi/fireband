package io.lp0onfire.fireband;

public abstract class Item {
  // Global item properties
  private String name;
  public String getName(){return name;}
  
  private ItemType type;
  public ItemType getType(){return type;}
  
  // The unit of weight is the tenth-pound.
  private int baseWeight = 0;
  public int getBaseWeight(){return baseWeight;}
  
  private int baseValue = 0;
  public int getBaseValue(){return baseValue;}
  
  // Generic item predicates.
  public boolean canBeWielded() {
    if(type == ItemType.TYPE_WEAPON) {
      return true;
    } else {
      return false;
    }
  }
  public boolean canBeWorn(){
    if(type == ItemType.TYPE_ARMOUR_AMULET
        || type == ItemType.TYPE_ARMOUR_BODY
        || type == ItemType.TYPE_ARMOUR_CLOAK
        || type == ItemType.TYPE_ARMOUR_FEET
        || type == ItemType.TYPE_ARMOUR_HANDS
        || type == ItemType.TYPE_ARMOUR_HEAD
        || type == ItemType.TYPE_ARMOUR_RING
        || type == ItemType.TYPE_ARMOUR_SHIELD) {
      return true;
    } else {
      return false;
    }
  }
  public boolean canBeUsed(){
    if(type == ItemType.TYPE_POTION
        || type == ItemType.TYPE_ROD
        || type == ItemType.TYPE_SCROLL
        || type == ItemType.TYPE_STAFF
        || type == ItemType.TYPE_WAND) {
      return true;
    } else {
      return false;
    }
  }
  
  public Item(ItemType type, String name, int baseWeight, int baseValue){
    this.type = type;
    this.name = name;
    this.baseWeight = baseWeight;
    this.baseValue = baseValue;
  }
}
