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
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjglx.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

  public static void main(String[] args) {
    int frameCount = 0;
    double previousTime = glfwGetTime();
    DisplayManager.createDisplay();
    GL.createCapabilities();
    glClear(GL_COLOR_BUFFER_BIT); //Set the clear color //glClearColor(0.0f, 1.0f, 0.0f, 0.0f);

    Loader loader = new Loader();
    StaticShader shader = new StaticShader();
    Renderer renderer = new Renderer(shader);

    float[] vertices = {
        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,

        -0.5f, 0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,

        0.5f, 0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,

        -0.5f, 0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,
        -0.5f, -0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f,

        -0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, 0.5f,

        -0.5f, -0.5f, 0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, 0.5f
    };

    float[] textureCoords = {
        0, 0,
        0, 1,
        1, 1,
        1, 0,
        0, 0,
        0, 1,
        1, 1,
        1, 0,
        0, 0,
        0, 1,
        1, 1,
        1, 0,
        0, 0,
        0, 1,
        1, 1,
        1, 0,
        0, 0,
        0, 1,
        1, 1,
        1, 0,
        0, 0,
        0, 1,
        1, 1,
        1, 0
    };

    int[] indices = {
        0, 1, 3,
        3, 1, 2,
        4, 5, 7,
        7, 5, 6,
        8, 9, 11,
        11, 9, 10,
        12, 13, 15,
        15, 13, 14,
        16, 17, 19,
        19, 17, 18,
        20, 21, 23,
        23, 21, 22

    };

    //RawModel model = loader.loadToVAO(vertices);
    RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
    ModelTexture texture = new ModelTexture(loader.loadTexture("luna"));
    TexturedModel texturedModel = new TexturedModel(model, texture);

    Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -5), 0, 0, 0, 1);

    Camera camera = new Camera();

    // Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(windowID)) {
      entity.increaseRotation(1, 1, 0);//rotate around x and y axes
      camera.move();
      renderer.prepare();
      shader.start();
      shader.loadViewMatrix(camera);
      renderer.render(entity, shader);
      shader.stop();
      // Rest of your game loop...
      // glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
      glfwSwapBuffers(windowID); // swap the color buffers // updates the contents of display
      //game logic render
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
    shader.cleanUp();
    loader.cleanUp();//delete al the VAOs and VBOs
    DisplayManager.closeDisplay();
  }
}
