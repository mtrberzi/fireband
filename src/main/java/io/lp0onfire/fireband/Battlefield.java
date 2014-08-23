package io.lp0onfire.fireband;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Battlefield {
  private static Logger log = LogManager.getLogger("Battlefield");
  private Grid<Tile> tiles;
  public Tile getTile(int x, int y) throws OutOfBoundsException{
    return tiles.at(x, y);
  }
  public int getWidth(){return tiles.getWidth();}
  public int getHeight(){return tiles.getHeight();}
  
  public Battlefield(int width, int height){
    this.tiles = new Grid<Tile>(width, height);
    try{
      for(int x = 0; x < width; ++x){
        for(int y = 0; y < height; ++y){
          tiles.set(x, y, new Tile(x, y));
        }
      }
    }catch(OutOfBoundsException e){
      log.error("unexpected bounds check error: " + e.getMessage());
    }
  }
  
  public void resetFlag(TileFlag flag){
    try{
      for(int x = 0; x < tiles.getWidth(); ++x){
        for(int y = 0; y < tiles.getHeight(); ++y){
          getTile(x,y).clearFlag(flag);
        }
      }
    }catch(OutOfBoundsException e){
      log.error("unexpected bounds check error: " + e.getMessage());
    }
  }
  
  public String dumpTerrainAndFeatures(){
    StringBuilder sb = new StringBuilder();
    String br = System.getProperty("line.separator");
    sb.append(br);
    try {
      for(int y = tiles.getHeight() - 1; y >= 0; --y){
        for(int x = 0; x < tiles.getWidth(); ++x){
            Tile t = tiles.at(x, y);
            if(t.hasFlag(TileFlag.TILE_ALLOCATED)){
              sb.append("*");
            }else{
              Terrain terrain = t.getTerrain();
              Feature feature = t.getFeature();
              // Feature overrides terrain
              if(feature == null){
                if(terrain == null){
                  sb.append(" ");
                }else{
                  sb.append(terrain.getSymbol());
                }
              }else{
                sb.append(feature.getSymbol());
              }
            }
        }
        sb.append(br);
      }
    } catch (OutOfBoundsException e) {
      log.error("unexpected bounds check error: " + e.getMessage());
    }
    return sb.toString();
  }
}
