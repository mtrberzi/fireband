package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class WeaponBuilder {
  private String name = "SORD.....";
  private int baseWeight = 0;
  private int baseValue = 0;
  private WeaponType type = WeaponType.WEAPON_EDGED;
  private int damageDice = 0;
  public void setDamageDice(int n){
    damageDice = n;
  }
  private int damageDieSize = 0;
  public void setDamageDieSize(int n){
    damageDieSize = n;
  }
  private DamageType damageType = DamageType.DAMAGE_SLASHING;
  private int criticalThreshold = 20;
  private int criticalMultiplier = 1;
  private List<Affix> affixes = new ArrayList<Affix>();
  public void addAffix(Affix a){
    // TODO check duplicate affixes
    affixes.add(a);
  }
  private int enhancementBonus = 0;
  public void setEnhancementBonus(int n){
    enhancementBonus = n;
  }

  private int range = 1;
  private boolean simple = false;
  public void setIsSimple(boolean s){
    simple = s;
  }
  
  public WeaponBuilder(Weapon base){
    // copy everything from a template base weapon
    name = base.getName();
    baseWeight = base.getBaseWeight();
    baseValue = base.getBaseValue();
    type = base.getWeaponType();
    damageDice = base.getDamageDice();
    damageDieSize = base.getDamageDieSize();
    damageType = base.getDamageType();
    criticalThreshold = base.getCriticalThreshold();
    criticalMultiplier = base.getCriticalMultiplier();
    affixes = new ArrayList<Affix>(base.getAffixes());
    enhancementBonus = base.getEnhancementBonus();
    range = base.getRange();
    simple = base.isSimpleWeapon();
  }
  
  public WeaponBuilder(JSONObject obj) throws JSONException {
    name = obj.getString("name");
    baseWeight = obj.getInt("weight");
    baseValue = obj.getInt("value");
    type = WeaponType.valueOf(obj.getString("type"));
    if(type == null){
      throw new IllegalArgumentException("unknown weapon type '" + obj.getString("type") + "'");
    }
    // parse a damage string of the form "XdY"
    String damage = obj.getString("damage");
    int dIndex = damage.indexOf('d');
    if(dIndex == -1){
      throw new IllegalArgumentException("invalid damage string '" + damage + "'");
    }else{
      String sDice = damage.substring(0, dIndex);
      String sDieSize = damage.substring(dIndex + 1);
      damageDice = Integer.parseInt(sDice);
      damageDieSize = Integer.parseInt(sDieSize);
    }
    damageType = DamageType.valueOf(obj.getString("damagetype"));
    if(damageType == null){
      throw new IllegalArgumentException("unknown damage type '" + obj.getString("damagetype") + "'");
    }
    criticalThreshold = obj.getInt("threshold");
    criticalMultiplier = obj.getInt("multiplier");
    
    // now for optional items
    
    affixes = new ArrayList<Affix>(); // TODO
    
    enhancementBonus = obj.optInt("enhancement");
    
    range = obj.optInt("range");
    if(range == 0) range = 1;
    
    simple = obj.optBoolean("simple");
  }
  
  public Weapon build(){
    return new Weapon(name, baseWeight, baseValue,
        type, damageDice, damageDieSize,
        damageType, criticalThreshold, criticalMultiplier,
        affixes, enhancementBonus, range, simple);
  }
}
