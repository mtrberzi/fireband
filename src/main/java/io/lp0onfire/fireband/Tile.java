package io.lp0onfire.fireband;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Tile {
  private Terrain terrain;
  public Terrain getTerrain(){return terrain;}
  public void setTerrain(Terrain t){this.terrain = t;}
  public void clearTerrain(){this.terrain = null;}
  
  private Feature feature;
  public Feature getFeature(){return feature;}
  public void setFeature(Feature f){this.feature = f;}
  public void clearFeature(){this.feature = null;}
  
  private Unit occupant;
  public Unit getOccupant(){return occupant;}
  public void setOccupant(Unit u){this.occupant = u;}
  public void clearOccupant(){this.occupant = null;}
  
  private List<Item> items;
  public List<Item> getItems(){return items;}
  public void addItem(Item i){this.items.add(i);}
  public void removeItem(Item i){this.items.remove(i);}
  
  private Set<TileFlag> flags;
  public void setFlag(TileFlag f){flags.add(f);}
  public void clearFlag(TileFlag f){flags.remove(f);}
  public boolean hasFlag(TileFlag f){return flags.contains(f);}
  
  public Tile(){
    // assume an empty tile
    this.terrain = null;
    this.feature = null;
    this.occupant = null;
    this.items = new LinkedList<Item>();
    this.flags = new HashSet<TileFlag>();
  }
}
