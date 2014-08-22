package io.lp0onfire.fireband;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Battlefield {
  private static Logger log = LogManager.getLogger("Battlefield");
  private Grid<Tile> tiles;
  public Tile getTile(int x, int y) throws OutOfBoundsException{
    return tiles.at(x, y);
  }
  
  public Battlefield(int width, int height){
    this.tiles = new Grid<Tile>(width, height);
    try{
      for(int x = 0; x < width; ++x){
        for(int y = 0; y < height; ++y){
          tiles.set(x, y, new Tile());
        }
      }
    }catch(OutOfBoundsException e){
      log.error("unexpected bounds check error: " + e.getMessage());
    }
  }
  
  public String dumpTerrain(){
    StringBuilder sb = new StringBuilder();
    String br = System.getProperty("line.separator");
    sb.append(br);
    for(int y = tiles.getHeight() - 1; y >= 0; --y){
      for(int x = 0; x < tiles.getWidth(); ++x){
        try {
          Tile t = tiles.at(x, y);
          Terrain terrain = t.getTerrain();
          if(terrain == null){
            sb.append(" ");
          }else{
            sb.append(terrain.getSymbol());
          }
        } catch (OutOfBoundsException e) {
          log.error("unexpected bounds check error: " + e.getMessage());
        }
      }
      sb.append(br);
    }
    return sb.toString();
  }
}
