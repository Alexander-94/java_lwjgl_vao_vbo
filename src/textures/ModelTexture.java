package textures;

public class ModelTexture {

  private int textureID;

  //how close the camera needs to be to the reflected light
  //to see change in the brightness on the surface
  private float shineDamper = 1;
  // if 0 - no light would be reflected at all and it would be no specular lighting
  private float reflectivity = 0;

  public ModelTexture(int id) {
    this.textureID = id;
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
