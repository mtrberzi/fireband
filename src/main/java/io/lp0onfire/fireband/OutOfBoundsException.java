package io.lp0onfire.fireband;

public class OutOfBoundsException extends Exception {
  private static final long serialVersionUID = 7201018564580169979L;

  int x, y;
  int dimX, dimY;
  
  public OutOfBoundsException(int x, int y, int dimX, int dimY){
    this.x = x;
    this.y = y;
    this.dimX = dimX;
    this.dimY = dimY;
  }
  
  @Override
  public String getMessage(){
    return "Location (" + x + "," + y + ") outside grid (" + dimX + "," + dimY + ")";
  }
  
}
