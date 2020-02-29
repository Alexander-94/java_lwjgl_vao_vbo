package engineTester;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static renderEngine.DisplayManager.GAME_NAME;
import static renderEngine.DisplayManager.windowID;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjglx.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

  public static void main(String[] args) {

    int frameCount = 0;
    double previousTime = glfwGetTime();

    DisplayManager.createDisplay();
    GL.createCapabilities();
    glClear(GL_COLOR_BUFFER_BIT); //Set the clear color //glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
    Loader loader = new Loader();

    //------------------------------------------
    ModelData data = OBJFileLoader.loadOBJ("stall");
    RawModel model = loader
        .loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(),
            data.getIndices());

    TexturedModel texturedModel = new TexturedModel(model,
        new ModelTexture(loader.loadTexture("stallTexture")));
    ModelTexture textureMod = texturedModel.getTexture();
    textureMod.setShineDamper(10);
    textureMod.setReflectivity(0.2f);
    Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -25), 0, 0, 0, 1);
    Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

    //--grass-----------------------------------
    ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
    RawModel gmodel = loader
        .loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(),
            grassData.getIndices());
    TexturedModel grass = new TexturedModel(gmodel,
        new ModelTexture(loader.loadTexture("grassTexture")));
    ModelTexture grassTexture = grass.getTexture();
    grassTexture.setShineDamper(10);
    grassTexture.setReflectivity(0.2f);
    grassTexture.setHasTransparency(true);
    grass.getTexture().setUseFakeLighting(true);
    Entity gentity = new Entity(grass, new Vector3f(0, 0, -10), 0, 0, 0, 1);

    //--terrain---------------------------------
    TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
    TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
    TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
    TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

    TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture,
        bTexture);
    TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

    Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
    Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);
    Camera camera = new Camera();
    MasterRenderer renderer = new MasterRenderer();

    ModelData bunnyModelData = OBJFileLoader.loadOBJ("stanfordBunny");
    RawModel bunnyModel = loader
        .loadToVAO(bunnyModelData.getVertices(), bunnyModelData.getTextureCoords(),
            bunnyModelData.getNormals(),
            bunnyModelData.getIndices());
    TexturedModel stanfordBunny = new TexturedModel(bunnyModel,
        new ModelTexture(loader.loadTexture("white")));
    Player player = new Player(stanfordBunny, new Vector3f(100, 0, -50), 0, 0, 0, 1);

    // Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(windowID)) {
      entity.increaseRotation(0, 1, 0);//rotate around x and y axes
      camera.move();
      player.move();

      renderer.processEntity(player);
      renderer.processTerrain(terrain);
      renderer.processTerrain(terrain2);
      renderer.processEntity(entity);
      renderer.processEntity(gentity);

      renderer.render(light, camera);
      glfwSwapBuffers(windowID); // swap the color buffers // updates the contents of display
      DisplayManager.updateDisplay();
      // mouse, keyboard input and window
      glfwPollEvents();

      //----FPS----
      // Measure speed
      double currentTime = glfwGetTime();
      frameCount++;
      // If a second has passed.
      if (currentTime - previousTime >= 1.0) {
        // Display the frame count here any way you want.
        glfwSetWindowTitle(windowID,
            GAME_NAME + " | FPS=" + frameCount + " | LWJGL " + Version.getVersion());
        frameCount = 0;
        previousTime = currentTime;
      }
    }

    renderer.cleanUp();
    loader.cleanUp();//delete al the VAOs and VBOs
    DisplayManager.closeDisplay();
  }
}
