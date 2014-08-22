package io.lp0onfire.fireband;

public class Feature {
  private final String name;
  public String getName(){return name;}
  
  private final char symbol;
  public char getSymbol(){return symbol;}
  
  public Feature(String name, char symbol){
    this.name = name;
    this.symbol = symbol;
  }
}
