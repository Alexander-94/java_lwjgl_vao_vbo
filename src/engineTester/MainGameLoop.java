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
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjglx.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {

  public static void main(String[] args) {

    int frameCount = 0;
    double previousTime = glfwGetTime();

    DisplayManager.createDisplay();
    GL.createCapabilities();
    glClear(GL_COLOR_BUFFER_BIT); //Set the clear color //glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
    Loader loader = new Loader();

    RawModel model = OBJLoader.loadObjModel("stall", loader);

    TexturedModel texturedModel = new TexturedModel(model,
        new ModelTexture(loader.loadTexture("stallTexture")));

    ModelTexture textureMod = texturedModel.getTexture();
    textureMod.setShineDamper(10);
    textureMod.setReflectivity(0.2f);

    Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -25), 0, 0, 0, 1);
    Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

    Terrain terrain = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("grass")));
    Terrain terrain2 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));

    Camera camera = new Camera();
    MasterRenderer renderer = new MasterRenderer();

    // Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(windowID)) {
      entity.increaseRotation(0, 1, 0);//rotate around x and y axes
      camera.move();

      renderer.processTerrain(terrain);
      renderer.processTerrain(terrain2);
      renderer.processEntity(entity);

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
