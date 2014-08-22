package io.lp0onfire.fireband;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BattlefieldGenerator {
  private static Logger log = LogManager.getLogger("BattlefieldGenerator");
  public BattlefieldGenerator(){}
  
  private void makeChamber(Battlefield field, Terrain floor, Feature wall,
      int x1, int y1, int x2, int y2) throws OutOfBoundsException {
    log.debug("Placing chamber in ("
        + x1 + "," + y1 + "),(" + x2 + "," + y2 + ")");
    // Allocate the entire room.
    for(int x = x1; x <= x2; ++x){
      for(int y = y1; y <= y2; ++y){
        field.getTile(x, y).setFlag(TileFlag.TILE_ALLOCATED);
      }
    }
    // Place walls around the room and clear the allocated flag.
    // * Left wall
    for(int y = y1; y <= y2; ++y){
      int x = x1;
      Tile t = field.getTile(x, y);
      t.clearFlag(TileFlag.TILE_ALLOCATED);
      t.setFeature(wall);
    }
    // * Right wall
    for(int y = y1; y <= y2; ++y){
      int x = x2;
      Tile t = field.getTile(x, y);
      t.clearFlag(TileFlag.TILE_ALLOCATED);
      t.setFeature(wall);
    }
    // * Top wall
    for(int x = x1; x <= x2; ++x){
      int y = y1;
      Tile t = field.getTile(x, y);
      t.clearFlag(TileFlag.TILE_ALLOCATED);
      t.setFeature(wall);
    }
    // * Bottom wall
    for(int x = x1; x <= x2; ++x){
      int y = y2;
      Tile t = field.getTile(x, y);
      t.clearFlag(TileFlag.TILE_ALLOCATED);
      t.setFeature(wall);
    }
    // Try to make a few exits of width 1-3.
  }
  
  /**
   * Build a system of chambers. 
   * Rework of the type-6 'chambers' algorithm from Sangband.
   * The rectangle from (x1,y1) to (x2,y2) inclusive will be used.
   * 
   * @param field the Battlefield to be populated
   * @param floor the Terrain that should be used as open floor
   * @param wall the Feature that should be used as the chamber walls
   * @param x1 left edge of area to use
   * @param y1 bottom edge of area to use
   * @param x2 right edge of area to use
   * @param y2 top edge of area to use 
   */
  private void placeChambers(Battlefield field, Terrain floor, Feature wall,
      int x1, int y1, int x2, int y2){
    int width = x2-x1 + 1;
    int height = y2-y1 + 1;
    int area = width * height;
    int nChambers = 10 + area / 80;
    log.debug("Building " + nChambers + " chambers");
    // Build the chambers.
    for(int i = 0; i < nChambers; ++i){
      int size = 2 + RNG.roll(5);
      int w = size + RNG.roll(10);
      int h = size + RNG.roll(4);
      // Pick a lower-left corner at random...
      int chamber_x1 = RNG.range(x1, x2 - w);
      int chamber_y1 = RNG.range(y1, y2 - w);
      // ...and calculate the top-right corner location...
      int chamber_x2 = chamber_x1 + w;
      int chamber_y2 = chamber_y1 + h;
      // ...then build the walls and allocate the interior space.
      try {
        makeChamber(field, floor, wall, chamber_x1, chamber_y1, chamber_x2, chamber_y2);
      } catch (OutOfBoundsException e) {
        // TODO Auto-generated catch block
        log.error(e);
      }
    }
    // Fill in tiny, narrow rooms.
    // Pick a random allocated spot near the center of the room...
    // ...and start hollowing out the first room.
    // Attempt to change every in-room allocated tile to floor.
    // Turn all walls and allocated tiles not adjacent to floor into voids.
    
  }
  
  /**
   * Build a system of chambers. The entire map area is used by default.
   * 
   * @param field the Battlefield to be populated
   * @param floor the Terrain that should be used as open floor
   * @param wall the Feature that should be used as the chamber walls
   */
  private void placeChambers(Battlefield field, Terrain floor, Feature wall){
    placeChambers(field, floor, wall,
        0, 0, field.getWidth() - 1, field.getHeight() - 1);
  }
  
  public Battlefield generateBattlefield(){
    // TODO map dimension generation parameters
    int width = (9 + RNG.roll(3)) * (2 + RNG.roll(3));
    int height = (9 + RNG.roll(3)) * (2 + RNG.roll(3));
    Battlefield field = new Battlefield(width, height);
    log.debug("Dimensions are " + width + " by " + height);
    
    // TODO generation themes, etc.
    Terrain floor = Terrains.instance.getTerrainByName("floor");
    Feature wall = Features.instance.getFeatureByName("wall");
    placeChambers(field, floor, wall);
    
    return field;
  }
}
