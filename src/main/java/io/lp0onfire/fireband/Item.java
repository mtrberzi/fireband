package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.List;

public abstract class Item {
  // Global item properties
  private String name;
  public String getName(){return name;}
  
  private ItemType type;
  public ItemType getType(){return type;}
  
  public abstract String getDescription();
  public abstract String getFullDescription();
  
  // The unit of weight is the tenth-pound.
  private int baseWeight = 0;
  public int getBaseWeight(){return baseWeight;}
  public int getEffectiveWeight(){
    int base = getBaseWeight();
    double fin = base;
    for(Affix a : getAffixes()){
      for(Effect e : a.getEffects()){
        fin += base * e.getItemWeightFactor();
      }
    }
    if(fin < 0.0) fin = 0.0;
    return (int)Math.floor(fin);
  }
  
  private int baseValue = 0;
  public int getBaseValue(){return baseValue;}
  public int getEffectiveValue(){
    int base = getBaseValue();
    // TODO effective enhancement bonus from affixes/magic
    return base;
  }
  
  private List<Affix> affixes;
  public List<Affix> getAffixes(){return affixes;}
  
  // Generic item predicates.
  public boolean canBeWielded() {
    return (type == ItemType.TYPE_WEAPON);
  }
  public boolean canBeWorn(){
    return (type == ItemType.TYPE_ARMOUR);
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
  
  public static String getDisplayName(Item item, int count){
    StringBuilder sb = new StringBuilder();
    String formatString = item.getName();
    // The format string will look like "%p name%s"
    // We substitute %p for the particle (a/an/the/count)
    // followed by zero or more prefixes;
    // each prefix is preceded by a space.
    // %s is substituted for the appropriate singular/plural ending.
    // %u is substituted for zero or more suffixes;
    // each suffix is preceded by one of the following:
    // * " of " if it is the first suffix
    // * ", " if it is not the first or last suffix
    // * " and " if it is the last suffix
    // Otherwise the format string is echoed directly
    for(int i = 0; i < formatString.length(); ++i){
      char c = formatString.charAt(i);
      if(c == '%'){
        // format escape
        char escapeCode = formatString.charAt(i+1);
        i += 1;
        switch(escapeCode){
        case 'p': // prefix
        {
          // TODO artifact prefix "the"
          if(count == 1){
            sb.append("%a"); // we need to do a second pass for the particle
          }else{
            sb.append(count);
          }
          for(Affix a : item.getAffixes()){
            if(a.isPrefix()){
              sb.append(" ").append(a.getName());
            }
          }
        } break;
        case 's': // plural form
        {
          // TODO irregular plural forms
          if(count == 1){
            // singular ending
          }else{
            // plural ending
            sb.append("s");
          }
        } break;
        case 'u': // suffixes
        {
          int suffixCount = 0;
          for(Affix a : item.getAffixes()){
            if(!a.isPrefix()){
              ++suffixCount;
            }
          }
          boolean firstSuffix;
          boolean lastSuffix;
          int suffixIndex = 0;
          for(Affix a : item.getAffixes()){
            if(a.isPrefix()) continue;
            firstSuffix = (suffixIndex == 0);
            lastSuffix = (suffixIndex == suffixCount - 1);
            ++suffixIndex;
            if(firstSuffix){
              sb.append(" of ").append(a.getName());
            }else if(!firstSuffix && !lastSuffix){
              sb.append(", ").append(a.getName());
            }else{ // last suffix but not first
              sb.append(" and ").append(a.getName());
            }
          }
        } break;
        default:
          throw new IllegalArgumentException(
              "Unknown escape code '%" + escapeCode + "' in format string '"
              + formatString + "'");
        }
      }else{
        sb.append(c);
      }
    }
    String firstPassResult = sb.toString();
    sb = new StringBuilder();
    // now do a second pass looking for %a
    for(int i = 0; i < firstPassResult.length(); ++i){
      char c = firstPassResult.charAt(i);
      if(c == '%'){
        char escapeCode = firstPassResult.charAt(i+1);
        i += 1;
        if(escapeCode == 'a'){
          // this is not how English articles work but it is close:
          // look ahead for the first non-space character we see,
          // and if it is a vowel, the particle is "an";
          // otherwise it is "a".
          String lookahead = firstPassResult.substring(i + 1).trim();
          char v = Character.toLowerCase(lookahead.charAt(0));
          if(v == 'a' || v == 'e' || v == 'i' || v == 'o' || v == 'u'){
            sb.append("an");
          }else{
            sb.append("a");
          }
        }else{
          throw new IllegalArgumentException(
              "Unknown escape code '%" + escapeCode 
              + "' in second-pass format string '"
              + formatString + "'");
        }
      }else{
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
  public String getDisplayName(){
    return getDisplayName(this, 1);
  }
  
  public Item(ItemType type, String name, int baseWeight, int baseValue,
      List<Affix> affixes){
    this.type = type;
    this.name = name;
    this.baseWeight = baseWeight;
    this.baseValue = baseValue;
    this.affixes = new ArrayList<Affix>(affixes);
  }
}
