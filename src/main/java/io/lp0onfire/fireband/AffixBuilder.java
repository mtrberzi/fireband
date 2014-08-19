package io.lp0onfire.fireband;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AffixBuilder {
  private String name;
  public void setName(String s){name = s;}
  
  // true: prefix (appear before item name)
  // false: suffix (appear after item name)
  private boolean prefix = true;
  public void setPrefix(boolean b){prefix = b;}
  
  private int minimumLevel = 0;
  public void setMinimumLevel(int i){minimumLevel = i;}
  
  private Set<ItemType> allowedItemTypes = new HashSet<ItemType>();
  public void addAllowedItemType(ItemType i){allowedItemTypes.add(i);}
  private List<Effect> effects = new ArrayList<Effect>();
  public void addEffect(Effect e){effects.add(e);}
  
  public AffixBuilder(JSONObject obj) throws JSONException{
    name = obj.getString("name");
    prefix = obj.optBoolean("prefix", false);
    minimumLevel = obj.optInt("level");
    JSONArray allowed = obj.getJSONArray("types");
    for(int i = 0; i < allowed.length(); ++i){
      String s = allowed.getString(i);
      ItemType type = ItemType.valueOf(s);
      addAllowedItemType(type);
    }
    JSONArray effs = obj.getJSONArray("effects");
    for(int i = 0; i < effs.length(); ++i){
      String s = effs.getString(i);
      Effect e = Effects.instance.getEffectByName(s);
      addEffect(e);
    }
  }
  
  public Affix build(){
    return new Affix(name, prefix, minimumLevel, allowedItemTypes, effects);
  }
}
