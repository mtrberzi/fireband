package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.List;

public class Affix {
  private String name;
  public String getName(){return name;}
  
  // true: prefix (appear before item name)
  // false: suffix (appear after item name)
  private boolean prefix = true;
  public boolean isPrefix(){return prefix;}
  
  private List<Effect> effects = new ArrayList<Effect>();
  
}
