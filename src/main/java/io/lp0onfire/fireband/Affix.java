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
  
  private int minimumLevel = 0;
  public int getMinimumLevel(){return minimumLevel;}
  
  private Set<ItemType> allowedItemTypes;
  public Set<ItemType> getAllowedItemTypes(){return allowedItemTypes;}
  private List<Effect> effects;
  public List<Effect> getEffects(){return effects;}
  
  public Affix(String name, boolean prefix, int minimumLevel,
      Set<ItemType> allowedItemTypes, List<Effect> effects){
    this.name = name;
    this.prefix = prefix;
    this.minimumLevel = minimumLevel;
    this.allowedItemTypes = new HashSet<ItemType>(allowedItemTypes);
    this.effects = new ArrayList<Effect>(effects);
  }
}
