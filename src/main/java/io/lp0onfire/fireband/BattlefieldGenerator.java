package io.lp0onfire.fireband;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BattlefieldGenerator {
  private static Logger log = LogManager.getLogger("BattlefieldGenerator");
  public BattlefieldGenerator(){}
  
  /**
   * Expand in every direction from a given point, searching for tiles
   * with TileFlag.TILE_ALLOCATED set. For each such tile, the terrain
   * is changed and the flag and feature are cleared.
   * @param field Battlefield to operate over
   * @param x Starting x coordinate
   * @param y Starting y coordinate
   * @param floor Terrain to place in allocated area
   */
  private void fillAllocatedArea(Battlefield field, int x, int y, Terrain floor){
    try{
      Tile t = field.getTile(x, y);
      if(t.hasFlag(TileFlag.TILE_ALLOCATED)){
        t.clearFlag(TileFlag.TILE_ALLOCATED);
        t.clearFeature();
        t.setTerrain(floor);
        // Recursive flood-fill
        for(int dx = -1; dx <= 1; ++dx){
          for(int dy = -1; dy <= 1; ++dy){
            if(dx == 0 && dy == 0) continue;
            fillAllocatedArea(field, x+dx, y+dy, floor);
          }
        }
      }
    }catch(OutOfBoundsException e){
      return;
    }
  }
  
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
      if(t.hasFlag(TileFlag.TILE_ALLOCATED)){
        t.clearFlag(TileFlag.TILE_ALLOCATED);
        t.setFeature(wall);
      }
    }
    // * Right wall
    for(int y = y1; y <= y2; ++y){
      int x = x2;
      Tile t = field.getTile(x, y);
      if(t.hasFlag(TileFlag.TILE_ALLOCATED)){
        t.clearFlag(TileFlag.TILE_ALLOCATED);
        t.setFeature(wall);
      }
    }
    // * Top wall
    for(int x = x1; x <= x2; ++x){
      int y = y1;
      Tile t = field.getTile(x, y);
      if(t.hasFlag(TileFlag.TILE_ALLOCATED)){
        t.clearFlag(TileFlag.TILE_ALLOCATED);
        t.setFeature(wall);
      }
    }
    // * Bottom wall
    for(int x = x1; x <= x2; ++x){
      int y = y2;
      Tile t = field.getTile(x, y);
      if(t.hasFlag(TileFlag.TILE_ALLOCATED)){
        t.clearFlag(TileFlag.TILE_ALLOCATED);
        t.setFeature(wall);
      }
    }
    // Try to make a few exits.
    int exitX, exitY;
    for(int i = 0; i < 20; ++i){
      if(RNG.oneIn(2)){
        // Place on left or right wall.
        if(RNG.oneIn(2)){
          exitX = x1;
        }else{
          exitX = x2;
        }
        exitY = y1 + RNG.roll(y2-y1 - 1);
      }else{
        // Place on top or bottom wall.
        if(RNG.oneIn(2)){
          exitY = y1;
        }else{
          exitY = y2;
        }
        exitX = x1 + RNG.roll(x2-x1 - 1);
      }
      // If this is not a wall, try again.
      Tile exitTile = field.getTile(exitX, exitY);
      if(!wall.equals(exitTile.getFeature())) continue;
      int wallCount = 0;
      // The exit must have at most two walls next to it.
      for(int dx = -1; dx <= 1; ++dx){
        for(int dy = -1; dy <= 1; ++dy){
          if(dx == 0 && dy == 0) continue;
          try{
            Tile checkTile = field.getTile(exitX + dx, exitY + dy);
            if(wall.equals(checkTile.getFeature())) ++wallCount;
          }catch(OutOfBoundsException e){
            continue;
          }
        }
      }
      if(wallCount <= 2){
        // Clear the feature here and allocate this tile.
        exitTile.clearFeature();
        exitTile.setFlag(TileFlag.TILE_ALLOCATED);
        //return;
      }
    }
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
    for(int y = y1; y <= y2; ++y){
      for(int x = x1; x <= x2; ++x){
        try{
          int wallCount = 0;
          for(int dx = -1; dx <= 1; ++dx){
            for(int dy = -1; dy <= 1; ++dy){
              Tile adjTile = field.getTile(x + dx, y + dy);
              if(wall.equals(adjTile.getFeature())) ++wallCount;
            }
          }
          if(wallCount >= 5){
            Tile t = field.getTile(x, y);
            t.clearFlag(TileFlag.TILE_ALLOCATED);
            t.setFeature(wall);
          }
        }catch(OutOfBoundsException e){
          continue;
        }
      }
    }
    // Pick a random allocated spot near the center of the room...
    int startX = 0, startY = 0;
    for(int i = 0; i < 50; ++i){
      startX = x1 + (int)Math.round(RNG.normal(width/2, width/4));
      startY = y1 + (int)Math.round(RNG.normal(height/2, height/4));
      try{
        Tile t = field.getTile(startX, startY);
        if(t.hasFlag(TileFlag.TILE_ALLOCATED)) break;
      }catch(OutOfBoundsException e){
        // retry
        i -= 1;
        continue;
      }
    }
    // ...and start hollowing out the first room.
    fillAllocatedArea(field, startX, startY, floor);
    // Attempt to change every in-room allocated tile to floor.
    for(int i = 0; i < 10000; ++i){
      // Assume this run will not change the map.
      boolean update = false;
      // Make new tunnels between allocated areas and floor.
      for(int y = y1; y <= y2; ++y){
        for(int x = x1; x <= x2; ++x){
          try{
            Tile t = field.getTile(x, y);
            if(!t.hasFlag(TileFlag.TILE_ALLOCATED)) continue;
            // Check horizontal and vertical directions.
            for(int dy = -1; dy <= 1; ++dy){
              for(int dx = -1; dx <= 1; ++dx){
                if(dx == 0 && dy == 0) continue;
                if(dx != 0 && dy != 0) continue;
                try{
                  Tile tAdj1 = field.getTile(x + dx, y + dy);
                  // If this is a wall, try to expand through it
                  if(wall.equals(tAdj1.getFeature())){
                    // Keep looking in the same direction.
                    Tile tAdj2 = field.getTile(x + 2*dx, y + 2*dy);
                    if(tAdj2.hasFlag(TileFlag.TILE_ALLOCATED) 
                        || floor.equals(tAdj2.getTerrain())){
                      // If we find a room at tAdj2,
                      // punch a hole in the wall at tAdj1
                      // and expand the new room at (x,y).
                      update = true;
                      tAdj1.clearFeature();
                      tAdj1.setTerrain(floor);
                      fillAllocatedArea(field, x,y, floor);
                    }else if(wall.equals(tAdj2.getFeature())){
                      // If we find another wall, try to expand through *that*.
                      Tile tAdj3 = field.getTile(x+3*dx, y+3*dy);
                      if(tAdj3.hasFlag(TileFlag.TILE_ALLOCATED)
                          || floor.equals(tAdj3.getTerrain())){
                        // If we now find floor, punch a hole through
                        // both walls at tAdj1 and tAdj2, and expand from (x,y).
                        update = true;
                        tAdj1.clearFeature();
                        tAdj1.setTerrain(floor);
                        tAdj2.clearFeature();
                        tAdj2.setTerrain(floor);
                        fillAllocatedArea(field, x,y, floor);
                      }
                    }
                  }
                }catch(OutOfBoundsException e){
                  continue;
                }
              }
            }
          }catch(OutOfBoundsException e){
            continue;
          }
        }
      }
      // Stop if this iteration did not modify the map.
      if(!update) break;
    }
    // TODO refactor into subroutine
    // Turn all floors adjacent to voids into walls.
    for(int y = y1; y <= y2; ++y){
      for(int x = x1; x <= x2; ++x){
        try{
          Tile t = field.getTile(x,y);
          if(floor.equals(t.getTerrain())){
            for(int dx = -1; dx <= 1; ++dx){
              for(int dy = -1; dy <= 1; ++dy){
                Tile tAdj = field.getTile(x+dx, y+dy);
                if(tAdj.getTerrain() == null && tAdj.getFeature() == null){
                  t.setFeature(wall);
                }
              }
            }
          }
        }catch(OutOfBoundsException e){
          continue;
        }
      }
    }
    // Turn all walls and allocated tiles not adjacent to floor into voids.
    for(int y = y1; y <= y2; ++y){
      for(int x = x1; x <= x2; ++x){
        try{
          Tile t = field.getTile(x,y);
          if(wall.equals(t.getFeature()) || t.hasFlag(TileFlag.TILE_ALLOCATED)){
            boolean adjFloor = false;
            for(int dx = -1; dx <= 1; ++dx){
              for(int dy = -1; dy <= 1; ++dy){
                Tile tAdj = field.getTile(x+dx, y+dy);
                if(floor.equals(tAdj.getTerrain())){
                  adjFloor = true;
                }
              }
            }
            if(!adjFloor){
              t.clearFlag(TileFlag.TILE_ALLOCATED);
              t.clearFeature();
              t.clearTerrain();
            }
          }
        }catch(OutOfBoundsException e){
          continue;
        }
      }
    }
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
