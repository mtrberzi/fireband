package io.lp0onfire.fireband;

public class Terrain {
  private final String name;
  public String getName(){return name;}
  
  private final int moveCost;
  public int getMoveCost(){return moveCost;}
  
  private final char symbol;
  public char getSymbol(){return symbol;}
  
  public Terrain(String name, int moveCost, char symbol){
    this.name = name;
    this.moveCost = moveCost;
    this.symbol = symbol;
  }
}
