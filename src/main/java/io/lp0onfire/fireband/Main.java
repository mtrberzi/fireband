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
    // for fun
    WeaponGenerator wGen = new WeaponGenerator();
    wGen.setItemQualityFactor(10);
    wGen.guaranteeDecentObject();
    Weapon w = wGen.generateWeapon();
    log.info(w.getDisplayName());
    log.info(w.getDescription());
    log.info(w.getFullDescription());
    
    ArmourGenerator aGen = new ArmourGenerator();
    aGen.setItemQualityFactor(10);
    aGen.guaranteeDecentObject();
    aGen.allowOnlyBodyArmour();
    Armour a = aGen.generateArmour();
    log.info(a.getDisplayName());
    log.info(a.getDescription());
    log.info(a.getFullDescription());
    
    UnitBuilder uBuild = new UnitBuilder();
    uBuild.setName("Robin");
    uBuild.setPronoun(Pronoun.PRONOUN_NEUTRAL);
    uBuild.equip(w);
    uBuild.equip(a);
    Unit u = uBuild.build();
    
    BattlefieldGenerator fieldGen = new BattlefieldGenerator();
    Battlefield field = fieldGen.generateBattlefield();
    log.info(field.dumpTerrainAndFeatures());
  }
  
  private void loadResources(String resourcePath) throws IOException, JSONException{
    // Load resources
    // lots of things depend on effects; load them first!
    String effectData = load(resourcePath + "magic/effects.json");
    Effects.instance.loadEffects(effectData);
    String baseWeaponData = load(resourcePath + "objects/baseWeapons.json");
    BaseWeapons.instance.loadWeapons(baseWeaponData);
    String baseArmourData = load(resourcePath + "objects/baseArmour.json");
    BaseArmour.instance.loadArmour(baseArmourData);
    String affixData = load(resourcePath + "objects/affixes.json");
    Affixes.instance.loadAffixes(affixData);
    
    String terrainData = load(resourcePath + "maps/terrain.json");
    Terrains.instance.loadTerrains(terrainData);
    String featureData = load(resourcePath + "maps/features.json");
    Features.instance.loadFeatures(featureData);
  }
  
}
