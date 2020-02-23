package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.TexturedModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

//handle all of the rendering code in the game
public class MasterRenderer {

  private static final float FOV = 70;
  private static final float NEAR_PLANE = 0.1f;
  private static final float FAR_PLANE = 1000;

  private static final float RED = 0.5f;
  private static final float BLUE = 0.5f;
  private static final float GREEN = 0.5f;

  private Matrix4f projectionMatrix;

  private StaticShader shader = new StaticShader();
  private EntityRenderer renderer;

  private TerrainRenderer terrainRenderer;
  private TerrainShader terrainShader = new TerrainShader();

  private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
  private List<Terrain> terrains = new ArrayList<Terrain>();

  //constructor
  public MasterRenderer() {
    //2 lines above - not to render inside of the 3d model
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glCullFace(GL11.GL_BACK);
    //load only ones in the beginning
    createProjectionMatrix();
    renderer = new EntityRenderer(shader, projectionMatrix);
    terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
  }

  public static void enableCulling() {
    //2 lines above - not to render inside of the 3d model
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glCullFace(GL11.GL_BACK);
  }

  public static void disableCulling() {
    GL11.glDisable(GL11.GL_CULL_FACE);
  }

  //once every frame
  public void render(Light sun, Camera camera) {
    prepare();
    shader.start();
    shader.loadSkyColour(RED, GREEN, BLUE);
    shader.loadLight(sun);
    shader.loadViewMatrix(camera);
    renderer.render(entities);
    shader.stop();
    terrainShader.start();
    terrainShader.loadSkyColour(RED, GREEN, BLUE);
    terrainShader.loadLight(sun);
    terrainShader.loadViewMatrix(camera);
    terrainRenderer.render(terrains);
    terrainShader.stop();
    terrains.clear();
    entities.clear();
  }

  public void processTerrain(Terrain terrain) {
    terrains.add(terrain);
  }

  public void processEntity(Entity entity) {
    TexturedModel entityModel = entity.getModel();
    List<Entity> batch = entities.get(entityModel);
    if (batch != null) {
      batch.add(entity);
    } else {
      List<Entity> newBatch = new ArrayList<Entity>();
      newBatch.add(entity);
      entities.put(entityModel, newBatch);
    }
  }

  public void cleanUp() {
    shader.cleanUp();
    terrainShader.cleanUp();
  }

  public void prepare() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    //determine the sky
    GL11.glClearColor(RED, GREEN, BLUE, 1);
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
