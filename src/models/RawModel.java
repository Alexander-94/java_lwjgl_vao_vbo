package models;

public class RawModel {

  //represents a 3d model stored in memory
  private int vaoID;
  private int vertexCount;

  //constructor
  public RawModel(int vaoId, int vertexCount) {
    this.vaoID = vaoId;
    this.vertexCount = vertexCount;
  }

  public int getVaoID() {
    return vaoID;
  }

  public int getVertexCount() {
    return vertexCount;
  }
}
