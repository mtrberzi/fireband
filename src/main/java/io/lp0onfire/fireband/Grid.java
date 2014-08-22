package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.List;

public class Grid<T> {
  // Grid entry: entries[x][y] = entry at (x, y)
  private List<List<T>> entries;
  private int dimX, dimY;
  public int getWidth(){return dimX;}
  public int getHeight(){return dimY;}
  
  private void assertBounds(int x, int y) throws OutOfBoundsException{
    if(x < 0 || x >= dimX || y < 0 || y >= dimY){
      throw new OutOfBoundsException(x, y, dimX, dimY);
    }
  }
  
  public void set(int x, int y, T item) throws OutOfBoundsException{
    assertBounds(x,y);
    entries.get(x).add(y,item);
  }
  
  public T at(int x, int y) throws OutOfBoundsException{
    assertBounds(x,y);
    return entries.get(x).get(y);
  }
  
  public Grid(int width, int height){
    this.dimX = width;
    this.dimY = height;
    
    // allocate this.entries
    entries = new ArrayList<List<T>>(dimX);
    for(int x = 0; x < dimX; ++x){
      entries.add(x, new ArrayList<T>(dimY));
      for(int y = 0; y < dimY; ++y){
        entries.get(x).add(y, null);
      }
    }
  }
}
