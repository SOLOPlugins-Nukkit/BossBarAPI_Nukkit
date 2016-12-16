package xenialdan.BossBarAPI.network;

public class BossBarValues{

  public int min;
  public int max;
  public float value;
  public String name;

  public BossBarValues(int min, int max, float value, String name){
    this.min = min;
    this.max = max;
    this.value = value;
    this.name = name;
  }
  public int getMinValue(){
    return this.min;
  }
  public int getMaxValue(){
    return this.max;
  }
  public float getValue(){
    return this.value;
  }
  public String getName(){
    return this.name;
  }
  public int getDefaultValue(){
    return this.min;
  }
}