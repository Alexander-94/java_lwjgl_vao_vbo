package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Loader {

  private List<Integer> vaos = new ArrayList<Integer>();
  private List<Integer> vbos = new ArrayList<Integer>();
  private List<Integer> textures = new ArrayList<Integer>();

  /*public RawModel loadToVAO(float[] positions) {//v1.0
    int vaoID = createVAO();
    storeDateInAttributeList(0, 3, positions);
    unbindVAO();
    return new RawModel(vaoID, positions.length / 3); //each vertex has x,y,z
  }*/

  //texturing a quad - method to loadup a texture into OpenGL
  public int loadTexture(String fileName) {
    Texture texture = null;
    try {
      texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    int textureID = texture.getTextureID();
    textures.add(textureID);
    return textureID;
  }

  public RawModel loadToVAO(float[] positions, float[] textureCoord, float[] normals,
      int[] indices) {//v2.0 index buffer
    int vaoID = createVAO();
    bindIndicesBuffer(indices);
    storeDateInAttributeList(0, 3, positions);
    storeDateInAttributeList(1, 2, textureCoord);
    storeDateInAttributeList(2, 3, normals);
    unbindVAO();
    return new RawModel(vaoID, indices.length); //6 for quad
  }

  public void cleanUp() {
    for (int vao : vaos) {
      GL30.glDeleteVertexArrays(vao);
    }
    for (int vbo : vbos) {
      GL15.glDeleteBuffers(vbo);
    }
    for (int texture : textures) {
      GL11.glDeleteTextures(texture);
    }
  }

  //create a new empty VAO, returns the ID of VAO
  private int createVAO() {
    int vaoID = GL30.glGenVertexArrays();
    vaos.add(vaoID);
    GL30.glBindVertexArray(vaoID);
    return vaoID;
  }

  private void storeDateInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    FloatBuffer buffer = storeDataInFloatBuffer(data);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);//unbind the current vbo
  }

  private void unbindVAO() {
    GL30.glBindVertexArray(0);
  }

  private void bindIndicesBuffer(int[] indices) {
    int vboID = GL15.glGenBuffers();//create an empty vbo
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
    IntBuffer buffer = storeDataInIntBuffer(indices);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
  }

  private IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }

  private FloatBuffer storeDataInFloatBuffer(float[] data) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }

}
