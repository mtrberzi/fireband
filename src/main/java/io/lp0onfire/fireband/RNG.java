package io.lp0onfire.fireband;

import java.util.List;
import java.util.Random;

public class RNG {
  public final static RNG instance = new RNG();
  private Random random;
  private RNG(){
    random = new Random();
  }
  
  public static int roll(int dieSize){
    if(dieSize < 1){
      return 0;
    }
    return instance.random.nextInt(dieSize) + 1;
  }
  
  public static int rollXdY(int x, int y){
    int sum = 0;
    if(x < 1){
      return 0;
    }
    for(int i = 0; i < x; ++i){
      sum += roll(y);
    }
    return sum;
  }
  
  // Expected value of rolling 1dY.
  public static double expectedValue(int y){
    return (double)(1 + y) / 2.0;
  }
  
  // Expected value of rolling XdY.
  public static double expectedValue(int x, int y){
    return x * expectedValue(y);
  }
  
  public static double normal(double mean, double standardDeviation){
    return instance.random.nextGaussian()*standardDeviation + mean;
  }
  
  public static int mBonus(int max, int level){
    if(level > 128) level = 128;
    double bonusFactor = ((double)level) / 128.0;
    double bonus = bonusFactor * max;
    double stdev = max / 4.0;
    int m = (int)Math.round(normal(bonus, stdev));
    if(m < 0){
      return 0;
    }else if(m > max){
      return max;
    }else{
      return m;
    }
  }
  
  public static boolean oneIn(int n){
    int i = roll(n);
    return(i == 1);
  }
  
  public static <T> T randomEntry(List<T> entries){
    int index = instance.random.nextInt(entries.size());
    return entries.get(index);
  }
  
}
