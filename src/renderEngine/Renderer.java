package renderEngine;

import entities.Entity;
import java.nio.IntBuffer;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;
import shaders.StaticShader;
import toolbox.Maths;

public class Renderer {

  private static final float FOV = 70;
  private static final float NEAR_PLANE = 0.1f;
  private static final float FAR_PLANE = 1000;

  private Matrix4f projectionMatrix;

  public Renderer(StaticShader shader) {
    //load only ones in the beginning
    createProjectionMatrix();
    shader.start();
    shader.loadProjectionMatrix(projectionMatrix);
    shader.stop();
  }

  public void prepare() {
    //test which triangles in front of which - to render in correct order
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glClear(
        GL11.GL_COLOR_BUFFER_BIT
            | GL11.GL_DEPTH_BUFFER_BIT);//clear the color and depth buffer from previous frame
    GL11.glClearColor(1, 0, 0, 1);
  }

  public void render(Entity entity, StaticShader shader) {
    TexturedModel texturedModel = entity.getModel();
    RawModel model = texturedModel.getRawModel();
    GL30.glBindVertexArray(model.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    //load up the entities transformations(positions, rotations and scale to the vertex shader
    Matrix4f transformationMatrix = Maths
        .createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(),
            entity.getRotZ(), entity.getScale());
    shader.loadTransformationMatrix(transformationMatrix);
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_PROXY_TEXTURE_2D, texturedModel.getTexture().getID());
    //GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL30.glBindVertexArray(0);
  }

  private void createProjectionMatrix() {
    //~ Get display width and height in Renderer#createProjectionMatrix
    IntBuffer w = BufferUtils.createIntBuffer(4);
    IntBuffer h = BufferUtils.createIntBuffer(4);
    long windowID = DisplayManager.getWindowID();
    GLFW.glfwGetWindowSize(windowID, w, h);

    float aspectRatio = (float) w.get(0) / (float) h.get(0);
    float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
    float x_scale = y_scale / aspectRatio;
    float frustum_length = FAR_PLANE - NEAR_PLANE;

    projectionMatrix = new Matrix4f();
    projectionMatrix.m00 = x_scale;
    projectionMatrix.m11 = y_scale;
    projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
    projectionMatrix.m23 = -1;
    projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
    projectionMatrix.m33 = 0;
  }

}
