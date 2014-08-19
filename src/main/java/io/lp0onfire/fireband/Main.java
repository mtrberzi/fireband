package io.lp0onfire.fireband;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.JSONException;

public class Main {
  private static Logger log = LogManager.getLogger("Main");
  private static void setupLogging(){
    LogManager.getRootLogger().setLevel(Level.ALL);
    PatternLayout layout = new PatternLayout(
        "%d{ISO8601} [%t] %-5p %c %x - %m%n");
    LogManager.getRootLogger().addAppender(
        new ConsoleAppender(layout, ConsoleAppender.SYSTEM_ERR));    
  }
  
  public static void main(String[] args) {
    setupLogging();
    Main game = new Main(args);
    try{
      game.run();
    }catch(Exception e){
      log.error("An unhandled exception has occurred.", e);
    }
  }

  private static String load(String path) throws IOException{
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, Charset.forName("UTF-8"));
  }
  
  private String librariesPath = "./lib/";
  
  private Main(String[] args){
  }
  
  private void run() throws IOException, JSONException{
    loadResources(librariesPath);
  }
  
  private void loadResources(String resourcePath) throws IOException, JSONException{
    // Load resources
    String baseWeaponData = load(resourcePath + "objects/baseWeapons.json");
    BaseWeapons.instance.loadWeapons(baseWeaponData);
  }
  
}
