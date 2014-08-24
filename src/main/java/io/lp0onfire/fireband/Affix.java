package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Affix {
  private String name;
  public String getName(){return name;}
  
  // true: prefix (appear before item name)
  // false: suffix (appear after item name)
  private boolean prefix = true;
  public boolean isPrefix(){return prefix;}
  
  // Get the enhancement bonus that is considered
  // equivalent in value to this affix for the purposes
  // of determining item value and maximum enchantment level.
  private int equivalentEnhancementBonus = 0;
  public int getEquivalentEnhancementBonus(){return equivalentEnhancementBonus;}
  
  private Set<ItemType> allowedItemTypes;
  public Set<ItemType> getAllowedItemTypes(){return allowedItemTypes;}
  // TODO affix restrictions by sub-item types (e.g. weapon type, armour type)
  private List<Effect> effects;
  public List<Effect> getEffects(){return effects;}
  
  public Affix(String name, boolean prefix, int equivalentEnhancementBonus,
      Set<ItemType> allowedItemTypes, List<Effect> effects){
    this.name = name;
    this.prefix = prefix;
    this.equivalentEnhancementBonus = equivalentEnhancementBonus;
    this.allowedItemTypes = new HashSet<ItemType>(allowedItemTypes);
    this.effects = new ArrayList<Effect>(effects);
  }
}
