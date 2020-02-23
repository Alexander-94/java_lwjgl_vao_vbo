package textures;

public class ModelTexture {

  private int textureID;

  //how close the camera needs to be to the reflected light
  //to see change in the brightness on the surface
  private float shineDamper = 1;
  // if 0 - no light would be reflected at all and it would be no specular lighting
  private float reflectivity = 0;

  private boolean hasTransparency = false;
  private boolean useFakeLighting = false;

  public boolean isUseFakeLighting() {
    return useFakeLighting;
  }

  public void setUseFakeLighting(boolean useFakeLighting) {
    this.useFakeLighting = useFakeLighting;
  }

  public ModelTexture(int id) {
    this.textureID = id;
  }

  public boolean isHasTransparency() {
    return hasTransparency;
  }

  public void setHasTransparency(boolean hasTransparency) {
    this.hasTransparency = hasTransparency;
  }

  public int getID() {
    return this.textureID;
  }

  public float getShineDamper() {
    return shineDamper;
  }

  public void setShineDamper(float shineDamper) {
    this.shineDamper = shineDamper;
  }

  public float getReflectivity() {
    return reflectivity;
  }

  public void setReflectivity(float reflectivity) {
    this.reflectivity = reflectivity;
  }
}
