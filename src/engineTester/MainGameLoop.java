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

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;

public class MainGameLoop {

  public static void main(String[] args) {
    int frameCount = 0;
    double previousTime = glfwGetTime();
    DisplayManager.createDisplay();
    GL.createCapabilities();
    glClear(GL_COLOR_BUFFER_BIT); //Set the clear color //glClearColor(0.0f, 1.0f, 0.0f, 0.0f);

    Loader loader = new Loader();
    Renderer renderer = new Renderer();
    StaticShader shader = new StaticShader();

    //vertices
    float[] vertices = {
        -0.5f, 0.5f, 0f,  //v0
        -0.5f, -0.5f, 0f, //v1
        0.5f, -0.5f, 0f,  //v2
        0.5f, 0.5f, 0f,   //v3
    };
    //index buffer
    int[] indices = {
        0, 1, 3, //left triangle v0,v1,v3
        3, 1, 2  //right triangle v3,v1,v2
    };

    //RawModel model = loader.loadToVAO(vertices);
    RawModel model = loader.loadToVAO(vertices, indices);

    // Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(windowID)) {
      renderer.prepare();
      shader.start();
      renderer.render(model);
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
